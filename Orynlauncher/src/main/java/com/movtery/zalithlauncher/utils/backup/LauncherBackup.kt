/*
 * OrynLauncher backup utility.
 * Stores backups in a user-selected document so they survive app uninstall.
 */
package com.movtery.zalithlauncher.utils.backup

import android.content.Context
import android.net.Uri
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

object LauncherBackup {
    private const val PRIVATE_PREFIX = "private/"
    private const val EXTERNAL_PREFIX = "external/"

    /**
     * Writes launcher files to a ZIP at the URI selected by the user.
     * Cache and database directories are intentionally excluded; the user can sign in again
     * after reinstalling instead of exporting account credentials or refresh tokens.
     */
    fun create(context: Context, destination: Uri): Result<Int> = runCatching {
        val output = context.contentResolver.openOutputStream(destination, "w")
            ?: error("Cannot open the selected backup destination")
        var count = 0
        ZipOutputStream(BufferedOutputStream(output)).use { zip ->
            count += addTree(zip, context.filesDir, PRIVATE_PREFIX)
            context.getExternalFilesDir(null)?.takeIf { it.exists() }?.let {
                count += addTree(zip, it, EXTERNAL_PREFIX)
            }
            zip.putNextEntry(ZipEntry("ORYN_BACKUP_INFO.txt"))
            zip.write(("OrynLauncher full backup\nCreated: ${System.currentTimeMillis()}\n" +
                "Includes launcher files and game data stored in app files directories.\n" +
                "Account database/tokens and temporary cache are excluded; sign in again after restore.\n").toByteArray(Charsets.UTF_8))
            zip.closeEntry()
        }
        count
    }

    /**
     * Restores only the two known roots created by this utility and rejects path traversal.
     * The caller must ask the user to confirm before invoking this method.
     */
    fun restore(context: Context, source: Uri): Result<Int> = runCatching {
        val input = context.contentResolver.openInputStream(source)
            ?: error("Cannot open the selected backup")
        var restored = 0
        var recognizedEntries = 0
        ZipInputStream(BufferedInputStream(input)).use { zip ->
            while (true) {
                val entry = zip.nextEntry ?: break
                try {
                    if (entry.isDirectory || entry.name == "ORYN_BACKUP_INFO.txt") continue
                    var normalized = entry.name.replace('\\', '/')
                    if (normalized.startsWith("/") || normalized.split('/').any { it == ".." }) {
                        error("Backup contains an unsafe path")
                    }

                    // Some file managers wrap the ZIP contents in one parent directory
                    // when creating or re-compressing an archive. Accept that layout too.
                    if (!normalized.startsWith(PRIVATE_PREFIX) &&
                        !normalized.startsWith(EXTERNAL_PREFIX)
                    ) {
                        val firstSeparator = normalized.indexOf('/')
                        if (firstSeparator > 0) {
                            val withoutWrapper = normalized.substring(firstSeparator + 1)
                            if (withoutWrapper.startsWith(PRIVATE_PREFIX) ||
                                withoutWrapper.startsWith(EXTERNAL_PREFIX)
                            ) {
                                normalized = withoutWrapper
                            }
                        }
                    }

                    val targetRoot: File
                    val relative: String
                    when {
                        normalized.startsWith(PRIVATE_PREFIX) -> {
                            targetRoot = context.filesDir
                            relative = normalized.removePrefix(PRIVATE_PREFIX)
                        }
                        normalized.startsWith(EXTERNAL_PREFIX) -> {
                            targetRoot = context.getExternalFilesDir(null)
                                ?: error("External app storage is unavailable")
                            relative = normalized.removePrefix(EXTERNAL_PREFIX)
                        }
                        else -> continue
                    }
                    recognizedEntries++
                    if (relative.isBlank()) continue
                    val target = File(targetRoot, relative).canonicalFile
                    val canonicalRoot = targetRoot.canonicalFile
                    if (target != canonicalRoot && !target.path.startsWith(canonicalRoot.path + File.separator)) {
                        error("Backup contains an unsafe path")
                    }
                    target.parentFile?.mkdirs()
                    target.outputStream().buffered().use { out -> zip.copyTo(out) }
                    restored++
                } finally {
                    zip.closeEntry()
                }
            }
        }
        if (recognizedEntries == 0) {
            error("This ZIP does not contain an OrynLauncher backup. Choose the full backup ZIP created by OrynLauncher.")
        }
        restored
    }

    private fun addTree(zip: ZipOutputStream, root: File, prefix: String): Int {
        if (!root.exists()) return 0
        var count = 0
        val rootPath = root.canonicalPath
        root.walkTopDown().forEach { file ->
            if (!file.isFile || file.name.endsWith(".lock")) return@forEach
            val canonical = runCatching { file.canonicalPath }.getOrNull() ?: return@forEach
            if (canonical != rootPath && !canonical.startsWith(rootPath + File.separator)) return@forEach
            val relative = file.relativeTo(root).invariantSeparatorsPath
            zip.putNextEntry(ZipEntry(prefix + relative))
            file.inputStream().buffered().use { it.copyTo(zip) }
            zip.closeEntry()
            count++
        }
        return count
    }
}
