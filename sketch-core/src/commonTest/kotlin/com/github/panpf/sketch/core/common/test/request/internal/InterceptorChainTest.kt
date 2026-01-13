package com.github.panpf.sketch.core.common.test.request.internal

import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.Interceptor
import com.github.panpf.sketch.request.internal.InterceptorChain
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

class InterceptorChainTest {

    @Test
    fun test() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        runBlock {
            val historyList = mutableListOf<String>()
            val interceptors = listOf(
                TestBitmapInterceptor1(historyList),
                TestBitmapInterceptor2(historyList),
                TestBitmapInterceptor3(historyList)
            )
            val request = ImageRequest(context, ResourceImages.jpeg.uri)
            val chain = InterceptorChain(
                requestContext = request.toRequestContext(sketch),
                interceptors = interceptors,
                index = 0
            )
            withContext(Dispatchers.Main) {
                chain.proceed(request).getOrThrow()
            }
            assertEquals(
                expected = listOf(
                    "TestInterceptor1",
                    "TestInterceptor2",
                    "TestInterceptor3",
                ),
                actual = historyList
            )
        }

        runBlock {
            val historyList = mutableListOf<String>()
            val interceptors = listOf(
                TestBitmapInterceptor2(historyList),
                TestBitmapInterceptor1(historyList),
                TestBitmapInterceptor3(historyList),
            )
            val request = ImageRequest(context, ResourceImages.jpeg.uri)
            val chain = InterceptorChain(
                requestContext = request.toRequestContext(sketch),
                interceptors = interceptors,
                index = 0
            )
            withContext(Dispatchers.Main) {
                chain.proceed(request).getOrThrow()
            }
            assertEquals(
                expected = listOf(
                    "TestInterceptor2",
                    "TestInterceptor1",
                    "TestInterceptor3",
                ),
                actual = historyList
            )
        }
    }

    private class TestBitmapInterceptor1(val historyList: MutableList<String>) :
        Interceptor {

        override val key: String? = null

        override val sortWeight: Int = 0

        override suspend fun intercept(chain: Interceptor.Chain): Result<ImageData> {
            historyList.add("TestInterceptor1")
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
            return "TestInterceptor1"
        }
    }

    private class TestBitmapInterceptor2(val historyList: MutableList<String>) :
        Interceptor {

        override val key: String? = null

        override val sortWeight: Int = 0

        override suspend fun intercept(chain: Interceptor.Chain): Result<ImageData> {
            historyList.add("TestInterceptor2")
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
            return "TestInterceptor2"
        }
    }

    private class TestBitmapInterceptor3(val historyList: MutableList<String>) : Interceptor {

        override val key: String? = null

        override val sortWeight: Int = 0

        override suspend fun intercept(chain: Interceptor.Chain): Result<ImageData> {
            historyList.add("TestInterceptor3")
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
            return "TestInterceptor3"
        }
    }
}