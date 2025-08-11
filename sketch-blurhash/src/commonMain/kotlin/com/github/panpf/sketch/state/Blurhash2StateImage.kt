package com.github.panpf.sketch.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.cache.ImageCacheValue
import com.github.panpf.sketch.decode.internal.createBlurHashBitmap
import com.github.panpf.sketch.fetch.Blurhash2Util
import com.github.panpf.sketch.fetch.parseQueryParameters
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.installPixels
import com.github.panpf.sketch.util.ioCoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Create a [ColorPainterStateImage] instance and remember it
 *
 * @see com.github.panpf.sketch.compose.core.common.test.state.ColorPainterStateImageTest.testRememberColorPainterStateImage
 */
@Composable
fun rememberBlurHashStateImage(blurHash: String, size: Size? = null): BlurHashStateImage =
    remember(blurHash) { BlurHashStateImage(blurHash, size) }

/**
 * StateImage that creates a Bitmap directly from blurHash and caches it in memory
 *
 * @see com.github.panpf.sketch.compose.core.common.test.state.BlurHashStateImageTest
 */
@Stable
data class BlurHashStateImage(val blurHash: String, val size: Size? = null) : StateImage {

    override val key: String = "BlurHashStateImage(${blurHash},${size})"

    private var sizeInUri: Size? = null

    init {
        if (size != null) {
            require(!size.isEmpty) {
                "size must be not empty"
            }
        } else {
            val queryString = blurHash.substring(blurHash.indexOf('&') + 1)
            val sizeInUri = parseQueryParameters(queryString)
            require(sizeInUri != null && !sizeInUri.isEmpty) {
                "When size is not set, size must be specified in blurHash uri"
            }
            this.sizeInUri = sizeInUri
        }
    }

    override fun getImage(sketch: Sketch, request: ImageRequest, throwable: Throwable?): Image? {
        if (!Blurhash2Util.isValid(blurHash)) {
            return null
        }

        val cacheKey = "blurhash:$blurHash"
        val memoryCache = sketch.memoryCache

        val cachedValue = memoryCache[cacheKey]
        if (cachedValue != null) {
            return cachedValue.image
        }

        val realIconSize = size ?: sizeInUri!!

        val bitmap = createBlurHashBitmap(realIconSize.width, realIconSize.height)
        val bitmapImage = bitmap.asImage()

        val cacheValue = ImageCacheValue(bitmapImage)
        memoryCache.put(cacheKey, cacheValue)

        @Suppress("OPT_IN_USAGE")
        GlobalScope.launch(Dispatchers.Main) {
            val decodingResult = withContext(ioCoroutineDispatcher()) {
                runCatching {
                    Blurhash2Util.decodeByte(blurHash, realIconSize.width, realIconSize.height)
                }
            }

            decodingResult.onSuccess { decodedBytes ->
                bitmap.installPixels(decodedBytes)
            }.onFailure { exception ->
                exception.printStackTrace()
            }
        }

        return bitmapImage
    }

    override fun toString(): String = "BlurHashStateImage(blurHash=${blurHash}, size=$size)"
}