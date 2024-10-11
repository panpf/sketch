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

package com.github.panpf.sketch.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.painter.EquitablePainter
import com.github.panpf.sketch.painter.IconPainter
import com.github.panpf.sketch.painter.asEquitable
import com.github.panpf.sketch.request.ImageRequest

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.common.test.state.IconPainterStateImageTest.testRememberIconPainterStateImage
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitablePainter,
    background: EquitablePainter? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconPainterStateImage = remember(icon, background, iconSize, iconTint) {
    IconPainterStateImage(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.common.test.state.IconPainterStateImageTest.testRememberIconPainterStateImage
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitablePainter,
    background: Color? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconPainterStateImage = remember(icon, background, iconSize, iconTint) {
    val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
    IconPainterStateImage(
        icon = icon,
        background = backgroundPainter,
        iconSize = iconSize,
        iconTint = iconTint
    )
}


/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.common.test.state.IconPainterStateImageTest.testRememberIconPainterStateImage
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitablePainter,
    background: EquitablePainter? = null,
    iconSize: Size? = null,
): IconPainterStateImage = remember(icon, background, iconSize) {
    IconPainterStateImage(
        icon = icon,
        background = background,
        iconSize = iconSize,
        iconTint = null
    )
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.common.test.state.IconPainterStateImageTest.testRememberIconPainterStateImage
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitablePainter,
    background: Color? = null,
    iconSize: Size? = null,
): IconPainterStateImage = remember(icon, background, iconSize) {
    val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
    IconPainterStateImage(
        icon = icon,
        background = backgroundPainter,
        iconSize = iconSize,
        iconTint = null
    )
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.common.test.state.IconPainterStateImageTest.testRememberIconPainterStateImage
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitablePainter,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconPainterStateImage = remember(icon, iconSize, iconTint) {
    IconPainterStateImage(
        icon = icon,
        background = null,
        iconSize = iconSize,
        iconTint = iconTint
    )
}


/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.common.test.state.IconPainterStateImageTest.testRememberIconPainterStateImage
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitablePainter,
    background: EquitablePainter? = null,
): IconPainterStateImage = remember(icon, background) {
    IconPainterStateImage(
        icon = icon,
        background = background,
        iconSize = null,
        iconTint = null
    )
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.common.test.state.IconPainterStateImageTest.testRememberIconPainterStateImage
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitablePainter,
    background: Color? = null,
): IconPainterStateImage = remember(icon, background) {
    val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
    IconPainterStateImage(
        icon = icon,
        background = backgroundPainter,
        iconSize = null,
        iconTint = null
    )
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.common.test.state.IconPainterStateImageTest.testRememberIconPainterStateImage
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitablePainter,
    iconSize: Size? = null,
): IconPainterStateImage = remember(icon, iconSize) {
    IconPainterStateImage(
        icon = icon,
        background = null,
        iconSize = iconSize,
        iconTint = null
    )
}


/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.common.test.state.IconPainterStateImageTest.testRememberIconPainterStateImage
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitablePainter,
): IconPainterStateImage = remember(icon) {
    IconPainterStateImage(
        icon = icon,
        background = null,
        iconSize = null,
        iconTint = null
    )
}

/**
 * StateImage implemented by IconPainter
 *
 * @see com.github.panpf.sketch.compose.core.common.test.state.IconPainterStateImageTest
 */
@Stable
data class IconPainterStateImage(
    val icon: EquitablePainter,
    val background: EquitablePainter? = null,
    val iconSize: Size? = null,
    val iconTint: Color? = null,
) : StateImage {

    override val key: String =
        "IconPainterStateImage(icon=${icon.key},background=${background?.key},iconSize=$iconSize,iconTint=${iconTint?.value})"

    override fun getImage(sketch: Sketch, request: ImageRequest, throwable: Throwable?): Image {
        return IconPainter(
            icon = icon,
            background = background,
            iconSize = iconSize,
            iconTint = iconTint
        ).asImage()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as IconPainterStateImage
        if (icon != other.icon) return false
        if (background != other.background) return false
        if (iconSize != other.iconSize) return false
        if (iconTint != other.iconTint) return false
        return true
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
        return "IconPainterStateImage(icon=$icon, background=$background, iconSize=$iconSize, iconTint=${iconTint?.value})"
    }
}