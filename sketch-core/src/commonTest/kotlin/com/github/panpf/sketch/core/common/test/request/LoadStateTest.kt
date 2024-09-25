package com.github.panpf.sketch.core.common.test.request

import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.LoadState
import com.github.panpf.sketch.request.name
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.source.DataFrom
import com.github.panpf.sketch.test.utils.FakeImage
import com.github.panpf.sketch.test.utils.getTestContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class LoadStateTest {

    @Test
    fun testStarted() {
        val context = getTestContext()
        val request = ImageRequest(context, "http://test.com/test.jpg")
        val request2 = ImageRequest(context, "http://test.com/test.png")

        LoadState.Started(request)

        assertEquals(
            expected = "Started(request=$request)",
            actual = LoadState.Started(request).toString()
        )

        val element1 = LoadState.Started(request)
        val element11 = element1.copy()
        val element2 = element1.copy(request2)

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
    }

    @Test
    fun testSuccess() {
        val context = getTestContext()
        val request = ImageRequest(context, "http://test.com/test.jpg")
        val request2 = ImageRequest(context, "http://test.com/test.png")
        val result = ImageResult.Success(
            request = request,
            image = FakeImage(100, 100),
            cacheKey = "cacheKey",
            imageInfo = ImageInfo(100, 100, "image/jpeg"),
            dataFrom = DataFrom.LOCAL,
            resize = Resize(200, 200),
            transformeds = null,
            extras = null
        )
        val result2 = result.copy(image = FakeImage(200, 200))

        LoadState.Success(request, result)

        assertEquals(
            expected = "Success(request=$request, result=$result)",
            actual = LoadState.Success(request, result).toString()
        )

        val element1 = LoadState.Success(request, result)
        val element11 = element1.copy()
        val element2 = element1.copy(request2, result)
        val element3 = element1.copy(request, result2)

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element2, element3)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element1.hashCode(), element3.hashCode())
        assertNotEquals(element2.hashCode(), element3.hashCode())
    }

    @Test
    fun testError() {
        val context = getTestContext()
        val request = ImageRequest(context, "http://test.com/test.jpg")
        val request2 = ImageRequest(context, "http://test.com/test.png")
        val result = ImageResult.Error(
            request = request,
            image = FakeImage(100, 100),
            throwable = Exception("test")
        )
        val result2 = result.copy(image = FakeImage(200, 200))

        LoadState.Error(request, result)

        assertEquals(
            expected = "Error(request=$request, result=$result)",
            actual = LoadState.Error(request, result).toString()
        )

        val element1 = LoadState.Error(request, result)
        val element11 = element1.copy()
        val element2 = element1.copy(request2, result)
        val element3 = element1.copy(request, result2)

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element2, element3)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element1.hashCode(), element3.hashCode())
        assertNotEquals(element2.hashCode(), element3.hashCode())
    }

    @Test
    fun testCanceled() {
        val context = getTestContext()
        val request = ImageRequest(context, "http://test.com/test.jpg")
        val request2 = ImageRequest(context, "http://test.com/test.png")

        LoadState.Canceled(request)

        assertEquals(
            expected = "Canceled(request=$request)",
            actual = LoadState.Canceled(request).toString()
        )

        val element1 = LoadState.Canceled(request)
        val element11 = element1.copy()
        val element2 = element1.copy(request2)

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
    }

    @Test
    fun testName() {
        val context = getTestContext()
        val request = ImageRequest(context, "http://test.com/test.jpg")
        assertEquals(
            expected = "Started",
            actual = LoadState.Started(request).name
        )
        assertEquals(
            expected = "Success",
            actual = LoadState.Success(
                request = request,
                result = ImageResult.Success(
                    request = request,
                    image = FakeImage(100, 100),
                    cacheKey = "cacheKey",
                    imageInfo = ImageInfo(100, 100, "image/jpeg"),
                    dataFrom = DataFrom.LOCAL,
                    resize = Resize(200, 200),
                    transformeds = null,
                    extras = null
                )
            ).name
        )
        assertEquals(
            expected = "Error",
            actual = LoadState.Error(
                request, ImageResult.Error(
                    request = request,
                    image = FakeImage(100, 100),
                    throwable = Exception("test")
                )
            ).name
        )
        assertEquals(
            expected = "Canceled",
            actual = LoadState.Canceled(request).name
        )
    }
}