package com.github.panpf.sketch3.common

import android.net.Uri
import com.github.panpf.sketch3.Sketch3
import com.github.panpf.sketch3.common.fetch.Fetcher

class ComponentRegistry private constructor(
    val fetcherFactoryList: List<Fetcher.Factory>
) {

    fun newBuilder(): Builder = Builder(this)

    fun newFetcher(uri: Uri, sketch3: Sketch3): Fetcher? {
        fetcherFactoryList.forEach {
            val fetcher = it.create(uri, sketch3)
            if (fetcher != null) {
                return fetcher
            }
        }
        return null
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