package com.github.panpf.sketch.sample.ui.preview

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.sample.ui.common.AppendState

@Preview
@Composable
fun AppendStatePreview() {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(20.dp)
    ) {
        AppendState(AppendState.Loading)
        AppendState(AppendState.Error())
        AppendState(AppendState.Error {})
        AppendState(AppendState.End)
    }
}