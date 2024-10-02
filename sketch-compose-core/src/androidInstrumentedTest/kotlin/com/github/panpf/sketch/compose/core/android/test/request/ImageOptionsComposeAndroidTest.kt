package com.github.panpf.sketch.compose.core.android.test.request

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.request.ComposableImageOptions
import com.github.panpf.sketch.request.sizeWithWindow
import com.github.panpf.sketch.resize.FixedSizeResolver
import com.github.panpf.sketch.util.screenSize
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class ImageOptionsComposeAndroidTest {

    @Test
    fun testSizeWithWindow() {
        runComposeUiTest {
            setContent {
                ComposableImageOptions {
                    sizeWithWindow()
                }.apply {
                    assertEquals(
                        expected = FixedSizeResolver(LocalContext.current.screenSize()),
                        actual = sizeResolver
                    )
                }
            }
        }
    }
}