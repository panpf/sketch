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
import android.graphics.Paint
import android.graphics.PorterDuff.Mode.SRC_IN
import android.graphics.PorterDuffXfermode
import androidx.annotation.WorkerThread
import com.github.panpf.sketch.BitmapImage
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.getBitmapOrNull
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.resize.internal.calculateResizeMapping
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.asOrNull
import com.github.panpf.sketch.util.safeConfig
import com.github.panpf.sketch.util.toAndroidRect
import java.lang.Integer.min

internal actual fun circleCropTransformation(image: Image,  scale: Scale): Image? {
    val inputBitmap = image.asOrNull<BitmapImage>()?.bitmap ?: return null
    val newSize = min(inputBitmap.width, inputBitmap.height)
    val resizeMapping = calculateResizeMapping(
        inputBitmap.width, inputBitmap.height, newSize, newSize, SAME_ASPECT_RATIO, scale
    )
    val config = inputBitmap.safeConfig
    val outBitmap = Bitmap.createBitmap(
        /* width = */ resizeMapping.newWidth,
        /* height = */ resizeMapping.newHeight,
        /* config = */ config,
    )
    val paint = Paint().apply {
        isAntiAlias = true
        color = -0x10000
    }
    val canvas = Canvas(outBitmap).apply {
        drawARGB(0, 0, 0, 0)
    }
    canvas.drawCircle(
        /* cx = */ resizeMapping.newWidth / 2f,
        /* cy = */ resizeMapping.newHeight / 2f,
        /* radius = */ min(resizeMapping.newWidth, resizeMapping.newHeight) / 2f,
        /* paint = */ paint
    )
    paint.xfermode = PorterDuffXfermode(SRC_IN)
    canvas.drawBitmap(
        /* bitmap = */ inputBitmap,
        /* src = */ resizeMapping.srcRect.toAndroidRect(),
        /* dst = */ resizeMapping.destRect.toAndroidRect(),
        /* paint = */ paint
    )
    return outBitmap.asSketchImage()
}