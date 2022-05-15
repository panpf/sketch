package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.decode.BitmapDecoder
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.RequestExtras

class TestBitmapDecoder : BitmapDecoder {

    override suspend fun decode(): BitmapDecodeResult {
        TODO("Not yet implemented")
    }

    class Factory : BitmapDecoder.Factory {

        override fun create(
            sketch: Sketch,
            request: ImageRequest,
            requestExtras: RequestExtras,
            fetchResult: FetchResult
        ): BitmapDecoder {
            return TestBitmapDecoder()
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