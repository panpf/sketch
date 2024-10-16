@file:OptIn(ExperimentalLayoutApi::class)

package com.github.panpf.sketch.sample.ui.test

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.panpf.sketch.AsyncImage
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.painter.AnimatablePainter
import com.github.panpf.sketch.rememberAsyncImageState
import com.github.panpf.sketch.request.ComposableImageRequest
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.animatedTransformation
import com.github.panpf.sketch.request.onAnimationEnd
import com.github.panpf.sketch.request.onAnimationStart
import com.github.panpf.sketch.request.repeatCount
import com.github.panpf.sketch.sample.resources.Res
import com.github.panpf.sketch.sample.resources.ic_image_broken_outline
import com.github.panpf.sketch.sample.resources.ic_image_outline
import com.github.panpf.sketch.sample.ui.base.BaseScreen
import com.github.panpf.sketch.sample.ui.base.ToolbarScaffold
import com.github.panpf.sketch.sample.util.platformGifDecoders
import com.github.panpf.sketch.state.rememberIconPainterStateImage

class AnimatedImageTestScreen : BaseScreen() {

    @Composable
    override fun DrawContent() {
        ToolbarScaffold(title = "AnimatedImage") {
            Column(
                modifier = Modifier.fillMaxSize()
                    .windowInsetsPadding(NavigationBarDefaults.windowInsets)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Text("Formats", fontSize = 20.sp, color = colorScheme.primary)
                Spacer(Modifier.size(10.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    Column(Modifier.width(100.dp)) {
                        Text(text = "GIF")
                        Spacer(Modifier.size(10.dp))
                        AsyncImage(
                            request = buildImageRequest(ResourceImages.animGif.uri),
                            contentDescription = "example",
                            modifier = Modifier
                                .size(100.dp)
                                .border(2.dp, Color.Red)
                                .padding(2.dp)
                        )
                    }

                    Column(Modifier.width(100.dp)) {
                        Text(text = "WEBP")
                        Spacer(Modifier.size(10.dp))
                        AsyncImage(
                            request = buildImageRequest(ResourceImages.animWebp.uri),
                            contentDescription = "example",
                            modifier = Modifier
                                .size(100.dp)
                                .border(2.dp, Color.Red)
                                .padding(2.dp)
                        )
                    }

                    Column(Modifier.width(100.dp)) {
                        Text(text = "HEIF")
                        Spacer(Modifier.size(10.dp))
                        AsyncImage(
                            request = buildImageRequest(ResourceImages.animHeif.uri),
                            contentDescription = "example",
                            modifier = Modifier
                                .size(100.dp)
                                .border(2.dp, Color.Red)
                                .padding(2.dp)
                        )
                    }
                }

                Spacer(Modifier.size(30.dp))

                Text("repeatCount (0)", fontSize = 20.sp, color = colorScheme.primary)
                Spacer(Modifier.size(10.dp))
                val gifDecoders = remember { platformGifDecoders() }
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    gifDecoders.forEach { gifDecoder ->
                        Column(Modifier.width(100.dp)) {
                            Text(text = gifDecoder.toString())
                            Spacer(Modifier.size(10.dp))
                            AsyncImage(
                                request = buildImageRequest(ResourceImages.numbersGif.uri) {
                                    components {
                                        addDecoder(gifDecoder)
                                    }
                                    repeatCount(0)
                                },
                                contentDescription = "example",
                                modifier = Modifier
                                    .size(100.dp)
                                    .border(2.dp, Color.Red)
                                    .padding(2.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.size(30.dp))

                Text("Animation Callback", fontSize = 20.sp, color = colorScheme.primary)
                Spacer(Modifier.size(10.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    gifDecoders.forEach { gifDecoder ->
                        Column(Modifier.width(100.dp)) {
                            var playing by remember { mutableStateOf(false) }
                            val startCallback = remember { { playing = true } }
                            val endCallback = remember { { playing = false } }
                            Text(text = gifDecoder.toString())
                            Spacer(Modifier.size(10.dp))
                            Spacer(Modifier.size(10.dp))
                            val state = rememberAsyncImageState()
                            AsyncImage(
                                request = buildImageRequest(ResourceImages.numbersGif.uri) {
                                    components {
                                        addDecoder(gifDecoder)
                                    }
                                    repeatCount(0)
                                    onAnimationStart(startCallback)
                                    onAnimationEnd(endCallback)
                                },
                                contentDescription = "example",
                                state = state,
                                modifier = Modifier
                                    .size(100.dp)
                                    .border(2.dp, Color.Red)
                                    .padding(2.dp)
                            )

                            Button(
                                onClick = {
                                    val painter = state.painter
                                    if (painter is AnimatablePainter) {
                                        if (playing) {
                                            painter.stop()
                                        } else {
                                            painter.start()
                                        }
                                    }
                                },
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text(text = if (playing) "Stop" else "Play")
                            }
                        }
                    }
                }

                Spacer(Modifier.size(30.dp))

                Text("Animated Transformation", fontSize = 20.sp, color = colorScheme.primary)
                Spacer(Modifier.size(10.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    gifDecoders.forEach { gifDecoder ->
                        Column(Modifier.width(100.dp)) {
                            Text(text = gifDecoder.toString())
                            Spacer(Modifier.size(10.dp))
                            val state = rememberAsyncImageState()
                            AsyncImage(
                                request = buildImageRequest(ResourceImages.animGif.uri) {
                                    animatedTransformation(TestAnimatedTransformation)
                                    components {
                                        addDecoder(gifDecoder)
                                    }
                                },
                                contentDescription = "example",
                                state = state,
                                modifier = Modifier
                                    .size(100.dp)
                                    .border(2.dp, Color.Red)
                                    .padding(2.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private inline fun buildImageRequest(
        uri: String,
    ): ImageRequest = ComposableImageRequest(uri) {
        placeholder(
            rememberIconPainterStateImage(
                icon = Res.drawable.ic_image_outline,
                background = colorScheme.primaryContainer,
                iconTint = colorScheme.onPrimaryContainer
            )
        )
        error(
            rememberIconPainterStateImage(
                icon = Res.drawable.ic_image_broken_outline,
                background = colorScheme.primaryContainer,
                iconTint = colorScheme.onPrimaryContainer
            )
        )
    }

    @Composable
    private inline fun buildImageRequest(
        uri: String,
        crossinline configBlock: @Composable (ImageRequest.Builder.() -> Unit)
    ): ImageRequest = ComposableImageRequest(uri) {
        placeholder(
            rememberIconPainterStateImage(
                icon = Res.drawable.ic_image_outline,
                background = colorScheme.primaryContainer,
                iconTint = colorScheme.onPrimaryContainer
            )
        )
        error(
            rememberIconPainterStateImage(
                icon = Res.drawable.ic_image_broken_outline,
                background = colorScheme.primaryContainer,
                iconTint = colorScheme.onPrimaryContainer
            )
        )
        configBlock()
    }
}