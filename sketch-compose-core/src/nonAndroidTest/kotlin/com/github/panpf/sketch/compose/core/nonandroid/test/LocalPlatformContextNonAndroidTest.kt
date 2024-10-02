package com.github.panpf.sketch.compose.core.nonandroid.test

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.LocalPlatformContext
import com.github.panpf.sketch.PlatformContext
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class LocalPlatformContextNonAndroidTest {

    @Test
    fun testLocalPlatformContext() {
        runComposeUiTest {
            setContent {
                assertEquals(
                    expected = PlatformContext.INSTANCE,
                    actual = LocalPlatformContext.current
                )
            }
        }
    }
}