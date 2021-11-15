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
package com.github.panpf.sketch.zoom

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import com.github.panpf.sketch.viewfun.ViewFunction

class ImageZoomFunction internal constructor(private val zoomer: ImageZoomer) : ViewFunction() {

    override fun onAttachedToWindow() {
        zoomer.reset("onAttachedToWindow")
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        zoomer.onDraw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return zoomer.onTouchEvent(event)
    }

    override fun onDrawableChanged(
        callPosition: String,
        oldDrawable: Drawable?,
        newDrawable: Drawable?
    ): Boolean {
        zoomer.reset("onDrawableChanged")
        return false
    }

    override fun onSizeChanged(left: Int, top: Int, right: Int, bottom: Int) {
        zoomer.reset("onSizeChanged")
    }

    override fun onDetachedFromWindow(): Boolean {
        zoomer.recycle("onDetachedFromWindow")
        return false
    }
}