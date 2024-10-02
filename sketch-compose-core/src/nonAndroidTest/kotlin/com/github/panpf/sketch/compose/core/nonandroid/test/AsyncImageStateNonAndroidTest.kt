package com.github.panpf.sketch.compose.core.nonandroid.test

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.getWindowContainerSize
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class, ExperimentalComposeUiApi::class)
class AsyncImageStateNonAndroidTest {

    @Test
    fun testGetWindowContainerSize() {
        runComposeUiTest {
            setContent {
                assertEquals(
                    expected = LocalWindowInfo.current.containerSize,
                    actual = getWindowContainerSize()
                )
            }
        }
    }
}