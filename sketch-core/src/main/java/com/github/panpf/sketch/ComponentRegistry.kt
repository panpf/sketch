package com.github.panpf.sketch

import com.github.panpf.sketch.decode.BitmapDecoder
import com.github.panpf.sketch.decode.DrawableDecoder
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.Fetcher
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.internal.ImageRequest

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

    fun newFetcher(sketch: Sketch, request: ImageRequest): Fetcher {
        return fetcherFactoryList.firstNotNullOfOrNull {
            it.create(sketch, request)
        } ?: throw IllegalArgumentException(
            "No Fetcher can handle this uri: ${request.uriString}, " +
                    "please pass ComponentRegistry. Builder addFetcher () function to add a new Fetcher to support it"
        )
    }

    fun newBitmapDecoder(
        sketch: Sketch,
        request: LoadRequest,
        fetchResult: FetchResult,
    ): BitmapDecoder = bitmapDecoderFactoryList.firstNotNullOfOrNull {
        it.create(sketch, request, fetchResult)
    } ?: throw IllegalArgumentException(
        "No BitmapDecoder can handle this uri: ${request.uriString}, " +
                "please pass ComponentRegistry.Builder.addBitmapDecoder() function to add a new BitmapDecoder to support it"
    )

    fun newDrawableDecoder(
        sketch: Sketch,
        request: DisplayRequest,
        fetchResult: FetchResult,
    ): DrawableDecoder = drawableDecoderFactoryList.firstNotNullOfOrNull {
        it.create(sketch, request, fetchResult)
    } ?: throw IllegalArgumentException(
        "No DrawableDecoder can handle this uri: ${request.uriString}, " +
                "please pass ComponentRegistry.Builder.addDrawableDecoder() function to add a new DrawableDecoder to support it"
    )

    companion object {
        fun new(
            configBlock: (Builder.() -> Unit)? = null
        ): ComponentRegistry = Builder().apply {
            configBlock?.invoke(this)
        }.build()

        fun newBuilder(
            configBlock: (Builder.() -> Unit)? = null
        ): Builder = Builder().apply {
            configBlock?.invoke(this)
        }
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