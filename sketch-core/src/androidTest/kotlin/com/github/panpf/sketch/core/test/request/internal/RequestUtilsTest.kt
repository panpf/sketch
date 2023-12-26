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
package com.github.panpf.sketch.core.test.request.internal

import android.R.color
import android.R.drawable
import android.graphics.Bitmap.Config.RGB_565
import android.graphics.ColorSpace
import android.graphics.ColorSpace.Named.ADOBE_RGB
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.cache.CachePolicy.READ_ONLY
import com.github.panpf.sketch.cache.CachePolicy.WRITE_ONLY
import com.github.panpf.sketch.request.Depth.LOCAL
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.internal.newCacheKey
import com.github.panpf.sketch.request.internal.newKey
import com.github.panpf.sketch.request.internal.newResizeKey
import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.Scale.END_CROP
import com.github.panpf.sketch.stateimage.DrawableStateImage
import com.github.panpf.sketch.stateimage.IconStateImage
import com.github.panpf.sketch.test.utils.TestBitmapDecodeInterceptor
import com.github.panpf.sketch.test.utils.TestBitmapDecoder
import com.github.panpf.sketch.test.utils.TestDownloadTarget
import com.github.panpf.sketch.test.utils.TestDrawableDecodeInterceptor
import com.github.panpf.sketch.test.utils.TestDrawableDecoder
import com.github.panpf.sketch.test.utils.TestFetcher
import com.github.panpf.sketch.test.utils.TestListenerImageView
import com.github.panpf.sketch.test.utils.TestLoadTarget
import com.github.panpf.sketch.test.utils.TestRequestInterceptor
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.transform.CircleCropTransformation
import com.github.panpf.sketch.transform.RotateTransformation
import com.github.panpf.sketch.transition.CrossfadeTransition
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RequestUtilsTest {

    @Test
    @Suppress("LocalVariableName")
    fun newCacheKeyWithDisplayRequest() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val uriString = "http://sample.com/sample.jpeg?from=sketch"

        val imageView = TestListenerImageView(context)
        var request = DisplayRequest(context, uriString)

        val verifyCacheKey: (String) -> Unit = { expectCacheKey ->
            val resizeSize = runBlocking { request.resizeSizeResolver.size() }
            val cacheKey = request.newCacheKey(resizeSize)
            Assert.assertEquals(expectCacheKey, cacheKey)
        }

        var resizeKey = request.newResizeKey(request.toRequestContext().resizeSize)
        var _resize = "&_resize=${resizeKey}"
        verifyCacheKey(
            uriString +
                    _resize
        )

        request = request.newDisplayRequest {
            listener(onStart = {})
        }
        verifyCacheKey(
            uriString +
                    _resize
        )

        request = request.newDisplayRequest {
            progressListener { _, _, _ -> }
        }
        verifyCacheKey(
            uriString +
                    _resize
        )

        request = request.newDisplayRequest {
            target(imageView)
        }
        verifyCacheKey(
            uriString +
                    _resize
        )

        request = request.newDisplayRequest {
            depth(LOCAL, "test")
        }
        verifyCacheKey(
            uriString +
                    _resize
        )

        request = request.newDisplayRequest {
            setParameter(key = "type", value = "list")
        }
        val _parameters = "&_parameters=${request.parameters!!.cacheKey}"
        verifyCacheKey(
            uriString + _parameters +
                    _resize
        )

        request = request.newDisplayRequest {
            setParameter(key = "big", value = "true", cacheKey = null)
        }
        verifyCacheKey(
            uriString + _parameters +
                    _resize
        )

        request = request.newDisplayRequest {
            setHttpHeader("from", "china")
        }
        verifyCacheKey(
            uriString + _parameters +
                    _resize
        )

        request = request.newDisplayRequest {
            downloadCachePolicy(READ_ONLY)
        }
        verifyCacheKey(
            uriString + _parameters +
                    _resize
        )

        request = request.newDisplayRequest {
            bitmapConfig(RGB_565)
        }
        val _bitmapConfig = "&_bitmapConfig=BitmapConfig(RGB_565)"
        verifyCacheKey(
            uriString + _parameters +
                    _bitmapConfig + _resize
        )

        val _colorSpace: String
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            request = request.newDisplayRequest {
                colorSpace(ColorSpace.get(ADOBE_RGB))
            }
            _colorSpace = "&_colorSpace=Adobe_RGB_(1998)"
            verifyCacheKey(
                uriString + _parameters +
                        _bitmapConfig + _colorSpace + _resize
            )
        } else {
            _colorSpace = ""
        }

        val _preferQualityOverSpeed: String
        if (VERSION.SDK_INT <= VERSION_CODES.M) {
            request = request.newDisplayRequest {
                @Suppress("DEPRECATION")
                preferQualityOverSpeed(true)
            }
            _preferQualityOverSpeed = "&_preferQualityOverSpeed=true"
            verifyCacheKey(
                uriString + _parameters +
                        _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize
            )
        } else {
            _preferQualityOverSpeed = ""
        }

        request = request.newDisplayRequest {
            resize(300, 200, EXACTLY, END_CROP)
        }
        resizeKey = request.newResizeKey(request.toRequestContext().resizeSize)
        _resize = "&_resize=${resizeKey}"
        verifyCacheKey(
            uriString + _parameters +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize
        )

        request = request.newDisplayRequest {
            transformations(CircleCropTransformation(), RotateTransformation(45))
        }
        val _transformations =
            request.transformations!!.joinToString(prefix = "[", postfix = "]", separator = ",") {
                it.key.replace("Transformation", "")
            }.let { "&_transformations=$it" }
        verifyCacheKey(
            uriString + _parameters +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize + _transformations
        )

        request = request.newDisplayRequest {
            disallowReuseBitmap(true)
        }
        verifyCacheKey(
            uriString + _parameters +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize + _transformations
        )

        request = request.newDisplayRequest {
            ignoreExifOrientation(true)
        }
        val _ignoreExifOrientation = "&_ignoreExifOrientation=true"
        verifyCacheKey(
            uriString + _parameters +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize + _transformations + _ignoreExifOrientation
        )

        request = request.newDisplayRequest {
            resultCachePolicy(WRITE_ONLY)
        }
        verifyCacheKey(
            uriString + _parameters +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize + _transformations + _ignoreExifOrientation
        )

        request = request.newDisplayRequest {
            placeholder(IconStateImage(drawable.ic_delete) {
                resColorBackground(color.background_dark)
            })
        }
        verifyCacheKey(
            uriString + _parameters +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize + _transformations + _ignoreExifOrientation
        )

        request = request.newDisplayRequest {
            error(DrawableStateImage(drawable.ic_delete))
        }
        verifyCacheKey(
            uriString + _parameters +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize + _transformations + _ignoreExifOrientation
        )

        request = request.newDisplayRequest {
            transitionFactory(CrossfadeTransition.Factory())
        }
        verifyCacheKey(
            uriString + _parameters +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize + _transformations + _ignoreExifOrientation
        )

        request = request.newDisplayRequest {
            disallowAnimatedImage(true)
        }
        val _disallowAnimatedImage = "&_disallowAnimatedImage=true"
        verifyCacheKey(
            uriString + _parameters +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize + _transformations + _ignoreExifOrientation +
                    _disallowAnimatedImage
        )

        request = request.newDisplayRequest {
            resizeApplyToDrawable(true)
        }
        verifyCacheKey(
            uriString + _parameters +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize + _transformations + _ignoreExifOrientation +
                    _disallowAnimatedImage
        )

        request = request.newDisplayRequest {
            memoryCachePolicy(WRITE_ONLY)
        }
        verifyCacheKey(
            uriString + _parameters +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize + _transformations + _ignoreExifOrientation +
                    _disallowAnimatedImage
        )

        request = request.newDisplayRequest {
            components {
                addFetcher(TestFetcher.Factory())
                addRequestInterceptor(TestRequestInterceptor())
                addDrawableDecodeInterceptor(TestDrawableDecodeInterceptor())
                addDrawableDecoder(TestDrawableDecoder.Factory())
                addBitmapDecodeInterceptor(TestBitmapDecodeInterceptor())
                addBitmapDecoder(TestBitmapDecoder.Factory())
            }
        }
        val _bitmapDecodeInterceptors = "&_bitmapDecodeInterceptors=[TestBitmapDecodeInterceptor]"
        val _drawableDecodeInterceptors =
            "&_drawableDecodeInterceptors=[TestDrawableDecodeInterceptor]"
        val _requestInterceptors = "&_requestInterceptors=[TestRequestInterceptor]"
        verifyCacheKey(
            uriString + _parameters +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize + _transformations + _ignoreExifOrientation +
                    _bitmapDecodeInterceptors + _disallowAnimatedImage + _drawableDecodeInterceptors + _requestInterceptors
        )
    }

    @Test
    @Suppress("LocalVariableName")
    fun newCacheKeyWithLoadRequest() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val uriString = "http://sample.com/sample.jpeg?from=sketch"

        var request = LoadRequest(context, uriString)

        val verifyCacheKey: (String) -> Unit = { expectCacheKey ->
            val resizeSize = runBlocking { request.resizeSizeResolver.size() }
            val cacheKey = request.newCacheKey(resizeSize)
            Assert.assertEquals(expectCacheKey, cacheKey)
        }

        var resizeKey = request.newResizeKey(request.toRequestContext().resizeSize)
        var _resize = "&_resize=${resizeKey}"
        verifyCacheKey(
            uriString +
                    _resize
        )

        request = request.newLoadRequest {
            listener(onStart = {})
        }
        verifyCacheKey(
            uriString +
                    _resize
        )

        request = request.newLoadRequest {
            progressListener { _, _, _ -> }
        }
        verifyCacheKey(
            uriString +
                    _resize
        )

        request = request.newLoadRequest {
            target(TestLoadTarget())
        }
        verifyCacheKey(
            uriString +
                    _resize
        )

        request = request.newLoadRequest {
            depth(LOCAL, "test")
        }
        verifyCacheKey(
            uriString +
                    _resize
        )

        request = request.newLoadRequest {
            setParameter(key = "type", value = "list")
        }
        val _parameters = "&_parameters=${request.parameters!!.cacheKey}"
        verifyCacheKey(
            uriString + _parameters +
                    _resize
        )

        request = request.newLoadRequest {
            setParameter(key = "big", value = "true", cacheKey = null)
        }
        verifyCacheKey(
            uriString + _parameters +
                    _resize
        )

        request = request.newLoadRequest {
            setHttpHeader("from", "china")
        }
        verifyCacheKey(
            uriString + _parameters +
                    _resize
        )

        request = request.newLoadRequest {
            downloadCachePolicy(READ_ONLY)
        }
        verifyCacheKey(
            uriString + _parameters +
                    _resize
        )

        request = request.newLoadRequest {
            bitmapConfig(RGB_565)
        }
        val _bitmapConfig = "&_bitmapConfig=BitmapConfig(RGB_565)"
        verifyCacheKey(
            uriString + _parameters +
                    _bitmapConfig + _resize
        )

        val _colorSpace: String
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            request = request.newLoadRequest {
                colorSpace(ColorSpace.get(ADOBE_RGB))
            }
            _colorSpace = "&_colorSpace=Adobe_RGB_(1998)"
            verifyCacheKey(
                uriString + _parameters +
                        _bitmapConfig + _colorSpace + _resize
            )
        } else {
            _colorSpace = ""
        }

        val _preferQualityOverSpeed: String
        if (VERSION.SDK_INT <= VERSION_CODES.M) {
            request = request.newLoadRequest {
                @Suppress("DEPRECATION")
                preferQualityOverSpeed(true)
            }
            _preferQualityOverSpeed = "&_preferQualityOverSpeed=true"
            verifyCacheKey(
                uriString + _parameters +
                        _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize
            )
        } else {
            _preferQualityOverSpeed = ""
        }

        request = request.newLoadRequest {
            resize(300, 200, EXACTLY, END_CROP)
        }
        resizeKey = request.newResizeKey(request.toRequestContext().resizeSize)
        _resize = "&_resize=${resizeKey}"
        verifyCacheKey(
            uriString + _parameters +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize
        )

        request = request.newLoadRequest {
            transformations(CircleCropTransformation(), RotateTransformation(45))
        }
        val _transformations =
            request.transformations!!.joinToString(prefix = "[", postfix = "]", separator = ",") {
                it.key.replace("Transformation", "")
            }.let { "&_transformations=$it" }
        verifyCacheKey(
            uriString + _parameters +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize + _transformations
        )

        request = request.newLoadRequest {
            disallowReuseBitmap(true)
        }
        verifyCacheKey(
            uriString + _parameters +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize + _transformations
        )

        request = request.newLoadRequest {
            ignoreExifOrientation(true)
        }
        val _ignoreExifOrientation = "&_ignoreExifOrientation=true"
        verifyCacheKey(
            uriString + _parameters +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize + _transformations + _ignoreExifOrientation
        )

        request = request.newLoadRequest {
            resultCachePolicy(WRITE_ONLY)
        }
        verifyCacheKey(
            uriString + _parameters +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize + _transformations + _ignoreExifOrientation
        )

        request = request.newLoadRequest {
            placeholder(IconStateImage(drawable.ic_delete) {
                resColorBackground(color.background_dark)
            })
        }
        verifyCacheKey(
            uriString + _parameters +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize + _transformations + _ignoreExifOrientation
        )

        request = request.newLoadRequest {
            error(DrawableStateImage(drawable.ic_delete))
        }
        verifyCacheKey(
            uriString + _parameters +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize + _transformations + _ignoreExifOrientation
        )

        request = request.newLoadRequest {
            transitionFactory(CrossfadeTransition.Factory())
        }
        verifyCacheKey(
            uriString + _parameters +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize + _transformations + _ignoreExifOrientation
        )

        request = request.newLoadRequest {
            disallowAnimatedImage(true)
        }
        val _disallowAnimatedImage = "&_disallowAnimatedImage=true"
        verifyCacheKey(
            uriString + _parameters +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize + _transformations + _ignoreExifOrientation +
                    _disallowAnimatedImage
        )

        request = request.newLoadRequest {
            resizeApplyToDrawable(true)
        }
        verifyCacheKey(
            uriString + _parameters +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize + _transformations + _ignoreExifOrientation +
                    _disallowAnimatedImage
        )

        request = request.newLoadRequest {
            memoryCachePolicy(WRITE_ONLY)
        }
        verifyCacheKey(
            uriString + _parameters +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize + _transformations + _ignoreExifOrientation +
                    _disallowAnimatedImage
        )

        request = request.newLoadRequest {
            components {
                addFetcher(TestFetcher.Factory())
                addRequestInterceptor(TestRequestInterceptor())
                addDrawableDecodeInterceptor(TestDrawableDecodeInterceptor())
                addDrawableDecoder(TestDrawableDecoder.Factory())
                addBitmapDecodeInterceptor(TestBitmapDecodeInterceptor())
                addBitmapDecoder(TestBitmapDecoder.Factory())
            }
        }
        val _bitmapDecodeInterceptors = "&_bitmapDecodeInterceptors=[TestBitmapDecodeInterceptor]"
        val _drawableDecodeInterceptors =
            "&_drawableDecodeInterceptors=[TestDrawableDecodeInterceptor]"
        val _requestInterceptors = "&_requestInterceptors=[TestRequestInterceptor]"
        verifyCacheKey(
            uriString + _parameters +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize + _transformations + _ignoreExifOrientation +
                    _bitmapDecodeInterceptors + _disallowAnimatedImage + _drawableDecodeInterceptors + _requestInterceptors
        )
    }

    @Test
    @Suppress("LocalVariableName")
    fun newCacheKeyWithDownloadRequest() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val uriString = "http://sample.com/sample.jpeg?from=sketch"

        var request = DownloadRequest(context, uriString)

        val verifyCacheKey: (String) -> Unit = { expectCacheKey ->
            val resizeSize = runBlocking { request.resizeSizeResolver.size() }
            val cacheKey = request.newCacheKey(resizeSize)
            Assert.assertEquals(expectCacheKey, cacheKey)
        }

        var resizeKey = request.newResizeKey(request.toRequestContext().resizeSize)
        var _resize = "&_resize=${resizeKey}"
        verifyCacheKey(
            uriString +
                    _resize
        )

        request = request.newDownloadRequest {
            listener(onStart = {})
        }
        verifyCacheKey(
            uriString +
                    _resize
        )

        request = request.newDownloadRequest {
            progressListener { _, _, _ -> }
        }
        verifyCacheKey(
            uriString +
                    _resize
        )

        request = request.newDownloadRequest {
            target(TestDownloadTarget())
        }
        verifyCacheKey(
            uriString +
                    _resize
        )

        request = request.newDownloadRequest {
            depth(LOCAL, "test")
        }
        verifyCacheKey(
            uriString +
                    _resize
        )

        request = request.newDownloadRequest {
            setParameter(key = "type", value = "list")
        }
        val _parameters = "&_parameters=${request.parameters!!.cacheKey}"
        verifyCacheKey(
            uriString + _parameters +
                    _resize
        )

        request = request.newDownloadRequest {
            setParameter(key = "big", value = "true", cacheKey = null)
        }
        verifyCacheKey(
            uriString + _parameters +
                    _resize
        )

        request = request.newDownloadRequest {
            setHttpHeader("from", "china")
        }
        verifyCacheKey(
            uriString + _parameters +
                    _resize
        )

        request = request.newDownloadRequest {
            downloadCachePolicy(READ_ONLY)
        }
        verifyCacheKey(
            uriString + _parameters +
                    _resize
        )

        request = request.newDownloadRequest {
            bitmapConfig(RGB_565)
        }
        val _bitmapConfig = "&_bitmapConfig=BitmapConfig(RGB_565)"
        verifyCacheKey(
            uriString + _parameters +
                    _bitmapConfig + _resize
        )

        val _colorSpace: String
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            request = request.newDownloadRequest {
                colorSpace(ColorSpace.get(ADOBE_RGB))
            }
            _colorSpace = "&_colorSpace=Adobe_RGB_(1998)"
            verifyCacheKey(
                uriString + _parameters +
                        _bitmapConfig + _colorSpace + _resize
            )
        } else {
            _colorSpace = ""
        }

        val _preferQualityOverSpeed: String
        if (VERSION.SDK_INT <= VERSION_CODES.M) {
            request = request.newDownloadRequest {
                @Suppress("DEPRECATION")
                preferQualityOverSpeed(true)
            }
            _preferQualityOverSpeed = "&_preferQualityOverSpeed=true"
            verifyCacheKey(
                uriString + _parameters +
                        _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize
            )
        } else {
            _preferQualityOverSpeed = ""
        }

        request = request.newDownloadRequest {
            resize(300, 200, EXACTLY, END_CROP)
        }
        resizeKey = request.newResizeKey(request.toRequestContext().resizeSize)
        _resize = "&_resize=${resizeKey}"
        verifyCacheKey(
            uriString + _parameters +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize
        )

        request = request.newDownloadRequest {
            transformations(CircleCropTransformation(), RotateTransformation(45))
        }
        val _transformations =
            request.transformations!!.joinToString(prefix = "[", postfix = "]", separator = ",") {
                it.key.replace("Transformation", "")
            }.let { "&_transformations=$it" }
        verifyCacheKey(
            uriString + _parameters +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize + _transformations
        )

        request = request.newDownloadRequest {
            disallowReuseBitmap(true)
        }
        verifyCacheKey(
            uriString + _parameters +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize + _transformations
        )

        request = request.newDownloadRequest {
            ignoreExifOrientation(true)
        }
        val _ignoreExifOrientation = "&_ignoreExifOrientation=true"
        verifyCacheKey(
            uriString + _parameters +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize + _transformations + _ignoreExifOrientation
        )

        request = request.newDownloadRequest {
            resultCachePolicy(WRITE_ONLY)
        }
        verifyCacheKey(
            uriString + _parameters +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize + _transformations + _ignoreExifOrientation
        )

        request = request.newDownloadRequest {
            placeholder(IconStateImage(drawable.ic_delete) {
                resColorBackground(color.background_dark)
            })
        }
        verifyCacheKey(
            uriString + _parameters +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize + _transformations + _ignoreExifOrientation
        )

        request = request.newDownloadRequest {
            error(DrawableStateImage(drawable.ic_delete))
        }
        verifyCacheKey(
            uriString + _parameters +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize + _transformations + _ignoreExifOrientation
        )

        request = request.newDownloadRequest {
            transitionFactory(CrossfadeTransition.Factory())
        }
        verifyCacheKey(
            uriString + _parameters +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize + _transformations + _ignoreExifOrientation
        )

        request = request.newDownloadRequest {
            disallowAnimatedImage(true)
        }
        val _disallowAnimatedImage = "&_disallowAnimatedImage=true"
        verifyCacheKey(
            uriString + _parameters +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize + _transformations + _ignoreExifOrientation +
                    _disallowAnimatedImage
        )

        request = request.newDownloadRequest {
            resizeApplyToDrawable(true)
        }
        verifyCacheKey(
            uriString + _parameters +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize + _transformations + _ignoreExifOrientation +
                    _disallowAnimatedImage
        )

        request = request.newDownloadRequest {
            memoryCachePolicy(WRITE_ONLY)
        }
        verifyCacheKey(
            uriString + _parameters +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize + _transformations + _ignoreExifOrientation +
                    _disallowAnimatedImage
        )

        request = request.newDownloadRequest {
            components {
                addFetcher(TestFetcher.Factory())
                addRequestInterceptor(TestRequestInterceptor())
                addDrawableDecodeInterceptor(TestDrawableDecodeInterceptor())
                addDrawableDecoder(TestDrawableDecoder.Factory())
                addBitmapDecodeInterceptor(TestBitmapDecodeInterceptor())
                addBitmapDecoder(TestBitmapDecoder.Factory())
            }
        }
        val _bitmapDecodeInterceptors = "&_bitmapDecodeInterceptors=[TestBitmapDecodeInterceptor]"
        val _drawableDecodeInterceptors =
            "&_drawableDecodeInterceptors=[TestDrawableDecodeInterceptor]"
        val _requestInterceptors = "&_requestInterceptors=[TestRequestInterceptor]"
        verifyCacheKey(
            uriString + _parameters +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize + _transformations + _ignoreExifOrientation +
                    _bitmapDecodeInterceptors + _disallowAnimatedImage + _drawableDecodeInterceptors + _requestInterceptors
        )
    }

    @Test
    @Suppress("LocalVariableName")
    fun newKeyWithDisplayRequest() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val uriString = "http://sample.com/sample.jpeg?from=sketch"

        val imageView = TestListenerImageView(context)
        var request = DisplayRequest(context, uriString)

        val verifyCacheKey: (String) -> Unit = { expectKey ->
            val resizeSize = runBlocking { request.resizeSizeResolver.size() }
            val key = request.newKey(resizeSize)
            Assert.assertEquals(expectKey, key)
        }

        var resizeKey = request.newResizeKey(request.toRequestContext().resizeSize)
        var _resize = "&_resize=${resizeKey}"
        verifyCacheKey(
            uriString +
                    _resize
        )

        request = request.newDisplayRequest {
            listener(onStart = {})
        }
        verifyCacheKey(
            uriString +
                    _resize
        )

        request = request.newDisplayRequest {
            progressListener { _, _, _ -> }
        }
        verifyCacheKey(
            uriString +
                    _resize
        )

        request = request.newDisplayRequest {
            target(imageView)
        }
        verifyCacheKey(
            uriString +
                    _resize
        )

        request = request.newDisplayRequest {
            depth(LOCAL, "test")
        }
        val _depth = "&_depth=LOCAL"
        var _parameters = "&_parameters=${request.parameters!!.key}"
        verifyCacheKey(
            uriString + _depth + _parameters +
                    _resize
        )

        request = request.newDisplayRequest {
            setParameter(key = "type", value = "list")
        }
        _parameters = "&_parameters=${request.parameters!!.key}"
        verifyCacheKey(
            uriString + _depth + _parameters +
                    _resize
        )

        request = request.newDisplayRequest {
            setParameter(key = "big", value = "true", cacheKey = null)
        }
        _parameters = "&_parameters=${request.parameters!!.key}"
        verifyCacheKey(
            uriString + _depth + _parameters +
                    _resize
        )

        request = request.newDisplayRequest {
            setHttpHeader("from", "china")
        }
        val _httpHeaders = "&_httpHeaders=${request.httpHeaders!!}"
        verifyCacheKey(
            uriString + _depth + _parameters + _httpHeaders +
                    _resize
        )

        request = request.newDisplayRequest {
            downloadCachePolicy(READ_ONLY)
        }
        val _downloadCachePolicy = "&_downloadCachePolicy=READ_ONLY"
        verifyCacheKey(
            uriString + _depth + _parameters + _httpHeaders + _downloadCachePolicy +
                    _resize
        )

        request = request.newDisplayRequest {
            bitmapConfig(RGB_565)
        }
        val _bitmapConfig = "&_bitmapConfig=BitmapConfig(RGB_565)"
        verifyCacheKey(
            uriString + _depth + _parameters + _httpHeaders + _downloadCachePolicy +
                    _bitmapConfig + _resize
        )

        val _colorSpace: String
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            request = request.newDisplayRequest {
                colorSpace(ColorSpace.get(ADOBE_RGB))
            }
            _colorSpace = "&_colorSpace=Adobe_RGB_(1998)"
            verifyCacheKey(
                uriString + _depth + _parameters + _httpHeaders + _downloadCachePolicy +
                        _bitmapConfig + _colorSpace + _resize
            )
        } else {
            _colorSpace = ""
        }

        val _preferQualityOverSpeed: String
        if (VERSION.SDK_INT <= VERSION_CODES.M) {
            request = request.newDisplayRequest {
                @Suppress("DEPRECATION")
                preferQualityOverSpeed(true)
            }
            _preferQualityOverSpeed = "&_preferQualityOverSpeed=true"
            verifyCacheKey(
                uriString + _depth + _parameters + _httpHeaders + _downloadCachePolicy +
                        _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize
            )
        } else {
            _preferQualityOverSpeed = ""
        }

        request = request.newDisplayRequest {
            resize(300, 200, EXACTLY, END_CROP)
        }
        resizeKey = request.newResizeKey(request.toRequestContext().resizeSize)
        _resize = "&_resize=${resizeKey}"
        verifyCacheKey(
            uriString + _depth + _parameters + _httpHeaders + _downloadCachePolicy +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize
        )

        request = request.newDisplayRequest {
            transformations(CircleCropTransformation(), RotateTransformation(45))
        }
        val _transformations =
            request.transformations!!.joinToString(prefix = "[", postfix = "]", separator = ",") {
                it.key.replace("Transformation", "")
            }.let { "&_transformations=$it" }
        verifyCacheKey(
            uriString + _depth + _parameters + _httpHeaders + _downloadCachePolicy +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize + _transformations
        )

        request = request.newDisplayRequest {
            disallowReuseBitmap(true)
        }
        val _disallowReuseBitmap = "&_disallowReuseBitmap=true"
        verifyCacheKey(
            uriString + _depth + _parameters + _httpHeaders + _downloadCachePolicy +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize +
                    _transformations + _disallowReuseBitmap
        )

        request = request.newDisplayRequest {
            ignoreExifOrientation(true)
        }
        val _ignoreExifOrientation = "&_ignoreExifOrientation=true"
        verifyCacheKey(
            uriString + _depth + _parameters + _httpHeaders + _downloadCachePolicy +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize +
                    _transformations + _disallowReuseBitmap + _ignoreExifOrientation
        )

        request = request.newDisplayRequest {
            resultCachePolicy(WRITE_ONLY)
        }
        val _resultCachePolicy = "&_resultCachePolicy=WRITE_ONLY"
        verifyCacheKey(
            uriString + _depth + _parameters + _httpHeaders + _downloadCachePolicy +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize +
                    _transformations + _disallowReuseBitmap + _ignoreExifOrientation +
                    _resultCachePolicy
        )

        request = request.newDisplayRequest {
            placeholder(IconStateImage(drawable.ic_delete) {
                resColorBackground(color.background_dark)
            })
        }
        verifyCacheKey(
            uriString + _depth + _parameters + _httpHeaders + _downloadCachePolicy +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize +
                    _transformations + _disallowReuseBitmap + _ignoreExifOrientation +
                    _resultCachePolicy
        )

        request = request.newDisplayRequest {
            error(DrawableStateImage(drawable.ic_delete))
        }
        verifyCacheKey(
            uriString + _depth + _parameters + _httpHeaders + _downloadCachePolicy +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize +
                    _transformations + _disallowReuseBitmap + _ignoreExifOrientation +
                    _resultCachePolicy
        )

        request = request.newDisplayRequest {
            transitionFactory(CrossfadeTransition.Factory())
        }
        verifyCacheKey(
            uriString + _depth + _parameters + _httpHeaders + _downloadCachePolicy +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize +
                    _transformations + _disallowReuseBitmap + _ignoreExifOrientation +
                    _resultCachePolicy
        )

        request = request.newDisplayRequest {
            disallowAnimatedImage(true)
        }
        val _disallowAnimatedImage = "&_disallowAnimatedImage=true"
        verifyCacheKey(
            uriString + _depth + _parameters + _httpHeaders + _downloadCachePolicy +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize +
                    _transformations + _disallowReuseBitmap + _ignoreExifOrientation +
                    _resultCachePolicy + _disallowAnimatedImage
        )

        request = request.newDisplayRequest {
            resizeApplyToDrawable(true)
        }
        verifyCacheKey(
            uriString + _depth + _parameters + _httpHeaders + _downloadCachePolicy +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize +
                    _transformations + _disallowReuseBitmap + _ignoreExifOrientation +
                    _resultCachePolicy + _disallowAnimatedImage
        )

        request = request.newDisplayRequest {
            memoryCachePolicy(WRITE_ONLY)
        }
        val _memoryCachePolicy = "&_memoryCachePolicy=WRITE_ONLY"
        verifyCacheKey(
            uriString + _depth + _parameters + _httpHeaders + _downloadCachePolicy +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize +
                    _transformations + _disallowReuseBitmap + _ignoreExifOrientation +
                    _resultCachePolicy + _disallowAnimatedImage + _memoryCachePolicy
        )

        request = request.newDisplayRequest {
            components {
                addFetcher(TestFetcher.Factory())
                addRequestInterceptor(TestRequestInterceptor())
                addDrawableDecodeInterceptor(TestDrawableDecodeInterceptor())
                addDrawableDecoder(TestDrawableDecoder.Factory())
                addBitmapDecodeInterceptor(TestBitmapDecodeInterceptor())
                addBitmapDecoder(TestBitmapDecoder.Factory())
            }
        }
        val _bitmapDecodeInterceptors = "&_bitmapDecodeInterceptors=[TestBitmapDecodeInterceptor]"
        val _drawableDecodeInterceptors =
            "&_drawableDecodeInterceptors=[TestDrawableDecodeInterceptor]"
        val _requestInterceptors = "&_requestInterceptors=[TestRequestInterceptor]"
        verifyCacheKey(
            uriString + _depth + _parameters + _httpHeaders + _downloadCachePolicy +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize +
                    _transformations + _disallowReuseBitmap + _ignoreExifOrientation +
                    _resultCachePolicy + _bitmapDecodeInterceptors + _disallowAnimatedImage +
                    _memoryCachePolicy + _drawableDecodeInterceptors + _requestInterceptors
        )
    }
}