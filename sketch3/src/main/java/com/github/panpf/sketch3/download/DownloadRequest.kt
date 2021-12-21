package com.github.panpf.sketch3.download

import com.github.panpf.sketch3.common.cache.CachePolicy

class DownloadRequest(
    val url: String,
    val diskCacheKey: String?,
    val diskCachePolicy: CachePolicy?,
    val listener: DownloadListener?,
    val progressListener: DownloadProgressListener?,
) {

    fun newBuilder(): Builder {
        return Builder(this)
    }

    class Builder(private val url: String) {
        //        private var diskCacheKey: String? = null
        private var diskCachePolicy: CachePolicy? = null
        private var listener: DownloadListener? = null
        private var progressListener: DownloadProgressListener? = null

        constructor(request: DownloadRequest) : this(request.url) {
//            this.diskCacheKey = request.diskCacheKey
            this.diskCachePolicy = request.diskCachePolicy
            this.listener = request.listener
            this.progressListener = request.progressListener
        }

        fun listener(listener: DownloadListener?): Builder = apply {
            this.listener = listener
        }

        fun progressListener(progressListener: DownloadProgressListener?): Builder = apply {
            this.progressListener = progressListener
        }

        fun diskCachePolicy(diskCachePolicy: CachePolicy?): Builder = apply {
            this.diskCachePolicy = diskCachePolicy
        }

        fun build(): DownloadRequest = DownloadRequest(
            url = url,
            diskCacheKey = null,
            diskCachePolicy = diskCachePolicy,
            listener = listener,
            progressListener = progressListener,
        )
    }
}