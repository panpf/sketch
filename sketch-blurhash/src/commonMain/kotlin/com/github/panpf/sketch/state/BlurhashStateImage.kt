package com.github.panpf.sketch.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.cache.ImageCacheValue
import com.github.panpf.sketch.decode.internal.createBlurhashBitmap
import com.github.panpf.sketch.fetch.BlurhashUtil
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
fun rememberBlurhashStateImage(blurhash: String, size: Size? = null): BlurhashStateImage =
    remember(blurhash) { BlurhashStateImage(blurhash, size) }

/**
 * StateImage that creates a Bitmap directly from blurhash and caches it in memory
 *
 * @see com.github.panpf.sketch.compose.core.common.test.state.BlurhashStateImageTest
 */
@Stable
data class BlurhashStateImage(val blurhash: String, val blurhashSize: Size? = null) : StateImage {

    override val key: String = "BlurhashStateImage(${blurhash},${blurhashSize})"

    private var sizeInUri: Size? = null

    init {
        if (blurhashSize != null) {
            require(!blurhashSize.isEmpty) {
                "blurhashSize must be not empty"
            }
        } else {
            val queryString = blurhash.substring(blurhash.indexOf('&') + 1)
            val sizeInUri = parseQueryParameters(queryString)
            require(sizeInUri != null && !sizeInUri.isEmpty) {
                "When blurhashSize is not set, size must be specified in blurhash uri"
            }
            this.sizeInUri = sizeInUri
        }
    }

    override fun getImage(sketch: Sketch, request: ImageRequest, throwable: Throwable?): Image? {
        if (!BlurhashUtil.isValid(blurhash)) {
            return null
        }

        val cacheKey = "blurhash:$blurhash"
        val memoryCache = sketch.memoryCache

        val cachedValue = memoryCache[cacheKey]
        if (cachedValue != null) {
            println("From cache blurhash $blurhash")
            return cachedValue.image
        }

        println("Creating bitmap for blurhash $blurhash")
        val realIconSize = blurhashSize ?: sizeInUri!!

        val bitmap = createBlurhashBitmap(realIconSize.width, realIconSize.height)
        val bitmapImage = bitmap.asImage()

        val cacheValue = ImageCacheValue(bitmapImage)
        memoryCache.put(cacheKey, cacheValue)

        @Suppress("OPT_IN_USAGE")
        GlobalScope.launch(Dispatchers.Main) {
            val decodingResult = withContext(ioCoroutineDispatcher()) {
                runCatching {
                    println("Decoding blurhash $blurhash on background thread")
                    BlurhashUtil.decodeByte(blurhash, realIconSize.width, realIconSize.height)
                }
            }

            decodingResult.onSuccess { decodedBytes ->
                println("Installing pixels for blurhash $blurhash")
                bitmap.installPixels(decodedBytes)
            }.onFailure { exception ->
                println("Failed to decode blurhash $blurhash: ${exception.message}")
                exception.printStackTrace()
            }
        }

        return bitmapImage
    }

    override fun toString(): String = "BlurhashStateImage(blurhash=${blurhash}, size=$blurhashSize)"
}