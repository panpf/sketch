package com.github.panpf.sketch3.download

import android.net.Uri
import com.github.panpf.sketch3.common.ImageRequest
import com.github.panpf.sketch3.common.cache.CachePolicy

class DownloadRequest private constructor(
    override val uri: Uri,
    val diskCacheKey: String,
    val diskCachePolicy: CachePolicy,
    val listener: DownloadListener?,
    val progressListener: DownloadProgressListener?,
) : ImageRequest {

    fun newBuilder(): Builder {
        return Builder(this)
    }

    class Builder(private val uri: Uri) {
        private var diskCacheKey: String? = null
        private var diskCachePolicy: CachePolicy? = null
        private var listener: DownloadListener? = null
        private var progressListener: DownloadProgressListener? = null

        constructor(request: DownloadRequest) : this(request.uri) {
            this.diskCacheKey = request.diskCacheKey
            this.diskCachePolicy = request.diskCachePolicy
            this.listener = request.listener
            this.progressListener = request.progressListener
        }

        fun diskCacheKey(diskCacheKey: String?): Builder = apply {
            this.diskCacheKey = diskCacheKey
        }

        fun diskCachePolicy(diskCachePolicy: CachePolicy?): Builder = apply {
            this.diskCachePolicy = diskCachePolicy
        }

        fun listener(listener: DownloadListener?): Builder = apply {
            this.listener = listener
        }

        fun progressListener(progressListener: DownloadProgressListener?): Builder = apply {
            this.progressListener = progressListener
        }

        fun build(): DownloadRequest = DownloadRequest(
            uri = uri,
            diskCacheKey = diskCacheKey ?: uri.toString(),
            diskCachePolicy = diskCachePolicy ?: CachePolicy.ENABLED,
            listener = listener,
            progressListener = progressListener,
        )
    }
}