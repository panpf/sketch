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
import com.github.panpf.sketch.drawable.ResDrawableFetcher
import com.github.panpf.sketch.painter.EquitablePainter
import com.github.panpf.sketch.painter.asEquitable
import com.github.panpf.sketch.util.IntColorFetcher
import com.github.panpf.sketch.util.ResColorFetcher

/* ********************************************* Painter icon ********************************************* */

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitablePainter,
    background: EquitablePainter? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconTintColor = iconTint
            ?.let { ResColorFetcher(it) }
            ?.getColor(context)
            ?.let { Color(it) }
        IconAnimatablePainterStateImage(
            icon = icon,
            background = background,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitablePainter,
    background: EquitablePainter? = null,
    iconSize: Size? = null,
    iconTint: IntColorFetcher? = null,
): IconAnimatablePainterStateImage {
    return remember(icon, background, iconSize, iconTint) {
        val iconTintColor = iconTint
            ?.color
            ?.let { Color(it) }
        IconAnimatablePainterStateImage(
            icon = icon,
            background = background,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitablePainter,
    background: Color? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
        val iconTintColor = iconTint
            ?.let { ResColorFetcher(it) }
            ?.getColor(context)
            ?.let { Color(it) }
        IconAnimatablePainterStateImage(
            icon = icon,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitablePainter,
    background: Color? = null,
    iconSize: Size? = null,
    iconTint: IntColorFetcher? = null,
): IconAnimatablePainterStateImage {
    return remember(icon, background, iconSize, iconTint) {
        val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
        val iconTintColor = iconTint
            ?.color
            ?.let { Color(it) }
        IconAnimatablePainterStateImage(
            icon = icon,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitablePainter,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val backgroundPainter = background
            ?.let { ResDrawableFetcher(it) }
            ?.getDrawable(context)
            ?.asEquitablePainter(background)
        IconAnimatablePainterStateImage(
            icon = icon,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitablePainter,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val backgroundPainter = background
            ?.let { ResDrawableFetcher(it) }
            ?.getDrawable(context)
            ?.asEquitablePainter(background)
        val iconTintColor = iconTint
            ?.let { ResColorFetcher(it) }
            ?.getColor(context)
            ?.let { Color(it) }
        IconAnimatablePainterStateImage(
            icon = icon,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitablePainter,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
    iconTint: IntColorFetcher? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val backgroundPainter = background
            ?.let { ResDrawableFetcher(it) }
            ?.getDrawable(context)
            ?.asEquitablePainter(background)
        val iconTintColor = iconTint
            ?.color
            ?.let { Color(it) }
        IconAnimatablePainterStateImage(
            icon = icon,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitablePainter,
    background: IntColorFetcher? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainterStateImage = remember(icon, background, iconSize, iconTint) {
    val backgroundPainter = background
        ?.let { ColorPainter(Color(it.color)) }
        ?.asEquitable()
    IconAnimatablePainterStateImage(
        icon = icon,
        background = backgroundPainter,
        iconSize = iconSize,
        iconTint = iconTint
    )
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitablePainter,
    background: IntColorFetcher? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val backgroundPainter = background
            ?.let { ColorPainter(Color(it.color)) }
            ?.asEquitable()
        val iconTintColor = iconTint
            ?.let { ResColorFetcher(it) }
            ?.getColor(context)
            ?.let { Color(it) }
        IconAnimatablePainterStateImage(
            icon = icon,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitablePainter,
    background: IntColorFetcher? = null,
    iconSize: Size? = null,
    iconTint: IntColorFetcher? = null,
): IconAnimatablePainterStateImage = remember(icon, background, iconSize, iconTint) {
    val backgroundPainter = background
        ?.let { ColorPainter(Color(it.color)) }
        ?.asEquitable()
    val iconTintColor = iconTint
        ?.color
        ?.let { Color(it) }
    IconAnimatablePainterStateImage(
        icon = icon,
        background = backgroundPainter,
        iconSize = iconSize,
        iconTint = iconTintColor
    )
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitablePainter,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainterStateImage = remember(icon, background, iconSize, iconTint) {
    val backgroundPainter = background?.asEquitablePainter()
    IconAnimatablePainterStateImage(
        icon = icon,
        background = backgroundPainter,
        iconSize = iconSize,
        iconTint = iconTint
    )
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitablePainter,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val backgroundPainter = background?.asEquitablePainter()
        val iconTintColor = iconTint
            ?.let { ResColorFetcher(it) }
            ?.getColor(context)
            ?.let { Color(it) }
        IconAnimatablePainterStateImage(
            icon = icon,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitablePainter,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
    iconTint: IntColorFetcher? = null,
): IconAnimatablePainterStateImage = remember(icon, background, iconSize, iconTint) {
    val backgroundPainter = background?.asEquitablePainter()
    val iconTintColor = iconTint?.color?.let { Color(it) }
    IconAnimatablePainterStateImage(
        icon = icon,
        background = backgroundPainter,
        iconSize = iconSize,
        iconTint = iconTintColor
    )
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitablePainter,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize) {
        val backgroundPainter = background
            ?.let { ResDrawableFetcher(it) }
            ?.getDrawable(context)
            ?.asEquitablePainter(background)
        IconAnimatablePainterStateImage(
            icon = icon,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = null
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitablePainter,
    background: IntColorFetcher? = null,
    iconSize: Size? = null,
): IconAnimatablePainterStateImage = remember(icon, background, iconSize) {
    val backgroundPainter = background
        ?.let { ColorPainter(Color(it.color)) }
        ?.asEquitable()
    IconAnimatablePainterStateImage(
        icon = icon,
        background = backgroundPainter,
        iconSize = iconSize,
        iconTint = null
    )
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitablePainter,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
): IconAnimatablePainterStateImage = remember(icon, background, iconSize) {
    val backgroundPainter = background?.asEquitablePainter()
    IconAnimatablePainterStateImage(
        icon = icon,
        background = backgroundPainter,
        iconSize = iconSize,
        iconTint = null
    )
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitablePainter,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, iconSize, iconTint) {
        val iconTintColor = iconTint
            ?.let { ResColorFetcher(it) }
            ?.getColor(context)
            ?.let { Color(it) }
        IconAnimatablePainterStateImage(
            icon = icon,
            background = null,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitablePainter,
    iconSize: Size? = null,
    iconTint: IntColorFetcher? = null,
): IconAnimatablePainterStateImage = remember(icon, iconSize, iconTint) {
    val iconTintColor = iconTint
        ?.color
        ?.let { Color(it) }
    IconAnimatablePainterStateImage(
        icon = icon,
        background = null,
        iconSize = iconSize,
        iconTint = iconTintColor
    )
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitablePainter,
    @DrawableRes background: Int? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, background) {
        val backgroundPainter = background
            ?.let { ResDrawableFetcher(it) }
            ?.getDrawable(context)
            ?.asEquitablePainter(background)
        IconAnimatablePainterStateImage(
            icon = icon,
            background = backgroundPainter,
            iconSize = null,
            iconTint = null
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitablePainter,
    background: IntColorFetcher? = null,
): IconAnimatablePainterStateImage = remember(icon, background) {
    val backgroundPainter = background
        ?.let { ColorPainter(Color(it.color)) }
        ?.asEquitable()
    IconAnimatablePainterStateImage(
        icon = icon,
        background = backgroundPainter,
        iconSize = null,
        iconTint = null
    )
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitablePainter,
    background: EquitableDrawable? = null,
): IconAnimatablePainterStateImage = remember(icon, background) {
    val backgroundPainter = background?.asEquitablePainter()
    IconAnimatablePainterStateImage(
        icon = icon,
        background = backgroundPainter,
        iconSize = null,
        iconTint = null
    )
}


/* ********************************************* Drawable icon ********************************************* */

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithDrawableIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitableDrawable,
    background: EquitablePainter? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainterStateImage = remember(icon, background, iconSize, iconTint) {
    val iconPainter = icon.asEquitablePainter()
    IconAnimatablePainterStateImage(
        icon = iconPainter,
        background = background,
        iconSize = iconSize,
        iconTint = iconTint
    )
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithDrawableIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitableDrawable,
    background: Color? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainterStateImage = remember(icon, background, iconSize, iconTint) {
    val iconPainter = icon.asEquitablePainter()
    val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
    IconAnimatablePainterStateImage(
        icon = iconPainter,
        background = backgroundPainter,
        iconSize = iconSize,
        iconTint = iconTint
    )
}


/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithDrawableIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitableDrawable,
    background: EquitablePainter? = null,
    iconSize: Size? = null,
): IconAnimatablePainterStateImage = remember(icon, background, iconSize) {
    val iconPainter = icon.asEquitablePainter()
    IconAnimatablePainterStateImage(
        icon = iconPainter,
        background = background,
        iconSize = iconSize,
        iconTint = null
    )
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithDrawableIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitableDrawable,
    background: Color? = null,
    iconSize: Size? = null,
): IconAnimatablePainterStateImage = remember(icon, background, iconSize) {
    val iconPainter = icon.asEquitablePainter()
    val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
    IconAnimatablePainterStateImage(
        icon = iconPainter,
        background = backgroundPainter,
        iconSize = iconSize,
        iconTint = null
    )
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithDrawableIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitableDrawable,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainterStateImage = remember(icon, iconSize, iconTint) {
    val iconPainter = icon.asEquitablePainter()
    IconAnimatablePainterStateImage(
        icon = iconPainter,
        background = null,
        iconSize = iconSize,
        iconTint = iconTint
    )
}


/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithDrawableIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitableDrawable,
    background: EquitablePainter? = null,
): IconAnimatablePainterStateImage = remember(icon, background) {
    val iconPainter = icon.asEquitablePainter()
    IconAnimatablePainterStateImage(
        icon = iconPainter,
        background = background,
        iconSize = null,
        iconTint = null
    )
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithDrawableIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitableDrawable,
    background: Color? = null,
): IconAnimatablePainterStateImage = remember(icon, background) {
    val iconPainter = icon.asEquitablePainter()
    val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
    IconAnimatablePainterStateImage(
        icon = iconPainter,
        background = backgroundPainter,
        iconSize = null,
        iconTint = null
    )
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithDrawableIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitableDrawable,
    iconSize: Size? = null,
): IconAnimatablePainterStateImage = remember(icon, iconSize) {
    val iconPainter = icon.asEquitablePainter()
    IconAnimatablePainterStateImage(
        icon = iconPainter,
        background = null,
        iconSize = iconSize,
        iconTint = null
    )
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithDrawableIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitableDrawable,
): IconAnimatablePainterStateImage = remember(icon) {
    val iconPainter = icon.asEquitablePainter()
    IconAnimatablePainterStateImage(
        icon = iconPainter,
        background = null,
        iconSize = null,
        iconTint = null
    )
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitableDrawable,
    background: EquitablePainter? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = icon.asEquitablePainter()
        val iconTintColor = iconTint
            ?.let { ResColorFetcher(it) }
            ?.getColor(context)
            ?.let { Color(it) }
        IconAnimatablePainterStateImage(
            icon = iconPainter,
            background = background,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitableDrawable,
    background: EquitablePainter? = null,
    iconSize: Size? = null,
    iconTint: IntColorFetcher? = null,
): IconAnimatablePainterStateImage {
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = icon.asEquitablePainter()
        val iconTintColor = iconTint
            ?.color
            ?.let { Color(it) }
        IconAnimatablePainterStateImage(
            icon = iconPainter,
            background = background,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitableDrawable,
    background: Color? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = icon.asEquitablePainter()
        val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
        val iconTintColor = iconTint
            ?.let { ResColorFetcher(it) }
            ?.getColor(context)
            ?.let { Color(it) }
        IconAnimatablePainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitableDrawable,
    background: Color? = null,
    iconSize: Size? = null,
    iconTint: IntColorFetcher? = null,
): IconAnimatablePainterStateImage {
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = icon.asEquitablePainter()
        val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
        val iconTintColor = iconTint
            ?.color
            ?.let { Color(it) }
        IconAnimatablePainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitableDrawable,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = icon.asEquitablePainter()
        val backgroundPainter = background
            ?.let { ResDrawableFetcher(it) }
            ?.getDrawable(context)
            ?.asEquitablePainter(background)
        IconAnimatablePainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitableDrawable,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = icon.asEquitablePainter()
        val backgroundPainter = background
            ?.let { ResDrawableFetcher(it) }
            ?.getDrawable(context)
            ?.asEquitablePainter(background)
        val iconTintColor = iconTint
            ?.let { ResColorFetcher(it) }
            ?.getColor(context)
            ?.let { Color(it) }
        IconAnimatablePainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitableDrawable,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
    iconTint: IntColorFetcher? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = icon.asEquitablePainter()
        val backgroundPainter = background
            ?.let { ResDrawableFetcher(it) }
            ?.getDrawable(context)
            ?.asEquitablePainter(background)
        val iconTintColor = iconTint
            ?.color
            ?.let { Color(it) }
        IconAnimatablePainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitableDrawable,
    background: IntColorFetcher? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainterStateImage = remember(icon, background, iconSize, iconTint) {
    val iconPainter = icon.asEquitablePainter()
    val backgroundPainter = background
        ?.let { ColorPainter(Color(it.color)) }
        ?.asEquitable()
    IconAnimatablePainterStateImage(
        icon = iconPainter,
        background = backgroundPainter,
        iconSize = iconSize,
        iconTint = iconTint
    )
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitableDrawable,
    background: IntColorFetcher? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = icon.asEquitablePainter()
        val backgroundPainter = background
            ?.let { ColorPainter(Color(it.color)) }
            ?.asEquitable()
        val iconTintColor = iconTint
            ?.let { ResColorFetcher(it) }
            ?.getColor(context)
            ?.let { Color(it) }
        IconAnimatablePainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitableDrawable,
    background: IntColorFetcher? = null,
    iconSize: Size? = null,
    iconTint: IntColorFetcher? = null,
): IconAnimatablePainterStateImage = remember(icon, background, iconSize, iconTint) {
    val iconPainter = icon.asEquitablePainter()
    val backgroundPainter = background
        ?.let { ColorPainter(Color(it.color)) }
        ?.asEquitable()
    val iconTintColor = iconTint
        ?.color
        ?.let { Color(it) }
    IconAnimatablePainterStateImage(
        icon = iconPainter,
        background = backgroundPainter,
        iconSize = iconSize,
        iconTint = iconTintColor
    )
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitableDrawable,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainterStateImage = remember(icon, background, iconSize, iconTint) {
    val iconPainter = icon.asEquitablePainter()
    val backgroundPainter = background?.asEquitablePainter()
    IconAnimatablePainterStateImage(
        icon = iconPainter,
        background = backgroundPainter,
        iconSize = iconSize,
        iconTint = iconTint
    )
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitableDrawable,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = icon.asEquitablePainter()
        val backgroundPainter = background?.asEquitablePainter()
        val iconTintColor = iconTint
            ?.let { ResColorFetcher(it) }
            ?.getColor(context)
            ?.let { Color(it) }
        IconAnimatablePainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitableDrawable,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
    iconTint: IntColorFetcher? = null,
): IconAnimatablePainterStateImage = remember(icon, background, iconSize, iconTint) {
    val iconPainter = icon.asEquitablePainter()
    val backgroundPainter = background?.asEquitablePainter()
    val iconTintColor = iconTint?.color?.let { Color(it) }
    IconAnimatablePainterStateImage(
        icon = iconPainter,
        background = backgroundPainter,
        iconSize = iconSize,
        iconTint = iconTintColor
    )
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitableDrawable,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize) {
        val iconPainter = icon.asEquitablePainter()
        val backgroundPainter = background
            ?.let { ResDrawableFetcher(it) }
            ?.getDrawable(context)
            ?.asEquitablePainter(background)
        IconAnimatablePainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = null
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitableDrawable,
    background: IntColorFetcher? = null,
    iconSize: Size? = null,
): IconAnimatablePainterStateImage = remember(icon, background, iconSize) {
    val iconPainter = icon.asEquitablePainter()
    val backgroundPainter = background
        ?.let { ColorPainter(Color(it.color)) }
        ?.asEquitable()
    IconAnimatablePainterStateImage(
        icon = iconPainter,
        background = backgroundPainter,
        iconSize = iconSize,
        iconTint = null
    )
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitableDrawable,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
): IconAnimatablePainterStateImage = remember(icon, background, iconSize) {
    val iconPainter = icon.asEquitablePainter()
    val backgroundPainter = background?.asEquitablePainter()
    IconAnimatablePainterStateImage(
        icon = iconPainter,
        background = backgroundPainter,
        iconSize = iconSize,
        iconTint = null
    )
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitableDrawable,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, iconSize, iconTint) {
        val iconPainter = icon.asEquitablePainter()
        val iconTintColor = iconTint
            ?.let { ResColorFetcher(it) }
            ?.getColor(context)
            ?.let { Color(it) }
        IconAnimatablePainterStateImage(
            icon = iconPainter,
            background = null,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitableDrawable,
    iconSize: Size? = null,
    iconTint: IntColorFetcher? = null,
): IconAnimatablePainterStateImage = remember(icon, iconSize, iconTint) {
    val iconPainter = icon.asEquitablePainter()
    val iconTintColor = iconTint
        ?.color
        ?.let { Color(it) }
    IconAnimatablePainterStateImage(
        icon = iconPainter,
        background = null,
        iconSize = iconSize,
        iconTint = iconTintColor
    )
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitableDrawable,
    @DrawableRes background: Int? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, background) {
        val iconPainter = icon.asEquitablePainter()
        val backgroundPainter = background
            ?.let { ResDrawableFetcher(it) }
            ?.getDrawable(context)
            ?.asEquitablePainter(background)
        IconAnimatablePainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = null,
            iconTint = null
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitableDrawable,
    background: IntColorFetcher? = null,
): IconAnimatablePainterStateImage = remember(icon, background) {
    val iconPainter = icon.asEquitablePainter()
    val backgroundPainter = background
        ?.let { ColorPainter(Color(it.color)) }
        ?.asEquitable()
    IconAnimatablePainterStateImage(
        icon = iconPainter,
        background = backgroundPainter,
        iconSize = null,
        iconTint = null
    )
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitableDrawable,
    background: EquitableDrawable? = null,
): IconAnimatablePainterStateImage = remember(icon, background) {
    val iconPainter = icon.asEquitablePainter()
    val backgroundPainter = background?.asEquitablePainter()
    IconAnimatablePainterStateImage(
        icon = iconPainter,
        background = backgroundPainter,
        iconSize = null,
        iconTint = null
    )
}


/* ********************************************* res icon ********************************************* */

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithResIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    @DrawableRes icon: Int,
    background: EquitablePainter? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = ResDrawableFetcher(icon).getDrawable(context).asEquitablePainter(icon)
        IconAnimatablePainterStateImage(
            icon = iconPainter,
            background = background,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithResIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    @DrawableRes icon: Int,
    background: Color? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = ResDrawableFetcher(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
        IconAnimatablePainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}


/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithResIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    @DrawableRes icon: Int,
    background: EquitablePainter? = null,
    iconSize: Size? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize) {
        val iconPainter = ResDrawableFetcher(icon).getDrawable(context).asEquitablePainter(icon)
        IconAnimatablePainterStateImage(
            icon = iconPainter,
            background = background,
            iconSize = iconSize,
            iconTint = null
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithResIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    @DrawableRes icon: Int,
    background: Color? = null,
    iconSize: Size? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize) {
        val iconPainter = ResDrawableFetcher(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
        IconAnimatablePainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = null
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithResIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    @DrawableRes icon: Int,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, iconSize, iconTint) {
        val iconPainter = ResDrawableFetcher(icon).getDrawable(context).asEquitablePainter(icon)
        IconAnimatablePainterStateImage(
            icon = iconPainter,
            background = null,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}


/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithResIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    @DrawableRes icon: Int,
    background: EquitablePainter? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, background) {
        val iconPainter = ResDrawableFetcher(icon).getDrawable(context).asEquitablePainter(icon)
        IconAnimatablePainterStateImage(
            icon = iconPainter,
            background = background,
            iconSize = null,
            iconTint = null
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithResIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    @DrawableRes icon: Int,
    background: Color? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, background) {
        val iconPainter = ResDrawableFetcher(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
        IconAnimatablePainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = null,
            iconTint = null
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithResIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    @DrawableRes icon: Int,
    iconSize: Size? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, iconSize) {
        val iconPainter = ResDrawableFetcher(icon).getDrawable(context).asEquitablePainter(icon)
        IconAnimatablePainterStateImage(
            icon = iconPainter,
            background = null,
            iconSize = iconSize,
            iconTint = null
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithResIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    @DrawableRes icon: Int,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon) {
        val iconPainter = ResDrawableFetcher(icon).getDrawable(context).asEquitablePainter(icon)
        IconAnimatablePainterStateImage(
            icon = iconPainter,
            background = null,
            iconSize = null,
            iconTint = null
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithResIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    @DrawableRes icon: Int,
    background: EquitablePainter? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = ResDrawableFetcher(icon).getDrawable(context).asEquitablePainter(icon)
        val iconTintColor = iconTint
            ?.let { ResColorFetcher(it) }
            ?.getColor(context)
            ?.let { Color(it) }
        IconAnimatablePainterStateImage(
            icon = iconPainter,
            background = background,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithResIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    @DrawableRes icon: Int,
    background: EquitablePainter? = null,
    iconSize: Size? = null,
    iconTint: IntColorFetcher? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = ResDrawableFetcher(icon).getDrawable(context).asEquitablePainter(icon)
        val iconTintColor = iconTint
            ?.color
            ?.let { Color(it) }
        IconAnimatablePainterStateImage(
            icon = iconPainter,
            background = background,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithResIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    @DrawableRes icon: Int,
    background: Color? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = ResDrawableFetcher(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
        val iconTintColor = iconTint
            ?.let { ResColorFetcher(it) }
            ?.getColor(context)
            ?.let { Color(it) }
        IconAnimatablePainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithResIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    @DrawableRes icon: Int,
    background: Color? = null,
    iconSize: Size? = null,
    iconTint: IntColorFetcher? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = ResDrawableFetcher(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
        val iconTintColor = iconTint
            ?.color
            ?.let { Color(it) }
        IconAnimatablePainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithResIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = ResDrawableFetcher(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background
            ?.let { ResDrawableFetcher(it) }
            ?.getDrawable(context)
            ?.asEquitablePainter(background)
        IconAnimatablePainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithResIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = ResDrawableFetcher(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background
            ?.let { ResDrawableFetcher(it) }
            ?.getDrawable(context)
            ?.asEquitablePainter(background)
        val iconTintColor = iconTint
            ?.let { ResColorFetcher(it) }
            ?.getColor(context)
            ?.let { Color(it) }
        IconAnimatablePainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithResIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
    iconTint: IntColorFetcher? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = ResDrawableFetcher(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background
            ?.let { ResDrawableFetcher(it) }
            ?.getDrawable(context)
            ?.asEquitablePainter(background)
        val iconTintColor = iconTint
            ?.color
            ?.let { Color(it) }
        IconAnimatablePainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithResIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    @DrawableRes icon: Int,
    background: IntColorFetcher? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = ResDrawableFetcher(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background
            ?.let { ColorPainter(Color(it.color)) }
            ?.asEquitable()
        IconAnimatablePainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithResIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    @DrawableRes icon: Int,
    background: IntColorFetcher? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = ResDrawableFetcher(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background
            ?.let { ColorPainter(Color(it.color)) }
            ?.asEquitable()
        val iconTintColor = iconTint
            ?.let { ResColorFetcher(it) }
            ?.getColor(context)
            ?.let { Color(it) }
        IconAnimatablePainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithResIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    @DrawableRes icon: Int,
    background: IntColorFetcher? = null,
    iconSize: Size? = null,
    iconTint: IntColorFetcher? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = ResDrawableFetcher(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background
            ?.let { ColorPainter(Color(it.color)) }
            ?.asEquitable()
        val iconTintColor = iconTint
            ?.color
            ?.let { Color(it) }
        IconAnimatablePainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithResIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    @DrawableRes icon: Int,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = ResDrawableFetcher(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background?.asEquitablePainter()
        IconAnimatablePainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTint
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithResIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    @DrawableRes icon: Int,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = ResDrawableFetcher(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background?.asEquitablePainter()
        val iconTintColor = iconTint
            ?.let { ResColorFetcher(it) }
            ?.getColor(context)
            ?.let { Color(it) }
        IconAnimatablePainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithResIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    @DrawableRes icon: Int,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
    iconTint: IntColorFetcher? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconPainter = ResDrawableFetcher(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background?.asEquitablePainter()
        val iconTintColor = iconTint?.color?.let { Color(it) }
        IconAnimatablePainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithResIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize) {
        val iconPainter = ResDrawableFetcher(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background
            ?.let { ResDrawableFetcher(it) }
            ?.getDrawable(context)
            ?.asEquitablePainter(background)
        IconAnimatablePainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = null
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithResIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    @DrawableRes icon: Int,
    background: IntColorFetcher? = null,
    iconSize: Size? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize) {
        val iconPainter = ResDrawableFetcher(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background
            ?.let { ColorPainter(Color(it.color)) }
            ?.asEquitable()
        IconAnimatablePainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = null
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithResIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    @DrawableRes icon: Int,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize) {
        val iconPainter = ResDrawableFetcher(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background?.asEquitablePainter()
        IconAnimatablePainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = iconSize,
            iconTint = null
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithResIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    @DrawableRes icon: Int,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, iconSize, iconTint) {
        val iconPainter = ResDrawableFetcher(icon).getDrawable(context).asEquitablePainter(icon)
        val iconTintColor = iconTint
            ?.let { ResColorFetcher(it) }
            ?.getColor(context)
            ?.let { Color(it) }
        IconAnimatablePainterStateImage(
            icon = iconPainter,
            background = null,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithResIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    @DrawableRes icon: Int,
    iconSize: Size? = null,
    iconTint: IntColorFetcher? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, iconSize, iconTint) {
        val iconPainter = ResDrawableFetcher(icon).getDrawable(context).asEquitablePainter(icon)
        val iconTintColor = iconTint
            ?.color
            ?.let { Color(it) }
        IconAnimatablePainterStateImage(
            icon = iconPainter,
            background = null,
            iconSize = iconSize,
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithResIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, background) {
        val iconPainter = ResDrawableFetcher(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background
            ?.let { ResDrawableFetcher(it) }
            ?.getDrawable(context)
            ?.asEquitablePainter(background)
        IconAnimatablePainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = null,
            iconTint = null
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithResIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    @DrawableRes icon: Int,
    background: IntColorFetcher? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, background) {
        val iconPainter = ResDrawableFetcher(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background
            ?.let { ColorPainter(Color(it.color)) }
            ?.asEquitable()
        IconAnimatablePainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = null,
            iconTint = null
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithResIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    @DrawableRes icon: Int,
    background: EquitableDrawable? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, background) {
        val iconPainter = ResDrawableFetcher(icon).getDrawable(context).asEquitablePainter(icon)
        val backgroundPainter = background?.asEquitablePainter()
        IconAnimatablePainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = null,
            iconTint = null
        )
    }
}