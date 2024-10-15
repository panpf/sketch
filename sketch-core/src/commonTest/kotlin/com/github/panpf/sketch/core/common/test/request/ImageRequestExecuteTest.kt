package com.github.panpf.sketch.core.common.test.request

import androidx.lifecycle.Lifecycle.State.CREATED
import androidx.lifecycle.Lifecycle.State.STARTED
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.cache.CachePolicy.ENABLED
import com.github.panpf.sketch.cache.CachePolicy.READ_ONLY
import com.github.panpf.sketch.cache.CachePolicy.WRITE_ONLY
import com.github.panpf.sketch.cache.memoryCacheKey
import com.github.panpf.sketch.cache.resultCacheKey
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.Depth.LOCAL
import com.github.panpf.sketch.request.Depth.MEMORY
import com.github.panpf.sketch.request.Depth.NETWORK
import com.github.panpf.sketch.request.DepthException
import com.github.panpf.sketch.request.GlobalLifecycle
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.LifecycleResolver
import com.github.panpf.sketch.request.get
import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.resize.Scale.END_CROP
import com.github.panpf.sketch.resize.Scale.FILL
import com.github.panpf.sketch.resize.Scale.START_CROP
import com.github.panpf.sketch.size
import com.github.panpf.sketch.source.DataFrom
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.singleton.request.execute
import com.github.panpf.sketch.test.utils.FakeImage
import com.github.panpf.sketch.test.utils.FakeStateImage
import com.github.panpf.sketch.test.utils.ListenerSupervisor
import com.github.panpf.sketch.test.utils.ProgressListenerSupervisor
import com.github.panpf.sketch.test.utils.TestCountTarget
import com.github.panpf.sketch.test.utils.TestDecodeInterceptor
import com.github.panpf.sketch.test.utils.TestErrorDecoder
import com.github.panpf.sketch.test.utils.TestFetcherFactory
import com.github.panpf.sketch.test.utils.TestHttpStack
import com.github.panpf.sketch.test.utils.TestLifecycle
import com.github.panpf.sketch.test.utils.TestRequestInterceptor
import com.github.panpf.sketch.test.utils.TestResizeOnDrawImage
import com.github.panpf.sketch.test.utils.TestResizeOnDrawTarget
import com.github.panpf.sketch.test.utils.TestTarget
import com.github.panpf.sketch.test.utils.TestTransitionTarget
import com.github.panpf.sketch.test.utils.block
import com.github.panpf.sketch.test.utils.corners
import com.github.panpf.sketch.test.utils.exist
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.ratio
import com.github.panpf.sketch.test.utils.runBlock
import com.github.panpf.sketch.test.utils.runInNewSketchWithUse
import com.github.panpf.sketch.test.utils.samplingByTarget
import com.github.panpf.sketch.test.utils.target
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.transform.CircleCropTransformation
import com.github.panpf.sketch.transform.RotateTransformation
import com.github.panpf.sketch.transform.RoundedCornersTransformation
import com.github.panpf.sketch.transform.getCircleCropTransformed
import com.github.panpf.sketch.transform.getRotateTransformed
import com.github.panpf.sketch.transform.getRoundedCornersTransformed
import com.github.panpf.sketch.transition.CrossfadeTransition
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.SketchSize
import com.github.panpf.sketch.util.asOrNull
import com.github.panpf.sketch.util.asOrThrow
import com.github.panpf.sketch.util.screenSize
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ImageRequestExecuteTest {

    @Test
    fun testDepth() = runTest {
        runInNewSketchWithUse({
            httpStack(TestHttpStack(it))
        }) { context, sketch ->
            val imageUri = TestHttpStack.testImages.first().uri

            // default
            sketch.downloadCache.clear()
            sketch.memoryCache.clear()
            ImageRequest(context, imageUri) {
                resultCachePolicy(DISABLED)
                target(TestCountTarget())
            }.let {
                sketch.execute(it)
            }.asOrNull<ImageResult.Success>()!!.apply {
                assertEquals(DataFrom.NETWORK, dataFrom)
            }

            // NETWORK
            sketch.downloadCache.clear()
            sketch.memoryCache.clear()
            ImageRequest(context, imageUri) {
                resultCachePolicy(DISABLED)
                depth(NETWORK)
                target(TestCountTarget())
            }.let {
                sketch.execute(it)
            }.asOrNull<ImageResult.Success>()!!.apply {
                assertEquals(DataFrom.NETWORK, dataFrom)
            }

            // LOCAL
            sketch.downloadCache.clear()
            sketch.memoryCache.clear()
            sketch.execute(ImageRequest(context, imageUri) {
                resultCachePolicy(DISABLED)
                target(TestCountTarget())
            })
            sketch.memoryCache.clear()
            assertTrue(sketch.downloadCache.exist(imageUri))
            ImageRequest(context, imageUri) {
                resultCachePolicy(DISABLED)
                depth(LOCAL)
                target(TestCountTarget())
            }.let {
                sketch.execute(it)
            }.asOrNull<ImageResult.Success>()!!.apply {
                assertEquals(DataFrom.DOWNLOAD_CACHE, dataFrom)
            }

            sketch.downloadCache.clear()
            sketch.memoryCache.clear()
            ImageRequest(context, imageUri) {
                resultCachePolicy(DISABLED)
                depth(LOCAL)
                target(TestCountTarget())
            }.let {
                sketch.execute(it)
            }.asOrNull<ImageResult.Error>()!!.apply {
                assertTrue(throwable is DepthException)
            }

            // MEMORY
            sketch.memoryCache.clear()
            sketch.execute(ImageRequest(context, imageUri) {
                resultCachePolicy(DISABLED)
                target(TestCountTarget())
            })
            ImageRequest(context, imageUri) {
                resultCachePolicy(DISABLED)
                depth(MEMORY)
                target(TestCountTarget())
            }.let {
                sketch.execute(it)
            }.asOrNull<ImageResult.Success>()!!.apply {
                assertEquals(DataFrom.MEMORY_CACHE, dataFrom)
            }

            sketch.memoryCache.clear()
            ImageRequest(context, imageUri) {
                resultCachePolicy(DISABLED)
                depth(MEMORY)
                target(TestCountTarget())
            }.let {
                sketch.execute(it)
            }.asOrNull<ImageResult.Error>()!!.apply {
                assertTrue(throwable is DepthException)
            }
        }
    }

    @Test
    fun testDownloadCachePolicy() = runTest {
        runInNewSketchWithUse({
            httpStack(TestHttpStack(it))
        }) { context, sketch ->
            val diskCache = sketch.downloadCache
            val imageUri = TestHttpStack.testImages.first().uri

            /* ENABLED */
            diskCache.clear()
            assertFalse(diskCache.exist(imageUri))
            ImageRequest(context, imageUri) {
                memoryCachePolicy(DISABLED)
                resultCachePolicy(DISABLED)
                downloadCachePolicy(ENABLED)
            }.let {
                sketch.execute(it)
            }.asOrNull<ImageResult.Success>()!!.apply {
                assertEquals(DataFrom.NETWORK, dataFrom)
            }

            assertTrue(diskCache.exist(imageUri))
            ImageRequest(context, imageUri) {
                memoryCachePolicy(DISABLED)
                resultCachePolicy(DISABLED)
                downloadCachePolicy(ENABLED)
            }.let {
                sketch.execute(it)
            }.asOrNull<ImageResult.Success>()!!.apply {
                assertEquals(DataFrom.DOWNLOAD_CACHE, dataFrom)
            }

            /* DISABLED */
            diskCache.clear()
            assertFalse(diskCache.exist(imageUri))
            ImageRequest(context, imageUri) {
                memoryCachePolicy(DISABLED)
                resultCachePolicy(DISABLED)
                downloadCachePolicy(DISABLED)
            }.let {
                sketch.execute(it)
            }.asOrNull<ImageResult.Success>()!!.apply {
                assertEquals(DataFrom.NETWORK, dataFrom)
            }

            assertFalse(diskCache.exist(imageUri))
            ImageRequest(context, imageUri) {
                memoryCachePolicy(DISABLED)
                resultCachePolicy(DISABLED)
                downloadCachePolicy(DISABLED)
            }.let {
                sketch.execute(it)
            }.asOrNull<ImageResult.Success>()!!.apply {
                assertEquals(DataFrom.NETWORK, dataFrom)
            }

            /* READ_ONLY */
            diskCache.clear()
            assertFalse(diskCache.exist(imageUri))
            ImageRequest(context, imageUri) {
                memoryCachePolicy(DISABLED)
                resultCachePolicy(DISABLED)
                downloadCachePolicy(READ_ONLY)
            }.let {
                sketch.execute(it)
            }.asOrNull<ImageResult.Success>()!!.apply {
                assertEquals(DataFrom.NETWORK, dataFrom)
            }

            assertFalse(diskCache.exist(imageUri))
            ImageRequest(context, imageUri) {
                memoryCachePolicy(DISABLED)
                resultCachePolicy(DISABLED)
                downloadCachePolicy(READ_ONLY)
            }.let {
                sketch.execute(it)
            }.asOrNull<ImageResult.Success>()!!.apply {
                assertEquals(DataFrom.NETWORK, dataFrom)
            }

            assertFalse(diskCache.exist(imageUri))
            ImageRequest(context, imageUri) {
                memoryCachePolicy(DISABLED)
                resultCachePolicy(DISABLED)
                downloadCachePolicy(ENABLED)
            }.let {
                sketch.execute(it)
            }
            assertTrue(diskCache.exist(imageUri))
            ImageRequest(context, imageUri) {
                memoryCachePolicy(DISABLED)
                resultCachePolicy(DISABLED)
                downloadCachePolicy(READ_ONLY)
            }.let {
                sketch.execute(it)
            }.asOrNull<ImageResult.Success>()!!.apply {
                assertEquals(DataFrom.DOWNLOAD_CACHE, dataFrom)
            }

            /* WRITE_ONLY */
            diskCache.clear()
            assertFalse(diskCache.exist(imageUri))
            ImageRequest(context, imageUri) {
                memoryCachePolicy(DISABLED)
                resultCachePolicy(DISABLED)
                downloadCachePolicy(WRITE_ONLY)
            }.let {
                sketch.execute(it)
            }.asOrNull<ImageResult.Success>()!!.apply {
                assertEquals(DataFrom.NETWORK, dataFrom)
            }

            assertTrue(diskCache.exist(imageUri))
            ImageRequest(context, imageUri) {
                memoryCachePolicy(DISABLED)
                resultCachePolicy(DISABLED)
                downloadCachePolicy(WRITE_ONLY)
            }.let {
                sketch.execute(it)
            }.asOrNull<ImageResult.Success>()!!.apply {
                assertEquals(DataFrom.NETWORK, dataFrom)
            }
        }
    }

    @Test
    fun testResize() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        // default
        ImageRequest(context, ResourceImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
        }
            .let { sketch.execute(it) }
            .asOrNull<ImageResult.Success>()!!.apply {
                assertEquals(ResourceImages.jpeg.size, imageInfo.size)
                assertEquals(
                    samplingByTarget(ResourceImages.jpeg.size, context.screenSize()),
                    image.size
                )
                assertEquals(imageInfo.size.ratio, image.size.ratio)
            }

        // size: small, precision=LESS_PIXELS/SAME_ASPECT_RATIO/EXACTLY
        val smallSize1 = Size(600, 500)
        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(smallSize1)
            precision(LESS_PIXELS)
        }.let { sketch.execute(it) }
            .asOrNull<ImageResult.Success>()!!.apply {
                assertEquals(ResourceImages.jpeg.size, imageInfo.size)
                assertEquals(Size(323, 484), image.size)
                assertEquals(imageInfo.size.ratio, image.size.ratio)
            }
        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(smallSize1)
            precision(SAME_ASPECT_RATIO)
        }.let { sketch.execute(it) }
            .asOrNull<ImageResult.Success>()!!.apply {
                assertEquals(ResourceImages.jpeg.size, imageInfo.size)
                assertTrue(
                    actual = image.size == Size(322, 268) || image.size == Size(323, 269),
                    message = image.toString()
                )
                assertEquals(smallSize1.ratio, image.size.ratio)
            }
        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(smallSize1)
            precision(EXACTLY)
        }.let { sketch.execute(it) }
            .asOrNull<ImageResult.Success>()!!.apply {
                assertEquals(ResourceImages.jpeg.size, imageInfo.size)
                assertEquals(smallSize1, image.size)
            }

        val smallSize2 = Size(500, 600)
        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(smallSize2)
            precision(LESS_PIXELS)
        }.let { sketch.execute(it) }
            .asOrNull<ImageResult.Success>()!!.apply {
                assertEquals(ResourceImages.jpeg.size, imageInfo.size)
                assertEquals(Size(323, 484), image.size)
                assertEquals(imageInfo.size.ratio, image.size.ratio)
            }
        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(smallSize2)
            precision(SAME_ASPECT_RATIO)
        }.let { sketch.execute(it) }
            .asOrNull<ImageResult.Success>()!!.apply {
                assertEquals(ResourceImages.jpeg.size, imageInfo.size)
                assertTrue(
                    actual = image.size == Size(322, 387) || image.size == Size(323, 388),
                    message = image.toString()
                )
                assertEquals(smallSize2.ratio, image.size.ratio)
            }
        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(smallSize2)
            precision(EXACTLY)
        }.let { sketch.execute(it) }
            .asOrNull<ImageResult.Success>()!!.apply {
                assertEquals(ResourceImages.jpeg.size, imageInfo.size)
                assertEquals(smallSize2, image.size)
            }

        // size: same, precision=LESS_PIXELS/SAME_ASPECT_RATIO/EXACTLY
        val sameSize = Size(ResourceImages.jpeg.size.width, ResourceImages.jpeg.size.height)
        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(sameSize)
            precision(LESS_PIXELS)
        }.let { sketch.execute(it) }
            .asOrNull<ImageResult.Success>()!!.apply {
                assertEquals(ResourceImages.jpeg.size, imageInfo.size)
                assertEquals(sameSize, image.size)
            }
        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(sameSize)
            precision(SAME_ASPECT_RATIO)
        }.let { sketch.execute(it) }
            .asOrNull<ImageResult.Success>()!!.apply {
                assertEquals(ResourceImages.jpeg.size, imageInfo.size)
                assertEquals(sameSize, image.size)
            }
        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(sameSize)
            precision(EXACTLY)
        }.let { sketch.execute(it) }
            .asOrNull<ImageResult.Success>()!!.apply {
                assertEquals(ResourceImages.jpeg.size, imageInfo.size)
                assertEquals(sameSize, image.size)
            }

        // size: big, precision=LESS_PIXELS/SAME_ASPECT_RATIO/EXACTLY
        val bigSize1 = Size(2500, 2100)
        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(bigSize1)
            precision(LESS_PIXELS)
        }.let { sketch.execute(it) }
            .asOrNull<ImageResult.Success>()!!.apply {
                assertEquals(ResourceImages.jpeg.size, imageInfo.size)
                assertEquals(ResourceImages.jpeg.size, image.size)
                assertEquals(imageInfo.size.ratio, image.size.ratio)
            }
        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(bigSize1)
            precision(SAME_ASPECT_RATIO)
        }.let { sketch.execute(it) }
            .asOrNull<ImageResult.Success>()!!.apply {
                assertEquals(ResourceImages.jpeg.size, imageInfo.size)
                assertEquals(
                    Size(1291, 1084),
                    image.size
                )
                assertEquals(
                    bigSize1.ratio,
                    image.size.ratio
                )
            }
        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(bigSize1)
            precision(EXACTLY)
        }.let { sketch.execute(it) }
            .asOrNull<ImageResult.Success>()!!.apply {
                assertEquals(ResourceImages.jpeg.size, imageInfo.size)
                assertEquals(
                    bigSize1,
                    image.size
                )
            }

        val bigSize2 = Size(2100, 2500)
        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(bigSize2)
            precision(LESS_PIXELS)
        }.let { sketch.execute(it) }
            .asOrNull<ImageResult.Success>()!!.apply {
                assertEquals(ResourceImages.jpeg.size, imageInfo.size)
                assertEquals(
                    ResourceImages.jpeg.size,
                    image.size
                )
                assertEquals(imageInfo.size.ratio, image.size.ratio)
            }
        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(bigSize2)
            precision(SAME_ASPECT_RATIO)
        }.let { sketch.execute(it) }
            .asOrNull<ImageResult.Success>()!!.apply {
                assertEquals(ResourceImages.jpeg.size, imageInfo.size)
                assertEquals(
                    Size(1291, 1537),
                    image.size
                )
                assertEquals(
                    bigSize2.ratio,
                    image.size.ratio
                )
            }
        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(bigSize2)
            precision(EXACTLY)
        }.let { sketch.execute(it) }
            .asOrNull<ImageResult.Success>()!!.apply {
                assertEquals(ResourceImages.jpeg.size, imageInfo.size)
                assertEquals(
                    bigSize2,
                    image.size
                )
            }

        val bigSize3 = Size(800, 2500)
        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(bigSize3)
            precision(LESS_PIXELS)
        }.let { sketch.execute(it) }
            .asOrNull<ImageResult.Success>()!!.apply {
                assertEquals(ResourceImages.jpeg.size, imageInfo.size)
                assertEquals(
                    Size(646, 968),
                    image.size
                )
                assertEquals(imageInfo.size.ratio, image.size.ratio)
            }
        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(bigSize3)
            precision(SAME_ASPECT_RATIO)
        }.let { sketch.execute(it) }
            .asOrNull<ImageResult.Success>()!!.apply {
                assertEquals(ResourceImages.jpeg.size, imageInfo.size)
                assertEquals(
                    Size(620, 1936),
                    image.size
                )
                assertEquals(
                    bigSize3.ratio,
                    image.size.ratio
                )
            }
        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(bigSize3)
            precision(EXACTLY)
        }.let { sketch.execute(it) }
            .asOrNull<ImageResult.Success>()!!.apply {
                assertEquals(ResourceImages.jpeg.size, imageInfo.size)
                assertEquals(
                    bigSize3,
                    image.size
                )
            }

        val bigSize4 = Size(2500, 800)
        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(bigSize4)
            precision(LESS_PIXELS)
        }.let { sketch.execute(it) }
            .asOrNull<ImageResult.Success>()!!.apply {
                assertEquals(ResourceImages.jpeg.size, imageInfo.size)
                assertEquals(
                    Size(646, 968),
                    image.size
                )
                assertEquals(imageInfo.size.ratio, image.size.ratio)
            }
        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(bigSize4)
            precision(SAME_ASPECT_RATIO)
        }.let { sketch.execute(it) }
            .asOrNull<ImageResult.Success>()!!.apply {
                assertEquals(ResourceImages.jpeg.size, imageInfo.size)
                assertEquals(
                    Size(1291, 413),
                    image.size
                )
                assertEquals(
                    bigSize4.ratio,
                    image.size.ratio
                )
            }
        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(bigSize4)
            precision(EXACTLY)
        }.let { sketch.execute(it) }
            .asOrNull<ImageResult.Success>()!!.apply {
                assertEquals(ResourceImages.jpeg.size, imageInfo.size)
                assertEquals(
                    bigSize4,
                    image.size
                )
            }

        /* scale */
        val size = Size(600, 500)
        var sarStartCropBitmap: Image?
        var sarCenterCropBitmap: Image?
        var sarEndCropBitmap: Image?
        var sarFillCropBitmap: Image?
        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(size)
            precision(SAME_ASPECT_RATIO)
            scale(START_CROP)
        }.let { sketch.execute(it) }
            .asOrNull<ImageResult.Success>()!!.apply {
                sarStartCropBitmap = image
                assertEquals(ResourceImages.jpeg.size, imageInfo.size)
                assertTrue(
                    actual = image.size == Size(322, 268) || image.size == Size(323, 269),
                    message = image.toString()
                )
                assertEquals(
                    size.ratio,
                    image.size.ratio
                )
            }
        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(size)
            precision(SAME_ASPECT_RATIO)
            scale(CENTER_CROP)
        }.let { sketch.execute(it) }
            .asOrNull<ImageResult.Success>()!!.apply {
                sarCenterCropBitmap = image
                assertEquals(ResourceImages.jpeg.size, imageInfo.size)
                assertTrue(
                    actual = image.size == Size(322, 268) || image.size == Size(323, 269),
                    message = image.toString()
                )
                assertEquals(
                    size.ratio,
                    image.size.ratio
                )
            }
        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(size)
            precision(SAME_ASPECT_RATIO)
            scale(END_CROP)
        }.let { sketch.execute(it) }
            .asOrNull<ImageResult.Success>()!!.apply {
                sarEndCropBitmap = image
                assertEquals(ResourceImages.jpeg.size, imageInfo.size)
                assertTrue(
                    actual = image.size == Size(322, 268) || image.size == Size(323, 269),
                    message = image.toString()
                )
                assertEquals(
                    size.ratio,
                    image.size.ratio
                )
            }
        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(size)
            precision(SAME_ASPECT_RATIO)
            scale(FILL)
        }.let { sketch.execute(it) }
            .asOrNull<ImageResult.Success>()!!.apply {
                sarFillCropBitmap = image
                assertEquals(ResourceImages.jpeg.size, imageInfo.size)
                // In Android 11, the size of the image may be 323x269
                assertTrue(image.size == Size(323, 269) || image.size == Size(322, 268))
                assertEquals(size.ratio, image.size.ratio)
            }
        assertNotEquals(sarStartCropBitmap!!.corners(), sarCenterCropBitmap!!.corners())
        assertNotEquals(sarStartCropBitmap!!.corners(), sarEndCropBitmap!!.corners())
        assertNotEquals(sarStartCropBitmap!!.corners(), sarFillCropBitmap!!.corners())
        assertNotEquals(sarCenterCropBitmap!!.corners(), sarEndCropBitmap!!.corners())
        assertNotEquals(sarCenterCropBitmap!!.corners(), sarFillCropBitmap!!.corners())
        assertNotEquals(sarEndCropBitmap!!.corners(), sarFillCropBitmap!!.corners())

        var exactlyStartCropBitmap: Image?
        var exactlyCenterCropBitmap: Image?
        var exactlyEndCropBitmap: Image?
        var exactlyFillCropBitmap: Image?
        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(size)
            precision(EXACTLY)
            scale(START_CROP)
        }.let { sketch.execute(it) }
            .asOrNull<ImageResult.Success>()!!.apply {
                exactlyStartCropBitmap = image
                assertEquals(ResourceImages.jpeg.size, imageInfo.size)
                assertEquals(size, image.size)
            }
        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(size)
            precision(EXACTLY)
            scale(CENTER_CROP)
        }.let { sketch.execute(it) }
            .asOrNull<ImageResult.Success>()!!.apply {
                exactlyCenterCropBitmap = image
                assertEquals(ResourceImages.jpeg.size, imageInfo.size)
                assertEquals(size, image.size)
            }
        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(size)
            precision(EXACTLY)
            scale(END_CROP)
        }.let { sketch.execute(it) }
            .asOrNull<ImageResult.Success>()!!.apply {
                exactlyEndCropBitmap = image
                assertEquals(ResourceImages.jpeg.size, imageInfo.size)
                assertEquals(size, image.size)
            }
        ImageRequest(context, ResourceImages.jpeg.uri) {
            size(size)
            precision(EXACTLY)
            scale(FILL)
        }.let { sketch.execute(it) }
            .asOrNull<ImageResult.Success>()!!.apply {
                exactlyFillCropBitmap = image
                assertEquals(ResourceImages.jpeg.size, imageInfo.size)
                assertEquals(size, image.size)
            }
        assertNotEquals(
            exactlyStartCropBitmap!!.corners(),
            exactlyCenterCropBitmap!!.corners()
        )
        assertNotEquals(exactlyStartCropBitmap!!.corners(), exactlyEndCropBitmap!!.corners())
        assertNotEquals(
            exactlyStartCropBitmap!!.corners(),
            exactlyFillCropBitmap!!.corners()
        )
        assertNotEquals(
            exactlyCenterCropBitmap!!.corners(),
            exactlyEndCropBitmap!!.corners()
        )
        assertNotEquals(
            exactlyCenterCropBitmap!!.corners(),
            exactlyFillCropBitmap!!.corners()
        )
        assertNotEquals(exactlyEndCropBitmap!!.corners(), exactlyFillCropBitmap!!.corners())

        // origin
        var size1: Size?
        ImageRequest(context, ResourceImages.longQMSHT.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
        }
            .let { sketch.execute(it) }
            .asOrNull<ImageResult.Success>()!!.apply {
                assertEquals(ResourceImages.longQMSHT.size, imageInfo.size)
                assertEquals(
                    samplingByTarget(ResourceImages.longQMSHT.size, context.screenSize()),
                    image.size
                )
                assertEquals(imageInfo.size.ratio, image.size.ratio, 0.2f)
                size1 = image.size
            }
        ImageRequest(context, ResourceImages.longQMSHT.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            size(Size.Origin)
        }
            .let { sketch.execute(it) }
            .asOrNull<ImageResult.Success>()!!.apply {
                assertEquals(ResourceImages.longQMSHT.size, imageInfo.size)
                assertEquals(
                    samplingByTarget(ResourceImages.longQMSHT.size, Size.Origin),
                    image.size
                )
                assertEquals(imageInfo.size.ratio, image.size.ratio)

                assertNotEquals(size1!!, image.size)
            }
    }

    @Test
    fun testTransformations() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val imageUri = ResourceImages.jpeg.uri
        val request = ImageRequest(context, imageUri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
        }

        request.let { sketch.execute(it) }
            .asOrNull<ImageResult.Success>()!!.apply {
                assertTrue(transformeds?.all {
                    it.startsWith("ResizeTransformed") || it.startsWith("InSampledTransformed")
                } != false)
            }

        request.let { sketch.execute(it) }
            .asOrNull<ImageResult.Success>()!!.apply {
                assertNotEquals(
                    listOf(0, 0, 0, 0),
                    image.corners()
                )
                assertNull(
                    transformeds?.getRoundedCornersTransformed()
                )
            }
        request.newRequest {
            addTransformations(RoundedCornersTransformation(30f))
        }.let { sketch.execute(it) }
            .asOrNull<ImageResult.Success>()!!.apply {
                assertEquals(
                    listOf(0, 0, 0, 0),
                    image.corners()
                )
                assertNotNull(
                    transformeds?.getRoundedCornersTransformed()
                )
            }

        request.newRequest {
            size(500, 500)
            precision(LESS_PIXELS)
        }.let { sketch.execute(it) }
            .asOrNull<ImageResult.Success>()!!.apply {
                assertEquals(Size(323, 484), Size(image.width, image.height))
                assertNull(
                    transformeds?.getRotateTransformed()
                )
            }
        request.newRequest {
            size(500, 500)
            precision(LESS_PIXELS)
            addTransformations(RotateTransformation(90))
        }.let { sketch.execute(it) }
            .asOrNull<ImageResult.Success>()!!.apply {
                assertEquals(Size(484, 323), Size(image.width, image.height))
                assertNotNull(
                    transformeds?.getRotateTransformed()
                )
            }

        request.newRequest {
            size(500, 500)
            precision(LESS_PIXELS)
        }.let { sketch.execute(it) }
            .asOrNull<ImageResult.Success>()!!.apply {
                assertEquals(Size(323, 484), Size(image.width, image.height))
                assertNotEquals(
                    listOf(0, 0, 0, 0),
                    image.corners()
                )
                assertNull(
                    transformeds?.getCircleCropTransformed()
                )
            }
        request.newRequest {
            size(500, 500)
            precision(LESS_PIXELS)
            addTransformations(CircleCropTransformation())
        }.let { sketch.execute(it) }
            .asOrNull<ImageResult.Success>()!!.apply {
                assertEquals(Size(323, 323), Size(image.width, image.height))
                assertEquals(
                    listOf(0, 0, 0, 0),
                    image.corners()
                )
                assertNotNull(
                    transformeds?.getCircleCropTransformed()
                )
            }
    }

    @Test
    fun testResultCachePolicy() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val diskCache = sketch.resultCache
        val imageUri = ResourceImages.jpeg.uri
        val request = ImageRequest(context, imageUri) {
            memoryCachePolicy(DISABLED)
            size(500, 500)
        }
        val resultCacheKey = request.toRequestContext(sketch).resultCacheKey

        /* ENABLED */
        diskCache.clear()
        assertFalse(diskCache.exist(resultCacheKey))
        request.newRequest {
            resultCachePolicy(ENABLED)
        }.let {
            sketch.execute(it)
        }.asOrNull<ImageResult.Success>()!!.apply {
            assertEquals(DataFrom.LOCAL, dataFrom)
        }

        assertTrue(diskCache.exist(resultCacheKey))
        request.newRequest {
            resultCachePolicy(ENABLED)
        }.let {
            sketch.execute(it)
        }.asOrNull<ImageResult.Success>()!!.apply {
            assertEquals(DataFrom.RESULT_CACHE, dataFrom)
        }

        /* DISABLED */
        diskCache.clear()
        assertFalse(diskCache.exist(resultCacheKey))
        request.newRequest {
            resultCachePolicy(DISABLED)
        }.let {
            sketch.execute(it)
        }.asOrNull<ImageResult.Success>()!!.apply {
            assertEquals(DataFrom.LOCAL, dataFrom)
        }

        assertFalse(diskCache.exist(resultCacheKey))
        request.newRequest {
            resultCachePolicy(DISABLED)
        }.let {
            sketch.execute(it)
        }.asOrNull<ImageResult.Success>()!!.apply {
            assertEquals(DataFrom.LOCAL, dataFrom)
        }

        /* READ_ONLY */
        diskCache.clear()
        assertFalse(diskCache.exist(resultCacheKey))
        request.newRequest {
            resultCachePolicy(READ_ONLY)
        }.let {
            sketch.execute(it)
        }.asOrNull<ImageResult.Success>()!!.apply {
            assertEquals(DataFrom.LOCAL, dataFrom)
        }

        assertFalse(diskCache.exist(resultCacheKey))
        request.newRequest {
            resultCachePolicy(READ_ONLY)
        }.let {
            sketch.execute(it)
        }.asOrNull<ImageResult.Success>()!!.apply {
            assertEquals(DataFrom.LOCAL, dataFrom)
        }

        assertFalse(diskCache.exist(resultCacheKey))
        request.newRequest {
            resultCachePolicy(ENABLED)
        }.let {
            sketch.execute(it)
        }
        assertTrue(diskCache.exist(resultCacheKey))
        request.newRequest {
            resultCachePolicy(READ_ONLY)
        }.let {
            sketch.execute(it)
        }.asOrNull<ImageResult.Success>()!!.apply {
            assertEquals(DataFrom.RESULT_CACHE, dataFrom)
        }

        /* WRITE_ONLY */
        diskCache.clear()
        assertFalse(diskCache.exist(resultCacheKey))
        request.newRequest {
            resultCachePolicy(WRITE_ONLY)
        }.let {
            sketch.execute(it)
        }.asOrNull<ImageResult.Success>()!!.apply {
            assertEquals(DataFrom.LOCAL, dataFrom)
        }

        assertTrue(diskCache.exist(resultCacheKey))
        request.newRequest {
            resultCachePolicy(WRITE_ONLY)
        }.let {
            sketch.execute(it)
        }.asOrNull<ImageResult.Success>()!!.apply {
            assertEquals(DataFrom.LOCAL, dataFrom)
        }
    }

    @Test
    fun testPlaceholder() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val imageUri = ResourceImages.jpeg.uri
        var onStartImage: Image?
        val request = ImageRequest(context, imageUri) {
            size(500, 500)
            target(
                onStart = { _, _, placeholder: Image? ->
                    onStartImage = placeholder
                }
            )
        }
        val memoryCacheKey = request.toRequestContext(sketch).memoryCacheKey
        val memoryCache = sketch.memoryCache
        val placeholderStateImage = FakeStateImage()

        memoryCache.clear()
        onStartImage = null
        assertFalse(memoryCache.exist(memoryCacheKey))
        request.newRequest()
            .let { sketch.execute(it) }
        assertNull(onStartImage)

        onStartImage = null
        assertTrue(memoryCache.exist(memoryCacheKey))
        request.newRequest {
            placeholder(placeholderStateImage)
        }.let { sketch.execute(it) }
        assertNull(onStartImage)

        onStartImage = null
        assertTrue(memoryCache.exist(memoryCacheKey))
        request.newRequest {
            memoryCachePolicy(DISABLED)
            placeholder(placeholderStateImage)
        }.let { sketch.execute(it) }
        assertNotNull(onStartImage)
        assertEquals(placeholderStateImage.image, onStartImage)
    }

    @Test
    fun testFallback() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val fallbackStateImage = FakeStateImage()

        runBlock {
            val target = TestTarget()
            val request = ImageRequest(context, ResourceImages.jpeg.uri) {
                size(500, 500)
                target(target)
            }
            sketch.execute(request)
            assertNotNull(target.successImage)
            assertNotEquals(fallbackStateImage.image, target.successImage)
        }

        runBlock {
            val target = TestTarget()
            val request = ImageRequest(context, "") {
                size(500, 500)
                target(target)
            }
            sketch.execute(request)
            assertNull(target.successImage)
            assertNull(target.errorImage)
        }

        runBlock {
            val target = TestTarget()
            val request = ImageRequest(context, "") {
                size(500, 500)
                target(target)
                fallback(fallbackStateImage)
            }
            sketch.execute(request)
            assertNull(target.successImage)
            assertNotNull(target.errorImage)
            assertEquals(fallbackStateImage.image, target.errorImage)
        }
    }

    @Test
    fun testError() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val imageUri = ResourceImages.jpeg.uri
        var onErrorImage: Image?
        val request = ImageRequest(context, imageUri) {
            size(500, 500)
            target(
                onError = { _, _, image ->
                    onErrorImage = image
                }
            )
        }
        val errorRequest = ImageRequest(context, ResourceImages.jpeg.uri + "1") {
            size(500, 500)
            target(
                onError = { _, _, image ->
                    onErrorImage = image
                }
            )
        }
        val errorStateImage = FakeStateImage()

        onErrorImage = null
        request.newRequest()
            .let { sketch.execute(it) }
        assertNull(onErrorImage)

        onErrorImage = null
        request.newRequest {
            error(errorStateImage)
        }.let { sketch.execute(it) }
        assertNull(onErrorImage)

        onErrorImage = null
        errorRequest.newRequest()
            .let { sketch.execute(it) }
        assertNull(onErrorImage)

        onErrorImage = null
        errorRequest.newRequest {
            error(errorStateImage)
        }.let { sketch.execute(it) }
        assertEquals(errorStateImage.image, onErrorImage)

        onErrorImage = null
        errorRequest.newRequest {
            placeholder(errorStateImage)
        }.let { sketch.execute(it) }
        assertEquals(errorStateImage.image, onErrorImage)
    }

    @Test
    fun testTransition() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val imageUri = ResourceImages.jpeg.uri
        val testTarget = TestTransitionTarget()
        val request = ImageRequest(context, imageUri) {
            size(500, 500)
            target(testTarget)
        }
        val memoryCache = sketch.memoryCache
        val memoryCacheKey = request.toRequestContext(sketch).memoryCacheKey

        memoryCache.clear()
        assertFalse(memoryCache.exist(memoryCacheKey))
        request.newRequest().execute(sketch)
        block(1000)
        assertFalse(
            actual = testTarget.image!!.toString().contains("Crossfade"),
            message = testTarget.image!!.toString()
        )

        assertTrue(memoryCache.exist(memoryCacheKey))
        request.newRequest {
            transitionFactory(CrossfadeTransition.Factory())
        }.execute(sketch)
        block(1000)
        assertFalse(
            actual = testTarget.image!!.toString().contains("Crossfade"),
            message = testTarget.image!!.toString()
        )

        memoryCache.clear()
        assertFalse(memoryCache.exist(memoryCacheKey))
        request.newRequest {
            transitionFactory(CrossfadeTransition.Factory())
        }.execute(sketch)
        block(1000)
        assertTrue(
            actual = testTarget.image!!.toString().contains("Crossfade"),
            message = testTarget.image!!.toString()
        )
    }

    @Test
    fun testResizeOnDraw() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val imageUri = ResourceImages.jpeg.uri
        val request = ImageRequest(context, imageUri) {
            size(500, 500)
            target(TestResizeOnDrawTarget())
        }

        request.let { sketch.execute(it) }
            .asOrNull<ImageResult.Success>()!!.apply {
                assertTrue(this.image !is TestResizeOnDrawImage)
            }

        request.newRequest {
            resizeOnDraw(false)
        }.let { sketch.execute(it) }
            .asOrNull<ImageResult.Success>()!!.apply {
                assertTrue(this.image !is TestResizeOnDrawImage)
            }

        request.newRequest {
            resizeOnDraw(null)
        }.let { sketch.execute(it) }
            .asOrNull<ImageResult.Success>()!!.apply {
                assertTrue(this.image !is TestResizeOnDrawImage)
            }

        request.newRequest {
            resizeOnDraw(true)
        }.let { sketch.execute(it) }
            .asOrNull<ImageResult.Success>()!!.apply {
                assertTrue(image is TestResizeOnDrawImage)
            }
    }

    @Test
    fun testAllowNullImage() = runTest {
        // com.github.panpf.sketch.view.core.test.target.GenericViewTargetTest.testAllowNullImage
        // com.github.panpf.sketch.compose.core.common.test.target.GenericComposeTargetTest.testAllowNullImage
    }

    @Test
    fun testMemoryCachePolicy() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val memoryCache = sketch.memoryCache
        val imageUri = ResourceImages.jpeg.uri
        val request = ImageRequest(context, imageUri) {
            resultCachePolicy(DISABLED)
            size(500, 500)
            target(TestCountTarget())
        }
        val memoryCacheKey = request.toRequestContext(sketch).memoryCacheKey

        /* ENABLED */
        memoryCache.clear()
        assertFalse(memoryCache.exist(memoryCacheKey))
        request.newRequest {
            memoryCachePolicy(ENABLED)
        }.let {
            sketch.execute(it)
        }.asOrNull<ImageResult.Success>()!!.apply {
            assertEquals(DataFrom.LOCAL, dataFrom)
        }

        assertTrue(memoryCache.exist(memoryCacheKey))
        request.newRequest {
            memoryCachePolicy(ENABLED)
        }.let {
            sketch.execute(it)
        }.asOrNull<ImageResult.Success>()!!.apply {
            assertEquals(DataFrom.MEMORY_CACHE, dataFrom)
        }

        /* DISABLED */
        memoryCache.clear()
        assertFalse(memoryCache.exist(memoryCacheKey))
        request.newRequest {
            memoryCachePolicy(DISABLED)
        }.let {
            sketch.execute(it)
        }.asOrNull<ImageResult.Success>()!!.apply {
            assertEquals(DataFrom.LOCAL, dataFrom)
        }

        assertFalse(memoryCache.exist(memoryCacheKey))
        request.newRequest {
            memoryCachePolicy(DISABLED)
        }.let {
            sketch.execute(it)
        }.asOrNull<ImageResult.Success>()!!.apply {
            assertEquals(DataFrom.LOCAL, dataFrom)
        }

        /* READ_ONLY */
        memoryCache.clear()
        assertFalse(memoryCache.exist(memoryCacheKey))
        request.newRequest {
            memoryCachePolicy(READ_ONLY)
        }.let {
            sketch.execute(it)
        }.asOrNull<ImageResult.Success>()!!.apply {
            assertEquals(DataFrom.LOCAL, dataFrom)
        }

        assertFalse(memoryCache.exist(memoryCacheKey))
        request.newRequest {
            memoryCachePolicy(READ_ONLY)
        }.let {
            sketch.execute(it)
        }.asOrNull<ImageResult.Success>()!!.apply {
            assertEquals(DataFrom.LOCAL, dataFrom)
        }

        assertFalse(memoryCache.exist(memoryCacheKey))
        request.newRequest {
            memoryCachePolicy(ENABLED)
        }.let {
            sketch.execute(it)
        }
        assertTrue(memoryCache.exist(memoryCacheKey))
        request.newRequest {
            memoryCachePolicy(READ_ONLY)
        }.let {
            sketch.execute(it)
        }.asOrNull<ImageResult.Success>()!!.apply {
            assertEquals(DataFrom.MEMORY_CACHE, dataFrom)
        }

        /* WRITE_ONLY */
        memoryCache.clear()
        assertFalse(memoryCache.exist(memoryCacheKey))
        request.newRequest {
            memoryCachePolicy(WRITE_ONLY)
        }.let {
            sketch.execute(it)
        }.asOrNull<ImageResult.Success>()!!.apply {
            assertEquals(DataFrom.LOCAL, dataFrom)
        }

        assertTrue(memoryCache.exist(memoryCacheKey))
        request.newRequest {
            memoryCachePolicy(WRITE_ONLY)
        }.let {
            sketch.execute(it)
        }.asOrNull<ImageResult.Success>()!!.apply {
            assertEquals(DataFrom.LOCAL, dataFrom)
        }
    }

    @Test
    fun testListener() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val imageUri = ResourceImages.jpeg.uri
        val errorImageUri = ResourceImages.jpeg.uri + ".fake"

        ListenerSupervisor().let { listenerSupervisor ->
            assertEquals(listOf(), listenerSupervisor.callbackActionList)

            ImageRequest(context, imageUri) {
                registerListener(listenerSupervisor)
            }.let { request ->
                sketch.execute(request)
            }
            assertEquals(
                listOf("onStart", "onSuccess"),
                listenerSupervisor.callbackActionList
            )
        }

        ListenerSupervisor().let { listenerSupervisor ->
            assertEquals(listOf(), listenerSupervisor.callbackActionList)

            ImageRequest(context, errorImageUri) {
                registerListener(listenerSupervisor)
            }.let { request ->
                sketch.execute(request)
            }
            assertEquals(listOf("onStart", "onError"), listenerSupervisor.callbackActionList)
        }

        var deferred: Deferred<ImageResult>? = null
        val listenerSupervisor = ListenerSupervisor {
            deferred?.cancel()
        }
        ImageRequest(context, ResourceImages.jpeg.uri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            registerListener(listenerSupervisor)
        }.let { request ->
            deferred = async {
                sketch.execute(request)
            }
            deferred?.join()
        }
        assertEquals(listOf("onStart", "onCancel"), listenerSupervisor.callbackActionList)
    }

    @Test
    fun testProgressListener() = runTest {
        runInNewSketchWithUse({
            httpStack(TestHttpStack(it, 20))
        }) { context, sketch ->
            val testImage = TestHttpStack.testImages.first()

            ProgressListenerSupervisor().let { listenerSupervisor ->
                assertEquals(listOf(), listenerSupervisor.callbackActionList)

                ImageRequest(context, testImage.uri) {
                    memoryCachePolicy(DISABLED)
                    resultCachePolicy(DISABLED)
                    downloadCachePolicy(DISABLED)
                    registerProgressListener(listenerSupervisor)
                }.let { request ->
                    sketch.execute(request)
                }

                assertTrue(listenerSupervisor.callbackActionList.size > 1)
                listenerSupervisor.callbackActionList.forEachIndexed { index, _ ->
                    if (index > 0) {
                        assertTrue(listenerSupervisor.callbackActionList[index - 1].toLong() < listenerSupervisor.callbackActionList[index].toLong())
                    }
                }
                assertEquals(
                    testImage.contentLength,
                    listenerSupervisor.callbackActionList.last().toLong()
                )
            }
        }
    }

    @Test
    fun testComponents() = runTest {
        val context = getTestContext()

        ImageRequest(context, ResourceImages.jpeg.uri)
            .execute().asOrThrow<ImageResult.Success>().apply {
                assertNull(request.extras?.get("TestRequestInterceptor"))
            }
        ImageRequest(context, ResourceImages.jpeg.uri) {
            components {
                addRequestInterceptor(TestRequestInterceptor())
            }
        }.execute().asOrThrow<ImageResult.Success>().apply {
            assertEquals("true", request.extras?.get("TestRequestInterceptor"))
        }

        ImageRequest(context, ResourceImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
        }.execute().asOrThrow<ImageResult.Success>().apply {
            assertFalse(transformeds?.contains("TestDecodeInterceptor") == true)
        }
        ImageRequest(context, ResourceImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            components {
                addDecodeInterceptor(TestDecodeInterceptor())
            }
        }.execute().asOrThrow<ImageResult.Success>().apply {
            assertTrue(transformeds?.contains("TestDecodeInterceptor") == true)
        }

        ImageRequest(context, TestFetcherFactory.createUri(ResourceImages.jpeg.uri)) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
        }.execute().apply {
            assertTrue(this is ImageResult.Error)
        }
        ImageRequest(context, TestFetcherFactory.createUri(ResourceImages.jpeg.uri)) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            components {
                addFetcher(TestFetcherFactory())
            }
        }.execute().apply {
            assertTrue(this is ImageResult.Success)
        }

        ImageRequest(context, ResourceImages.jpeg.uri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
        }.execute().apply {
            assertTrue(this is ImageResult.Success)
        }
        ImageRequest(context, ResourceImages.jpeg.uri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            components {
                addDecoder(TestErrorDecoder.Factory())
            }
        }.execute().apply {
            assertTrue(this is ImageResult.Error)
        }
    }

    @Test
    fun testTarget() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        TestTarget().let { testTarget ->
            assertNull(testTarget.startImage)
            assertNull(testTarget.successImage)
            assertNull(testTarget.errorImage)
        }

        TestTarget().let { testTarget ->
            ImageRequest(context, ResourceImages.jpeg.uri) {
                target(testTarget)
            }.let { request ->
                sketch.execute(request)
            }
            assertNull(testTarget.startImage)
            assertNotNull(testTarget.successImage)
            assertNull(testTarget.errorImage)
        }

        TestTarget().let { testTarget ->
            val placeholderStateImage = FakeStateImage()
            val errorStateImage = FakeStateImage(FakeImage(SketchSize(200, 200)))
            ImageRequest(context, ResourceImages.jpeg.uri + ".fake") {
                placeholder(placeholderStateImage)
                error(errorStateImage)
                target(testTarget)
            }.let { request ->
                sketch.execute(request)
            }
            assertEquals(placeholderStateImage.image, testTarget.startImage)
            assertNull(testTarget.successImage)
            assertEquals(errorStateImage.image, testTarget.errorImage)
        }

        TestTarget().let { testTarget ->
            var deferred: Deferred<ImageResult>? = null
            val listenerSupervisor = ListenerSupervisor {
                deferred?.cancel()
            }
            ImageRequest(context, ResourceImages.jpeg.uri) {
                memoryCachePolicy(DISABLED)
                resultCachePolicy(DISABLED)
                registerListener(listenerSupervisor)
                target(testTarget)
            }.let { request ->
                deferred = async {
                    sketch.execute(request)
                }
                deferred?.join()
            }
            assertNull(testTarget.startImage)
            assertNull(testTarget.successImage)
            assertNull(testTarget.errorImage)
        }

        TestTarget().let { testTarget ->
            var deferred: Deferred<ImageResult>? = null
            val listenerSupervisor = ListenerSupervisor {
                deferred?.cancel()
            }
            ImageRequest(context, ResourceImages.jpeg.uri + ".fake") {
                memoryCachePolicy(DISABLED)
                resultCachePolicy(DISABLED)
                registerListener(listenerSupervisor)
                error(FakeStateImage())
                target(testTarget)
            }.let { request ->
                deferred = async {
                    sketch.execute(request)
                }
                deferred?.join()
            }
            assertNull(testTarget.startImage)
            assertNull(testTarget.successImage)
            assertNull(testTarget.errorImage)
        }
    }

    @Test
    fun testLifecycle() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val lifecycle = TestLifecycle()
        withContext(Dispatchers.Main) {
            lifecycle.currentState = CREATED
        }

        ImageRequest(context, ResourceImages.jpeg.uri).let { request ->
            assertEquals(
                LifecycleResolver(GlobalLifecycle),
                request.lifecycleResolver
            )
            sketch.execute(request)
        }.apply {
            assertTrue(this is ImageResult.Success)
        }

        ImageRequest(context, ResourceImages.jpeg.uri) {
            lifecycle(lifecycle)
        }.let { request ->
            assertEquals(
                LifecycleResolver(lifecycle),
                request.lifecycleResolver
            )
            val deferred = async {
                sketch.execute(request)
            }
            block(2000)
            if (!deferred.isCompleted) {
                withContext(Dispatchers.Main) {
                    lifecycle.currentState = STARTED
                }
            }
            block(2000)
            deferred.await()
        }.apply {
            assertTrue(this is ImageResult.Success)
        }
    }
}