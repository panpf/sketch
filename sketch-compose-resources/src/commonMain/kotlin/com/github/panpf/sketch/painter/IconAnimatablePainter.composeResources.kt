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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.DrawableResource

/* ********************************************* Painter icon ********************************************* */

/**
 * Create a [IconAnimatablePainter] and remember it.
 *
 * @see com.github.panpf.sketch.compose.resources.common.test.painter.IconAnimatablePainterComposeResourcesTest#testRememberIconAnimatablePainterWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitablePainter,
    background: DrawableResource? = null,
    iconSize: Size? = null,
    iconTint: Color? = null,
): IconAnimatablePainter {
    val backgroundPainter = background?.let { rememberEquitablePainterResource(it) }
    return remember(icon, background, iconSize, iconTint) {
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
 * @see com.github.panpf.sketch.compose.resources.common.test.painter.IconAnimatablePainterComposeResourcesTest#testRememberIconAnimatablePainterWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitablePainter,
    background: DrawableResource? = null,
    iconSize: Size? = null,
): IconAnimatablePainter {
    val backgroundPainter = background?.let { rememberEquitablePainterResource(it) }
    return remember(icon, background, iconSize) {
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
 * @see com.github.panpf.sketch.compose.resources.common.test.painter.IconAnimatablePainterComposeResourcesTest#testRememberIconAnimatablePainterWithPainterIcon
 */
@Composable
fun rememberIconAnimatablePainter(
    icon: EquitablePainter,
    background: DrawableResource? = null,
): IconAnimatablePainter {
    val backgroundPainter = background?.let { rememberEquitablePainterResource(it) }
    return remember(icon, background) {
        IconAnimatablePainter(
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
// * Create a [IconAnimatablePainter] and remember it.
// *
// * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithDrawableIcon
// */
//@Composable
//fun rememberIconAnimatablePainter(
//    icon: DrawableResource,
//    background: EquitablePainter? = null,
//    iconSize: Size? = null,
//    iconTint: Color? = null,
//): IconAnimatablePainter {
//    val iconPainter = rememberEquitablePainterResource(icon)
//    return remember(icon, background, iconSize, iconTint) {
//        IconAnimatablePainter(
//            icon = iconPainter,
//            background = background,
//            iconSize = iconSize,
//            iconTint = iconTint
//        )
//    }
//}
//
///**
// * Create a [IconAnimatablePainter] and remember it.
// *
// * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithDrawableIcon
// */
//@Composable
//fun rememberIconAnimatablePainter(
//    icon: DrawableResource,
//    background: Color? = null,
//    iconSize: Size? = null,
//    iconTint: Color? = null,
//): IconAnimatablePainter {
//    val iconPainter = rememberEquitablePainterResource(icon)
//    return remember(icon, background, iconSize, iconTint) {
//        val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
//        IconAnimatablePainter(
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
// * Create a [IconAnimatablePainter] and remember it.
// *
// * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithDrawableIcon
// */
//@Composable
//fun rememberIconAnimatablePainter(
//    icon: DrawableResource,
//    background: EquitablePainter? = null,
//    iconSize: Size? = null,
//): IconAnimatablePainter {
//    val iconPainter = rememberEquitablePainterResource(icon)
//    return remember(icon, background, iconSize) {
//        IconAnimatablePainter(
//            icon = iconPainter,
//            background = background,
//            iconSize = iconSize,
//            iconTint = null
//        )
//    }
//}
//
///**
// * Create a [IconAnimatablePainter] and remember it.
// *
// * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithDrawableIcon
// */
//@Composable
//fun rememberIconAnimatablePainter(
//    icon: DrawableResource,
//    background: Color? = null,
//    iconSize: Size? = null,
//): IconAnimatablePainter {
//    val iconPainter = rememberEquitablePainterResource(icon)
//    return remember(icon, background, iconSize) {
//        val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
//        IconAnimatablePainter(
//            icon = iconPainter,
//            background = backgroundPainter,
//            iconSize = iconSize,
//            iconTint = null
//        )
//    }
//}
//
///**
// * Create a [IconAnimatablePainter] and remember it.
// *
// * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithDrawableIcon
// */
//@Composable
//fun rememberIconAnimatablePainter(
//    icon: DrawableResource,
//    iconSize: Size? = null,
//    iconTint: Color? = null,
//): IconAnimatablePainter {
//    val iconPainter = rememberEquitablePainterResource(icon)
//    return remember(icon, iconSize, iconTint) {
//        IconAnimatablePainter(
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
// * Create a [IconAnimatablePainter] and remember it.
// *
// * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithDrawableIcon
// */
//@Composable
//fun rememberIconAnimatablePainter(
//    icon: DrawableResource,
//    background: EquitablePainter? = null,
//): IconAnimatablePainter {
//    val iconPainter = rememberEquitablePainterResource(icon)
//    return remember(icon, background) {
//        IconAnimatablePainter(
//            icon = iconPainter,
//            background = background,
//            iconSize = null,
//            iconTint = null
//        )
//    }
//}
//
///**
// * Create a [IconAnimatablePainter] and remember it.
// *
// * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithDrawableIcon
// */
//@Composable
//fun rememberIconAnimatablePainter(
//    icon: DrawableResource,
//    background: Color? = null,
//): IconAnimatablePainter {
//    val iconPainter = rememberEquitablePainterResource(icon)
//    return remember(icon, background) {
//        val backgroundPainter = background?.let { ColorPainter(it) }?.asEquitable()
//        IconAnimatablePainter(
//            icon = iconPainter,
//            background = backgroundPainter,
//            iconSize = null,
//            iconTint = null
//        )
//    }
//}
//
///**
// * Create a [IconAnimatablePainter] and remember it.
// *
// * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithDrawableIcon
// */
//@Composable
//fun rememberIconAnimatablePainter(
//    icon: DrawableResource,
//    iconSize: Size? = null,
//): IconAnimatablePainter {
//    val iconPainter = rememberEquitablePainterResource(icon)
//    return remember(icon, iconSize) {
//        IconAnimatablePainter(
//            icon = iconPainter,
//            background = null,
//            iconSize = iconSize,
//            iconTint = null
//        )
//    }
//}
//
///**
// * Create a [IconAnimatablePainter] and remember it.
// *
// * @see com.github.panpf.sketch.compose.core.android.test.painter.IconAnimatablePainterAndroidTest.testRememberIconAnimatablePainterWithDrawableIcon
// */
//@Composable
//fun rememberIconAnimatablePainter(
//    icon: DrawableResource,
//): IconAnimatablePainter {
//    val iconPainter = rememberEquitablePainterResource(icon)
//    return remember(icon) {
//        IconAnimatablePainter(
//            icon = iconPainter,
//            background = null,
//            iconSize = null,
//            iconTint = null
//        )
//    }
//}
//
///**
// * Create a [IconAnimatablePainter] and remember it.
// *
// * @see com.github.panpf.sketch.compose.resources.common.test.painter.IconAnimatablePainterComposeResourcesTest#testRememberIconAnimatablePainterWithPainterIcon
// */
//@Composable
//fun rememberIconAnimatablePainter(
//    icon: DrawableResource,
//    background: DrawableResource? = null,
//    iconSize: Size? = null,
//    iconTint: Color? = null,
//): IconAnimatablePainter {
//    val iconPainter = rememberEquitablePainterResource(icon)
//    val backgroundPainter = background?.let { rememberEquitablePainterResource(it) }
//    return remember(icon, background, iconSize, iconTint) {
//        IconAnimatablePainter(
//            icon = iconPainter,
//            background = backgroundPainter,
//            iconSize = iconSize,
//            iconTint = iconTint
//        )
//    }
//}
//
///**
// * Create a [IconAnimatablePainter] and remember it.
// *
// * @see com.github.panpf.sketch.compose.resources.common.test.painter.IconAnimatablePainterComposeResourcesTest#testRememberIconAnimatablePainterWithPainterIcon
// */
//@Composable
//fun rememberIconAnimatablePainter(
//    icon: DrawableResource,
//    background: DrawableResource? = null,
//    iconSize: Size? = null,
//): IconAnimatablePainter {
//    val iconPainter = rememberEquitablePainterResource(icon)
//    val backgroundPainter = background?.let { rememberEquitablePainterResource(it) }
//    return remember(icon, background, iconSize) {
//        IconAnimatablePainter(
//            icon = iconPainter,
//            background = backgroundPainter,
//            iconSize = iconSize,
//            iconTint = null
//        )
//    }
//}
//
///**
// * Create a [IconAnimatablePainter] and remember it.
// *
// * @see com.github.panpf.sketch.compose.resources.common.test.painter.IconAnimatablePainterComposeResourcesTest#testRememberIconAnimatablePainterWithPainterIcon
// */
//@Composable
//fun rememberIconAnimatablePainter(
//    icon: DrawableResource,
//    background: DrawableResource? = null,
//): IconAnimatablePainter {
//    val iconPainter = rememberEquitablePainterResource(icon)
//    val backgroundPainter = background?.let { rememberEquitablePainterResource(it) }
//    return remember(icon, background) {
//        IconAnimatablePainter(
//            icon = iconPainter,
//            background = backgroundPainter,
//            iconSize = null,
//            iconTint = null
//        )
//    }
//}