@file:OptIn(ExperimentalLayoutApi::class)

package com.github.panpf.sketch.sample.ui.preview

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.sample.ui.common.PageState

@Preview
@Composable
fun PageStatePreview() {
    FlowRow(
        Modifier.fillMaxSize(),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(20.dp),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(20.dp)
    ) {
        PageState(PageState.Loading)
        PageState(PageState.Error())
        PageState(PageState.Error("Error message"))
        PageState(PageState.Error("Error message") {})
        PageState(PageState.Empty())
        PageState(PageState.Empty("Empty message"))
        PageState(PageState.Empty("Empty message") {})
    }
}