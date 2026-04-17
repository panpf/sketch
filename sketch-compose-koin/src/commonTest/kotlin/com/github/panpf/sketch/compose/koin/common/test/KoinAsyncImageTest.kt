package com.github.panpf.sketch.compose.koin.common.test

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.AsyncImage
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.rememberAsyncImageState
import com.github.panpf.sketch.request.ComposableImageRequest
import com.github.panpf.sketch.test.utils.Koins
import com.github.panpf.sketch.test.utils.LifecycleContainer
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class KoinAsyncImageTest {

    init {
        Koins.initial()
    }

    @Test
    fun testAsyncImage1() {
        runComposeUiTest {
            setContent {
                LifecycleContainer {
                    AsyncImage(ComposeResImageFiles.jpeg.uri, "test image")

                    AsyncImage(
                        ComposeResImageFiles.jpeg.uri,
                        "test image",
                        Modifier,
                        rememberAsyncImageState(),
                        Alignment.TopStart,
                        ContentScale.Crop,
                        0.5f,
                        ColorFilter.tint(Color.Red),
                        FilterQuality.High,
                        clipToBounds = false
                    )

                    AsyncImage(
                        uri = ComposeResImageFiles.jpeg.uri,
                        contentDescription = "test image",
                        modifier = Modifier,
                        state = rememberAsyncImageState(),
                        alignment = Alignment.TopStart,
                        contentScale = ContentScale.Crop,
                        alpha = 0.5f,
                        colorFilter = ColorFilter.tint(Color.Red),
                        filterQuality = FilterQuality.High,
                        clipToBounds = false
                    )
                }
            }

            // TODO test: Screenshot test or draw to Bitmap, then compare Bitmap
        }
    }

    @Test
    fun testAsyncImage2() {
        runComposeUiTest {
            setContent {
                LifecycleContainer {
                    AsyncImage(ComposableImageRequest(ComposeResImageFiles.jpeg.uri), "test image")

                    AsyncImage(
                        ComposableImageRequest(ComposeResImageFiles.jpeg.uri),
                        "test image",
                        Modifier,
                        rememberAsyncImageState(),
                        Alignment.TopStart,
                        ContentScale.Crop,
                        0.5f,
                        ColorFilter.tint(Color.Red),
                        FilterQuality.High,
                        clipToBounds = false
                    )

                    AsyncImage(
                        request = ComposableImageRequest(ComposeResImageFiles.jpeg.uri),
                        contentDescription = "test image",
                        modifier = Modifier,
                        state = rememberAsyncImageState(),
                        alignment = Alignment.TopStart,
                        contentScale = ContentScale.Crop,
                        alpha = 0.5f,
                        colorFilter = ColorFilter.tint(Color.Red),
                        filterQuality = FilterQuality.High,
                        clipToBounds = false
                    )
                }
            }

            // TODO test: Screenshot test or draw to Bitmap, then compare Bitmap
        }
    }
}