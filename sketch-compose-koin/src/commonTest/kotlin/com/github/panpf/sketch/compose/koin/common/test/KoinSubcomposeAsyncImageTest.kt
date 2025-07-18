@file:OptIn(ExperimentalTestApi::class)

package com.github.panpf.sketch.compose.koin.common.test

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.SubcomposeAsyncImage
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.rememberAsyncImageState
import com.github.panpf.sketch.request.ComposableImageRequest
import com.github.panpf.sketch.test.utils.Koins
import com.github.panpf.sketch.test.utils.LifecycleContainer
import kotlin.test.Test

class KoinSubcomposeAsyncImageTest {

    init {
        Koins.initial()
    }

    @Test
    fun testSubcomposeAsyncImage1() {
        runComposeUiTest {
            setContent {
                LifecycleContainer {
                    SubcomposeAsyncImage(ResourceImages.jpeg.uri, "test image")

                    SubcomposeAsyncImage(
                        ResourceImages.jpeg.uri,
                        "test image",
                        Modifier,
                        rememberAsyncImageState(),
                        {},
                        {},
                        {},
                        Alignment.TopStart,
                        ContentScale.Crop,
                        0.5f,
                        ColorFilter.tint(Color.Red),
                        FilterQuality.High,
                        clipToBounds = false
                    )

                    SubcomposeAsyncImage(
                        uri = ResourceImages.jpeg.uri,
                        contentDescription = "test image",
                        modifier = Modifier,
                        state = rememberAsyncImageState(),
                        loading = {},
                        success = {},
                        error = {},
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
    fun testSubcomposeAsyncImage2() {
        runComposeUiTest {
            setContent {
                LifecycleContainer {
                    SubcomposeAsyncImage(
                        ResourceImages.jpeg.uri,
                        "test image",
                        Modifier,
                        rememberAsyncImageState(),
                        Alignment.TopStart,
                        ContentScale.Crop,
                        0.5f,
                        ColorFilter.tint(Color.Red),
                        FilterQuality.High,
                    ) {

                    }

                    SubcomposeAsyncImage(
                        uri = ResourceImages.jpeg.uri,
                        contentDescription = "test image",
                        modifier = Modifier,
                        state = rememberAsyncImageState(),
                        alignment = Alignment.TopStart,
                        contentScale = ContentScale.Crop,
                        alpha = 0.5f,
                        colorFilter = ColorFilter.tint(Color.Red),
                        filterQuality = FilterQuality.High,
                    ) {

                    }
                }
            }

            // TODO test: Screenshot test or draw to Bitmap, then compare Bitmap
        }
    }

    @Test
    fun testSubcomposeAsyncImage3() {
        runComposeUiTest {
            setContent {
                LifecycleContainer {
                    SubcomposeAsyncImage(
                        ComposableImageRequest(ResourceImages.jpeg.uri),
                        "test image"
                    )

                    SubcomposeAsyncImage(
                        ComposableImageRequest(ResourceImages.jpeg.uri),
                        "test image",
                        Modifier,
                        rememberAsyncImageState(),
                        {},
                        {},
                        {},
                        Alignment.TopStart,
                        ContentScale.Crop,
                        0.5f,
                        ColorFilter.tint(Color.Red),
                        FilterQuality.High,
                        clipToBounds = false
                    )

                    SubcomposeAsyncImage(
                        request = ComposableImageRequest(ResourceImages.jpeg.uri),
                        contentDescription = "test image",
                        modifier = Modifier,
                        state = rememberAsyncImageState(),
                        loading = {},
                        success = {},
                        error = {},
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
    fun testSubcomposeAsyncImage4() {
        runComposeUiTest {
            setContent {
                LifecycleContainer {
                    SubcomposeAsyncImage(
                        ComposableImageRequest(ResourceImages.jpeg.uri),
                        "test image",
                        Modifier,
                        rememberAsyncImageState(),
                        Alignment.TopStart,
                        ContentScale.Crop,
                        0.5f,
                        ColorFilter.tint(Color.Red),
                        FilterQuality.High,
                    ) {

                    }

                    SubcomposeAsyncImage(
                        request = ComposableImageRequest(ResourceImages.jpeg.uri),
                        contentDescription = "test image",
                        modifier = Modifier,
                        state = rememberAsyncImageState(),
                        alignment = Alignment.TopStart,
                        contentScale = ContentScale.Crop,
                        alpha = 0.5f,
                        colorFilter = ColorFilter.tint(Color.Red),
                        filterQuality = FilterQuality.High,
                    ) {

                    }
                }
            }

            // TODO test: Screenshot test or draw to Bitmap, then compare Bitmap
        }
    }
}