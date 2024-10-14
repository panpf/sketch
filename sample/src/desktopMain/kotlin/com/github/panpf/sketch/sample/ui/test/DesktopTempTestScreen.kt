package com.github.panpf.sketch.sample.ui.test

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.AsyncImage
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ComposableImageRequest
import com.github.panpf.sketch.request.repeatCount
import com.github.panpf.sketch.sample.ui.base.BaseScreen
import com.github.panpf.sketch.sample.ui.base.ToolbarScaffold

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

//                var animatedPainter by remember { mutableStateOf<AnimatedImagePainter?>(null) }
//                val context = LocalPlatformContext.current
//                LaunchedEffect(Unit) {
//                    withContext(ioCoroutineDispatcher()) {
//                        val sketch = SingletonSketch.get(context)
//                        val request = ImageRequest(context, ResourceImages.animGif.uri)
//                        val requestContext = RequestContext(sketch, request, Size.Empty)
//                        val bytes = sketch.components.newFetcherOrThrow(requestContext)
//                            .fetch().getOrThrow()
//                            .dataSource.openSource().buffer()
//                            .use { it.readByteArray() }
//
//                        val data = Data.makeFromBytes(bytes)
//                        val image = AnimatedImage(
//                            codec = Codec.makeFromData(data),
//                            repeatCount = null,
//                            cacheDecodeTimeoutFrame = true,
//                            animationStartCallback = request.animationStartCallback,
//                            animationEndCallback = request.animationEndCallback
//                        )
//                        animatedPainter = AnimatedImagePainter(
//                            animatedImage = image,
//                        )
//                    }
//                }
//                val animatedPainter1 = animatedPainter
////                val animatedPainter1 = remember(animatedPainter) { animatedPainter }
//                if (animatedPainter1 != null) {
//                    Image(
//                        painter = animatedPainter1,
//                        contentDescription = null,
//                        modifier = Modifier.size(200.dp)
//                    )
//                }

                AsyncImage(ComposableImageRequest(ResourceImages.numbersGif.uri) {
                    repeatCount(0)
                }, contentDescription = "numbersGif", modifier = Modifier.size(200.dp))
            }
        }
    }
}