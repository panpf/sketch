package com.github.panpf.sketch3.test

import android.content.Context
import android.net.Uri
import com.github.panpf.sketch3.common.http.HttpStack
import java.io.IOException
import java.io.InputStream

class TestHttpStack(val context: Context) : HttpStack {

    companion object {
        val urls = arrayOf(
            TestUri(
                Uri.parse("http://5b0988e595225.cdn.sohucs.com/images/20171219/fd5717876ab046b8aa889c9aaac4b56c.jpeg"),
                540456
            )
        )
    }

    override fun getResponse(uri: String): HttpStack.Response {
        if (uri.endsWith("fd5717876ab046b8aa889c9aaac4b56c.jpeg")) {
            return TestResponse(context)
        } else {
            throw IOException("TestHttpStack only support fd5717876ab046b8aa889c9aaac4b56c.jpeg")
        }
    }

    class TestResponse(val context: Context) : HttpStack.Response {
        override val code: Int
            get() = 200
        override val message: String?
            get() = null
        override val contentLength: Long
            get() = 540456
        override val contentType: String
            get() = "image/jpeg"
        override val isContentChunked: Boolean
            get() = false
        override val contentEncoding: String?
            get() = null

        override fun getHeaderField(name: String): String? {
            return null
        }

        override fun getHeaderFieldInt(name: String, defaultValue: Int): Int {
            return defaultValue
        }

        override fun getHeaderFieldLong(name: String, defaultValue: Long): Long {
            return defaultValue
        }

        override val headersString: String?
            get() = null
        override val content: InputStream
            get() = context.assets.open("fd5717876ab046b8aa889c9aaac4b56c.jpeg")

        override fun releaseConnection() {
        }
    }

    class TestUri(val uri: Uri, val contentLength: Long)
}