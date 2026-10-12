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
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

object LauncherBackup {
    private const val PRIVATE_PREFIX = "private/"
    private const val EXTERNAL_PREFIX = "external/"

    /**
     * Build the archive locally first, then copy it to the selected document.
     * Re-open and validate the saved document before reporting success; some document
     * providers can fail or truncate writes even when opening the output stream succeeds.
     */
    fun create(context: Context, destination: Uri): Result<Int> = runCatching {
        val temporary = File.createTempFile("oryn-backup-", ".zip", context.cacheDir)
        try {
            val count = ZipOutputStream(BufferedOutputStream(temporary.outputStream())).use { zip ->
                var files = 0
                files += addTree(zip, context.filesDir, PRIVATE_PREFIX)
                context.getExternalFilesDir(null)?.takeIf { it.exists() }?.let {
                    files += addTree(zip, it, EXTERNAL_PREFIX)
                }
                zip.putNextEntry(ZipEntry("ORYN_BACKUP_INFO.txt"))
                zip.write(("OrynLauncher full backup\nCreated: ${System.currentTimeMillis()}\n" +
                    "Includes launcher files and game data stored in app files directories.\n" +
                    "Account database/tokens and temporary cache are excluded; sign in again after restore.\n").toByteArray(Charsets.UTF_8))
                zip.closeEntry()
                files
            }

            context.contentResolver.openOutputStream(destination, "w")?.use { output ->
                temporary.inputStream().buffered().use { it.copyTo(output) }
                output.flush()
            } ?: error("Cannot open the selected backup destination")

            // Verify the actual saved document, not just the temporary archive.
            val savedCopy = File.createTempFile("oryn-backup-check-", ".zip", context.cacheDir)
            try {
                context.contentResolver.openInputStream(destination)?.use { input ->
                    savedCopy.outputStream().buffered().use { input.copyTo(it) }
                } ?: error("Cannot re-open the saved backup to verify it")
                validateArchive(savedCopy)
            } finally {
                savedCopy.delete()
            }
            count
        } finally {
            temporary.delete()
        }
    }

    /**
     * Validate the complete archive before extracting anything. This avoids leaving a
     * half-restored launcher if a ZIP entry is truncated or its compressed data is corrupt.
     */
    fun restore(context: Context, source: Uri): Result<Int> = runCatching {
        val staged = File.createTempFile("oryn-restore-", ".zip", context.cacheDir)
        try {
            context.contentResolver.openInputStream(source)?.use { input ->
                staged.outputStream().buffered().use { input.copyTo(it) }
            } ?: error("Cannot open the selected backup")

            validateArchive(staged)
            var restored = 0
            ZipInputStream(BufferedInputStream(staged.inputStream())).use { zip ->
                while (true) {
                    val entry = zip.nextEntry ?: break
                    try {
                        if (entry.isDirectory || entry.name == "ORYN_BACKUP_INFO.txt") continue
                        var normalized = entry.name.replace('\\', '/')
                        if (normalized.startsWith("/") || normalized.split('/').any { it == ".." }) {
                            error("Backup contains an unsafe path")
                        }

                        // Accept archives wrapped in one parent directory by some file managers.
                        if (!normalized.startsWith(PRIVATE_PREFIX) &&
                            !normalized.startsWith(EXTERNAL_PREFIX)
                        ) {
                            val firstSeparator = normalized.indexOf('/')
                            if (firstSeparator > 0) {
                                val withoutWrapper = normalized.substring(firstSeparator + 1)
                                if (withoutWrapper.startsWith(PRIVATE_PREFIX) ||
                                    withoutWrapper.startsWith(EXTERNAL_PREFIX)
                                ) normalized = withoutWrapper
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
                        if (relative.isBlank()) continue
                        val target = File(targetRoot, relative).canonicalFile
                        val canonicalRoot = targetRoot.canonicalFile
                        if (target != canonicalRoot &&
                            !target.path.startsWith(canonicalRoot.path + File.separator)
                        ) error("Backup contains an unsafe path")
                        target.parentFile?.mkdirs()
                        target.outputStream().buffered().use { out -> zip.copyTo(out) }
                        restored++
                    } finally {
                        zip.closeEntry()
                    }
                }
            }
            restored
        } catch (e: java.io.EOFException) {
            error("Backup is incomplete or corrupted. Create a new full backup and try again.")
        } catch (e: java.util.zip.ZipException) {
            error("Backup is incomplete or corrupted. Create a new full backup and try again.")
        } finally {
            staged.delete()
        }
    }

    private fun validateArchive(file: File) {
        var recognizedEntries = 0
        ZipInputStream(BufferedInputStream(file.inputStream())).use { zip ->
            while (true) {
                val entry = zip.nextEntry ?: break
                try {
                    if (entry.name == "ORYN_BACKUP_INFO.txt") {
                        zip.copyTo(DiscardOutputStream)
                        continue
                    }
                    val name = entry.name.replace('\\', '/')
                    if (name.startsWith(PRIVATE_PREFIX) || name.startsWith(EXTERNAL_PREFIX)) {
                        recognizedEntries++
                    } else {
                        val separator = name.indexOf('/')
                        if (separator > 0) {
                            val unwrapped = name.substring(separator + 1)
                            if (unwrapped.startsWith(PRIVATE_PREFIX) || unwrapped.startsWith(EXTERNAL_PREFIX)) {
                                recognizedEntries++
                            }
                        }
                    }
                    // Force decompression of every entry so truncated data is detected now.
                    zip.copyTo(DiscardOutputStream)
                } finally {
                    zip.closeEntry()
                }
            }
        }
        if (recognizedEntries == 0) {
            error("This ZIP does not contain an OrynLauncher full backup.")
        }
    }

    private object DiscardOutputStream : OutputStream() {
        override fun write(value: Int) = Unit
        override fun write(buffer: ByteArray, offset: Int, length: Int) = Unit
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
