package com.github.panpf.sketch.request

import android.net.Uri
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.request.internal.DownloadableRequest

class DownloadRequest private constructor(
    override val uri: Uri,
    override val parameters: Parameters?,
    override val httpHeaders: Map<String, String>?,
    _diskCacheKey: String?,
    _diskCachePolicy: CachePolicy?,
) : DownloadableRequest {

    override val diskCacheKey: String = _diskCacheKey ?: uri.toString()

    override val diskCachePolicy: CachePolicy = _diskCachePolicy ?: CachePolicy.ENABLED

    override val key: String by lazy {
        val parametersInfo = parameters?.let { "_${it.key}" } ?: ""
        "Download_${uri}${parametersInfo})_diskCacheKey($diskCacheKey)_diskCachePolicy($diskCachePolicy)"
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
            uri: Uri,
            configBlock: (Builder.() -> Unit)? = null
        ): DownloadRequest = Builder(uri).apply {
            configBlock?.invoke(this)
        }.build()

        fun new(
            uriString: String,
            configBlock: (Builder.() -> Unit)? = null
        ): DownloadRequest = Builder(uriString).apply {
            configBlock?.invoke(this)
        }.build()
    }

    class Builder {
        private val uri: Uri
        private var parameters: Parameters?
        private var httpHeaders: Map<String, String>?
        private var diskCacheKey: String?
        private var diskCachePolicy: CachePolicy?

        constructor(uri: Uri) {
            this.uri = uri
            this.parameters = null
            this.httpHeaders = null
            this.diskCacheKey = null
            this.diskCachePolicy = null
        }

        constructor(uriString: String) : this(Uri.parse(uriString))

        internal constructor(request: DownloadRequest) {
            this.uri = request.uri
            this.parameters = request.parameters
            this.httpHeaders = request.httpHeaders
            this.diskCacheKey = request.diskCacheKey
            this.diskCachePolicy = request.diskCachePolicy
        }

        fun httpHeaders(httpHeaders: Map<String, String>?): Builder = apply {
            this.httpHeaders = httpHeaders
        }

        fun parameters(parameters: Parameters?): Builder = apply {
            this.parameters = parameters
        }

        fun diskCacheKey(diskCacheKey: String?): Builder = apply {
            this.diskCacheKey = diskCacheKey
        }

        fun diskCachePolicy(diskCachePolicy: CachePolicy?): Builder = apply {
            this.diskCachePolicy = diskCachePolicy
        }

        fun build(): DownloadRequest = DownloadRequest(
            uri = uri,
            parameters = parameters,
            httpHeaders = httpHeaders,
            _diskCacheKey = diskCacheKey,
            _diskCachePolicy = diskCachePolicy,
        )
    }
}