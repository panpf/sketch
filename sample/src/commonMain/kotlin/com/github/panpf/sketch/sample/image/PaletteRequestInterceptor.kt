package com.github.panpf.sketch.sample.image

import androidx.annotation.MainThread
import com.github.panpf.sketch.BitmapImage
import com.github.panpf.sketch.cache.internal.ResultCacheRequestInterceptor
import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.RequestInterceptor
import com.kmpalette.palette.graphics.Palette
import kotlinx.coroutines.withContext

class PaletteRequestInterceptor : RequestInterceptor {

    companion object Companion {
        const val SORT_WEIGHT = ResultCacheRequestInterceptor.SORT_WEIGHT + 1
    }

    override val key: String = "Palette"
    override val sortWeight: Int = SORT_WEIGHT

    @MainThread
    override suspend fun intercept(chain: RequestInterceptor.Chain): Result<ImageData> {
        val result = chain.proceed(chain.request)
        val imageData = result.getOrNull() ?: return result
        val image = imageData.image
        if (image !is BitmapImage) return result
        val bitmap = image.bitmap
        val newImageData = withContext(chain.sketch.decodeTaskDispatcher) {
            runCatching {
                val palette = Palette.Builder(bitmap).generate()
                val propertyString = palette.toPropertyString()
                    ?: throw Exception("Palette is empty")
                imageData.newImageData {
                    addExtras("simple_palette", propertyString)
                }
            }
        }.onFailure {
            chain.sketch.logger.e("PaletteRequestInterceptor. $it")
        }.getOrNull()
            ?: return result
        return Result.success(newImageData)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other != null && this::class == other::class
    }

    override fun hashCode(): Int {
        return this::class.hashCode()
    }

    override fun toString(): String = "PaletteRequestInterceptor"
}

val ImageData.simplePalette: SimplePalette?
    get() = extras?.get("simple_palette")
        ?.trim()?.takeIf { it.isNotEmpty() }
        ?.let {
            try {
                Palette.fromPropertyString(it)
            } catch (e: Exception) {
                Exception("SimplePalette fromPropertyString error: $it", e).printStackTrace()
                null
            }
        }

val ImageResult.Success.simplePalette: SimplePalette?
    get() = extras?.get("simple_palette")
        ?.trim()?.takeIf { it.isNotEmpty() }
        ?.let {
            try {
                Palette.fromPropertyString(it)
            } catch (e: Exception) {
                Exception("SimplePalette fromPropertyString error: $it", e).printStackTrace()
                null
            }
        }

fun Palette.toPropertyString(): String? = buildString {
    listOf(
        "dominantSwatch" to dominantSwatch,
        "vibrantSwatch" to vibrantSwatch,
        "darkVibrantSwatch" to darkVibrantSwatch,
        "lightVibrantSwatch" to lightVibrantSwatch,
        "mutedSwatch" to mutedSwatch,
        "darkMutedSwatch" to darkMutedSwatch,
        "lightMutedSwatch" to lightMutedSwatch,
    ).forEach { (name, value) ->
        if (value != null) {
            if (this@buildString.isNotEmpty()) {
                append(";")
            }
            append("${name}=${value.rgb},${value.population}")
        }
    }
}.trim().takeIf { it.isNotEmpty() }

fun Palette.Companion.fromPropertyString(propertyString: String): SimplePalette {
    val swatchMap = propertyString.split(";").associate { line ->
        val (name, value) = line.split("=")
        val (rgb, population) = value.split(",")
        name to Palette.Swatch(
            rgb = rgb.toInt(),
            population = population.toInt(),
        )
    }
    return SimplePalette(
        dominantSwatch = swatchMap["dominantSwatch"],
        vibrantSwatch = swatchMap["vibrantSwatch"],
        darkVibrantSwatch = swatchMap["darkVibrantSwatch"],
        lightVibrantSwatch = swatchMap["lightVibrantSwatch"],
        mutedSwatch = swatchMap["mutedSwatch"],
        darkMutedSwatch = swatchMap["darkMutedSwatch"],
        lightMutedSwatch = swatchMap["lightMutedSwatch"],
    )
}

class SimplePalette(
    val dominantSwatch: Palette.Swatch?,
    val darkMutedSwatch: Palette.Swatch?,
    val mutedSwatch: Palette.Swatch?,
    val lightMutedSwatch: Palette.Swatch?,
    val darkVibrantSwatch: Palette.Swatch?,
    val vibrantSwatch: Palette.Swatch?,
    val lightVibrantSwatch: Palette.Swatch?,
)