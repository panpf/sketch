package com.github.panpf.sketch.ability

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import com.github.panpf.sketch.request.PauseLoadWhenScrollingDecodeInterceptor

@Composable
fun bindPauseLoadWhenScrolling(scrollableState: ScrollableState) {
    LaunchedEffect(scrollableState) {
        snapshotFlow { scrollableState.isScrollInProgress }.collect {
            PauseLoadWhenScrollingDecodeInterceptor.scrolling = it
        }
    }
}