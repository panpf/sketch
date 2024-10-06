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
import com.github.panpf.sketch.state.asEquitablePainter
import com.github.panpf.sketch.util.IntColor
import com.github.panpf.sketch.util.ResColor

/* ********************************************* Painter icon ********************************************* */

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitablePainter,
    background: EquitablePainter? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatablePainter {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconTintColor = iconTint
            ?.let { ResColor(it) }
            ?.getColor(context)
            ?.let { Color(it) }
        IconAnimatablePainter(
            icon = icon,
            background = background,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitablePainter,
    background: EquitablePainter? = null,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconAnimatablePainter {
    return remember(icon, background, iconSize, iconTint) {
        val iconTintColor = iconTint
            ?.color
            ?.let { Color(it) }
        IconAnimatablePainter(
            icon = icon,
            background = background,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitablePainter,
    background: Color? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatablePainter {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
        val iconTintColor = iconTint
            ?.let { ResColor(it) }
            ?.getColor(context)
            ?.let { Color(it) }
        IconAnimatablePainter(
            icon = icon,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitablePainter,
    background: Color? = null,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconAnimatablePainter {
    return remember(icon, background, iconSize, iconTint) {
        val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
        val iconTintColor = iconTint
            ?.color
            ?.let { Color(it) }
        IconAnimatablePainter(
            icon = icon,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitablePainter,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainter {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val backgroundPainter = background
            ?.let { ResDrawable(it) }
            ?.getDrawable(context)
            ?.asEquitablePainter(background)
        IconAnimatablePainter(
            icon = icon,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitablePainter,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatablePainter {
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
        IconAnimatablePainter(
            icon = icon,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitablePainter,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconAnimatablePainter {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val backgroundPainter = background
            ?.let { ResDrawable(it) }
            ?.getDrawable(context)
            ?.asEquitablePainter(background)
        val iconTintColor = iconTint
            ?.color
            ?.let { Color(it) }
        IconAnimatablePainter(
            icon = icon,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitablePainter,
    background: IntColor? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainter = remember(icon, background, iconSize, iconTint) {
    val backgroundPainter = background
        ?.let { ColorPainter(Color(it.color)) }
        ?.asEquitable()
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
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitablePainter,
    background: IntColor? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatablePainter {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val backgroundPainter = background
            ?.let { ColorPainter(Color(it.color)) }
            ?.asEquitable()
        val iconTintColor = iconTint
            ?.let { ResColor(it) }
            ?.getColor(context)
            ?.let { Color(it) }
        IconAnimatablePainter(
            icon = icon,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitablePainter,
    background: IntColor? = null,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconAnimatablePainter = remember(icon, background, iconSize, iconTint) {
    val backgroundPainter = background
        ?.let { ColorPainter(Color(it.color)) }
        ?.asEquitable()
    val iconTintColor = iconTint
        ?.color
        ?.let { Color(it) }
    IconAnimatablePainter(
        icon = icon,
        background = backgroundPainter,
        iconSize = iconSize,
        iconTint = iconTintColor
    )
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitablePainter,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainter = remember(icon, background, iconSize, iconTint) {
    val backgroundPainter = background?.asEquitablePainter()
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
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitablePainter,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatablePainter {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val backgroundPainter = background?.asEquitablePainter()
        val iconTintColor = iconTint
            ?.let { ResColor(it) }
            ?.getColor(context)
            ?.let { Color(it) }
        IconAnimatablePainter(
            icon = icon,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitablePainter,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconAnimatablePainter = remember(icon, background, iconSize, iconTint) {
    val backgroundPainter = background?.asEquitablePainter()
    val iconTintColor = iconTint?.color?.let { Color(it) }
    IconAnimatablePainter(
        icon = icon,
        background = backgroundPainter,
        iconSize = iconSize,
        iconTint = iconTintColor
    )
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitablePainter,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
): IconAnimatablePainter {
    val context = LocalContext.current
    return remember(icon, background, iconSize) {
        val backgroundPainter = background
            ?.let { ResDrawable(it) }
            ?.getDrawable(context)
            ?.asEquitablePainter(background)
        IconAnimatablePainter(
            icon = icon,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = null
        )
    }
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitablePainter,
    background: IntColor? = null,
    iconSize: Size? = null,
): IconAnimatablePainter = remember(icon, background, iconSize) {
    val backgroundPainter = background
        ?.let { ColorPainter(Color(it.color)) }
        ?.asEquitable()
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
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitablePainter,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
): IconAnimatablePainter = remember(icon, background, iconSize) {
    val backgroundPainter = background?.asEquitablePainter()
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
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitablePainter,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatablePainter {
    val context = LocalContext.current
    return remember(icon, iconSize, iconTint) {
        val iconTintColor = iconTint
            ?.let { ResColor(it) }
            ?.getColor(context)
            ?.let { Color(it) }
        IconAnimatablePainter(
            icon = icon,
            background = null,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitablePainter,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconAnimatablePainter = remember(icon, iconSize, iconTint) {
    val iconTintColor = iconTint
        ?.color
        ?.let { Color(it) }
    IconAnimatablePainter(
        icon = icon,
        background = null,
        iconSize = iconSize,
        iconTint = iconTintColor
    )
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitablePainter,
    @DrawableRes background: Int? = null,
): IconAnimatablePainter {
    val context = LocalContext.current
    return remember(icon, background) {
        val backgroundPainter = background
            ?.let { ResDrawable(it) }
            ?.getDrawable(context)
            ?.asEquitablePainter(background)
        IconAnimatablePainter(
            icon = icon,
            background = backgroundPainter,
            iconSize = null,
            iconTint = null
        )
    }
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitablePainter,
    background: IntColor? = null,
): IconAnimatablePainter = remember(icon, background) {
    val backgroundPainter = background
        ?.let { ColorPainter(Color(it.color)) }
        ?.asEquitable()
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
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitablePainter,
    background: EquitableDrawable? = null,
): IconAnimatablePainter = remember(icon, background) {
    val backgroundPainter = background?.asEquitablePainter()
    IconAnimatablePainter(
        icon = icon,
        background = backgroundPainter,
        iconSize = null,
        iconTint = null
    )
}


/* ********************************************* Drawable icon ********************************************* */

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithDrawableIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitableDrawable,
    background: EquitablePainter? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainter = remember(icon, background, iconSize, iconTint) {
    val iconPainter = icon.asEquitablePainter()
    IconAnimatablePainter(
        icon = iconPainter,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithDrawableIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitableDrawable,
    background: Color? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainter = remember(icon, background, iconSize, iconTint) {
    val iconPainter = icon.asEquitablePainter()
    val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
    IconAnimatablePainter(
        icon = iconPainter,
        background = backgroundPainter,
        iconSize = iconSize,
        iconTint = iconTint
    )
}


/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithDrawableIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitableDrawable,
    background: EquitablePainter? = null,
    iconSize: Size? = null,
): IconAnimatablePainter = remember(icon, background, iconSize) {
    val iconPainter = icon.asEquitablePainter()
    IconAnimatablePainter(
        icon = iconPainter,
        background = background,
        iconSize = iconSize,
        iconTint = null
    )
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithDrawableIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitableDrawable,
    background: Color? = null,
    iconSize: Size? = null,
): IconAnimatablePainter = remember(icon, background, iconSize) {
    val iconPainter = icon.asEquitablePainter()
    val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
    IconAnimatablePainter(
        icon = iconPainter,
        background = backgroundPainter,
        iconSize = iconSize,
        iconTint = null
    )
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithDrawableIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitableDrawable,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainter = remember(icon, iconSize, iconTint) {
    val iconPainter = icon.asEquitablePainter()
    IconAnimatablePainter(
        icon = iconPainter,
        background = null,
        iconSize = iconSize,
        iconTint = iconTint
    )
}


/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithDrawableIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitableDrawable,
    background: EquitablePainter? = null,
): IconAnimatablePainter = remember(icon, background) {
    val iconPainter = icon.asEquitablePainter()
    IconAnimatablePainter(
        icon = iconPainter,
        background = background,
        iconSize = null,
        iconTint = null
    )
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithDrawableIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitableDrawable,
    background: Color? = null,
): IconAnimatablePainter = remember(icon, background) {
    val iconPainter = icon.asEquitablePainter()
    val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
    IconAnimatablePainter(
        icon = iconPainter,
        background = backgroundPainter,
        iconSize = null,
        iconTint = null
    )
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithDrawableIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitableDrawable,
    iconSize: Size? = null,
): IconAnimatablePainter = remember(icon, iconSize) {
    val iconPainter = icon.asEquitablePainter()
    IconAnimatablePainter(
        icon = iconPainter,
        background = null,
        iconSize = iconSize,
        iconTint = null
    )
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithDrawableIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitableDrawable,
): IconAnimatablePainter = remember(icon) {
    val iconPainter = icon.asEquitablePainter()
    IconAnimatablePainter(
        icon = iconPainter,
        background = null,
        iconSize = null,
        iconTint = null
    )
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitableDrawable,
    background: EquitablePainter? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatablePainter {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = icon.asEquitablePainter()
        val iconTintColor = iconTint
            ?.let { ResColor(it) }
            ?.getColor(context)
            ?.let { Color(it) }
        IconAnimatablePainter(
            icon = iconPainter,
            background = background,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitableDrawable,
    background: EquitablePainter? = null,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconAnimatablePainter {
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = icon.asEquitablePainter()
        val iconTintColor = iconTint
            ?.color
            ?.let { Color(it) }
        IconAnimatablePainter(
            icon = iconPainter,
            background = background,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitableDrawable,
    background: Color? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatablePainter {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = icon.asEquitablePainter()
        val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
        val iconTintColor = iconTint
            ?.let { ResColor(it) }
            ?.getColor(context)
            ?.let { Color(it) }
        IconAnimatablePainter(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitableDrawable,
    background: Color? = null,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconAnimatablePainter {
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = icon.asEquitablePainter()
        val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
        val iconTintColor = iconTint
            ?.color
            ?.let { Color(it) }
        IconAnimatablePainter(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitableDrawable,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainter {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = icon.asEquitablePainter()
        val backgroundPainter = background
            ?.let { ResDrawable(it) }
            ?.getDrawable(context)
            ?.asEquitablePainter(background)
        IconAnimatablePainter(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitableDrawable,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatablePainter {
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
        IconAnimatablePainter(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitableDrawable,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconAnimatablePainter {
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
        IconAnimatablePainter(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitableDrawable,
    background: IntColor? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainter = remember(icon, background, iconSize, iconTint) {
    val iconPainter = icon.asEquitablePainter()
    val backgroundPainter = background
        ?.let { ColorPainter(Color(it.color)) }
        ?.asEquitable()
    IconAnimatablePainter(
        icon = iconPainter,
        background = backgroundPainter,
        iconSize = iconSize,
        iconTint = iconTint
    )
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitableDrawable,
    background: IntColor? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatablePainter {
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
        IconAnimatablePainter(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitableDrawable,
    background: IntColor? = null,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconAnimatablePainter = remember(icon, background, iconSize, iconTint) {
    val iconPainter = icon.asEquitablePainter()
    val backgroundPainter = background
        ?.let { ColorPainter(Color(it.color)) }
        ?.asEquitable()
    val iconTintColor = iconTint
        ?.color
        ?.let { Color(it) }
    IconAnimatablePainter(
        icon = iconPainter,
        background = backgroundPainter,
        iconSize = iconSize,
        iconTint = iconTintColor
    )
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitableDrawable,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainter = remember(icon, background, iconSize, iconTint) {
    val iconPainter = icon.asEquitablePainter()
    val backgroundPainter = background?.asEquitablePainter()
    IconAnimatablePainter(
        icon = iconPainter,
        background = backgroundPainter,
        iconSize = iconSize,
        iconTint = iconTint
    )
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitableDrawable,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatablePainter {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = icon.asEquitablePainter()
        val backgroundPainter = background?.asEquitablePainter()
        val iconTintColor = iconTint
            ?.let { ResColor(it) }
            ?.getColor(context)
            ?.let { Color(it) }
        IconAnimatablePainter(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitableDrawable,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconAnimatablePainter = remember(icon, background, iconSize, iconTint) {
    val iconPainter = icon.asEquitablePainter()
    val backgroundPainter = background?.asEquitablePainter()
    val iconTintColor = iconTint?.color?.let { Color(it) }
    IconAnimatablePainter(
        icon = iconPainter,
        background = backgroundPainter,
        iconSize = iconSize,
        iconTint = iconTintColor
    )
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitableDrawable,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
): IconAnimatablePainter {
    val context = LocalContext.current
    return remember(icon, background, iconSize) {
        val iconPainter = icon.asEquitablePainter()
        val backgroundPainter = background
            ?.let { ResDrawable(it) }
            ?.getDrawable(context)
            ?.asEquitablePainter(background)
        IconAnimatablePainter(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = null
        )
    }
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitableDrawable,
    background: IntColor? = null,
    iconSize: Size? = null,
): IconAnimatablePainter = remember(icon, background, iconSize) {
    val iconPainter = icon.asEquitablePainter()
    val backgroundPainter = background
        ?.let { ColorPainter(Color(it.color)) }
        ?.asEquitable()
    IconAnimatablePainter(
        icon = iconPainter,
        background = backgroundPainter,
        iconSize = iconSize,
        iconTint = null
    )
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitableDrawable,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
): IconAnimatablePainter = remember(icon, background, iconSize) {
    val iconPainter = icon.asEquitablePainter()
    val backgroundPainter = background?.asEquitablePainter()
    IconAnimatablePainter(
        icon = iconPainter,
        background = backgroundPainter,
        iconSize = iconSize,
        iconTint = null
    )
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitableDrawable,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatablePainter {
    val context = LocalContext.current
    return remember(icon, iconSize, iconTint) {
        val iconPainter = icon.asEquitablePainter()
        val iconTintColor = iconTint
            ?.let { ResColor(it) }
            ?.getColor(context)
            ?.let { Color(it) }
        IconAnimatablePainter(
            icon = iconPainter,
            background = null,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitableDrawable,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconAnimatablePainter = remember(icon, iconSize, iconTint) {
    val iconPainter = icon.asEquitablePainter()
    val iconTintColor = iconTint
        ?.color
        ?.let { Color(it) }
    IconAnimatablePainter(
        icon = iconPainter,
        background = null,
        iconSize = iconSize,
        iconTint = iconTintColor
    )
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitableDrawable,
    @DrawableRes background: Int? = null,
): IconAnimatablePainter {
    val context = LocalContext.current
    return remember(icon, background) {
        val iconPainter = icon.asEquitablePainter()
        val backgroundPainter = background
            ?.let { ResDrawable(it) }
            ?.getDrawable(context)
            ?.asEquitablePainter(background)
        IconAnimatablePainter(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = null,
            iconTint = null
        )
    }
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitableDrawable,
    background: IntColor? = null,
): IconAnimatablePainter = remember(icon, background) {
    val iconPainter = icon.asEquitablePainter()
    val backgroundPainter = background
        ?.let { ColorPainter(Color(it.color)) }
        ?.asEquitable()
    IconAnimatablePainter(
        icon = iconPainter,
        background = backgroundPainter,
        iconSize = null,
        iconTint = null
    )
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitableDrawable,
    background: EquitableDrawable? = null,
): IconAnimatablePainter = remember(icon, background) {
    val iconPainter = icon.asEquitablePainter()
    val backgroundPainter = background?.asEquitablePainter()
    IconAnimatablePainter(
        icon = iconPainter,
        background = backgroundPainter,
        iconSize = null,
        iconTint = null
    )
}


/* ********************************************* res icon ********************************************* */

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithResIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    @DrawableRes icon: Int,
    background: EquitablePainter? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainter {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
        IconAnimatablePainter(
            icon = iconPainter,
            background = background,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithResIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    @DrawableRes icon: Int,
    background: Color? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainter {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
        IconAnimatablePainter(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}


/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithResIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    @DrawableRes icon: Int,
    background: EquitablePainter? = null,
    iconSize: Size? = null,
): IconAnimatablePainter {
    val context = LocalContext.current
    return remember(icon, background, iconSize) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
        IconAnimatablePainter(
            icon = iconPainter,
            background = background,
            iconSize = iconSize,
            iconTint = null
        )
    }
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithResIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    @DrawableRes icon: Int,
    background: Color? = null,
    iconSize: Size? = null,
): IconAnimatablePainter {
    val context = LocalContext.current
    return remember(icon, background, iconSize) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
        IconAnimatablePainter(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = null
        )
    }
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithResIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    @DrawableRes icon: Int,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainter {
    val context = LocalContext.current
    return remember(icon, iconSize, iconTint) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
        IconAnimatablePainter(
            icon = iconPainter,
            background = null,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}


/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithResIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    @DrawableRes icon: Int,
    background: EquitablePainter? = null,
): IconAnimatablePainter {
    val context = LocalContext.current
    return remember(icon, background) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
        IconAnimatablePainter(
            icon = iconPainter,
            background = background,
            iconSize = null,
            iconTint = null
        )
    }
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithResIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    @DrawableRes icon: Int,
    background: Color? = null,
): IconAnimatablePainter {
    val context = LocalContext.current
    return remember(icon, background) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
        IconAnimatablePainter(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = null,
            iconTint = null
        )
    }
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithResIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    @DrawableRes icon: Int,
    iconSize: Size? = null,
): IconAnimatablePainter {
    val context = LocalContext.current
    return remember(icon, iconSize) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
        IconAnimatablePainter(
            icon = iconPainter,
            background = null,
            iconSize = iconSize,
            iconTint = null
        )
    }
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithResIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    @DrawableRes icon: Int,
): IconAnimatablePainter {
    val context = LocalContext.current
    return remember(icon) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
        IconAnimatablePainter(
            icon = iconPainter,
            background = null,
            iconSize = null,
            iconTint = null
        )
    }
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithResIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    @DrawableRes icon: Int,
    background: EquitablePainter? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatablePainter {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
        val iconTintColor = iconTint
            ?.let { ResColor(it) }
            ?.getColor(context)
            ?.let { Color(it) }
        IconAnimatablePainter(
            icon = iconPainter,
            background = background,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithResIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    @DrawableRes icon: Int,
    background: EquitablePainter? = null,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconAnimatablePainter {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
        val iconTintColor = iconTint
            ?.color
            ?.let { Color(it) }
        IconAnimatablePainter(
            icon = iconPainter,
            background = background,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithResIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    @DrawableRes icon: Int,
    background: Color? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatablePainter {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
        val iconTintColor = iconTint
            ?.let { ResColor(it) }
            ?.getColor(context)
            ?.let { Color(it) }
        IconAnimatablePainter(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithResIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    @DrawableRes icon: Int,
    background: Color? = null,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconAnimatablePainter {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
        val iconTintColor = iconTint
            ?.color
            ?.let { Color(it) }
        IconAnimatablePainter(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithResIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainter {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background
            ?.let { ResDrawable(it) }
            ?.getDrawable(context)
            ?.asEquitablePainter(background)
        IconAnimatablePainter(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithResIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatablePainter {
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
        IconAnimatablePainter(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithResIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconAnimatablePainter {
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
        IconAnimatablePainter(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithResIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    @DrawableRes icon: Int,
    background: IntColor? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainter {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background
            ?.let { ColorPainter(Color(it.color)) }
            ?.asEquitable()
        IconAnimatablePainter(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithResIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    @DrawableRes icon: Int,
    background: IntColor? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatablePainter {
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
        IconAnimatablePainter(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithResIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    @DrawableRes icon: Int,
    background: IntColor? = null,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconAnimatablePainter {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background
            ?.let { ColorPainter(Color(it.color)) }
            ?.asEquitable()
        val iconTintColor = iconTint
            ?.color
            ?.let { Color(it) }
        IconAnimatablePainter(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithResIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    @DrawableRes icon: Int,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainter {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background?.asEquitablePainter()
        IconAnimatablePainter(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithResIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    @DrawableRes icon: Int,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatablePainter {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background?.asEquitablePainter()
        val iconTintColor = iconTint
            ?.let { ResColor(it) }
            ?.getColor(context)
            ?.let { Color(it) }
        IconAnimatablePainter(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithResIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    @DrawableRes icon: Int,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconAnimatablePainter {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background?.asEquitablePainter()
        val iconTintColor = iconTint?.color?.let { Color(it) }
        IconAnimatablePainter(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithResIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
): IconAnimatablePainter {
    val context = LocalContext.current
    return remember(icon, background, iconSize) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background
            ?.let { ResDrawable(it) }
            ?.getDrawable(context)
            ?.asEquitablePainter(background)
        IconAnimatablePainter(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = null
        )
    }
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithResIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    @DrawableRes icon: Int,
    background: IntColor? = null,
    iconSize: Size? = null,
): IconAnimatablePainter {
    val context = LocalContext.current
    return remember(icon, background, iconSize) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background
            ?.let { ColorPainter(Color(it.color)) }
            ?.asEquitable()
        IconAnimatablePainter(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = null
        )
    }
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithResIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    @DrawableRes icon: Int,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
): IconAnimatablePainter {
    val context = LocalContext.current
    return remember(icon, background, iconSize) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background?.asEquitablePainter()
        IconAnimatablePainter(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = null
        )
    }
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithResIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    @DrawableRes icon: Int,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatablePainter {
    val context = LocalContext.current
    return remember(icon, iconSize, iconTint) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
        val iconTintColor = iconTint
            ?.let { ResColor(it) }
            ?.getColor(context)
            ?.let { Color(it) }
        IconAnimatablePainter(
            icon = iconPainter,
            background = null,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithResIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    @DrawableRes icon: Int,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconAnimatablePainter {
    val context = LocalContext.current
    return remember(icon, iconSize, iconTint) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
        val iconTintColor = iconTint
            ?.color
            ?.let { Color(it) }
        IconAnimatablePainter(
            icon = iconPainter,
            background = null,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithResIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
): IconAnimatablePainter {
    val context = LocalContext.current
    return remember(icon, background) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background
            ?.let { ResDrawable(it) }
            ?.getDrawable(context)
            ?.asEquitablePainter(background)
        IconAnimatablePainter(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = null,
            iconTint = null
        )
    }
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithResIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    @DrawableRes icon: Int,
    background: IntColor? = null,
): IconAnimatablePainter {
    val context = LocalContext.current
    return remember(icon, background) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background
            ?.let { ColorPainter(Color(it.color)) }
            ?.asEquitable()
        IconAnimatablePainter(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = null,
            iconTint = null
        )
    }
}

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithResIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    @DrawableRes icon: Int,
    background: EquitableDrawable? = null,
): IconAnimatablePainter {
    val context = LocalContext.current
    return remember(icon, background) {
        val iconPainter = ResDrawable(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background?.asEquitablePainter()
        IconAnimatablePainter(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = null,
            iconTint = null
        )
    }
}