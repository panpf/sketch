package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.state.StateImage
import com.github.panpf.sketch.util.SketchSize

class FakeStateImage(val image: Image = FakeImage(SketchSize(100, 100))) : StateImage {

    override fun getImage(sketch: Sketch, request: ImageRequest, throwable: Throwable?): Image {
        return image
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as FakeStateImage
        return image == other.image
    }

    override fun hashCode(): Int {
        return image.hashCode()
    }

    override fun toString(): String {
        return "FakeStateImage(image=$image)"
    }
}