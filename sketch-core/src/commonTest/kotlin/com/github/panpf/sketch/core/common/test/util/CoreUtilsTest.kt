package com.github.panpf.sketch.core.common.test.util

import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.images.ResourceImages
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
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.asOrNull
import com.github.panpf.sketch.util.asOrThrow
import com.github.panpf.sketch.util.ceilRoundPow2
import com.github.panpf.sketch.util.computeScaleMultiplierWithFit
import com.github.panpf.sketch.util.computeScaleMultiplierWithOneSide
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
import com.github.panpf.sketch.util.md5
import com.github.panpf.sketch.util.toHexString
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runTest
import okio.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

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
    fun testComputeScaleMultiplierWithFit() {
        assertEquals(0.2, computeScaleMultiplierWithFit(1000, 600, 200, 400, true), 0.1)
        assertEquals(0.6, computeScaleMultiplierWithFit(1000, 600, 200, 400, false), 0.1)
        assertEquals(0.3, computeScaleMultiplierWithFit(1000, 600, 400, 200, true), 0.1)
        assertEquals(0.4, computeScaleMultiplierWithFit(1000, 600, 400, 200, false), 0.1)

        assertEquals(0.6, computeScaleMultiplierWithFit(1000, 600, 2000, 400, true), 0.1)
        assertEquals(2.0, computeScaleMultiplierWithFit(1000, 600, 2000, 400, false), 0.1)
        assertEquals(0.4, computeScaleMultiplierWithFit(1000, 600, 400, 2000, true), 0.1)
        assertEquals(3.3, computeScaleMultiplierWithFit(1000, 600, 400, 2000, false), 0.1)

        assertEquals(2.0, computeScaleMultiplierWithFit(1000, 600, 2000, 4000, true), 0.1)
        assertEquals(6.6, computeScaleMultiplierWithFit(1000, 600, 2000, 4000, false), 0.1)
        assertEquals(3.3, computeScaleMultiplierWithFit(1000, 600, 4000, 2000, true), 0.1)
        assertEquals(4.0, computeScaleMultiplierWithFit(1000, 600, 4000, 2000, false), 0.1)
    }

    @Test
    fun testComputeScaleMultiplierWithOneSide() {
        assertEquals(
            expected = 1.5f,
            actual = computeScaleMultiplierWithOneSide(
                sourceSize = Size(100, 100),
                targetSize = Size(150, 200)
            )
        )
        assertEquals(
            expected = 0.2f,
            actual = computeScaleMultiplierWithOneSide(
                sourceSize = Size(100, 100),
                targetSize = Size(50, 20)
            )
        )
        assertEquals(
            expected = 2.0f,
            actual = computeScaleMultiplierWithOneSide(
                sourceSize = Size(100, 100),
                targetSize = Size(0, 200)
            )
        )
        assertEquals(
            expected = 0.5f,
            actual = computeScaleMultiplierWithOneSide(
                sourceSize = Size(100, 100),
                targetSize = Size(50, 0)
            )
        )
        assertEquals(
            expected = 1.0f,
            actual = computeScaleMultiplierWithOneSide(
                sourceSize = Size(100, 100),
                targetSize = Size(0, 0)
            )
        )
    }

    @Test
    fun testImageRequestDifference() {
        val context = getTestContext()
        val request = ImageRequest(context, ResourceImages.jpeg.uri) {
            registerListener(object : Listener {})
            registerProgressListener { _, _ -> }
            target(TestTarget())
            lifecycle(TestLifecycle())
            depth(depth = Depth.LOCAL, from = "test")
            setExtra("extra1", "extra1Value")
            httpHeader("httpHeader", "httpHeaderValue")
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
            disallowAnimatedImage(false)
            resizeOnDraw(true)
            memoryCachePolicy(CachePolicy.ENABLED)
            components {
                addDecoder(TestDecoder.Factory())
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
//        ImageRequest(context2, ResourceImages.jpeg.uri).apply {
//            assertEquals(
//                expected = "context different: '${request.context}' vs '${this@apply.context}'",
//                actual = request.difference(this@apply)
//            )
//        }
        ImageRequest(context, ResourceImages.png.uri).apply {
            assertEquals(
                expected = "uri different: '${request.uri}' vs '${this@apply.uri}'",
                actual = request.difference(this@apply)
            )
        }
        request.newRequest {
            registerListener(object : Listener {})
        }.apply {
            assertEquals(
                expected = "listener different: '${request.listener}' vs '${this@apply.listener}'",
                actual = request.difference(this@apply)
            )
        }
        request.newRequest {
            registerProgressListener { _, _ -> }
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
            httpHeader("httpHeader", "httpHeaderValue2")
        }.apply {
            assertEquals(
                expected = "httpHeaders different: '${request.httpHeaders}' vs '${this@apply.httpHeaders}'",
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
            disallowAnimatedImage(true)
        }.apply {
            assertEquals(
                expected = "disallowAnimatedImage different: '${request.disallowAnimatedImage}' vs '${this@apply.disallowAnimatedImage}'",
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
                addDecoder(TestDecoder.Factory())
                addFetcher(TestFetcher.Factory())
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
            httpHeader("httpHeader", "httpHeaderValue")
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
            disallowAnimatedImage(false)
            resizeOnDraw(true)
            memoryCachePolicy(CachePolicy.ENABLED)
            components {
                addDecoder(TestDecoder.Factory())
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
            httpHeader("httpHeader", "httpHeaderValue2")
        }.apply {
            assertEquals(
                expected = "httpHeaders different: '${options.httpHeaders}' vs '${this@apply.httpHeaders}'",
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
            disallowAnimatedImage(true)
        }.apply {
            assertEquals(
                expected = "disallowAnimatedImage different: '${options.disallowAnimatedImage}' vs '${this@apply.disallowAnimatedImage}'",
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
                addDecoder(TestDecoder.Factory())
                addFetcher(TestFetcher.Factory())
            }
        }.apply {
            assertEquals(
                expected = "componentRegistry different: '${options.componentRegistry}' vs '${this@apply.componentRegistry}'",
                actual = options.difference(this@apply)
            )
        }
    }

    private class FormatItem<T>(val number: T, val newScale: Int, val expected: T)
}