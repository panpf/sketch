package com.github.panpf.sketch.compose.core.android.test

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.LocalPlatformContext
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class LocalPlatformContextAndroidTest {

    @Test
    fun testLocalPlatformContext() {
        runComposeUiTest {
            setContent {
                assertEquals(
                    expected = LocalContext.current,
                    actual = LocalPlatformContext.current
                )
            }
        }
    }
}