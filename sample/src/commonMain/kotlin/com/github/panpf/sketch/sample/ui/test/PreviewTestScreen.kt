package com.github.panpf.sketch.sample.ui.test

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.AsyncImage
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ComposableImageRequest
import com.github.panpf.sketch.sample.resources.Res
import com.github.panpf.sketch.sample.resources.ic_image_outline
import com.github.panpf.sketch.sample.ui.base.BaseScreen
import com.github.panpf.sketch.sample.ui.base.ToolbarScaffold
import com.github.panpf.sketch.state.rememberIconPainterStateImage

class PreviewTestScreen : BaseScreen() {

    @Composable
    override fun DrawContent() {
        ToolbarScaffold(title = "Preview") {
            Row(
                modifier = Modifier.fillMaxSize()
                    .windowInsetsPadding(NavigationBarDefaults.windowInsets),
            ) {
                Column(
                    Modifier.weight(1f).fillMaxHeight().padding(20.dp),
                    horizontalAlignment = CenterHorizontally
                ) {
                    Text(text = "Preview Mode")

                    Spacer(Modifier.size(10.dp))

                    CompositionLocalProvider(LocalInspectionMode provides true) {
                        AsyncImage(
                            request = ComposableImageRequest(ResourceImages.jpeg.uri) {
                                placeholder(
                                    rememberIconPainterStateImage(
                                        icon = Res.drawable.ic_image_outline,
                                        background = colorScheme.primaryContainer,
                                        iconTint = colorScheme.onPrimaryContainer
                                    )
                                )
                            },
                            contentDescription = "example",
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .aspectRatio(1f)
                                .border(2.dp, Color.Red)
                                .padding(2.dp)
                        )
                    }
                }

                Column(
                    Modifier.weight(1f).fillMaxHeight().padding(20.dp),
                    horizontalAlignment = CenterHorizontally
                ) {
                    Text(text = "Non Preview Mode")

                    Spacer(Modifier.size(10.dp))

                    CompositionLocalProvider(LocalInspectionMode provides false) {
                        AsyncImage(
                            request = ComposableImageRequest(ResourceImages.jpeg.uri) {
                                placeholder(
                                    rememberIconPainterStateImage(
                                        icon = Res.drawable.ic_image_outline,
                                        background = colorScheme.primaryContainer,
                                        iconTint = colorScheme.onPrimaryContainer
                                    )
                                )
                            },
                            contentDescription = "example",
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .aspectRatio(1f)
                                .border(2.dp, Color.Red)
                                .padding(2.dp)
                        )
                    }
                }
            }
        }
    }
}