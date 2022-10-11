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

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.multidex.MultiDexApplication
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.SketchFactory
import com.github.panpf.sketch.decode.ApkIconBitmapDecoder
import com.github.panpf.sketch.decode.AppIconBitmapDecoder
import com.github.panpf.sketch.decode.FFmpegVideoFrameBitmapDecoder
import com.github.panpf.sketch.decode.GifAnimatedDrawableDecoder
import com.github.panpf.sketch.decode.GifDrawableDrawableDecoder
import com.github.panpf.sketch.decode.GifMovieDrawableDecoder
import com.github.panpf.sketch.decode.HeifAnimatedDrawableDecoder
import com.github.panpf.sketch.decode.SvgBitmapDecoder
import com.github.panpf.sketch.decode.VideoFrameBitmapDecoder
import com.github.panpf.sketch.decode.WebpAnimatedDrawableDecoder
import com.github.panpf.sketch.fetch.AppIconUriFetcher
import com.github.panpf.sketch.http.OkHttpStack
import com.github.panpf.sketch.request.PauseLoadWhenScrollingDrawableDecodeInterceptor
import com.github.panpf.sketch.request.SaveCellularTrafficDisplayInterceptor
import com.github.panpf.sketch.sample.image.SettingsDisplayRequestInterceptor
import com.github.panpf.sketch.util.Logger
import com.tencent.mmkv.MMKV

class MyApplication : MultiDexApplication(), SketchFactory {

    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
    }

    override fun createSketch(): Sketch = Sketch.Builder(this).apply {
        logger(Logger(Logger.Level.valueOf(prefsService.logLevel.value)))
        httpStack(OkHttpStack.Builder().apply {
            if (VERSION.SDK_INT <= 19) {
                enabledTlsProtocols("TLSv1.1", "TLSv1.2")
            }
        }.build())
//        httpStack(HurlStack.Builder().apply {
//            if (VERSION.SDK_INT <= 19) {
//                enabledTlsProtocols("TLSv1.1", "TLSv1.2")
//            }
//        }.build())
        components {
            // global image request config
            addRequestInterceptor(SettingsDisplayRequestInterceptor())

            addRequestInterceptor(SaveCellularTrafficDisplayInterceptor())

            addDrawableDecodeInterceptor(PauseLoadWhenScrollingDrawableDecodeInterceptor())

            // app icon
            addFetcher(AppIconUriFetcher.Factory())
            addBitmapDecoder(AppIconBitmapDecoder.Factory())

            // apk icon
            addBitmapDecoder(ApkIconBitmapDecoder.Factory())

            // svg
            addBitmapDecoder(SvgBitmapDecoder.Factory())

            // video
            addBitmapDecoder(
                if (VERSION.SDK_INT >= VERSION_CODES.O_MR1) {
                    VideoFrameBitmapDecoder.Factory()
                } else {
                    FFmpegVideoFrameBitmapDecoder.Factory()
                }
            )

            // gif
            addDrawableDecoder(
                when {
                    VERSION.SDK_INT >= VERSION_CODES.P -> GifAnimatedDrawableDecoder.Factory()
                    VERSION.SDK_INT >= VERSION_CODES.KITKAT -> GifMovieDrawableDecoder.Factory()
                    else -> GifDrawableDrawableDecoder.Factory()
                }
            )

            // webp animated
            if (VERSION.SDK_INT >= VERSION_CODES.P) {
                addDrawableDecoder(WebpAnimatedDrawableDecoder.Factory())
            }

            // heif animated
            if (VERSION.SDK_INT >= VERSION_CODES.R) {
                addDrawableDecoder(HeifAnimatedDrawableDecoder.Factory())
            }
        }
    }.build()
}