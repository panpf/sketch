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
package com.github.panpf.sketch.drawable

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
import com.github.panpf.sketch.drawable.internal.SketchDrawable
import com.github.panpf.sketch.drawable.internal.calculateFitBounds
import com.github.panpf.sketch.drawable.internal.toLogString
import com.github.panpf.sketch.util.Size

/**
 * It consists of two parts: icon and bg. bg is scaled to fill bounds, the icon size is unchanged always centered.
 * It is suitable for use as a placeholder image for waterfall flow.
 */
class IconDrawable constructor(
    val icon: Drawable,
    val background: Drawable? = null,
    val iconSize: Size? = null,
    // TODO iconTint
) : Drawable(), Callback, SketchDrawable {

    init {
        background?.callback = this
        icon.callback = this
    }

    override fun getIntrinsicWidth(): Int {
        return -1
    }

    override fun getIntrinsicHeight(): Int {
        return -1
    }

    override fun mutate(): IconDrawable {
        val newIcon = icon.mutate()
        val newBackground = background?.mutate()
        return if (newIcon !== icon || newBackground !== background) {
            IconDrawable(newIcon, newBackground, iconSize)
        } else {
            this
        }
    }

    override fun draw(canvas: Canvas) {
        background?.draw(canvas)
        icon.draw(canvas)
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        background?.bounds = bounds
        val contentSize = iconSize ?: Size(icon.intrinsicWidth, icon.intrinsicHeight)
        val iconBounds = calculateFitBounds(contentSize, bounds)
        icon.bounds = iconBounds
    }

    override fun setChangingConfigurations(configs: Int) {
        background?.changingConfigurations = configs
        icon.changingConfigurations = configs
    }

    override fun getChangingConfigurations(): Int {
        return icon.changingConfigurations
    }

    override fun isFilterBitmap(): Boolean {
        return Build.VERSION.SDK_INT >= VERSION_CODES.M
                && (icon.isFilterBitmap || background?.isFilterBitmap == true)
    }

    override fun setFilterBitmap(filter: Boolean) {
        background?.isFilterBitmap = filter
        icon.isFilterBitmap = filter
    }

    override fun setAlpha(alpha: Int) {
        background?.alpha = alpha
        icon.alpha = alpha
    }

    override fun getAlpha(): Int {
        return DrawableCompat.getAlpha(icon)
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        background?.colorFilter = colorFilter
        icon.colorFilter = colorFilter
    }

    @Deprecated("Deprecated in Java")
    override fun setColorFilter(color: Int, mode: Mode) {
        @Suppress("DEPRECATION")
        background?.setColorFilter(color, mode)
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
            ?: background?.opacity.takeIf { it != PixelFormat.OPAQUE }
            ?: PixelFormat.OPAQUE
    }


    override fun isStateful(): Boolean {
        return background?.isStateful == true || icon.isStateful
    }

    override fun setState(stateSet: IntArray): Boolean {
        val result1 = background?.setState(stateSet) == true
        val result2 = icon.setState(stateSet)
        return result1 || result2
    }

    override fun getState(): IntArray {
        return icon.state
    }

    override fun jumpToCurrentState() {
        background?.jumpToCurrentState()
        icon.jumpToCurrentState()
    }

    override fun setVisible(visible: Boolean, restart: Boolean): Boolean {
        super.setVisible(visible, restart)
        val result1 = background?.setVisible(visible, restart) == true
        val result2 = icon.setVisible(visible, restart)
        return result1 || result2
    }

    override fun getTransparentRegion(): Region? {
        return background?.transparentRegion ?: icon.transparentRegion
    }

    override fun getPadding(padding: Rect): Boolean {
        return background?.getPadding(padding) == true
    }

    override fun onLevelChange(level: Int): Boolean {
        val result1 = background?.setLevel(level) == true
        val result2 = icon.setLevel(level)
        return result1 || result2
    }

    override fun setAutoMirrored(mirrored: Boolean) {
        background?.let { DrawableCompat.setAutoMirrored(it, mirrored) }
        DrawableCompat.setAutoMirrored(icon, mirrored)
    }

    override fun isAutoMirrored(): Boolean {
        return background?.let { DrawableCompat.isAutoMirrored(it) } == true
                || DrawableCompat.isAutoMirrored(icon)
    }

    override fun setTint(tint: Int) {
        background?.let { DrawableCompat.setTint(it, tint) }
        DrawableCompat.setTint(icon, tint)
    }

    override fun setTintList(tint: ColorStateList?) {
        background?.let { DrawableCompat.setTintList(it, tint) }
        DrawableCompat.setTintList(icon, tint)
    }

    override fun setTintMode(tintMode: Mode?) {
        background?.let { DrawableCompat.setTintMode(it, tintMode!!) }
        DrawableCompat.setTintMode(icon, tintMode!!)
    }

    override fun setHotspot(x: Float, y: Float) {
        background?.let { DrawableCompat.setHotspot(it, x, y) }
        DrawableCompat.setHotspot(icon, x, y)
    }

    override fun setHotspotBounds(left: Int, top: Int, right: Int, bottom: Int) {
        background?.let { DrawableCompat.setHotspotBounds(it, left, top, right, bottom) }
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is IconDrawable) return false
        if (icon != other.icon) return false
        if (background != other.background) return false
        return iconSize == other.iconSize
    }

    override fun hashCode(): Int {
        var result = icon.hashCode()
        result = 31 * result + (background?.hashCode() ?: 0)
        result = 31 * result + (iconSize?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "IconDrawable(icon=${icon.toLogString()}, background=${background?.toLogString()}, iconSize=$iconSize)"
    }
}