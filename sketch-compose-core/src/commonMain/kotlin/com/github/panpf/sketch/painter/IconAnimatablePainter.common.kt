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
 * Create a [IconAnimatablePainter] and remember it.
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
 * Create a [IconAnimatablePainter] and remember it.
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
    val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
    IconAnimatablePainter(
        icon = icon,
        background = backgroundPainter,
        iconSize = iconSize,
        iconTint = iconTint
    )
}


/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.common.test.painter.IconAnimatablePainterTest.testRememberIconAnimatablePainter
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitablePainter,
    background: EquitablePainter? = null,
    iconSize: Size? = null,
): IconAnimatablePainter = remember(icon, background, iconSize) {
    IconAnimatablePainter(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = null
    )
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.common.test.painter.IconAnimatablePainterTest.testRememberIconAnimatablePainter
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitablePainter,
    background: Color? = null,
    iconSize: Size? = null,
): IconAnimatablePainter = remember(icon, background, iconSize) {
    val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
    IconAnimatablePainter(
        icon = icon,
        background = backgroundPainter,
        iconSize = iconSize,
        iconTint = null
    )
}

/**
 * Create a [IconAnimatablePainter] and remember it.
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
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.common.test.painter.IconAnimatablePainterTest.testRememberIconAnimatablePainter
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitablePainter,
    background: EquitablePainter? = null,
): IconAnimatablePainter = remember(icon, background) {
    IconAnimatablePainter(
        icon = icon,
        background = background,
        iconSize = null,
        iconTint = null
    )
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.common.test.painter.IconAnimatablePainterTest.testRememberIconAnimatablePainter
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitablePainter,
    background: Color? = null,
): IconAnimatablePainter = remember(icon, background) {
    val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
    IconAnimatablePainter(
        icon = icon,
        background = backgroundPainter,
        iconSize = null,
        iconTint = null
    )
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.common.test.painter.IconAnimatablePainterTest.testRememberIconAnimatablePainter
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitablePainter,
    iconSize: Size? = null,
): IconAnimatablePainter = remember(icon, iconSize) {
    IconAnimatablePainter(
        icon = icon,
        background = null,
        iconSize = iconSize,
        iconTint = null
    )
}


/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.common.test.painter.IconAnimatablePainterTest.testRememberIconAnimatablePainter
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitablePainter,
): IconAnimatablePainter = remember(icon) {
    IconAnimatablePainter(
        icon = icon,
        background = null,
        iconSize = null,
        iconTint = null
    )
}

/**
 * An [IconAnimatablePainter] that can be animated.
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

    private val animatablePainterIcon: AnimatablePainter

    init {
        require(icon is AnimatablePainter) {
            "icon must be AnimatablePainter"
        }
        require(background == null || background !is AnimatablePainter) {
            "background can't be Animatable"
        }
        @Suppress("USELESS_CAST")
        animatablePainterIcon = icon as AnimatablePainter
    }

    override fun start() {
        animatablePainterIcon.start()
    }

    override fun stop() {
        animatablePainterIcon.stop()
    }

    override fun isRunning(): Boolean {
        return animatablePainterIcon.isRunning()
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
        // Because iconSize are value classes, they will be replaced by long.
        // Long will lose precision when converting hashCode, causing the hashCode generated by different srcOffset and srcSize to be the same.
        result = 31 * result + (iconSize?.toString()?.hashCode() ?: 0)
        result = 31 * result + (iconTint?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "IconAnimatablePainter(icon=${icon}, background=${background}, iconSize=$iconSize, iconTint=$iconTint)"
    }
}