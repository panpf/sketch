/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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

@file:Suppress("RedundantConstructorKeyword")

package com.github.panpf.sketch.painter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.util.Rect
import com.github.panpf.sketch.util.calculateCropBounds
import com.github.panpf.sketch.util.calculateInsideBounds
import com.github.panpf.sketch.util.size
import com.github.panpf.sketch.util.toIntSizeOrNull
import com.github.panpf.sketch.util.toSize
import com.github.panpf.sketch.util.toSketchSize

/**
 * Create a [IconPainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.common.test.painter.IconPainterTest.testRememberIconPainter
 */
@Composable
fun rememberIconPainter(
    icon: EquitablePainter,
    background: EquitablePainter? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconPainter = remember(icon, background, iconSize, iconTint) {
    IconPainter(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
}

/**
 * Create a [IconPainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.common.test.painter.IconPainterTest.testRememberIconPainter
 */
@Composable
fun rememberIconPainter(
    icon: EquitablePainter,
    background: Color? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconPainter = remember(icon, background, iconSize, iconTint) {
    val backgroundPainter = background?.let { ColorPainter(it) }
    IconPainter(
        icon = icon,
        background = backgroundPainter?.asEquitable(),
        iconSize = iconSize,
        iconTint = iconTint
    )
}

/**
 * Create a [IconPainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.common.test.painter.IconPainterTest.testRememberIconPainter
 */
@Composable
fun rememberIconPainter(
    icon: EquitablePainter,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconPainter = remember(icon, iconSize, iconTint) {
    IconPainter(
        icon = icon,
        background = null,
        iconSize = iconSize,
        iconTint = iconTint
    )
}


/**
 * It consists of two parts: icon and bg. bg is scaled to fill bounds, the icon size is unchanged always centered.
 * It is suitable for use as a placeholder image for waterfall flow.
 *
 * @see com.github.panpf.sketch.compose.core.common.test.painter.IconPainterTest
 */
@Stable
open class IconPainter constructor(
    val icon: EquitablePainter,
    val background: EquitablePainter? = null,
    val iconSize: Size? = null,
    val iconTint: Color? = null,
) : Painter(), RememberObserver, SketchPainter {

    private var alpha: Float = 1.0f
    private var colorFilter: ColorFilter? = null

    override val intrinsicSize: Size = Size.Unspecified

    init {
        if (iconSize != null) {
            require(!iconSize.isEmpty()) {
                "iconSize must be not empty"
            }
        } else {
            require(!icon.intrinsicSize.isEmpty()) {
                "When iconSize is not set, icon's size must be not empty"
            }
        }
    }

    override fun DrawScope.onDraw() {
        (icon as? PainterDrawInvalidate)?.drawInvalidateTick?.value
        (background as? PainterDrawInvalidate)?.drawInvalidateTick?.value

        val containerSize = this@onDraw.size
        val containerBounds = Rect(0, 0, containerSize.width.toInt(), containerSize.height.toInt())
        if (background != null) {
            val backgroundSize = background.intrinsicSize
            val backgroundBounds = if (backgroundSize.isSpecified) {
                calculateCropBounds(
                    contentSize = backgroundSize.toIntSizeOrNull()!!.toSketchSize(),
                    containerBounds = containerBounds
                )
            } else {
                containerBounds
            }
            translate(
                left = backgroundBounds.left.toFloat(),
                top = backgroundBounds.top.toFloat()
            ) {
                with(background) {
                    draw(
                        size = backgroundBounds.size.toSize(),
                        colorFilter = colorFilter
                    )
                }
            }
        }

        val realIconSize = iconSize ?: icon.intrinsicSize
        val iconBounds = calculateInsideBounds(
            contentSize = realIconSize.toIntSizeOrNull()!!.toSketchSize(),
            containerBounds = containerBounds
        )
        translate(left = iconBounds.left.toFloat(), top = iconBounds.top.toFloat()) {
            with(icon) {
                val filter = iconTint?.let { ColorFilter.tint(it) } ?: colorFilter
                draw(
                    size = iconBounds.size.toSize(),
                    colorFilter = filter
                )
            }
        }
    }

    override fun applyAlpha(alpha: Float): Boolean {
        this.alpha = alpha
        return true
    }

    override fun applyColorFilter(colorFilter: ColorFilter?): Boolean {
        this.colorFilter = colorFilter
        return true
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
        if (other == null || this::class != other::class) return false
        other as IconPainter
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
        return "IconPainter(icon=${icon}, background=${background}, iconSize=$iconSize, iconTint=$iconTint)"
    }
}