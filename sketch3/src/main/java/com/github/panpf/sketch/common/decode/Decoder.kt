package com.github.panpf.sketch.common.decode

import android.graphics.Bitmap
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.common.ImageRequest
import com.github.panpf.sketch.common.ImageResult
import com.github.panpf.sketch.common.RequestExtras
import com.github.panpf.sketch.common.datasource.DataSource

/**
 * A [Decoder] converts a [DataSource] into a [Bitmap].
 */
fun interface Decoder {

    suspend fun decode(): DecodeResult?

    fun interface Factory {

        fun create(
            sketch: Sketch,
            request: ImageRequest,
            extras: RequestExtras<ImageRequest, ImageResult>?,
            source: DataSource,
        ): Decoder?
    }
}
