package com.github.panpf.sketch.util

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.ImageTransformer
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.cache.MemoryCache.Value
import com.github.panpf.sketch.decode.DecodeResult
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.Disposable
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.internal.RequestContext

fun Sketch.enqueueDownload(uri: String): Disposable {
    val request = buildDownloadRequest(context, uri)
    return enqueue(request)
}

suspend fun Sketch.executeDownload(uri: String): ImageResult {
    val request = buildDownloadRequest(context, uri)
    return execute(request)
}

fun buildDownloadRequest(context: PlatformContext, uri: String): ImageRequest =
    ImageRequest(context, uri) {
        memoryCachePolicy(CachePolicy.DISABLED)
        resultCachePolicy(CachePolicy.DISABLED)
        downloadCachePolicy(CachePolicy.ENABLED)
        components {
            addDecoder(DownloadFakeDecoder.Factory())
        }
    }

private class DownloadFakeDecoder(private val fetchResult: FetchResult) : Decoder {

    override suspend fun decode(): Result<DecodeResult> {
        val result = DecodeResult(
            image = DownloadFakeImage(),
            imageInfo = ImageInfo(
                mimeType = "image/png",
                width = 1,
                height = 1,
            ),
            dataFrom = fetchResult.dataFrom,
            transformedList = null,
            extras = null,
        )
        return Result.success(result)
    }

    class Factory : Decoder.Factory {

        override val key: String = "DownloadFakeDecoder"

        override fun create(requestContext: RequestContext, fetchResult: FetchResult): Decoder {
            return DownloadFakeDecoder(fetchResult)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other is Factory
        }

        override fun hashCode(): Int {
            return this@Factory::class.hashCode()
        }

        override fun toString(): String = "DownloadFakeDecoder"
    }
}

private class DownloadFakeImage : Image {

    override val width: Int = 1
    override val height: Int = 1
    override val byteCount: Long = 1
    override val allocationByteCount: Long = 1
    override val shareable: Boolean = true

    override fun cacheValue(extras: Map<String, Any?>?): Value? = null

    override fun checkValid(): Boolean = true

    override fun transformer(): ImageTransformer? = null

    override fun getPixels(): IntArray? = null
}