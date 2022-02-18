package com.github.panpf.sketch.request

import android.content.Context
import android.net.Uri
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.http.HttpHeaders
import com.github.panpf.sketch.request.DownloadRequest.Builder
import com.github.panpf.sketch.request.internal.ImageRequest
import com.github.panpf.sketch.request.internal.ImageResult

fun DownloadRequest(
    context: Context,
    uriString: String,
    configBlock: (Builder.() -> Unit)? = null
): DownloadRequest = Builder(context, uriString).apply {
    configBlock?.invoke(this)
}.build()

fun DownloadRequestBuilder(
    context: Context,
    uriString: String,
    configBlock: (Builder.() -> Unit)? = null
): Builder = Builder(context, uriString).apply {
    configBlock?.invoke(this)
}

interface DownloadRequest : ImageRequest {

    val networkContentDiskCacheKey: String

    val httpHeaders: HttpHeaders?
    val networkContentDiskCachePolicy: CachePolicy?
    val progressListener: ProgressListener<ImageRequest>?

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

    open class Builder(context: Context, private val uriString: String) {

        private val context: Context = context.applicationContext
        private var depth: RequestDepth? = null
        private var parametersBuilder: Parameters.Builder? = null
        private var listener: Listener<ImageRequest, ImageResult, ImageResult>? = null

        private var httpHeaders: HttpHeaders.Builder? = null
        private var networkContentDiskCachePolicy: CachePolicy? = null
        private var progressListener: ProgressListener<ImageRequest>? = null

        internal constructor(request: DownloadRequest, context: Context = request.context)
                : this(context, request.uriString) {
            this.depth = request.depth
            this.parametersBuilder = request.parameters?.newBuilder()
            this.listener = request.listener

            this.httpHeaders = request.httpHeaders?.newBuilder()
            this.networkContentDiskCachePolicy = request.networkContentDiskCachePolicy
            this.progressListener = request.progressListener
        }

        fun options(options: DownloadOptions, requestFirst: Boolean = false): Builder = apply {
            if (!requestFirst || this.depth == null) {
                options.depth?.let {
                    this.depth = it
                }
            }
            options.parameters?.takeIf { it.isNotEmpty() }?.let {
                it.forEach { entry ->
                    if (!requestFirst || parametersBuilder?.exist(entry.first) != true) {
                        setParameter(entry.first, entry.second.value, entry.second.cacheKey)
                    }
                }
            }
            options.httpHeaders?.takeIf { !it.isEmpty() }?.let { headers ->
                headers.addList.forEach {
                    addHttpHeader(it.first, it.second)
                }
                headers.setList.forEach {
                    if (!requestFirst || httpHeaders?.setExist(it.first) != true) {
                        setHttpHeader(it.first, it.second)
                    }
                }
            }
            if (!requestFirst || this.networkContentDiskCachePolicy == null) {
                options.networkContentDiskCachePolicy?.let {
                    this.networkContentDiskCachePolicy = it
                }
            }
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

        fun listener(listener: Listener<DownloadRequest, DownloadResult.Success, DownloadResult.Error>?): Builder =
            apply {
                @Suppress("UNCHECKED_CAST")
                this.listener = listener as Listener<ImageRequest, ImageResult, ImageResult>?
            }

        /**
         * Convenience function to create and set the [Listener].
         */
        inline fun listener(
            crossinline onStart: (request: DownloadRequest) -> Unit = {},
            crossinline onCancel: (request: DownloadRequest) -> Unit = {},
            crossinline onError: (request: DownloadRequest, result: DownloadResult.Error) -> Unit = { _, _ -> },
            crossinline onSuccess: (request: DownloadRequest, result: DownloadResult.Success) -> Unit = { _, _ -> }
        ): Builder = listener(object :
            Listener<DownloadRequest, DownloadResult.Success, DownloadResult.Error> {
            override fun onStart(request: DownloadRequest) = onStart(request)
            override fun onCancel(request: DownloadRequest) = onCancel(request)
            override fun onError(request: DownloadRequest, result: DownloadResult.Error) =
                onError(request, result)

            override fun onSuccess(request: DownloadRequest, result: DownloadResult.Success) =
                onSuccess(request, result)
        })

        fun progressListener(progressListener: ProgressListener<DownloadRequest>?): Builder =
            apply {
                @Suppress("UNCHECKED_CAST")
                this.progressListener =
                    progressListener as ProgressListener<ImageRequest>?
            }

        fun build(): DownloadRequest = DownloadRequestImpl(
            context = context,
            uriString = uriString,
            depth = depth,
            parameters = parametersBuilder?.build(),
            httpHeaders = httpHeaders?.build(),
            networkContentDiskCachePolicy = networkContentDiskCachePolicy,
            listener = listener,
            progressListener = progressListener,
        )
    }

    private class DownloadRequestImpl(
        override val context: Context,
        override val uriString: String,
        override val depth: RequestDepth?,
        override val parameters: Parameters?,
        override val httpHeaders: HttpHeaders?,
        override val networkContentDiskCachePolicy: CachePolicy?,
        override val listener: Listener<ImageRequest, ImageResult, ImageResult>?,
        override val progressListener: ProgressListener<ImageRequest>?,
    ) : DownloadRequest {

        override val uri: Uri by lazy { Uri.parse(uriString) }

        override val networkContentDiskCacheKey: String = uriString

        override val key: String by lazy {
            buildString {
                append("Download")
                append("_").append(uriString)
                depth?.let {
                    append("_").append("RequestDepth(${it})")
                }
                parameters?.key?.takeIf { it.isNotEmpty() }?.let {
                    append("_").append(it)
                }
                httpHeaders?.takeIf { !it.isEmpty() }?.let {
                    append("_").append(it)
                }
                networkContentDiskCachePolicy?.let {
                    append("_").append("networkContentDiskCachePolicy($it)")
                }
            }
        }

        override fun toString(): String = key
    }
}