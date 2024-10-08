package com.github.panpf.sketch.state

import androidx.annotation.ColorRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.res.ResourcesCompat


/**
 * Create a [ColorPainterStateImage] instance and remember it
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.ColorPainterStateImageAndroidTest.testRememberColorPainterStateImageWithRes
 */
@Composable
fun rememberColorPainterStateImageWithRes(@ColorRes resId: Int): ColorPainterStateImage {
    val context = LocalContext.current
    return remember(resId) {
        val color = ResourcesCompat.getColor(context.resources, resId, null)
        ColorPainterStateImage(Color(color))
    }
}