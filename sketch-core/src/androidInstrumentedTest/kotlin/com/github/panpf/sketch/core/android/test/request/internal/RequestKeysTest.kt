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
import android.graphics.ColorSpace
import android.graphics.ColorSpace.Named.ADOBE_RGB
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.cache.CachePolicy.READ_ONLY
import com.github.panpf.sketch.cache.CachePolicy.WRITE_ONLY
import com.github.panpf.sketch.request.Depth.LOCAL
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.newCacheKey
import com.github.panpf.sketch.request.internal.newKey
import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.Scale.END_CROP
import com.github.panpf.sketch.state.DrawableStateImage
import com.github.panpf.sketch.state.IconStateImage
import com.github.panpf.sketch.test.utils.TestDecodeInterceptor
import com.github.panpf.sketch.test.utils.TestDecoder
import com.github.panpf.sketch.test.utils.TestFetcher
import com.github.panpf.sketch.test.utils.TestListenerImageView
import com.github.panpf.sketch.test.utils.TestRequestInterceptor
import com.github.panpf.sketch.transform.CircleCropTransformation
import com.github.panpf.sketch.transform.RotateTransformation
import com.github.panpf.sketch.transition.CrossfadeTransition
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RequestKeysTest {

    @Test
    fun newCacheKey() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val uriString = "http://sample.com/sample.jpeg?from=sketch"

        val imageView = TestListenerImageView(context)
        var request = ImageRequest(context, uriString)

        val verifyCacheKey: (String) -> Unit = { expectCacheKey ->
            val resizeSize = runBlocking { request.resizeSizeResolver.size() }
            val cacheKey = request.newCacheKey(resizeSize)
            Assert.assertEquals(expectCacheKey, cacheKey)
        }

        var resizeSize = runBlocking { request.resizeSizeResolver.size() }
        var _resize =
            "&_size=${resizeSize}&_precision=${request.resizePrecisionDecider.key}&_scale=${request.resizeScaleDecider.key}"
        verifyCacheKey(uriString + _resize)

        request = request.newRequest {
            listener(onStart = {})
        }
        verifyCacheKey(uriString + _resize)

        request = request.newRequest {
            progressListener { _, _ -> }
        }
        verifyCacheKey(uriString + _resize)

        request = request.newRequest {
            target(imageView)
        }
        verifyCacheKey(uriString + _resize)

        request = request.newRequest {
            depth(LOCAL, "test")
        }
        verifyCacheKey(uriString + _resize)

        request = request.newRequest {
            setParameter(key = "type", value = "list")
        }
        val _parameters = "&_parameters=${request.parameters!!.cacheKey}"
        verifyCacheKey(
            uriString + _parameters +
                    _resize
        )

        request = request.newRequest {
            setParameter(key = "big", value = "true", cacheKey = null)
        }
        verifyCacheKey(
            uriString + _parameters +
                    _resize
        )

        request = request.newRequest {
            setHttpHeader("from", "china")
        }
        verifyCacheKey(
            uriString + _parameters +
                    _resize
        )

        request = request.newRequest {
            downloadCachePolicy(READ_ONLY)
        }
        verifyCacheKey(
            uriString + _parameters +
                    _resize
        )

        request = request.newRequest {
            bitmapConfig(RGB_565)
        }
        val _bitmapConfig = "&_bitmapConfig=BitmapConfig(RGB_565)"
        verifyCacheKey(
            uriString + _parameters +
                    _bitmapConfig + _resize
        )

        val _colorSpace: String
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            request = request.newRequest {
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
            request = request.newRequest {
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

        request = request.newRequest {
            resize(300, 200, EXACTLY, END_CROP)
        }
        resizeSize = runBlocking { request.resizeSizeResolver.size() }
        _resize =
            "&_size=${resizeSize}&_precision=${request.resizePrecisionDecider.key}&_scale=${request.resizeScaleDecider.key}"
        verifyCacheKey(
            uriString + _parameters +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize
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
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize + _transformations
        )

        request = request.newRequest {
            disallowReuseBitmap(true)
        }
        verifyCacheKey(
            uriString + _parameters +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize + _transformations
        )

        request = request.newRequest {
            ignoreExifOrientation(true)
        }
        val _ignoreExifOrientation = "&_ignoreExifOrientation=true"
        verifyCacheKey(
            uriString + _parameters +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize + _transformations + _ignoreExifOrientation
        )

        request = request.newRequest {
            resultCachePolicy(WRITE_ONLY)
        }
        verifyCacheKey(
            uriString + _parameters +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize + _transformations + _ignoreExifOrientation
        )

        request = request.newRequest {
            placeholder(IconStateImage(drawable.ic_delete) {
                resColorBackground(color.background_dark)
            })
        }
        verifyCacheKey(
            uriString + _parameters +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize + _transformations + _ignoreExifOrientation
        )

        request = request.newRequest {
            error(DrawableStateImage(drawable.ic_delete))
        }
        verifyCacheKey(
            uriString + _parameters +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize + _transformations + _ignoreExifOrientation
        )

        request = request.newRequest {
            transitionFactory(CrossfadeTransition.Factory())
        }
        verifyCacheKey(
            uriString + _parameters +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize + _transformations + _ignoreExifOrientation
        )

        request = request.newRequest {
            disallowAnimatedImage(true)
        }
        val _disallowAnimatedImage = "&_disallowAnimatedImage=true"
        verifyCacheKey(
            uriString + _parameters +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize + _transformations + _ignoreExifOrientation +
                    _disallowAnimatedImage
        )

        request = request.newRequest {
            resizeApplyToDrawable(true)
        }
        verifyCacheKey(
            uriString + _parameters +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize + _transformations + _ignoreExifOrientation +
                    _disallowAnimatedImage
        )

        request = request.newRequest {
            memoryCachePolicy(WRITE_ONLY)
        }
        verifyCacheKey(
            uriString + _parameters +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize + _transformations + _ignoreExifOrientation +
                    _disallowAnimatedImage
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
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize + _transformations + _ignoreExifOrientation +
                    _disallowAnimatedImage + _decoders + _decodeInterceptors + _requestInterceptors
        )
    }

    @Test
    fun newKey() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val uriString = "http://sample.com/sample.jpeg?from=sketch"

        val imageView = TestListenerImageView(context)
        var request = ImageRequest(context, uriString)

        val verifyKey: (String) -> Unit = { expectKey ->
            val key = request.newKey()
            Assert.assertEquals(expectKey, key)
        }

        var _resize =
            "&_size=${request.resizeSizeResolver.key}&_precision=${request.resizePrecisionDecider.key}&_scale=${request.resizeScaleDecider.key}"
        verifyKey(uriString + _resize)

        request = request.newRequest {
            listener(onStart = {})
        }
        verifyKey(uriString + _resize)

        request = request.newRequest {
            progressListener { _, _ -> }
        }
        verifyKey(uriString + _resize)

        request = request.newRequest {
            target(imageView)
        }
        _resize =
            "&_size=${request.resizeSizeResolver.key}&_precision=${request.resizePrecisionDecider.key}&_scale=${request.resizeScaleDecider.key}"
        verifyKey(uriString + _resize)

        request = request.newRequest {
            depth(LOCAL, "test")
        }
        val _depth = "&_depth=LOCAL"
        var _parameters = "&_parameters=${request.parameters!!.key}"
        verifyKey(uriString + _depth + _parameters + _resize)

        request = request.newRequest {
            setParameter(key = "type", value = "list")
        }
        _parameters = "&_parameters=${request.parameters!!.key}"
        verifyKey(uriString + _depth + _parameters + _resize)

        request = request.newRequest {
            setParameter(key = "big", value = "true", cacheKey = null)
        }
        _parameters = "&_parameters=${request.parameters!!.key}"
        verifyKey(uriString + _depth + _parameters + _resize)

        request = request.newRequest {
            setHttpHeader("from", "china")
        }
        val _httpHeaders = "&_httpHeaders=${request.httpHeaders!!}"
        verifyKey(uriString + _depth + _parameters + _httpHeaders + _resize)

        request = request.newRequest {
            downloadCachePolicy(READ_ONLY)
        }
        val _downloadCachePolicy = "&_downloadCachePolicy=READ_ONLY"
        verifyKey(uriString + _depth + _parameters + _httpHeaders + _downloadCachePolicy + _resize)

        request = request.newRequest {
            bitmapConfig(RGB_565)
        }
        val _bitmapConfig = "&_bitmapConfig=BitmapConfig(RGB_565)"
        verifyKey(
            uriString + _depth + _parameters + _httpHeaders + _downloadCachePolicy +
                    _bitmapConfig + _resize
        )

        val _colorSpace: String
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            request = request.newRequest {
                colorSpace(ColorSpace.get(ADOBE_RGB))
            }
            _colorSpace = "&_colorSpace=Adobe_RGB_(1998)"
            verifyKey(
                uriString + _depth + _parameters + _httpHeaders + _downloadCachePolicy +
                        _bitmapConfig + _colorSpace + _resize
            )
        } else {
            _colorSpace = ""
        }

        val _preferQualityOverSpeed: String
        if (VERSION.SDK_INT <= VERSION_CODES.M) {
            request = request.newRequest {
                @Suppress("DEPRECATION")
                preferQualityOverSpeed(true)
            }
            _preferQualityOverSpeed = "&_preferQualityOverSpeed=true"
            verifyKey(
                uriString + _depth + _parameters + _httpHeaders + _downloadCachePolicy +
                        _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize
            )
        } else {
            _preferQualityOverSpeed = ""
        }

        request = request.newRequest {
            resize(300, 200, EXACTLY, END_CROP)
        }
        _resize =
            "&_size=${request.resizeSizeResolver.key}&_precision=${request.resizePrecisionDecider.key}&_scale=${request.resizeScaleDecider.key}"
        verifyKey(
            uriString + _depth + _parameters + _httpHeaders + _downloadCachePolicy +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize
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
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize + _transformations
        )

        request = request.newRequest {
            disallowReuseBitmap(true)
        }
        val _disallowReuseBitmap = "&_disallowReuseBitmap=true"
        verifyKey(
            uriString + _depth + _parameters + _httpHeaders + _downloadCachePolicy +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize +
                    _transformations + _disallowReuseBitmap
        )

        request = request.newRequest {
            ignoreExifOrientation(true)
        }
        val _ignoreExifOrientation = "&_ignoreExifOrientation=true"
        verifyKey(
            uriString + _depth + _parameters + _httpHeaders + _downloadCachePolicy +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize +
                    _transformations + _disallowReuseBitmap + _ignoreExifOrientation
        )

        request = request.newRequest {
            resultCachePolicy(WRITE_ONLY)
        }
        val _resultCachePolicy = "&_resultCachePolicy=WRITE_ONLY"
        verifyKey(
            uriString + _depth + _parameters + _httpHeaders + _downloadCachePolicy +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize +
                    _transformations + _disallowReuseBitmap + _ignoreExifOrientation +
                    _resultCachePolicy
        )

        request = request.newRequest {
            placeholder(IconStateImage(drawable.ic_delete) {
                resColorBackground(color.background_dark)
            })
        }
        verifyKey(
            uriString + _depth + _parameters + _httpHeaders + _downloadCachePolicy +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize +
                    _transformations + _disallowReuseBitmap + _ignoreExifOrientation +
                    _resultCachePolicy
        )

        request = request.newRequest {
            error(DrawableStateImage(drawable.ic_delete))
        }
        verifyKey(
            uriString + _depth + _parameters + _httpHeaders + _downloadCachePolicy +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize +
                    _transformations + _disallowReuseBitmap + _ignoreExifOrientation +
                    _resultCachePolicy
        )

        request = request.newRequest {
            transitionFactory(CrossfadeTransition.Factory())
        }
        verifyKey(
            uriString + _depth + _parameters + _httpHeaders + _downloadCachePolicy +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize +
                    _transformations + _disallowReuseBitmap + _ignoreExifOrientation +
                    _resultCachePolicy
        )

        request = request.newRequest {
            disallowAnimatedImage(true)
        }
        val _disallowAnimatedImage = "&_disallowAnimatedImage=true"
        verifyKey(
            uriString + _depth + _parameters + _httpHeaders + _downloadCachePolicy +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize +
                    _transformations + _disallowReuseBitmap + _ignoreExifOrientation +
                    _resultCachePolicy + _disallowAnimatedImage
        )

        request = request.newRequest {
            resizeApplyToDrawable(true)
        }
        val _resizeApplyToDrawable = "&_resizeApplyToDrawable=true"
        verifyKey(
            uriString + _depth + _parameters + _httpHeaders + _downloadCachePolicy +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize +
                    _transformations + _disallowReuseBitmap + _ignoreExifOrientation +
                    _resultCachePolicy + _disallowAnimatedImage + _resizeApplyToDrawable
        )

        request = request.newRequest {
            memoryCachePolicy(WRITE_ONLY)
        }
        val _memoryCachePolicy = "&_memoryCachePolicy=WRITE_ONLY"
        verifyKey(
            uriString + _depth + _parameters + _httpHeaders + _downloadCachePolicy +
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize +
                    _transformations + _disallowReuseBitmap + _ignoreExifOrientation +
                    _resultCachePolicy + _disallowAnimatedImage + _resizeApplyToDrawable +
                    _memoryCachePolicy
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
                    _bitmapConfig + _colorSpace + _preferQualityOverSpeed + _resize +
                    _transformations + _disallowReuseBitmap + _ignoreExifOrientation +
                    _resultCachePolicy + _disallowAnimatedImage + _resizeApplyToDrawable +
                    _memoryCachePolicy + _decoders + _decodeInterceptors + _requestInterceptors
        )
    }
}