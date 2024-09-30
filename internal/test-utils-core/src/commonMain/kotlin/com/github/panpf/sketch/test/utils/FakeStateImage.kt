package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.state.StateImage
import com.github.panpf.sketch.util.SketchSize

data class FakeStateImage(val image: Image = FakeImage(SketchSize(100, 100))) : StateImage {

    override val key: String = "FakeStateImage(image=$image)"

    override fun getImage(sketch: Sketch, request: ImageRequest, throwable: Throwable?): Image {
        return image
    }

    override fun toString(): String {
        return "FakeStateImage(image=$image)"
    }
}