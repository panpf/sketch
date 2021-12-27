package com.github.panpf.sketch.common

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.common.fetch.Fetcher

class ComponentRegistry private constructor(
    val fetcherFactoryList: List<Fetcher.Factory>
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
        extras: RequestExtras<in ImageRequest, in ImageData>?
    ): Fetcher = fetcherFactoryList.firstNotNullOfOrNull {
        it.create(sketch, request, extras)
    } ?: throw IllegalArgumentException("Unsupported uri: ${request.uri}")

    companion object {
        fun new(
            configBlock: (Builder.() -> Unit)? = null
        ): ComponentRegistry = Builder().apply {
            configBlock?.invoke(this)
        }.build()
    }

    class Builder {
        private val fetcherFactoryList: MutableList<Fetcher.Factory>

        constructor() {
            this.fetcherFactoryList = mutableListOf()
        }

        constructor(componentRegistry: ComponentRegistry) {
            this.fetcherFactoryList = componentRegistry.fetcherFactoryList.toMutableList()
        }

        fun addFetcher(factory: Fetcher.Factory) {
            fetcherFactoryList.add(factory)
        }

        fun build(): ComponentRegistry = ComponentRegistry(fetcherFactoryList.toList())
    }
}