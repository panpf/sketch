package com.github.panpf.sketch.request

import android.net.Uri
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.request.RequestDepth.NETWORK
import com.github.panpf.sketch.request.internal.ImageData
import com.github.panpf.sketch.request.internal.ImageRequest

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
            uriString: String,
            configBlock: (Builder.() -> Unit)? = null
        ): DownloadRequest = Builder(uriString).apply {
            configBlock?.invoke(this)
        }.build()

        fun new(
            uri: Uri,
            configBlock: (Builder.() -> Unit)? = null
        ): DownloadRequest = Builder(uri).apply {
            configBlock?.invoke(this)
        }.build()

        fun newBuilder(
            uriString: String,
            configBlock: (Builder.() -> Unit)? = null
        ): Builder = Builder(uriString).apply {
            configBlock?.invoke(this)
        }

        fun newBuilder(
            uri: Uri,
            configBlock: (Builder.() -> Unit)? = null
        ): Builder = Builder(uri).apply {
            configBlock?.invoke(this)
        }
    }

    open class Builder(private val uri: Uri) {

        private var depth: RequestDepth? = null
        private var parameters: Parameters? = null
        private var httpHeaders: Map<String, String>? = null
        private var diskCacheKey: String? = null
        private var diskCachePolicy: CachePolicy? = null
        private var listener: Listener<ImageRequest, ImageData>? = null
        private var networkProgressListener: ProgressListener<ImageRequest>? = null

        constructor(uriString: String) : this(Uri.parse(uriString))

        internal constructor(request: DownloadRequest) : this(request.uri) {
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

        fun listener(listener: Listener<DownloadRequest, DownloadData>?): Builder = apply {
            @Suppress("UNCHECKED_CAST")
            this.listener = listener as Listener<ImageRequest, ImageData>?
        }

        fun networkProgressListener(networkProgressListener: ProgressListener<DownloadRequest>?): Builder =
            apply {
                @Suppress("UNCHECKED_CAST")
                this.networkProgressListener =
                    networkProgressListener as ProgressListener<ImageRequest>?
            }

        fun build(): DownloadRequest = DownloadRequestImpl(
            uri = uri,
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
        override val uri: Uri,
        _depth: RequestDepth?,
        override val parameters: Parameters?,
        override val httpHeaders: Map<String, String>?,
        _diskCacheKey: String?,
        _diskCachePolicy: CachePolicy?,
        override val listener: Listener<ImageRequest, ImageData>?,
        override val networkProgressListener: ProgressListener<ImageRequest>?,
    ) : DownloadRequest {

        override val uriString: String by lazy { uri.toString() }

        override val depth: RequestDepth = _depth ?: NETWORK

        override val diskCacheKey: String = _diskCacheKey ?: uriString

        override val diskCachePolicy: CachePolicy = _diskCachePolicy ?: CachePolicy.ENABLED

        override val key: String by lazy {
            val parametersInfo = parameters?.let { "_${it.key}" } ?: ""
            "Download_${uriString}${parametersInfo})_diskCacheKey($diskCacheKey)_diskCachePolicy($diskCachePolicy)"
        }
    }
}