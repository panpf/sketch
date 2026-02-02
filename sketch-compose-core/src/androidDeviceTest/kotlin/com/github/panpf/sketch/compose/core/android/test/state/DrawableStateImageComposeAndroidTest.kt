@file:OptIn(ExperimentalTestApi::class)

package com.github.panpf.sketch.compose.core.android.test.state

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.drawable.asEquitable
import com.github.panpf.sketch.state.DrawableStateImage
import com.github.panpf.sketch.state.rememberDrawableStateImage
import kotlin.test.Test
import kotlin.test.assertEquals

class DrawableStateImageComposeAndroidTest {

    @Test
    fun testRememberDrawableStateImage() {
        runComposeUiTest {
            setContent {
                rememberDrawableStateImage(ColorDrawable(Color.RED).asEquitable()).apply {
                    assertEquals(
                        expected = DrawableStateImage(ColorDrawable(Color.RED).asEquitable()),
                        actual = this
                    )
                }

                rememberDrawableStateImage(android.R.drawable.ic_delete).apply {
                    assertEquals(
                        expected = DrawableStateImage(android.R.drawable.ic_delete),
                        actual = this
                    )
                }
            }
        }
    }
}