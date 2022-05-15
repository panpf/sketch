package com.github.panpf.sketch.util

import android.content.ComponentCallbacks2
import android.content.Context
import android.content.res.Configuration
import com.github.panpf.sketch.Sketch
import java.util.concurrent.atomic.AtomicBoolean

class SystemCallbacks(
    val context: Context,
    val sketch: Sketch,
) {

    private val networkObserver by lazy { NetworkObserver(context) }
    private val _isShutdown = AtomicBoolean(false)
    private val componentCallbacks2 = object : ComponentCallbacks2 {
        override fun onConfigurationChanged(newConfig: Configuration) {
        }

        override fun onLowMemory() {
            sketch.memoryCache.clear()
            sketch.bitmapPool.clear()
        }

        override fun onTrimMemory(level: Int) {
            sketch.memoryCache.trim(level)
            sketch.bitmapPool.trim(level)
        }
    }

    init {
        context.registerComponentCallbacks(componentCallbacks2)
    }

    val isShutdown get() = _isShutdown.get()

    val isCellularNetworkConnected: Boolean
        get() = networkObserver.isCellularNetworkConnected

    fun shutdown() {
        if (_isShutdown.getAndSet(true)) return
        context.unregisterComponentCallbacks(componentCallbacks2)
        networkObserver.shutdown()
    }

//    fun isCellularNetworkConnected(): Boolean =
//        if (VERSION.SDK_INT >= VERSION_CODES.M) {
//            val network = connectivityManager?.activeNetwork
//            val networkCapabilities =
//                if (network != null) connectivityManager?.getNetworkCapabilities(network) else null
//            networkCapabilities != null &&
//                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
//                    && networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
//        } else {
//            val networkInfo = connectivityManager?.activeNetworkInfo
//            if (networkInfo != null && networkInfo.isConnected) {
//                val type = networkInfo.type
//                type == ConnectivityManager.TYPE_MOBILE
//                        || type == ConnectivityManager.TYPE_MOBILE_DUN
//                        || type == ConnectivityManager.TYPE_MOBILE_HIPRI
//                        || type == ConnectivityManager.TYPE_MOBILE_MMS
//                        || type == ConnectivityManager.TYPE_MOBILE_SUPL
//                        || type == 10
//                        || type == 11
//                        || type == 12
//                        || type == 14
//                        || type == 15
//            } else {
//                false
//            }
//        }
}