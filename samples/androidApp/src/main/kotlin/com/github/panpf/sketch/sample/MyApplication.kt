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

package com.github.panpf.sketch.sample

import android.annotation.SuppressLint
import android.app.Application
import com.github.panpf.sketch.sample.ui.gallery.PhotoPaletteViewModel
import com.github.panpf.sketch.sample.ui.setting.AppSettingsViewModel
import com.github.panpf.sketch.sample.ui.test.DrawableScaleTypeViewModel
import com.github.panpf.sketch.sample.ui.test.LocalVideoListViewModel
import com.github.panpf.sketch.sample.ui.test.transform.BlurTransformationTestViewModel
import com.github.panpf.sketch.sample.ui.test.transform.CircleCropTransformationTestViewModel
import com.github.panpf.sketch.sample.ui.test.transform.MaskTransformationTestViewModel
import com.github.panpf.sketch.sample.ui.test.transform.MultiTransformationTestViewModel
import com.github.panpf.sketch.sample.ui.test.transform.RotateTransformationTestViewModel
import com.github.panpf.sketch.sample.ui.test.transform.RoundedCornersTransformationTestViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        handleSSLHandshake()
        initialApp(this@MyApplication) {
            modules(viewModule())
        }
    }

    private fun viewModule(): Module = module {
        viewModel { AppSettingsViewModel(sketch = get(), appSettings = get(), page = it.get()) }
        viewModelOf(::LocalVideoListViewModel)
        viewModelOf(::DrawableScaleTypeViewModel)
        viewModelOf(::BlurTransformationTestViewModel)
        viewModelOf(::MaskTransformationTestViewModel)
        viewModelOf(::MultiTransformationTestViewModel)
        viewModelOf(::RotateTransformationTestViewModel)
        viewModelOf(::CircleCropTransformationTestViewModel)
        viewModelOf(::RoundedCornersTransformationTestViewModel)
        viewModelOf(::PhotoPaletteViewModel)
    }

    // TODO apigiphy.com is not accessible on Android
    //  The simulator reports SSLHandshakeException: connection closed
    //  A real machine must be bypassed to gain access.
    /**
     * for api.pexels.com on Android 5.0
     */
    private fun handleSSLHandshake() {
        try {
            val trustAllCerts = arrayOf<TrustManager>(
                @SuppressLint("CustomX509TrustManager")
                object : X509TrustManager {
                    override fun getAcceptedIssuers(): Array<X509Certificate?> {
                        return arrayOfNulls(0)
                    }

                    @SuppressLint("TrustAllX509TrustManager")
                    override fun checkClientTrusted(
                        certs: Array<X509Certificate?>?,
                        authType: String?
                    ) {
                    }

                    @SuppressLint("TrustAllX509TrustManager")
                    override fun checkServerTrusted(
                        certs: Array<X509Certificate?>?,
                        authType: String?
                    ) {
                    }
                })
            val sc = SSLContext.getInstance("TLS")
            // trustAllCerts trust all certificates
            sc.init(null, trustAllCerts, SecureRandom())
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.socketFactory)
            HttpsURLConnection.setDefaultHostnameVerifier { _, _ -> true }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}