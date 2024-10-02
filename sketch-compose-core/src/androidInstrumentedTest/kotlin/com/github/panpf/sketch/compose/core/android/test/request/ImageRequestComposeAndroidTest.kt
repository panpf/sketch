package com.github.panpf.sketch.compose.core.android.test.request

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.request.ComposableImageRequest
import com.github.panpf.sketch.request.sizeWithWindow
import com.github.panpf.sketch.resize.FixedSizeResolver
import com.github.panpf.sketch.util.screenSize
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class ImageRequestComposeAndroidTest {

    @Test
    fun testSizeWithWindow() {
        runComposeUiTest {
            setContent {
                ComposableImageRequest("http://sample.com/sample.jpeg") {
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