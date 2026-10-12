/*
 * Zalith Launcher 2
 * Copyright (C) 2025 MovTery <movtery228@qq.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/gpl-3.0.txt>.
 */

package com.movtery.zalithlauncher.game.recorder

import android.Manifest
import android.app.Activity
import android.app.Notification
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.media.projection.MediaProjectionManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.notification.NOTIFICATION_ID_RECORDING_SERVICE
import com.movtery.zalithlauncher.notification.NotificationChannelData

class MediaProjectionForegroundService : Service() {
    companion object {
        private const val TAG = "ProjectionFGS"
        const val ACTION_START_RECORDING = "com.movtery.zalithlauncher.action.START_SCREEN_RECORDING"
        const val EXTRA_RESULT_CODE = "projection_result_code"
        const val EXTRA_RESULT_DATA = "projection_result_data"
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val channel = NotificationChannelData.RECORDING_SERVICE_CHANNEL
        val notification: Notification = NotificationCompat.Builder(this, channel.channelId)
            .setContentTitle(getString(R.string.recorder_notification_title))
            .setContentText(getString(R.string.recorder_notification_text))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        val canUseMediaProjection = checkSelfPermission(
            Manifest.permission.FOREGROUND_SERVICE_MEDIA_PROJECTION
        ) == PackageManager.PERMISSION_GRANTED

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                if (!canUseMediaProjection) {
                    throw SecurityException("Missing FOREGROUND_SERVICE_MEDIA_PROJECTION permission.")
                }
                startForeground(
                    NOTIFICATION_ID_RECORDING_SERVICE,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
                )
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(
                    NOTIFICATION_ID_RECORDING_SERVICE,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
                )
            } else {
                startForeground(NOTIFICATION_ID_RECORDING_SERVICE, notification)
            }
        } catch (e: Exception) {
            val message = "Could not start screen recorder foreground service: ${e.message ?: e.javaClass.simpleName}"
            Log.e(TAG, message, e)
            GameRecorder.reportError(message)
            stopSelf(startId)
            return START_NOT_STICKY
        }

        if (intent?.action == ACTION_START_RECORDING) {
            @Suppress("DEPRECATION")
            val resultData = if (Build.VERSION.SDK_INT >= 33) {
                intent.getParcelableExtra(EXTRA_RESULT_DATA, Intent::class.java)
            } else {
                intent.getParcelableExtra(EXTRA_RESULT_DATA)
            }
            val resultCode = intent.getIntExtra(EXTRA_RESULT_CODE, Activity.RESULT_CANCELED)
            if (resultCode != Activity.RESULT_OK || resultData == null) {
                GameRecorder.reportError("Screen capture permission result was invalid. Try starting the recorder again.")
                stopSelf(startId)
                return START_NOT_STICKY
            }

            try {
                // getMediaProjection must run only after this service has entered
                // the mediaProjection foreground state (Android 14+ requirement).
                val manager = getSystemService(MediaProjectionManager::class.java)
                val projection = manager.getMediaProjection(resultCode, resultData)
                    ?: throw IllegalStateException("Android did not provide a screen-capture session.")
                GameRecorder.start(this, projection)
                if (GameRecorder.state.value == RecordingState.IDLE) {
                    if (GameRecorder.lastError.value == null) {
                        GameRecorder.reportError("The recorder could not start. Check available storage and try again.")
                    }
                    stopSelf(startId)
                }
            } catch (e: Exception) {
                val message = "Could not begin screen capture: ${e.message ?: e.javaClass.simpleName}"
                Log.e(TAG, message, e)
                GameRecorder.reportError(message)
                stopSelf(startId)
            }
        }

        return START_NOT_STICKY
    }
}
