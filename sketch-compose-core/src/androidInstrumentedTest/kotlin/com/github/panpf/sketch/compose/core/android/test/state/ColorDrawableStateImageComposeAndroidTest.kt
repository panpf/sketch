@file:OptIn(ExperimentalTestApi::class)

package com.github.panpf.sketch.compose.core.android.test.state

import android.graphics.Color
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.state.ColorDrawableStateImage
import com.github.panpf.sketch.state.IntColorDrawableStateImage
import com.github.panpf.sketch.state.ResColorDrawableStateImage
import com.github.panpf.sketch.state.rememberColorDrawableStateImage
import com.github.panpf.sketch.state.rememberColorDrawableStateImageWithInt
import com.github.panpf.sketch.state.rememberColorDrawableStateImageWithRes
import com.github.panpf.sketch.util.ColorFetcher
import com.github.panpf.sketch.util.IntColorFetcher
import com.github.panpf.sketch.util.ResColorFetcher
import kotlin.test.Test
import kotlin.test.assertEquals

class ColorDrawableStateImageComposeAndroidTest {

    @Test
    fun testRememberColorDrawableStateImageWithInt() {
        runComposeUiTest {
            setContent {
                rememberColorDrawableStateImageWithInt(Color.YELLOW).apply {
                    assertEquals(
                        expected = IntColorDrawableStateImage(Color.YELLOW),
                        actual = this
                    )
                }
            }
        }
    }

    @Test
    fun testRememberColorDrawableStateImageWithRes() {
        runComposeUiTest {
            setContent {
                rememberColorDrawableStateImageWithRes(android.R.color.holo_purple).apply {
                    assertEquals(
                        expected = ResColorDrawableStateImage(android.R.color.holo_purple),
                        actual = this
                    )
                }
            }
        }
    }

    @Test
    fun testRememberColorDrawableStateImage() {
        runComposeUiTest {
            setContent {
                rememberColorDrawableStateImage(ResColorFetcher(android.R.color.holo_purple)).apply {
                    assertEquals(
                        expected = ColorDrawableStateImage(ResColorFetcher(android.R.color.holo_purple)),
                        actual = this
                    )
                }
                rememberColorDrawableStateImage(IntColorFetcher(Color.YELLOW)).apply {
                    assertEquals(
                        expected = ColorDrawableStateImage(IntColorFetcher(Color.YELLOW)),
                        actual = this
                    )
                }
                rememberColorDrawableStateImage(IntColorFetcher(Color.YELLOW) as ColorFetcher).apply {
                    assertEquals(
                        expected = ColorDrawableStateImage(IntColorFetcher(Color.YELLOW) as ColorFetcher),
                        actual = this
                    )
                }
            }
        }
    }
}