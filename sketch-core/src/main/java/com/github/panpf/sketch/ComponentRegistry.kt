package com.github.panpf.sketch

import com.github.panpf.sketch.request.internal.ImageRequest
import com.github.panpf.sketch.request.internal.ImageResult
import com.github.panpf.sketch.request.ListenerInfo
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.fetch.Fetcher

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
        listenerInfo: ListenerInfo<in ImageRequest, in ImageResult>?
    ): Fetcher = fetcherFactoryList.firstNotNullOfOrNull {
        it.create(sketch, request, listenerInfo)
    } ?: throw IllegalArgumentException("Unsupported uri: ${request.uri}")

    fun newDecoder(
        sketch: Sketch,
        request: ImageRequest,
        listenerInfo: ListenerInfo<in ImageRequest, in ImageResult>?,
        dataSource: DataSource,
    ): Decoder = decoderFactoryList.firstNotNullOfOrNull {
        it.create(sketch, request, listenerInfo, dataSource)
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