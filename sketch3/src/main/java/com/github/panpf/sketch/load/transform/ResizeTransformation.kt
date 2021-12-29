package com.github.panpf.sketch.load.transform

import android.graphics.Bitmap
import com.github.panpf.sketch.load.Resize
import com.github.panpf.sketch.load.internal.LoadableRequest

class ResizeTransformation(val resize: Resize) : Transformation {
    override val cacheKey: String
        get() = TODO("Not yet implemented")

    override suspend fun transform(request: LoadableRequest, input: Bitmap): Bitmap {
        TODO("Not yet implemented")
    }
}