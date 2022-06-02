package com.github.panpf.sketch.test.utils

import android.content.Context
import com.github.panpf.sketch.http.HttpStack
import com.github.panpf.sketch.request.ImageRequest
import java.io.InputStream

class TestHttpStack(private val context: Context, val readDelayMillis: Long? = null) : HttpStack {

    companion object {
        val testImages = arrayOf(
            TestImage(
                "http://5b0988e595225.cdn.sohucs.com/images/20171219/sample.jpeg",
                540456
            )
        )
    }

    override fun getResponse(request: ImageRequest, url: String): HttpStack.Response {
        return TestResponse(context, url.substring(url.lastIndexOf("/") + 1), readDelayMillis)
    }

    override fun toString(): String {
        return "TestHttpStack(readDelayMillis=$readDelayMillis)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TestHttpStack

        if (readDelayMillis != other.readDelayMillis) return false

        return true
    }

    override fun hashCode(): Int {
        return readDelayMillis?.hashCode() ?: 0
    }

    class TestResponse(
        private val context: Context,
        private val assetFileName: String,
        private val readDelayMillis: Long? = null
    ) :
        HttpStack.Response {
        override val code: Int
            get() = 200
        override val message: String?
            get() = null
        override val contentLength: Long
            get() = 540456
        override val contentType: String
            get() = "image/jpeg"

        override fun getHeaderField(name: String): String? {
            return null
        }
        override val content: InputStream
            get() = context.assets.open(assetFileName).run {
                if (readDelayMillis != null) {
                    SlowInputStream(this, readDelayMillis)
                } else {
                    this
                }
            }
    }

    class TestImage(val uriString: String, val contentLength: Long)
}