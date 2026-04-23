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

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
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
@SuppressLint("MissingPermission")
class NetworkObserver(context: Context) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    private var _isCellularNetworkConnected: Boolean = false

    val isCellularNetworkConnected: Boolean
        get() = _isCellularNetworkConnected

    init {
        if (checkPermission(context)) {
            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()
            val networkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) = onConnectivityChange()

                override fun onUnavailable() = onConnectivityChange()

                override fun onLost(network: Network) = onConnectivityChange()

                override fun onCapabilitiesChanged(
                    network: Network, networkCapabilities: NetworkCapabilities
                ) = onConnectivityChange()
            }
            connectivityManager?.registerNetworkCallback(request, networkCallback)
            this.networkCallback = networkCallback

            _isCellularNetworkConnected =
                connectivityManager?.activeNetworkCompat()?.isCellularNetworkConnected() == true
        }
    }

    fun shutdown() {
        val networkCallback = networkCallback
        if (networkCallback != null) {
            connectivityManager?.unregisterNetworkCallback(networkCallback)
        }
    }

    private fun checkPermission(context: Context): Boolean {
        return VERSION.SDK_INT < VERSION_CODES.M
                || context.checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED
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