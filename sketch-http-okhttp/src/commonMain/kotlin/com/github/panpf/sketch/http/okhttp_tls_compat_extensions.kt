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

package com.github.panpf.sketch.http

import com.github.panpf.sketch.http.internal.TlsCompatSocketFactory
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import okhttp3.internal.platform.Platform

fun OkHttpClient.Builder.setEnabledTlsProtocols(enabledTlsProtocols: Array<String>): OkHttpClient.Builder {
    if (enabledTlsProtocols.isNotEmpty()) {
        try {
            val sslSocketFactory = TlsCompatSocketFactory(enabledTlsProtocols)
            val trustManager = Platform.get().trustManager(sslSocketFactory)
                ?: throw IllegalStateException(
                    "Unable to extract the trust manager on ${Platform.get()}, " +
                            "sslSocketFactory is ${sslSocketFactory.javaClass}"
                )
            sslSocketFactory(sslSocketFactory, trustManager)

            val connectionSpec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(*enabledTlsProtocols.map { TlsVersion.forJavaName(it) }.toTypedArray())
                .build()
            connectionSpecs(
                listOf(connectionSpec, ConnectionSpec.COMPATIBLE_TLS, ConnectionSpec.CLEARTEXT)
            )
        } catch (e: Exception) {
            e.printStackTrace()
            println("OkHttpTLSCompat. Error while setting TLS ${enabledTlsProtocols.joinToString()}")
        }
    }
    return this
}