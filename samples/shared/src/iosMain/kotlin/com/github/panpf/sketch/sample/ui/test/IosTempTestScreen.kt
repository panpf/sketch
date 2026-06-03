package com.github.panpf.sketch.sample.ui.test

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.panpf.sketch.sample.ui.base.ToolbarScaffold

@Composable
fun IosTempTestScreen() {
    ToolbarScaffold(title = "IosTempTest") {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {

        }
    }
}