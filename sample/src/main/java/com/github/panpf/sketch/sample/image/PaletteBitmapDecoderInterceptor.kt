package com.github.panpf.sketch.sample.image

import androidx.annotation.WorkerThread
import androidx.palette.graphics.Palette
import androidx.palette.graphics.Palette.Swatch
import com.github.panpf.sketch.decode.BitmapDecodeInterceptor
import com.github.panpf.sketch.decode.BitmapDecodeInterceptor.Chain
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.request.LoadResult
import com.github.panpf.sketch.sample.image.PaletteBitmapDecoderInterceptor.Companion.simplePaletteFromJSONObject
import org.json.JSONObject

class PaletteBitmapDecoderInterceptor : BitmapDecodeInterceptor {

    override val key: String? = null

    @WorkerThread
    override suspend fun intercept(chain: Chain): BitmapDecodeResult {
        val result = chain.proceed()
        val palette = Palette.from(result.bitmap).generate()
        return result.newResult {
            addExtras("simple_palette", paletteToJSONObject(palette).toString())
        }
    }

    companion object {
        fun paletteToJSONObject(palette: Palette): JSONObject {
            return JSONObject().apply {
                mapOf(
                    "dominantSwatch" to palette.dominantSwatch,
                    "darkMutedSwatch" to palette.darkMutedSwatch,
                    "mutedSwatch" to palette.mutedSwatch,
                    "lightMutedSwatch" to palette.lightMutedSwatch,
                    "darkVibrantSwatch" to palette.darkVibrantSwatch,
                    "vibrantSwatch" to palette.vibrantSwatch,
                    "lightVibrantSwatch" to palette.lightVibrantSwatch,
                ).entries.forEach {
                    val swatch = it.value
                    if (swatch != null) {
                        put(it.key, JSONObject().apply {
                            put("rgb", swatch.rgb)
                            put("population", swatch.population)
                        })
                    }
                }
            }
        }

        fun simplePaletteFromJSONObject(jsonObject: JSONObject): SimplePalette {
            return SimplePalette(
                dominantSwatch = jsonObject.optJSONObject("dominantSwatch")?.let {
                    Swatch(it.getInt("rgb"), it.getInt("population"))
                },
                darkMutedSwatch = jsonObject.optJSONObject("darkMutedSwatch")?.let {
                    Swatch(it.getInt("rgb"), it.getInt("population"))
                },
                mutedSwatch = jsonObject.optJSONObject("mutedSwatch")?.let {
                    Swatch(it.getInt("rgb"), it.getInt("population"))
                },
                lightMutedSwatch = jsonObject.optJSONObject("lightMutedSwatch")?.let {
                    Swatch(it.getInt("rgb"), it.getInt("population"))
                },
                darkVibrantSwatch = jsonObject.optJSONObject("darkVibrantSwatch")?.let {
                    Swatch(it.getInt("rgb"), it.getInt("population"))
                },
                vibrantSwatch = jsonObject.optJSONObject("vibrantSwatch")?.let {
                    Swatch(it.getInt("rgb"), it.getInt("population"))
                },
                lightVibrantSwatch = jsonObject.optJSONObject("lightVibrantSwatch")?.let {
                    Swatch(it.getInt("rgb"), it.getInt("population"))
                },
            )
        }
    }
}

val DisplayResult.Success.simplePalette: SimplePalette?
    get() = extras?.get("simple_palette")?.let {
        simplePaletteFromJSONObject(JSONObject(it))
    }

val LoadResult.Success.simplePalette: SimplePalette?
    get() = extras?.get("simple_palette")?.let {
        simplePaletteFromJSONObject(JSONObject(it))
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