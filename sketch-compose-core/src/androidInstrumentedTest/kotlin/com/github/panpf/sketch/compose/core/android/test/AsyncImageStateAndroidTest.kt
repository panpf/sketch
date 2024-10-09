package com.github.panpf.sketch.compose.core.android.test

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.util.screenSize
import com.github.panpf.sketch.util.toIntSize
import com.github.panpf.sketch.windowContainerSize
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class AsyncImageStateAndroidTest {

    @Test
    fun testWindowContainerSize() {
        runComposeUiTest {
            setContent {
                assertEquals(
                    expected = LocalContext.current.screenSize().toIntSize(),
                    actual = windowContainerSize()
                )
            }
        }
    }
}