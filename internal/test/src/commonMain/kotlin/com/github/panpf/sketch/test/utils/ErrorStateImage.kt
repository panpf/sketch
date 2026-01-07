package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.state.StateImage

class ErrorStateImage : StateImage {

    override val key: String = "ErrorStateImage"

    override fun getImage(sketch: Sketch, request: ImageRequest, throwable: Throwable?): Image {
        throw Exception("ErrorStateImage always throws an exception when getImage is called")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other != null && this::class == other::class
    }

    override fun hashCode(): Int {
        return this::class.hashCode()
    }

    override fun toString(): String = "ErrorStateImage"
}