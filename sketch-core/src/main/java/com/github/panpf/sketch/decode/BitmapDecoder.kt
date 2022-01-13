package com.github.panpf.sketch.decode

import android.graphics.Bitmap
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.request.LoadRequest
import java.io.Closeable

/**
 * A [BitmapDecoder] converts a [DataSource] into a [Bitmap].
 */

// todo 解码器分成 Bitmap 和 Drawable 两种，最后一个 drawable 解码器再用 bitmap 解码器解码并封装成 drawable
interface BitmapDecoder : Closeable {

    suspend fun decodeBitmap(): BitmapDecodeResult

    fun interface Factory {

        fun create(
            sketch: Sketch,
            request: LoadRequest,
            dataSource: DataSource,
        ): BitmapDecoder?
    }
}
