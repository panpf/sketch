package com.github.panpf.sketch.sample.ui.test

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.AsyncImage
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.request.ComposableImageRequest
import com.github.panpf.sketch.request.repeatCount
import com.github.panpf.sketch.sample.ui.base.BaseScreen
import com.github.panpf.sketch.sample.ui.base.ToolbarScaffold

class DesktopTempTestScreen : BaseScreen() {

    @Composable
    override fun DrawContent() {
        ToolbarScaffold(title = "TempTest") {
            Column {
                AsyncImage(ComposableImageRequest(ComposeResImageFiles.numbersGif.uri) {
                    repeatCount(0)
                }, contentDescription = "numbersGif", modifier = Modifier.size(200.dp))
            }
        }
    }
}