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

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.platform.LocalContext
import com.github.panpf.sketch.drawable.EquitableDrawable
import com.github.panpf.sketch.drawable.ResDrawable
import com.github.panpf.sketch.painter.EquitablePainter
import com.github.panpf.sketch.painter.asEquitable
import com.github.panpf.sketch.util.IntColor
import com.github.panpf.sketch.util.ResColor

/* ********************************************* Painter icon ********************************************* */

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitablePainter,
    background: EquitablePainter? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconTintColor = iconTint
            ?.let { ResColor(it) }
            ?.getColor(context)
            ?.let { Color(it) }
        IconPainterStateImage(
            icon = icon,
            background = background,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitablePainter,
    background: EquitablePainter? = null,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconPainterStateImage {
    return remember(icon, background, iconSize, iconTint) {
        val iconTintColor = iconTint
            ?.color
            ?.let { Color(it) }
        IconPainterStateImage(
            icon = icon,
            background = background,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitablePainter,
    background: Color? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
        val iconTintColor = iconTint
            ?.let { ResColor(it) }
            ?.getColor(context)
            ?.let { Color(it) }
        IconPainterStateImage(
            icon = icon,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitablePainter,
    background: Color? = null,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconPainterStateImage {
    return remember(icon, background, iconSize, iconTint) {
        val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
        val iconTintColor = iconTint
            ?.color
            ?.let { Color(it) }
        IconPainterStateImage(
            icon = icon,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitablePainter,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val backgroundPainter = background
            ?.let { ResDrawable(it) }
            ?.getDrawable(context)
            ?.asEquitablePainter(background)
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
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitablePainter,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val backgroundPainter = background
            ?.let { ResDrawable(it) }
            ?.getDrawable(context)
            ?.asEquitablePainter(background)
        val iconTintColor = iconTint
            ?.let { ResColor(it) }
            ?.getColor(context)
            ?.let { Color(it) }
        IconPainterStateImage(
            icon = icon,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitablePainter,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val backgroundPainter = background
            ?.let { ResDrawable(it) }
            ?.getDrawable(context)
            ?.asEquitablePainter(background)
        val iconTintColor = iconTint
            ?.color
            ?.let { Color(it) }
        IconPainterStateImage(
            icon = icon,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitablePainter,
    background: IntColor? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconPainterStateImage = remember(icon, background, iconSize, iconTint) {
    val backgroundPainter = background
        ?.let { ColorPainter(Color(it.color)) }
        ?.asEquitable()
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
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitablePainter,
    background: IntColor? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val backgroundPainter = background
            ?.let { ColorPainter(Color(it.color)) }
            ?.asEquitable()
        val iconTintColor = iconTint
            ?.let { ResColor(it) }
            ?.getColor(context)
            ?.let { Color(it) }
        IconPainterStateImage(
            icon = icon,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitablePainter,
    background: IntColor? = null,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconPainterStateImage = remember(icon, background, iconSize, iconTint) {
    val backgroundPainter = background
        ?.let { ColorPainter(Color(it.color)) }
        ?.asEquitable()
    val iconTintColor = iconTint
        ?.color
        ?.let { Color(it) }
    IconPainterStateImage(
        icon = icon,
        background = backgroundPainter,
        iconSize = iconSize,
        iconTint = iconTintColor
    )
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitablePainter,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconPainterStateImage = remember(icon, background, iconSize, iconTint) {
    val backgroundPainter = background?.asEquitablePainter()
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
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitablePainter,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val backgroundPainter = background?.asEquitablePainter()
        val iconTintColor = iconTint
            ?.let { ResColor(it) }
            ?.getColor(context)
            ?.let { Color(it) }
        IconPainterStateImage(
            icon = icon,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitablePainter,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconPainterStateImage = remember(icon, background, iconSize, iconTint) {
    val backgroundPainter = background?.asEquitablePainter()
    val iconTintColor = iconTint?.color?.let { Color(it) }
    IconPainterStateImage(
        icon = icon,
        background = backgroundPainter,
        iconSize = iconSize,
        iconTint = iconTintColor
    )
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitablePainter,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize) {
        val backgroundPainter = background
            ?.let { ResDrawable(it) }
            ?.getDrawable(context)
            ?.asEquitablePainter(background)
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
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitablePainter,
    background: IntColor? = null,
    iconSize: Size? = null,
): IconPainterStateImage = remember(icon, background, iconSize) {
    val backgroundPainter = background
        ?.let { ColorPainter(Color(it.color)) }
        ?.asEquitable()
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
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitablePainter,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
): IconPainterStateImage = remember(icon, background, iconSize) {
    val backgroundPainter = background?.asEquitablePainter()
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
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitablePainter,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, iconSize, iconTint) {
        val iconTintColor = iconTint
            ?.let { ResColor(it) }
            ?.getColor(context)
            ?.let { Color(it) }
        IconPainterStateImage(
            icon = icon,
            background = null,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitablePainter,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconPainterStateImage = remember(icon, iconSize, iconTint) {
    val iconTintColor = iconTint
        ?.color
        ?.let { Color(it) }
    IconPainterStateImage(
        icon = icon,
        background = null,
        iconSize = iconSize,
        iconTint = iconTintColor
    )
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitablePainter,
    @DrawableRes background: Int? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, background) {
        val backgroundPainter = background
            ?.let { ResDrawable(it) }
            ?.getDrawable(context)
            ?.asEquitablePainter(background)
        IconPainterStateImage(
            icon = icon,
            background = backgroundPainter,
            iconSize = null,
            iconTint = null
        )
    }
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitablePainter,
    background: IntColor? = null,
): IconPainterStateImage = remember(icon, background) {
    val backgroundPainter = background
        ?.let { ColorPainter(Color(it.color)) }
        ?.asEquitable()
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
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitablePainter,
    background: EquitableDrawable? = null,
): IconPainterStateImage = remember(icon, background) {
    val backgroundPainter = background?.asEquitablePainter()
    IconPainterStateImage(
        icon = icon,
        background = backgroundPainter,
        iconSize = null,
        iconTint = null
    )
}


/* ********************************************* Drawable icon ********************************************* */

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithDrawableIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitableDrawable,
    background: EquitablePainter? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconPainterStateImage = remember(icon, background, iconSize, iconTint) {
    val iconPainter = icon.asEquitablePainter()
    IconPainterStateImage(
        icon = iconPainter,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithDrawableIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitableDrawable,
    background: Color? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconPainterStateImage = remember(icon, background, iconSize, iconTint) {
    val iconPainter = icon.asEquitablePainter()
    val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
    IconPainterStateImage(
        icon = iconPainter,
        background = backgroundPainter,
        iconSize = iconSize,
        iconTint = iconTint
    )
}


/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithDrawableIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitableDrawable,
    background: EquitablePainter? = null,
    iconSize: Size? = null,
): IconPainterStateImage = remember(icon, background, iconSize) {
    val iconPainter = icon.asEquitablePainter()
    IconPainterStateImage(
        icon = iconPainter,
        background = background,
        iconSize = iconSize,
        iconTint = null
    )
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithDrawableIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitableDrawable,
    background: Color? = null,
    iconSize: Size? = null,
): IconPainterStateImage = remember(icon, background, iconSize) {
    val iconPainter = icon.asEquitablePainter()
    val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
    IconPainterStateImage(
        icon = iconPainter,
        background = backgroundPainter,
        iconSize = iconSize,
        iconTint = null
    )
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithDrawableIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitableDrawable,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconPainterStateImage = remember(icon, iconSize, iconTint) {
    val iconPainter = icon.asEquitablePainter()
    IconPainterStateImage(
        icon = iconPainter,
        background = null,
        iconSize = iconSize,
        iconTint = iconTint
    )
}


/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithDrawableIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitableDrawable,
    background: EquitablePainter? = null,
): IconPainterStateImage = remember(icon, background) {
    val iconPainter = icon.asEquitablePainter()
    IconPainterStateImage(
        icon = iconPainter,
        background = background,
        iconSize = null,
        iconTint = null
    )
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithDrawableIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitableDrawable,
    background: Color? = null,
): IconPainterStateImage = remember(icon, background) {
    val iconPainter = icon.asEquitablePainter()
    val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
    IconPainterStateImage(
        icon = iconPainter,
        background = backgroundPainter,
        iconSize = null,
        iconTint = null
    )
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithDrawableIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitableDrawable,
    iconSize: Size? = null,
): IconPainterStateImage = remember(icon, iconSize) {
    val iconPainter = icon.asEquitablePainter()
    IconPainterStateImage(
        icon = iconPainter,
        background = null,
        iconSize = iconSize,
        iconTint = null
    )
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithDrawableIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitableDrawable,
): IconPainterStateImage = remember(icon) {
    val iconPainter = icon.asEquitablePainter()
    IconPainterStateImage(
        icon = iconPainter,
        background = null,
        iconSize = null,
        iconTint = null
    )
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitableDrawable,
    background: EquitablePainter? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = icon.asEquitablePainter()
        val iconTintColor = iconTint
            ?.let { ResColor(it) }
            ?.getColor(context)
            ?.let { Color(it) }
        IconPainterStateImage(
            icon = iconPainter,
            background = background,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitableDrawable,
    background: EquitablePainter? = null,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconPainterStateImage {
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = icon.asEquitablePainter()
        val iconTintColor = iconTint
            ?.color
            ?.let { Color(it) }
        IconPainterStateImage(
            icon = iconPainter,
            background = background,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitableDrawable,
    background: Color? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = icon.asEquitablePainter()
        val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
        val iconTintColor = iconTint
            ?.let { ResColor(it) }
            ?.getColor(context)
            ?.let { Color(it) }
        IconPainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitableDrawable,
    background: Color? = null,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconPainterStateImage {
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = icon.asEquitablePainter()
        val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
        val iconTintColor = iconTint
            ?.color
            ?.let { Color(it) }
        IconPainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitableDrawable,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = icon.asEquitablePainter()
        val backgroundPainter = background
            ?.let { ResDrawable(it) }
            ?.getDrawable(context)
            ?.asEquitablePainter(background)
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
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitableDrawable,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = icon.asEquitablePainter()
        val backgroundPainter = background
            ?.let { ResDrawable(it) }
            ?.getDrawable(context)
            ?.asEquitablePainter(background)
        val iconTintColor = iconTint
            ?.let { ResColor(it) }
            ?.getColor(context)
            ?.let { Color(it) }
        IconPainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitableDrawable,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = icon.asEquitablePainter()
        val backgroundPainter = background
            ?.let { ResDrawable(it) }
            ?.getDrawable(context)
            ?.asEquitablePainter(background)
        val iconTintColor = iconTint
            ?.color
            ?.let { Color(it) }
        IconPainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitableDrawable,
    background: IntColor? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconPainterStateImage = remember(icon, background, iconSize, iconTint) {
    val iconPainter = icon.asEquitablePainter()
    val backgroundPainter = background
        ?.let { ColorPainter(Color(it.color)) }
        ?.asEquitable()
    IconPainterStateImage(
        icon = iconPainter,
        background = backgroundPainter,
        iconSize = iconSize,
        iconTint = iconTint
    )
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitableDrawable,
    background: IntColor? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = icon.asEquitablePainter()
        val backgroundPainter = background
            ?.let { ColorPainter(Color(it.color)) }
            ?.asEquitable()
        val iconTintColor = iconTint
            ?.let { ResColor(it) }
            ?.getColor(context)
            ?.let { Color(it) }
        IconPainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitableDrawable,
    background: IntColor? = null,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconPainterStateImage = remember(icon, background, iconSize, iconTint) {
    val iconPainter = icon.asEquitablePainter()
    val backgroundPainter = background
        ?.let { ColorPainter(Color(it.color)) }
        ?.asEquitable()
    val iconTintColor = iconTint
        ?.color
        ?.let { Color(it) }
    IconPainterStateImage(
        icon = iconPainter,
        background = backgroundPainter,
        iconSize = iconSize,
        iconTint = iconTintColor
    )
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitableDrawable,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconPainterStateImage = remember(icon, background, iconSize, iconTint) {
    val iconPainter = icon.asEquitablePainter()
    val backgroundPainter = background?.asEquitablePainter()
    IconPainterStateImage(
        icon = iconPainter,
        background = backgroundPainter,
        iconSize = iconSize,
        iconTint = iconTint
    )
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitableDrawable,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = icon.asEquitablePainter()
        val backgroundPainter = background?.asEquitablePainter()
        val iconTintColor = iconTint
            ?.let { ResColor(it) }
            ?.getColor(context)
            ?.let { Color(it) }
        IconPainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitableDrawable,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconPainterStateImage = remember(icon, background, iconSize, iconTint) {
    val iconPainter = icon.asEquitablePainter()
    val backgroundPainter = background?.asEquitablePainter()
    val iconTintColor = iconTint?.color?.let { Color(it) }
    IconPainterStateImage(
        icon = iconPainter,
        background = backgroundPainter,
        iconSize = iconSize,
        iconTint = iconTintColor
    )
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitableDrawable,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize) {
        val iconPainter = icon.asEquitablePainter()
        val backgroundPainter = background
            ?.let { ResDrawable(it) }
            ?.getDrawable(context)
            ?.asEquitablePainter(background)
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
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitableDrawable,
    background: IntColor? = null,
    iconSize: Size? = null,
): IconPainterStateImage = remember(icon, background, iconSize) {
    val iconPainter = icon.asEquitablePainter()
    val backgroundPainter = background
        ?.let { ColorPainter(Color(it.color)) }
        ?.asEquitable()
    IconPainterStateImage(
        icon = iconPainter,
        background = backgroundPainter,
        iconSize = iconSize,
        iconTint = null
    )
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitableDrawable,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
): IconPainterStateImage = remember(icon, background, iconSize) {
    val iconPainter = icon.asEquitablePainter()
    val backgroundPainter = background?.asEquitablePainter()
    IconPainterStateImage(
        icon = iconPainter,
        background = backgroundPainter,
        iconSize = iconSize,
        iconTint = null
    )
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitableDrawable,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, iconSize, iconTint) {
        val iconPainter = icon.asEquitablePainter()
        val iconTintColor = iconTint
            ?.let { ResColor(it) }
            ?.getColor(context)
            ?.let { Color(it) }
        IconPainterStateImage(
            icon = iconPainter,
            background = null,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitableDrawable,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconPainterStateImage = remember(icon, iconSize, iconTint) {
    val iconPainter = icon.asEquitablePainter()
    val iconTintColor = iconTint
        ?.color
        ?.let { Color(it) }
    IconPainterStateImage(
        icon = iconPainter,
        background = null,
        iconSize = iconSize,
        iconTint = iconTintColor
    )
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitableDrawable,
    @DrawableRes background: Int? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, background) {
        val iconPainter = icon.asEquitablePainter()
        val backgroundPainter = background
            ?.let { ResDrawable(it) }
            ?.getDrawable(context)
            ?.asEquitablePainter(background)
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
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitableDrawable,
    background: IntColor? = null,
): IconPainterStateImage = remember(icon, background) {
    val iconPainter = icon.asEquitablePainter()
    val backgroundPainter = background
        ?.let { ColorPainter(Color(it.color)) }
        ?.asEquitable()
    IconPainterStateImage(
        icon = iconPainter,
        background = backgroundPainter,
        iconSize = null,
        iconTint = null
    )
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconPainterStateImage(
    icon: EquitableDrawable,
    background: EquitableDrawable? = null,
): IconPainterStateImage = remember(icon, background) {
    val iconPainter = icon.asEquitablePainter()
    val backgroundPainter = background?.asEquitablePainter()
    IconPainterStateImage(
        icon = iconPainter,
        background = backgroundPainter,
        iconSize = null,
        iconTint = null
    )
}


/* ********************************************* res icon ********************************************* */

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithResIcon
 */
@Composable
fun rememberIconPainterStateImage(
    @DrawableRes icon: Int,
    background: EquitablePainter? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
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
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithResIcon
 */
@Composable
fun rememberIconPainterStateImage(
    @DrawableRes icon: Int,
    background: Color? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
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
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithResIcon
 */
@Composable
fun rememberIconPainterStateImage(
    @DrawableRes icon: Int,
    background: EquitablePainter? = null,
    iconSize: Size? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
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
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithResIcon
 */
@Composable
fun rememberIconPainterStateImage(
    @DrawableRes icon: Int,
    background: Color? = null,
    iconSize: Size? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
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
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithResIcon
 */
@Composable
fun rememberIconPainterStateImage(
    @DrawableRes icon: Int,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, iconSize, iconTint) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
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
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithResIcon
 */
@Composable
fun rememberIconPainterStateImage(
    @DrawableRes icon: Int,
    background: EquitablePainter? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, background) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
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
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithResIcon
 */
@Composable
fun rememberIconPainterStateImage(
    @DrawableRes icon: Int,
    background: Color? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, background) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
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
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithResIcon
 */
@Composable
fun rememberIconPainterStateImage(
    @DrawableRes icon: Int,
    iconSize: Size? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, iconSize) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
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
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithResIcon
 */
@Composable
fun rememberIconPainterStateImage(
    @DrawableRes icon: Int,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
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
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithResIcon
 */
@Composable
fun rememberIconPainterStateImage(
    @DrawableRes icon: Int,
    background: EquitablePainter? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
        val iconTintColor = iconTint
            ?.let { ResColor(it) }
            ?.getColor(context)
            ?.let { Color(it) }
        IconPainterStateImage(
            icon = iconPainter,
            background = background,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithResIcon
 */
@Composable
fun rememberIconPainterStateImage(
    @DrawableRes icon: Int,
    background: EquitablePainter? = null,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
        val iconTintColor = iconTint
            ?.color
            ?.let { Color(it) }
        IconPainterStateImage(
            icon = iconPainter,
            background = background,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithResIcon
 */
@Composable
fun rememberIconPainterStateImage(
    @DrawableRes icon: Int,
    background: Color? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
        val iconTintColor = iconTint
            ?.let { ResColor(it) }
            ?.getColor(context)
            ?.let { Color(it) }
        IconPainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithResIcon
 */
@Composable
fun rememberIconPainterStateImage(
    @DrawableRes icon: Int,
    background: Color? = null,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
        val iconTintColor = iconTint
            ?.color
            ?.let { Color(it) }
        IconPainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithResIcon
 */
@Composable
fun rememberIconPainterStateImage(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background
            ?.let { ResDrawable(it) }
            ?.getDrawable(context)
            ?.asEquitablePainter(background)
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
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithResIcon
 */
@Composable
fun rememberIconPainterStateImage(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background
            ?.let { ResDrawable(it) }
            ?.getDrawable(context)
            ?.asEquitablePainter(background)
        val iconTintColor = iconTint
            ?.let { ResColor(it) }
            ?.getColor(context)
            ?.let { Color(it) }
        IconPainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithResIcon
 */
@Composable
fun rememberIconPainterStateImage(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background
            ?.let { ResDrawable(it) }
            ?.getDrawable(context)
            ?.asEquitablePainter(background)
        val iconTintColor = iconTint
            ?.color
            ?.let { Color(it) }
        IconPainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithResIcon
 */
@Composable
fun rememberIconPainterStateImage(
    @DrawableRes icon: Int,
    background: IntColor? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background
            ?.let { ColorPainter(Color(it.color)) }
            ?.asEquitable()
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
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithResIcon
 */
@Composable
fun rememberIconPainterStateImage(
    @DrawableRes icon: Int,
    background: IntColor? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background
            ?.let { ColorPainter(Color(it.color)) }
            ?.asEquitable()
        val iconTintColor = iconTint
            ?.let { ResColor(it) }
            ?.getColor(context)
            ?.let { Color(it) }
        IconPainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithResIcon
 */
@Composable
fun rememberIconPainterStateImage(
    @DrawableRes icon: Int,
    background: IntColor? = null,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background
            ?.let { ColorPainter(Color(it.color)) }
            ?.asEquitable()
        val iconTintColor = iconTint
            ?.color
            ?.let { Color(it) }
        IconPainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithResIcon
 */
@Composable
fun rememberIconPainterStateImage(
    @DrawableRes icon: Int,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background?.asEquitablePainter()
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
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithResIcon
 */
@Composable
fun rememberIconPainterStateImage(
    @DrawableRes icon: Int,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background?.asEquitablePainter()
        val iconTintColor = iconTint
            ?.let { ResColor(it) }
            ?.getColor(context)
            ?.let { Color(it) }
        IconPainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithResIcon
 */
@Composable
fun rememberIconPainterStateImage(
    @DrawableRes icon: Int,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background?.asEquitablePainter()
        val iconTintColor = iconTint?.color?.let { Color(it) }
        IconPainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithResIcon
 */
@Composable
fun rememberIconPainterStateImage(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background
            ?.let { ResDrawable(it) }
            ?.getDrawable(context)
            ?.asEquitablePainter(background)
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
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithResIcon
 */
@Composable
fun rememberIconPainterStateImage(
    @DrawableRes icon: Int,
    background: IntColor? = null,
    iconSize: Size? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background
            ?.let { ColorPainter(Color(it.color)) }
            ?.asEquitable()
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
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithResIcon
 */
@Composable
fun rememberIconPainterStateImage(
    @DrawableRes icon: Int,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background?.asEquitablePainter()
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
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithResIcon
 */
@Composable
fun rememberIconPainterStateImage(
    @DrawableRes icon: Int,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, iconSize, iconTint) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
        val iconTintColor = iconTint
            ?.let { ResColor(it) }
            ?.getColor(context)
            ?.let { Color(it) }
        IconPainterStateImage(
            icon = iconPainter,
            background = null,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithResIcon
 */
@Composable
fun rememberIconPainterStateImage(
    @DrawableRes icon: Int,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, iconSize, iconTint) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
        val iconTintColor = iconTint
            ?.color
            ?.let { Color(it) }
        IconPainterStateImage(
            icon = iconPainter,
            background = null,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconPainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithResIcon
 */
@Composable
fun rememberIconPainterStateImage(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, background) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background
            ?.let { ResDrawable(it) }
            ?.getDrawable(context)
            ?.asEquitablePainter(background)
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
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithResIcon
 */
@Composable
fun rememberIconPainterStateImage(
    @DrawableRes icon: Int,
    background: IntColor? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, background) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background
            ?.let { ColorPainter(Color(it.color)) }
            ?.asEquitable()
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
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconPainterStateImageAndroidTest.testRememberIconPainterStateImageWithResIcon
 */
@Composable
fun rememberIconPainterStateImage(
    @DrawableRes icon: Int,
    background: EquitableDrawable? = null,
): IconPainterStateImage {
    val context = LocalContext.current
    return remember(icon, background) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background?.asEquitablePainter()
        IconPainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = null,
            iconTint = null
        )
    }
}