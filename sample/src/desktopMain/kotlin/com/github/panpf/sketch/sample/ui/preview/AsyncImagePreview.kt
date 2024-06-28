package com.github.panpf.sketch.sample.ui.preview

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.AsyncImage
import com.github.panpf.sketch.LocalPlatformContext
import com.github.panpf.sketch.rememberAsyncImageState
import com.github.panpf.sketch.request.ComposableImageOptions
import com.github.panpf.sketch.request.ComposableImageRequest
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.placeholder
import com.github.panpf.sketch.sample.resources.Res
import com.github.panpf.sketch.sample.resources.ic_image2_outline
import com.github.panpf.sketch.sample.ui.util.PreviewWrapper
import com.github.panpf.sketch.state.rememberPainterStateImage


@Preview
@Composable
fun AsyncImagePlaceholderPreview() = PreviewWrapper {
    Column {
        AsyncImage(
            uri = "https://www.example.com/image.jpg",
            contentDescription = "My Image",
            state = rememberAsyncImageState(ComposableImageOptions {
                placeholder(Res.drawable.ic_image2_outline)
            }),
            modifier = Modifier.size(100.dp).background(Color.Cyan)
        )

        Spacer(Modifier.size(20.dp))

        val placeholder = rememberPainterStateImage(Res.drawable.ic_image2_outline)
        AsyncImage(
            uri = "https://www.example.com/image.jpg",
            contentDescription = "My Image",
            state = rememberAsyncImageState {
                ImageOptions {
                    placeholder(placeholder)
                }
            },
            modifier = Modifier.size(100.dp).background(Color.Cyan)
        )

        Spacer(Modifier.size(20.dp))

        AsyncImage(
            request = ComposableImageRequest("https://www.example.com/image.jpg") {
                placeholder(Res.drawable.ic_image2_outline)
            },
            contentDescription = "My Image",
            modifier = Modifier.size(100.dp).background(Color.Cyan)
        )

        Spacer(Modifier.size(20.dp))

        AsyncImage(
            request = ImageRequest(
                LocalPlatformContext.current,
                "https://www.example.com/image.jpg"
            ) {
                placeholder(placeholder)
            },
            contentDescription = "My Image",
            modifier = Modifier.size(100.dp).background(Color.Cyan)
        )
    }
}