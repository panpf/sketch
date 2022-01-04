package com.github.panpf.sketch.request

import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.request.RequestDepth.NETWORK
import com.github.panpf.sketch.request.internal.ImageRequest
import com.github.panpf.sketch.request.internal.ImageResult

interface DownloadRequest : ImageRequest {

    val httpHeaders: Map<String, String>?
    val diskCacheKey: String
    val diskCachePolicy: CachePolicy
    val networkProgressListener: ProgressListener<ImageRequest>?

    fun newDownloadRequest(
        configBlock: (Builder.() -> Unit)? = null
    ): DownloadRequest = Builder(this).apply {
        configBlock?.invoke(this)
    }.build()

    fun newDownloadRequestBuilder(
        configBlock: (Builder.() -> Unit)? = null
    ): Builder = Builder(this).apply {
        configBlock?.invoke(this)
    }

    companion object {
        fun new(
            url: String,
            configBlock: (Builder.() -> Unit)? = null
        ): DownloadRequest = Builder(url).apply {
            configBlock?.invoke(this)
        }.build()

        fun newBuilder(
            url: String,
            configBlock: (Builder.() -> Unit)? = null
        ): Builder = Builder(url).apply {
            configBlock?.invoke(this)
        }
    }

    open class Builder(private val url: String) {

        private var depth: RequestDepth? = null
        private var parameters: Parameters? = null
        private var httpHeaders: Map<String, String>? = null
        private var diskCacheKey: String? = null
        private var diskCachePolicy: CachePolicy? = null
        private var listener: Listener<ImageRequest, ImageResult>? = null
        private var networkProgressListener: ProgressListener<ImageRequest>? = null

        internal constructor(request: DownloadRequest) : this(request.url) {
            this.depth = request.depth
            this.parameters = request.parameters
            this.httpHeaders = request.httpHeaders
            this.diskCacheKey = request.diskCacheKey
            this.diskCachePolicy = request.diskCachePolicy
            this.listener = request.listener
            this.networkProgressListener = request.networkProgressListener
        }

        fun depth(depth: RequestDepth?): Builder = apply {
            this.depth = depth
        }

        fun parameters(parameters: Parameters?): Builder = apply {
            this.parameters = parameters
        }

        fun httpHeaders(httpHeaders: Map<String, String>?): Builder = apply {
            this.httpHeaders = httpHeaders
        }

        fun diskCacheKey(diskCacheKey: String?): Builder = apply {
            this.diskCacheKey = diskCacheKey
        }

        fun diskCachePolicy(diskCachePolicy: CachePolicy?): Builder = apply {
            this.diskCachePolicy = diskCachePolicy
        }

        fun listener(listener: Listener<DownloadRequest, DownloadResult>?): Builder = apply {
            @Suppress("UNCHECKED_CAST")
            this.listener = listener as Listener<ImageRequest, ImageResult>?
        }

        fun networkProgressListener(networkProgressListener: ProgressListener<DownloadRequest>?): Builder =
            apply {
                @Suppress("UNCHECKED_CAST")
                this.networkProgressListener =
                    networkProgressListener as ProgressListener<ImageRequest>?
            }

        fun build(): DownloadRequest = DownloadRequestImpl(
            url = url,
            _depth = depth,
            parameters = parameters,
            httpHeaders = httpHeaders,
            _diskCacheKey = diskCacheKey,
            _diskCachePolicy = diskCachePolicy,
            listener = listener,
            networkProgressListener = networkProgressListener,
        )
    }

    private class DownloadRequestImpl(
        override val url: String,
        _depth: RequestDepth?,
        override val parameters: Parameters?,
        override val httpHeaders: Map<String, String>?,
        _diskCacheKey: String?,
        _diskCachePolicy: CachePolicy?,
        override val listener: Listener<ImageRequest, ImageResult>?,
        override val networkProgressListener: ProgressListener<ImageRequest>?,
    ) : DownloadRequest {

        override val depth: RequestDepth = _depth ?: NETWORK

        override val diskCacheKey: String = _diskCacheKey ?: url

        override val diskCachePolicy: CachePolicy = _diskCachePolicy ?: CachePolicy.ENABLED

        override val key: String by lazy {
            val parametersInfo = parameters?.let { "_${it.key}" } ?: ""
            "Download_${url}${parametersInfo})_diskCacheKey($diskCacheKey)_diskCachePolicy($diskCachePolicy)"
        }
    }
}