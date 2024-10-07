@file:OptIn(ExperimentalTestApi::class)

package com.github.panpf.sketch.compose.core.android.test.state

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.drawable.asEquitable
import com.github.panpf.sketch.state.CurrentStateImage
import com.github.panpf.sketch.state.IntColorDrawableStateImage
import com.github.panpf.sketch.state.rememberCurrentStateImage
import com.github.panpf.sketch.state.rememberIntColorDrawableStateImage
import kotlin.test.Test
import kotlin.test.assertEquals

class CurrentStateImageComposeAndroidTest {

    @Test
    fun testRememberCurrentStateImage() {
        runComposeUiTest {
            setContent {
                rememberCurrentStateImage().apply {
                    assertEquals(
                        expected = CurrentStateImage(),
                        actual = this
                    )
                }

                rememberCurrentStateImage(rememberIntColorDrawableStateImage(Color.RED)).apply {
                    assertEquals(
                        expected = CurrentStateImage(IntColorDrawableStateImage(Color.RED)),
                        actual = this
                    )
                }

                rememberCurrentStateImage(ColorDrawable(Color.RED).asEquitable()).apply {
                    assertEquals(
                        expected = CurrentStateImage(ColorDrawable(Color.RED).asEquitable()),
                        actual = this
                    )
                }

                rememberCurrentStateImage(android.R.drawable.ic_delete).apply {
                    assertEquals(
                        expected = CurrentStateImage(android.R.drawable.ic_delete),
                        actual = this
                    )
                }
            }
        }
    }
}