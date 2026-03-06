package com.github.panpf.sketch.sample.ui.test

import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.AsyncImage
import com.github.panpf.sketch.LocalPlatformContext
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.sample.Res
import com.github.panpf.sketch.sample.ic_image_outline
import com.github.panpf.sketch.sample.ui.util.getPreviewSketch
import com.github.panpf.sketch.state.rememberIconPainterStateImage

@Preview
@Composable
fun AsyncImagePreview() {
    val context = LocalPlatformContext.current
    val placeholderIcon = rememberIconPainterStateImage(
        icon = Res.drawable.ic_image_outline,
        background = colorScheme.primaryContainer,
        iconTint = colorScheme.onPrimaryContainer
    )
    val request = remember {
        ImageRequest(context, "") {
            placeholder(placeholderIcon)
        }
    }
    AsyncImage(
        request = request,
        sketch = getPreviewSketch(),
        contentDescription = "",
        modifier = Modifier.size(200.dp)
    )
}

@Preview
@Composable
fun TextPreview() {
    Surface(Modifier.size(200.dp)) {
        Text(text = "AsyncImagePreview")
    }
}
