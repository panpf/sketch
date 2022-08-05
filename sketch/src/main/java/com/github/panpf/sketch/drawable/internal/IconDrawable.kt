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
package com.github.panpf.sketch.drawable.internal

import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.PorterDuff.Mode
import android.graphics.Rect
import android.graphics.Region
import android.graphics.drawable.Drawable
import android.graphics.drawable.Drawable.Callback
import android.os.Build
import android.os.Build.VERSION_CODES
import androidx.core.graphics.drawable.DrawableCompat

class IconDrawable constructor(
    val icon: Drawable,
    val bg: Drawable? = null,
) : Drawable(), Callback {

    init {
        bg?.callback = this
        icon.callback = this
    }

    override fun mutate(): IconDrawable {
        val newIcon = icon.mutate()
        val newBg = bg?.mutate()
        return if (newIcon !== icon || newBg !== bg) {
            IconDrawable(newIcon, newBg)
        } else {
            this
        }
    }

    override fun draw(canvas: Canvas) {
        bg?.draw(canvas)
        icon.draw(canvas)
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        bg?.bounds = bounds
        icon.apply {
            val iconWidth = icon.intrinsicWidth
            val iconHeight = icon.intrinsicHeight
            val left = bounds.left + (bounds.width() - iconWidth) / 2
            val top = bounds.top + (bounds.height() - iconHeight) / 2
            setBounds(left, top, left + iconWidth, top + iconHeight)
        }
    }

    override fun setChangingConfigurations(configs: Int) {
        bg?.changingConfigurations = configs
        icon.changingConfigurations = configs
    }

    override fun getChangingConfigurations(): Int {
        return icon.changingConfigurations
    }

    override fun isFilterBitmap(): Boolean {
        return Build.VERSION.SDK_INT >= VERSION_CODES.M
                && (icon.isFilterBitmap || bg?.isFilterBitmap == true)
    }

    override fun setFilterBitmap(filter: Boolean) {
        bg?.isFilterBitmap = filter
        icon.isFilterBitmap = filter
    }

    override fun setAlpha(alpha: Int) {
        bg?.alpha = alpha
        icon.alpha = alpha
    }

    override fun getAlpha(): Int {
        return DrawableCompat.getAlpha(icon)
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        bg?.colorFilter = colorFilter
        icon.colorFilter = colorFilter
    }

    @Deprecated("Deprecated in Java")
    override fun setColorFilter(color: Int, mode: Mode) {
        @Suppress("DEPRECATION")
        bg?.setColorFilter(color, mode)
        @Suppress("DEPRECATION")
        icon.setColorFilter(color, mode)
    }

    override fun getColorFilter(): ColorFilter? {
        return DrawableCompat.getColorFilter(icon)
    }

    @Deprecated("Deprecated in Java")
    override fun getOpacity(): Int {
        @Suppress("DEPRECATION")
        return icon.opacity.takeIf { it != PixelFormat.OPAQUE }
            ?: bg?.opacity.takeIf { it != PixelFormat.OPAQUE }
            ?: PixelFormat.OPAQUE
    }


    override fun isStateful(): Boolean {
        return bg?.isStateful == true || icon.isStateful
    }

    override fun setState(stateSet: IntArray): Boolean {
        val result1 = bg?.setState(stateSet) == true
        val result2 = icon.setState(stateSet)
        return result1 || result2
    }

    override fun getState(): IntArray {
        return icon.state
    }

    override fun jumpToCurrentState() {
        bg?.jumpToCurrentState()
        icon.jumpToCurrentState()
    }

    override fun setVisible(visible: Boolean, restart: Boolean): Boolean {
        super.setVisible(visible, restart)
        val result1 = bg?.setVisible(visible, restart) == true
        val result2 = icon.setVisible(visible, restart)
        return result1 || result2
    }

    override fun getTransparentRegion(): Region? {
        return bg?.transparentRegion ?: icon.transparentRegion
    }

    override fun getPadding(padding: Rect): Boolean {
        return bg?.getPadding(padding) == true
    }

    override fun onLevelChange(level: Int): Boolean {
        val result1 = bg?.setLevel(level) == true
        val result2 = icon.setLevel(level)
        return result1 || result2
    }

    override fun setAutoMirrored(mirrored: Boolean) {
        bg?.let { DrawableCompat.setAutoMirrored(it, mirrored) }
        DrawableCompat.setAutoMirrored(icon, mirrored)
    }

    override fun isAutoMirrored(): Boolean {
        return bg?.let { DrawableCompat.isAutoMirrored(it) } == true
                || DrawableCompat.isAutoMirrored(icon)
    }

    override fun setTint(tint: Int) {
        bg?.let { DrawableCompat.setTint(it, tint) }
        DrawableCompat.setTint(icon, tint)
    }

    override fun setTintList(tint: ColorStateList?) {
        bg?.let { DrawableCompat.setTintList(it, tint) }
        DrawableCompat.setTintList(icon, tint)
    }

    override fun setTintMode(tintMode: Mode?) {
        bg?.let { DrawableCompat.setTintMode(it, tintMode!!) }
        DrawableCompat.setTintMode(icon, tintMode!!)
    }

    override fun setHotspot(x: Float, y: Float) {
        bg?.let { DrawableCompat.setHotspot(it, x, y) }
        DrawableCompat.setHotspot(icon, x, y)
    }

    override fun setHotspotBounds(left: Int, top: Int, right: Int, bottom: Int) {
        bg?.let { DrawableCompat.setHotspotBounds(it, left, top, right, bottom) }
        DrawableCompat.setHotspotBounds(icon, left, top, right, bottom)
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

