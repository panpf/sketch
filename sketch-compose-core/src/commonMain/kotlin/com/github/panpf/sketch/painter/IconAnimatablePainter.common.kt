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
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter

/**
 * Create an [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.common.test.painter.IconAnimatablePainterTest.testRememberIconAnimatablePainter
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitablePainter,
    background: EquitablePainter? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainter = remember(icon, background, iconSize, iconTint) {
    IconAnimatablePainter(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
}

/**
 * Create an [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.common.test.painter.IconAnimatablePainterTest.testRememberIconAnimatablePainter
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitablePainter,
    background: Color? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainter = remember(icon, background, iconSize, iconTint) {
    val backgroundPainter = background?.let { ColorPainter(it) }
    IconAnimatablePainter(
        icon = icon,
        background = backgroundPainter?.asEquitable(),
        iconSize = iconSize,
        iconTint = iconTint
    )
}

/**
 * Create an [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.common.test.painter.IconAnimatablePainterTest.testRememberIconAnimatablePainter
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitablePainter,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainter = remember(icon, iconSize, iconTint) {
    IconAnimatablePainter(
        icon = icon,
        background = null,
        iconSize = iconSize,
        iconTint = iconTint
    )
}


/**
 * An [IconPainter] that can be animated.
 *
 * @see com.github.panpf.sketch.compose.core.common.test.painter.IconAnimatablePainterTest
 */
@Stable
class IconAnimatablePainter constructor(
    icon: EquitablePainter,
    background: EquitablePainter? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
) : IconPainter(icon, background, iconSize, iconTint), AnimatablePainter {

    private val animatablePainterIcon: AnimatablePainter?
    private val animatablePainterBackground: AnimatablePainter?

    init {
        require(icon is AnimatablePainter) {
            "icon must be AnimatablePainter"
        }
        require(background == null || background !is AnimatablePainter) {
            "background can't be Animatable"
        }
        @Suppress("USELESS_CAST")
        animatablePainterIcon = icon as? AnimatablePainter
        animatablePainterBackground = background as? AnimatablePainter
    }

    override fun start() {
        animatablePainterIcon?.start()
        animatablePainterBackground?.start()
    }

    override fun stop() {
        animatablePainterIcon?.stop()
        animatablePainterBackground?.stop()
    }

    override fun isRunning(): Boolean {
        return animatablePainterIcon?.isRunning() == true || animatablePainterBackground?.isRunning() == true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as IconAnimatablePainter
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
        return "IconAnimatablePainter(icon=${icon}, background=${background}, iconSize=$iconSize, iconTint=$iconTint)"
    }
}