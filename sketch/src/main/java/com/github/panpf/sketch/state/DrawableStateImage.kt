/*
 * Copyright (C) 2019 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.state

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.github.panpf.sketch.SketchView
import com.github.panpf.sketch.drawable.SketchShapeBitmapDrawable
import com.github.panpf.sketch.request.DisplayOptions
import com.github.panpf.sketch.request.ShapeSize
import com.github.panpf.sketch.shaper.ImageShaper

/**
 * 给什么图片显示什么图片，支持 [ShapeSize] 和 [ImageShaper]
 */
class DrawableStateImage : StateImage {

    var originDrawable: Drawable? = null
        private set
    var resId = -1
        private set

    constructor(drawable: Drawable) {
        originDrawable = drawable
    }

    constructor(resId: Int) {
        this.resId = resId
    }

    override fun getDrawable(
        context: Context,
        sketchView: SketchView,
        displayOptions: DisplayOptions
    ): Drawable? {
        var drawable = originDrawable
        if (drawable == null && resId != -1) {
            drawable = context.resources.getDrawable(resId)
        }
        val shapeSize = displayOptions.shapeSize
        val imageShaper = displayOptions.shaper
        if ((shapeSize != null || imageShaper != null) && drawable is BitmapDrawable) {
            drawable = SketchShapeBitmapDrawable(
                context,
                (drawable as BitmapDrawable?)!!,
                shapeSize,
                imageShaper
            )
        }
        return drawable
    }
}