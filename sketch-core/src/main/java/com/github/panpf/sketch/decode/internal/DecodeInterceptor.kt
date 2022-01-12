package com.github.panpf.sketch.decode.internal

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.request.internal.ImageRequest

interface DecodeInterceptor<REQUEST : ImageRequest, RESULT> {

    suspend fun intercept(chain: Chain<REQUEST, RESULT>): RESULT

    interface Chain<REQUEST : ImageRequest, RESULT> {

        val sketch: Sketch

        val request: REQUEST

        val dataSource: DataSource?

        suspend fun proceed(request: REQUEST): RESULT
    }
}