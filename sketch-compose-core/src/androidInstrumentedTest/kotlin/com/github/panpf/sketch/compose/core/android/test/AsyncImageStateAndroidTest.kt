package com.github.panpf.sketch.compose.core.android.test

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.getWindowContainerSize
import com.github.panpf.sketch.util.screenSize
import com.github.panpf.sketch.util.toIntSize
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class AsyncImageStateAndroidTest {

    @Test
    fun testGetWindowContainerSize() {
        runComposeUiTest {
            setContent {
                assertEquals(
                    expected = LocalContext.current.screenSize().toIntSize(),
                    actual = getWindowContainerSize()
                )
            }
        }
    }
}