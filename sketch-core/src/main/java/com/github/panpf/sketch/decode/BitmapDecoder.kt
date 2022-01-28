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

// todo 增加 decodeRegionBitmap 方法，这样 core 和 zoom 都可以用
interface BitmapDecoder : Closeable {

    suspend fun decodeBitmap(): BitmapDecodeResult

    fun interface Factory {

        fun create(sketch: Sketch, request: LoadRequest, fetchResult: FetchResult): BitmapDecoder?
    }
}
