package com.github.panpf.sketch.compose.core.common.test.internal

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.LocalPlatformContext
import com.github.panpf.sketch.internal.requestOf
import com.github.panpf.sketch.request.ImageRequest
import kotlin.test.Test
import kotlin.test.assertEquals

class UtilsTest {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun testRequestOf() {
        runComposeUiTest {
            val requests1 = mutableListOf<ImageRequest>()
            val requests2 = mutableListOf<ImageRequest>()
            setContent {
                var index by remember { mutableIntStateOf(0) }
                val context = LocalPlatformContext.current
                val request1 = requestOf(context, "testUri")
                requests1.add(request1)
                val request2 = requestOf(context, "testUri${index}")
                requests2.add(request2)
                LaunchedEffect(index) {
                    if (index < 9) {
                        index++
                    }
                }
            }
            waitForIdle()
            assertEquals(expected = 10, actual = requests1.size)
            assertEquals(expected = 10, actual = requests2.size)

            requests1.takeLast(9).all { it == requests1.first() }
            requests2.takeLast(9).all { it != requests2.first() }
            requests1.takeLast(9).all { it === requests1.first() }
            requests2.takeLast(9).all { it !== requests2.first() }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun testRequestOf2() {
        runComposeUiTest {
            val requests1 = mutableListOf<ImageRequest>()
            val requests2 = mutableListOf<ImageRequest>()
            setContent {
                var index by remember { mutableIntStateOf(0) }
                val request1 = requestOf("testUri")
                requests1.add(request1)
                val request2 = requestOf("testUri${index}")
                requests2.add(request2)
                LaunchedEffect(index) {
                    if (index < 9) {
                        index++
                    }
                }
            }
            waitForIdle()
            assertEquals(expected = 10, actual = requests1.size)
            assertEquals(expected = 10, actual = requests2.size)

            requests1.takeLast(9).all { it == requests1.first() }
            requests2.takeLast(9).all { it != requests2.first() }
            requests1.takeLast(9).all { it === requests1.first() }
            requests2.takeLast(9).all { it !== requests2.first() }
        }
    }
}