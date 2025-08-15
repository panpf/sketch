package com.github.panpf.sketch.componentloadertest.android.test

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import com.github.panpf.sketch.decode.AnimatedWebpDecoder
import com.github.panpf.sketch.decode.ApkIconDecoder
import com.github.panpf.sketch.decode.BlurHashDecoder
import com.github.panpf.sketch.decode.FFmpegVideoFrameDecoder
import com.github.panpf.sketch.decode.GifDecoder
import com.github.panpf.sketch.decode.ImageDecoderAnimatedHeifDecoder
import com.github.panpf.sketch.decode.KoralGifDecoder
import com.github.panpf.sketch.decode.SvgDecoder
import com.github.panpf.sketch.decode.VideoFrameDecoder
import com.github.panpf.sketch.decode.internal.AnimatedWebpDecoderProvider
import com.github.panpf.sketch.decode.internal.ApkIconDecoderProvider
import com.github.panpf.sketch.decode.internal.BlurHashDecoderProvider
import com.github.panpf.sketch.decode.internal.FFmpegVideoFrameDecoderProvider
import com.github.panpf.sketch.decode.internal.GifDecoderProvider
import com.github.panpf.sketch.decode.internal.ImageDecoderAnimatedHeifDecoderProvider
import com.github.panpf.sketch.decode.internal.KoralGifDecoderProvider
import com.github.panpf.sketch.decode.internal.SvgDecoderProvider
import com.github.panpf.sketch.decode.internal.VideoFrameDecoderProvider
import com.github.panpf.sketch.fetch.AppIconUriFetcher
import com.github.panpf.sketch.fetch.BlurHashUriFetcher
import com.github.panpf.sketch.fetch.ComposeResourceUriFetcher
import com.github.panpf.sketch.fetch.HurlHttpUriFetcher
import com.github.panpf.sketch.fetch.KtorHttpUriFetcher
import com.github.panpf.sketch.fetch.OkHttpHttpUriFetcher
import com.github.panpf.sketch.fetch.internal.AppIconUriFetcherProvider
import com.github.panpf.sketch.fetch.internal.BlurHashUriFetcherProvider
import com.github.panpf.sketch.fetch.internal.ComposeResourceUriFetcherProvider
import com.github.panpf.sketch.fetch.internal.HurlHttpUriFetcherProvider
import com.github.panpf.sketch.fetch.internal.KtorHttpUriFetcherProvider
import com.github.panpf.sketch.fetch.internal.OkHttpHttpUriFetcherProvider
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
        assertEquals(6, fetcherProviderList.size)
        assertNotNull(fetcherProviderList.find { it is AppIconUriFetcherProvider })
        assertNotNull(fetcherProviderList.find { it is ComposeResourceUriFetcherProvider })
        assertNotNull(fetcherProviderList.find { it is KtorHttpUriFetcherProvider })
        assertNotNull(fetcherProviderList.find { it is HurlHttpUriFetcherProvider })
        assertNotNull(fetcherProviderList.find { it is OkHttpHttpUriFetcherProvider })
        assertNotNull(fetcherProviderList.find { it is BlurHashUriFetcherProvider })
    }

    @Test
    fun testDecoders() {
        val decoderProviderList = ComponentLoader.decoders
        assertEquals(9, decoderProviderList.size)
        assertNotNull(decoderProviderList.find { it is KoralGifDecoderProvider })
        assertNotNull(decoderProviderList.find { it is ImageDecoderAnimatedHeifDecoderProvider })
        assertNotNull(decoderProviderList.find { it is ApkIconDecoderProvider })
        assertNotNull(decoderProviderList.find { it is VideoFrameDecoderProvider })
        assertNotNull(decoderProviderList.find { it is FFmpegVideoFrameDecoderProvider })
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

        assertEquals(6, componentRegistry.fetcherFactoryList.size)
        assertNotNull(componentRegistry.fetcherFactoryList.find { it is AppIconUriFetcher.Factory })
        assertNotNull(componentRegistry.fetcherFactoryList.find { it is ComposeResourceUriFetcher.Factory })
        assertNotNull(componentRegistry.fetcherFactoryList.find { it is KtorHttpUriFetcher.Factory })
        assertNotNull(componentRegistry.fetcherFactoryList.find { it is HurlHttpUriFetcher.Factory })
        assertNotNull(componentRegistry.fetcherFactoryList.find { it is OkHttpHttpUriFetcher.Factory })
        assertNotNull(componentRegistry.fetcherFactoryList.find { it is BlurHashUriFetcher.Factory })

        if (VERSION.SDK_INT >= VERSION_CODES.R) {
            assertEquals(9, componentRegistry.decoderFactoryList.size)
            assertNotNull(componentRegistry.decoderFactoryList.find { it is ImageDecoderAnimatedHeifDecoder.Factory })
        } else {
            assertEquals(8, componentRegistry.decoderFactoryList.size)
            assertNull(componentRegistry.decoderFactoryList.find { it is ImageDecoderAnimatedHeifDecoder.Factory })
        }
        assertNotNull(componentRegistry.decoderFactoryList.find { it is KoralGifDecoder.Factory })
        assertNotNull(componentRegistry.decoderFactoryList.find { it is ApkIconDecoder.Factory })
        assertNotNull(componentRegistry.decoderFactoryList.find { it is VideoFrameDecoder.Factory })
        assertNotNull(componentRegistry.decoderFactoryList.find { it is FFmpegVideoFrameDecoder.Factory })
        assertNotNull(componentRegistry.decoderFactoryList.find { it is GifDecoder.Factory })
        assertNotNull(componentRegistry.decoderFactoryList.find { it is AnimatedWebpDecoder.Factory })
        assertNotNull(componentRegistry.decoderFactoryList.find { it is SvgDecoder.Factory })
        assertNotNull(componentRegistry.decoderFactoryList.find { it is BlurHashDecoder.Factory })

        // ignoreProviderClasses
        val componentRegistry2 = componentLoader.toComponentRegistry(
            context = context,
            ignoreFetcherProviders = listOf(ComposeResourceUriFetcherProvider::class),
            ignoreDecoderProviders = listOf(SvgDecoderProvider::class),
        )

        assertEquals(5, componentRegistry2.fetcherFactoryList.size)
        assertNotNull(componentRegistry2.fetcherFactoryList.find { it is AppIconUriFetcher.Factory })
        assertNull(componentRegistry2.fetcherFactoryList.find { it is ComposeResourceUriFetcher.Factory })
        assertNotNull(componentRegistry2.fetcherFactoryList.find { it is KtorHttpUriFetcher.Factory })
        assertNotNull(componentRegistry2.fetcherFactoryList.find { it is HurlHttpUriFetcher.Factory })
        assertNotNull(componentRegistry2.fetcherFactoryList.find { it is OkHttpHttpUriFetcher.Factory })
        assertNotNull(componentRegistry2.fetcherFactoryList.find { it is BlurHashUriFetcher.Factory })

        if (VERSION.SDK_INT >= VERSION_CODES.R) {
            assertEquals(8, componentRegistry2.decoderFactoryList.size)
            assertNotNull(componentRegistry2.decoderFactoryList.find { it is ImageDecoderAnimatedHeifDecoder.Factory })
        } else {
            assertEquals(7, componentRegistry2.decoderFactoryList.size)
            assertNull(componentRegistry2.decoderFactoryList.find { it is ImageDecoderAnimatedHeifDecoder.Factory })
        }
        assertNotNull(componentRegistry2.decoderFactoryList.find { it is KoralGifDecoder.Factory })
        assertNotNull(componentRegistry2.decoderFactoryList.find { it is ApkIconDecoder.Factory })
        assertNotNull(componentRegistry2.decoderFactoryList.find { it is VideoFrameDecoder.Factory })
        assertNotNull(componentRegistry2.decoderFactoryList.find { it is FFmpegVideoFrameDecoder.Factory })
        assertNotNull(componentRegistry2.decoderFactoryList.find { it is GifDecoder.Factory })
        assertNotNull(componentRegistry2.decoderFactoryList.find { it is AnimatedWebpDecoder.Factory })
        assertNull(componentRegistry2.decoderFactoryList.find { it is SvgDecoder.Factory })
        assertNotNull(componentRegistry2.decoderFactoryList.find { it is BlurHashDecoder.Factory })
    }
}