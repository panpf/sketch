package com.github.panpf.sketch.sample.ui.components

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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