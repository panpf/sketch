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
import com.github.panpf.sketch.drawable.ColorFetcherDrawableFetcher
import com.github.panpf.sketch.drawable.EquitableDrawable
import com.github.panpf.sketch.drawable.RealDrawableFetcher
import com.github.panpf.sketch.drawable.ResDrawableFetcher
import com.github.panpf.sketch.util.IntColorFetcher
import com.github.panpf.sketch.util.ResColorFetcher
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
        icon = RealDrawableFetcher(icon),
        background = background?.let { RealDrawableFetcher(it) },
        iconSize = iconSize,
        iconTint = iconTint?.let { ResColorFetcher(it) }
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
    iconTint: IntColorFetcher? = null,
): IconAnimatableDrawableStateImage = remember(icon, background, iconSize, iconTint) {
    IconAnimatableDrawableStateImage(
        icon = RealDrawableFetcher(icon),
        background = background?.let { RealDrawableFetcher(it) },
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
        icon = RealDrawableFetcher(icon),
        background = background?.let { ResDrawableFetcher(it) },
        iconSize = iconSize,
        iconTint = iconTint?.let { ResColorFetcher(it) }
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
    iconTint: IntColorFetcher? = null,
): IconAnimatableDrawableStateImage = remember(icon, background, iconSize, iconTint) {
    IconAnimatableDrawableStateImage(
        icon = RealDrawableFetcher(icon),
        background = background?.let { ResDrawableFetcher(it) },
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
    background: IntColorFetcher? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatableDrawableStateImage = remember(icon, background, iconSize, iconTint) {
    IconAnimatableDrawableStateImage(
        icon = RealDrawableFetcher(icon),
        background = background?.let { ColorFetcherDrawableFetcher(it) },
        iconSize = iconSize,
        iconTint = iconTint?.let { ResColorFetcher(it) }
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
    background: IntColorFetcher? = null,
    iconSize: Size? = null,
    iconTint: IntColorFetcher? = null,
): IconAnimatableDrawableStateImage = remember(icon, background, iconSize, iconTint) {
    IconAnimatableDrawableStateImage(
        icon = RealDrawableFetcher(icon),
        background = background?.let { ColorFetcherDrawableFetcher(it) },
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
        icon = RealDrawableFetcher(icon),
        background = background?.let { ResDrawableFetcher(it) },
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
    background: IntColorFetcher? = null,
    iconSize: Size? = null,
): IconAnimatableDrawableStateImage = remember(icon, background, iconSize) {
    IconAnimatableDrawableStateImage(
        icon = RealDrawableFetcher(icon),
        background = background?.let { ColorFetcherDrawableFetcher(it) },
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
        icon = RealDrawableFetcher(icon),
        background = background?.let { RealDrawableFetcher(it) },
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
        icon = RealDrawableFetcher(icon),
        background = null,
        iconSize = iconSize,
        iconTint = iconTint?.let { ResColorFetcher(it) }
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
    iconTint: IntColorFetcher? = null,
): IconAnimatableDrawableStateImage = remember(icon, iconSize, iconTint) {
    IconAnimatableDrawableStateImage(
        icon = RealDrawableFetcher(icon),
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
        icon = RealDrawableFetcher(icon),
        background = background?.let { ResDrawableFetcher(it) },
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
    background: IntColorFetcher? = null,
): IconAnimatableDrawableStateImage = remember(icon, background) {
    IconAnimatableDrawableStateImage(
        icon = RealDrawableFetcher(icon),
        background = background?.let { ColorFetcherDrawableFetcher(it) },
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
        icon = RealDrawableFetcher(icon),
        background = background?.let { RealDrawableFetcher(it) },
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
        icon = RealDrawableFetcher(icon),
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
        icon = RealDrawableFetcher(icon),
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
        icon = ResDrawableFetcher(icon),
        background = background?.let { RealDrawableFetcher(it) },
        iconSize = iconSize,
        iconTint = iconTint?.let { ResColorFetcher(it) }
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
    iconTint: IntColorFetcher? = null,
): IconAnimatableDrawableStateImage = remember(icon, background, iconSize, iconTint) {
    IconAnimatableDrawableStateImage(
        icon = ResDrawableFetcher(icon),
        background = background?.let { RealDrawableFetcher(it) },
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
        icon = ResDrawableFetcher(icon),
        background = background?.let { ResDrawableFetcher(it) },
        iconSize = iconSize,
        iconTint = iconTint?.let { ResColorFetcher(it) }
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
    iconTint: IntColorFetcher? = null,
): IconAnimatableDrawableStateImage = remember(icon, background, iconSize, iconTint) {
    IconAnimatableDrawableStateImage(
        icon = ResDrawableFetcher(icon),
        background = background?.let { ResDrawableFetcher(it) },
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
    background: IntColorFetcher? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatableDrawableStateImage = remember(icon, background, iconSize, iconTint) {
    IconAnimatableDrawableStateImage(
        icon = ResDrawableFetcher(icon),
        background = background?.let { ColorFetcherDrawableFetcher(it) },
        iconSize = iconSize,
        iconTint = iconTint?.let { ResColorFetcher(it) }
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
    background: IntColorFetcher? = null,
    iconSize: Size? = null,
    iconTint: IntColorFetcher? = null,
): IconAnimatableDrawableStateImage = remember(icon, background, iconSize, iconTint) {
    IconAnimatableDrawableStateImage(
        icon = ResDrawableFetcher(icon),
        background = background?.let { ColorFetcherDrawableFetcher(it) },
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
        icon = ResDrawableFetcher(icon),
        background = background?.let { ResDrawableFetcher(it) },
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
    background: IntColorFetcher? = null,
    iconSize: Size? = null,
): IconAnimatableDrawableStateImage = remember(icon, background, iconSize) {
    IconAnimatableDrawableStateImage(
        icon = ResDrawableFetcher(icon),
        background = background?.let { ColorFetcherDrawableFetcher(it) },
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
        icon = ResDrawableFetcher(icon),
        background = background?.let { RealDrawableFetcher(it) },
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
        icon = ResDrawableFetcher(icon),
        background = null,
        iconSize = iconSize,
        iconTint = iconTint?.let { ResColorFetcher(it) }
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
    iconTint: IntColorFetcher? = null,
): IconAnimatableDrawableStateImage = remember(icon, iconSize, iconTint) {
    IconAnimatableDrawableStateImage(
        icon = ResDrawableFetcher(icon),
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
        icon = ResDrawableFetcher(icon),
        background = background?.let { ResDrawableFetcher(it) },
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
    background: IntColorFetcher? = null,
): IconAnimatableDrawableStateImage = remember(icon, background) {
    IconAnimatableDrawableStateImage(
        icon = ResDrawableFetcher(icon),
        background = background?.let { ColorFetcherDrawableFetcher(it) },
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
        icon = ResDrawableFetcher(icon),
        background = background?.let { RealDrawableFetcher(it) },
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
        icon = ResDrawableFetcher(icon),
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
        icon = ResDrawableFetcher(icon),
        background = null,
        iconSize = null,
        iconTint = null
    )
}