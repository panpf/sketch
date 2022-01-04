package com.github.panpf.sketch.decode

import android.graphics.Bitmap
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.request.internal.ImageRequest

/**
 * A [Decoder] converts a [DataSource] into a [Bitmap].
 */
fun interface Decoder {

    // todo 抽象解码器，支持视频和 svg，以及 gif

    suspend fun decode(): DecodeResult

    fun interface Factory {

        fun create(
            sketch: Sketch,
            request: ImageRequest,
            dataSource: DataSource,
        ): Decoder?
    }
}
