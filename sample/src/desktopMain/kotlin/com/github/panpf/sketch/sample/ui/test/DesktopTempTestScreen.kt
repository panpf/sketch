package com.github.panpf.sketch.sample.ui.test

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.LocalPlatformContext
import com.github.panpf.sketch.SingletonSketch
import com.github.panpf.sketch.SkiaAnimatedImage
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.painter.SkiaAnimatedImagePainter
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.animationEndCallback
import com.github.panpf.sketch.request.animationStartCallback
import com.github.panpf.sketch.sample.ui.base.BaseScreen
import com.github.panpf.sketch.sample.ui.base.ToolbarScaffold
import com.github.panpf.sketch.util.ioCoroutineDispatcher
import kotlinx.coroutines.withContext
import okio.buffer
import okio.use
import org.jetbrains.skia.Codec
import org.jetbrains.skia.Data

class DesktopTempTestScreen : BaseScreen() {

    @Composable
    override fun DrawContent() {
        ToolbarScaffold(title = "TempTest") {
//            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                Text("In development...")
//            }
//            LazyColumn(modifier = Modifier.fillMaxSize()) {
//                item {
//                    AsyncImage(
//                        request = ImageRequest(
//                            LocalPlatformContext.current,
//                            newComposeResourceUri(Res.getUri("files/liuyifei.jpg"))
//                        ) {
//                            memoryCachePolicy(DISABLED)
//                            resultCachePolicy(DISABLED)
//                        },
//                        contentDescription = null,
//                        modifier = Modifier.fillMaxWidth().wrapContentHeight()
//                    )
//                }
//            }
            Column {
//                Image(
//                    painter = painterResource(resource = Res.drawable.ic_image_outline),
//                    contentDescription = null,
//                    contentScale = ContentScale.None,
//                    modifier = Modifier.size(200.dp).background(Color.Cyan)
//                )
//
//                Image(
//                    painter = rememberIconPainter(
//                        icon = Res.drawable.ic_image_outline,
//                        background = colorScheme.primaryContainer,
//                        iconTint = colorScheme.onPrimaryContainer,
//                    ),
//                    contentDescription = null,
//                    modifier = Modifier.size(200.dp).background(Color.Cyan)
//                )

                var animatedPainter by remember { mutableStateOf<SkiaAnimatedImagePainter?>(null) }
                val context = LocalPlatformContext.current
                LaunchedEffect(Unit) {
                    withContext(ioCoroutineDispatcher()) {
                        val sketch = SingletonSketch.get(context)
                        val request = ImageRequest(context, ResourceImages.animGif.uri)
                        val bytes = sketch.components.newFetcherOrThrow(request)
                            .fetch().getOrThrow()
                            .dataSource.openSource().buffer()
                            .use { it.readByteArray() }

                        val data = Data.makeFromBytes(bytes)
                        val image = SkiaAnimatedImage(
                            codec = Codec.makeFromData(data),
                            repeatCount = null,
                            cacheDecodeTimeoutFrame = true,
                            animationStartCallback = request.animationStartCallback,
                            animationEndCallback = request.animationEndCallback
                        )
                        animatedPainter = SkiaAnimatedImagePainter(
                            animatedImage = image,
                        )
                    }
                }
                val animatedPainter1 = animatedPainter
//                val animatedPainter1 = remember(animatedPainter) { animatedPainter }
                if (animatedPainter1 != null) {
                    Image(
                        painter = animatedPainter1,
                        contentDescription = null,
                        modifier = Modifier.size(200.dp)
                    )
                }
            }
        }
    }
}