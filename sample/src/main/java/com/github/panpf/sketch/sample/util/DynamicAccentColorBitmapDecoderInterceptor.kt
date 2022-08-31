package com.github.panpf.sketch.sample.util

import androidx.annotation.ColorInt
import androidx.annotation.WorkerThread
import androidx.palette.graphics.Palette
import com.github.panpf.sketch.decode.BitmapDecodeInterceptor
import com.github.panpf.sketch.decode.BitmapDecodeInterceptor.Chain
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.request.LoadResult

class DynamicAccentColorBitmapDecoderInterceptor : BitmapDecodeInterceptor {

    override val key: String? = null

    @WorkerThread
    override suspend fun intercept(chain: Chain): BitmapDecodeResult {
        val result = chain.proceed()
        val palette = Palette.from(result.bitmap).generate()
        val accentColor =
            palette.darkMutedSwatch?.rgb
                ?: palette.darkVibrantSwatch?.rgb
                ?: palette.mutedSwatch?.rgb
                ?: palette.lightMutedSwatch?.rgb
                ?: palette.vibrantSwatch?.rgb
                ?: palette.lightVibrantSwatch?.rgb
        return result.newResult {
            addExtras("DynamicAccentColor", accentColor.toString())
        }
    }
}

@get:ColorInt
val DisplayResult.Success.dynamicAccentColor: Int?
    get() = extras?.get("DynamicAccentColor")?.toIntOrNull()

@get:ColorInt
val LoadResult.Success.dynamicAccentColor: Int?
    get() = extras?.get("DynamicAccentColor")?.toIntOrNull()