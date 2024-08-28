/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.panpf.sketch.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.annotation.RequiresApi

fun NetworkObserver(context: Context): NetworkObserver =
    if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
        NetworkObserver21(context)
    } else {
        NetworkObserver1(context)
    }


interface NetworkObserver {
    val isCellularNetworkConnected: Boolean

    /** Stop observing network changes. */
    fun shutdown()
}

@RequiresApi(VERSION_CODES.LOLLIPOP)
class NetworkObserver21(context: Context) : NetworkObserver {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    private var _isCellularNetworkConnected: Boolean = false
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) = onConnectivityChange()
        override fun onUnavailable() = onConnectivityChange()
        override fun onLost(network: Network) = onConnectivityChange()
        override fun onCapabilitiesChanged(
            network: Network, networkCapabilities: NetworkCapabilities
        ) = onConnectivityChange()
    }

    override val isCellularNetworkConnected: Boolean
        get() = _isCellularNetworkConnected

    init {
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager?.registerNetworkCallback(request, networkCallback)

        _isCellularNetworkConnected =
            connectivityManager?.activeNetworkCompat()?.isCellularNetworkConnected() == true
    }

    override fun shutdown() {
        connectivityManager?.unregisterNetworkCallback(networkCallback)
    }

    private fun onConnectivityChange() {
        _isCellularNetworkConnected =
            connectivityManager?.activeNetworkCompat()?.isCellularNetworkConnected() == true
    }

    private fun ConnectivityManager.activeNetworkCompat(): Network? =
        if (VERSION.SDK_INT >= VERSION_CODES.M) {
            activeNetwork
        } else {
            @Suppress("DEPRECATION")
            allNetworks.firstOrNull()
        }

    private fun Network.isCellularNetworkConnected(): Boolean {
        val networkCapabilities = connectivityManager?.getNetworkCapabilities(this)
        return networkCapabilities != null
                && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                && networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    }
}

@Suppress("DEPRECATION")
class NetworkObserver1(context: Context) : NetworkObserver {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

    override val isCellularNetworkConnected: Boolean
        get() {
            val networkInfo = connectivityManager?.activeNetworkInfo
            return if (networkInfo != null && networkInfo.isConnected) {
                val type = networkInfo.type
                type == ConnectivityManager.TYPE_MOBILE
                        || type == ConnectivityManager.TYPE_MOBILE_DUN
                        || type == ConnectivityManager.TYPE_MOBILE_HIPRI
                        || type == ConnectivityManager.TYPE_MOBILE_MMS
                        || type == ConnectivityManager.TYPE_MOBILE_SUPL
                        || type == 10
                        || type == 11
                        || type == 12
                        || type == 14
                        || type == 15
            } else {
                false
            }
        }

    override fun shutdown() {
    }
}