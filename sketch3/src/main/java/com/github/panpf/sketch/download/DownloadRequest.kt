package com.github.panpf.sketch.download

import android.net.Uri
import com.github.panpf.sketch.common.DownloadableRequest
import com.github.panpf.sketch.common.cache.CachePolicy

class DownloadRequest constructor(
    override val uri: Uri,
    override val diskCacheKey: String,
    override val diskCachePolicy: CachePolicy,
) : DownloadableRequest {

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
        private var diskCacheKey: String?
        private var diskCachePolicy: CachePolicy?

        constructor(uri: Uri) {
            this.uri = uri
            this.diskCacheKey = null
            this.diskCachePolicy = null
        }

        constructor(uriString: String) : this(Uri.parse(uriString))

        internal constructor(request: DownloadRequest) {
            this.uri = request.uri
            this.diskCacheKey = request.diskCacheKey
            this.diskCachePolicy = request.diskCachePolicy
        }

        fun diskCacheKey(diskCacheKey: String?): Builder = apply {
            this.diskCacheKey = diskCacheKey
        }

        fun diskCachePolicy(diskCachePolicy: CachePolicy?): Builder = apply {
            this.diskCachePolicy = diskCachePolicy
        }

        fun build(): DownloadRequest = DownloadRequest(
            uri = uri,
            diskCacheKey = diskCacheKey ?: uri.toString(),
            diskCachePolicy = diskCachePolicy ?: CachePolicy.ENABLED,
        )
    }
}