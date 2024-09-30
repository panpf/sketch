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
 * Create and remember IconAnimatablePainterStateImage
 *
 * @see com.github.panpf.sketch.compose.core.common.test.state.IconAnimatablePainterStateImageTest.testRememberIconAnimatablePainterStateImage
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitablePainter,
    background: EquitablePainter? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainterStateImage {
    return remember(icon, background, iconSize, iconTint) {
        IconAnimatablePainterStateImage(
            icon = icon,
            background = background,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}

/**
 * Create and remember IconAnimatablePainterStateImage
 *
 * @see com.github.panpf.sketch.compose.core.common.test.state.IconAnimatablePainterStateImageTest.testRememberIconAnimatablePainterStateImage
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitablePainter,
    background: Color? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainterStateImage {
    return remember(icon, background, iconSize, iconTint) {
        val backgroundPainter = background?.let { ColorPainter(it) }
        IconAnimatablePainterStateImage(
            icon = icon,
            background = backgroundPainter?.asEquitable(),
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}

/**
 * Create and remember IconAnimatablePainterStateImage
 *
 * @see com.github.panpf.sketch.compose.core.common.test.state.IconAnimatablePainterStateImageTest.testRememberIconAnimatablePainterStateImage
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitablePainter,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainterStateImage {
    return remember(icon, iconSize, iconTint) {
        IconAnimatablePainterStateImage(
            icon = icon,
            background = null,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}

/**
 * StateImage implemented by IconAnimatablePainter
 *
 * @see com.github.panpf.sketch.compose.core.common.test.state.IconAnimatablePainterStateImageTest
 */
@Stable
data class IconAnimatablePainterStateImage(
    val icon: EquitablePainter,
    val background: EquitablePainter? = null,
    val iconSize: Size? = null,
    val iconTint: Color? = null,
) : StateImage {

    override val key: String =
        "IconAnimatablePainterStateImage(icon=${icon.key},background=${background?.key},iconSize=$iconSize,iconTint=$iconTint)"

    override fun getImage(sketch: Sketch, request: ImageRequest, throwable: Throwable?): Image {
        return IconPainter(icon, background, iconSize, iconTint).asImage()
    }

    override fun toString(): String {
        return "IconAnimatablePainterStateImage(icon=$icon, background=$background, iconSize=$iconSize, iconTint=$iconTint)"
    }
}