package com.github.panpf.sketch.decode

import android.graphics.Bitmap
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.LoadRequest
import java.io.Closeable

/**
 * A [BitmapDecoder] converts a [DataSource] into a [Bitmap].
 */

interface BitmapDecoder : Closeable {

    suspend fun decodeBitmap(): BitmapDecodeResult

    fun interface Factory {

        fun create(sketch: Sketch, request: LoadRequest, fetchResult: FetchResult): BitmapDecoder?
    }
}
