package com.github.panpf.sketch.sample.ui.test

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.LocalPlatformContext
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.asPainter
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.fetch.newBlurHashUri
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.rememberAsyncImageState
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.blurHashPlaceholder
import com.github.panpf.sketch.sample.image.DelayDecodeInterceptor
import com.github.panpf.sketch.sample.ui.base.BaseScreen
import com.github.panpf.sketch.sample.ui.base.ToolbarScaffold
import com.github.panpf.sketch.sample.ui.components.MyAsyncImage
import com.github.panpf.sketch.state.ColorPainterStateImage
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.decodeBlurHashToBitmap
import com.github.panpf.sketch.util.limitSide
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlin.math.min

expect val alpha8ColorType: String

class BlurHashTestScreen : BaseScreen() {

    @Composable
    override fun DrawContent() {
        ToolbarScaffold(title = "BlurHash") {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .windowInsetsPadding(NavigationBarDefaults.windowInsets)
                    .verticalScroll(rememberScrollState()).padding(20.dp),
            ) {
                val context = LocalPlatformContext.current
                val coroutineScope = rememberCoroutineScope()
                val itemModifier = Modifier
                    .size(110.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                val imageFile = ResourceImages.jpeg
                val imageBlurHash = "d7D+0q5W00^h01~A~B0gInR%?G9vR%R+NH=_I;NG\$\$-o"
                val imageBlurHashUri = newBlurHashUri(
                    blurHash = imageBlurHash,
                    width = imageFile.size.width,
                    height = imageFile.size.height
                )
                val maxSide = 200

                Text(
                    text = "Basic",
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(Modifier.size(10.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Column {
                        Text(
                            text = "Source(${imageFile.size})",
                            style = MaterialTheme.typography.bodySmall,
                        )
                        Spacer(Modifier.size(4.dp))
                        MyAsyncImage(
                            uri = imageFile.uri,
                            contentDescription = "Source image",
                            modifier = itemModifier,
                        )
                    }

                    val items = remember {
                        listOf(
                            "Keep" to imageFile.size
                                .limitSide(maxSide),
                            "Square" to imageFile.size
                                .let { min(a = it.width, b = it.height) }
                                .let { Size(width = it, height = it) }
                                .limitSide(maxSide),
                            "Reverse" to imageFile.size
                                .let { Size(width = it.height, height = it.width) }
                                .limitSide(maxSide)
                        )
                    }
                    items.forEach { (title, size) ->
                        Column {
                            val painter = remember {
                                decodeBlurHashToBitmap(
                                    blurHash = imageBlurHash,
                                    width = size.width,
                                    height = size.height
                                ).asImage().asPainter()
                            }
                            Text(
                                text = "${title}(${size})",
                                style = MaterialTheme.typography.bodySmall,
                            )
                            Spacer(Modifier.size(4.dp))
                            Image(
                                painter = painter,
                                contentDescription = "BlurHash example: $title",
                                modifier = itemModifier,
                            )
                        }
                    }

                    Column {
                        val painter = remember {
                            val size = imageFile.size
                                .limitSide(maxSide)
                            decodeBlurHashToBitmap(
                                blurHash = imageBlurHash,
                                width = size.width,
                                height = size.height
                            ).asImage().asPainter()
                        }
                        Text(
                            text = "Keep-Crop",
                            style = MaterialTheme.typography.bodySmall,
                        )
                        Spacer(Modifier.size(4.dp))
                        Image(
                            painter = painter,
                            contentDescription = "BlurHash example: Keep-Crop",
                            modifier = itemModifier,
                            contentScale = ContentScale.Crop,
                        )
                    }
                }

                Spacer(Modifier.size(30.dp))

                val refreshFlow = remember { MutableSharedFlow<Int>() }
                Row {
                    Text(
                        text = "Placeholder",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.CenterVertically),
                    )
                    Spacer(Modifier.size(20.dp))
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                refreshFlow.emit(1)
                            }
                        },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Icon(imageVector = Icons.Filled.Refresh, contentDescription = "Restart")
                    }
                }
                Spacer(Modifier.size(10.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Column {
                        Text(
                            text = "Fit",
                            style = MaterialTheme.typography.bodySmall,
                        )
                        Spacer(Modifier.size(4.dp))
                        val imageState = rememberAsyncImageState()
                        LaunchedEffect(Unit) {
                            refreshFlow.collect {
                                imageState.restart()
                            }
                        }
                        MyAsyncImage(
                            request = ImageRequest(context, imageFile.uri) {
                                memoryCachePolicy(CachePolicy.DISABLED)
                                resultCachePolicy(CachePolicy.DISABLED)
                                blurHashPlaceholder(imageBlurHashUri, maxSide = maxSide)
                                crossfade()
                                components {
                                    addDecodeInterceptor(DelayDecodeInterceptor(2000))
                                }
                            },
                            contentDescription = "Placeholder Fit",
                            state = imageState,
                            modifier = itemModifier,
                            contentScale = ContentScale.Fit,
                        )
                    }

                    Column {
                        Text(
                            text = "Crop",
                            style = MaterialTheme.typography.bodySmall,
                        )
                        Spacer(Modifier.size(4.dp))
                        val imageState = rememberAsyncImageState()
                        LaunchedEffect(Unit) {
                            refreshFlow.collect {
                                imageState.restart()
                            }
                        }
                        MyAsyncImage(
                            request = ImageRequest(context, imageFile.uri) {
                                memoryCachePolicy(CachePolicy.DISABLED)
                                resultCachePolicy(CachePolicy.DISABLED)
                                blurHashPlaceholder(
                                    imageBlurHashUri,
                                    maxSide = maxSide,
                                )
                                crossfade()
                                components {
                                    addDecodeInterceptor(DelayDecodeInterceptor(2000))
                                }
                            },
                            contentDescription = "Placeholder Crop",
                            state = imageState,
                            modifier = itemModifier,
                            contentScale = ContentScale.Crop,
                        )
                    }

                    Column {
                        Text(
                            text = "Crop-Square",
                            style = MaterialTheme.typography.bodySmall,
                        )
                        Spacer(Modifier.size(4.dp))
                        val imageState = rememberAsyncImageState()
                        LaunchedEffect(Unit) {
                            refreshFlow.collect {
                                imageState.restart()
                            }
                        }
                        MyAsyncImage(
                            request = ImageRequest(context, imageFile.uri) {
                                memoryCachePolicy(CachePolicy.DISABLED)
                                resultCachePolicy(CachePolicy.DISABLED)
                                blurHashPlaceholder(
                                    imageBlurHashUri,
                                    maxSide = maxSide,
                                    size = imageFile.size
                                        .let { min(a = it.width, b = it.height) }
                                        .let { Size(width = it, height = it) }
                                        .limitSide(maxSide)
                                )
                                crossfade()
                                components {
                                    addDecodeInterceptor(DelayDecodeInterceptor(2000))
                                }
                            },
                            contentDescription = "Placeholder Crop Square",
                            state = imageState,
                            modifier = itemModifier,
                            contentScale = ContentScale.Crop,
                        )
                    }
                }

                Spacer(Modifier.size(30.dp))

                val refresh2Flow = remember { MutableSharedFlow<Int>() }
                Row {
                    Text(
                        text = "Decode",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.CenterVertically),
                    )
                    Spacer(Modifier.size(20.dp))
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                refresh2Flow.emit(1)
                            }
                        },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Icon(imageVector = Icons.Filled.Refresh, contentDescription = "Restart")
                    }
                }
                Spacer(Modifier.size(10.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Column {
                        Text(
                            text = "Fit",
                            style = MaterialTheme.typography.bodySmall,
                        )
                        Spacer(Modifier.size(4.dp))
                        val imageState = rememberAsyncImageState()
                        LaunchedEffect(Unit) {
                            refresh2Flow.collect {
                                imageState.restart()
                            }
                        }
                        MyAsyncImage(
                            request = ImageRequest(context, imageBlurHashUri) {
                                memoryCachePolicy(CachePolicy.DISABLED)
                                resultCachePolicy(CachePolicy.DISABLED)
                                placeholder(ColorPainterStateImage(Color.Transparent))
                                crossfade()
                                components {
                                    addDecodeInterceptor(DelayDecodeInterceptor(2000))
                                }
                            },
                            contentDescription = "Decode Fit",
                            state = imageState,
                            modifier = itemModifier,
                            contentScale = ContentScale.Fit,
                        )
                    }

                    Column {
                        Text(
                            text = "Crop",
                            style = MaterialTheme.typography.bodySmall,
                        )
                        Spacer(Modifier.size(4.dp))
                        val imageState = rememberAsyncImageState()
                        LaunchedEffect(Unit) {
                            refresh2Flow.collect {
                                imageState.restart()
                            }
                        }
                        MyAsyncImage(
                            request = ImageRequest(context, imageBlurHashUri) {
                                memoryCachePolicy(CachePolicy.DISABLED)
                                resultCachePolicy(CachePolicy.DISABLED)
                                placeholder(ColorPainterStateImage(Color.Transparent))
                                crossfade()
                                components {
                                    addDecodeInterceptor(DelayDecodeInterceptor(2000))
                                }
                            },
                            contentDescription = "Decode Crop",
                            state = imageState,
                            modifier = itemModifier,
                            contentScale = ContentScale.Crop,
                        )
                    }

                    Column {
                        Text(
                            text = "ALPHA_8",
                            style = MaterialTheme.typography.bodySmall,
                        )
                        Spacer(Modifier.size(4.dp))
                        val imageState = rememberAsyncImageState()
                        LaunchedEffect(Unit) {
                            refresh2Flow.collect {
                                imageState.restart()
                            }
                        }
                        MyAsyncImage(
                            request = ImageRequest(context, imageBlurHashUri) {
                                memoryCachePolicy(CachePolicy.DISABLED)
                                resultCachePolicy(CachePolicy.DISABLED)
                                placeholder(ColorPainterStateImage(Color.Transparent))
                                colorType(alpha8ColorType)
                                crossfade()
                                components {
                                    addDecodeInterceptor(DelayDecodeInterceptor(2000))
                                }
                            },
                            contentDescription = "Decode ALPHA_8",
                            state = imageState,
                            modifier = itemModifier,
                        )
                    }
                }
            }
        }
    }
}