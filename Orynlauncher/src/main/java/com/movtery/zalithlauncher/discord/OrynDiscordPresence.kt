/*
 * OrynLauncher Discord Rich Presence bridge.
 * Uses Discord's Android Social SDK RPC Binder contract.
 */
package com.movtery.zalithlauncher.discord

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.IInterface
import android.os.Looper
import android.os.Parcel
import android.os.RemoteException
import android.util.Log
import java.util.UUID

/** Publishes the current Minecraft version while the in-game screen is active. */
class OrynDiscordPresence(context: Context) {
    private val context = context
    private val main = Handler(Looper.getMainLooper())
    private var service: RpcService? = null
    private var connection: RpcConnection? = null
    private var bound = false
    private var pendingDisconnect: Runnable? = null
    private var version = "Unknown"

    fun start(minecraftVersion: String) {
        version = minecraftVersion.ifBlank { "Unknown" }
        main.post {
            if (connection != null) {
                publish()
                return@post
            }
            if (bound) unbind()
            try {
                context.packageManager.getPackageInfo(DISCORD_PACKAGE, 0)
                bound = context.bindService(
                    Intent(RPC_ACTION).setPackage(DISCORD_PACKAGE),
                    serviceConnection,
                    Context.BIND_AUTO_CREATE
                )
                if (!bound) Log.d(TAG, "Discord RPC service unavailable")
            } catch (e: Exception) {
                Log.d(TAG, "Discord unavailable; Rich Presence skipped", e)
            }
        }
    }

    fun stop() {
        if (Looper.myLooper() == Looper.getMainLooper()) clearAndDisconnect()
        else main.post { clearAndDisconnect() }
    }

    private val callback = object : RpcCallback() {
        override fun onFrame(frame: String) { Log.d(TAG, "Discord RPC response received") }
        override fun onClose(code: Int, message: String) {
            Log.d(TAG, "Discord RPC closed: $code $message")
        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
            try {
                service = RpcService(binder)
                connection = service?.connect(APPLICATION_ID, "1", callback)
                publish()
            } catch (e: Exception) {
                Log.w(TAG, "Discord RPC handshake failed", e)
                connection = null
            }
        }
        override fun onServiceDisconnected(name: ComponentName) {
            service = null
            connection = null
            bound = false
        }
    }

    private fun publish() {
        val rpc = connection ?: return
        val safeVersion = escape(version)
        val payload = """{"cmd":"SET_ACTIVITY","args":{"pid":${android.os.Process.myPid()},"activity":{"name":"OrynLauncher","details":"Playing Minecraft $safeVersion","state":"Minecraft $safeVersion","timestamps":{"start":${System.currentTimeMillis()}}}},"nonce":"${UUID.randomUUID()}"}"""
        try {
            rpc.sendFrame(payload)
        } catch (e: RemoteException) {
            Log.w(TAG, "Unable to publish Discord Rich Presence", e)
        }
    }

    private fun clearAndDisconnect() {
        val current = connection
        connection = null
        if (current == null) {
            unbind()
            return
        }
        val clear = """{"cmd":"SET_ACTIVITY","args":{"pid":${android.os.Process.myPid()},"activity":null},"nonce":"${UUID.randomUUID()}"}"""
        try { current.sendFrame(clear) }
        catch (e: RemoteException) { Log.d(TAG, "Unable to clear Discord activity", e) }
        pendingDisconnect?.let(main::removeCallbacks)
        val disconnect = Runnable {
            try { current.disconnect() } catch (_: RemoteException) { }
            unbind()
            pendingDisconnect = null
        }
        pendingDisconnect = disconnect
        main.postDelayed(disconnect, 750)
    }

    private fun unbind() {
        pendingDisconnect?.let(main::removeCallbacks)
        pendingDisconnect = null
        service = null
        if (bound) {
            try { context.unbindService(serviceConnection) } catch (_: IllegalArgumentException) { }
            bound = false
        }
    }

    private fun escape(value: String): String =
        value.replace("\\", "\\\\").replace("\"", "\\\"")

    private abstract class RpcCallback : Binder(), IInterface {
        init { attachInterface(this, CALLBACK_DESCRIPTOR) }
        override fun asBinder(): IBinder = this
        abstract fun onFrame(frame: String)
        abstract fun onClose(code: Int, message: String)
        override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
            if (code == INTERFACE_TRANSACTION) {
                reply?.writeString(CALLBACK_DESCRIPTOR)
                return true
            }
            if (code in 1..0xFFFFFF) data.enforceInterface(CALLBACK_DESCRIPTOR)
            when (code) {
                1 -> { onFrame(data.readString().orEmpty()); reply?.writeNoException(); return true }
                2 -> { onClose(data.readInt(), data.readString().orEmpty()); reply?.writeNoException(); return true }
            }
            return super.onTransact(code, data, reply, flags)
        }
    }

    private class RpcService(private val binder: IBinder) : IInterface {
        override fun asBinder(): IBinder = binder
        fun connect(appId: Long, sdkVersion: String, callback: IBinder): RpcConnection? {
            val data = Parcel.obtain()
            val reply = Parcel.obtain()
            try {
                data.writeInterfaceToken(RPC_ACTION)
                data.writeLong(appId)
                data.writeString(sdkVersion)
                data.writeStrongBinder(callback)
                binder.transact(1, data, reply, 0)
                reply.readException()
                return reply.readStrongBinder()?.let(::RpcConnection)
            } finally { reply.recycle(); data.recycle() }
        }
    }

    private class RpcConnection(private val binder: IBinder) : IInterface {
        override fun asBinder(): IBinder = binder
        fun sendFrame(frame: String) = transactString(1, frame)
        fun disconnect() {
            val data = Parcel.obtain()
            val reply = Parcel.obtain()
            try {
                data.writeInterfaceToken(CONNECTION_DESCRIPTOR)
                binder.transact(2, data, reply, 0)
                reply.readException()
            } finally { reply.recycle(); data.recycle() }
        }
        private fun transactString(code: Int, value: String) {
            val data = Parcel.obtain()
            val reply = Parcel.obtain()
            try {
                data.writeInterfaceToken(CONNECTION_DESCRIPTOR)
                data.writeString(value)
                binder.transact(code, data, reply, 0)
                reply.readException()
            } finally { reply.recycle(); data.recycle() }
        }
    }

    companion object {
        private const val TAG = "OrynDiscordPresence"
        private const val APPLICATION_ID = 1556187212620763238L
        private const val DISCORD_PACKAGE = "com.discord"
        private const val RPC_ACTION = "com.discord.socialsdk.rpc.IDiscordRpcService"
        private const val CALLBACK_DESCRIPTOR = "com.discord.socialsdk.rpc.IDiscordRpcCallback"
        private const val CONNECTION_DESCRIPTOR = "com.discord.socialsdk.rpc.IDiscordRpcConnection"
    }
}
