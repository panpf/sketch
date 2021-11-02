package com.github.panpf.sketch.optionsfilter

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import com.github.panpf.sketch.Configuration
import java.lang.ref.WeakReference

/**
 * 全局移动数据或有流量限制的 WIFI 下暂停下载控制器
 */
class MobileDataPauseDownloadController(private val configuration: Configuration) {

    private val receiver: NetworkChangedBroadcastReceiver =
        NetworkChangedBroadcastReceiver(configuration.context, this)

    /**
     * 开启功能
     */
    var isOpened = false
        set(opened) {
            if (isOpened == opened) {
                return
            }
            field = opened
            if (isOpened) {
                updateStatus(receiver.context)
                receiver.register()
            } else {
                configuration.isPauseDownloadEnabled = false
                receiver.unregister()
            }
        }

    /**
     * 网络状态变化或初始化时更新全局暂停功能
     *
     * @param context [Context]
     */
    private fun updateStatus(context: Context) {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        var pause = false
        if (networkInfo != null && networkInfo.isAvailable) {
            if (networkInfo.type == ConnectivityManager.TYPE_MOBILE) {
                pause = true
            } else if (networkInfo.type == ConnectivityManager.TYPE_WIFI) {
                if (connectivityManager.isActiveNetworkMetered) {
                    pause = true
                }
            }
        }
        configuration.isPauseDownloadEnabled = pause
    }

    /**
     * 监听网络变化的广播
     */
    private class NetworkChangedBroadcastReceiver(
        context: Context,
        download: MobileDataPauseDownloadController
    ) : BroadcastReceiver() {

        val context: Context = context.applicationContext
        private val weakReference: WeakReference<MobileDataPauseDownloadController> =
            WeakReference(download)

        override fun onReceive(context: Context, intent: Intent) {
            if (ConnectivityManager.CONNECTIVITY_ACTION == intent.action) {
                val pauseDownloadController = weakReference.get()
                pauseDownloadController?.updateStatus(context)
            }
        }

        fun register() {
            try {
                context.registerReceiver(
                    this,
                    IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
                )
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }
        }

        fun unregister() {
            try {
                context.unregisterReceiver(this)
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }
        }
    }
}