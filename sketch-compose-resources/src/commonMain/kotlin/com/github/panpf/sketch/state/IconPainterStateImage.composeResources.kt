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
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import com.github.panpf.sketch.painter.EquitablePainter
import com.github.panpf.sketch.painter.asEquitable
import com.github.panpf.sketch.painter.rememberEquitablePainterResource
import org.jetbrains.compose.resources.DrawableResource

/* ********************************************* Painter icon ********************************************* */

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.resources.common.test.state.IconPainterStateImageComposeResourcesTest.testRememberIconPainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitablePainter,
    background: DrawableResource? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconPainterStateImage {
    val backgroundPainter = background?.let { rememberEquitablePainterResource(it) }
    return remember(icon, background, iconSize, iconTint) {
        IconPainterStateImage(
            icon = icon,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.resources.common.test.state.IconPainterStateImageComposeResourcesTest.testRememberIconPainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitablePainter,
    background: DrawableResource? = null,
    iconSize: Size? = null,
): IconPainterStateImage {
    val backgroundPainter = background?.let { rememberEquitablePainterResource(it) }
    return remember(icon, background, iconSize) {
        IconPainterStateImage(
            icon = icon,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = null
        )
    }
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.resources.common.test.state.IconPainterStateImageComposeResourcesTest.testRememberIconPainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitablePainter,
    background: DrawableResource? = null,
): IconPainterStateImage {
    val backgroundPainter = background?.let { rememberEquitablePainterResource(it) }
    return remember(icon, background) {
        IconPainterStateImage(
            icon = icon,
            background = backgroundPainter,
            iconSize = null,
            iconTint = null
        )
    }
}


/* ********************************************* DrawableResource icon ********************************************* */

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.resources.common.test.state.IconPainterStateImageComposeResourcesTest.testRememberIconPainterStateImageWithDrawableResourcesIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: DrawableResource,
    background: EquitablePainter? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconPainterStateImage {
    val iconPainter = rememberEquitablePainterResource(icon)
    return remember(icon, background, iconSize, iconTint) {
        IconPainterStateImage(
            icon = iconPainter,
            background = background,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.resources.common.test.state.IconPainterStateImageComposeResourcesTest.testRememberIconPainterStateImageWithDrawableResourcesIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: DrawableResource,
    background: Color? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconPainterStateImage {
    val iconPainter = rememberEquitablePainterResource(icon)
    return remember(icon, background, iconSize, iconTint) {
        val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
        IconPainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}


/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.resources.common.test.state.IconPainterStateImageComposeResourcesTest.testRememberIconPainterStateImageWithDrawableResourcesIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: DrawableResource,
    background: EquitablePainter? = null,
    iconSize: Size? = null,
): IconPainterStateImage {
    val iconPainter = rememberEquitablePainterResource(icon)
    return remember(icon, background, iconSize) {
        IconPainterStateImage(
            icon = iconPainter,
            background = background,
            iconSize = iconSize,
            iconTint = null
        )
    }
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.resources.common.test.state.IconPainterStateImageComposeResourcesTest.testRememberIconPainterStateImageWithDrawableResourcesIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: DrawableResource,
    background: Color? = null,
    iconSize: Size? = null,
): IconPainterStateImage {
    val iconPainter = rememberEquitablePainterResource(icon)
    return remember(icon, background, iconSize) {
        val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
        IconPainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = null
        )
    }
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.resources.common.test.state.IconPainterStateImageComposeResourcesTest.testRememberIconPainterStateImageWithDrawableResourcesIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: DrawableResource,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconPainterStateImage {
    val iconPainter = rememberEquitablePainterResource(icon)
    return remember(icon, iconSize, iconTint) {
        IconPainterStateImage(
            icon = iconPainter,
            background = null,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}


/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.resources.common.test.state.IconPainterStateImageComposeResourcesTest.testRememberIconPainterStateImageWithDrawableResourcesIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: DrawableResource,
    background: EquitablePainter? = null,
): IconPainterStateImage {
    val iconPainter = rememberEquitablePainterResource(icon)
    return remember(icon, background) {
        IconPainterStateImage(
            icon = iconPainter,
            background = background,
            iconSize = null,
            iconTint = null
        )
    }
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.resources.common.test.state.IconPainterStateImageComposeResourcesTest.testRememberIconPainterStateImageWithDrawableResourcesIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: DrawableResource,
    background: Color? = null,
): IconPainterStateImage {
    val iconPainter = rememberEquitablePainterResource(icon)
    return remember(icon, background) {
        val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
        IconPainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = null,
            iconTint = null
        )
    }
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.resources.common.test.state.IconPainterStateImageComposeResourcesTest.testRememberIconPainterStateImageWithDrawableResourcesIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: DrawableResource,
    iconSize: Size? = null,
): IconPainterStateImage {
    val iconPainter = rememberEquitablePainterResource(icon)
    return remember(icon, iconSize) {
        IconPainterStateImage(
            icon = iconPainter,
            background = null,
            iconSize = iconSize,
            iconTint = null
        )
    }
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.resources.common.test.state.IconPainterStateImageComposeResourcesTest.testRememberIconPainterStateImageWithDrawableResourcesIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: DrawableResource,
): IconPainterStateImage {
    val iconPainter = rememberEquitablePainterResource(icon)
    return remember(icon) {
        IconPainterStateImage(
            icon = iconPainter,
            background = null,
            iconSize = null,
            iconTint = null
        )
    }
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.resources.common.test.state.IconPainterStateImageComposeResourcesTest.testRememberIconPainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: DrawableResource,
    background: DrawableResource? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconPainterStateImage {
    val iconPainter = rememberEquitablePainterResource(icon)
    val backgroundPainter = background?.let { rememberEquitablePainterResource(it) }
    return remember(icon, background, iconSize, iconTint) {
        IconPainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.resources.common.test.state.IconPainterStateImageComposeResourcesTest.testRememberIconPainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: DrawableResource,
    background: DrawableResource? = null,
    iconSize: Size? = null,
): IconPainterStateImage {
    val iconPainter = rememberEquitablePainterResource(icon)
    val backgroundPainter = background?.let { rememberEquitablePainterResource(it) }
    return remember(icon, background, iconSize) {
        IconPainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = null
        )
    }
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.resources.common.test.state.IconPainterStateImageComposeResourcesTest.testRememberIconPainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: DrawableResource,
    background: DrawableResource? = null,
): IconPainterStateImage {
    val iconPainter = rememberEquitablePainterResource(icon)
    val backgroundPainter = background?.let { rememberEquitablePainterResource(it) }
    return remember(icon, background) {
        IconPainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = null,
            iconTint = null
        )
    }
}