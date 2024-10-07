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
 * Create a [IconAnimatableDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatableDrawableStateImageComposeAndroidTest.testRememberIconAnimatableDrawableStateImageWithDrawableIcon
 */
@Composable
fun rememberIconAnimatableDrawableStateImage(
    icon: EquitableDrawable,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatableDrawableStateImage = remember(icon, background, iconSize, iconTint) {
    IconAnimatableDrawableStateImage(
        icon = RealEquitableDrawable(icon),
        background = background?.let { RealEquitableDrawable(it) },
        iconSize = iconSize,
        iconTint = iconTint?.let { ResColor(it) }
    )
}

/**
 * Create a [IconAnimatableDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatableDrawableStateImageComposeAndroidTest.testRememberIconAnimatableDrawableStateImageWithDrawableIcon
 */
@Composable
fun rememberIconAnimatableDrawableStateImage(
    icon: EquitableDrawable,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconAnimatableDrawableStateImage = remember(icon, background, iconSize, iconTint) {
    IconAnimatableDrawableStateImage(
        icon = RealEquitableDrawable(icon),
        background = background?.let { RealEquitableDrawable(it) },
        iconSize = iconSize,
        iconTint = iconTint
    )
}

/**
 * Create a [IconAnimatableDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatableDrawableStateImageComposeAndroidTest.testRememberIconAnimatableDrawableStateImageWithDrawableIcon
 */
@Composable
fun rememberIconAnimatableDrawableStateImage(
    icon: EquitableDrawable,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatableDrawableStateImage = remember(icon, background, iconSize, iconTint) {
    IconAnimatableDrawableStateImage(
        icon = RealEquitableDrawable(icon),
        background = background?.let { ResDrawable(it) },
        iconSize = iconSize,
        iconTint = iconTint?.let { ResColor(it) }
    )
}

/**
 * Create a [IconAnimatableDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatableDrawableStateImageComposeAndroidTest.testRememberIconAnimatableDrawableStateImageWithDrawableIcon
 */
@Composable
fun rememberIconAnimatableDrawableStateImage(
    icon: EquitableDrawable,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconAnimatableDrawableStateImage = remember(icon, background, iconSize, iconTint) {
    IconAnimatableDrawableStateImage(
        icon = RealEquitableDrawable(icon),
        background = background?.let { ResDrawable(it) },
        iconSize = iconSize,
        iconTint = iconTint
    )
}

/**
 * Create a [IconAnimatableDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatableDrawableStateImageComposeAndroidTest.testRememberIconAnimatableDrawableStateImageWithDrawableIcon
 */
@Composable
fun rememberIconAnimatableDrawableStateImage(
    icon: EquitableDrawable,
    background: IntColor? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatableDrawableStateImage = remember(icon, background, iconSize, iconTint) {
    IconAnimatableDrawableStateImage(
        icon = RealEquitableDrawable(icon),
        background = background?.let { ColorFetcherDrawable(it) },
        iconSize = iconSize,
        iconTint = iconTint?.let { ResColor(it) }
    )
}

/**
 * Create a [IconAnimatableDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatableDrawableStateImageComposeAndroidTest.testRememberIconAnimatableDrawableStateImageWithDrawableIcon
 */
@Composable
fun rememberIconAnimatableDrawableStateImage(
    icon: EquitableDrawable,
    background: IntColor? = null,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconAnimatableDrawableStateImage = remember(icon, background, iconSize, iconTint) {
    IconAnimatableDrawableStateImage(
        icon = RealEquitableDrawable(icon),
        background = background?.let { ColorFetcherDrawable(it) },
        iconSize = iconSize,
        iconTint = iconTint
    )
}

/**
 * Create a [IconAnimatableDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatableDrawableStateImageComposeAndroidTest.testRememberIconAnimatableDrawableStateImageWithDrawableIcon
 */
@Composable
fun rememberIconAnimatableDrawableStateImage(
    icon: EquitableDrawable,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
): IconAnimatableDrawableStateImage = remember(icon, background, iconSize) {
    IconAnimatableDrawableStateImage(
        icon = RealEquitableDrawable(icon),
        background = background?.let { ResDrawable(it) },
        iconSize = iconSize,
        iconTint = null
    )
}

/**
 * Create a [IconAnimatableDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatableDrawableStateImageComposeAndroidTest.testRememberIconAnimatableDrawableStateImageWithDrawableIcon
 */
@Composable
fun rememberIconAnimatableDrawableStateImage(
    icon: EquitableDrawable,
    background: IntColor? = null,
    iconSize: Size? = null,
): IconAnimatableDrawableStateImage = remember(icon, background, iconSize) {
    IconAnimatableDrawableStateImage(
        icon = RealEquitableDrawable(icon),
        background = background?.let { ColorFetcherDrawable(it) },
        iconSize = iconSize,
        iconTint = null
    )
}

/**
 * Create a [IconAnimatableDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatableDrawableStateImageComposeAndroidTest.testRememberIconAnimatableDrawableStateImageWithDrawableIcon
 */
@Composable
fun rememberIconAnimatableDrawableStateImage(
    icon: EquitableDrawable,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
): IconAnimatableDrawableStateImage = remember(icon, background, iconSize) {
    IconAnimatableDrawableStateImage(
        icon = RealEquitableDrawable(icon),
        background = background?.let { RealEquitableDrawable(it) },
        iconSize = iconSize,
        iconTint = null
    )
}

/**
 * Create a [IconAnimatableDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatableDrawableStateImageComposeAndroidTest.testRememberIconAnimatableDrawableStateImageWithDrawableIcon
 */
@Composable
fun rememberIconAnimatableDrawableStateImage(
    icon: EquitableDrawable,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatableDrawableStateImage = remember(icon, iconSize, iconTint) {
    IconAnimatableDrawableStateImage(
        icon = RealEquitableDrawable(icon),
        background = null,
        iconSize = iconSize,
        iconTint = iconTint?.let { ResColor(it) }
    )
}

/**
 * Create a [IconAnimatableDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatableDrawableStateImageComposeAndroidTest.testRememberIconAnimatableDrawableStateImageWithDrawableIcon
 */
@Composable
fun rememberIconAnimatableDrawableStateImage(
    icon: EquitableDrawable,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconAnimatableDrawableStateImage = remember(icon, iconSize, iconTint) {
    IconAnimatableDrawableStateImage(
        icon = RealEquitableDrawable(icon),
        background = null,
        iconSize = iconSize,
        iconTint = iconTint
    )
}

/**
 * Create a [IconAnimatableDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatableDrawableStateImageComposeAndroidTest.testRememberIconAnimatableDrawableStateImageWithDrawableIcon
 */
@Composable
fun rememberIconAnimatableDrawableStateImage(
    icon: EquitableDrawable,
    @DrawableRes background: Int? = null,
): IconAnimatableDrawableStateImage = remember(icon, background) {
    IconAnimatableDrawableStateImage(
        icon = RealEquitableDrawable(icon),
        background = background?.let { ResDrawable(it) },
        iconSize = null,
        iconTint = null
    )
}

/**
 * Create a [IconAnimatableDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatableDrawableStateImageComposeAndroidTest.testRememberIconAnimatableDrawableStateImageWithDrawableIcon
 */
@Composable
fun rememberIconAnimatableDrawableStateImage(
    icon: EquitableDrawable,
    background: IntColor? = null,
): IconAnimatableDrawableStateImage = remember(icon, background) {
    IconAnimatableDrawableStateImage(
        icon = RealEquitableDrawable(icon),
        background = background?.let { ColorFetcherDrawable(it) },
        iconSize = null,
        iconTint = null
    )
}

/**
 * Create a [IconAnimatableDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatableDrawableStateImageComposeAndroidTest.testRememberIconAnimatableDrawableStateImageWithDrawableIcon
 */
@Composable
fun rememberIconAnimatableDrawableStateImage(
    icon: EquitableDrawable,
    background: EquitableDrawable? = null,
): IconAnimatableDrawableStateImage = remember(icon, background) {
    IconAnimatableDrawableStateImage(
        icon = RealEquitableDrawable(icon),
        background = background?.let { RealEquitableDrawable(it) },
        iconSize = null,
        iconTint = null
    )
}

/**
 * Create a [IconAnimatableDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatableDrawableStateImageComposeAndroidTest.testRememberIconAnimatableDrawableStateImageWithDrawableIcon
 */
@Composable
fun rememberIconAnimatableDrawableStateImage(
    icon: EquitableDrawable,
    iconSize: Size? = null,
): IconAnimatableDrawableStateImage = remember(icon, iconSize) {
    IconAnimatableDrawableStateImage(
        icon = RealEquitableDrawable(icon),
        background = null,
        iconSize = iconSize,
        iconTint = null
    )
}

/**
 * Create a [IconAnimatableDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatableDrawableStateImageComposeAndroidTest.testRememberIconAnimatableDrawableStateImageWithDrawableIcon
 */
@Composable
fun rememberIconAnimatableDrawableStateImage(
    icon: EquitableDrawable,
): IconAnimatableDrawableStateImage = remember(icon) {
    IconAnimatableDrawableStateImage(
        icon = RealEquitableDrawable(icon),
        background = null,
        iconSize = null,
        iconTint = null
    )
}


/* ********************************************* res icon ********************************************* */

/**
 * Create a [IconAnimatableDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatableDrawableStateImageComposeAndroidTest.testRememberIconAnimatableDrawableStateImageWithResIcon
 */
@Composable
fun rememberIconAnimatableDrawableStateImage(
    @DrawableRes icon: Int,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatableDrawableStateImage = remember(icon, background, iconSize, iconTint) {
    IconAnimatableDrawableStateImage(
        icon = ResDrawable(icon),
        background = background?.let { RealEquitableDrawable(it) },
        iconSize = iconSize,
        iconTint = iconTint?.let { ResColor(it) }
    )
}

/**
 * Create a [IconAnimatableDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatableDrawableStateImageComposeAndroidTest.testRememberIconAnimatableDrawableStateImageWithResIcon
 */
@Composable
fun rememberIconAnimatableDrawableStateImage(
    @DrawableRes icon: Int,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconAnimatableDrawableStateImage = remember(icon, background, iconSize, iconTint) {
    IconAnimatableDrawableStateImage(
        icon = ResDrawable(icon),
        background = background?.let { RealEquitableDrawable(it) },
        iconSize = iconSize,
        iconTint = iconTint
    )
}

/**
 * Create a [IconAnimatableDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatableDrawableStateImageComposeAndroidTest.testRememberIconAnimatableDrawableStateImageWithResIcon
 */
@Composable
fun rememberIconAnimatableDrawableStateImage(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatableDrawableStateImage = remember(icon, background, iconSize, iconTint) {
    IconAnimatableDrawableStateImage(
        icon = ResDrawable(icon),
        background = background?.let { ResDrawable(it) },
        iconSize = iconSize,
        iconTint = iconTint?.let { ResColor(it) }
    )
}

/**
 * Create a [IconAnimatableDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatableDrawableStateImageComposeAndroidTest.testRememberIconAnimatableDrawableStateImageWithResIcon
 */
@Composable
fun rememberIconAnimatableDrawableStateImage(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconAnimatableDrawableStateImage = remember(icon, background, iconSize, iconTint) {
    IconAnimatableDrawableStateImage(
        icon = ResDrawable(icon),
        background = background?.let { ResDrawable(it) },
        iconSize = iconSize,
        iconTint = iconTint
    )
}

/**
 * Create a [IconAnimatableDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatableDrawableStateImageComposeAndroidTest.testRememberIconAnimatableDrawableStateImageWithResIcon
 */
@Composable
fun rememberIconAnimatableDrawableStateImage(
    @DrawableRes icon: Int,
    background: IntColor? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatableDrawableStateImage = remember(icon, background, iconSize, iconTint) {
    IconAnimatableDrawableStateImage(
        icon = ResDrawable(icon),
        background = background?.let { ColorFetcherDrawable(it) },
        iconSize = iconSize,
        iconTint = iconTint?.let { ResColor(it) }
    )
}

/**
 * Create a [IconAnimatableDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatableDrawableStateImageComposeAndroidTest.testRememberIconAnimatableDrawableStateImageWithResIcon
 */
@Composable
fun rememberIconAnimatableDrawableStateImage(
    @DrawableRes icon: Int,
    background: IntColor? = null,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconAnimatableDrawableStateImage = remember(icon, background, iconSize, iconTint) {
    IconAnimatableDrawableStateImage(
        icon = ResDrawable(icon),
        background = background?.let { ColorFetcherDrawable(it) },
        iconSize = iconSize,
        iconTint = iconTint
    )
}

/**
 * Create a [IconAnimatableDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatableDrawableStateImageComposeAndroidTest.testRememberIconAnimatableDrawableStateImageWithResIcon
 */
@Composable
fun rememberIconAnimatableDrawableStateImage(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
): IconAnimatableDrawableStateImage = remember(icon, background, iconSize) {
    IconAnimatableDrawableStateImage(
        icon = ResDrawable(icon),
        background = background?.let { ResDrawable(it) },
        iconSize = iconSize,
        iconTint = null
    )
}

/**
 * Create a [IconAnimatableDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatableDrawableStateImageComposeAndroidTest.testRememberIconAnimatableDrawableStateImageWithResIcon
 */
@Composable
fun rememberIconAnimatableDrawableStateImage(
    @DrawableRes icon: Int,
    background: IntColor? = null,
    iconSize: Size? = null,
): IconAnimatableDrawableStateImage = remember(icon, background, iconSize) {
    IconAnimatableDrawableStateImage(
        icon = ResDrawable(icon),
        background = background?.let { ColorFetcherDrawable(it) },
        iconSize = iconSize,
        iconTint = null
    )
}

/**
 * Create a [IconAnimatableDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatableDrawableStateImageComposeAndroidTest.testRememberIconAnimatableDrawableStateImageWithResIcon
 */
@Composable
fun rememberIconAnimatableDrawableStateImage(
    @DrawableRes icon: Int,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
): IconAnimatableDrawableStateImage = remember(icon, background, iconSize) {
    IconAnimatableDrawableStateImage(
        icon = ResDrawable(icon),
        background = background?.let { RealEquitableDrawable(it) },
        iconSize = iconSize,
        iconTint = null
    )
}

/**
 * Create a [IconAnimatableDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatableDrawableStateImageComposeAndroidTest.testRememberIconAnimatableDrawableStateImageWithResIcon
 */
@Composable
fun rememberIconAnimatableDrawableStateImage(
    @DrawableRes icon: Int,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatableDrawableStateImage = remember(icon, iconSize, iconTint) {
    IconAnimatableDrawableStateImage(
        icon = ResDrawable(icon),
        background = null,
        iconSize = iconSize,
        iconTint = iconTint?.let { ResColor(it) }
    )
}

/**
 * Create a [IconAnimatableDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatableDrawableStateImageComposeAndroidTest.testRememberIconAnimatableDrawableStateImageWithResIcon
 */
@Composable
fun rememberIconAnimatableDrawableStateImage(
    @DrawableRes icon: Int,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconAnimatableDrawableStateImage = remember(icon, iconSize, iconTint) {
    IconAnimatableDrawableStateImage(
        icon = ResDrawable(icon),
        background = null,
        iconSize = iconSize,
        iconTint = iconTint
    )
}

/**
 * Create a [IconAnimatableDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatableDrawableStateImageComposeAndroidTest.testRememberIconAnimatableDrawableStateImageWithResIcon
 */
@Composable
fun rememberIconAnimatableDrawableStateImage(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
): IconAnimatableDrawableStateImage = remember(icon, background) {
    IconAnimatableDrawableStateImage(
        icon = ResDrawable(icon),
        background = background?.let { ResDrawable(it) },
        iconSize = null,
        iconTint = null
    )
}

/**
 * Create a [IconAnimatableDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatableDrawableStateImageComposeAndroidTest.testRememberIconAnimatableDrawableStateImageWithResIcon
 */
@Composable
fun rememberIconAnimatableDrawableStateImage(
    @DrawableRes icon: Int,
    background: IntColor? = null,
): IconAnimatableDrawableStateImage = remember(icon, background) {
    IconAnimatableDrawableStateImage(
        icon = ResDrawable(icon),
        background = background?.let { ColorFetcherDrawable(it) },
        iconSize = null,
        iconTint = null
    )
}

/**
 * Create a [IconAnimatableDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatableDrawableStateImageComposeAndroidTest.testRememberIconAnimatableDrawableStateImageWithResIcon
 */
@Composable
fun rememberIconAnimatableDrawableStateImage(
    @DrawableRes icon: Int,
    background: EquitableDrawable? = null,
): IconAnimatableDrawableStateImage = remember(icon, background) {
    IconAnimatableDrawableStateImage(
        icon = ResDrawable(icon),
        background = background?.let { RealEquitableDrawable(it) },
        iconSize = null,
        iconTint = null
    )
}

/**
 * Create a [IconAnimatableDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatableDrawableStateImageComposeAndroidTest.testRememberIconAnimatableDrawableStateImageWithResIcon
 */
@Composable
fun rememberIconAnimatableDrawableStateImage(
    @DrawableRes icon: Int,
    iconSize: Size? = null,
): IconAnimatableDrawableStateImage = remember(icon, iconSize) {
    IconAnimatableDrawableStateImage(
        icon = ResDrawable(icon),
        background = null,
        iconSize = iconSize,
        iconTint = null
    )
}

/**
 * Create a [IconAnimatableDrawableStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.IconAnimatableDrawableStateImageComposeAndroidTest.testRememberIconAnimatableDrawableStateImageWithResIcon
 */
@Composable
fun rememberIconAnimatableDrawableStateImage(
    @DrawableRes icon: Int,
): IconAnimatableDrawableStateImage = remember(icon) {
    IconAnimatableDrawableStateImage(
        icon = ResDrawable(icon),
        background = null,
        iconSize = null,
        iconTint = null
    )
}