package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.Fetcher
import com.github.panpf.sketch.request.ImageRequest

class TestFetcher : Fetcher {

    override suspend fun fetch(): FetchResult {
        TODO("Not yet implemented")
    }

    class Factory : Fetcher.Factory {

        override fun create(sketch: Sketch, request: ImageRequest): Fetcher? {
            return TestFetcher()
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