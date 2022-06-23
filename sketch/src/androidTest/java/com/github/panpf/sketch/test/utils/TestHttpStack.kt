package com.github.panpf.sketch.test.utils

import android.content.Context
import com.github.panpf.sketch.http.HttpStack
import com.github.panpf.sketch.request.ImageRequest
import java.io.InputStream

class TestHttpStack constructor(
    private val context: Context,
    val readDelayMillis: Long? = null,
    val connectionDelayMillis: Long? = null,
) :
    HttpStack {

    companion object {
        val testImages = arrayOf(TestImage("http://assets.com/sample.jpeg", 540456))
        val errorImage = TestImage("http://assets.com.com/error.jpeg", 540456)
        val chunkedErrorImage = TestImage(
            "http://assets.com/sample.png",
            254533,
            mapOf("Transfer-Encoding" to "chunked")
        )
        val lengthErrorImage = TestImage(
            "http://assets.com/sample.bmp",
            2833654 - 1,
        )
    }

    override fun getResponse(request: ImageRequest, url: String): HttpStack.Response {
        connectionDelayMillis?.let {
            Thread.sleep(it)
        }
        val testImage = testImages.plus(errorImage).plus(chunkedErrorImage).plus(lengthErrorImage)
            .find { it.uriString == url }
        return if (testImage != null) {
            TestResponse(context, testImage, readDelayMillis)
        } else {
            ErrorResponse(404, "Not found resource")
        }
    }

    override fun toString(): String {
        return "TestHttpStack(readDelayMillis=$readDelayMillis)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TestHttpStack) return false

        if (readDelayMillis != other.readDelayMillis) return false

        return true
    }

    override fun hashCode(): Int {
        return readDelayMillis?.hashCode() ?: 0
    }

    class ErrorResponse(
        override val code: Int,
        override val message: String,
    ) : HttpStack.Response {
        override val contentLength: Long
            get() = 0
        override val contentType: String
            get() = ""

        override fun getHeaderField(name: String): String? {
            return null
        }

        override val content: InputStream
            get() = throw Exception()
    }


    class TestResponse(
        private val context: Context,
        private val testImage: TestImage,
        private val readDelayMillis: Long? = null
    ) : HttpStack.Response {

        override val code: Int
            get() = 200
        override val message: String?
            get() = null
        override val contentLength: Long
            get() = testImage.contentLength
        override val contentType: String
            get() = "image/jpeg"

        override fun getHeaderField(name: String): String? = testImage.headerMap?.get(name)

        override val content: InputStream
            get() {
                val assetFileName =
                    testImage.uriString.substring(testImage.uriString.lastIndexOf("/") + 1)
                return context.assets.open(assetFileName).run {
                    if (readDelayMillis != null) {
                        SlowInputStream(this, readDelayMillis)
                    } else {
                        this
                    }
                }
            }

    }

    class TestImage constructor(
        val uriString: String,
        val contentLength: Long,
        val headerMap: Map<String, String>? = null
    )
}