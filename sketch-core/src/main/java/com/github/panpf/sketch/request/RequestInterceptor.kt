package com.github.panpf.sketch.request

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.internal.ImageRequest

interface RequestInterceptor<REQUEST : ImageRequest, RESULT> {

    suspend fun intercept(chain: Chain<REQUEST, RESULT>): RESULT

    interface Chain<REQUEST : ImageRequest, RESULT> {

        val sketch: Sketch

        val request: REQUEST

        suspend fun proceed(request: REQUEST): RESULT
    }
}