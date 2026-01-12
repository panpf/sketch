package com.github.panpf.sketch.sample.ui.test

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.LocalPlatformContext
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.painter.asEquitable
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.sample.image.DelayRequestInterceptor
import com.github.panpf.sketch.sample.ui.base.BaseScreen
import com.github.panpf.sketch.sample.ui.base.ToolbarScaffold
import com.github.panpf.sketch.sample.ui.components.MyAsyncImage
import com.github.panpf.sketch.sample.ui.components.NewMoonLoadingPainter
import com.github.panpf.sketch.state.rememberIconAnimatablePainterStateImage

class AnimatablePlaceholderTestScreen : BaseScreen() {

    @Composable
    override fun DrawContent() {
        ToolbarScaffold(title = "AnimatablePlaceholderTest") {
            val context = LocalPlatformContext.current
            val density = LocalDensity.current
            val iconPainter = remember {
                val sizePx = with(density) { 24.dp.toPx() }
                val size = Size(sizePx, sizePx)
                NewMoonLoadingPainter(size).asEquitable("NewMoonLoadingPainter")
            }
            val colorScheme = MaterialTheme.colorScheme
            val placeholderStateImage = rememberIconAnimatablePainterStateImage(
                icon = iconPainter,
                background = colorScheme.primaryContainer,
                iconTint = colorScheme.onPrimaryContainer
            )
            Column(
                modifier = Modifier.fillMaxSize()
                    .windowInsetsPadding(NavigationBarDefaults.windowInsets)
            ) {
                var urlIndexState by remember { mutableStateOf(0) }
                val images = remember {
                    arrayOf(
                        ResourceImages.jpeg.uri,
                        ResourceImages.webp.uri,
                        ResourceImages.bmp.uri
                    )
                }
                val uri = images[urlIndexState % images.size]
                val request = ImageRequest(context, uri) {
                    memoryCachePolicy(DISABLED)
                    resultCachePolicy(DISABLED)
                    placeholder(placeholderStateImage)
                    components {
                        addRequestInterceptor(DelayRequestInterceptor(3000))
                    }
                }
                Spacer(modifier = Modifier.size(20.dp))
                MyAsyncImage(
                    request = request,
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .aspectRatio(1f)
                        .weight(1f)
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.size(16.dp))
                MyAsyncImage(
                    request = request,
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .aspectRatio(1.5f)
                        .weight(1f)
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.size(16.dp))
                MyAsyncImage(
                    request = request,
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .aspectRatio(0.5f)
                        .weight(1f)
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.size(40.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Button(
                        onClick = { urlIndexState += 1 },
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Text(text = "Next")
                    }
                }
                Spacer(modifier = Modifier.size(40.dp))
            }
        }
    }
}