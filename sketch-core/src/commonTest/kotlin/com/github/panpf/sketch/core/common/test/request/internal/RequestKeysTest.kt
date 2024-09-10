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

@file:Suppress("LocalVariableName")

package com.github.panpf.sketch.core.common.test.request.internal

import com.github.panpf.sketch.cache.CachePolicy.READ_ONLY
import com.github.panpf.sketch.cache.CachePolicy.WRITE_ONLY
import com.github.panpf.sketch.decode.BitmapConfig
import com.github.panpf.sketch.request.Depth.LOCAL
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.newCacheKey
import com.github.panpf.sketch.request.internal.newKey
import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.Scale.END_CROP
import com.github.panpf.sketch.state.ErrorStateImage
import com.github.panpf.sketch.test.utils.FakeImage
import com.github.panpf.sketch.test.utils.FakeStateImage
import com.github.panpf.sketch.test.utils.TestDecodeInterceptor
import com.github.panpf.sketch.test.utils.TestDecoder
import com.github.panpf.sketch.test.utils.TestFetcher
import com.github.panpf.sketch.test.utils.TestRequestInterceptor
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.transform.CircleCropTransformation
import com.github.panpf.sketch.transform.RotateTransformation
import com.github.panpf.sketch.transition.CrossfadeTransition
import com.github.panpf.sketch.util.SketchSize
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class RequestKeysTest {

    @Test
    fun testNewRequestKey() {
        val context = getTestContext()
        val uri = "http://sample.com/sample.jpeg?from=sketch"

        var request = ImageRequest(context, uri)

        val verifyKey: (String) -> Unit = { expectKey ->
            val key = request.newKey()
            assertEquals(expectKey, key)
        }

        var _size = "&_size=${request.sizeResolver.key}"
        var _precision = "&_precision=${request.precisionDecider.key}"
        var _scale = "&_scale=${request.scaleDecider.key}"
        verifyKey(uri + _size + _precision + _scale)

        request = request.newRequest {
            registerListener(onStart = {})
        }
        verifyKey(uri + _size + _precision + _scale)

        request = request.newRequest {
            registerProgressListener { _, _ -> }
        }
        verifyKey(uri + _size + _precision + _scale)

        request = request.newRequest {
            depth(LOCAL, "test")
        }
        val _depth = "&_depth=${request.depthHolder.key}"
        verifyKey(uri + _depth + _size + _precision + _scale)

        request = request.newRequest {
            setExtra(key = "type", value = "list")
        }
        var _extras = "&_extras=${request.extras!!.requestKey}"
        verifyKey(uri + _depth + _extras + _size + _precision + _scale)

        request = request.newRequest {
            setExtra(key = "big", value = "true", cacheKey = null)
        }
        _extras = "&_extras=${request.extras!!.requestKey}"
        verifyKey(uri + _depth + _extras + _size + _precision + _scale)

        request = request.newRequest {
            setHttpHeader("from", "china")
        }
        val _httpHeaders = "&_httpHeaders=${request.httpHeaders!!}"
        verifyKey(uri + _depth + _extras + _httpHeaders + _size + _precision + _scale)

        request = request.newRequest {
            downloadCachePolicy(READ_ONLY)
        }
        val _downloadCachePolicy = "&_downloadCachePolicy=READ_ONLY"
        verifyKey(uri + _depth + _extras + _httpHeaders + _downloadCachePolicy + _size + _precision + _scale)

        request = request.newRequest {
            bitmapConfig(BitmapConfig.HighQuality)
        }
        val _bitmapConfig = "&_bitmapConfig=${request.bitmapConfig?.key}"

        request = request.newRequest {
            resize(300, 200, EXACTLY, END_CROP)
        }
        _size = "&_size=${request.sizeResolver.key}"
        _precision = "&_precision=${request.precisionDecider.key}"
        _scale = "&_scale=${request.scaleDecider.key}"
        verifyKey(
            uri + _depth + _extras + _httpHeaders + _downloadCachePolicy + _bitmapConfig + _size + _precision + _scale
        )

        request = request.newRequest {
            sizeMultiplier(1.5f)
        }
        val _sizeMultiplier = "&_sizeMultiplier=${request.sizeMultiplier}"
        verifyKey(
            uri + _depth + _extras + _httpHeaders + _downloadCachePolicy + _bitmapConfig +
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
            uri + _depth + _extras + _httpHeaders + _downloadCachePolicy + _bitmapConfig +
                    _size + _sizeMultiplier + _precision + _scale + _transformations
        )

        request = request.newRequest {
            resultCachePolicy(WRITE_ONLY)
        }
        val _resultCachePolicy = "&_resultCachePolicy=WRITE_ONLY"
        verifyKey(
            uri + _depth + _extras + _httpHeaders + _downloadCachePolicy + _bitmapConfig +
                    _size + _sizeMultiplier + _precision + _scale +
                    _transformations + _resultCachePolicy
        )

        request = request.newRequest {
            disallowAnimatedImage(true)
        }
        val _disallowAnimatedImage = "&_disallowAnimatedImage=true"
        verifyKey(
            uri + _depth + _extras + _httpHeaders + _downloadCachePolicy + _bitmapConfig +
                    _size + _sizeMultiplier + _precision + _scale +
                    _transformations + _resultCachePolicy + _disallowAnimatedImage
        )

        request = request.newRequest {
            resizeOnDraw(true)
        }
        val _resizeOnDraw = "&_resizeOnDraw=true"
        verifyKey(
            uri + _depth + _extras + _httpHeaders + _downloadCachePolicy + _bitmapConfig +
                    _size + _sizeMultiplier + _precision + _scale +
                    _transformations + _resultCachePolicy + _disallowAnimatedImage + _resizeOnDraw
        )

        request = request.newRequest {
            allowNullImage(true)
        }
        val _allowNullImage = "&_allowNullImage=true"
        verifyKey(
            uri + _depth + _extras + _httpHeaders + _downloadCachePolicy + _bitmapConfig +
                    _size + _sizeMultiplier + _precision + _scale +
                    _transformations + _resultCachePolicy + _disallowAnimatedImage + _resizeOnDraw +
                    _allowNullImage
        )

        request = request.newRequest {
            memoryCachePolicy(WRITE_ONLY)
        }
        val _memoryCachePolicy = "&_memoryCachePolicy=WRITE_ONLY"
        verifyKey(
            uri + _depth + _extras + _httpHeaders + _downloadCachePolicy + _bitmapConfig +
                    _size + _sizeMultiplier + _precision + _scale +
                    _transformations + _resultCachePolicy + _disallowAnimatedImage + _resizeOnDraw +
                    _allowNullImage + _memoryCachePolicy
        )

        val transitionFactory = CrossfadeTransition.Factory()
        request = request.newRequest {
            transitionFactory(transitionFactory)
        }
        val _transitionFactory = "&_transitionFactory=${transitionFactory.key}"
        verifyKey(
            uri + _depth + _extras + _httpHeaders + _downloadCachePolicy + _bitmapConfig +
                    _size + _sizeMultiplier + _precision + _scale +
                    _transformations + _resultCachePolicy + _disallowAnimatedImage + _resizeOnDraw +
                    _allowNullImage + _memoryCachePolicy + _transitionFactory
        )

        val placeholder = FakeStateImage()
        request = request.newRequest {
            placeholder(placeholder)
        }
        val _placeholder = "&_placeholder=${placeholder.key}"
        verifyKey(
            uri + _depth + _extras + _httpHeaders + _downloadCachePolicy + _bitmapConfig +
                    _size + _sizeMultiplier + _precision + _scale +
                    _transformations + _resultCachePolicy + _disallowAnimatedImage + _resizeOnDraw +
                    _allowNullImage + _memoryCachePolicy + _transitionFactory + _placeholder
        )

        val fallback = FakeStateImage(FakeImage(SketchSize(200, 200)))
        request = request.newRequest {
            fallback(fallback)
        }
        val _fallback = "&_fallback=${fallback.key}"
        verifyKey(
            uri + _depth + _extras + _httpHeaders + _downloadCachePolicy + _bitmapConfig +
                    _size + _sizeMultiplier + _precision + _scale +
                    _transformations + _resultCachePolicy + _disallowAnimatedImage + _resizeOnDraw +
                    _allowNullImage + _memoryCachePolicy + _transitionFactory + _placeholder + _fallback
        )

        val error = FakeStateImage()
        request = request.newRequest {
            error(error)
        }
        val _error = "&_error=${ErrorStateImage(error).key}"
        verifyKey(
            uri + _depth + _extras + _httpHeaders + _downloadCachePolicy + _bitmapConfig +
                    _size + _sizeMultiplier + _precision + _scale +
                    _transformations + _resultCachePolicy + _disallowAnimatedImage + _resizeOnDraw +
                    _allowNullImage + _memoryCachePolicy + _transitionFactory + _placeholder +
                    _fallback + _error
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
            uri + _depth + _extras + _httpHeaders + _downloadCachePolicy + _bitmapConfig +
                    _size + _sizeMultiplier + _precision + _scale +
                    _transformations + _resultCachePolicy + _disallowAnimatedImage + _resizeOnDraw +
                    _allowNullImage + _memoryCachePolicy + _transitionFactory + _placeholder +
                    _fallback + _error + _decoders + _decodeInterceptors + _requestInterceptors
        )
    }

    @Test
    fun testNewCacheKey() = runTest {
        val context = getTestContext()
        val uri = "http://sample.com/sample.jpeg?from=sketch"

        var request = ImageRequest(context, uri)

        val verifyCacheKey: suspend (String) -> Unit = { expectCacheKey ->
            val size = request.sizeResolver.size()
            val cacheKey = request.newCacheKey(size)
            assertEquals(expectCacheKey, cacheKey)
        }

        var size = request.sizeResolver.size()
        var _size = "&_size=${size}"
        var _precision = "&_precision=${request.precisionDecider.key}"
        var _scale = "&_scale=${request.scaleDecider.key}"
        verifyCacheKey(uri + _size + _precision + _scale)

        request = request.newRequest {
            registerListener(onStart = {})
        }
        verifyCacheKey(uri + _size + _precision + _scale)

        request = request.newRequest {
            registerProgressListener { _, _ -> }
        }
        verifyCacheKey(uri + _size + _precision + _scale)

        request = request.newRequest {
            depth(LOCAL, "test")
        }
        verifyCacheKey(uri + _size + _precision + _scale)

        request = request.newRequest {
            setExtra(key = "type", value = "list")
        }
        val _extras = "&_extras=${request.extras!!.cacheKey}"
        verifyCacheKey(
            uri + _extras + _size + _precision + _scale
        )

        request = request.newRequest {
            setExtra(key = "big", value = "true", cacheKey = null)
        }
        verifyCacheKey(
            uri + _extras + _size + _precision + _scale
        )

        request = request.newRequest {
            setHttpHeader("from", "china")
        }
        verifyCacheKey(
            uri + _extras + _size + _precision + _scale
        )

        request = request.newRequest {
            downloadCachePolicy(READ_ONLY)
        }
        verifyCacheKey(
            uri + _extras + _size + _precision + _scale
        )

        request = request.newRequest {
            bitmapConfig(BitmapConfig.HighQuality)
        }
        val _bitmapConfig = "&_bitmapConfig=${request.bitmapConfig?.key}"

        request = request.newRequest {
            resize(300, 200, EXACTLY, END_CROP)
        }
        size = request.sizeResolver.size()
        _size = "&_size=${size}"
        _precision = "&_precision=${request.precisionDecider.key}"
        _scale = "&_scale=${request.scaleDecider.key}"
        verifyCacheKey(
            uri + _extras + _bitmapConfig + _size + _precision + _scale
        )

        request = request.newRequest {
            sizeMultiplier(1.5f)
        }
        val _sizeMultiplier = "&_sizeMultiplier=${request.sizeMultiplier}"
        verifyCacheKey(
            uri + _extras + _bitmapConfig + _size + _sizeMultiplier + _precision + _scale
        )

        request = request.newRequest {
            transformations(CircleCropTransformation(), RotateTransformation(45))
        }
        val _transformations =
            request.transformations!!.joinToString(prefix = "[", postfix = "]", separator = ",") {
                it.key.replace("Transformation", "")
            }.let { "&_transformations=$it" }
        verifyCacheKey(
            uri + _extras + _bitmapConfig + _size + _sizeMultiplier + _precision + _scale + _transformations
        )

        request = request.newRequest {
            resultCachePolicy(WRITE_ONLY)
        }
        verifyCacheKey(
            uri + _extras + _bitmapConfig + _size + _sizeMultiplier + _precision + _scale + _transformations
        )

        request = request.newRequest {
            disallowAnimatedImage(true)
        }
        val _disallowAnimatedImage = "&_disallowAnimatedImage=true"
        verifyCacheKey(
            uri + _extras + _bitmapConfig +
                    _size + _sizeMultiplier + _precision + _scale + _transformations + _disallowAnimatedImage
        )

        request = request.newRequest {
            resizeOnDraw(true)
        }
        verifyCacheKey(
            uri + _extras + _bitmapConfig +
                    _size + _sizeMultiplier + _precision + _scale + _transformations + _disallowAnimatedImage
        )

        request = request.newRequest {
            memoryCachePolicy(WRITE_ONLY)
        }
        verifyCacheKey(
            uri + _extras + _bitmapConfig +
                    _size + _sizeMultiplier + _precision + _scale + _transformations + _disallowAnimatedImage
        )

        val transitionFactory = CrossfadeTransition.Factory()
        request = request.newRequest {
            transitionFactory(transitionFactory)
        }
        verifyCacheKey(
            uri + _extras + _bitmapConfig +
                    _size + _sizeMultiplier + _precision + _scale + _transformations + _disallowAnimatedImage
        )

        val placeholder = FakeStateImage()
        request = request.newRequest {
            placeholder(placeholder)
        }
        verifyCacheKey(
            uri + _extras + _bitmapConfig +
                    _size + _sizeMultiplier + _precision + _scale + _transformations + _disallowAnimatedImage
        )

        val fallback = FakeStateImage(FakeImage(SketchSize(200, 200)))
        request = request.newRequest {
            fallback(fallback)
        }
        verifyCacheKey(
            uri + _extras + _bitmapConfig +
                    _size + _sizeMultiplier + _precision + _scale + _transformations + _disallowAnimatedImage
        )

        val error = FakeStateImage()
        request = request.newRequest {
            error(error)
        }
        verifyCacheKey(
            uri + _extras + _bitmapConfig +
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
            uri + _extras + _bitmapConfig +
                    _size + _sizeMultiplier + _precision + _scale + _transformations + _disallowAnimatedImage + _decoders + _decodeInterceptors + _requestInterceptors
        )
    }
}