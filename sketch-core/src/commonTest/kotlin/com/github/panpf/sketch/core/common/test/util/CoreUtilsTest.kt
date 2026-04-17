package com.github.panpf.sketch.core.common.test.util

import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.request.Depth
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.Listener
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.test.utils.FakeImage
import com.github.panpf.sketch.test.utils.FakeStateImage
import com.github.panpf.sketch.test.utils.TestColor
import com.github.panpf.sketch.test.utils.TestDecoder
import com.github.panpf.sketch.test.utils.TestFetcher
import com.github.panpf.sketch.test.utils.TestLifecycle
import com.github.panpf.sketch.test.utils.TestTarget
import com.github.panpf.sketch.test.utils.TestTransition
import com.github.panpf.sketch.test.utils.TestTransitionTarget
import com.github.panpf.sketch.test.utils.block
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.pow
import com.github.panpf.sketch.transform.CircleCropTransformation
import com.github.panpf.sketch.transform.MaskTransformation
import com.github.panpf.sketch.transition.CrossfadeTransition
import com.github.panpf.sketch.util.Rect
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.asOrNull
import com.github.panpf.sketch.util.asOrThrow
import com.github.panpf.sketch.util.calculateCropBounds
import com.github.panpf.sketch.util.calculateInsideBounds
import com.github.panpf.sketch.util.calculateScaleMultiplierWithCrop
import com.github.panpf.sketch.util.calculateScaleMultiplierWithFit
import com.github.panpf.sketch.util.calculateScaleMultiplierWithInside
import com.github.panpf.sketch.util.calculateScaleMultiplierWithOneSide
import com.github.panpf.sketch.util.ceilRoundPow2
import com.github.panpf.sketch.util.compareVersions
import com.github.panpf.sketch.util.difference
import com.github.panpf.sketch.util.floorRoundPow2
import com.github.panpf.sketch.util.format
import com.github.panpf.sketch.util.formatFileSize
import com.github.panpf.sketch.util.getCompletedOrNull
import com.github.panpf.sketch.util.ifApply
import com.github.panpf.sketch.util.ifLet
import com.github.panpf.sketch.util.ifOrNull
import com.github.panpf.sketch.util.intMerged
import com.github.panpf.sketch.util.intSplit
import com.github.panpf.sketch.util.ioCoroutineDispatcher
import com.github.panpf.sketch.util.isThumbnailWithSize
import com.github.panpf.sketch.util.md5
import com.github.panpf.sketch.util.plus
import com.github.panpf.sketch.util.toHexString
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runTest
import okio.IOException
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CoreUtilsTest {

    @Test
    fun testIfOrNull() {
        assertEquals("yes", ifOrNull(true) { "yes" })
        assertEquals(null, ifOrNull(false) { "yes" })
    }

    @Test
    fun testIfApply() {
        var result: String? = null

        1.ifApply(false) {
            result = this@ifApply.toString()
        }
        assertEquals(null, result)

        1.ifApply(true) {
            result = this@ifApply.toString()
        }
        assertEquals("1", result)
    }

    @Test
    fun testIfLet() {
        assertEquals(1, 1.ifLet(false) { it + 1 })
        assertEquals(2, 1.ifLet(true) { it + 1 })
    }

    @Test
    fun testAsOrNull() {
        assertNotNull(IOException().asOrNull<Exception>())
        assertNull((null as Exception?).asOrNull<Exception>())
        assertFailsWith(ClassCastException::class) {
            Throwable() as Exception
        }
        assertNull(Throwable().asOrNull<Exception>())
    }

    @Test
    fun testAsOrThrow() {
        assertNotNull(IOException().asOrThrow<Exception>())
        assertFailsWith(ClassCastException::class) {
            Throwable() as Exception
        }
    }

    @Test
    fun testGetCompletedOrNull() = runTest {
        val deferred = async(ioCoroutineDispatcher()) {
            block(1000)
            "yes"
        }

        assertFailsWith(IllegalStateException::class) {
            @Suppress("OPT_IN_USAGE")
            deferred.getCompleted()
        }
        assertEquals(null, deferred.getCompletedOrNull())

        block(1500)
        assertEquals("yes", deferred.getCompletedOrNull())
    }

    @Test
    fun testMd5() {
        assertEquals("7ac66c0f148de9519b8bd264312c4d64", "abcdefg".md5())
    }

    @Test
    fun testToHexString() {
        val any = Any()
        assertEquals(
            expected = any.hashCode().toString(16),
            actual = any.toHexString()
        )
    }

    @Test
    fun testFloatFormat() {
        listOf(
            FormatItem(number = 6.2517f, newScale = 3, expected = 6.252f),
            FormatItem(number = 6.2517f, newScale = 2, expected = 6.25f),
            FormatItem(number = 6.2517f, newScale = 1, expected = 6.3f),
            FormatItem(number = 6.251f, newScale = 2, expected = 6.25f),
            FormatItem(number = 6.251f, newScale = 1, expected = 6.3f),

            FormatItem(number = 0.6253f, newScale = 3, expected = 0.625f),
            FormatItem(number = 0.6253f, newScale = 2, expected = 0.63f),
            FormatItem(number = 0.6253f, newScale = 1, expected = 0.6f),
            FormatItem(number = 0.625f, newScale = 2, expected = 0.62f),
            FormatItem(number = 0.625f, newScale = 1, expected = 0.6f),
        ).forEach {
            assertEquals(
                expected = it.expected,
                actual = it.number.format(it.newScale),
                absoluteTolerance = 0f,
                message = "format. number=${it.number}, newScale=${it.newScale}"
            )
        }
    }

    @Test
    fun testDoubleFormat() {
        listOf(
            FormatItem(number = 6.2517, newScale = 3, expected = 6.252),
            FormatItem(number = 6.2517, newScale = 2, expected = 6.25),
            FormatItem(number = 6.2517, newScale = 1, expected = 6.3),
            FormatItem(number = 6.251, newScale = 2, expected = 6.25),
            FormatItem(number = 6.251, newScale = 1, expected = 6.3),

            FormatItem(number = 0.6253, newScale = 3, expected = 0.625),
            FormatItem(number = 0.6253, newScale = 2, expected = 0.63),
            FormatItem(number = 0.6253, newScale = 1, expected = 0.6),
            FormatItem(number = 0.625, newScale = 2, expected = 0.62),
            FormatItem(number = 0.625, newScale = 1, expected = 0.6),
        ).forEach {
            assertEquals(
                expected = it.expected,
                actual = it.number.format(it.newScale),
                absoluteTolerance = 0.0,
                message = "format. number=${it.number}, newScale=${it.newScale}"
            )
        }
    }

    @Test
    fun testFormatFileSize() {
        assertEquals("0B", (0L - 1).formatFileSize())
        assertEquals("0B", 0L.formatFileSize(2))
        assertEquals("999B", 999L.formatFileSize())
        assertEquals("0.98KB", (999L + 1).formatFileSize(2))

        assertEquals("1KB", 1024L.pow(1).formatFileSize())
        assertEquals("999KB", (1024L.pow(1) * (1000 - 1)).formatFileSize(2))
        assertEquals("0.98MB", (1024L.pow(1) * 1000).formatFileSize(2))

        assertEquals("1MB", 1024L.pow(2).formatFileSize())
        assertEquals("999MB", (1024L.pow(2) * (1000 - 1)).formatFileSize(2))
        assertEquals("0.98GB", (1024L.pow(2) * 1000).formatFileSize(2))

        assertEquals("1GB", 1024L.pow(3).formatFileSize())
        assertEquals("999GB", (1024L.pow(3) * (1000 - 1)).formatFileSize(2))
        assertEquals("0.98TB", (1024L.pow(3) * 1000).formatFileSize(2))

        assertEquals("1TB", 1024L.pow(4).formatFileSize())
        assertEquals("999TB", (1024L.pow(4) * (1000 - 1)).formatFileSize(2))
        assertEquals("0.98PB", (1024L.pow(4) * 1000).formatFileSize(2))

        assertEquals("1PB", 1024L.pow(5).formatFileSize())
        assertEquals("999PB", (1024L.pow(5) * (1000 - 1)).formatFileSize(2))
        assertEquals("1000PB", (1024L.pow(5) * 1000).formatFileSize(2))

        assertEquals("1024PB", 1024L.pow(6).formatFileSize())
    }

    @Test
    fun testIntMergedAndIntSplit() {
        intSplit(intMerged(39, 25)).apply {
            assertEquals(39, first)
            assertEquals(25, second)
        }
        intSplit(intMerged(7, 43)).apply {
            assertEquals(7, first)
            assertEquals(43, second)
        }

        assertFailsWith(IllegalArgumentException::class) {
            intMerged(-1, 25)
        }
        assertFailsWith(IllegalArgumentException::class) {
            intMerged(Short.MAX_VALUE + 1, 25)
        }
        assertFailsWith(IllegalArgumentException::class) {
            intMerged(25, -1)
        }
        assertFailsWith(IllegalArgumentException::class) {
            intMerged(25, Short.MAX_VALUE + 1)
        }
    }

    @Test
    fun testFloorRoundPow2() {
        assertEquals(1, floorRoundPow2(-1))
        assertEquals(1, floorRoundPow2(0))
        assertEquals(1, floorRoundPow2(1))
        assertEquals(2, floorRoundPow2(2))
        assertEquals(2, floorRoundPow2(3))
        assertEquals(4, floorRoundPow2(4))
        assertEquals(4, floorRoundPow2(5))
        assertEquals(4, floorRoundPow2(6))
        assertEquals(4, floorRoundPow2(7))
        assertEquals(8, floorRoundPow2(8))
        assertEquals(8, floorRoundPow2(9))
        assertEquals(8, floorRoundPow2(10))
        assertEquals(8, floorRoundPow2(11))
        assertEquals(8, floorRoundPow2(12))
        assertEquals(8, floorRoundPow2(13))
        assertEquals(8, floorRoundPow2(14))
        assertEquals(8, floorRoundPow2(15))
        assertEquals(16, floorRoundPow2(16))
        assertEquals(16, floorRoundPow2(17))
    }

    @Test
    fun testCeilRoundPow2() {
        assertEquals(1, ceilRoundPow2(-1))
        assertEquals(1, ceilRoundPow2(0))
        assertEquals(1, ceilRoundPow2(1))
        assertEquals(2, ceilRoundPow2(2))
        assertEquals(4, ceilRoundPow2(3))
        assertEquals(4, ceilRoundPow2(4))
        assertEquals(8, ceilRoundPow2(5))
        assertEquals(8, ceilRoundPow2(6))
        assertEquals(8, ceilRoundPow2(7))
        assertEquals(8, ceilRoundPow2(8))
        assertEquals(16, ceilRoundPow2(9))
        assertEquals(16, ceilRoundPow2(10))
        assertEquals(16, ceilRoundPow2(11))
        assertEquals(16, ceilRoundPow2(12))
        assertEquals(16, ceilRoundPow2(13))
        assertEquals(16, ceilRoundPow2(14))
        assertEquals(16, ceilRoundPow2(15))
        assertEquals(16, ceilRoundPow2(16))
        assertEquals(32, ceilRoundPow2(17))
    }

    @Test
    fun testCalculateScaleMultiplierWithFit() {
        assertEquals(0.2f, calculateScaleMultiplierWithFit(1000f, 600f, 200f, 400f, true), 0f)
        assertEquals(
            0.666f,
            calculateScaleMultiplierWithFit(1000f, 600f, 200f, 400f, false),
            0.001f
        )
        assertEquals(0.333f, calculateScaleMultiplierWithFit(1000f, 600f, 400f, 200f, true), 0.001f)
        assertEquals(0.4f, calculateScaleMultiplierWithFit(1000f, 600f, 400f, 200f, false), 0f)

        assertEquals(
            0.666f,
            calculateScaleMultiplierWithFit(1000f, 600f, 2000f, 400f, true),
            0.001f
        )
        assertEquals(2.0f, calculateScaleMultiplierWithFit(1000f, 600f, 2000f, 400f, false), 0f)
        assertEquals(0.4f, calculateScaleMultiplierWithFit(1000f, 600f, 400f, 2000f, true), 0f)
        assertEquals(
            3.333f,
            calculateScaleMultiplierWithFit(1000f, 600f, 400f, 2000f, false),
            0.001f
        )

        assertEquals(2.0f, calculateScaleMultiplierWithFit(1000f, 600f, 2000f, 4000f, true), 0f)
        assertEquals(
            6.666f,
            calculateScaleMultiplierWithFit(1000f, 600f, 2000f, 4000f, false),
            0.001f
        )
        assertEquals(
            3.333f,
            calculateScaleMultiplierWithFit(1000f, 600f, 4000f, 2000f, true),
            0.001f
        )
        assertEquals(4.0f, calculateScaleMultiplierWithFit(1000f, 600f, 4000f, 2000f, false), 0f)
    }

    @Test
    fun testCalculateScaleMultiplierWithInside() {
        assertEquals(0.2f, calculateScaleMultiplierWithInside(1000f, 600f, 200f, 400f), 0f)
        assertEquals(0.333f, calculateScaleMultiplierWithInside(1000f, 600f, 400f, 200f), 0.001f)

        assertEquals(0.666f, calculateScaleMultiplierWithInside(1000f, 600f, 2000f, 400f), 0.001f)
        assertEquals(0.4f, calculateScaleMultiplierWithInside(1000f, 600f, 400f, 2000f), 0f)

        assertEquals(1f, calculateScaleMultiplierWithInside(1000f, 600f, 2000f, 4000f), 0f)
        assertEquals(1f, calculateScaleMultiplierWithInside(1000f, 600f, 4000f, 2000f), 0f)
    }

    @Test
    fun testCalculateScaleMultiplierWithCrop() {
        assertEquals(0.666f, calculateScaleMultiplierWithCrop(1000f, 600f, 200f, 400f), 0.001f)
        assertEquals(0.4f, calculateScaleMultiplierWithCrop(1000f, 600f, 400f, 200f), 0f)

        assertEquals(2.0f, calculateScaleMultiplierWithCrop(1000f, 600f, 2000f, 400f), 0f)
        assertEquals(3.333f, calculateScaleMultiplierWithCrop(1000f, 600f, 400f, 2000f), 0.001f)

        assertEquals(6.666f, calculateScaleMultiplierWithCrop(1000f, 600f, 2000f, 4000f), 0.001f)
        assertEquals(4.0f, calculateScaleMultiplierWithCrop(1000f, 600f, 4000f, 2000f), 0f)
    }

    @Test
    fun testCalculateScaleMultiplierWithOneSide() {
        assertEquals(
            expected = 1.5f,
            actual = calculateScaleMultiplierWithOneSide(
                sourceSize = Size(100, 100),
                targetSize = Size(150, 200)
            )
        )
        assertEquals(
            expected = 0.2f,
            actual = calculateScaleMultiplierWithOneSide(
                sourceSize = Size(100, 100),
                targetSize = Size(50, 20)
            )
        )
        assertEquals(
            expected = 2.0f,
            actual = calculateScaleMultiplierWithOneSide(
                sourceSize = Size(100, 100),
                targetSize = Size(0, 200)
            )
        )
        assertEquals(
            expected = 0.5f,
            actual = calculateScaleMultiplierWithOneSide(
                sourceSize = Size(100, 100),
                targetSize = Size(50, 0)
            )
        )
        assertEquals(
            expected = 1.0f,
            actual = calculateScaleMultiplierWithOneSide(
                sourceSize = Size(100, 100),
                targetSize = Size(0, 0)
            )
        )
    }

    @Test
    fun testCalculateInsideBounds() {
        assertEquals(
            expected = Rect(450, 450, 550, 550),
            actual = calculateInsideBounds(
                contentSize = Size(100, 100),
                containerBounds = Rect(0, 0, 1000, 1000)
            )
        )
        assertEquals(
            expected = Rect(475, 0, 525, 1000),
            actual = calculateInsideBounds(
                contentSize = Size(100, 2000),
                containerBounds = Rect(0, 0, 1000, 1000)
            )
        )
        assertEquals(
            expected = Rect(0, 475, 1000, 525),
            actual = calculateInsideBounds(
                contentSize = Size(2000, 100),
                containerBounds = Rect(0, 0, 1000, 1000)
            )
        )
        assertEquals(
            expected = Rect(0, 0, 1000, 1000),
            actual = calculateInsideBounds(
                contentSize = Size(2000, 2000),
                containerBounds = Rect(0, 0, 1000, 1000)
            )
        )
    }

    @Test
    fun testCalculateCropBounds() {
        assertEquals(
            expected = Rect(-500, 0, 1500, 1000),
            actual = calculateCropBounds(
                contentSize = Size(100, 50),
                containerBounds = Rect(0, 0, 1000, 1000)
            )
        )
        assertEquals(
            expected = Rect(0, -500, 1000, 1500),
            actual = calculateCropBounds(
                contentSize = Size(50, 100),
                containerBounds = Rect(0, 0, 1000, 1000)
            )
        )
        assertEquals(
            expected = Rect(0, -9500, 1000, 10500),
            actual = calculateCropBounds(
                contentSize = Size(100, 2000),
                containerBounds = Rect(0, 0, 1000, 1000)
            )
        )
        assertEquals(
            expected = Rect(-9500, 0, 10500, 1000),
            actual = calculateCropBounds(
                contentSize = Size(2000, 100),
                containerBounds = Rect(0, 0, 1000, 1000)
            )
        )
        assertEquals(
            expected = Rect(0, 0, 1000, 1000),
            actual = calculateCropBounds(
                contentSize = Size(2000, 2000),
                containerBounds = Rect(0, 0, 1000, 1000)
            )
        )
    }

    @Test
    fun testImageRequestDifference() {
        val context = getTestContext()
        val request = ImageRequest(context, ComposeResImageFiles.jpeg.uri) {
            addListener(object : Listener {})
            addProgressListener { _, _ -> }
            target(TestTarget())
            lifecycle(TestLifecycle())
            depth(depth = Depth.LOCAL, from = "test")
            setExtra("extra1", "extra1Value")
            downloadCachePolicy(CachePolicy.ENABLED)
            colorType("RGB_565")
            colorSpace("sRGB")
            size(Size.Origin)
            sizeMultiplier(1f)
            precision(Precision.LESS_PIXELS)
            scale(Scale.CENTER_CROP)
            transformations(CircleCropTransformation())
            resultCachePolicy(CachePolicy.ENABLED)
            placeholder(FakeStateImage(FakeImage(Size(100, 100))))
            fallback(FakeStateImage(FakeImage(Size(200, 200))))
            error(FakeStateImage(FakeImage(Size(300, 300))))
            transitionFactory(CrossfadeTransition.Factory())
            resizeOnDraw(true)
            memoryCachePolicy(CachePolicy.ENABLED)
            components {
                add(TestDecoder.Factory())
            }
            defaultOptions(ImageOptions {
                size(Size(99, 99))
            })
        }
        assertEquals(
            expected = "Both are null",
            actual = (null as ImageRequest?).difference(null as ImageRequest?)
        )
        assertEquals(
            expected = "This is null",
            actual = (null as ImageRequest?).difference(request)
        )
        assertEquals(
            expected = "Other is null",
            actual = request.difference(null as ImageRequest?)
        )
        assertEquals(
            expected = "Same instance",
            actual = request.difference(request)
        )
        assertEquals(
            expected = "Same content",
            actual = request.difference(request.newRequest())
        )

//        val context2 = getTestContext()
//        ImageRequest(context2, ComposeResImageFiles.jpeg.uri).apply {
//            assertEquals(
//                expected = "context different: '${request.context}' vs '${this@apply.context}'",
//                actual = request.difference(this@apply)
//            )
//        }
        ImageRequest(context, ComposeResImageFiles.png.uri).apply {
            assertEquals(
                expected = "uri different: '${request.uri}' vs '${this@apply.uri}'",
                actual = request.difference(this@apply)
            )
        }
        request.newRequest {
            addListener(object : Listener {})
        }.apply {
            assertEquals(
                expected = "listener different: '${request.listener}' vs '${this@apply.listener}'",
                actual = request.difference(this@apply)
            )
        }
        request.newRequest {
            addProgressListener { _, _ -> }
        }.apply {
            assertEquals(
                expected = "progressListener different: '${request.progressListener}' vs '${this@apply.progressListener}'",
                actual = request.difference(this@apply)
            )
        }
        request.newRequest {
            target(TestTransitionTarget())
        }.apply {
            assertEquals(
                expected = "target different: '${request.target}' vs '${this@apply.target}'",
                actual = request.difference(this@apply)
            )
        }
        request.newRequest {
            lifecycle(TestLifecycle())
        }.apply {
            assertEquals(
                expected = "lifecycleResolver different: '${request.lifecycleResolver}' vs '${this@apply.lifecycleResolver}'",
                actual = request.difference(this@apply)
            )
        }
        request.newRequest {
            depth(depth = Depth.MEMORY, from = "test")
        }.apply {
            assertEquals(
                expected = "depth different: '${request.depthHolder}' vs '${this@apply.depthHolder}'",
                actual = request.difference(this@apply)
            )
        }
        request.newRequest {
            setExtra("extra2", "extra2Value")
        }.apply {
            assertEquals(
                expected = "extras different: '${request.extras}' vs '${this@apply.extras}'",
                actual = request.difference(this@apply)
            )
        }
        request.newRequest {
            downloadCachePolicy(CachePolicy.READ_ONLY)
        }.apply {
            assertEquals(
                expected = "downloadCachePolicy different: '${request.downloadCachePolicy}' vs '${this@apply.downloadCachePolicy}'",
                actual = request.difference(this@apply)
            )
        }
        request.newRequest {
            colorType("ARGB_8888")
        }.apply {
            assertEquals(
                expected = "colorType different: '${request.colorType}' vs '${this@apply.colorType}'",
                actual = request.difference(this@apply)
            )
        }
        request.newRequest {
            colorSpace("DISPLAY_V3")
        }.apply {
            assertEquals(
                expected = "colorSpace different: '${request.colorSpace}' vs '${this@apply.colorSpace}'",
                actual = request.difference(this@apply)
            )
        }
        request.newRequest {
            size(100, 100)
        }.apply {
            assertEquals(
                expected = "sizeResolver different: '${request.sizeResolver}' vs '${this@apply.sizeResolver}'",
                actual = request.difference(this@apply)
            )
        }
        request.newRequest {
            sizeMultiplier(1.5f)
        }.apply {
            assertEquals(
                expected = "sizeMultiplier different: '${request.sizeMultiplier}' vs '${this@apply.sizeMultiplier}'",
                actual = request.difference(this@apply)
            )
        }
        request.newRequest {
            precision(Precision.EXACTLY)
        }.apply {
            assertEquals(
                expected = "precisionDecider different: '${request.precisionDecider}' vs '${this@apply.precisionDecider}'",
                actual = request.difference(this@apply)
            )
        }
        request.newRequest {
            scale(Scale.START_CROP)
        }.apply {
            assertEquals(
                expected = "scaleDecider different: '${request.scaleDecider}' vs '${this@apply.scaleDecider}'",
                actual = request.difference(this@apply)
            )
        }
        request.newRequest {
            transformations(MaskTransformation(TestColor.RED))
        }.apply {
            assertEquals(
                expected = "transformations different: '${request.transformations}' vs '${this@apply.transformations}'",
                actual = request.difference(this@apply)
            )
        }
        request.newRequest {
            resultCachePolicy(CachePolicy.READ_ONLY)
        }.apply {
            assertEquals(
                expected = "resultCachePolicy different: '${request.resultCachePolicy}' vs '${this@apply.resultCachePolicy}'",
                actual = request.difference(this@apply)
            )
        }
        request.newRequest {
            placeholder(FakeStateImage(FakeImage(Size(101, 101))))
        }.apply {
            assertEquals(
                expected = "placeholder different: '${request.placeholder}' vs '${this@apply.placeholder}'",
                actual = request.difference(this@apply)
            )
        }
        request.newRequest {
            fallback(FakeStateImage(FakeImage(Size(201, 201))))
        }.apply {
            assertEquals(
                expected = "fallback different: '${request.fallback}' vs '${this@apply.fallback}'",
                actual = request.difference(this@apply)
            )
        }
        request.newRequest {
            error(FakeStateImage(FakeImage(Size(301, 301))))
        }.apply {
            assertEquals(
                expected = "error different: '${request.error}' vs '${this@apply.error}'",
                actual = request.difference(this@apply)
            )
        }
        request.newRequest {
            transitionFactory(TestTransition.Factory())
        }.apply {
            assertEquals(
                expected = "transitionFactory different: '${request.transitionFactory}' vs '${this@apply.transitionFactory}'",
                actual = request.difference(this@apply)
            )
        }
        request.newRequest {
            resizeOnDraw(false)
        }.apply {
            assertEquals(
                expected = "resizeOnDraw different: '${request.resizeOnDraw}' vs '${this@apply.resizeOnDraw}'",
                actual = request.difference(this@apply)
            )
        }
        request.newRequest {
            memoryCachePolicy(CachePolicy.READ_ONLY)
        }.apply {
            assertEquals(
                expected = "memoryCachePolicy different: '${request.memoryCachePolicy}' vs '${this@apply.memoryCachePolicy}'",
                actual = request.difference(this@apply)
            )
        }
        request.newRequest {
            components {
                add(TestDecoder.Factory())
                add(TestFetcher.Factory())
            }
        }.apply {
            assertEquals(
                expected = "componentRegistry different: '${request.componentRegistry}' vs '${this@apply.componentRegistry}'",
                actual = request.difference(this@apply)
            )
        }
        request.newRequest {
            defaultOptions(ImageOptions {
                size(Size(88, 88))
            })
        }.apply {
            assertEquals(
                expected = "defaultOptions different: '${request.defaultOptions.difference(this@apply.defaultOptions)}'",
                actual = request.difference(this@apply)
            )
        }
    }

    @Test
    fun testImageOptionsDifference() {
        val options = ImageOptions {
            depth(depth = Depth.LOCAL, from = "test")
            setExtra("extra1", "extra1Value")
            downloadCachePolicy(CachePolicy.ENABLED)
            colorType("RGB_565")
            colorSpace("sRGB")
            size(Size.Origin)
            sizeMultiplier(1f)
            precision(Precision.LESS_PIXELS)
            scale(Scale.CENTER_CROP)
            transformations(CircleCropTransformation())
            resultCachePolicy(CachePolicy.ENABLED)
            placeholder(FakeStateImage(FakeImage(Size(100, 100))))
            fallback(FakeStateImage(FakeImage(Size(200, 200))))
            error(FakeStateImage(FakeImage(Size(300, 300))))
            transitionFactory(CrossfadeTransition.Factory())
            resizeOnDraw(true)
            memoryCachePolicy(CachePolicy.ENABLED)
            components {
                add(TestDecoder.Factory())
            }
        }
        assertEquals(
            expected = "Both are null",
            actual = (null as ImageOptions?).difference(null as ImageOptions?)
        )
        assertEquals(
            expected = "This is null",
            actual = (null as ImageOptions?).difference(options)
        )
        assertEquals(
            expected = "Other is null",
            actual = options.difference(null as ImageOptions?)
        )
        assertEquals(
            expected = "Same instance",
            actual = options.difference(options)
        )
        assertEquals(
            expected = "Same content",
            actual = options.difference(options.newOptions())
        )

        options.newOptions {
            depth(depth = Depth.MEMORY, from = "test")
        }.apply {
            assertEquals(
                expected = "depth different: '${options.depthHolder}' vs '${this@apply.depthHolder}'",
                actual = options.difference(this@apply)
            )
        }
        options.newOptions {
            setExtra("extra2", "extra2Value")
        }.apply {
            assertEquals(
                expected = "extras different: '${options.extras}' vs '${this@apply.extras}'",
                actual = options.difference(this@apply)
            )
        }
        options.newOptions {
            downloadCachePolicy(CachePolicy.READ_ONLY)
        }.apply {
            assertEquals(
                expected = "downloadCachePolicy different: '${options.downloadCachePolicy}' vs '${this@apply.downloadCachePolicy}'",
                actual = options.difference(this@apply)
            )
        }
        options.newOptions {
            colorType("ARGB_8888")
        }.apply {
            assertEquals(
                expected = "colorType different: '${options.colorType}' vs '${this@apply.colorType}'",
                actual = options.difference(this@apply)
            )
        }
        options.newOptions {
            colorSpace("DISPLAY_V3")
        }.apply {
            assertEquals(
                expected = "colorSpace different: '${options.colorSpace}' vs '${this@apply.colorSpace}'",
                actual = options.difference(this@apply)
            )
        }
        options.newOptions {
            size(100, 100)
        }.apply {
            assertEquals(
                expected = "sizeResolver different: '${options.sizeResolver}' vs '${this@apply.sizeResolver}'",
                actual = options.difference(this@apply)
            )
        }
        options.newOptions {
            sizeMultiplier(1.5f)
        }.apply {
            assertEquals(
                expected = "sizeMultiplier different: '${options.sizeMultiplier}' vs '${this@apply.sizeMultiplier}'",
                actual = options.difference(this@apply)
            )
        }
        options.newOptions {
            precision(Precision.EXACTLY)
        }.apply {
            assertEquals(
                expected = "precisionDecider different: '${options.precisionDecider}' vs '${this@apply.precisionDecider}'",
                actual = options.difference(this@apply)
            )
        }
        options.newOptions {
            scale(Scale.START_CROP)
        }.apply {
            assertEquals(
                expected = "scaleDecider different: '${options.scaleDecider}' vs '${this@apply.scaleDecider}'",
                actual = options.difference(this@apply)
            )
        }
        options.newOptions {
            transformations(MaskTransformation(TestColor.RED))
        }.apply {
            assertEquals(
                expected = "transformations different: '${options.transformations}' vs '${this@apply.transformations}'",
                actual = options.difference(this@apply)
            )
        }
        options.newOptions {
            resultCachePolicy(CachePolicy.READ_ONLY)
        }.apply {
            assertEquals(
                expected = "resultCachePolicy different: '${options.resultCachePolicy}' vs '${this@apply.resultCachePolicy}'",
                actual = options.difference(this@apply)
            )
        }
        options.newOptions {
            placeholder(FakeStateImage(FakeImage(Size(101, 101))))
        }.apply {
            assertEquals(
                expected = "placeholder different: '${options.placeholder}' vs '${this@apply.placeholder}'",
                actual = options.difference(this@apply)
            )
        }
        options.newOptions {
            fallback(FakeStateImage(FakeImage(Size(201, 201))))
        }.apply {
            assertEquals(
                expected = "fallback different: '${options.fallback}' vs '${this@apply.fallback}'",
                actual = options.difference(this@apply)
            )
        }
        options.newOptions {
            error(FakeStateImage(FakeImage(Size(301, 301))))
        }.apply {
            assertEquals(
                expected = "error different: '${options.error}' vs '${this@apply.error}'",
                actual = options.difference(this@apply)
            )
        }
        options.newOptions {
            transitionFactory(TestTransition.Factory())
        }.apply {
            assertEquals(
                expected = "transitionFactory different: '${options.transitionFactory}' vs '${this@apply.transitionFactory}'",
                actual = options.difference(this@apply)
            )
        }
        options.newOptions {
            resizeOnDraw(false)
        }.apply {
            assertEquals(
                expected = "resizeOnDraw different: '${options.resizeOnDraw}' vs '${this@apply.resizeOnDraw}'",
                actual = options.difference(this@apply)
            )
        }
        options.newOptions {
            memoryCachePolicy(CachePolicy.READ_ONLY)
        }.apply {
            assertEquals(
                expected = "memoryCachePolicy different: '${options.memoryCachePolicy}' vs '${this@apply.memoryCachePolicy}'",
                actual = options.difference(this@apply)
            )
        }
        options.newOptions {
            components {
                add(TestDecoder.Factory())
                add(TestFetcher.Factory())
            }
        }.apply {
            assertEquals(
                expected = "componentRegistry different: '${options.componentRegistry}' vs '${this@apply.componentRegistry}'",
                actual = options.difference(this@apply)
            )
        }
    }

    @Test
    fun testCompareVersions() {
        assertEquals(-1, compareVersions("0.8", "0.8.1"))
        assertEquals(1, compareVersions("0.8.1", "0.8"))
        assertEquals(-1, compareVersions("0.8.10", "0.8.10.1"))
        assertEquals(1, compareVersions("0.8.10.1", "0.8.10"))
        assertEquals(-1, compareVersions("0.8.15", "0.8.16"))
        assertEquals(1, compareVersions("0.8.16", "0.8.15"))
        assertEquals(-1, compareVersions("0.7.99", "0.8.0"))
        assertEquals(1, compareVersions("0.8.0", "0.7.99"))
        assertEquals(-1, compareVersions("0.6.99", "0.7.99"))
        assertEquals(1, compareVersions("0.7.99", "0.6.99"))

        assertEquals(0, compareVersions("1.0.0", "1.0.0"))
        assertEquals(0, compareVersions("0.8.1", "0.8.1"))

        assertEquals(-1, compareVersions("0.8.0", "0.8.1-SNAPSHOT01"))
        assertEquals(1, compareVersions("0.8.1-SNAPSHOT01", "0.8.0"))
        assertEquals(-1, compareVersions("0.8.1-SNAPSHOT01", "0.8.1"))
        assertEquals(1, compareVersions("0.8.1", "0.8.1-SNAPSHOT01"))
        assertEquals(-1, compareVersions("0.8.1-SNAPSHOT01", "0.8.2"))
        assertEquals(1, compareVersions("0.8.2", "0.8.1-SNAPSHOT01"))
        assertEquals(0, compareVersions("0.8.1-SNAPSHOT01", "0.8.1-SNAPSHOT1"))
        assertEquals(0, compareVersions("0.8.1-SNAPSHOT09", "0.8.1-SNAPSHOT9"))
        assertEquals(-1, compareVersions("0.8.1-SNAPSHOT1", "0.8.1-SNAPSHOT2"))
        assertEquals(-1, compareVersions("0.8.1-SNAPSHOT01", "0.8.1-SNAPSHOT2"))
        assertEquals(-1, compareVersions("0.8.1-SNAPSHOT01", "0.8.1-SNAPSHOT02"))
        assertEquals(1, compareVersions("0.8.1-SNAPSHOT2", "0.8.1-SNAPSHOT1"))
        assertEquals(1, compareVersions("0.8.1-SNAPSHOT2", "0.8.1-SNAPSHOT01"))
        assertEquals(1, compareVersions("0.8.1-SNAPSHOT02", "0.8.1-SNAPSHOT01"))

        assertEquals(-1, compareVersions("0.8.0", "0.8.1-alpha01"))
        assertEquals(1, compareVersions("0.8.1-alpha01", "0.8.0"))
        assertEquals(-1, compareVersions("0.8.1-alpha01", "0.8.1"))
        assertEquals(1, compareVersions("0.8.1", "0.8.1-alpha01"))
        assertEquals(-1, compareVersions("0.8.1-alpha01", "0.8.2"))
        assertEquals(1, compareVersions("0.8.2", "0.8.1-alpha01"))
        assertEquals(0, compareVersions("0.8.1-alpha01", "0.8.1-alpha1"))
        assertEquals(0, compareVersions("0.8.1-alpha09", "0.8.1-alpha9"))
        assertEquals(-1, compareVersions("0.8.1-alpha1", "0.8.1-alpha2"))
        assertEquals(-1, compareVersions("0.8.1-alpha01", "0.8.1-alpha2"))
        assertEquals(-1, compareVersions("0.8.1-alpha01", "0.8.1-alpha02"))
        assertEquals(1, compareVersions("0.8.1-alpha2", "0.8.1-alpha1"))
        assertEquals(1, compareVersions("0.8.1-alpha2", "0.8.1-alpha01"))
        assertEquals(1, compareVersions("0.8.1-alpha02", "0.8.1-alpha01"))

        assertEquals(-1, compareVersions("0.8.0", "0.8.1-beta01"))
        assertEquals(1, compareVersions("0.8.1-beta01", "0.8.0"))
        assertEquals(-1, compareVersions("0.8.1-beta01", "0.8.1"))
        assertEquals(1, compareVersions("0.8.1", "0.8.1-beta01"))
        assertEquals(-1, compareVersions("0.8.1-beta01", "0.8.2"))
        assertEquals(1, compareVersions("0.8.2", "0.8.1-beta01"))
        assertEquals(0, compareVersions("0.8.1-beta01", "0.8.1-beta1"))
        assertEquals(0, compareVersions("0.8.1-beta09", "0.8.1-beta9"))
        assertEquals(-1, compareVersions("0.8.1-beta1", "0.8.1-beta2"))
        assertEquals(-1, compareVersions("0.8.1-beta01", "0.8.1-beta2"))
        assertEquals(-1, compareVersions("0.8.1-beta01", "0.8.1-beta02"))
        assertEquals(1, compareVersions("0.8.1-beta2", "0.8.1-beta1"))
        assertEquals(1, compareVersions("0.8.1-beta2", "0.8.1-beta01"))
        assertEquals(1, compareVersions("0.8.1-beta02", "0.8.1-beta01"))

        assertEquals(-1, compareVersions("0.8.0", "0.8.1-rc01"))
        assertEquals(1, compareVersions("0.8.1-rc01", "0.8.0"))
        assertEquals(-1, compareVersions("0.8.1-rc01", "0.8.1"))
        assertEquals(1, compareVersions("0.8.1", "0.8.1-rc01"))
        assertEquals(-1, compareVersions("0.8.1-rc01", "0.8.2"))
        assertEquals(1, compareVersions("0.8.2", "0.8.1-rc01"))
        assertEquals(0, compareVersions("0.8.1-rc01", "0.8.1-rc1"))
        assertEquals(0, compareVersions("0.8.1-rc09", "0.8.1-rc9"))
        assertEquals(-1, compareVersions("0.8.1-rc1", "0.8.1-rc2"))
        assertEquals(-1, compareVersions("0.8.1-rc01", "0.8.1-rc2"))
        assertEquals(-1, compareVersions("0.8.1-rc01", "0.8.1-rc02"))
        assertEquals(1, compareVersions("0.8.1-rc2", "0.8.1-rc1"))
        assertEquals(1, compareVersions("0.8.1-rc2", "0.8.1-rc01"))
        assertEquals(1, compareVersions("0.8.1-rc02", "0.8.1-rc01"))

        assertEquals(-1, compareVersions("0.8.0", "0.8.1-SNAPSHOT1"))
        assertEquals(-1, compareVersions("0.8.1-SNAPSHOT1", "0.8.1-alpha01"))
        assertEquals(-1, compareVersions("0.8.1-alpha01", "0.8.1-beta1"))
        assertEquals(-1, compareVersions("0.8.1-beta1", "0.8.1-rc02"))
        assertEquals(-1, compareVersions("0.8.1-rc02", "0.8.1"))
        assertEquals(-1, compareVersions("0.8.1", "0.8.2"))

        assertEquals(1, compareVersions("0.8.2", "0.8.1"))
        assertEquals(1, compareVersions("0.8.1", "0.8.1-rc.02"))
        assertEquals(1, compareVersions("0.8.1-rc.02", "0.8.1-beta.1"))
        assertEquals(1, compareVersions("0.8.1-beta.1", "0.8.1-alpha.01"))
        assertEquals(1, compareVersions("0.8.1-alpha.01", "0.8.1-SNAPSHOT.1"))
        assertEquals(1, compareVersions("0.8.1-SNAPSHOT.1", "0.8.0"))
    }

    @Test
    fun testIsThumbnailWithSize() {
        assertFalse(isThumbnailWithSize(Size(0, 2000), Size(500, 1000)))
        assertFalse(isThumbnailWithSize(Size(1000, 0), Size(500, 1000)))
        assertFalse(isThumbnailWithSize(Size(1000, 2000), Size(0, 1000)))
        assertFalse(isThumbnailWithSize(Size(1000, 2000), Size(500, 0)))
        assertFalse(isThumbnailWithSize(Size(1000, 2000), Size(1001, 200)))
        assertFalse(isThumbnailWithSize(Size(1000, 2000), Size(100, 2001)))
        assertFalse(isThumbnailWithSize(Size(100, 200), Size(1000, 100)))

        assertFalse(
            isThumbnailWithSize(
                size = Size(6799, 4882),
                otherSize = Size(696, 501),
                epsilonPixels = 1f
            )
        )
        assertTrue(
            isThumbnailWithSize(
                size = Size(6799, 4882),
                otherSize = Size(696, 501),
                epsilonPixels = 2f
            )
        )

        var imageSize = Size(29999, 325)
        val maxMultiple = 257

        val nextFunction: (Float) -> Float = { it + 0.1f }
        val calculateThumbnailSize: (Size, Float, RoundMode) -> Size =
            { size, multiple, mode ->
                when (mode) {
                    RoundMode.CEIL -> Size(
                        width = ceil(size.width / multiple).toInt(),
                        height = ceil(size.height / multiple).toInt()
                    )

                    RoundMode.FLOOR -> Size(
                        width = floor(size.width / multiple).toInt(),
                        height = floor(size.height / multiple).toInt()
                    )

                    RoundMode.ROUND -> Size(
                        width = (size.width / multiple).roundToInt(),
                        height = (size.height / multiple).roundToInt()
                    )
                }
            }

        generateSequence(1f, nextFunction).takeWhile { it <= maxMultiple }.forEach { multiple ->
            val thumbnailSize =
                calculateThumbnailSize(imageSize, multiple, RoundMode.CEIL)
            assertEquals(
                expected = imageSize != thumbnailSize,
                actual = isThumbnailWithSize(imageSize, thumbnailSize),
                message = "imageSize=${imageSize}, " +
                        "thumbnailSize=${thumbnailSize}, " +
                        "multiple=${multiple.format(2)}"
            )

            val thumbnailSize2 = thumbnailSize + Size(0, 2)
            assertFalse(
                actual = isThumbnailWithSize(imageSize, thumbnailSize2),
                message = "imageSize=${imageSize}, " +
                        "thumbnailSize=${thumbnailSize2}, " +
                        "multiple=${multiple.format(2)}"
            )
        }

        generateSequence(1f, nextFunction).takeWhile { it <= maxMultiple }.forEach { multiple ->
            val thumbnailSize =
                calculateThumbnailSize(imageSize, multiple, RoundMode.FLOOR)
            assertEquals(
                expected = imageSize != thumbnailSize,
                actual = isThumbnailWithSize(imageSize, thumbnailSize),
                message = "imageSize=${imageSize}, " +
                        "thumbnailSize=${thumbnailSize}, " +
                        "multiple=${multiple.format(2)}"
            )

            val thumbnailSize2 = thumbnailSize + Size(0, 2)
            assertFalse(
                actual = isThumbnailWithSize(imageSize, thumbnailSize2),
                message = "imageSize=${imageSize}, " +
                        "thumbnailSize=${thumbnailSize2}, " +
                        "multiple=${multiple.format(2)}"
            )
        }

        generateSequence(1f, nextFunction).takeWhile { it <= maxMultiple }.forEach { multiple ->
            val thumbnailSize =
                calculateThumbnailSize(imageSize, multiple, RoundMode.ROUND)
            assertEquals(
                expected = imageSize != thumbnailSize,
                actual = isThumbnailWithSize(imageSize, thumbnailSize),
                message = "imageSize=${imageSize}, " +
                        "thumbnailSize=${thumbnailSize}, " +
                        "multiple=${multiple.format(2)}"
            )

            val thumbnailSize2 = thumbnailSize + Size(0, 2)
            assertFalse(
                actual = isThumbnailWithSize(imageSize, thumbnailSize2),
                message = "imageSize=${imageSize}, " +
                        "thumbnailSize=${thumbnailSize2}, " +
                        "multiple=${multiple.format(2)}"
            )
        }

        imageSize = Size(325, 29999)

        generateSequence(1f, nextFunction).takeWhile { it <= maxMultiple }.forEach { multiple ->
            val thumbnailSize =
                calculateThumbnailSize(imageSize, multiple, RoundMode.CEIL)
            assertEquals(
                expected = imageSize != thumbnailSize,
                actual = isThumbnailWithSize(thumbnailSize, imageSize),
                message = "imageSize=${imageSize}, " +
                        "thumbnailSize=${thumbnailSize}, " +
                        "multiple=${multiple.format(2)}"
            )

            val thumbnailSize2 = thumbnailSize + Size(2, 0)
            assertFalse(
                actual = isThumbnailWithSize(thumbnailSize2, imageSize),
                message = "imageSize=${imageSize}, " +
                        "thumbnailSize=${thumbnailSize2}, " +
                        "multiple=${multiple.format(2)}"
            )
        }

        generateSequence(1f, nextFunction).takeWhile { it <= maxMultiple }.forEach { multiple ->
            val thumbnailSize =
                calculateThumbnailSize(imageSize, multiple, RoundMode.FLOOR)
            assertEquals(
                expected = imageSize != thumbnailSize,
                actual = isThumbnailWithSize(thumbnailSize, imageSize),
                message = "imageSize=${imageSize}, " +
                        "thumbnailSize=${thumbnailSize}, " +
                        "multiple=${multiple.format(2)}"
            )

            val thumbnailSize2 = thumbnailSize + Size(2, 0)
            assertFalse(
                actual = isThumbnailWithSize(thumbnailSize2, imageSize),
                message = "imageSize=${imageSize}, " +
                        "thumbnailSize=${thumbnailSize2}, " +
                        "multiple=${multiple.format(2)}"
            )
        }

        generateSequence(1f, nextFunction).takeWhile { it <= maxMultiple }.forEach { multiple ->
            val thumbnailSize =
                calculateThumbnailSize(imageSize, multiple, RoundMode.ROUND)
            assertEquals(
                expected = imageSize != thumbnailSize,
                actual = isThumbnailWithSize(thumbnailSize, imageSize),
                message = "imageSize=${imageSize}, " +
                        "thumbnailSize=${thumbnailSize}, " +
                        "multiple=${multiple.format(2)}"
            )

            val thumbnailSize2 = thumbnailSize + Size(2, 0)
            assertFalse(
                actual = isThumbnailWithSize(thumbnailSize2, imageSize),
                message = "imageSize=${imageSize}, " +
                        "thumbnailSize=${thumbnailSize2}, " +
                        "multiple=${multiple.format(2)}"
            )
        }
    }

    enum class RoundMode {
        CEIL,
        FLOOR,
        ROUND,
    }

    private class FormatItem<T>(val number: T, val newScale: Int, val expected: T)
}