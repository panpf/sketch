package com.github.panpf.sketch.http

import com.github.panpf.sketch.request.Parameters
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.cancel

class KtorStack(client: HttpClient? = null) : HttpStack {

    private val client = client ?: HttpClient()

    override suspend fun getResponse(
        url: String,
        httpHeaders: HttpHeaders?,
        parameters: Parameters?
    ): HttpStack.Response {
        val httpRequest = HttpRequestBuilder().apply {
            url(url)
            httpHeaders?.apply {
                addList.forEach {
                    header(it.first, it.second)
                }
                setList.forEach {
                    header(it.first, it.second)
                }
            }
        }
        val httpResponse = client.request(httpRequest)
        return Response(httpResponse)
    }

    override fun toString(): String = "KtorStack"

    class Response(val httpResponse: HttpResponse) : HttpStack.Response {

        override val code: Int = httpResponse.status.value

        override val message: String = httpResponse.status.description

        override val contentLength: Long = getHeaderField("content-length")?.toLongOrNull() ?: -1L

        override val contentType: String? = getHeaderField("content-type")

        override fun getHeaderField(name: String): String? {
            return httpResponse.headers[name]
        }

        override suspend fun content(): HttpStack.Content {
            return Content(httpResponse.bodyAsChannel())
        }
    }

    class Content(private val channel: ByteReadChannel) : HttpStack.Content {

        override suspend fun read(buffer: ByteArray): Int {
            return channel.readAvailable(buffer, 0, buffer.size)
        }

        override fun close() {
            channel.cancel()
        }
    }
}