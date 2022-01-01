package com.github.panpf.sketch

import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.fetch.Fetcher
import com.github.panpf.sketch.request.internal.ImageRequest
import com.github.panpf.sketch.request.internal.ProgressListenerDelegate

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
        httpFetchProgressListenerDelegate: ProgressListenerDelegate<ImageRequest>?
    ): Fetcher = fetcherFactoryList.firstNotNullOfOrNull {
        it.create(sketch, request, httpFetchProgressListenerDelegate)
    } ?: throw IllegalArgumentException(
        "No Fetcher can handle this uri: ${request.uri}, " +
                "please pass ComponentRegistry. Builder addFetcher () function to add a new Fetcher to support it"
    )

    fun newDecoder(
        sketch: Sketch,
        request: ImageRequest,
        dataSource: DataSource,
    ): Decoder = decoderFactoryList.firstNotNullOfOrNull {
        it.create(sketch, request, dataSource)
    } ?: throw IllegalArgumentException(
        "No Decoder can handle this uri: ${request.uri}, " +
                "please pass ComponentRegistry. Builder addDecoder () function to add a new Decoder to support it"
    )

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