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
import com.github.panpf.sketch.drawable.ColorFetcherDrawable
import com.github.panpf.sketch.drawable.EquitableDrawable
import com.github.panpf.sketch.drawable.RealEquitableDrawable
import com.github.panpf.sketch.drawable.ResDrawable
import com.github.panpf.sketch.util.IntColor
import com.github.panpf.sketch.util.ResColor
import com.github.panpf.sketch.util.Size

/* ********************************************* Drawable icon ********************************************* */

/**
 * Create a [IconDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconDrawableStateImageComposeAndroidTest.testRememberIconDrawableStateImageWithDrawableIcon
 */
@Composable
fun rememberIconDrawableStateImage(
    icon: EquitableDrawable,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconDrawableStateImage = remember(icon, background, iconSize, iconTint) {
    IconDrawableStateImage(
        icon = RealEquitableDrawable(icon),
        background = background?.let { RealEquitableDrawable(it) },
        iconSize = iconSize,
        iconTint = iconTint?.let { ResColor(it) }
    )
}

/**
 * Create a [IconDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconDrawableStateImageComposeAndroidTest.testRememberIconDrawableStateImageWithDrawableIcon
 */
@Composable
fun rememberIconDrawableStateImage(
    icon: EquitableDrawable,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconDrawableStateImage = remember(icon, background, iconSize, iconTint) {
    IconDrawableStateImage(
        icon = RealEquitableDrawable(icon),
        background = background?.let { RealEquitableDrawable(it) },
        iconSize = iconSize,
        iconTint = iconTint
    )
}

/**
 * Create a [IconDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconDrawableStateImageComposeAndroidTest.testRememberIconDrawableStateImageWithDrawableIcon
 */
@Composable
fun rememberIconDrawableStateImage(
    icon: EquitableDrawable,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconDrawableStateImage = remember(icon, background, iconSize, iconTint) {
    IconDrawableStateImage(
        icon = RealEquitableDrawable(icon),
        background = background?.let { ResDrawable(it) },
        iconSize = iconSize,
        iconTint = iconTint?.let { ResColor(it) }
    )
}

/**
 * Create a [IconDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconDrawableStateImageComposeAndroidTest.testRememberIconDrawableStateImageWithDrawableIcon
 */
@Composable
fun rememberIconDrawableStateImage(
    icon: EquitableDrawable,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconDrawableStateImage = remember(icon, background, iconSize, iconTint) {
    IconDrawableStateImage(
        icon = RealEquitableDrawable(icon),
        background = background?.let { ResDrawable(it) },
        iconSize = iconSize,
        iconTint = iconTint
    )
}

/**
 * Create a [IconDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconDrawableStateImageComposeAndroidTest.testRememberIconDrawableStateImageWithDrawableIcon
 */
@Composable
fun rememberIconDrawableStateImage(
    icon: EquitableDrawable,
    background: IntColor? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconDrawableStateImage = remember(icon, background, iconSize, iconTint) {
    IconDrawableStateImage(
        icon = RealEquitableDrawable(icon),
        background = background?.let { ColorFetcherDrawable(it) },
        iconSize = iconSize,
        iconTint = iconTint?.let { ResColor(it) }
    )
}

/**
 * Create a [IconDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconDrawableStateImageComposeAndroidTest.testRememberIconDrawableStateImageWithDrawableIcon
 */
@Composable
fun rememberIconDrawableStateImage(
    icon: EquitableDrawable,
    background: IntColor? = null,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconDrawableStateImage = remember(icon, background, iconSize, iconTint) {
    IconDrawableStateImage(
        icon = RealEquitableDrawable(icon),
        background = background?.let { ColorFetcherDrawable(it) },
        iconSize = iconSize,
        iconTint = iconTint
    )
}

/**
 * Create a [IconDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconDrawableStateImageComposeAndroidTest.testRememberIconDrawableStateImageWithDrawableIcon
 */
@Composable
fun rememberIconDrawableStateImage(
    icon: EquitableDrawable,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
): IconDrawableStateImage = remember(icon, background, iconSize) {
    IconDrawableStateImage(
        icon = RealEquitableDrawable(icon),
        background = background?.let { ResDrawable(it) },
        iconSize = iconSize,
        iconTint = null
    )
}

/**
 * Create a [IconDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconDrawableStateImageComposeAndroidTest.testRememberIconDrawableStateImageWithDrawableIcon
 */
@Composable
fun rememberIconDrawableStateImage(
    icon: EquitableDrawable,
    background: IntColor? = null,
    iconSize: Size? = null,
): IconDrawableStateImage = remember(icon, background, iconSize) {
    IconDrawableStateImage(
        icon = RealEquitableDrawable(icon),
        background = background?.let { ColorFetcherDrawable(it) },
        iconSize = iconSize,
        iconTint = null
    )
}

/**
 * Create a [IconDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconDrawableStateImageComposeAndroidTest.testRememberIconDrawableStateImageWithDrawableIcon
 */
@Composable
fun rememberIconDrawableStateImage(
    icon: EquitableDrawable,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
): IconDrawableStateImage = remember(icon, background, iconSize) {
    IconDrawableStateImage(
        icon = RealEquitableDrawable(icon),
        background = background?.let { RealEquitableDrawable(it) },
        iconSize = iconSize,
        iconTint = null
    )
}

/**
 * Create a [IconDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconDrawableStateImageComposeAndroidTest.testRememberIconDrawableStateImageWithDrawableIcon
 */
@Composable
fun rememberIconDrawableStateImage(
    icon: EquitableDrawable,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconDrawableStateImage = remember(icon, iconSize, iconTint) {
    IconDrawableStateImage(
        icon = RealEquitableDrawable(icon),
        background = null,
        iconSize = iconSize,
        iconTint = iconTint?.let { ResColor(it) }
    )
}

/**
 * Create a [IconDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconDrawableStateImageComposeAndroidTest.testRememberIconDrawableStateImageWithDrawableIcon
 */
@Composable
fun rememberIconDrawableStateImage(
    icon: EquitableDrawable,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconDrawableStateImage = remember(icon, iconSize, iconTint) {
    IconDrawableStateImage(
        icon = RealEquitableDrawable(icon),
        background = null,
        iconSize = iconSize,
        iconTint = iconTint
    )
}

/**
 * Create a [IconDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconDrawableStateImageComposeAndroidTest.testRememberIconDrawableStateImageWithDrawableIcon
 */
@Composable
fun rememberIconDrawableStateImage(
    icon: EquitableDrawable,
    @DrawableRes background: Int? = null,
): IconDrawableStateImage = remember(icon, background) {
    IconDrawableStateImage(
        icon = RealEquitableDrawable(icon),
        background = background?.let { ResDrawable(it) },
        iconSize = null,
        iconTint = null
    )
}

/**
 * Create a [IconDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconDrawableStateImageComposeAndroidTest.testRememberIconDrawableStateImageWithDrawableIcon
 */
@Composable
fun rememberIconDrawableStateImage(
    icon: EquitableDrawable,
    background: IntColor? = null,
): IconDrawableStateImage = remember(icon, background) {
    IconDrawableStateImage(
        icon = RealEquitableDrawable(icon),
        background = background?.let { ColorFetcherDrawable(it) },
        iconSize = null,
        iconTint = null
    )
}

/**
 * Create a [IconDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconDrawableStateImageComposeAndroidTest.testRememberIconDrawableStateImageWithDrawableIcon
 */
@Composable
fun rememberIconDrawableStateImage(
    icon: EquitableDrawable,
    background: EquitableDrawable? = null,
): IconDrawableStateImage = remember(icon, background) {
    IconDrawableStateImage(
        icon = RealEquitableDrawable(icon),
        background = background?.let { RealEquitableDrawable(it) },
        iconSize = null,
        iconTint = null
    )
}

/**
 * Create a [IconDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconDrawableStateImageComposeAndroidTest.testRememberIconDrawableStateImageWithDrawableIcon
 */
@Composable
fun rememberIconDrawableStateImage(
    icon: EquitableDrawable,
    iconSize: Size? = null,
): IconDrawableStateImage = remember(icon, iconSize) {
    IconDrawableStateImage(
        icon = RealEquitableDrawable(icon),
        background = null,
        iconSize = iconSize,
        iconTint = null
    )
}

/**
 * Create a [IconDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconDrawableStateImageComposeAndroidTest.testRememberIconDrawableStateImageWithDrawableIcon
 */
@Composable
fun rememberIconDrawableStateImage(
    icon: EquitableDrawable,
): IconDrawableStateImage = remember(icon) {
    IconDrawableStateImage(
        icon = RealEquitableDrawable(icon),
        background = null,
        iconSize = null,
        iconTint = null
    )
}


/* ********************************************* res icon ********************************************* */

/**
 * Create a [IconDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconDrawableStateImageComposeAndroidTest.testRememberIconDrawableStateImageWithResIcon
 */
@Composable
fun rememberIconDrawableStateImage(
    @DrawableRes icon: Int,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconDrawableStateImage = remember(icon, background, iconSize, iconTint) {
    IconDrawableStateImage(
        icon = ResDrawable(icon),
        background = background?.let { RealEquitableDrawable(it) },
        iconSize = iconSize,
        iconTint = iconTint?.let { ResColor(it) }
    )
}

/**
 * Create a [IconDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconDrawableStateImageComposeAndroidTest.testRememberIconDrawableStateImageWithResIcon
 */
@Composable
fun rememberIconDrawableStateImage(
    @DrawableRes icon: Int,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconDrawableStateImage = remember(icon, background, iconSize, iconTint) {
    IconDrawableStateImage(
        icon = ResDrawable(icon),
        background = background?.let { RealEquitableDrawable(it) },
        iconSize = iconSize,
        iconTint = iconTint
    )
}

/**
 * Create a [IconDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconDrawableStateImageComposeAndroidTest.testRememberIconDrawableStateImageWithResIcon
 */
@Composable
fun rememberIconDrawableStateImage(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconDrawableStateImage = remember(icon, background, iconSize, iconTint) {
    IconDrawableStateImage(
        icon = ResDrawable(icon),
        background = background?.let { ResDrawable(it) },
        iconSize = iconSize,
        iconTint = iconTint?.let { ResColor(it) }
    )
}

/**
 * Create a [IconDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconDrawableStateImageComposeAndroidTest.testRememberIconDrawableStateImageWithResIcon
 */
@Composable
fun rememberIconDrawableStateImage(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconDrawableStateImage = remember(icon, background, iconSize, iconTint) {
    IconDrawableStateImage(
        icon = ResDrawable(icon),
        background = background?.let { ResDrawable(it) },
        iconSize = iconSize,
        iconTint = iconTint
    )
}

/**
 * Create a [IconDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconDrawableStateImageComposeAndroidTest.testRememberIconDrawableStateImageWithResIcon
 */
@Composable
fun rememberIconDrawableStateImage(
    @DrawableRes icon: Int,
    background: IntColor? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconDrawableStateImage = remember(icon, background, iconSize, iconTint) {
    IconDrawableStateImage(
        icon = ResDrawable(icon),
        background = background?.let { ColorFetcherDrawable(it) },
        iconSize = iconSize,
        iconTint = iconTint?.let { ResColor(it) }
    )
}

/**
 * Create a [IconDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconDrawableStateImageComposeAndroidTest.testRememberIconDrawableStateImageWithResIcon
 */
@Composable
fun rememberIconDrawableStateImage(
    @DrawableRes icon: Int,
    background: IntColor? = null,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconDrawableStateImage = remember(icon, background, iconSize, iconTint) {
    IconDrawableStateImage(
        icon = ResDrawable(icon),
        background = background?.let { ColorFetcherDrawable(it) },
        iconSize = iconSize,
        iconTint = iconTint
    )
}

/**
 * Create a [IconDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconDrawableStateImageComposeAndroidTest.testRememberIconDrawableStateImageWithResIcon
 */
@Composable
fun rememberIconDrawableStateImage(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
): IconDrawableStateImage = remember(icon, background, iconSize) {
    IconDrawableStateImage(
        icon = ResDrawable(icon),
        background = background?.let { ResDrawable(it) },
        iconSize = iconSize,
        iconTint = null
    )
}

/**
 * Create a [IconDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconDrawableStateImageComposeAndroidTest.testRememberIconDrawableStateImageWithResIcon
 */
@Composable
fun rememberIconDrawableStateImage(
    @DrawableRes icon: Int,
    background: IntColor? = null,
    iconSize: Size? = null,
): IconDrawableStateImage = remember(icon, background, iconSize) {
    IconDrawableStateImage(
        icon = ResDrawable(icon),
        background = background?.let { ColorFetcherDrawable(it) },
        iconSize = iconSize,
        iconTint = null
    )
}

/**
 * Create a [IconDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconDrawableStateImageComposeAndroidTest.testRememberIconDrawableStateImageWithResIcon
 */
@Composable
fun rememberIconDrawableStateImage(
    @DrawableRes icon: Int,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
): IconDrawableStateImage = remember(icon, background, iconSize) {
    IconDrawableStateImage(
        icon = ResDrawable(icon),
        background = background?.let { RealEquitableDrawable(it) },
        iconSize = iconSize,
        iconTint = null
    )
}

/**
 * Create a [IconDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconDrawableStateImageComposeAndroidTest.testRememberIconDrawableStateImageWithResIcon
 */
@Composable
fun rememberIconDrawableStateImage(
    @DrawableRes icon: Int,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconDrawableStateImage = remember(icon, iconSize, iconTint) {
    IconDrawableStateImage(
        icon = ResDrawable(icon),
        background = null,
        iconSize = iconSize,
        iconTint = iconTint?.let { ResColor(it) }
    )
}

/**
 * Create a [IconDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconDrawableStateImageComposeAndroidTest.testRememberIconDrawableStateImageWithResIcon
 */
@Composable
fun rememberIconDrawableStateImage(
    @DrawableRes icon: Int,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconDrawableStateImage = remember(icon, iconSize, iconTint) {
    IconDrawableStateImage(
        icon = ResDrawable(icon),
        background = null,
        iconSize = iconSize,
        iconTint = iconTint
    )
}

/**
 * Create a [IconDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconDrawableStateImageComposeAndroidTest.testRememberIconDrawableStateImageWithResIcon
 */
@Composable
fun rememberIconDrawableStateImage(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
): IconDrawableStateImage = remember(icon, background) {
    IconDrawableStateImage(
        icon = ResDrawable(icon),
        background = background?.let { ResDrawable(it) },
        iconSize = null,
        iconTint = null
    )
}

/**
 * Create a [IconDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconDrawableStateImageComposeAndroidTest.testRememberIconDrawableStateImageWithResIcon
 */
@Composable
fun rememberIconDrawableStateImage(
    @DrawableRes icon: Int,
    background: IntColor? = null,
): IconDrawableStateImage = remember(icon, background) {
    IconDrawableStateImage(
        icon = ResDrawable(icon),
        background = background?.let { ColorFetcherDrawable(it) },
        iconSize = null,
        iconTint = null
    )
}

/**
 * Create a [IconDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconDrawableStateImageComposeAndroidTest.testRememberIconDrawableStateImageWithResIcon
 */
@Composable
fun rememberIconDrawableStateImage(
    @DrawableRes icon: Int,
    background: EquitableDrawable? = null,
): IconDrawableStateImage = remember(icon, background) {
    IconDrawableStateImage(
        icon = ResDrawable(icon),
        background = background?.let { RealEquitableDrawable(it) },
        iconSize = null,
        iconTint = null
    )
}

/**
 * Create a [IconDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconDrawableStateImageComposeAndroidTest.testRememberIconDrawableStateImageWithResIcon
 */
@Composable
fun rememberIconDrawableStateImage(
    @DrawableRes icon: Int,
    iconSize: Size? = null,
): IconDrawableStateImage = remember(icon, iconSize) {
    IconDrawableStateImage(
        icon = ResDrawable(icon),
        background = null,
        iconSize = iconSize,
        iconTint = null
    )
}

/**
 * Create a [IconDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconDrawableStateImageComposeAndroidTest.testRememberIconDrawableStateImageWithResIcon
 */
@Composable
fun rememberIconDrawableStateImage(
    @DrawableRes icon: Int,
): IconDrawableStateImage = remember(icon) {
    IconDrawableStateImage(
        icon = ResDrawable(icon),
        background = null,
        iconSize = null,
        iconTint = null
    )
}