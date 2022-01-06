package com.github.panpf.sketch.request

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.internal.ImageRequest

interface Interceptor<REQUEST : ImageRequest, RESULT> {

    suspend fun intercept(sketch: Sketch, chain: Chain<REQUEST, RESULT>): RESULT

    interface Chain<REQUEST : ImageRequest, RESULT> {

        val request: REQUEST

        suspend fun proceed(sketch: Sketch, request: REQUEST): RESULT
    }
}