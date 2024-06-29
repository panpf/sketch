/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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

import android.app.Application
import android.content.Context
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import com.github.panpf.sketch.SingletonSketch
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.supportAnimatedGif
import com.github.panpf.sketch.decode.supportAnimatedHeif
import com.github.panpf.sketch.decode.supportAnimatedWebp
import com.github.panpf.sketch.decode.supportApkIcon
import com.github.panpf.sketch.decode.supportFFmpegVideoFrame
import com.github.panpf.sketch.decode.supportKoralGif
import com.github.panpf.sketch.decode.supportMovieGif
import com.github.panpf.sketch.decode.supportSvg
import com.github.panpf.sketch.decode.supportVideoFrame
import com.github.panpf.sketch.fetch.supportAppIcon
import com.github.panpf.sketch.fetch.supportComposeResources
import com.github.panpf.sketch.http.HurlStack
import com.github.panpf.sketch.http.KtorStack
import com.github.panpf.sketch.http.OkHttpStack
import com.github.panpf.sketch.request.supportPauseLoadWhenScrolling
import com.github.panpf.sketch.request.supportSaveCellularTraffic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MyApplication : Application(), SingletonSketch.Factory {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun createSketch(context: Context): Sketch = Sketch.Builder(this).apply {
        val httpStack = when (appSettingsService.httpClient.value) {
            "Ktor" -> KtorStack()
            "OkHttp" -> OkHttpStack.Builder().build()
            "HttpURLConnection" -> HurlStack.Builder().build()
            else -> throw IllegalArgumentException("Unknown httpClient: ${appSettingsService.httpClient.value}")
        }
        httpStack(httpStack)
        components {
            supportComposeResources()

            supportSaveCellularTraffic()
            supportPauseLoadWhenScrolling()

            supportAppIcon()
            supportApkIcon()
            supportSvg()

            // video
            when (appSettingsService.videoFrameDecoder.value) {
                "FFmpeg" -> supportFFmpegVideoFrame()
                "AndroidBuiltIn" -> supportVideoFrame()
                else -> throw IllegalArgumentException("Unknown videoFrameDecoder: ${appSettingsService.videoFrameDecoder.value}")
            }

            // gif
            when (appSettingsService.gifDecoder.value) {
                "KoralGif" -> supportKoralGif()
                "ImageDecoder+Movie" -> if (VERSION.SDK_INT >= VERSION_CODES.P) supportAnimatedGif() else supportMovieGif()
                else -> throw IllegalArgumentException("Unknown animatedDecoder: ${appSettingsService.gifDecoder.value}")
            }

            // webp animated
            if (VERSION.SDK_INT >= VERSION_CODES.P) {
                supportAnimatedWebp()
            }

            // heif animated
            if (VERSION.SDK_INT >= VERSION_CODES.R) {
                supportAnimatedHeif()
            }
        }
        // To be able to print the Sketch initialization log
        logger(level = appSettingsService.logLevel.value)
    }.build().apply {
        coroutineScope.launch {
            appSettingsService.logLevel.collect {
                logger.level = it
            }
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        coroutineScope.cancel()
    }
}