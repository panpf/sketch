package com.github.panpf.sketch

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.decode.BitmapDecoder
import com.github.panpf.sketch.decode.DrawableDecoder
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.Fetcher
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.util.requiredWorkThread

class ComponentRegistry private constructor(
    val fetcherFactoryList: List<Fetcher.Factory>,
    val bitmapDecoderFactoryList: List<BitmapDecoder.Factory>,
    val drawableDecoderFactoryList: List<DrawableDecoder.Factory>,
) {

    fun newBuilder(
        configBlock: (Builder.() -> Unit)? = null
    ): Builder = Builder(this).apply {
        configBlock?.invoke(this)
    }

    fun new(
        configBlock: (Builder.() -> Unit)? = null
    ): ComponentRegistry = Builder(this).apply {
        configBlock?.invoke(this)
    }.build()

    @WorkerThread
    internal fun newFetcher(sketch: Sketch, request: ImageRequest): Fetcher {
        requiredWorkThread()
        return fetcherFactoryList.firstNotNullOfOrNull {
            it.create(sketch, request)
        } ?: throw IllegalArgumentException(
            "No Fetcher can handle this uri '${request.uriString}', " +
                    "please pass ComponentRegistry. Builder addFetcher () function to add a new Fetcher to support it"
        )
    }

    @WorkerThread
    internal fun newBitmapDecoder(
        sketch: Sketch,
        request: ImageRequest,
        requestContext: RequestContext,
        fetchResult: FetchResult,
    ): BitmapDecoder {
        requiredWorkThread()
        return bitmapDecoderFactoryList.firstNotNullOfOrNull {
            it.create(sketch, request, requestContext, fetchResult)
        } ?: throw IllegalArgumentException(
            "No BitmapDecoder can handle this uri '${request.uriString}', " +
                    "please pass ComponentRegistry.Builder.addBitmapDecoder() function to add a new BitmapDecoder to support it"
        )
    }

    @WorkerThread
    internal fun newDrawableDecoder(
        sketch: Sketch,
        request: ImageRequest,
        requestContext: RequestContext,
        fetchResult: FetchResult,
    ): DrawableDecoder {
        requiredWorkThread()
        return drawableDecoderFactoryList.firstNotNullOfOrNull {
            it.create(sketch, request, requestContext, fetchResult)
        } ?: throw IllegalArgumentException(
            "No DrawableDecoder can handle this uri '${request.uriString}', " +
                    "please pass ComponentRegistry.Builder.addDrawableDecoder() function to add a new DrawableDecoder to support it"
        )
    }

    override fun toString(): String =
        "ComponentRegistry(fetcherFactoryList=${
            fetcherFactoryList.joinToString(
                prefix = "[",
                postfix = "]",
                separator = ","
            )
        }," +
                "bitmapDecoderFactoryList=${
                    bitmapDecoderFactoryList.joinToString(
                        prefix = "[",
                        postfix = "]",
                        separator = ","
                    )
                }," +
                "drawableDecoderFactoryList=${
                    drawableDecoderFactoryList.joinToString(
                        prefix = "[",
                        postfix = "]",
                        separator = ","
                    )
                })"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ComponentRegistry

        if (fetcherFactoryList != other.fetcherFactoryList) return false
        if (bitmapDecoderFactoryList != other.bitmapDecoderFactoryList) return false
        if (drawableDecoderFactoryList != other.drawableDecoderFactoryList) return false

        return true
    }

    override fun hashCode(): Int {
        var result = fetcherFactoryList.hashCode()
        result = 31 * result + bitmapDecoderFactoryList.hashCode()
        result = 31 * result + drawableDecoderFactoryList.hashCode()
        return result
    }

    companion object {
        fun new(
            configBlock: (Builder.() -> Unit)? = null
        ): ComponentRegistry = Builder().apply {
            configBlock?.invoke(this)
        }.build()
    }

    class Builder {
        private val fetcherFactoryList: MutableList<Fetcher.Factory>
        private val bitmapDecoderFactoryList: MutableList<BitmapDecoder.Factory>
        private val drawableDecoderFactoryList: MutableList<DrawableDecoder.Factory>

        constructor() {
            this.fetcherFactoryList = mutableListOf()
            this.bitmapDecoderFactoryList = mutableListOf()
            this.drawableDecoderFactoryList = mutableListOf()
        }

        constructor(componentRegistry: ComponentRegistry) {
            this.fetcherFactoryList = componentRegistry.fetcherFactoryList.toMutableList()
            this.bitmapDecoderFactoryList =
                componentRegistry.bitmapDecoderFactoryList.toMutableList()
            this.drawableDecoderFactoryList =
                componentRegistry.drawableDecoderFactoryList.toMutableList()
        }

        fun addFetcher(fetchFactory: Fetcher.Factory) {
            fetcherFactoryList.add(fetchFactory)
        }

        fun addBitmapDecoder(bitmapDecoderFactory: BitmapDecoder.Factory) {
            bitmapDecoderFactoryList.add(bitmapDecoderFactory)
        }

        fun addDrawableDecoder(drawableDecoderFactory: DrawableDecoder.Factory) {
            drawableDecoderFactoryList.add(drawableDecoderFactory)
        }

        fun build(): ComponentRegistry = ComponentRegistry(
            fetcherFactoryList = fetcherFactoryList.toList(),
            bitmapDecoderFactoryList = bitmapDecoderFactoryList.toList(),
            drawableDecoderFactoryList = drawableDecoderFactoryList.toList(),
        )
    }
}

class ComponentService(private val sketch: Sketch, internal val registry: ComponentRegistry) {

    @WorkerThread
    fun newFetcher(request: ImageRequest): Fetcher = registry.newFetcher(sketch, request)

    @WorkerThread
    fun newBitmapDecoder(
        request: ImageRequest,
        requestContext: RequestContext,
        fetchResult: FetchResult,
    ): BitmapDecoder = registry.newBitmapDecoder(sketch, request, requestContext, fetchResult)

    @WorkerThread
    fun newDrawableDecoder(
        request: ImageRequest,
        requestContext: RequestContext,
        fetchResult: FetchResult,
    ): DrawableDecoder = registry.newDrawableDecoder(sketch, request, requestContext, fetchResult)
}