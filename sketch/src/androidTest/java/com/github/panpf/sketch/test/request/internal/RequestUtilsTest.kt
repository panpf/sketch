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
package com.github.panpf.sketch.test.request.internal

import android.graphics.Bitmap.Config.RGB_565
import android.graphics.ColorSpace
import android.graphics.ColorSpace.Named.SRGB
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.cache.CachePolicy.READ_ONLY
import com.github.panpf.sketch.cache.CachePolicy.WRITE_ONLY
import com.github.panpf.sketch.request.Depth.MEMORY
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.internal.newCacheKey
import com.github.panpf.sketch.request.internal.newKey
import com.github.panpf.sketch.test.utils.Test3BitmapDecodeInterceptor
import com.github.panpf.sketch.test.utils.Test3RequestInterceptor
import com.github.panpf.sketch.test.utils.Test4DrawableDecodeInterceptor
import com.github.panpf.sketch.transform.CircleCropTransformation
import com.github.panpf.sketch.transform.RotateTransformation
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RequestUtilsTest {

    @Test
    fun newCacheKey() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val uriString = "http://sample.com/sample.jpeg?from=sketch"
        val uri = Uri.parse(uriString)

        var request = DisplayRequest(context, uriString)
        var cacheKeyUri = uri
        val cacheKeyHistoryList = ArrayList<String>()

        // default
        Assert.assertEquals(
            cacheKeyUri.toString().let { Uri.decode(it) },
            request.newCacheKey().apply { cacheKeyHistoryList.add(this) }
        )

        // Parameter no cacheKey
        request = request.newDisplayRequest {
            setParameter("testKey1", "testValue1", null)
        }
        cacheKeyUri = cacheKeyUri.buildUpon().build()
        Assert.assertEquals(
            cacheKeyUri.toString().let { Uri.decode(it) },
            request.newCacheKey().apply { cacheKeyHistoryList.add(this) }
        )

        // Parameter cacheKey
        request = request.newDisplayRequest {
            setParameter("testKey2", "testValue2")
        }
        cacheKeyUri = cacheKeyUri.buildUpon().apply {
            appendQueryParameter("_parameters", request.parameters!!.cacheKey)
        }.build()
        Assert.assertEquals(
            cacheKeyUri.toString().let { Uri.decode(it) },
            request.newCacheKey().apply { cacheKeyHistoryList.add(this) }
        )

        // bitmapConfig
        request = request.newDisplayRequest {
            bitmapConfig(RGB_565)
        }
        cacheKeyUri = cacheKeyUri.buildUpon().apply {
            appendQueryParameter("_bitmapConfig", request.bitmapConfig!!.key)
        }.build()
        Assert.assertEquals(
            cacheKeyUri.toString().let { Uri.decode(it) },
            request.newCacheKey().apply { cacheKeyHistoryList.add(this) }
        )

        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            // colorSpace
            request = request.newDisplayRequest {
                colorSpace(ColorSpace.get(SRGB))
            }
            cacheKeyUri = cacheKeyUri.buildUpon().apply {
                appendQueryParameter("_colorSpace", request.colorSpace!!.name.replace(" ", "_"))
            }.build()
            Assert.assertEquals(
                cacheKeyUri.toString().let { Uri.decode(it) },
                request.newCacheKey().apply { cacheKeyHistoryList.add(this) }
            )
        }

        // preferQualityOverSpeed false
        request = request.newDisplayRequest {
            @Suppress("DEPRECATION")
            preferQualityOverSpeed(false)
        }
        cacheKeyUri = cacheKeyUri.buildUpon().build()
        Assert.assertEquals(
            cacheKeyUri.toString().let { Uri.decode(it) },
            request.newCacheKey().apply { cacheKeyHistoryList.add(this) }
        )

        // preferQualityOverSpeed true
        request = request.newDisplayRequest {
            @Suppress("DEPRECATION")
            preferQualityOverSpeed(true)
        }
        cacheKeyUri = cacheKeyUri.buildUpon().apply {
            appendQueryParameter("_preferQualityOverSpeed", "true")
        }.build()
        Assert.assertEquals(
            cacheKeyUri.toString().let { Uri.decode(it) },
            request.newCacheKey().apply { cacheKeyHistoryList.add(this) }
        )

        // resize
        request = request.newDisplayRequest {
            resize(200, 300)
        }
        cacheKeyUri = cacheKeyUri.buildUpon().apply {
            appendQueryParameter("_resize", request.resize!!.key)
        }.build()
        Assert.assertEquals(
            cacheKeyUri.toString().let { Uri.decode(it) },
            request.newCacheKey().apply { cacheKeyHistoryList.add(this) }
        )

        // transformations
        request = request.newDisplayRequest {
            transformations(CircleCropTransformation(), RotateTransformation(40))
        }
        cacheKeyUri = cacheKeyUri.buildUpon().apply {
            appendQueryParameter(
                "_transformations",
                request.transformations!!
                    .joinToString(prefix = "[", postfix = "]", separator = ",") {
                        it.key.replace("Transformation", "")
                    }
            )
        }.build()
        Assert.assertEquals(
            cacheKeyUri.toString().let { Uri.decode(it) },
            request.newCacheKey().apply { cacheKeyHistoryList.add(this) }
        )

        // decodeInterceptors
        request = request.newDisplayRequest {
            components {
                addRequestInterceptor(Test3RequestInterceptor())
                addBitmapDecodeInterceptor(Test3BitmapDecodeInterceptor())
                addDrawableDecodeInterceptor(Test4DrawableDecodeInterceptor())
            }
        }
        cacheKeyUri = cacheKeyUri.buildUpon().apply {
            appendQueryParameter(
                "_interceptors",
                "[Test3RequestInterceptor,Test3BitmapDecodeInterceptor,Test4DrawableDecodeInterceptor]"
            )
        }.build()
        Assert.assertEquals(
            cacheKeyUri.toString().let { Uri.decode(it) },
            request.newCacheKey().apply { cacheKeyHistoryList.add(this) }
        )

        // ignoreExifOrientation false
        request = request.newDisplayRequest {
            ignoreExifOrientation(false)
        }
        cacheKeyUri = cacheKeyUri.buildUpon().build()
        Assert.assertEquals(
            cacheKeyUri.toString().let { Uri.decode(it) },
            request.newCacheKey().apply { cacheKeyHistoryList.add(this) }
        )

        // ignoreExifOrientation true
        request = request.newDisplayRequest {
            ignoreExifOrientation(true)
        }
        cacheKeyUri = cacheKeyUri.buildUpon().apply {
            appendQueryParameter("_ignoreExifOrientation", "true")
        }.build()
        Assert.assertEquals(
            cacheKeyUri.toString().let { Uri.decode(it) },
            request.newCacheKey().apply { cacheKeyHistoryList.add(this) }
        )

        // disallowAnimatedImage false
        request = request.newDisplayRequest {
            disallowAnimatedImage(false)
        }
        cacheKeyUri = cacheKeyUri.buildUpon().build()
        Assert.assertEquals(
            cacheKeyUri.toString().let { Uri.decode(it) },
            request.newCacheKey().apply { cacheKeyHistoryList.add(this) }
        )

        // disallowAnimatedImage true
        request = request.newDisplayRequest {
            disallowAnimatedImage(true)
        }
        cacheKeyUri = cacheKeyUri.buildUpon().apply {
            appendQueryParameter("_disallowAnimatedImage", "true")
        }.build()
        Assert.assertEquals(
            cacheKeyUri.toString().let { Uri.decode(it) },
            request.newCacheKey().apply { cacheKeyHistoryList.add(this) }
        )

        Assert.assertEquals(cacheKeyHistoryList.size - 4, cacheKeyHistoryList.distinct().size)
    }

    @Test
    fun newKeyWithDisplay() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val uriString = "http://sample.com/sample.jpeg?from=sketch"
        val uri = Uri.parse(uriString)

        var request = DisplayRequest(context, uriString)
        var keyUri = uri
        val keyHistoryList = ArrayList<String>()

        // default
        Assert.assertEquals(
            keyUri.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        // depth
        request = request.newDisplayRequest {
            depth(MEMORY)
        }
        keyUri = keyUri.buildUpon().apply {
            appendQueryParameter("_depth", request.depth.toString())
        }.build()
        Assert.assertEquals(
            keyUri.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        // Parameter no cacheKey
        request = request.newDisplayRequest {
            setParameter("testKey1", "testValue1", null)
        }
        val keyUri1 = keyUri.buildUpon().apply {
            appendQueryParameter("_parameters", request.parameters!!.key)
        }.build()
        Assert.assertEquals(
            keyUri1.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        // Parameter cacheKey
        request = request.newDisplayRequest {
            setParameter("testKey2", "testValue2")
        }
        keyUri = keyUri.buildUpon().apply {
            appendQueryParameter("_parameters", request.parameters!!.key)
        }.build()
        Assert.assertEquals(
            keyUri.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        // httpHeaders true
        request = request.newDisplayRequest {
            addHttpHeader("httpKey", "httpValue")
        }
        keyUri = keyUri.buildUpon().apply {
            appendQueryParameter("_httpHeaders", request.httpHeaders!!.toString())
        }.build()
        Assert.assertEquals(
            keyUri.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        // downloadCachePolicy true
        request = request.newDisplayRequest {
            downloadCachePolicy(WRITE_ONLY)
        }
        keyUri = keyUri.buildUpon().apply {
            appendQueryParameter("_downloadCachePolicy", request.downloadCachePolicy.toString())
        }.build()
        Assert.assertEquals(
            keyUri.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        // bitmapConfig
        request = request.newDisplayRequest {
            bitmapConfig(RGB_565)
        }
        keyUri = keyUri.buildUpon().apply {
            appendQueryParameter("_bitmapConfig", request.bitmapConfig!!.key)
        }.build()
        Assert.assertEquals(
            keyUri.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            // colorSpace
            request = request.newDisplayRequest {
                colorSpace(ColorSpace.get(SRGB))
            }
            keyUri = keyUri.buildUpon().apply {
                appendQueryParameter("_colorSpace", request.colorSpace!!.name.replace(" ", "_"))
            }.build()
            Assert.assertEquals(
                keyUri.toString().let { Uri.decode(it) },
                request.newKey().apply { keyHistoryList.add(this) }
            )
        }

        // preferQualityOverSpeed false
        request = request.newDisplayRequest {
            @Suppress("DEPRECATION")
            preferQualityOverSpeed(false)
        }
        keyUri = keyUri.buildUpon().build()
        Assert.assertEquals(
            keyUri.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        // preferQualityOverSpeed true
        request = request.newDisplayRequest {
            @Suppress("DEPRECATION")
            preferQualityOverSpeed(true)
        }
        keyUri = keyUri.buildUpon().apply {
            appendQueryParameter("_preferQualityOverSpeed", "true")
        }.build()
        Assert.assertEquals(
            keyUri.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        // resize
        request = request.newDisplayRequest {
            resize(200, 300)
        }
        keyUri = keyUri.buildUpon().apply {
            appendQueryParameter("_resize", request.resize!!.key)
        }.build()
        Assert.assertEquals(
            keyUri.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        // transformations
        request = request.newDisplayRequest {
            transformations(CircleCropTransformation(), RotateTransformation(40))
        }
        keyUri = keyUri.buildUpon().apply {
            appendQueryParameter(
                "_transformations",
                request.transformations!!
                    .joinToString(prefix = "[", postfix = "]", separator = ",") {
                        it.key.replace("Transformation", "")
                    })
        }.build()
        Assert.assertEquals(
            keyUri.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        // bitmapDecodeInterceptors
        request = request.newDisplayRequest {
            components {
                addRequestInterceptor(Test3RequestInterceptor())
                addBitmapDecodeInterceptor(Test3BitmapDecodeInterceptor())
                addDrawableDecodeInterceptor(Test4DrawableDecodeInterceptor())
            }
        }
        keyUri = keyUri.buildUpon().apply {
            appendQueryParameter(
                "_interceptors",
                "[Test3RequestInterceptor,Test3BitmapDecodeInterceptor,Test4DrawableDecodeInterceptor]"
            )
        }.build()
        Assert.assertEquals(
            keyUri.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        // ignoreExifOrientation false
        request = request.newDisplayRequest {
            ignoreExifOrientation(false)
        }
        keyUri = keyUri.buildUpon().build()
        Assert.assertEquals(
            keyUri.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        // ignoreExifOrientation true
        request = request.newDisplayRequest {
            ignoreExifOrientation(true)
        }
        keyUri = keyUri.buildUpon().apply {
            appendQueryParameter("_ignoreExifOrientation", "true")
        }.build()
        Assert.assertEquals(
            keyUri.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        // resultCachePolicy true
        request = request.newDisplayRequest {
            resultCachePolicy(READ_ONLY)
        }
        keyUri = keyUri.buildUpon().apply {
            appendQueryParameter("_resultCachePolicy", request.resultCachePolicy.toString())
        }.build()
        Assert.assertEquals(
            keyUri.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        // disallowAnimatedImage false
        request = request.newDisplayRequest {
            disallowAnimatedImage(false)
        }
        keyUri = keyUri.buildUpon().build()
        Assert.assertEquals(
            keyUri.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        // disallowAnimatedImage true
        request = request.newDisplayRequest {
            disallowAnimatedImage(true)
        }
        keyUri = keyUri.buildUpon().apply {
            appendQueryParameter("_disallowAnimatedImage", "true")
        }.build()
        Assert.assertEquals(
            keyUri.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        // memoryCachePolicy true
        request = request.newDisplayRequest {
            memoryCachePolicy(DISABLED)
        }
        keyUri = keyUri.buildUpon().apply {
            appendQueryParameter("_memoryCachePolicy", request.memoryCachePolicy.toString())
        }.build()
        Assert.assertEquals(
            keyUri.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        Assert.assertEquals(keyHistoryList.size - 3, keyHistoryList.distinct().size)
    }

    @Test
    fun newKeyWithLoad() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val uriString = "http://sample.com/sample.jpeg?from=sketch"
        val uri = Uri.parse(uriString)

        var request = LoadRequest(context, uriString)
        var keyUri = uri
        val keyHistoryList = ArrayList<String>()

        // default
        Assert.assertEquals(
            keyUri.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        // depth
        request = request.newLoadRequest {
            depth(MEMORY)
        }
        keyUri = keyUri.buildUpon().apply {
            appendQueryParameter("_depth", request.depth.toString())
        }.build()
        Assert.assertEquals(
            keyUri.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        // Parameter no cacheKey
        request = request.newLoadRequest {
            setParameter("testKey1", "testValue1", null)
        }
        val keyUri1 = keyUri.buildUpon().apply {
            appendQueryParameter("_parameters", request.parameters!!.key)
        }.build()
        Assert.assertEquals(
            keyUri1.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        // Parameter cacheKey
        request = request.newLoadRequest {
            setParameter("testKey2", "testValue2")
        }
        keyUri = keyUri.buildUpon().apply {
            appendQueryParameter("_parameters", request.parameters!!.key)
        }.build()
        Assert.assertEquals(
            keyUri.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        // httpHeaders true
        request = request.newLoadRequest {
            addHttpHeader("httpKey", "httpValue")
        }
        keyUri = keyUri.buildUpon().apply {
            appendQueryParameter("_httpHeaders", request.httpHeaders!!.toString())
        }.build()
        Assert.assertEquals(
            keyUri.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        // downloadCachePolicy true
        request = request.newLoadRequest {
            downloadCachePolicy(WRITE_ONLY)
        }
        keyUri = keyUri.buildUpon().apply {
            appendQueryParameter("_downloadCachePolicy", request.downloadCachePolicy.toString())
        }.build()
        Assert.assertEquals(
            keyUri.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        // bitmapConfig
        request = request.newLoadRequest {
            bitmapConfig(RGB_565)
        }
        keyUri = keyUri.buildUpon().apply {
            appendQueryParameter("_bitmapConfig", request.bitmapConfig!!.key)
        }.build()
        Assert.assertEquals(
            keyUri.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            // colorSpace
            request = request.newLoadRequest {
                colorSpace(ColorSpace.get(SRGB))
            }
            keyUri = keyUri.buildUpon().apply {
                appendQueryParameter("_colorSpace", request.colorSpace!!.name.replace(" ", "_"))
            }.build()
            Assert.assertEquals(
                keyUri.toString().let { Uri.decode(it) },
                request.newKey().apply { keyHistoryList.add(this) }
            )
        }

        // preferQualityOverSpeed false
        request = request.newLoadRequest {
            @Suppress("DEPRECATION")
            preferQualityOverSpeed(false)
        }
        keyUri = keyUri.buildUpon().build()
        Assert.assertEquals(
            keyUri.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        // preferQualityOverSpeed true
        request = request.newLoadRequest {
            @Suppress("DEPRECATION")
            preferQualityOverSpeed(true)
        }
        keyUri = keyUri.buildUpon().apply {
            appendQueryParameter("_preferQualityOverSpeed", "true")
        }.build()
        Assert.assertEquals(
            keyUri.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        // resize
        request = request.newLoadRequest {
            resize(200, 300)
        }
        keyUri = keyUri.buildUpon().apply {
            appendQueryParameter("_resize", request.resize!!.key)
        }.build()
        Assert.assertEquals(
            keyUri.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        // transformations
        request = request.newLoadRequest {
            transformations(CircleCropTransformation(), RotateTransformation(40))
        }
        keyUri = keyUri.buildUpon().apply {
            appendQueryParameter(
                "_transformations",
                request.transformations!!
                    .joinToString(prefix = "[", postfix = "]", separator = ",") {
                        it.key.replace("Transformation", "")
                    })
        }.build()
        Assert.assertEquals(
            keyUri.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        // bitmapDecodeInterceptors
        request = request.newLoadRequest {
            components {
                addRequestInterceptor(Test3RequestInterceptor())
                addBitmapDecodeInterceptor(Test3BitmapDecodeInterceptor())
                addDrawableDecodeInterceptor(Test4DrawableDecodeInterceptor())
            }
        }
        keyUri = keyUri.buildUpon().apply {
            appendQueryParameter(
                "_interceptors",
                "[Test3RequestInterceptor,Test3BitmapDecodeInterceptor,Test4DrawableDecodeInterceptor]"
            )
        }.build()
        Assert.assertEquals(
            keyUri.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        // ignoreExifOrientation false
        request = request.newLoadRequest {
            ignoreExifOrientation(false)
        }
        keyUri = keyUri.buildUpon().build()
        Assert.assertEquals(
            keyUri.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        // ignoreExifOrientation true
        request = request.newLoadRequest {
            ignoreExifOrientation(true)
        }
        keyUri = keyUri.buildUpon().apply {
            appendQueryParameter("_ignoreExifOrientation", "true")
        }.build()
        Assert.assertEquals(
            keyUri.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        // resultCachePolicy true
        request = request.newLoadRequest {
            resultCachePolicy(READ_ONLY)
        }
        keyUri = keyUri.buildUpon().apply {
            appendQueryParameter("_resultCachePolicy", request.resultCachePolicy.toString())
        }.build()
        Assert.assertEquals(
            keyUri.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        // disallowAnimatedImage false
        request = request.newLoadRequest {
            disallowAnimatedImage(false)
        }
        keyUri = keyUri.buildUpon().build()
        Assert.assertEquals(
            keyUri.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        // disallowAnimatedImage true
        request = request.newLoadRequest {
            disallowAnimatedImage(true)
        }
        keyUri = keyUri.buildUpon().build()
        Assert.assertEquals(
            keyUri.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        // memoryCachePolicy true
        request = request.newLoadRequest {
            memoryCachePolicy(DISABLED)
        }
        keyUri = keyUri.buildUpon().build()
        Assert.assertEquals(
            keyUri.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        Assert.assertEquals(keyHistoryList.size - 5, keyHistoryList.distinct().size)
    }

    @Test
    fun newKeyWithDownload() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val uriString = "http://sample.com/sample.jpeg?from=sketch"
        val uri = Uri.parse(uriString)

        var request = DownloadRequest(context, uriString)
        var keyUri = uri
        val keyHistoryList = ArrayList<String>()

        // default
        Assert.assertEquals(
            keyUri.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        // depth
        request = request.newDownloadRequest {
            depth(MEMORY)
        }
        keyUri = keyUri.buildUpon().apply {
            appendQueryParameter("_depth", request.depth.toString())
        }.build()
        Assert.assertEquals(
            keyUri.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        // Parameter no cacheKey
        request = request.newDownloadRequest {
            setParameter("testKey1", "testValue1", null)
        }
        val keyUri1 = keyUri.buildUpon().apply {
            appendQueryParameter("_parameters", request.parameters!!.key)
        }.build()
        Assert.assertEquals(
            keyUri1.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        // Parameter cacheKey
        request = request.newDownloadRequest {
            setParameter("testKey2", "testValue2")
        }
        keyUri = keyUri.buildUpon().apply {
            appendQueryParameter("_parameters", request.parameters!!.key)
        }.build()
        Assert.assertEquals(
            keyUri.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        // httpHeaders true
        request = request.newDownloadRequest {
            addHttpHeader("httpKey", "httpValue")
        }
        keyUri = keyUri.buildUpon().apply {
            appendQueryParameter("_httpHeaders", request.httpHeaders!!.toString())
        }.build()
        Assert.assertEquals(
            keyUri.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        // downloadCachePolicy true
        request = request.newDownloadRequest {
            downloadCachePolicy(WRITE_ONLY)
        }
        keyUri = keyUri.buildUpon().apply {
            appendQueryParameter("_downloadCachePolicy", request.downloadCachePolicy.toString())
        }.build()
        Assert.assertEquals(
            keyUri.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        // bitmapConfig
        request = request.newDownloadRequest {
            bitmapConfig(RGB_565)
        }
        keyUri = keyUri.buildUpon().build()
        Assert.assertEquals(
            keyUri.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            // colorSpace
            request = request.newDownloadRequest {
                colorSpace(ColorSpace.get(SRGB))
            }
            keyUri = keyUri.buildUpon().build()
            Assert.assertEquals(
                keyUri.toString().let { Uri.decode(it) },
                request.newKey().apply { keyHistoryList.add(this) }
            )
        }

        // preferQualityOverSpeed false
        request = request.newDownloadRequest {
            @Suppress("DEPRECATION")
            preferQualityOverSpeed(false)
        }
        keyUri = keyUri.buildUpon().build()
        Assert.assertEquals(
            keyUri.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        // preferQualityOverSpeed true
        request = request.newDownloadRequest {
            @Suppress("DEPRECATION")
            preferQualityOverSpeed(true)
        }
        keyUri = keyUri.buildUpon().build()
        Assert.assertEquals(
            keyUri.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        // resize
        request = request.newDownloadRequest {
            resize(200, 300)
        }
        keyUri = keyUri.buildUpon().build()
        Assert.assertEquals(
            keyUri.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        // transformations
        request = request.newDownloadRequest {
            transformations(CircleCropTransformation(), RotateTransformation(40))
        }
        keyUri = keyUri.buildUpon().build()
        Assert.assertEquals(
            keyUri.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        // bitmapDecodeInterceptors
        request = request.newDownloadRequest {
            components {
                addBitmapDecodeInterceptor(Test3BitmapDecodeInterceptor())
                addDrawableDecodeInterceptor(Test4DrawableDecodeInterceptor())
            }
        }
        keyUri = keyUri.buildUpon().build()
        Assert.assertEquals(
            keyUri.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        // ignoreExifOrientation false
        request = request.newDownloadRequest {
            ignoreExifOrientation(false)
        }
        keyUri = keyUri.buildUpon().build()
        Assert.assertEquals(
            keyUri.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        // ignoreExifOrientation true
        request = request.newDownloadRequest {
            ignoreExifOrientation(true)
        }
        keyUri = keyUri.buildUpon().build()
        Assert.assertEquals(
            keyUri.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        // resultCachePolicy true
        request = request.newDownloadRequest {
            resultCachePolicy(READ_ONLY)
        }
        keyUri = keyUri.buildUpon().build()
        Assert.assertEquals(
            keyUri.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        // disallowAnimatedImage false
        request = request.newDownloadRequest {
            disallowAnimatedImage(false)
        }
        keyUri = keyUri.buildUpon().build()
        Assert.assertEquals(
            keyUri.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        // disallowAnimatedImage true
        request = request.newDownloadRequest {
            disallowAnimatedImage(true)
        }
        keyUri = keyUri.buildUpon().build()
        Assert.assertEquals(
            keyUri.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        // memoryCachePolicy true
        request = request.newDownloadRequest {
            memoryCachePolicy(DISABLED)
        }
        keyUri = keyUri.buildUpon().build()
        Assert.assertEquals(
            keyUri.toString().let { Uri.decode(it) },
            request.newKey().apply { keyHistoryList.add(this) }
        )

        Assert.assertEquals(6, keyHistoryList.distinct().size)
    }
}