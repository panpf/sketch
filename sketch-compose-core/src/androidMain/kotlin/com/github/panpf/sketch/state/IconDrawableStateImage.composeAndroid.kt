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
        icon = RealDrawableFetcher(icon),
        background = background?.let { RealDrawableFetcher(it) },
        iconSize = iconSize,
        iconTint = iconTint?.let { ResColorFetcher(it) }
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
    iconTint: IntColorFetcher? = null,
): IconDrawableStateImage = remember(icon, background, iconSize, iconTint) {
    IconDrawableStateImage(
        icon = RealDrawableFetcher(icon),
        background = background?.let { RealDrawableFetcher(it) },
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
        icon = RealDrawableFetcher(icon),
        background = background?.let { ResDrawableFetcher(it) },
        iconSize = iconSize,
        iconTint = iconTint?.let { ResColorFetcher(it) }
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
    iconTint: IntColorFetcher? = null,
): IconDrawableStateImage = remember(icon, background, iconSize, iconTint) {
    IconDrawableStateImage(
        icon = RealDrawableFetcher(icon),
        background = background?.let { ResDrawableFetcher(it) },
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
    background: IntColorFetcher? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconDrawableStateImage = remember(icon, background, iconSize, iconTint) {
    IconDrawableStateImage(
        icon = RealDrawableFetcher(icon),
        background = background?.let { ColorFetcherDrawableFetcher(it) },
        iconSize = iconSize,
        iconTint = iconTint?.let { ResColorFetcher(it) }
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
    background: IntColorFetcher? = null,
    iconSize: Size? = null,
    iconTint: IntColorFetcher? = null,
): IconDrawableStateImage = remember(icon, background, iconSize, iconTint) {
    IconDrawableStateImage(
        icon = RealDrawableFetcher(icon),
        background = background?.let { ColorFetcherDrawableFetcher(it) },
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
        icon = RealDrawableFetcher(icon),
        background = background?.let { ResDrawableFetcher(it) },
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
    background: IntColorFetcher? = null,
    iconSize: Size? = null,
): IconDrawableStateImage = remember(icon, background, iconSize) {
    IconDrawableStateImage(
        icon = RealDrawableFetcher(icon),
        background = background?.let { ColorFetcherDrawableFetcher(it) },
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
        icon = RealDrawableFetcher(icon),
        background = background?.let { RealDrawableFetcher(it) },
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
        icon = RealDrawableFetcher(icon),
        background = null,
        iconSize = iconSize,
        iconTint = iconTint?.let { ResColorFetcher(it) }
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
    iconTint: IntColorFetcher? = null,
): IconDrawableStateImage = remember(icon, iconSize, iconTint) {
    IconDrawableStateImage(
        icon = RealDrawableFetcher(icon),
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
        icon = RealDrawableFetcher(icon),
        background = background?.let { ResDrawableFetcher(it) },
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
    background: IntColorFetcher? = null,
): IconDrawableStateImage = remember(icon, background) {
    IconDrawableStateImage(
        icon = RealDrawableFetcher(icon),
        background = background?.let { ColorFetcherDrawableFetcher(it) },
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
        icon = RealDrawableFetcher(icon),
        background = background?.let { RealDrawableFetcher(it) },
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
        icon = RealDrawableFetcher(icon),
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
        icon = RealDrawableFetcher(icon),
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
        icon = ResDrawableFetcher(icon),
        background = background?.let { RealDrawableFetcher(it) },
        iconSize = iconSize,
        iconTint = iconTint?.let { ResColorFetcher(it) }
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
    iconTint: IntColorFetcher? = null,
): IconDrawableStateImage = remember(icon, background, iconSize, iconTint) {
    IconDrawableStateImage(
        icon = ResDrawableFetcher(icon),
        background = background?.let { RealDrawableFetcher(it) },
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
        icon = ResDrawableFetcher(icon),
        background = background?.let { ResDrawableFetcher(it) },
        iconSize = iconSize,
        iconTint = iconTint?.let { ResColorFetcher(it) }
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
    iconTint: IntColorFetcher? = null,
): IconDrawableStateImage = remember(icon, background, iconSize, iconTint) {
    IconDrawableStateImage(
        icon = ResDrawableFetcher(icon),
        background = background?.let { ResDrawableFetcher(it) },
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
    background: IntColorFetcher? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconDrawableStateImage = remember(icon, background, iconSize, iconTint) {
    IconDrawableStateImage(
        icon = ResDrawableFetcher(icon),
        background = background?.let { ColorFetcherDrawableFetcher(it) },
        iconSize = iconSize,
        iconTint = iconTint?.let { ResColorFetcher(it) }
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
    background: IntColorFetcher? = null,
    iconSize: Size? = null,
    iconTint: IntColorFetcher? = null,
): IconDrawableStateImage = remember(icon, background, iconSize, iconTint) {
    IconDrawableStateImage(
        icon = ResDrawableFetcher(icon),
        background = background?.let { ColorFetcherDrawableFetcher(it) },
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
        icon = ResDrawableFetcher(icon),
        background = background?.let { ResDrawableFetcher(it) },
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
    background: IntColorFetcher? = null,
    iconSize: Size? = null,
): IconDrawableStateImage = remember(icon, background, iconSize) {
    IconDrawableStateImage(
        icon = ResDrawableFetcher(icon),
        background = background?.let { ColorFetcherDrawableFetcher(it) },
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
        icon = ResDrawableFetcher(icon),
        background = background?.let { RealDrawableFetcher(it) },
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
        icon = ResDrawableFetcher(icon),
        background = null,
        iconSize = iconSize,
        iconTint = iconTint?.let { ResColorFetcher(it) }
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
    iconTint: IntColorFetcher? = null,
): IconDrawableStateImage = remember(icon, iconSize, iconTint) {
    IconDrawableStateImage(
        icon = ResDrawableFetcher(icon),
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
        icon = ResDrawableFetcher(icon),
        background = background?.let { ResDrawableFetcher(it) },
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
    background: IntColorFetcher? = null,
): IconDrawableStateImage = remember(icon, background) {
    IconDrawableStateImage(
        icon = ResDrawableFetcher(icon),
        background = background?.let { ColorFetcherDrawableFetcher(it) },
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
        icon = ResDrawableFetcher(icon),
        background = background?.let { RealDrawableFetcher(it) },
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
        icon = ResDrawableFetcher(icon),
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
        icon = ResDrawableFetcher(icon),
        background = null,
        iconSize = null,
        iconTint = null
    )
}