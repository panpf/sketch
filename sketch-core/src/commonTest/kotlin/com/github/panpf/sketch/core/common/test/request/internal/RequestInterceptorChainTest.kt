package com.github.panpf.sketch.core.common.test.request.internal

import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.RequestInterceptor
import com.github.panpf.sketch.request.internal.RequestInterceptorChain
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.createBitmapImage
import com.github.panpf.sketch.test.utils.runBlock
import com.github.panpf.sketch.test.utils.toRequestContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertEquals

class RequestInterceptorChainTest {

    @Test
    fun test() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        runBlock {
            val historyList = mutableListOf<String>()
            val interceptors = listOf(
                TestBitmapRequestInterceptor1(historyList),
                TestBitmapRequestInterceptor2(historyList),
                TestBitmapRequestInterceptor3(historyList)
            )
            val request = ImageRequest(context, ResourceImages.jpeg.uri)
            val chain = RequestInterceptorChain(
                requestContext = request.toRequestContext(sketch),
                interceptors = interceptors,
                index = 0
            )
            withContext(Dispatchers.Main) {
                chain.proceed(request).getOrThrow()
            }
            assertEquals(
                expected = listOf(
                    "TestRequestInterceptor1",
                    "TestRequestInterceptor2",
                    "TestRequestInterceptor3",
                ),
                actual = historyList
            )
        }

        runBlock {
            val historyList = mutableListOf<String>()
            val interceptors = listOf(
                TestBitmapRequestInterceptor2(historyList),
                TestBitmapRequestInterceptor1(historyList),
                TestBitmapRequestInterceptor3(historyList),
            )
            val request = ImageRequest(context, ResourceImages.jpeg.uri)
            val chain = RequestInterceptorChain(
                requestContext = request.toRequestContext(sketch),
                interceptors = interceptors,
                index = 0
            )
            withContext(Dispatchers.Main) {
                chain.proceed(request).getOrThrow()
            }
            assertEquals(
                expected = listOf(
                    "TestRequestInterceptor2",
                    "TestRequestInterceptor1",
                    "TestRequestInterceptor3",
                ),
                actual = historyList
            )
        }
    }

    private class TestBitmapRequestInterceptor1(val historyList: MutableList<String>) :
        RequestInterceptor {

        override val key: String? = null

        override val sortWeight: Int = 0

        override suspend fun intercept(chain: RequestInterceptor.Chain): Result<ImageData> {
            historyList.add("TestRequestInterceptor1")
            return chain.proceed(chain.request)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other != null && this::class == other::class
        }

        override fun hashCode(): Int {
            return this::class.hashCode()
        }

        override fun toString(): String {
            return "TestRequestInterceptor1"
        }
    }

    private class TestBitmapRequestInterceptor2(val historyList: MutableList<String>) :
        RequestInterceptor {

        override val key: String? = null

        override val sortWeight: Int = 0

        override suspend fun intercept(chain: RequestInterceptor.Chain): Result<ImageData> {
            historyList.add("TestRequestInterceptor2")
            return chain.proceed(chain.request)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other != null && this::class == other::class
        }

        override fun hashCode(): Int {
            return this::class.hashCode()
        }

        override fun toString(): String {
            return "TestRequestInterceptor2"
        }
    }

    private class TestBitmapRequestInterceptor3(val historyList: MutableList<String>) :
        RequestInterceptor {

        override val key: String? = null

        override val sortWeight: Int = 0

        override suspend fun intercept(chain: RequestInterceptor.Chain): Result<ImageData> {
            historyList.add("TestRequestInterceptor3")
            return Result.success(
                ImageData(
                    image = createBitmapImage(12, 45),
                    imageInfo = ImageInfo(12, 45, "image/jpeg"),
                    dataFrom = LOCAL,
                    resize = Resize(100, 100, LESS_PIXELS, CENTER_CROP),
                    transformeds = null,
                    extras = null,
                )
            )
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other != null && this::class == other::class
        }

        override fun hashCode(): Int {
            return this::class.hashCode()
        }

        override fun toString(): String {
            return "TestRequestInterceptor3"
        }
    }
}