package com.github.panpf.sketch.sample.ui.components

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun VerticalScrollbarCompat(
    modifier: Modifier,
    gridState: LazyGridState
) {
    VerticalScrollbar(
        modifier = modifier,
        adapter = rememberScrollbarAdapter(
            scrollState = gridState
        )
    )
}