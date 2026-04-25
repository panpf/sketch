package com.github.panpf.sketch.componentloadertest.js.test

import com.github.panpf.sketch.decode.BlurHashDecoder
import com.github.panpf.sketch.decode.SkiaAnimatedWebpDecoder
import com.github.panpf.sketch.decode.SkiaGifDecoder
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
import com.github.panpf.sketch.test.utils.DoNothingDecoder
import com.github.panpf.sketch.test.utils.DoNothingDecoderProvider
import com.github.panpf.sketch.test.utils.DoNothingFetcher
import com.github.panpf.sketch.test.utils.DoNothingFetcherProvider
import com.github.panpf.sketch.test.utils.DoNothingInterceptor
import com.github.panpf.sketch.test.utils.DoNothingInterceptorProvider
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
        assertEquals(4, fetcherProviderList.size)
        assertNotNull(fetcherProviderList.find { it is ComposeResourceUriFetcherProvider })
        assertNotNull(fetcherProviderList.find { it is KtorHttpUriFetcherProvider })
        assertNotNull(fetcherProviderList.find { it is BlurHashUriFetcherProvider })
        assertNotNull(fetcherProviderList.find { it is DoNothingFetcherProvider })
    }

    @Test
    fun testDecoders() {
        val decoderProviderList = ComponentLoader.decoders
        assertEquals(5, decoderProviderList.size)
        assertNotNull(decoderProviderList.find { it is GifDecoderProvider })
        assertNotNull(decoderProviderList.find { it is AnimatedWebpDecoderProvider })
        assertNotNull(decoderProviderList.find { it is SvgDecoderProvider })
        assertNotNull(decoderProviderList.find { it is BlurHashDecoderProvider })
        assertNotNull(decoderProviderList.find { it is DoNothingDecoderProvider })
    }

    @Test
    fun testInterceptors() {
        val interceptorProviderList = ComponentLoader.interceptors
        assertEquals(1, interceptorProviderList.size)
        assertNotNull(interceptorProviderList.find { it is DoNothingInterceptorProvider })
    }

    @Test
    fun testToComponentRegistry() {
        val context = getTestContext()
        ComponentLoader.toComponentRegistry(context).apply {
            assertEquals(4, fetchers.size)
            assertNotNull(fetchers.find { it is ComposeResourceUriFetcher.Factory })
            assertNotNull(fetchers.find { it is KtorHttpUriFetcher.Factory })
            assertNotNull(fetchers.find { it is BlurHashUriFetcher.Factory })
            assertNotNull(fetchers.find { it is DoNothingFetcher.Factory })

            assertEquals(5, decoders.size)
            assertNotNull(decoders.find { it is SkiaGifDecoder.Factory })
            assertNotNull(decoders.find { it is SkiaAnimatedWebpDecoder.Factory })
            assertNotNull(decoders.find { it is SvgDecoder.Factory })
            assertNotNull(decoders.find { it is BlurHashDecoder.Factory })
            assertNotNull(decoders.find { it is DoNothingDecoder.Factory })

            assertEquals(1, interceptors.size)
            assertNotNull(interceptors.find { it is DoNothingInterceptor })
        }

        // ignoreProviderClasses
        ComponentLoader.toComponentRegistry(
            context = context,
            ignoreFetcherProviders = listOf(DoNothingFetcherProvider::class),
            ignoreDecoderProviders = listOf(DoNothingDecoderProvider::class),
            ignoreInterceptorProviders = listOf(DoNothingInterceptorProvider::class),
        ).apply {
            assertEquals(3, fetchers.size)
            assertNotNull(fetchers.find { it is ComposeResourceUriFetcher.Factory })
            assertNotNull(fetchers.find { it is KtorHttpUriFetcher.Factory })
            assertNotNull(fetchers.find { it is BlurHashUriFetcher.Factory })
            assertNull(fetchers.find { it is DoNothingFetcher.Factory })

            assertEquals(4, decoders.size)
            assertNotNull(decoders.find { it is SkiaGifDecoder.Factory })
            assertNotNull(decoders.find { it is SkiaAnimatedWebpDecoder.Factory })
            assertNotNull(decoders.find { it is SvgDecoder.Factory })
            assertNotNull(decoders.find { it is BlurHashDecoder.Factory })
            assertNull(decoders.find { it is DoNothingDecoder.Factory })

            assertEquals(0, interceptors.size)
            assertNull(interceptors.find { it is DoNothingInterceptor })
        }
    }
}