/*
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.panpf.sketch.zoom.new.scale

import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import com.github.panpf.sketch.util.format
import com.github.panpf.sketch.zoom.new.Zoomer

internal class NewTapHelper constructor(context: Context, private val zoomer: Zoomer) :
    SimpleOnGestureListener() {

    private val tapGestureDetector: GestureDetector = GestureDetector(context, this)

    fun onTouchEvent(event: MotionEvent): Boolean {
        return tapGestureDetector.onTouchEvent(event)
    }

    override fun onDown(e: MotionEvent): Boolean {
        return true
    }

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        return zoomer.onViewTapListener?.onViewTap(zoomer.view, e.x, e.y) != null
                || zoomer.view.performClick()
    }

    override fun onLongPress(e: MotionEvent) {
        super.onLongPress(e)
        zoomer.onViewLongPressListener?.onViewLongPress(zoomer.view, e.x, e.y)
            ?: zoomer.view.performLongClick()
    }

    override fun onDoubleTap(ev: MotionEvent): Boolean {
        try {
            val currentScaleFormat = zoomer.zoomScale.format(2)
            var finalScale = -1f
            for (scale in zoomer.zoomScales.zoomScales) {
                if (finalScale == -1f) {
                    finalScale = scale
                } else if (currentScaleFormat < scale.format(2)) {
                    finalScale = scale
                    break
                }
            }
            zoomer.zoom(finalScale, true)
        } catch (e: ArrayIndexOutOfBoundsException) {
            // Can sometimes happen when getX() and getY() is called
        }
        return true
    }
}