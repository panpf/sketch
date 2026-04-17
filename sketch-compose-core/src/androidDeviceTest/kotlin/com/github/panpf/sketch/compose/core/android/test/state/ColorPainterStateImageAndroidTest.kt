@file:OptIn(ExperimentalTestApi::class)

package com.github.panpf.sketch.compose.core.android.test.state

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import androidx.core.content.res.ResourcesCompat
import com.github.panpf.sketch.state.ColorPainterStateImage
import com.github.panpf.sketch.state.rememberColorPainterStateImageWithRes
import kotlin.test.Test
import kotlin.test.assertEquals

class ColorPainterStateImageAndroidTest {

    @Test
    fun testRememberColorPainterStateImageWithRes() {
        runComposeUiTest {
            setContent {
                val context = LocalContext.current
                rememberColorPainterStateImageWithRes(android.R.color.holo_purple).apply {
                    assertEquals(
                        expected = ColorPainterStateImage(
                            Color(
                                ResourcesCompat.getColor(
                                    /* res = */ context.resources,
                                    /* id = */ android.R.color.holo_purple,
                                    /* theme = */ null
                                )
                            )
                        ),
                        actual = this
                    )
                }
            }
        }
    }
}