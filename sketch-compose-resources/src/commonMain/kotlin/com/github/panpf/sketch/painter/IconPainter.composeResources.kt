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

/* ********************************************* Painter icon ********************************************* */

/**
 * Create a [IconPainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.resources.common.test.painter.IconPainterComposeResourcesTest.testRememberIconPainterWithPainterIcon
 */
@Composable
fun rememberIconPainter(
    icon: EquitablePainter,
    background: DrawableResource? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconPainter {
    val backgroundPainter = background?.let { rememberEquitablePainterResource(it) }
    return remember(icon, background, iconSize, iconTint) {
        IconPainter(
            icon = icon,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}

/**
 * Create a [IconPainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.resources.common.test.painter.IconPainterComposeResourcesTest.testRememberIconPainterWithPainterIcon
 */
@Composable
fun rememberIconPainter(
    icon: EquitablePainter,
    background: DrawableResource? = null,
    iconSize: Size? = null,
): IconPainter {
    val backgroundPainter = background?.let { rememberEquitablePainterResource(it) }
    return remember(icon, background, iconSize) {
        IconPainter(
            icon = icon,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = null
        )
    }
}

/**
 * Create a [IconPainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.resources.common.test.painter.IconPainterComposeResourcesTest.testRememberIconPainterWithPainterIcon
 */
@Composable
fun rememberIconPainter(
    icon: EquitablePainter,
    background: DrawableResource? = null,
): IconPainter {
    val backgroundPainter = background?.let { rememberEquitablePainterResource(it) }
    return remember(icon, background) {
        IconPainter(
            icon = icon,
            background = backgroundPainter,
            iconSize = null,
            iconTint = null
        )
    }
}


/* ********************************************* DrawableResource icon ********************************************* */

/**
 * Create a [IconPainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.resources.common.test.painter.IconPainterComposeResourcesTest.testRememberIconPainterWithDrawableResourcesIcon
 */
@Composable
fun rememberIconPainter(
    icon: DrawableResource,
    background: EquitablePainter? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconPainter {
    val iconPainter = rememberEquitablePainterResource(icon)
    return remember(icon, background, iconSize, iconTint) {
        IconPainter(
            icon = iconPainter,
            background = background,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}

/**
 * Create a [IconPainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.resources.common.test.painter.IconPainterComposeResourcesTest.testRememberIconPainterWithDrawableResourcesIcon
 */
@Composable
fun rememberIconPainter(
    icon: DrawableResource,
    background: Color? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconPainter {
    val iconPainter = rememberEquitablePainterResource(icon)
    return remember(icon, background, iconSize, iconTint) {
        val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
        IconPainter(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}


/**
 * Create a [IconPainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.resources.common.test.painter.IconPainterComposeResourcesTest.testRememberIconPainterWithDrawableResourcesIcon
 */
@Composable
fun rememberIconPainter(
    icon: DrawableResource,
    background: EquitablePainter? = null,
    iconSize: Size? = null,
): IconPainter {
    val iconPainter = rememberEquitablePainterResource(icon)
    return remember(icon, background, iconSize) {
        IconPainter(
            icon = iconPainter,
            background = background,
            iconSize = iconSize,
            iconTint = null
        )
    }
}

/**
 * Create a [IconPainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.resources.common.test.painter.IconPainterComposeResourcesTest.testRememberIconPainterWithDrawableResourcesIcon
 */
@Composable
fun rememberIconPainter(
    icon: DrawableResource,
    background: Color? = null,
    iconSize: Size? = null,
): IconPainter {
    val iconPainter = rememberEquitablePainterResource(icon)
    return remember(icon, background, iconSize) {
        val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
        IconPainter(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = null
        )
    }
}

/**
 * Create a [IconPainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.resources.common.test.painter.IconPainterComposeResourcesTest.testRememberIconPainterWithDrawableResourcesIcon
 */
@Composable
fun rememberIconPainter(
    icon: DrawableResource,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconPainter {
    val iconPainter = rememberEquitablePainterResource(icon)
    return remember(icon, iconSize, iconTint) {
        IconPainter(
            icon = iconPainter,
            background = null,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}


/**
 * Create a [IconPainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.resources.common.test.painter.IconPainterComposeResourcesTest.testRememberIconPainterWithDrawableResourcesIcon
 */
@Composable
fun rememberIconPainter(
    icon: DrawableResource,
    background: EquitablePainter? = null,
): IconPainter {
    val iconPainter = rememberEquitablePainterResource(icon)
    return remember(icon, background) {
        IconPainter(
            icon = iconPainter,
            background = background,
            iconSize = null,
            iconTint = null
        )
    }
}

/**
 * Create a [IconPainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.resources.common.test.painter.IconPainterComposeResourcesTest.testRememberIconPainterWithDrawableResourcesIcon
 */
@Composable
fun rememberIconPainter(
    icon: DrawableResource,
    background: Color? = null,
): IconPainter {
    val iconPainter = rememberEquitablePainterResource(icon)
    return remember(icon, background) {
        val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
        IconPainter(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = null,
            iconTint = null
        )
    }
}

/**
 * Create a [IconPainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.resources.common.test.painter.IconPainterComposeResourcesTest.testRememberIconPainterWithDrawableResourcesIcon
 */
@Composable
fun rememberIconPainter(
    icon: DrawableResource,
    iconSize: Size? = null,
): IconPainter {
    val iconPainter = rememberEquitablePainterResource(icon)
    return remember(icon, iconSize) {
        IconPainter(
            icon = iconPainter,
            background = null,
            iconSize = iconSize,
            iconTint = null
        )
    }
}

/**
 * Create a [IconPainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.resources.common.test.painter.IconPainterComposeResourcesTest.testRememberIconPainterWithDrawableResourcesIcon
 */
@Composable
fun rememberIconPainter(
    icon: DrawableResource,
): IconPainter {
    val iconPainter = rememberEquitablePainterResource(icon)
    return remember(icon) {
        IconPainter(
            icon = iconPainter,
            background = null,
            iconSize = null,
            iconTint = null
        )
    }
}

/**
 * Create a [IconPainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.resources.common.test.painter.IconPainterComposeResourcesTest.testRememberIconPainterWithPainterIcon
 */
@Composable
fun rememberIconPainter(
    icon: DrawableResource,
    background: DrawableResource? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconPainter {
    val iconPainter = rememberEquitablePainterResource(icon)
    val backgroundPainter = background?.let { rememberEquitablePainterResource(it) }
    return remember(icon, background, iconSize, iconTint) {
        IconPainter(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}

/**
 * Create a [IconPainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.resources.common.test.painter.IconPainterComposeResourcesTest.testRememberIconPainterWithPainterIcon
 */
@Composable
fun rememberIconPainter(
    icon: DrawableResource,
    background: DrawableResource? = null,
    iconSize: Size? = null,
): IconPainter {
    val iconPainter = rememberEquitablePainterResource(icon)
    val backgroundPainter = background?.let { rememberEquitablePainterResource(it) }
    return remember(icon, background, iconSize) {
        IconPainter(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = null
        )
    }
}

/**
 * Create a [IconPainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.resources.common.test.painter.IconPainterComposeResourcesTest.testRememberIconPainterWithPainterIcon
 */
@Composable
fun rememberIconPainter(
    icon: DrawableResource,
    background: DrawableResource? = null,
): IconPainter {
    val iconPainter = rememberEquitablePainterResource(icon)
    val backgroundPainter = background?.let { rememberEquitablePainterResource(it) }
    return remember(icon, background) {
        IconPainter(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = null,
            iconTint = null
        )
    }
}