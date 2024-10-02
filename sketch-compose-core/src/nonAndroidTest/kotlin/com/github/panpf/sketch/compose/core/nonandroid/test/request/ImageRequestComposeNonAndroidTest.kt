package com.github.panpf.sketch.compose.core.nonandroid.test.request

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.request.ComposableImageRequest
import com.github.panpf.sketch.request.sizeWithWindow
import com.github.panpf.sketch.resize.FixedSizeResolver
import com.github.panpf.sketch.util.toSketchSize
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class, ExperimentalComposeUiApi::class)
class ImageRequestComposeNonAndroidTest {

    @Test
    fun testSizeWithWindow() {
        runComposeUiTest {
            setContent {
                ComposableImageRequest("http://sample.com/sample.jpeg") {
                    sizeWithWindow()
                }.apply {
                    assertEquals(
                        expected = FixedSizeResolver(LocalWindowInfo.current.containerSize.toSketchSize()),
                        actual = sizeResolver
                    )
                }
            }
        }
    }
}