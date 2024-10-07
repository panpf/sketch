@file:OptIn(ExperimentalTestApi::class)

package com.github.panpf.sketch.compose.core.android.test.state

import android.graphics.Color
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.state.IntColorDrawableStateImage
import com.github.panpf.sketch.state.ResColorDrawableStateImage
import com.github.panpf.sketch.state.rememberIntColorDrawableStateImage
import com.github.panpf.sketch.state.rememberResColorDrawableStateImage
import kotlin.test.Test
import kotlin.test.assertEquals

class ColorDrawableStateImageComposeAndroidTest {

    @Test
    fun testRememberIntColorDrawableStateImage() {
        runComposeUiTest {
            setContent {
                rememberIntColorDrawableStateImage(Color.YELLOW).apply {
                    assertEquals(
                        expected = IntColorDrawableStateImage(Color.YELLOW),
                        actual = this
                    )
                }
            }
        }
    }

    @Test
    fun testRememberResColorDrawableStateImage() {
        runComposeUiTest {
            setContent {
                rememberResColorDrawableStateImage(android.R.color.holo_purple).apply {
                    assertEquals(
                        expected = ResColorDrawableStateImage(android.R.color.holo_purple),
                        actual = this
                    )
                }
            }
        }
    }
}