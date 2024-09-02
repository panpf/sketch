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

package com.github.panpf.sketch.painter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import org.jetbrains.compose.resources.DrawableResource

/**
 * Create an [IconAnimatablePainter] that can be remembered in the composition
 *
 * @see com.github.panpf.sketch.compose.resources.common.test.painter.IconAnimatablePainterComposeResourcesTest.testRememberIconAnimatablePainter
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: PainterEqualizer,
    background: DrawableResource? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainter {
    val backgroundPainter = background?.let { rememberEqualityPainterResource(it) }
    return remember(icon, background, iconSize, iconTint) {
        IconAnimatablePainter(
            icon = icon,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}

/**
 * Create an [IconAnimatablePainter] that can be remembered in the composition
 *
 * @see com.github.panpf.sketch.compose.resources.common.test.painter.IconAnimatablePainterComposeResourcesTest.testRememberIconAnimatablePainter
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: DrawableResource,
    background: PainterEqualizer? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainter {
    val iconPainter = rememberEqualityPainterResource(icon)
    return remember(icon, background, iconSize, iconTint) {
        IconAnimatablePainter(
            icon = iconPainter,
            background = background,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}

/**
 * Create an [IconAnimatablePainter] that can be remembered in the composition
 *
 * @see com.github.panpf.sketch.compose.resources.common.test.painter.IconAnimatablePainterComposeResourcesTest.testRememberIconAnimatablePainter
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: DrawableResource,
    background: Color? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainter {
    val iconPainter = rememberEqualityPainterResource(icon)
    return remember(icon, background, iconSize, iconTint) {
        val backgroundPainter = background?.let { ColorPainter(it) }
        IconAnimatablePainter(
            icon = iconPainter,
            background = backgroundPainter?.asEquality(),
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}

/**
 * Create an [IconAnimatablePainter] that can be remembered in the composition
 *
 * @see com.github.panpf.sketch.compose.resources.common.test.painter.IconAnimatablePainterComposeResourcesTest.testRememberIconAnimatablePainter
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: DrawableResource,
    background: DrawableResource? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainter {
    val iconPainter = rememberEqualityPainterResource(icon)
    val backgroundPainter = background?.let { rememberEqualityPainterResource(it) }
    return remember(icon, background, iconSize, iconTint) {
        IconAnimatablePainter(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}

/**
 * Create an [IconAnimatablePainter] that can be remembered in the composition
 *
 * @see com.github.panpf.sketch.compose.resources.common.test.painter.IconAnimatablePainterComposeResourcesTest.testRememberIconAnimatablePainter
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: DrawableResource,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainter {
    val iconPainter = rememberEqualityPainterResource(icon)
    return remember(icon, iconSize, iconTint) {
        IconAnimatablePainter(
            icon = iconPainter,
            background = null,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}