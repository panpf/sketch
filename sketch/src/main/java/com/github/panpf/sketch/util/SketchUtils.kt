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
package com.github.panpf.sketch.util

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.view.View
import com.github.panpf.sketch.R
import com.github.panpf.sketch.drawable.SketchCountBitmapDrawable
import com.github.panpf.sketch.drawable.SketchDrawable
import com.github.panpf.sketch.drawable.internal.CrossfadeDrawable
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.request.internal.ViewTargetRequestManager

class SketchUtils private constructor() {

    companion object {

        internal fun requestManagerOrNull(view: View): ViewTargetRequestManager? =
            view.getTag(R.id.sketch_request_manager) as ViewTargetRequestManager?

        /**
         * Dispose the request that's attached to this view (if there is one).
         */
        fun dispose(view: View) = requestManagerOrNull(view)?.dispose()

        /**
         * Get the [DisplayResult] of the most recently executed image request that's attached to this view.
         */
        fun getResult(view: View): DisplayResult? = requestManagerOrNull(view)?.getResult()

        /**
         * Restart ImageRequest
         */
        fun restart(view: View) = requestManagerOrNull(view)?.restart()
    }
}

/**
 * Find the last [SketchDrawable] from the specified Drawable
 */
@SuppressLint("RestrictedApi")
fun Drawable.findLastSketchDrawable(): SketchDrawable? {
    val drawable = this
    return when {
        drawable is SketchDrawable -> drawable
        drawable is CrossfadeDrawable -> {
            drawable.end?.findLastSketchDrawable()
        }
        drawable is LayerDrawable -> {
            drawable.getLastChildDrawable()?.findLastSketchDrawable()
        }
        drawable is androidx.appcompat.graphics.drawable.DrawableWrapper -> {
            drawable.wrappedDrawable?.findLastSketchDrawable()
        }
        VERSION.SDK_INT >= VERSION_CODES.M && drawable is android.graphics.drawable.DrawableWrapper -> {
            drawable.drawable?.findLastSketchDrawable()
        }
        else -> null
    }
}

/**
 * Traverse all SketchCountBitmapDrawable in specified Drawable
 */
@SuppressLint("RestrictedApi")
fun Drawable.iterateSketchCountBitmapDrawable(block: (SketchCountBitmapDrawable) -> Unit) {
    val drawable = this
    when {
        drawable is SketchCountBitmapDrawable -> {
            block(drawable)
        }
        drawable is LayerDrawable -> {
            val layerCount = drawable.numberOfLayers
            for (index in 0 until layerCount) {
                drawable.getDrawable(index).iterateSketchCountBitmapDrawable(block)
            }
        }
        drawable is CrossfadeDrawable -> {
            drawable.start?.iterateSketchCountBitmapDrawable(block)
            drawable.end?.iterateSketchCountBitmapDrawable(block)
        }
        drawable is androidx.appcompat.graphics.drawable.DrawableWrapper -> {
            drawable.wrappedDrawable?.iterateSketchCountBitmapDrawable(block)
        }
        VERSION.SDK_INT >= VERSION_CODES.M && drawable is android.graphics.drawable.DrawableWrapper -> {
            drawable.drawable?.iterateSketchCountBitmapDrawable(block)
        }
    }
}