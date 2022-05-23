package com.github.panpf.sketch.test.utils

import android.graphics.Bitmap
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.transform.TransformResult
import com.github.panpf.sketch.transform.Transformation
import kotlinx.coroutines.delay

class DelayTransformation(
    val delayTime: Long = 1000,
    val onTransform: (() -> Unit)? = null
) : Transformation {

    override val key: String
        get() = "DelayTransformation"

    override suspend fun transform(
        sketch: Sketch,
        request: ImageRequest,
        input: Bitmap
    ): TransformResult? {
        onTransform?.invoke()
        delay(delayTime)
        return null
    }
}