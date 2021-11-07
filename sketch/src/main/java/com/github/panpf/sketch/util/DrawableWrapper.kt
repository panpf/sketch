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
package com.github.panpf.sketch.util

import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Build

open class DrawableWrapper(drawable: Drawable?) : Drawable(), Drawable.Callback {

    var wrappedDrawable: Drawable? = drawable
        set(value) {
            if (value !== this@DrawableWrapper) {
                field?.callback = null
                value?.callback = this
                field = value
            }
        }

    override fun draw(canvas: Canvas) {
        wrappedDrawable?.draw(canvas)
    }

    override fun onBoundsChange(bounds: Rect) {
        wrappedDrawable?.bounds = bounds
    }

    override fun getChangingConfigurations(): Int {
        return wrappedDrawable?.changingConfigurations ?: super.getChangingConfigurations()
    }

    override fun setChangingConfigurations(configs: Int) {
        wrappedDrawable?.changingConfigurations = configs
    }

    override fun setDither(dither: Boolean) {
        wrappedDrawable?.setDither(dither)
    }

    override fun setFilterBitmap(filter: Boolean) {
        wrappedDrawable?.isFilterBitmap = filter
    }

    override fun setAlpha(alpha: Int) {
        wrappedDrawable?.alpha = alpha
    }

    override fun setColorFilter(cf: ColorFilter) {
        wrappedDrawable?.colorFilter = cf
    }

    override fun isStateful(): Boolean {
        return wrappedDrawable?.isStateful ?: super.isStateful()
    }

    override fun setState(stateSet: IntArray): Boolean {
        return wrappedDrawable?.setState(stateSet) ?: super.setState(stateSet)
    }

    override fun getState(): IntArray {
        return wrappedDrawable?.state ?: super.getState()
    }

    override fun jumpToCurrentState() {
        wrappedDrawable?.jumpToCurrentState()
    }

    override fun getCurrent(): Drawable {
        return wrappedDrawable?.current ?: super.getCurrent()
    }

    override fun setVisible(visible: Boolean, restart: Boolean): Boolean {
        return super.setVisible(
            visible,
            restart
        ) || wrappedDrawable?.setVisible(visible, restart) == true
    }

    override fun getOpacity(): Int {
        return wrappedDrawable?.opacity ?: PixelFormat.UNKNOWN
    }

    override fun getTransparentRegion(): Region? {
        return wrappedDrawable?.transparentRegion ?: super.getTransparentRegion()
    }

    override fun getIntrinsicWidth(): Int {
        return wrappedDrawable?.intrinsicWidth ?: super.getIntrinsicWidth()
    }

    override fun getIntrinsicHeight(): Int {
        return wrappedDrawable?.intrinsicHeight ?: super.getIntrinsicHeight()
    }

    override fun getMinimumWidth(): Int {
        return wrappedDrawable?.minimumWidth ?: super.getMinimumWidth()
    }

    override fun getMinimumHeight(): Int {
        return wrappedDrawable?.minimumHeight ?: super.getMinimumHeight()
    }

    override fun getPadding(padding: Rect): Boolean {
        return wrappedDrawable?.getPadding(padding) ?: super.getPadding(
            padding
        )
    }

    override fun onLevelChange(level: Int): Boolean {
        return wrappedDrawable?.setLevel(level) ?: super.onLevelChange(
            level
        )
    }

    override fun isAutoMirrored(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && wrappedDrawable?.isAutoMirrored == true
    }

    override fun setAutoMirrored(mirrored: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            wrappedDrawable?.isAutoMirrored = mirrored
        }
    }

    override fun setTint(tint: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            wrappedDrawable?.setTint(tint)
        }
    }

    override fun setTintList(tint: ColorStateList) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            wrappedDrawable?.setTintList(tint)
        }
    }

    override fun setTintMode(tintMode: PorterDuff.Mode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            wrappedDrawable?.setTintMode(tintMode)
        }
    }

    override fun setHotspot(x: Float, y: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            wrappedDrawable?.setHotspot(x, y)
        }
    }

    override fun setHotspotBounds(left: Int, top: Int, right: Int, bottom: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            wrappedDrawable?.setHotspotBounds(left, top, right, bottom)
        }
    }

    override fun invalidateDrawable(who: Drawable) {
        invalidateSelf()
    }

    override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {
        scheduleSelf(what, `when`)
    }

    override fun unscheduleDrawable(who: Drawable, what: Runnable) {
        unscheduleSelf(what)
    }
}