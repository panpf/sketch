@file:OptIn(ExperimentalTestApi::class)

package com.github.panpf.sketch.compose.core.common.test

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.github.panpf.sketch.AsyncImageState
import com.github.panpf.sketch.BitmapImage
import com.github.panpf.sketch.LocalPlatformContext
import com.github.panpf.sketch.PainterImage
import com.github.panpf.sketch.PainterState
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.painter.ImageBitmapPainter
import com.github.panpf.sketch.rememberAsyncImageState
import com.github.panpf.sketch.request.GlobalLifecycle
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.LoadState
import com.github.panpf.sketch.request.Progress
import com.github.panpf.sketch.request.error
import com.github.panpf.sketch.request.name
import com.github.panpf.sketch.request.placeholder
import com.github.panpf.sketch.resize.FixedSizeResolver
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.size
import com.github.panpf.sketch.target.AsyncImageTarget
import com.github.panpf.sketch.test.singleton.SingletonSketch
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.ComposeSize
import com.github.panpf.sketch.test.utils.DelayRequestInterceptor
import com.github.panpf.sketch.test.utils.Platform
import com.github.panpf.sketch.test.utils.TestErrorEqualsSizeResolver
import com.github.panpf.sketch.test.utils.TestHttpStack
import com.github.panpf.sketch.test.utils.TestHttpUriFetcher
import com.github.panpf.sketch.test.utils.TestTarget
import com.github.panpf.sketch.test.utils.asOrThrow
import com.github.panpf.sketch.test.utils.block
import com.github.panpf.sketch.test.utils.current
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.runInNewSketchWithUse
import com.github.panpf.sketch.test.utils.similarity
import com.github.panpf.sketch.test.utils.toPreviewBitmap
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.div
import com.github.panpf.sketch.util.screenSize
import com.github.panpf.sketch.util.toHexString
import com.github.panpf.sketch.util.toIntSize
import com.github.panpf.sketch.util.toSize
import com.github.panpf.sketch.util.windowContainerSize
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AsyncImageStateTest {

    @Test
    fun testRememberAsyncImageState() {
        val testLifecycle = GlobalLifecycle
        runComposeUiTest {
            setContent {
                CompositionLocalProvider(LocalLifecycleOwner provides testLifecycle.owner) {
                    rememberAsyncImageState().apply {
                        assertEquals(expected = testLifecycle, actual = lifecycle)
                        assertEquals(expected = false, actual = inspectionMode)
                        assertEquals(
                            expected = windowContainerSize(),
                            actual = windowContainerSize
                        )
                        assertEquals(expected = null, actual = imageOptions)
                    }

                    CompositionLocalProvider(LocalInspectionMode provides true) {
                        rememberAsyncImageState().apply {
                            assertEquals(expected = GlobalLifecycle, actual = lifecycle)
                            assertEquals(expected = true, actual = inspectionMode)
                            assertEquals(
                                expected = windowContainerSize(),
                                actual = windowContainerSize
                            )
                            assertEquals(expected = null, actual = imageOptions)
                        }
                    }

                    rememberAsyncImageState(ImageOptions { size(101, 202) }).apply {
                        assertEquals(expected = testLifecycle, actual = lifecycle)
                        assertEquals(expected = false, actual = inspectionMode)
                        assertEquals(
                            expected = windowContainerSize(),
                            actual = windowContainerSize
                        )
                        assertEquals(
                            expected = ImageOptions { size(101, 202) },
                            actual = imageOptions
                        )
                    }

                    rememberAsyncImageState {
                        ImageOptions { size(202, 101) }
                    }.apply {
                        assertEquals(expected = testLifecycle, actual = lifecycle)
                        assertEquals(expected = false, actual = inspectionMode)
                        assertEquals(
                            expected = windowContainerSize(),
                            actual = windowContainerSize
                        )
                        assertEquals(
                            expected = ImageOptions { size(202, 101) },
                            actual = imageOptions
                        )
                    }
                }
            }
        }
    }

    @Test
    fun testConstructor() {
        val context = getTestContext()
        runComposeUiTest {
            setContent {
                AsyncImageState(
                    context = context,
                    inspectionMode = false,
                    lifecycle = GlobalLifecycle,
                    imageOptions = ImageOptions()
                ).apply {
                    assertEquals(expected = GlobalLifecycle, actual = lifecycle)
                    assertEquals(expected = false, actual = inspectionMode)
                    assertEquals(expected = null, actual = windowContainerSize)
                    assertEquals(expected = ImageOptions(), actual = imageOptions)
                }
            }
        }
    }

    @Test
    fun testLifecycle() {
        val context = getTestContext()
        val lifecycle = GlobalLifecycle
        val asyncImageState = AsyncImageState(
            context = context,
            inspectionMode = false,
            lifecycle = lifecycle,
            imageOptions = ImageOptions()
        )
        assertEquals(
            expected = lifecycle,
            actual = asyncImageState.lifecycle
        )
    }

    @Test
    fun testImageOptions() {
        val context = getTestContext()
        val asyncImageState1 = AsyncImageState(
            context = context,
            inspectionMode = false,
            lifecycle = GlobalLifecycle,
            imageOptions = null
        )
        assertEquals(
            expected = null,
            actual = asyncImageState1.imageOptions
        )

        val asyncImageState2 = AsyncImageState(
            context = context,
            inspectionMode = false,
            lifecycle = GlobalLifecycle,
            imageOptions = ImageOptions()
        )
        assertEquals(
            expected = ImageOptions(),
            actual = asyncImageState2.imageOptions
        )
    }

    @Test
    fun testContentScaleAndAlignment() {
        val context = getTestContext()
        val asyncImageState = AsyncImageState(
            context = context,
            inspectionMode = false,
            lifecycle = GlobalLifecycle,
            imageOptions = ImageOptions()
        )
        assertEquals(
            expected = ContentScale.Fit,
            actual = asyncImageState.contentScale
        )
        asyncImageState.contentScale = ContentScale.FillBounds
        assertEquals(
            expected = ContentScale.FillBounds,
            actual = asyncImageState.contentScale
        )

        assertEquals(
            expected = Alignment.Center,
            actual = asyncImageState.alignment
        )
        asyncImageState.alignment = Alignment.TopStart
        assertEquals(
            expected = Alignment.TopStart,
            actual = asyncImageState.alignment
        )
    }

    @Test
    fun testFilterQuality() {
        val context = getTestContext()
        val asyncImageState = AsyncImageState(
            context = context,
            inspectionMode = false,
            lifecycle = GlobalLifecycle,
            imageOptions = ImageOptions()
        )

        assertEquals(
            expected = DrawScope.DefaultFilterQuality,
            actual = asyncImageState.filterQuality
        )

        asyncImageState.filterQuality = FilterQuality.High
        assertEquals(expected = FilterQuality.High, actual = asyncImageState.filterQuality)
    }

    @Test
    fun testSize() {
        val context = getTestContext()
        val windowContainerSize = context.screenSize()
        val asyncImageState = AsyncImageState(
            context = context,
            inspectionMode = false,
            lifecycle = GlobalLifecycle,
            imageOptions = ImageOptions()
        ).apply {
            setWindowContainerSizeWithLeast(context.screenSize().toIntSize())
        }
        assertEquals(expected = null, actual = asyncImageState.size)
        assertEquals(
            expected = null,
            actual = asyncImageState.sizeResolver.sizeState.value
        )

        asyncImageState.setSizeWithLeast(IntSize(0, 1000))
        assertEquals(
            expected = IntSize(windowContainerSize.width, 1000),
            actual = asyncImageState.size
        )
        assertEquals(
            expected = IntSize(windowContainerSize.width, 1000),
            actual = asyncImageState.sizeResolver.sizeState.value
        )

        asyncImageState.setSizeWithLeast(IntSize(1000, 0))
        assertEquals(
            expected = IntSize(1000, windowContainerSize.height),
            actual = asyncImageState.size
        )
        assertEquals(
            expected = IntSize(1000, windowContainerSize.height),
            actual = asyncImageState.sizeResolver.sizeState.value
        )

        asyncImageState.setSizeWithLeast(IntSize(0, 0))
        assertEquals(
            expected = IntSize(windowContainerSize.width, windowContainerSize.height),
            actual = asyncImageState.size
        )
        assertEquals(
            expected = IntSize(windowContainerSize.width, windowContainerSize.height),
            actual = asyncImageState.sizeResolver.sizeState.value
        )

        asyncImageState.setSizeWithLeast(IntSize(300, 400))
        assertEquals(expected = IntSize(300, 400), actual = asyncImageState.size)
        assertEquals(
            expected = IntSize(300, 400),
            actual = asyncImageState.sizeResolver.sizeState.value
        )
    }

    @Test
    fun testRemembered() {
        val context = getTestContext()
        val asyncImageState = AsyncImageState(
            context = context,
            inspectionMode = false,
            lifecycle = GlobalLifecycle,
            imageOptions = ImageOptions()
        )
        @Suppress("USELESS_IS_CHECK")
        assertTrue(actual = asyncImageState is RememberObserver)

        assertEquals(expected = 0, actual = asyncImageState.rememberedCounter.count)
        assertEquals(expected = 0, actual = asyncImageState.requestManager.rememberedCounter.count)
        assertEquals(expected = null, actual = asyncImageState.coroutineScope)

        asyncImageState.onRemembered()
        assertEquals(expected = 1, actual = asyncImageState.rememberedCounter.count)
        assertEquals(expected = 1, actual = asyncImageState.requestManager.rememberedCounter.count)
        assertNotEquals(illegal = null, actual = asyncImageState.coroutineScope)

        asyncImageState.onRemembered()
        assertEquals(expected = 2, actual = asyncImageState.rememberedCounter.count)
        assertEquals(expected = 1, actual = asyncImageState.requestManager.rememberedCounter.count)
        assertNotEquals(illegal = null, actual = asyncImageState.coroutineScope)

        asyncImageState.onRemembered()
        assertEquals(expected = 3, actual = asyncImageState.rememberedCounter.count)
        assertEquals(expected = 1, actual = asyncImageState.requestManager.rememberedCounter.count)
        assertNotEquals(illegal = null, actual = asyncImageState.coroutineScope)

        asyncImageState.onAbandoned()
        assertEquals(expected = 2, actual = asyncImageState.rememberedCounter.count)
        assertEquals(expected = 1, actual = asyncImageState.requestManager.rememberedCounter.count)
        assertNotEquals(illegal = null, actual = asyncImageState.coroutineScope)

        asyncImageState.onForgotten()
        assertEquals(expected = 1, actual = asyncImageState.rememberedCounter.count)
        assertEquals(expected = 1, actual = asyncImageState.requestManager.rememberedCounter.count)
        assertNotEquals(illegal = null, actual = asyncImageState.coroutineScope)

        asyncImageState.onForgotten()
        assertEquals(expected = 0, actual = asyncImageState.rememberedCounter.count)
        assertEquals(expected = 0, actual = asyncImageState.requestManager.rememberedCounter.count)
        assertEquals(expected = null, actual = asyncImageState.coroutineScope)
    }

    @Test
    fun testCheckRequest() {
        val (context, sketch) = getTestContextAndSketch()
        var throwable: Throwable?
        val asyncImageState = AsyncImageState(
            context = context,
            inspectionMode = true,
            lifecycle = GlobalLifecycle,
            imageOptions = ImageOptions()
        ).apply {
            coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable1 ->
                throwable1.printStackTrace()
                throwable = throwable1
            }
        }

        // Normal
        throwable = null
        runComposeUiTest {
            setContent {
                remember { asyncImageState }
                asyncImageState.sketch = sketch
                asyncImageState.request = ImageRequest(context, ResourceImages.jpeg.uri)
                asyncImageState.contentScale = ContentScale.Fit
                asyncImageState.alignment = Alignment.Center
                asyncImageState.filterQuality = DrawScope.DefaultFilterQuality
            }
            waitForIdle()
            block(100)
        }
        assertNull(throwable)

        // addListener is not allowed
        throwable = null
        runComposeUiTest {
            setContent {
                remember { asyncImageState }
                asyncImageState.sketch = sketch
                asyncImageState.request = ImageRequest(context, ResourceImages.jpeg.uri) {
                    addListener(
                        onStart = {

                        },
                    )
                }
                asyncImageState.contentScale = ContentScale.Fit
                asyncImageState.alignment = Alignment.Center
                asyncImageState.filterQuality = DrawScope.DefaultFilterQuality
            }
            waitForIdle()
            block(100)
        }
        assertTrue(actual = throwable is IllegalArgumentException, message = "throwable=$throwable")

        // addProgressListener is not allowed
        throwable = null
        runComposeUiTest {
            setContent {
                remember { asyncImageState }
                asyncImageState.sketch = sketch
                asyncImageState.request = ImageRequest(context, ResourceImages.jpeg.uri) {
                    addProgressListener { _, _ -> }
                }
                asyncImageState.contentScale = ContentScale.Fit
                asyncImageState.alignment = Alignment.Center
                asyncImageState.filterQuality = DrawScope.DefaultFilterQuality
            }
            waitForIdle()
            block(100)
        }
        assertTrue(actual = throwable is IllegalArgumentException, message = "throwable=$throwable")

        // target is not allowed
        throwable = null
        runComposeUiTest {
            setContent {
                remember { asyncImageState }
                asyncImageState.sketch = sketch
                asyncImageState.request = ImageRequest(context, ResourceImages.jpeg.uri) {
                    target(TestTarget())
                }
                asyncImageState.contentScale = ContentScale.Fit
                asyncImageState.alignment = Alignment.Center
                asyncImageState.filterQuality = DrawScope.DefaultFilterQuality
            }
            waitForIdle()
            block(100)
        }
        assertTrue(actual = throwable is IllegalArgumentException, message = "throwable=$throwable")

        // Correct equals implementation
        throwable = null
        runComposeUiTest {
            setContent {
                remember { asyncImageState }
                asyncImageState.sketch = sketch
                asyncImageState.request = ImageRequest(context, ResourceImages.jpeg.uri) {
                    size(FixedSizeResolver(Size(100, 100)))
                }
                asyncImageState.contentScale = ContentScale.Fit
                asyncImageState.alignment = Alignment.Center
                asyncImageState.filterQuality = DrawScope.DefaultFilterQuality
            }
            waitForIdle()
            block(100)

            asyncImageState.request = ImageRequest(context, ResourceImages.jpeg.uri) {
                size(FixedSizeResolver(Size(100, 100)))
            }
            block(100)
        }
        assertNull(throwable)

        // Wrong equals implementation
        throwable = null
        runComposeUiTest {
            setContent {
                remember { asyncImageState }
                asyncImageState.sketch = sketch
                asyncImageState.request = ImageRequest(context, ResourceImages.jpeg.uri) {
                    size(TestErrorEqualsSizeResolver(Size(100, 100)))
                }
                asyncImageState.contentScale = ContentScale.Fit
                asyncImageState.alignment = Alignment.Center
                asyncImageState.filterQuality = DrawScope.DefaultFilterQuality
            }
            waitForIdle()
            block(100)

            asyncImageState.request = ImageRequest(context, ResourceImages.jpeg.uri) {
                size(TestErrorEqualsSizeResolver(Size(100, 100)))
            }
            block(100)
        }
        assertTrue(actual = throwable is IllegalArgumentException, message = "throwable=$throwable")
    }

    @Test
    fun testPreview() {
        if (Platform.current == Platform.iOS) {
            // Files in kotlin resources cannot be accessed in ios test environment.
            return
        }
        val (context, sketch) = getTestContextAndSketch()
        val asyncImageState = AsyncImageState(
            context = context,
            inspectionMode = true,
            lifecycle = GlobalLifecycle,
            imageOptions = ImageOptions()
        )

        val request = ImageRequest(context, ResourceImages.jpeg.uri) {
            size(Size.Origin)
        }
        val request2 = ImageRequest(context, ResourceImages.jpeg.uri) {
            size(Size.Origin)
            placeholder(Color.Red)
        }
        assertNotEquals(illegal = request, actual = request2)

        runComposeUiTest {
            setContent {
                remember { asyncImageState }
                asyncImageState.sketch = sketch
                asyncImageState.request = request
                asyncImageState.contentScale = ContentScale.Fit
                asyncImageState.alignment = Alignment.Center
                asyncImageState.filterQuality = DrawScope.DefaultFilterQuality
            }
            waitForIdle()
            block(100)

            assertEquals(expected = null, actual = asyncImageState.painter)
            assertEquals(
                expected = PainterState.Loading(null),
                actual = asyncImageState.painterState
            )
            assertEquals(expected = LoadState.Started(request), actual = asyncImageState.loadState)

            asyncImageState.request = request2
            block(100)
            assertEquals(expected = ColorPainter(Color.Red), actual = asyncImageState.painter)
            assertEquals(
                expected = PainterState.Loading(ColorPainter(Color.Red)),
                actual = asyncImageState.painterState
            )
            assertEquals(expected = LoadState.Started(request2), actual = asyncImageState.loadState)
        }
    }

    @Test
    fun testLoad() {
        if (Platform.current == Platform.iOS) {
            // Files in kotlin resources cannot be accessed in ios test environment.
            return
        }
        val (context, sketch) = getTestContextAndSketch()

        val resourceImage = ResourceImages.jpeg
        val asyncImageState = AsyncImageState(
            context = context,
            inspectionMode = false,
            lifecycle = GlobalLifecycle,
            imageOptions = ImageOptions {
                memoryCachePolicy(CachePolicy.DISABLED)
                size(resourceImage.size / 2f)
            }
        )
        val request0 = ImageRequest(context, resourceImage.uri)
        val request1 = ImageRequest(context, resourceImage.uri) {
            size(Size.Origin)
        }
        val request2 = ImageRequest(context, resourceImage.uri) {
            resize(300, 300, Precision.EXACTLY)
            placeholder(Color.Red)
        }
        runComposeUiTest {
            setContent {
                remember { asyncImageState }
                asyncImageState.sketch = sketch
                asyncImageState.request = request0
                asyncImageState.contentScale = ContentScale.Fit
                asyncImageState.alignment = Alignment.Center
                asyncImageState.filterQuality = DrawScope.DefaultFilterQuality
            }
            waitForIdle()
            block(2000)

            asyncImageState.painter!!.apply {
                assertTrue(actual = this is ImageBitmapPainter, message = "painter=$this")
                assertEquals(expected = (resourceImage.size / 2f).toSize(), actual = intrinsicSize)
                assertEquals(expected = FilterQuality.Low, actual = filterQuality)
            }
            assertTrue(
                actual = asyncImageState.painterState is PainterState.Success,
                message = "painterState=${asyncImageState.painterState}"
            )
            asyncImageState.result!!.asOrThrow<ImageResult.Success>().apply {
                assertTrue(actual = image is BitmapImage, message = "image=$image")
                assertEquals(expected = resourceImage.size / 2f, actual = image.size)
                assertEquals(expected = resourceImage.imageInfo, actual = imageInfo)
            }
            assertTrue(
                actual = asyncImageState.loadState is LoadState.Success,
                message = "loadState=${asyncImageState.loadState}"
            )
            assertTrue(
                actual = asyncImageState.loadState!!.request.target is AsyncImageTarget,
                message = "loadState=${asyncImageState.loadState}"
            )
            val bitmap0 =
                asyncImageState.result!!.asOrThrow<ImageResult.Success>().image.asOrThrow<BitmapImage>().bitmap

            asyncImageState.request = request1
            block(2000)
            asyncImageState.painter!!.apply {
                assertTrue(actual = this is ImageBitmapPainter, message = "painter=$this")
                assertEquals(expected = resourceImage.size.toSize(), actual = intrinsicSize)
                assertEquals(expected = FilterQuality.Low, actual = filterQuality)
            }
            asyncImageState.result!!.asOrThrow<ImageResult.Success>().apply {
                assertTrue(actual = image is BitmapImage, message = "image=$image")
                assertEquals(expected = resourceImage.size, actual = image.size)
                assertEquals(expected = resourceImage.imageInfo, actual = imageInfo)
            }
            val bitmap1 =
                asyncImageState.result!!.asOrThrow<ImageResult.Success>().image.asOrThrow<BitmapImage>().bitmap

            asyncImageState.request = request2
            block(2000)
            asyncImageState.painter!!.apply {
                assertTrue(actual = this is ImageBitmapPainter, message = "painter=$this")
                assertEquals(expected = ComposeSize(300f, 300f), actual = intrinsicSize)
                assertEquals(expected = FilterQuality.Low, actual = filterQuality)
            }
            asyncImageState.result!!.asOrThrow<ImageResult.Success>().apply {
                assertTrue(actual = image is BitmapImage, message = "image=$image")
                assertEquals(expected = Size(300, 300), actual = image.size)
                assertEquals(expected = resourceImage.imageInfo, actual = imageInfo)
            }
            val bitmap2 =
                asyncImageState.result!!.asOrThrow<ImageResult.Success>().image.asOrThrow<BitmapImage>().bitmap

            asyncImageState.contentScale = ContentScale.FillBounds
            block(2000)
            asyncImageState.painter!!.apply {
                assertTrue(actual = this is ImageBitmapPainter, message = "painter=$this")
                assertEquals(expected = ComposeSize(300f, 300f), actual = intrinsicSize)
                assertEquals(expected = FilterQuality.Low, actual = filterQuality)
            }
            asyncImageState.result!!.asOrThrow<ImageResult.Success>().apply {
                assertTrue(actual = image is BitmapImage, message = "image=$image")
                assertEquals(expected = Size(300, 300), actual = image.size)
                assertEquals(expected = resourceImage.imageInfo, actual = imageInfo)
            }
            val bitmap3 =
                asyncImageState.result!!.asOrThrow<ImageResult.Success>().image.asOrThrow<BitmapImage>().bitmap

            asyncImageState.filterQuality = FilterQuality.High
            block(2000)
            asyncImageState.painter!!.apply {
                assertTrue(actual = this is ImageBitmapPainter, message = "painter=$this")
                assertEquals(expected = ComposeSize(300f, 300f), actual = intrinsicSize)
                assertEquals(expected = FilterQuality.High, actual = filterQuality)
            }
            asyncImageState.result!!.asOrThrow<ImageResult.Success>().apply {
                assertTrue(actual = image is BitmapImage, message = "image=$image")
                assertEquals(expected = Size(300, 300), actual = image.size)
                assertEquals(expected = resourceImage.imageInfo, actual = imageInfo)
            }
            val bitmap4 =
                asyncImageState.result!!.asOrThrow<ImageResult.Success>().image.asOrThrow<BitmapImage>().bitmap

            @Suppress("UNUSED_VARIABLE", "unused") val bitmap0Preview = bitmap0.toPreviewBitmap()
            @Suppress("UNUSED_VARIABLE", "unused") val bitmap1Preview = bitmap1.toPreviewBitmap()
            @Suppress("UNUSED_VARIABLE", "unused") val bitmap2Preview = bitmap2.toPreviewBitmap()
            @Suppress("UNUSED_VARIABLE", "unused") val bitmap3Preview = bitmap3.toPreviewBitmap()
            @Suppress("UNUSED_VARIABLE", "unused") val bitmap4Preview = bitmap3.toPreviewBitmap()
            bitmap0.similarity(bitmap1).apply {
                assertTrue(actual = this == 0, message = "similarity=$this")
            }
            bitmap1.similarity(bitmap2).apply {
                assertTrue(actual = this >= 5, message = "similarity=$this")
            }
            bitmap1.similarity(bitmap3).apply {
                assertTrue(actual = this == 0, message = "similarity=$this")
            }
            bitmap1.similarity(bitmap4).apply {
                assertTrue(actual = this == 0, message = "similarity=$this")
            }
            bitmap2.similarity(bitmap3).apply {
                assertTrue(actual = this >= 5, message = "similarity=$this")
            }
            bitmap2.similarity(bitmap4).apply {
                assertTrue(actual = this >= 5, message = "similarity=$this")
            }
            bitmap3.similarity(bitmap4).apply {
                assertTrue(actual = this == 0, message = "similarity=$this")
            }
        }
    }

    @Test
    fun testRestart() {
        if (Platform.current == Platform.iOS) {
            // Files in kotlin resources cannot be accessed in ios test environment.
            return
        }
        val (context, sketch) = getTestContextAndSketch()

        val resourceImage = ResourceImages.jpeg
        val asyncImageState = AsyncImageState(
            context = context,
            inspectionMode = false,
            lifecycle = GlobalLifecycle,
            imageOptions = ImageOptions {
                memoryCachePolicy(CachePolicy.DISABLED)
                size(Size.Origin)
            }
        )
        val request = ImageRequest(context, resourceImage.uri)
        runComposeUiTest {
            setContent {
                remember { asyncImageState }
                asyncImageState.sketch = sketch
                asyncImageState.request = request
                asyncImageState.contentScale = ContentScale.Fit
                asyncImageState.alignment = Alignment.Center
                asyncImageState.filterQuality = DrawScope.DefaultFilterQuality
            }
            waitForIdle()
            block(2000)

            assertTrue(
                actual = asyncImageState.loadState is LoadState.Success,
                message = "loadState=${asyncImageState.loadState}"
            )
            assertTrue(
                actual = asyncImageState.result?.image is BitmapImage,
                message = "image=${asyncImageState.result?.image}"
            )
            val loadState1 = asyncImageState.loadState
            val image1 = asyncImageState.result!!.image

            asyncImageState.restart()
            block(2000)
            assertTrue(
                actual = asyncImageState.loadState is LoadState.Success,
                message = "loadState=${asyncImageState.loadState}"
            )
            assertTrue(
                actual = asyncImageState.result?.image is BitmapImage,
                message = "image=${asyncImageState.result?.image}"
            )
            val loadState2 = asyncImageState.loadState
            val image2 = asyncImageState.result!!.image

            assertNotSame(illegal = loadState1, actual = loadState2)
            assertNotSame(illegal = image1, actual = image2)
        }
    }

    @Test
    fun testPainterResultPainterStateLoadStateProgress() = runTest {
        if (Platform.current == Platform.iOS) {
            // Files in kotlin resources cannot be accessed in ios test environment.
            return@runTest
        }
        runInNewSketchWithUse({
            components {
                addFetcher(TestHttpUriFetcher.Factory(it, readDelayMillis = 20))
            }
        }) { context, sketch ->
            // success
            runComposeUiTest {
                val asyncImageState = AsyncImageState(
                    context = context,
                    inspectionMode = false,
                    lifecycle = GlobalLifecycle,
                    imageOptions = ImageOptions {
                        memoryCachePolicy(CachePolicy.DISABLED)
                        resultCachePolicy(CachePolicy.DISABLED)
                        downloadCachePolicy(CachePolicy.DISABLED)
                        size(Size.Origin)
                    }
                )
                val request = ImageRequest(context, TestHttpStack.testImages.first().uri) {
                    placeholder(Color.Gray)
                    error(Color.Red)
                }
                val painterHistory = mutableListOf<Painter?>()
                val painterStateHistory = mutableListOf<PainterState?>()
                val resultHistory = mutableListOf<ImageResult?>()
                val loadStateHistory = mutableListOf<LoadState?>()
                val progressHistory = mutableListOf<Progress?>()

                setContent {
                    LaunchedEffect(Unit) {
                        snapshotFlow { asyncImageState.painter }.collect {
                            painterHistory.add(it)
                        }
                    }
                    LaunchedEffect(Unit) {
                        snapshotFlow { asyncImageState.painterState }.collect {
                            painterStateHistory.add(it)
                        }
                    }
                    LaunchedEffect(Unit) {
                        snapshotFlow { asyncImageState.result }.collect {
                            resultHistory.add(it)
                        }
                    }
                    LaunchedEffect(Unit) {
                        snapshotFlow { asyncImageState.loadState }.collect {
                            loadStateHistory.add(it)
                        }
                    }
                    LaunchedEffect(Unit) {
                        snapshotFlow { asyncImageState.progress }.collect {
                            progressHistory.add(it)
                        }
                    }
                    remember { asyncImageState }
                    asyncImageState.sketch = sketch
                    asyncImageState.request = request
                    asyncImageState.contentScale = ContentScale.Fit
                    asyncImageState.alignment = Alignment.Center
                    asyncImageState.filterQuality = DrawScope.DefaultFilterQuality
                }
                waitForIdle()
                block(4000)

                assertTrue(
                    actual = asyncImageState.loadState is LoadState.Success,
                    message = "loadState=${asyncImageState.loadState}"
                )
                assertTrue(
                    actual = asyncImageState.result?.image is BitmapImage,
                    message = "image=${asyncImageState.result?.image}"
                )

                assertEquals(
                    expected = 3,
                    actual = painterHistory.size,
                    message = "painterHistory=${painterHistory}"
                )
                assertEquals(expected = null, actual = painterHistory[0])
                assertEquals(expected = ColorPainter(Color.Gray), actual = painterHistory[1])
                assertTrue(
                    actual = painterHistory[2] is ImageBitmapPainter,
                    message = "painter=${painterHistory[2]}"
                )

                assertEquals(
                    expected = 3,
                    actual = painterStateHistory.size,
                    message = "painterStateHistory=${painterStateHistory}"
                )
                assertEquals(expected = null, actual = painterStateHistory[0])
                assertEquals(
                    expected = PainterState.Loading(ColorPainter(Color.Gray)),
                    actual = painterStateHistory[1]
                )
                assertTrue(
                    actual = painterStateHistory[2]?.asOrThrow<PainterState.Success>()?.painter is ImageBitmapPainter,
                    message = "painter=${painterStateHistory[2]}"
                )

                assertEquals(
                    expected = 2,
                    actual = resultHistory.size,
                    message = "resultHistory=${resultHistory}"
                )
                assertEquals(expected = null, actual = resultHistory[0])
                assertTrue(
                    actual = resultHistory[1]?.asOrThrow<ImageResult.Success>()?.image is BitmapImage,
                    message = "painter=${resultHistory[1]}"
                )

                assertEquals(
                    expected = 3,
                    actual = loadStateHistory.size,
                    message = "loadStateHistory=${loadStateHistory}"
                )
                assertEquals(expected = null, actual = loadStateHistory[0])
                assertTrue(
                    actual = loadStateHistory[1] is LoadState.Started,
                    message = "loadStateHistory[1]=${loadStateHistory[1]}"
                )
                assertTrue(
                    actual = loadStateHistory[2]?.asOrThrow<LoadState.Success>()?.result?.asOrThrow<ImageResult.Success>()?.image is BitmapImage,
                    message = "loadStateHistory[2]=${loadStateHistory[2]}"
                )

                assertTrue(
                    actual = progressHistory.size >= 5,
                    message = "progressHistory=${progressHistory}"
                )
                assertEquals(
                    expected = null,
                    actual = progressHistory[0],
                )
                assertTrue(
                    actual = progressHistory[1]!!.completedLength < progressHistory[1]!!.totalLength,
                    message = "progressHistory[1]=${progressHistory[1]}"
                )
                assertEquals(
                    expected = progressHistory.last()!!.completedLength,
                    actual = progressHistory.last()!!.totalLength,
                )
            }

            // error
            runComposeUiTest {
                val asyncImageState = AsyncImageState(
                    context = context,
                    inspectionMode = false,
                    lifecycle = GlobalLifecycle,
                    imageOptions = ImageOptions {
                        memoryCachePolicy(CachePolicy.DISABLED)
                        resultCachePolicy(CachePolicy.DISABLED)
                        downloadCachePolicy(CachePolicy.DISABLED)
                        size(Size.Origin)
                    }
                )
                val request = ImageRequest(context, TestHttpStack.errorImage.uri) {
                    placeholder(Color.Gray)
                    error(Color.Red)
                }
                val painterHistory = mutableListOf<Painter?>()
                val painterStateHistory = mutableListOf<PainterState?>()
                val resultHistory = mutableListOf<ImageResult?>()
                val loadStateHistory = mutableListOf<LoadState?>()
                val progressHistory = mutableListOf<Progress?>()
                setContent {
                    LaunchedEffect(Unit) {
                        snapshotFlow { asyncImageState.painter }.collect {
                            painterHistory.add(it)
                        }
                    }
                    LaunchedEffect(Unit) {
                        snapshotFlow { asyncImageState.painterState }.collect {
                            painterStateHistory.add(it)
                        }
                    }
                    LaunchedEffect(Unit) {
                        snapshotFlow { asyncImageState.result }.collect {
                            resultHistory.add(it)
                        }
                    }
                    LaunchedEffect(Unit) {
                        snapshotFlow { asyncImageState.loadState }.collect {
                            loadStateHistory.add(it)
                        }
                    }
                    LaunchedEffect(Unit) {
                        snapshotFlow { asyncImageState.progress }.collect {
                            progressHistory.add(it)
                        }
                    }
                    remember { asyncImageState }
                    asyncImageState.sketch = sketch
                    asyncImageState.request = request
                    asyncImageState.contentScale = ContentScale.Fit
                    asyncImageState.alignment = Alignment.Center
                    asyncImageState.filterQuality = DrawScope.DefaultFilterQuality
                }
                waitForIdle()
                block(2000)

                assertTrue(
                    actual = asyncImageState.loadState is LoadState.Error,
                    message = "loadState=${asyncImageState.loadState}"
                )
                assertTrue(
                    actual = asyncImageState.result?.image is PainterImage,
                    message = "image=${asyncImageState.result?.image}"
                )

                assertEquals(
                    expected = 3,
                    actual = painterHistory.size,
                    message = "painterHistory=${painterHistory}"
                )
                assertEquals(expected = null, actual = painterHistory[0])
                assertEquals(expected = ColorPainter(Color.Gray), actual = painterHistory[1])
                assertEquals(expected = ColorPainter(Color.Red), actual = painterHistory[2])

                assertEquals(
                    expected = 3,
                    actual = painterStateHistory.size,
                    message = "painterStateHistory=${painterStateHistory}"
                )
                assertEquals(expected = null, actual = painterStateHistory[0])
                assertEquals(
                    expected = PainterState.Loading(ColorPainter(Color.Gray)),
                    actual = painterStateHistory[1]
                )
                assertTrue(
                    actual = painterStateHistory[2] is PainterState.Error
                )
                assertEquals(
                    expected = ColorPainter(Color.Red),
                    actual = painterStateHistory[2]?.painter
                )

                assertEquals(
                    expected = 2,
                    actual = resultHistory.size,
                    message = "resultHistory=${resultHistory}"
                )
                assertEquals(expected = null, actual = resultHistory[0])
                assertEquals(
                    expected = PainterImage(ColorPainter(Color.Red)),
                    actual = resultHistory[1]?.asOrThrow<ImageResult.Error>()?.image
                )

                assertEquals(
                    expected = 3,
                    actual = loadStateHistory.size,
                    message = "loadStateHistory=${loadStateHistory}"
                )
                assertEquals(expected = null, actual = loadStateHistory[0])
                assertTrue(
                    actual = loadStateHistory[1] is LoadState.Started,
                    message = "loadStateHistory[1]=${loadStateHistory[1]}"
                )
                assertEquals(
                    expected = PainterImage(ColorPainter(Color.Red)),
                    actual = loadStateHistory[2]?.asOrThrow<LoadState.Error>()?.result?.asOrThrow<ImageResult.Error>()?.image,
                )

                assertTrue(
                    actual = progressHistory.size == 1,
                    message = "progressHistory=${progressHistory}"
                )
                assertEquals(
                    expected = null,
                    actual = progressHistory[0],
                )
            }
        }
    }

    @Test
    fun testRequestManagerAndRemembered() {
        val context = getTestContext()
        val state = AsyncImageState(context, false, GlobalLifecycle, null)
        assertEquals(expected = 0, actual = state.requestManager.rememberedCounter.count)

        state.onRemembered()
        assertEquals(expected = 1, actual = state.requestManager.rememberedCounter.count)

        state.onRemembered()
        assertEquals(expected = 1, actual = state.requestManager.rememberedCounter.count)

        state.onForgotten()
        assertEquals(expected = 1, actual = state.requestManager.rememberedCounter.count)

        state.onForgotten()
        assertEquals(expected = 0, actual = state.requestManager.rememberedCounter.count)

        state.onForgotten()
        assertEquals(expected = 0, actual = state.requestManager.rememberedCounter.count)
    }

    @Test
    fun testEqualsAndHashCode() = runTest {
        val context = getTestContext()
        val lifecycle = GlobalLifecycle
        val imageOptions = ImageOptions()
        val element1 = AsyncImageState(context, true, lifecycle, imageOptions)
        val element11 = AsyncImageState(context, true, lifecycle, imageOptions)

        assertNotEquals(illegal = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())

        assertNotEquals(illegal = element1.hashCode(), actual = element11.hashCode())
    }

    @Test
    fun testToString() = runTest {
        val context = getTestContext()
        val asyncImageState = AsyncImageState(context, true, GlobalLifecycle, ImageOptions())
        assertEquals(
            expected = "AsyncImageState@${asyncImageState.toHexString()}",
            actual = asyncImageState.toString()
        )
    }

    @Test
    fun testNoSharingState() {
        // The second request is immediately initiated when the first request reaches Started. It is expected that the first request will be cancelled, but the callback does not call back. The loadStateList is [Started, Started, Success], and the error is [Started, Started, Canceled, Success]
        val testLifecycle = GlobalLifecycle
        runComposeUiTest {
            val loadStateList = mutableListOf<LoadState>()
            setContent {
                CompositionLocalProvider(LocalLifecycleOwner provides testLifecycle.owner) {
                    val imageState = rememberAsyncImageState()
                    LaunchedEffect(imageState) {
                        imageState.onLoadState = {
                            val empty = loadStateList.isEmpty()
                            loadStateList.add(it)
                            if (empty && it is LoadState.Started) {
                                imageState.contentScale = ContentScale.None
                            }
                        }
                    }
                    val context = LocalPlatformContext.current
                    LaunchedEffect(imageState) {
                        imageState.contentScale = ContentScale.Fit
                        imageState.alignment = Alignment.Center
                        imageState.filterQuality = DrawScope.DefaultFilterQuality
                        imageState.sketch = SingletonSketch.get(context)
                        imageState.request = ImageRequest(context, ResourceImages.jpeg.uri) {
                            components {
                                addRequestInterceptor(DelayRequestInterceptor(1000))
                            }
                        }
                        imageState.setSizeWithLeast(IntSize(500, 500))
                    }
                }
            }
            block(2000)
            assertEquals("Started, Started, Success", loadStateList.joinToString { it.name })
        }
    }
}