package com.github.panpf.sketch.common

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.common.datasource.DataSource
import com.github.panpf.sketch.common.decode.Decoder
import com.github.panpf.sketch.common.fetch.Fetcher

class ComponentRegistry private constructor(
    val fetcherFactoryList: List<Fetcher.Factory>,
    val decoderFactoryList: List<Decoder.Factory>,
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

    fun newFetcher(
        sketch: Sketch,
        request: ImageRequest,
        extras: RequestExtras<in ImageRequest, in ImageResult>?
    ): Fetcher = fetcherFactoryList.firstNotNullOfOrNull {
        it.create(sketch, request, extras)
    } ?: throw IllegalArgumentException("Unsupported uri: ${request.uri}")

    fun newDecoder(
        sketch: Sketch,
        request: ImageRequest,
        extras: RequestExtras<in ImageRequest, in ImageResult>?,
        dataSource: DataSource,
    ): Decoder = decoderFactoryList.firstNotNullOfOrNull {
        it.create(sketch, request, extras, dataSource)
    } ?: throw IllegalArgumentException("Unsupported image format: ${request.uri}")

    companion object {
        fun new(
            configBlock: (Builder.() -> Unit)? = null
        ): ComponentRegistry = Builder().apply {
            configBlock?.invoke(this)
        }.build()
    }

    class Builder {
        private val fetcherFactoryList: MutableList<Fetcher.Factory>
        private val decoderFactoryList: MutableList<Decoder.Factory>

        constructor() {
            this.fetcherFactoryList = mutableListOf()
            this.decoderFactoryList = mutableListOf()
        }

        constructor(componentRegistry: ComponentRegistry) {
            this.fetcherFactoryList = componentRegistry.fetcherFactoryList.toMutableList()
            this.decoderFactoryList = componentRegistry.decoderFactoryList.toMutableList()
        }

        fun addFetcher(factory: Fetcher.Factory) {
            fetcherFactoryList.add(factory)
        }

        fun addDecoder(decoder: Decoder.Factory) {
            decoderFactoryList.add(decoder)
        }

        fun build(): ComponentRegistry = ComponentRegistry(
            fetcherFactoryList.toList(),
            decoderFactoryList.toList(),
        )
    }
}