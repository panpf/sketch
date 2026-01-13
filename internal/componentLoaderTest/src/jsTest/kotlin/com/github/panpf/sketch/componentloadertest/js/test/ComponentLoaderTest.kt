package com.github.panpf.sketch.componentloadertest.js.test

import com.github.panpf.sketch.decode.AnimatedWebpDecoder
import com.github.panpf.sketch.decode.BlurHashDecoder
import com.github.panpf.sketch.decode.GifDecoder
import com.github.panpf.sketch.decode.SvgDecoder
import com.github.panpf.sketch.decode.internal.AnimatedWebpDecoderProvider
import com.github.panpf.sketch.decode.internal.BlurHashDecoderProvider
import com.github.panpf.sketch.decode.internal.GifDecoderProvider
import com.github.panpf.sketch.decode.internal.SvgDecoderProvider
import com.github.panpf.sketch.fetch.BlurHashUriFetcher
import com.github.panpf.sketch.fetch.ComposeResourceUriFetcher
import com.github.panpf.sketch.fetch.KtorHttpUriFetcher
import com.github.panpf.sketch.fetch.internal.BlurHashUriFetcherProvider
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
        assertEquals(3, fetcherProviderList.size)
        assertNotNull(fetcherProviderList.find { it is ComposeResourceUriFetcherProvider })
        assertNotNull(fetcherProviderList.find { it is KtorHttpUriFetcherProvider })
        assertNotNull(fetcherProviderList.find { it is BlurHashUriFetcherProvider })
    }

    @Test
    fun testDecoders() {
        val decoderProviderList = ComponentLoader.decoders
        assertEquals(4, decoderProviderList.size)
        assertNotNull(decoderProviderList.find { it is GifDecoderProvider })
        assertNotNull(decoderProviderList.find { it is AnimatedWebpDecoderProvider })
        assertNotNull(decoderProviderList.find { it is SvgDecoderProvider })
        assertNotNull(decoderProviderList.find { it is BlurHashDecoderProvider })
    }

    @Test
    fun testToComponentRegistry() {
        val context = getTestContext()
        val componentLoader = ComponentLoader
        val componentRegistry = componentLoader.toComponentRegistry(context)

        assertEquals(3, componentRegistry.fetchers.size)
        assertNotNull(componentRegistry.fetchers.find { it is ComposeResourceUriFetcher.Factory })
        assertNotNull(componentRegistry.fetchers.find { it is KtorHttpUriFetcher.Factory })
        assertNotNull(componentRegistry.fetchers.find { it is BlurHashUriFetcher.Factory })

        assertEquals(4, componentRegistry.decoders.size)
        assertNotNull(componentRegistry.decoders.find { it is GifDecoder.Factory })
        assertNotNull(componentRegistry.decoders.find { it is AnimatedWebpDecoder.Factory })
        assertNotNull(componentRegistry.decoders.find { it is SvgDecoder.Factory })
        assertNotNull(componentRegistry.decoders.find { it is BlurHashDecoder.Factory })

        // ignoreProviderClasses
        val componentRegistry2 = componentLoader.toComponentRegistry(
            context = context,
            ignoreFetcherProviders = listOf(ComposeResourceUriFetcherProvider::class),
            ignoreDecoderProviders = listOf(SvgDecoderProvider::class),
        )

        assertEquals(2, componentRegistry2.fetchers.size)
        assertNull(componentRegistry2.fetchers.find { it is ComposeResourceUriFetcher.Factory })
        assertNotNull(componentRegistry2.fetchers.find { it is KtorHttpUriFetcher.Factory })
        assertNotNull(componentRegistry2.fetchers.find { it is BlurHashUriFetcher.Factory })

        assertEquals(3, componentRegistry2.decoders.size)
        assertNotNull(componentRegistry2.decoders.find { it is GifDecoder.Factory })
        assertNotNull(componentRegistry2.decoders.find { it is AnimatedWebpDecoder.Factory })
        assertNull(componentRegistry2.decoders.find { it is SvgDecoder.Factory })
        assertNotNull(componentRegistry2.decoders.find { it is BlurHashDecoder.Factory })
    }
}