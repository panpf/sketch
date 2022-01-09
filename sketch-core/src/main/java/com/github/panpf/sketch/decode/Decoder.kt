package com.github.panpf.sketch.decode

import android.graphics.Bitmap
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.request.LoadRequest

/**
 * A [Decoder] converts a [DataSource] into a [Bitmap].
 */
interface Decoder {

    suspend fun decodeBitmap(): BitmapDecodeResult

    fun interface Factory {

        fun create(
            sketch: Sketch,
            request: LoadRequest,
            dataSource: DataSource,
        ): Decoder?
    }
}
