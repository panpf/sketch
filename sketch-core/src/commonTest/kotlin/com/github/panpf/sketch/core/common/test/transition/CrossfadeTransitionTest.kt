package com.github.panpf.sketch.core.common.test.transition

import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.source.DataFrom.MEMORY_CACHE
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.FakeImage
import com.github.panpf.sketch.test.utils.TestCrossfadeTransition
import com.github.panpf.sketch.test.utils.TestTransitionTarget
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.transition.CrossfadeTransition
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CrossfadeTransitionTest {

    @Test
    fun testFactoryConstructor() {
        CrossfadeTransition.Factory().apply {
            assertEquals(200, durationMillis)
            assertEquals(false, preferExactIntrinsicSize)
            assertEquals(false, alwaysUse)
        }

        assertFailsWith(IllegalArgumentException::class) {
            CrossfadeTransition.Factory(0)
        }

        CrossfadeTransition.Factory(
            durationMillis = 300,
            preferExactIntrinsicSize = true,
            alwaysUse = true
        ).apply {
            assertEquals(300, durationMillis)
            assertEquals(true, preferExactIntrinsicSize)
            assertEquals(true, alwaysUse)
        }
    }

    @Test
    fun testFactoryCreate() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, ComposeResImageFiles.jpeg.uri)
        val factory = CrossfadeTransition.Factory()
        val target = TestTransitionTarget()

        val requestContext = request.toRequestContext(sketch)
        val successResult = ImageResult.Success(
            request = request,
            cacheKey = requestContext.cacheKey,
            memoryCacheKey = requestContext.memoryCacheKey,
            resultCacheKey = requestContext.resultCacheKey,
            downloadCacheKey = requestContext.downloadCacheKey,
            image = FakeImage(Size(100, 200)),
            imageInfo = ImageInfo(100, 200, "image/jpeg"),
            dataFrom = LOCAL,
            resize = Resize(100, 100, Precision.LESS_PIXELS, Scale.CENTER_CROP),
            transformeds = null,
            extras = null,
        )
        factory.create(sketch, request, target, successResult)!!.apply {
            assertTrue(
                actual = this is CrossfadeTransition,
                message = "$this"
            )
            assertTrue(
                actual = this.wrappedTransition is TestCrossfadeTransition,
                message = "${this.wrappedTransition}"
            )
        }

        val errorResult = ImageResult.Error(
            request = request,
            image = FakeImage(Size(100, 200)),
            throwable = Exception("")
        )
        assertNotNull(factory.create(sketch, request, target, errorResult))

        val fromMemoryCacheSuccessResult = ImageResult.Success(
            request = request,
            cacheKey = requestContext.cacheKey,
            memoryCacheKey = requestContext.memoryCacheKey,
            resultCacheKey = requestContext.resultCacheKey,
            downloadCacheKey = requestContext.downloadCacheKey,
            image = FakeImage(Size(100, 200)),
            imageInfo = ImageInfo(100, 200, "image/jpeg"),
            dataFrom = MEMORY_CACHE,
            resize = Resize(100, 100, Precision.LESS_PIXELS, Scale.CENTER_CROP),
            transformeds = null,
            extras = null,
        )
        assertNull(factory.create(sketch, request, target, fromMemoryCacheSuccessResult))

        val alwaysUseFactory = CrossfadeTransition.Factory(alwaysUse = true)
        assertNotNull(
            alwaysUseFactory.create(sketch, request, target, fromMemoryCacheSuccessResult)
        )
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val element1 = CrossfadeTransition.Factory()
        val element11 = CrossfadeTransition.Factory()
        val element2 = CrossfadeTransition.Factory(durationMillis = 300)
        val element3 = CrossfadeTransition.Factory(preferExactIntrinsicSize = true)
        val element4 = CrossfadeTransition.Factory(alwaysUse = true)
        val element5 = CrossfadeTransition.Factory(fadeStart = false)

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element1, element4)
        assertNotEquals(element1, element5)
        assertNotEquals(element2, element3)
        assertNotEquals(element2, element4)
        assertNotEquals(element3, element4)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element1.hashCode(), element3.hashCode())
        assertNotEquals(element1.hashCode(), element4.hashCode())
        assertNotEquals(element1.hashCode(), element5.hashCode())
        assertNotEquals(element2.hashCode(), element3.hashCode())
        assertNotEquals(element2.hashCode(), element4.hashCode())
        assertNotEquals(element3.hashCode(), element4.hashCode())
    }

    @Test
    fun testFactoryKey() {
        val element1 = CrossfadeTransition.Factory()
        val element2 = CrossfadeTransition.Factory(preferExactIntrinsicSize = true)
        val element3 = CrossfadeTransition.Factory(alwaysUse = true)
        val element4 = CrossfadeTransition.Factory(fadeStart = false)

        assertEquals(
            "Crossfade(200,true,false,false)",
            element1.key
        )
        assertEquals(
            "Crossfade(200,true,true,false)",
            element2.key
        )
        assertEquals(
            "Crossfade(200,true,false,true)",
            element3.key
        )
        assertEquals(
            "Crossfade(200,false,false,false)",
            element4.key
        )
    }

    @Test
    fun testFactoryToString() {
        val element1 = CrossfadeTransition.Factory()
        val element2 = CrossfadeTransition.Factory(preferExactIntrinsicSize = true)
        val element3 = CrossfadeTransition.Factory(alwaysUse = true)
        val element4 = CrossfadeTransition.Factory(fadeStart = false)

        assertEquals(
            "CrossfadeTransition.Factory(durationMillis=200, fadeStart=true, preferExactIntrinsicSize=false, alwaysUse=false)",
            element1.toString()
        )
        assertEquals(
            "CrossfadeTransition.Factory(durationMillis=200, fadeStart=true, preferExactIntrinsicSize=true, alwaysUse=false)",
            element2.toString()
        )
        assertEquals(
            "CrossfadeTransition.Factory(durationMillis=200, fadeStart=true, preferExactIntrinsicSize=false, alwaysUse=true)",
            element3.toString()
        )
        assertEquals(
            "CrossfadeTransition.Factory(durationMillis=200, fadeStart=false, preferExactIntrinsicSize=false, alwaysUse=false)",
            element4.toString()
        )
    }
}