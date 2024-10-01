package com.github.panpf.sketch.extensions.compose.common.test.ability

import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.Orientation.Vertical
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.ability.bindPauseLoadWhenScrolling
import com.github.panpf.sketch.request.PauseLoadWhenScrollingDecodeInterceptor
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class PauseLoadWhenScrollingTest {

    @Test
    fun testBindPauseLoadWhenScrolling() = runComposeUiTest {
        val scrollingList = mutableListOf<Boolean>()
        setContent {
            val scrollableState = rememberScrollableState { it }
            Column(Modifier.size(300.dp, 1000.dp).scrollable(scrollableState, Vertical)) {

            }
            bindPauseLoadWhenScrolling(scrollableState)

            LaunchedEffect(scrollableState.isScrollInProgress) {
                scrollingList.add(PauseLoadWhenScrollingDecodeInterceptor.scrolling)
            }

            LaunchedEffect(Unit) {
                scrollableState.animateScrollBy(
                    value = 500f,
                    animationSpec = tween(durationMillis = 500)
                )
            }
        }
        waitForIdle()
        assertEquals(expected = listOf(false, true, false), actual = scrollingList)
    }
}