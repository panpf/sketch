package com.github.panpf.sketch.request

import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.http.HttpHeaders
import com.github.panpf.sketch.request.internal.ImageOptions
import com.github.panpf.sketch.request.internal.ImageRequest

fun DownloadOptions(
    configBlock: (DownloadOptions.Builder.() -> Unit)? = null
): DownloadOptions = DownloadOptions.Builder().apply {
    configBlock?.invoke(this)
}.build()

fun DownloadOptionsBuilder(
    configBlock: (DownloadOptions.Builder.() -> Unit)? = null
): DownloadOptions.Builder = DownloadOptions.Builder().apply {
    configBlock?.invoke(this)
}

interface DownloadOptions : ImageOptions {

    val httpHeaders: HttpHeaders?
    val networkContentDiskCachePolicy: CachePolicy?

    override fun isEmpty(): Boolean =
        super.isEmpty() && httpHeaders == null && networkContentDiskCachePolicy == null

    fun newDownloadOptions(
        configBlock: (Builder.() -> Unit)? = null
    ): DownloadOptions = Builder(this).apply {
        configBlock?.invoke(this)
    }.build()

    fun newDownloadOptionsBuilder(
        configBlock: (Builder.() -> Unit)? = null
    ): Builder = Builder(this).apply {
        configBlock?.invoke(this)
    }

    open class Builder {

        private var depth: RequestDepth? = null
        private var parametersBuilder: Parameters.Builder? = null

        private var httpHeaders: HttpHeaders.Builder? = null
        private var networkContentDiskCachePolicy: CachePolicy? = null

        constructor()

        internal constructor(options: DownloadOptions) {
            this.depth = options.depth
            this.parametersBuilder = options.parameters?.newBuilder()

            this.httpHeaders = options.httpHeaders?.newBuilder()
            this.networkContentDiskCachePolicy = options.networkContentDiskCachePolicy
        }

        fun depth(depth: RequestDepth?): Builder = apply {
            this.depth = depth
        }

        fun depthFrom(from: String?): Builder = apply {
            if (from != null) {
                setParameter(ImageRequest.REQUEST_DEPTH_FROM, from, null)
            } else {
                removeParameter(ImageRequest.REQUEST_DEPTH_FROM)
            }
        }

        fun parameters(parameters: Parameters?): Builder = apply {
            this.parametersBuilder = parameters?.newBuilder()
        }

        /**
         * Set a parameter for this request.
         *
         * @see Parameters.Builder.set
         */
        @JvmOverloads
        fun setParameter(key: String, value: Any?, cacheKey: String? = value?.toString()): Builder =
            apply {
                this.parametersBuilder = (this.parametersBuilder ?: Parameters.Builder()).apply {
                    set(key, value, cacheKey)
                }
            }

        /**
         * Remove a parameter from this request.
         *
         * @see Parameters.Builder.remove
         */
        fun removeParameter(key: String): Builder = apply {
            this.parametersBuilder?.remove(key)
        }

        fun httpHeaders(httpHeaders: HttpHeaders?): Builder = apply {
            this.httpHeaders = httpHeaders?.newBuilder()
        }

        /**
         * Add a header for any network operations performed by this request.
         */
        fun addHttpHeader(name: String, value: String): Builder = apply {
            this.httpHeaders = (this.httpHeaders ?: HttpHeaders.Builder()).apply {
                add(name, value)
            }
        }

        /**
         * Set a header for any network operations performed by this request.
         */
        fun setHttpHeader(name: String, value: String): Builder = apply {
            this.httpHeaders = (this.httpHeaders ?: HttpHeaders.Builder()).apply {
                set(name, value)
            }
        }

        /**
         * Remove all network headers with the key [name].
         */
        fun removeHttpHeader(name: String): Builder = apply {
            this.httpHeaders?.removeAll(name)
        }

        fun networkContentDiskCachePolicy(networkContentDiskCachePolicy: CachePolicy?): Builder =
            apply {
                this.networkContentDiskCachePolicy = networkContentDiskCachePolicy
            }

        fun build(): DownloadOptions = DownloadOptionsImpl(
            depth = depth,
            parameters = parametersBuilder?.build(),
            httpHeaders = httpHeaders?.build(),
            networkContentDiskCachePolicy = networkContentDiskCachePolicy,
        )
    }

    private class DownloadOptionsImpl(
        override val depth: RequestDepth?,
        override val parameters: Parameters?,
        override val httpHeaders: HttpHeaders?,
        override val networkContentDiskCachePolicy: CachePolicy?,
    ) : DownloadOptions
}