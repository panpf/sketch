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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.platform.LocalContext
import com.github.panpf.sketch.drawable.EquitableDrawable
import com.github.panpf.sketch.drawable.ResDrawable
import com.github.panpf.sketch.painter.asEquitable
import com.github.panpf.sketch.util.IntColor
import com.github.panpf.sketch.util.ResColor
import com.github.panpf.sketch.util.SketchSize
import com.github.panpf.sketch.util.toSize


/* ********************************************* drawable icon ********************************************* */

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImage
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitableDrawable,
    background: EquitableDrawable? = null,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconTintColor = ResColor(iconTint)
            .getColor(context)
            .let { Color(it) }
        IconAnimatablePainterStateImage(
            icon = icon.asEquitablePainter(),
            background = background?.asEquitablePainter(),
            iconSize = iconSize?.toSize(),
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImage
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitableDrawable,
    @DrawableRes background: Int? = null,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val backgroundDrawable = background
            ?.let { ResDrawable(it) }
            ?.getDrawable(context)
        val iconTintColor = ResColor(iconTint)
            .getColor(context)
            .let { Color(it) }
        IconAnimatablePainterStateImage(
            icon = icon.asEquitablePainter(),
            background = backgroundDrawable?.asEquitablePainter(background),
            iconSize = iconSize?.toSize(),
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImage
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitableDrawable,
    background: IntColor? = null,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconTintColor = ResColor(iconTint)
            .getColor(context)
            .let { Color(it) }
        IconAnimatablePainterStateImage(
            icon = icon.asEquitablePainter(),
            background = background?.let { ColorPainter(Color(it.color)) }?.asEquitable(),
            iconSize = iconSize?.toSize(),
            iconTint = iconTintColor
        )
    }
}


/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImage
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitableDrawable,
    background: EquitableDrawable? = null,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): IconAnimatablePainterStateImage = remember(icon, background, iconSize, iconTint) {
    IconAnimatablePainterStateImage(
        icon = icon.asEquitablePainter(),
        background = background?.asEquitablePainter(),
        iconSize = iconSize?.toSize(),
        iconTint = iconTint?.let { Color(it.color) }
    )
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImage
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitableDrawable,
    @DrawableRes background: Int? = null,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val backgroundDrawable = background
            ?.let { ResDrawable(it) }
            ?.getDrawable(context)
        IconAnimatablePainterStateImage(
            icon = icon.asEquitablePainter(),
            background = backgroundDrawable?.asEquitablePainter(background),
            iconSize = iconSize?.toSize(),
            iconTint = iconTint?.let { Color(it.color) }
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImage
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitableDrawable,
    background: IntColor? = null,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): IconAnimatablePainterStateImage = remember(icon, background, iconSize, iconTint) {
    IconAnimatablePainterStateImage(
        icon = icon.asEquitablePainter(),
        background = background?.let { ColorPainter(Color(it.color)) }?.asEquitable(),
        iconSize = iconSize?.toSize(),
        iconTint = iconTint?.let { Color(it.color) }
    )
}


/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImage
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitableDrawable,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, iconSize, iconTint) {
        val iconTintColor = ResColor(iconTint)
            .getColor(context)
            .let { Color(it) }
        IconAnimatablePainterStateImage(
            icon = icon.asEquitablePainter(),
            background = null,
            iconSize = iconSize?.toSize(),
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImage
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitableDrawable,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): IconAnimatablePainterStateImage = remember(icon, iconSize, iconTint) {
    IconAnimatablePainterStateImage(
        icon = icon.asEquitablePainter(),
        background = null,
        iconSize = iconSize?.toSize(),
        iconTint = iconTint?.let { Color(it.color) }
    )
}


/* ********************************************* res icon ********************************************* */

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImage
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    @DrawableRes icon: Int,
    background: EquitableDrawable? = null,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconDrawable = ResDrawable(icon).getDrawable(context)
        val iconTintColor = ResColor(iconTint)
            .getColor(context)
            .let { Color(it) }
        IconAnimatablePainterStateImage(
            icon = iconDrawable.asEquitablePainter(icon),
            background = background?.asEquitablePainter(),
            iconSize = iconSize?.toSize(),
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImage
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconDrawable = ResDrawable(icon).getDrawable(context)
        val backgroundDrawable = background
            ?.let { ResDrawable(it) }
            ?.getDrawable(context)
        val iconTintColor = ResColor(iconTint)
            .getColor(context)
            .let { Color(it) }
        IconAnimatablePainterStateImage(
            icon = iconDrawable.asEquitablePainter(icon),
            background = backgroundDrawable?.asEquitablePainter(background),
            iconSize = iconSize?.toSize(),
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImage
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    @DrawableRes icon: Int,
    background: IntColor? = null,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconDrawable = ResDrawable(icon).getDrawable(context)
        val iconTintColor = ResColor(iconTint)
            .getColor(context)
            .let { Color(it) }
        IconAnimatablePainterStateImage(
            icon = iconDrawable.asEquitablePainter(icon),
            background = background?.let { ColorPainter(Color(it.color)) }?.asEquitable(),
            iconSize = iconSize?.toSize(),
            iconTint = iconTintColor
        )
    }
}


/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImage
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    @DrawableRes icon: Int,
    background: EquitableDrawable? = null,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconDrawable = ResDrawable(icon).getDrawable(context)
        IconAnimatablePainterStateImage(
            icon = iconDrawable.asEquitablePainter(icon),
            background = background?.asEquitablePainter(),
            iconSize = iconSize?.toSize(),
            iconTint = iconTint?.let { Color(it.color) }
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImage
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconDrawable = ResDrawable(icon).getDrawable(context)
        val backgroundDrawable = background
            ?.let { ResDrawable(it) }
            ?.getDrawable(context)
        IconAnimatablePainterStateImage(
            icon = iconDrawable.asEquitablePainter(icon),
            background = backgroundDrawable?.asEquitablePainter(background),
            iconSize = iconSize?.toSize(),
            iconTint = iconTint?.let { Color(it.color) }
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImage
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    @DrawableRes icon: Int,
    background: IntColor? = null,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, background, iconSize, iconTint) {
        val iconDrawable = ResDrawable(icon).getDrawable(context)
        IconAnimatablePainterStateImage(
            icon = iconDrawable.asEquitablePainter(icon),
            background = background?.let { ColorPainter(Color(it.color)) }?.asEquitable(),
            iconSize = iconSize?.toSize(),
            iconTint = iconTint?.let { Color(it.color) }
        )
    }
}


/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImage
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    @DrawableRes icon: Int,
    iconSize: SketchSize? = null,
    @ColorRes iconTint: Int,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, iconSize, iconTint) {
        val iconDrawable = ResDrawable(icon).getDrawable(context)
        val iconTintColor = ResColor(iconTint)
            .getColor(context)
            .let { Color(it) }
        IconAnimatablePainterStateImage(
            icon = iconDrawable.asEquitablePainter(icon),
            background = null,
            iconSize = iconSize?.toSize(),
            iconTint = iconTintColor
        )
    }
}

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImage
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    @DrawableRes icon: Int,
    iconSize: SketchSize? = null,
    iconTint: IntColor? = null,
): IconAnimatablePainterStateImage {
    val context = LocalContext.current
    return remember(icon, iconSize, iconTint) {
        val iconDrawable = ResDrawable(icon).getDrawable(context)
        IconAnimatablePainterStateImage(
            icon = iconDrawable.asEquitablePainter(icon),
            background = null,
            iconSize = iconSize?.toSize(),
            iconTint = iconTint?.let { Color(it.color) }
        )
    }
}