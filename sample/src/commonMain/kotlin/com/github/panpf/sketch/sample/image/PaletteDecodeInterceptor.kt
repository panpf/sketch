package com.github.panpf.sketch.sample.image

import com.github.panpf.sketch.BitmapImage
import com.github.panpf.sketch.decode.DecodeInterceptor
import com.github.panpf.sketch.decode.DecodeInterceptor.Chain
import com.github.panpf.sketch.decode.DecodeResult
import com.github.panpf.sketch.request.ImageResult
import com.kmpalette.palette.graphics.Palette

class PaletteDecodeInterceptor : DecodeInterceptor {

    override val key: String? = null

    override val sortWeight: Int = 95

    override suspend fun intercept(chain: Chain): Result<DecodeResult> {
        val result = chain.proceed()
        val decodeResult = result.getOrNull() ?: return result
        val image = decodeResult.image
        val bitmap = if (image is BitmapImage) {
            image.bitmap
        } else {
            return result
        }
        val palette = try {
            Palette.Builder(bitmap).generate()
        } catch (e: Throwable) {
            e.printStackTrace()
            return result
        }
        val propertyString = palette.toPropertyString()
        @Suppress("FoldInitializerAndIfToElvis", "RedundantSuppression")
        if (propertyString == null) {
            chain.sketch.logger.e("PaletteDecodeInterceptor. palette is empty")
            return result
        }
        val newDecodeResult = decodeResult.newResult {
            addExtras("simple_palette", propertyString)
        }
        return Result.success(newDecodeResult)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other != null && this::class == other::class
    }

    override fun hashCode(): Int {
        return this::class.hashCode()
    }

    override fun toString(): String = "PaletteDecodeInterceptor"
}

val DecodeResult.simplePalette: SimplePalette?
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