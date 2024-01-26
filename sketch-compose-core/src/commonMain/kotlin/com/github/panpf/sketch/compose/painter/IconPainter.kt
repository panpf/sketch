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

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.compose.painter.internal.SketchPainter
import com.github.panpf.sketch.compose.painter.internal.toLogString

/**
 * It consists of two parts: icon and bg. bg is scaled to fill bounds, the icon size is unchanged always centered.
 * It is suitable for use as a placeholder image for waterfall flow.
 */
class IconPainter constructor(
    val icon: Painter,
    val background: Painter? = null,
    val iconSize: Size? = null,
    val iconTint: Color? = null,
) : Painter(), SketchPainter {

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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
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
        return "IconPainter(icon=${icon.toLogString()}, background=${background?.toLogString()}, iconSize=$iconSize, iconTint=$iconTint)"
    }
}