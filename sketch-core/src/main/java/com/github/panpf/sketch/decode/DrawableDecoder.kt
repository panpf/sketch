package com.github.panpf.sketch.decode

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.request.DisplayRequest

interface DrawableDecoder {

    suspend fun decodeDrawable(): DrawableDecodeResult

    fun interface Factory {

        fun create(
            sketch: Sketch,
            request: DisplayRequest,
            dataSource: DataSource,
        ): DrawableDecoder?
    }
}