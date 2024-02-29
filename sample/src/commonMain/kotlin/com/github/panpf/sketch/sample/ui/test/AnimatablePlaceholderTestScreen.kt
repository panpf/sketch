package com.github.panpf.sketch.sample.ui.test

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.compose.AsyncImage
import com.github.panpf.sketch.compose.LocalPlatformContext
import com.github.panpf.sketch.compose.stateimage.rememberIconAnimatablePainterStateImage
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.resources.AssetImages
import com.github.panpf.sketch.sample.image.DelayDecodeInterceptor
import com.github.panpf.sketch.sample.ui.rememberIconPlaceholderEclipseAnimatedPainter
import com.github.panpf.sketch.sample.ui.screen.base.BaseScreen
import com.github.panpf.sketch.sample.ui.screen.base.ToolbarScaffold

class AnimatablePlaceholderTestScreen : BaseScreen() {

    @Composable
    override fun DrawContent() {
        ToolbarScaffold(title = "AnimatablePlaceholderTest") {
            val context = LocalPlatformContext.current
            val eclipseAnimatedPainter = rememberIconPlaceholderEclipseAnimatedPainter(context)
            if (eclipseAnimatedPainter != null) {
                Column(modifier = Modifier.fillMaxSize()) {
                    var urlIndexState by remember { mutableStateOf(0) }
                    val images = remember {
                        arrayOf(AssetImages.jpeg.uri, AssetImages.webp.uri, AssetImages.bmp.uri)
                    }
                    val uriString = images[urlIndexState % images.size]
                    val colorScheme = MaterialTheme.colorScheme
                    val placeholderStateImage = rememberIconAnimatablePainterStateImage(
                        icon = eclipseAnimatedPainter,
                        background = colorScheme.primaryContainer
                    )
                    val request = ImageRequest(context, uriString) {
                        memoryCachePolicy(DISABLED)
                        resultCachePolicy(DISABLED)
                        // TODO AnimatedVectorDrawable and AnimatedVectorDrawableCompat cannot be played above android api 29
                        placeholder(placeholderStateImage)
                        components {
                            addDecodeInterceptor(DelayDecodeInterceptor(3000))
                        }
                    }
                    Spacer(modifier = Modifier.size(20.dp))
                    AsyncImage(
                        request = request,
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .weight(1f)
                            .align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                    AsyncImage(
                        request = request,
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .aspectRatio(1.5f)
                            .weight(1f)
                            .align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                    AsyncImage(
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
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Not supported on this platform")
                }
            }
        }
    }
}