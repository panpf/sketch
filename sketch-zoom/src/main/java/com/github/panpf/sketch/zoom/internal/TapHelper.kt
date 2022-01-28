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
package com.github.panpf.sketch.zoom.internal

import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import com.github.panpf.sketch.util.format

internal class TapHelper(appContext: Context, private val imageZoomer: ImageZoomer) :
    SimpleOnGestureListener() {

    private val tapGestureDetector: GestureDetector = GestureDetector(appContext, this)

    fun onTouchEvent(event: MotionEvent): Boolean {
        return tapGestureDetector.onTouchEvent(event)
    }

    override fun onDown(e: MotionEvent): Boolean {
        return true
    }

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        val imageView = imageZoomer.getImageView()
        imageView.performClick()
//        val tapListener = imageZoomer.onViewTapListener
//        if (tapListener != null) {
//            tapListener.onViewTap(imageView, e.x, e.y)
//            return true
//        }
//        if (imageView is FunctionCallbackView) {
//            val clickListener = imageView.getOnClickListener()
//            if (imageView.isClickable) {
//                clickListener.onClick(imageView)
//                return true
//            }
//        }
        return false
    }

    override fun onLongPress(e: MotionEvent) {
        super.onLongPress(e)
        val imageView = imageZoomer.getImageView()
        imageView.performLongClick()
//        val longPressListener = imageZoomer.onViewLongPressListener
//        if (longPressListener != null) {
//            longPressListener.onViewLongPress(imageView, e.x, e.y)
//            return
//        }
//        if (imageView is FunctionCallbackView) {
//            val longClickListener = imageView.getOnLongClickListener()
//            if (longClickListener != null && imageView.isLongClickable) {
//                longClickListener.onLongClick(imageView)
//            }
//        }
    }

    override fun onDoubleTap(ev: MotionEvent): Boolean {
        try {
            val currentScaleFormat = imageZoomer.zoomScale.format(2)
            var finalScale = -1f
            for (scale in imageZoomer.getZoomScales().zoomScales!!) {
                if (finalScale == -1f) {
                    finalScale = scale
                } else if (currentScaleFormat < scale.format(2)) {
                    finalScale = scale
                    break
                }
            }
            imageZoomer.zoom(finalScale, true)
        } catch (e: ArrayIndexOutOfBoundsException) {
            // Can sometimes happen when getX() and getY() is called
        }
        return true
    }
}