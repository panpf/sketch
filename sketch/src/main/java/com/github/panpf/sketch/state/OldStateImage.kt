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
import com.github.panpf.sketch.drawable.SketchLoadingDrawable
import com.github.panpf.sketch.drawable.SketchShapeBitmapDrawable
import com.github.panpf.sketch.request.DisplayOptions
import com.github.panpf.sketch.util.SketchUtils

/**
 * 使用当前 [ImageView] 正在显示的图片作为状态图片
 */
class OldStateImage : StateImage {
    private var whenEmptyImage: StateImage? = null

    constructor(whenEmptyImage: StateImage?) {
        this.whenEmptyImage = whenEmptyImage
    }

    constructor()

    override fun getDrawable(
        context: Context,
        sketchView: SketchView,
        displayOptions: DisplayOptions
    ): Drawable? {
        var drawable = SketchUtils.getLastDrawable(sketchView.getDrawable())
        if (drawable is SketchLoadingDrawable) {
            drawable = drawable.wrappedDrawable
        }
        if (drawable != null) {
            val shapeSize = displayOptions.shapeSize
            val imageShaper = displayOptions.shaper
            if (shapeSize != null || imageShaper != null) {
                if (drawable is SketchShapeBitmapDrawable) {
                    drawable = SketchShapeBitmapDrawable(
                        context,
                        drawable.bitmapDrawable,
                        shapeSize,
                        imageShaper
                    )
                } else if (drawable is BitmapDrawable) {
                    drawable = SketchShapeBitmapDrawable(
                        context,
                        (drawable as BitmapDrawable?)!!,
                        shapeSize,
                        imageShaper
                    )
                }
            }
        }
        if (drawable == null && whenEmptyImage != null) {
            drawable = whenEmptyImage!!.getDrawable(context, sketchView, displayOptions)
        }
        return drawable
    }
}