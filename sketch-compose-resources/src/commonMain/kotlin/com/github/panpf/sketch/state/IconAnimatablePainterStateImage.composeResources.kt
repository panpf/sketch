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
import com.github.panpf.sketch.painter.EquitablePainter
import com.github.panpf.sketch.painter.rememberEquitablePainterResource
import org.jetbrains.compose.resources.DrawableResource

/* ********************************************* Painter icon ********************************************* */

/**
 * Create a [IconAnimatablePainterStateImage] and remember it.
 *
 * @see com.github.panpf.sketch.compose.resources.common.test.state.IconAnimatablePainterStateImageComposeResourcesTest.testRememberIconAnimatablePainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitablePainter,
    background: DrawableResource? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainterStateImage {
    val backgroundPainter = background?.let { rememberEquitablePainterResource(it) }
    return remember(icon, background, iconSize, iconTint) {
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
 * @see com.github.panpf.sketch.compose.resources.common.test.state.IconAnimatablePainterStateImageComposeResourcesTest.testRememberIconAnimatablePainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitablePainter,
    background: DrawableResource? = null,
    iconSize: Size? = null,
): IconAnimatablePainterStateImage {
    val backgroundPainter = background?.let { rememberEquitablePainterResource(it) }
    return remember(icon, background, iconSize) {
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
 * @see com.github.panpf.sketch.compose.resources.common.test.state.IconAnimatablePainterStateImageComposeResourcesTest.testRememberIconAnimatablePainterStateImageWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainterStateImage(
    icon: EquitablePainter,
    background: DrawableResource? = null,
): IconAnimatablePainterStateImage {
    val backgroundPainter = background?.let { rememberEquitablePainterResource(it) }
    return remember(icon, background) {
        IconAnimatablePainterStateImage(
            icon = icon,
            background = backgroundPainter,
            iconSize = null,
            iconTint = null
        )
    }
}


// DrawableResource does not support animations, and it is impossible to
///* ********************************************* DrawableResource icon ********************************************* */
//
///**
// * Create a [IconAnimatablePainterStateImage] and remember it.
// *
// * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithDrawableIcon
// */
//@Composable
//fun rememberIconAnimatablePainterStateImage(
//    icon: DrawableResource,
//    background: EquitablePainter? = null,
//    iconSize: Size? = null,
//    iconTint: Color? = null,
//): IconAnimatablePainterStateImage {
//    val iconPainter = rememberEquitablePainterResource(icon)
//    return remember(icon, background, iconSize, iconTint) {
//        IconAnimatablePainterStateImage(
//            icon = iconPainter,
//            background = background,
//            iconSize = iconSize,
//            iconTint = iconTint
//        )
//    }
//}
//
///**
// * Create a [IconAnimatablePainterStateImage] and remember it.
// *
// * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithDrawableIcon
// */
//@Composable
//fun rememberIconAnimatablePainterStateImage(
//    icon: DrawableResource,
//    background: Color? = null,
//    iconSize: Size? = null,
//    iconTint: Color? = null,
//): IconAnimatablePainterStateImage {
//    val iconPainter = rememberEquitablePainterResource(icon)
//    return remember(icon, background, iconSize, iconTint) {
//        val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
//        IconAnimatablePainterStateImage(
//            icon = iconPainter,
//            background = backgroundPainter,
//            iconSize = iconSize,
//            iconTint = iconTint
//        )
//    }
//}
//
//
///**
// * Create a [IconAnimatablePainterStateImage] and remember it.
// *
// * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithDrawableIcon
// */
//@Composable
//fun rememberIconAnimatablePainterStateImage(
//    icon: DrawableResource,
//    background: EquitablePainter? = null,
//    iconSize: Size? = null,
//): IconAnimatablePainterStateImage {
//    val iconPainter = rememberEquitablePainterResource(icon)
//    return remember(icon, background, iconSize) {
//        IconAnimatablePainterStateImage(
//            icon = iconPainter,
//            background = background,
//            iconSize = iconSize,
//            iconTint = null
//        )
//    }
//}
//
///**
// * Create a [IconAnimatablePainterStateImage] and remember it.
// *
// * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithDrawableIcon
// */
//@Composable
//fun rememberIconAnimatablePainterStateImage(
//    icon: DrawableResource,
//    background: Color? = null,
//    iconSize: Size? = null,
//): IconAnimatablePainterStateImage {
//    val iconPainter = rememberEquitablePainterResource(icon)
//    return remember(icon, background, iconSize) {
//        val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
//        IconAnimatablePainterStateImage(
//            icon = iconPainter,
//            background = backgroundPainter,
//            iconSize = iconSize,
//            iconTint = null
//        )
//    }
//}
//
///**
// * Create a [IconAnimatablePainterStateImage] and remember it.
// *
// * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithDrawableIcon
// */
//@Composable
//fun rememberIconAnimatablePainterStateImage(
//    icon: DrawableResource,
//    iconSize: Size? = null,
//    iconTint: Color? = null,
//): IconAnimatablePainterStateImage {
//    val iconPainter = rememberEquitablePainterResource(icon)
//    return remember(icon, iconSize, iconTint) {
//        IconAnimatablePainterStateImage(
//            icon = iconPainter,
//            background = null,
//            iconSize = iconSize,
//            iconTint = iconTint
//        )
//    }
//}
//
//
///**
// * Create a [IconAnimatablePainterStateImage] and remember it.
// *
// * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithDrawableIcon
// */
//@Composable
//fun rememberIconAnimatablePainterStateImage(
//    icon: DrawableResource,
//    background: EquitablePainter? = null,
//): IconAnimatablePainterStateImage {
//    val iconPainter = rememberEquitablePainterResource(icon)
//    return remember(icon, background) {
//        IconAnimatablePainterStateImage(
//            icon = iconPainter,
//            background = background,
//            iconSize = null,
//            iconTint = null
//        )
//    }
//}
//
///**
// * Create a [IconAnimatablePainterStateImage] and remember it.
// *
// * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithDrawableIcon
// */
//@Composable
//fun rememberIconAnimatablePainterStateImage(
//    icon: DrawableResource,
//    background: Color? = null,
//): IconAnimatablePainterStateImage {
//    val iconPainter = rememberEquitablePainterResource(icon)
//    return remember(icon, background) {
//        val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
//        IconAnimatablePainterStateImage(
//            icon = iconPainter,
//            background = backgroundPainter,
//            iconSize = null,
//            iconTint = null
//        )
//    }
//}
//
///**
// * Create a [IconAnimatablePainterStateImage] and remember it.
// *
// * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithDrawableIcon
// */
//@Composable
//fun rememberIconAnimatablePainterStateImage(
//    icon: DrawableResource,
//    iconSize: Size? = null,
//): IconAnimatablePainterStateImage {
//    val iconPainter = rememberEquitablePainterResource(icon)
//    return remember(icon, iconSize) {
//        IconAnimatablePainterStateImage(
//            icon = iconPainter,
//            background = null,
//            iconSize = iconSize,
//            iconTint = null
//        )
//    }
//}
//
///**
// * Create a [IconAnimatablePainterStateImage] and remember it.
// *
// * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterStateImageAndroidTest.testRememberIconAnimatablePainterStateImageWithDrawableIcon
// */
//@Composable
//fun rememberIconAnimatablePainterStateImage(
//    icon: DrawableResource,
//): IconAnimatablePainterStateImage {
//    val iconPainter = rememberEquitablePainterResource(icon)
//    return remember(icon) {
//        IconAnimatablePainterStateImage(
//            icon = iconPainter,
//            background = null,
//            iconSize = null,
//            iconTint = null
//        )
//    }
//}
//
///**
// * Create a [IconAnimatablePainterStateImage] and remember it.
// *
// * @see com.github.panpf.sketch.compose.resources.common.test.state.IconAnimatablePainterStateImageComposeResourcesTest.testRememberIconAnimatablePainterStateImageWithPainterIcon
// */
//@Composable
//fun rememberIconAnimatablePainterStateImage(
//    icon: DrawableResource,
//    background: DrawableResource? = null,
//    iconSize: Size? = null,
//    iconTint: Color? = null,
//): IconAnimatablePainterStateImage {
//    val iconPainter = rememberEquitablePainterResource(icon)
//    val backgroundPainter = background?.let { rememberEquitablePainterResource(it) }
//    return remember(icon, background, iconSize, iconTint) {
//        IconAnimatablePainterStateImage(
//            icon = iconPainter,
//            background = backgroundPainter,
//            iconSize = iconSize,
//            iconTint = iconTint
//        )
//    }
//}
//
///**
// * Create a [IconAnimatablePainterStateImage] and remember it.
// *
// * @see com.github.panpf.sketch.compose.resources.common.test.state.IconAnimatablePainterStateImageComposeResourcesTest.testRememberIconAnimatablePainterStateImageWithPainterIcon
// */
//@Composable
//fun rememberIconAnimatablePainterStateImage(
//    icon: DrawableResource,
//    background: DrawableResource? = null,
//    iconSize: Size? = null,
//): IconAnimatablePainterStateImage {
//    val iconPainter = rememberEquitablePainterResource(icon)
//    val backgroundPainter = background?.let { rememberEquitablePainterResource(it) }
//    return remember(icon, background, iconSize) {
//        IconAnimatablePainterStateImage(
//            icon = iconPainter,
//            background = backgroundPainter,
//            iconSize = iconSize,
//            iconTint = null
//        )
//    }
//}
//
///**
// * Create a [IconAnimatablePainterStateImage] and remember it.
// *
// * @see com.github.panpf.sketch.compose.resources.common.test.state.IconAnimatablePainterStateImageComposeResourcesTest.testRememberIconAnimatablePainterStateImageWithPainterIcon
// */
//@Composable
//fun rememberIconAnimatablePainterStateImage(
//    icon: DrawableResource,
//    background: DrawableResource? = null,
//): IconAnimatablePainterStateImage {
//    val iconPainter = rememberEquitablePainterResource(icon)
//    val backgroundPainter = background?.let { rememberEquitablePainterResource(it) }
//    return remember(icon, background) {
//        IconAnimatablePainterStateImage(
//            icon = iconPainter,
//            background = backgroundPainter,
//            iconSize = null,
//            iconTint = null
//        )
//    }
//}