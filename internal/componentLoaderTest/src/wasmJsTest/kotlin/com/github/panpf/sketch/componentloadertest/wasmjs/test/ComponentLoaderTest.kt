package com.github.panpf.sketch.componentloadertest.wasmjs.test

import com.github.panpf.sketch.decode.AnimatedWebpDecoder
import com.github.panpf.sketch.decode.GifDecoder
import com.github.panpf.sketch.decode.SvgDecoder
import com.github.panpf.sketch.decode.internal.AnimatedWebpDecoderProvider
import com.github.panpf.sketch.decode.internal.GifDecoderProvider
import com.github.panpf.sketch.decode.internal.SvgDecoderProvider
import com.github.panpf.sketch.fetch.ComposeResourceUriFetcher
import com.github.panpf.sketch.fetch.KtorHttpUriFetcher
import com.github.panpf.sketch.fetch.internal.ComposeResourceUriFetcherProvider
import com.github.panpf.sketch.fetch.internal.KtorHttpUriFetcherProvider
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.ComponentLoader
import com.github.panpf.sketch.util.toComponentRegistry
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ComponentLoaderTest {

    @Test
    fun testFetchers() {
        val fetcherProviderList = ComponentLoader.fetchers
        assertEquals(2, fetcherProviderList.size)
        assertNotNull(fetcherProviderList.find { it is ComposeResourceUriFetcherProvider })
        assertNotNull(fetcherProviderList.find { it is KtorHttpUriFetcherProvider })
    }

    @Test
    fun testDecoders() {
        val decoderProviderList = ComponentLoader.decoders
        assertEquals(3, decoderProviderList.size)
        assertNotNull(decoderProviderList.find { it is GifDecoderProvider })
        assertNotNull(decoderProviderList.find { it is AnimatedWebpDecoderProvider })
        assertNotNull(decoderProviderList.find { it is SvgDecoderProvider })
    }

    @Test
    fun testToComponentRegistry() {
        val context = getTestContext()
        val componentLoader = ComponentLoader
        val componentRegistry = componentLoader.toComponentRegistry(context)

        assertEquals(2, componentRegistry.fetcherFactoryList.size)
        assertNotNull(componentRegistry.fetcherFactoryList.find { it is ComposeResourceUriFetcher.Factory })
        assertNotNull(componentRegistry.fetcherFactoryList.find { it is KtorHttpUriFetcher.Factory })

        assertEquals(3, componentRegistry.decoderFactoryList.size)
        assertNotNull(componentRegistry.decoderFactoryList.find { it is GifDecoder.Factory })
        assertNotNull(componentRegistry.decoderFactoryList.find { it is AnimatedWebpDecoder.Factory })
        assertNotNull(componentRegistry.decoderFactoryList.find { it is SvgDecoder.Factory })

        // ignoreProviderClasses
        val componentRegistry2 = componentLoader.toComponentRegistry(
            context = context,
            ignoreFetcherProviders = listOf(ComposeResourceUriFetcherProvider::class),
            ignoreDecoderProviders = listOf(SvgDecoderProvider::class),
        )

        assertEquals(1, componentRegistry2.fetcherFactoryList.size)
        assertNull(componentRegistry2.fetcherFactoryList.find { it is ComposeResourceUriFetcher.Factory })
        assertNotNull(componentRegistry2.fetcherFactoryList.find { it is KtorHttpUriFetcher.Factory })

        assertEquals(2, componentRegistry2.decoderFactoryList.size)
        assertNotNull(componentRegistry2.decoderFactoryList.find { it is GifDecoder.Factory })
        assertNotNull(componentRegistry2.decoderFactoryList.find { it is AnimatedWebpDecoder.Factory })
        assertNull(componentRegistry2.decoderFactoryList.find { it is SvgDecoder.Factory })
    }
}