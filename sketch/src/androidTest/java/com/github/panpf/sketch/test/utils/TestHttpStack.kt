package com.github.panpf.sketch.test.utils

import android.content.Context
import com.github.panpf.sketch.http.HttpStack
import com.github.panpf.sketch.request.ImageRequest
import java.io.InputStream

class TestHttpStack(private val context: Context, val readDelayMillis: Long? = null) : HttpStack {

    companion object {
        val testUris = arrayOf(
            TestUri(
                "http://5b0988e595225.cdn.sohucs.com/images/20171219/sample.jpeg",
                540456
            )
        )
    }

    override fun getResponse(request: ImageRequest, url: String): HttpStack.Response {
        return TestResponse(context, url.substring(url.lastIndexOf("/") + 1), readDelayMillis)
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

    class TestUri(val uriString: String, val contentLength: Long)
}