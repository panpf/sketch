package com.github.panpf.sketch.request

import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.request.RequestDepth.NETWORK
import com.github.panpf.sketch.request.internal.DownloadableRequest
import com.github.panpf.sketch.request.internal.ListenerRequest

class DownloadRequest private constructor(
    override val url: String,
    _depth: RequestDepth?,
    override val parameters: Parameters?,
    override val httpHeaders: Map<String, String>?,
    _diskCacheKey: String?,
    _diskCachePolicy: CachePolicy?,
    override val listener: Listener<DownloadRequest, DownloadResult>?,
    override val networkProgressListener: ProgressListener<DownloadRequest>?,
) : DownloadableRequest, ListenerRequest<DownloadRequest, DownloadResult> {

    override val depth: RequestDepth = _depth ?: NETWORK

    override val diskCacheKey: String = _diskCacheKey ?: url.toString()

    override val diskCachePolicy: CachePolicy = _diskCachePolicy ?: CachePolicy.ENABLED

    override val key: String by lazy {
        val parametersInfo = parameters?.let { "_${it.key}" } ?: ""
        "Download_${url}${parametersInfo})_diskCacheKey($diskCacheKey)_diskCachePolicy($diskCachePolicy)"
    }

    fun newBuilder(
        configBlock: (Builder.() -> Unit)? = null
    ): Builder = Builder(this).apply {
        configBlock?.invoke(this)
    }

    fun new(
        configBlock: (Builder.() -> Unit)? = null
    ): DownloadRequest = Builder(this).apply {
        configBlock?.invoke(this)
    }.build()

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

    class Builder(private val url: String) {

        private var depth: RequestDepth? = null
        private var parameters: Parameters? = null
        private var httpHeaders: Map<String, String>? = null
        private var diskCacheKey: String? = null
        private var diskCachePolicy: CachePolicy? = null
        private var listener: Listener<DownloadRequest, DownloadResult>? = null
        private var networkProgressListener: ProgressListener<DownloadRequest>? = null

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
            this.listener = listener
        }

        fun networkProgressListener(networkProgressListener: ProgressListener<DownloadRequest>?): Builder =
            apply {
                this.networkProgressListener = networkProgressListener
            }

        fun build(): DownloadRequest = DownloadRequest(
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
}