/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.panpf.sketch.transform

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.getBitmapOrNull
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.util.safeConfig

/**
 * Bitmap Rotation Transformation
 */
class RotateTransformation(val degrees: Int) : Transformation {

    override val key: String = "RotateTransformation($degrees)"

    @WorkerThread
    override suspend fun transform(
        sketch: Sketch,
        requestContext: RequestContext,
        input: Image
    ): TransformResult? {
        val inputBitmap = input.getBitmapOrNull() ?: return null
        val matrix = Matrix().apply {
            setRotate(degrees.toFloat())
        }
        val newRect =
            RectF(0f, 0f, inputBitmap.width.toFloat(), inputBitmap.height.toFloat()).apply {
                matrix.mapRect(this)
            }
        val newWidth = newRect.width().toInt()
        val newHeight = newRect.height().toInt()

        // If the Angle is not divisible by 90°, the new image will be oblique, so support transparency so that the oblique part is not black
        var config = inputBitmap.safeConfig
        if (degrees % 90 != 0 && config != Bitmap.Config.ARGB_8888) {
            config = Bitmap.Config.ARGB_8888
        }
        // TODO BitmapPool
//        val result = sketch.bitmapPool.getOrCreate(
//            width = newWidth,
//            height = newHeight,
//            config = config,
//            disallowReuseBitmap = requestContext.request.disallowReuseBitmap,
//            caller = "RotateTransformation"
//        )
        val result = Bitmap.createBitmap(
            /* width = */ newWidth,
            /* height = */ newHeight,
            /* config = */ config,
        )
        matrix.postTranslate(-newRect.left, -newRect.top)
        val canvas = Canvas(result)
        val paint = Paint(Paint.DITHER_FLAG or Paint.FILTER_BITMAP_FLAG)
        canvas.drawBitmap(inputBitmap, matrix, paint)
        return TransformResult(result.asSketchImage(), createRotateTransformed(degrees))
    }

    override fun toString(): String = key

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as RotateTransformation
        if (degrees != other.degrees) return false
        return true
    }

    override fun hashCode(): Int {
        return degrees
    }
}

fun createRotateTransformed(degrees: Int) =
    "RotateTransformed($degrees)"

fun isRotateTransformed(transformed: String): Boolean =
    transformed.startsWith("RotateTransformed(")

fun List<String>.getRotateTransformed(): String? =
    find { isRotateTransformed(it) }