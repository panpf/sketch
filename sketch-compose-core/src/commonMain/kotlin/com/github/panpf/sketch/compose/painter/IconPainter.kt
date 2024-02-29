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
package com.github.panpf.sketch.compose.painter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.compose.painter.internal.SketchPainter
import com.github.panpf.sketch.compose.painter.internal.toLogString

@Composable
fun rememberIconPainter(
    icon: Painter,
    background: Painter? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconPainter {
    return remember(icon, background, iconSize, iconTint) {
        IconPainter(icon, background, iconSize, iconTint)
    }
}

@Composable
fun rememberIconPainter(
    icon: Painter,
    background: Color? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconPainter {
    return remember(icon, background, iconSize, iconTint) {
        val backgroundPainter = background?.let { ColorPainter(it) }
        IconPainter(icon, backgroundPainter, iconSize, iconTint)
    }
}

@Composable
fun rememberIconAnimatablePainter(
    icon: Painter,
    background: Painter? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainter {
    return remember(icon, background, iconSize, iconTint) {
        IconAnimatablePainter(icon, background, iconSize, iconTint)
    }
}

@Composable
fun rememberIconAnimatablePainter(
    icon: Painter,
    background: Color? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainter {
    return remember(icon, background, iconSize, iconTint) {
        val backgroundPainter = background?.let { ColorPainter(it) }
        IconAnimatablePainter(icon, backgroundPainter, iconSize, iconTint)
    }
}

/**
 * It consists of two parts: icon and bg. bg is scaled to fill bounds, the icon size is unchanged always centered.
 * It is suitable for use as a placeholder image for waterfall flow.
 */
open class IconPainter constructor(
    val icon: Painter,
    val background: Painter? = null,
    val iconSize: Size? = null,
    val iconTint: Color? = null,
) : Painter(), RememberObserver, SketchPainter {

    private var alpha: Float = 1.0f
    private var colorFilter: ColorFilter? = null

    override fun applyAlpha(alpha: Float): Boolean {
        this.alpha = alpha
        return true
    }

    override fun applyColorFilter(colorFilter: ColorFilter?): Boolean {
        this.colorFilter = colorFilter
        return true
    }

    override val intrinsicSize: Size = Size.Unspecified

    init {
        require(icon.intrinsicSize.isSpecified) {
            "icon's intrinsicSize must be specified"
        }
        require(background == null || background.intrinsicSize.isUnspecified) {
            "background's intrinsicSize must be unspecified"
        }
        require(iconSize == null || iconSize.isSpecified) {
            "iconSize must be specified"
        }
    }

    override fun DrawScope.onDraw() {
        val icon = icon
        val background = background
        (icon as? DrawInvalidate)?.drawInvalidateTick?.value
        (background as? DrawInvalidate)?.drawInvalidateTick?.value

        if (background != null) {
            with(background) {
                draw(size = this@onDraw.size, alpha = alpha, colorFilter = colorFilter)
            }
        }

        val finalIconSize = iconSize ?: icon.intrinsicSize
        translate(
            (size.width - finalIconSize.width) / 2,
            (size.height - finalIconSize.height) / 2
        ) {
            with(icon) {
                val filter = iconTint?.let { ColorFilter.tint(it) }
                draw(size = finalIconSize, colorFilter = filter)
            }
        }
    }

    override fun onRemembered() {
        (icon as? RememberObserver)?.onRemembered()
        (background as? RememberObserver)?.onRemembered()
        (icon as? AnimatablePainter)?.start()
        (background as? AnimatablePainter)?.start()
    }

    override fun onAbandoned() = onForgotten()

    override fun onForgotten() {
        (icon as? AnimatablePainter)?.stop()
        (background as? AnimatablePainter)?.stop()
        (icon as? RememberObserver)?.onForgotten()
        (background as? RememberObserver)?.onForgotten()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is IconPainter) return false
        if (icon != other.icon) return false
        if (background != other.background) return false
        if (iconSize != other.iconSize) return false
        return iconTint == other.iconTint
    }

    override fun hashCode(): Int {
        var result = icon.hashCode()
        result = 31 * result + (background?.hashCode() ?: 0)
        result = 31 * result + (iconSize?.hashCode() ?: 0)
        result = 31 * result + (iconTint?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "IconPainter(icon=${icon.toLogString()}, background=${background?.toLogString()}, iconSize=$iconSize, iconTint=$iconTint)"
    }
}

class IconAnimatablePainter(
    icon: Painter,
    background: Painter? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
) : IconPainter(icon, background, iconSize, iconTint), AnimatablePainter {

    private val animatableIcon: AnimatablePainter?
    private val animatableBackground: AnimatablePainter?

    init {
        require(icon is AnimatablePainter || background is AnimatablePainter) {
            "painter must be AnimatablePainter"
        }
        animatableIcon = icon as? AnimatablePainter
        animatableBackground = background as? AnimatablePainter
    }

    override fun start() {
        animatableIcon?.start()
        animatableBackground?.start()
    }

    override fun stop() {
        animatableIcon?.stop()
        animatableBackground?.stop()
    }

    override fun isRunning(): Boolean {
        return animatableIcon?.isRunning() == true || animatableBackground?.isRunning() == true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is IconAnimatablePainter) return false
        if (icon != other.icon) return false
        if (background != other.background) return false
        if (iconSize != other.iconSize) return false
        return iconTint == other.iconTint
    }

    override fun hashCode(): Int {
        var result = icon.hashCode()
        result = 31 * result + (background?.hashCode() ?: 0)
        result = 31 * result + (iconSize?.hashCode() ?: 0)
        result = 31 * result + (iconTint?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "IconAnimatablePainter(icon=${icon.toLogString()}, background=${background?.toLogString()}, iconSize=$iconSize, iconTint=$iconTint)"
    }
}