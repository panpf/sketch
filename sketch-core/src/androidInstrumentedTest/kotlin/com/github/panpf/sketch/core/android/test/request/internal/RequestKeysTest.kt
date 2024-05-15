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
@file:Suppress("LocalVariableName")

package com.github.panpf.sketch.core.android.test.request.internal

import android.R.color
import android.R.drawable
import android.graphics.Bitmap.Config.RGB_565
import android.graphics.ColorSpace.Named.ADOBE_RGB
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.cache.CachePolicy.READ_ONLY
import com.github.panpf.sketch.cache.CachePolicy.WRITE_ONLY
import com.github.panpf.sketch.request.Depth.LOCAL
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.bitmapConfig
import com.github.panpf.sketch.request.colorSpace
import com.github.panpf.sketch.request.internal.newCacheKey
import com.github.panpf.sketch.request.internal.newKey
import com.github.panpf.sketch.request.preferQualityOverSpeed
import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.Scale.END_CROP
import com.github.panpf.sketch.state.DrawableStateImage
import com.github.panpf.sketch.state.IconStateImage
import com.github.panpf.sketch.test.utils.TestDecodeInterceptor
import com.github.panpf.sketch.test.utils.TestDecoder
import com.github.panpf.sketch.test.utils.TestFetcher
import com.github.panpf.sketch.test.utils.TestRequestInterceptor
import com.github.panpf.sketch.transform.CircleCropTransformation
import com.github.panpf.sketch.transform.CrossfadeTransition
import com.github.panpf.sketch.transform.RotateTransformation
import com.github.panpf.sketch.transition.ViewCrossfadeTransition
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RequestKeysTest {

    @Test
    fun newRequestKey() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val uriString = "http://sample.com/sample.jpeg?from=sketch"

        var request = ImageRequest(context, uriString)

        val verifyKey: (String) -> Unit = { expectKey ->
            val key = request.newKey()
            Assert.assertEquals(expectKey, key)
        }

        var _size = "&_size=${request.sizeResolver.key}"
        var _precision = "&_precision=${request.precisionDecider.key}"
        var _scale = "&_scale=${request.scaleDecider.key}"
        verifyKey(uriString + _size + _precision + _scale)

        request = request.newRequest {
            registerListener(onStart = {})
        }
        verifyKey(uriString + _size + _precision + _scale)

        request = request.newRequest {
            registerProgressListener { _, _ -> }
        }
        verifyKey(uriString + _size + _precision + _scale)

        request = request.newRequest {
            depth(LOCAL, "test")
        }
        val _depth = "&_depth=LOCAL"
        var _parameters = "&_parameters=${request.parameters!!.requestKey}"
        verifyKey(uriString + _depth + _parameters + _size + _precision + _scale)

        request = request.newRequest {
            setParameter(key = "type", value = "list")
        }
        _parameters = "&_parameters=${request.parameters!!.requestKey}"
        verifyKey(uriString + _depth + _parameters + _size + _precision + _scale)

        request = request.newRequest {
            setParameter(key = "big", value = "true", cacheKey = null)
        }
        _parameters = "&_parameters=${request.parameters!!.requestKey}"
        verifyKey(uriString + _depth + _parameters + _size + _precision + _scale)

        request = request.newRequest {
            setHttpHeader("from", "china")
        }
        val _httpHeaders = "&_httpHeaders=${request.httpHeaders!!}"
        verifyKey(uriString + _depth + _parameters + _httpHeaders + _size + _precision + _scale)

        request = request.newRequest {
            downloadCachePolicy(READ_ONLY)
        }
        val _downloadCachePolicy = "&_downloadCachePolicy=READ_ONLY"
        verifyKey(uriString + _depth + _parameters + _httpHeaders + _downloadCachePolicy + _size + _precision + _scale)

        request = request.newRequest {
            bitmapConfig(RGB_565)
        }
        _parameters = "&_parameters=${request.parameters!!.requestKey}"
        verifyKey(
            uriString + _depth + _parameters + _httpHeaders + _downloadCachePolicy + _size + _precision + _scale
        )

        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            request = request.newRequest {
                colorSpace(ADOBE_RGB)
            }
            _parameters = "&_parameters=${request.parameters!!.requestKey}"
            verifyKey(
                uriString + _depth + _parameters + _httpHeaders + _downloadCachePolicy + _size + _precision + _scale
            )
        }

        if (VERSION.SDK_INT <= VERSION_CODES.M) {
            request = request.newRequest {
                @Suppress("DEPRECATION")
                preferQualityOverSpeed(true)
            }
            _parameters = "&_parameters=${request.parameters!!.requestKey}"
            verifyKey(
                uriString + _depth + _parameters + _httpHeaders + _downloadCachePolicy + _size + _precision + _scale
            )
        }

        request = request.newRequest {
            resize(300, 200, EXACTLY, END_CROP)
        }
        _size = "&_size=${request.sizeResolver.key}"
        _precision = "&_precision=${request.precisionDecider.key}"
        _scale = "&_scale=${request.scaleDecider.key}"
        verifyKey(
            uriString + _depth + _parameters + _httpHeaders + _downloadCachePolicy +
                    _size + _precision + _scale
        )

        request = request.newRequest {
            sizeMultiplier(1.5f)
        }
        val _sizeMultiplier = "&_sizeMultiplier=${request.sizeMultiplier}"
        verifyKey(
            uriString + _depth + _parameters + _httpHeaders + _downloadCachePolicy +
                    _size + _sizeMultiplier + _precision + _scale
        )

        request = request.newRequest {
            transformations(CircleCropTransformation(), RotateTransformation(45))
        }
        val _transformations =
            request.transformations!!.joinToString(prefix = "[", postfix = "]", separator = ",") {
                it.key.replace("Transformation", "")
            }.let { "&_transformations=$it" }
        verifyKey(
            uriString + _depth + _parameters + _httpHeaders + _downloadCachePolicy +
                    _size + _sizeMultiplier + _precision + _scale + _transformations
        )

        request = request.newRequest {
            resultCachePolicy(WRITE_ONLY)
        }
        val _resultCachePolicy = "&_resultCachePolicy=WRITE_ONLY"
        verifyKey(
            uriString + _depth + _parameters + _httpHeaders + _downloadCachePolicy +
                    _size + _sizeMultiplier + _precision + _scale +
                    _transformations + _resultCachePolicy
        )

        request = request.newRequest {
            placeholder(
                IconStateImage(
                    icon = drawable.ic_delete,
                    background = color.background_dark
                )
            )
        }
        verifyKey(
            uriString + _depth + _parameters + _httpHeaders + _downloadCachePolicy +
                    _size + _sizeMultiplier + _precision + _scale +
                    _transformations + _resultCachePolicy
        )

        request = request.newRequest {
            error(DrawableStateImage(drawable.ic_delete))
        }
        verifyKey(
            uriString + _depth + _parameters + _httpHeaders + _downloadCachePolicy +
                    _size + _sizeMultiplier + _precision + _scale +
                    _transformations + _resultCachePolicy
        )

        request = request.newRequest {
            disallowAnimatedImage(true)
        }
        val _disallowAnimatedImage = "&_disallowAnimatedImage=true"
        verifyKey(
            uriString + _depth + _parameters + _httpHeaders + _downloadCachePolicy +
                    _size + _sizeMultiplier + _precision + _scale +
                    _transformations + _resultCachePolicy + _disallowAnimatedImage
        )

        request = request.newRequest {
            resizeOnDraw(true)
        }
        val _resizeOnDraw = "&_resizeOnDraw=true"
        verifyKey(
            uriString + _depth + _parameters + _httpHeaders + _downloadCachePolicy +
                    _size + _sizeMultiplier + _precision + _scale +
                    _transformations + _resultCachePolicy + _disallowAnimatedImage + _resizeOnDraw
        )

        request = request.newRequest {
            memoryCachePolicy(WRITE_ONLY)
        }
        val _memoryCachePolicy = "&_memoryCachePolicy=WRITE_ONLY"
        verifyKey(
            uriString + _depth + _parameters + _httpHeaders + _downloadCachePolicy +
                    _size + _sizeMultiplier + _precision + _scale +
                    _transformations + _resultCachePolicy + _disallowAnimatedImage + _resizeOnDraw +
                    _memoryCachePolicy
        )

        request = request.newRequest {
            transitionFactory(CrossfadeTransition.Factory())
        }
        val _transitionFactory = "&_transitionFactory=${CrossfadeTransition.Factory().key}"
        verifyKey(
            uriString + _depth + _parameters + _httpHeaders + _downloadCachePolicy +
                    _size + _sizeMultiplier + _precision + _scale +
                    _transformations + _resultCachePolicy + _disallowAnimatedImage + _resizeOnDraw +
                    _memoryCachePolicy + _transitionFactory
        )

        request = request.newRequest {
            components {
                addFetcher(TestFetcher.Factory())
                addRequestInterceptor(TestRequestInterceptor())
                addDecodeInterceptor(TestDecodeInterceptor())
                addDecoder(TestDecoder.Factory())
            }
        }
        val _decoders = "&_decoders=[TestDecoder]"
        val _decodeInterceptors = "&_decodeInterceptors=[TestDecodeInterceptor]"
        val _requestInterceptors = "&_requestInterceptors=[TestRequestInterceptor]"
        verifyKey(
            uriString + _depth + _parameters + _httpHeaders + _downloadCachePolicy +
                    _size + _sizeMultiplier + _precision + _scale +
                    _transformations + _resultCachePolicy + _disallowAnimatedImage + _resizeOnDraw +
                    _memoryCachePolicy + _transitionFactory + _decoders + _decodeInterceptors + _requestInterceptors
        )
    }

    @Test
    fun newCacheKey() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val uriString = "http://sample.com/sample.jpeg?from=sketch"

        var request = ImageRequest(context, uriString)

        val verifyCacheKey: (String) -> Unit = { expectCacheKey ->
            val resizeSize = runBlocking { request.sizeResolver.size() }
            val cacheKey = request.newCacheKey(resizeSize)
            Assert.assertEquals(expectCacheKey, cacheKey)
        }

        var resizeSize = runBlocking { request.sizeResolver.size() }
        var _size = "&_size=${resizeSize}"
        var _precision = "&_precision=${request.precisionDecider.key}"
        var _scale = "&_scale=${request.scaleDecider.key}"
        verifyCacheKey(uriString + _size + _precision + _scale)

        request = request.newRequest {
            registerListener(onStart = {})
        }
        verifyCacheKey(uriString + _size + _precision + _scale)

        request = request.newRequest {
            registerProgressListener { _, _ -> }
        }
        verifyCacheKey(uriString + _size + _precision + _scale)

        request = request.newRequest {
            depth(LOCAL, "test")
        }
        verifyCacheKey(uriString + _size + _precision + _scale)

        request = request.newRequest {
            setParameter(key = "type", value = "list")
        }
        var _parameters = "&_parameters=${request.parameters!!.cacheKey}"
        verifyCacheKey(
            uriString + _parameters +
                    _size + _precision + _scale
        )

        request = request.newRequest {
            setParameter(key = "big", value = "true", cacheKey = null)
        }
        verifyCacheKey(
            uriString + _parameters +
                    _size + _precision + _scale
        )

        request = request.newRequest {
            setHttpHeader("from", "china")
        }
        verifyCacheKey(
            uriString + _parameters +
                    _size + _precision + _scale
        )

        request = request.newRequest {
            downloadCachePolicy(READ_ONLY)
        }
        verifyCacheKey(
            uriString + _parameters +
                    _size + _precision + _scale
        )

        request = request.newRequest {
            bitmapConfig(RGB_565)
        }
        _parameters = "&_parameters=${request.parameters!!.cacheKey}"
        verifyCacheKey(
            uriString + _parameters + _size + _precision + _scale
        )

        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            request = request.newRequest {
                colorSpace(ADOBE_RGB)
            }
            _parameters = "&_parameters=${request.parameters!!.cacheKey}"
            verifyCacheKey(
                uriString + _parameters + _size + _precision + _scale
            )
        }

        if (VERSION.SDK_INT <= VERSION_CODES.M) {
            request = request.newRequest {
                @Suppress("DEPRECATION")
                preferQualityOverSpeed(true)
            }
            _parameters = "&_parameters=${request.parameters!!.cacheKey}"
            verifyCacheKey(
                uriString + _parameters + _size + _precision + _scale
            )
        }

        request = request.newRequest {
            resize(300, 200, EXACTLY, END_CROP)
        }
        resizeSize = runBlocking { request.sizeResolver.size() }
        _size = "&_size=${resizeSize}"
        _precision = "&_precision=${request.precisionDecider.key}"
        _scale = "&_scale=${request.scaleDecider.key}"
        verifyCacheKey(
            uriString + _parameters +
                    _size + _precision + _scale
        )

        request = request.newRequest {
            sizeMultiplier(1.5f)
        }
        val _sizeMultiplier = "&_sizeMultiplier=${request.sizeMultiplier}"
        verifyCacheKey(
            uriString + _parameters +
                    _size + _sizeMultiplier + _precision + _scale
        )

        request = request.newRequest {
            transformations(CircleCropTransformation(), RotateTransformation(45))
        }
        val _transformations =
            request.transformations!!.joinToString(prefix = "[", postfix = "]", separator = ",") {
                it.key.replace("Transformation", "")
            }.let { "&_transformations=$it" }
        verifyCacheKey(
            uriString + _parameters +
                    _size + _sizeMultiplier + _precision + _scale + _transformations
        )

        request = request.newRequest {
            resultCachePolicy(WRITE_ONLY)
        }
        verifyCacheKey(
            uriString + _parameters +
                    _size + _sizeMultiplier + _precision + _scale + _transformations
        )

        request = request.newRequest {
            placeholder(
                IconStateImage(
                    icon = drawable.ic_delete,
                    background = color.background_dark
                )
            )
        }
        verifyCacheKey(
            uriString + _parameters +
                    _size + _sizeMultiplier + _precision + _scale + _transformations
        )

        request = request.newRequest {
            error(DrawableStateImage(drawable.ic_delete))
        }
        verifyCacheKey(
            uriString + _parameters +
                    _size + _sizeMultiplier + _precision + _scale + _transformations
        )

        request = request.newRequest {
            transitionFactory(ViewCrossfadeTransition.Factory())
        }
        verifyCacheKey(
            uriString + _parameters +
                    _size + _sizeMultiplier + _precision + _scale + _transformations
        )

        request = request.newRequest {
            disallowAnimatedImage(true)
        }
        val _disallowAnimatedImage = "&_disallowAnimatedImage=true"
        verifyCacheKey(
            uriString + _parameters +
                    _size + _sizeMultiplier + _precision + _scale + _transformations + _disallowAnimatedImage
        )

        request = request.newRequest {
            resizeOnDraw(true)
        }
        verifyCacheKey(
            uriString + _parameters +
                    _size + _sizeMultiplier + _precision + _scale + _transformations + _disallowAnimatedImage
        )

        request = request.newRequest {
            memoryCachePolicy(WRITE_ONLY)
        }
        verifyCacheKey(
            uriString + _parameters +
                    _size + _sizeMultiplier + _precision + _scale + _transformations + _disallowAnimatedImage
        )

        request = request.newRequest {
            components {
                addFetcher(TestFetcher.Factory())
                addRequestInterceptor(TestRequestInterceptor())
                addDecodeInterceptor(TestDecodeInterceptor())
                addDecoder(TestDecoder.Factory())
            }
        }
        val _decoders = "&_decoders=[TestDecoder]"
        val _decodeInterceptors = "&_decodeInterceptors=[TestDecodeInterceptor]"
        val _requestInterceptors = "&_requestInterceptors=[TestRequestInterceptor]"
        verifyCacheKey(
            uriString + _parameters +
                    _size + _sizeMultiplier + _precision + _scale + _transformations + _disallowAnimatedImage + _decoders + _decodeInterceptors + _requestInterceptors
        )
    }
}