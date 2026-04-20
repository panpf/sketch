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

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES

/**
 * Network observer, used to monitor network changes
 *
 * @see com.github.panpf.sketch.core.android.test.util.NetworkObserverTest
 */
fun NetworkObserver(context: Context): NetworkObserver = NetworkObserver21(context)

interface NetworkObserver {
    val isCellularNetworkConnected: Boolean

    /** Stop observing network changes. */
    fun shutdown()
}

@SuppressLint("MissingPermission")
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
        if (VERSION.SDK_INT < VERSION_CODES.M
            || context.checkSelfPermission(android.Manifest.permission.ACCESS_NETWORK_STATE) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()
            connectivityManager?.registerNetworkCallback(request, networkCallback)
        }

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