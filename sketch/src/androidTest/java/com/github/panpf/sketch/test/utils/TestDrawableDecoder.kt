package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.DrawableDecodeResult
import com.github.panpf.sketch.decode.DrawableDecoder
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.RequestExtras

class TestDrawableDecoder : DrawableDecoder {

    override suspend fun decode(): DrawableDecodeResult {
        TODO("Not yet implemented")
    }

    class Factory : DrawableDecoder.Factory {

        override fun create(
            sketch: Sketch,
            request: ImageRequest,
            requestExtras: RequestExtras,
            fetchResult: FetchResult
        ): DrawableDecoder {
            return TestDrawableDecoder()
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            return true
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }

        override fun toString(): String {
            return "TestFetcher"
        }
    }
}