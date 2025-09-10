package com.github.panpf.sketch.sample.ui.test

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.AsyncImage
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ComposableImageRequest
import com.github.panpf.sketch.sample.ui.base.BaseScreen
import com.github.panpf.sketch.sample.ui.base.ToolbarScaffold

class TempTestScreen : BaseScreen() {

    @Composable
    override fun DrawContent() {
        ToolbarScaffold(title = "TempTest") {
            Column {
                AsyncImage(
                    request = ComposableImageRequest(ResourceImages.svg.uri) {
                        crossfade(alwaysUse = true)
                    },
                    contentDescription = "",
                    modifier = Modifier.size(100.dp),
                    colorFilter = ColorFilter.tint(Color.Blue)
                )
            }
        }
    }
}