package com.github.panpf.sketch.componentloadertest.android.test

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import com.github.panpf.sketch.decode.ApkIconDecoder
import com.github.panpf.sketch.decode.BlurHashDecoder
import com.github.panpf.sketch.decode.FFmpegVideoFrameDecoder
import com.github.panpf.sketch.decode.ImageDecoderAnimatedHeifDecoder
import com.github.panpf.sketch.decode.ImageDecoderAnimatedWebpDecoder
import com.github.panpf.sketch.decode.ImageDecoderGifDecoder
import com.github.panpf.sketch.decode.KoralGifDecoder
import com.github.panpf.sketch.decode.MovieGifDecoder
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
        assertEquals(7, fetcherProviderList.size)
        assertNotNull(fetcherProviderList.find { it is AppIconUriFetcherProvider })
        assertNotNull(fetcherProviderList.find { it is ComposeResourceUriFetcherProvider })
        assertNotNull(fetcherProviderList.find { it is KtorHttpUriFetcherProvider })
        assertNotNull(fetcherProviderList.find { it is HurlHttpUriFetcherProvider })
        assertNotNull(fetcherProviderList.find { it is OkHttpHttpUriFetcherProvider })
        assertNotNull(fetcherProviderList.find { it is BlurHashUriFetcherProvider })
        assertNotNull(fetcherProviderList.find { it is DoNothingFetcherProvider })
    }

    @Test
    fun testDecoders() {
        val decoderProviderList = ComponentLoader.decoders
        assertEquals(10, decoderProviderList.size)
        assertNotNull(decoderProviderList.find { it is KoralGifDecoderProvider })
        assertNotNull(decoderProviderList.find { it is ImageDecoderAnimatedHeifDecoderProvider })
        assertNotNull(decoderProviderList.find { it is ApkIconDecoderProvider })
        assertNotNull(decoderProviderList.find { it is VideoFrameDecoderProvider })
        assertNotNull(decoderProviderList.find { it is FFmpegVideoFrameDecoderProvider })
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
            assertEquals(7, fetchers.size)
            assertNotNull(fetchers.find { it is AppIconUriFetcher.Factory })
            assertNotNull(fetchers.find { it is ComposeResourceUriFetcher.Factory })
            assertNotNull(fetchers.find { it is KtorHttpUriFetcher.Factory })
            assertNotNull(fetchers.find { it is HurlHttpUriFetcher.Factory })
            assertNotNull(fetchers.find { it is OkHttpHttpUriFetcher.Factory })
            assertNotNull(fetchers.find { it is BlurHashUriFetcher.Factory })
            assertNotNull(fetchers.find { it is DoNothingFetcher.Factory })

            var expectedDecoderSize = 10
            if (VERSION.SDK_INT < VERSION_CODES.P) {
                expectedDecoderSize--   // ImageDecoderAnimatedWebpDecoder
            }
            if (VERSION.SDK_INT < VERSION_CODES.R) {
                expectedDecoderSize--   // ImageDecoderAnimatedHeifDecoder
            }
            assertEquals(expectedDecoderSize, decoders.size)
            assertNotNull(decoders.find { it is KoralGifDecoder.Factory })
            assertNotNull(decoders.find { it is ApkIconDecoder.Factory })
            assertNotNull(decoders.find { it is VideoFrameDecoder.Factory })
            assertNotNull(decoders.find { it is FFmpegVideoFrameDecoder.Factory })
            if (VERSION.SDK_INT >= VERSION_CODES.P) {
                assertNotNull(decoders.find { it is ImageDecoderGifDecoder.Factory })
            } else {
                assertNotNull(decoders.find { it is MovieGifDecoder.Factory })
            }
            if (VERSION.SDK_INT >= VERSION_CODES.P) {
                assertNotNull(decoders.find { it is ImageDecoderAnimatedWebpDecoder.Factory })
            } else {
                assertNull(decoders.find { it is ImageDecoderAnimatedWebpDecoder.Factory })
            }
            if (VERSION.SDK_INT >= VERSION_CODES.R) {
                assertNotNull(decoders.find { it is ImageDecoderAnimatedHeifDecoder.Factory })
            } else {
                assertNull(decoders.find { it is ImageDecoderAnimatedHeifDecoder.Factory })
            }
            assertNotNull(decoders.find { it is SvgDecoder.Factory })
            assertNotNull(decoders.find { it is BlurHashDecoder.Factory })
            assertNotNull(decoders.find { it is DoNothingDecoder.Factory })

            assertEquals(1, interceptors.size)
            assertNotNull(interceptors.find { it is DoNothingInterceptor })
        }

        /*
         * ignoreProviderClasses
         */
        ComponentLoader.toComponentRegistry(
            context = context,
            ignoreFetcherProviders = listOf(DoNothingFetcherProvider::class),
            ignoreDecoderProviders = listOf(DoNothingDecoderProvider::class),
            ignoreInterceptorProviders = listOf(DoNothingInterceptorProvider::class),
        ).apply {
            assertEquals(6, fetchers.size)
            assertNotNull(fetchers.find { it is AppIconUriFetcher.Factory })
            assertNotNull(fetchers.find { it is ComposeResourceUriFetcher.Factory })
            assertNotNull(fetchers.find { it is KtorHttpUriFetcher.Factory })
            assertNotNull(fetchers.find { it is HurlHttpUriFetcher.Factory })
            assertNotNull(fetchers.find { it is OkHttpHttpUriFetcher.Factory })
            assertNotNull(fetchers.find { it is BlurHashUriFetcher.Factory })
            assertNull(fetchers.find { it is DoNothingFetcher.Factory })

            var expectedDecoderSize2 = 9
            if (VERSION.SDK_INT < VERSION_CODES.P) {
                expectedDecoderSize2--   // ImageDecoderAnimatedWebpDecoder
            }
            if (VERSION.SDK_INT < VERSION_CODES.R) {
                expectedDecoderSize2--   // ImageDecoderAnimatedHeifDecoder
            }
            assertEquals(expectedDecoderSize2, decoders.size)
            assertNotNull(decoders.find { it is KoralGifDecoder.Factory })
            assertNotNull(decoders.find { it is ApkIconDecoder.Factory })
            assertNotNull(decoders.find { it is VideoFrameDecoder.Factory })
            assertNotNull(decoders.find { it is FFmpegVideoFrameDecoder.Factory })
            if (VERSION.SDK_INT >= VERSION_CODES.P) {
                assertNotNull(decoders.find { it is ImageDecoderGifDecoder.Factory })
            } else {
                assertNotNull(decoders.find { it is MovieGifDecoder.Factory })
            }
            if (VERSION.SDK_INT >= VERSION_CODES.P) {
                assertNotNull(decoders.find { it is ImageDecoderAnimatedWebpDecoder.Factory })
            } else {
                assertNull(decoders.find { it is ImageDecoderAnimatedWebpDecoder.Factory })
            }
            if (VERSION.SDK_INT >= VERSION_CODES.R) {
                assertNotNull(decoders.find { it is ImageDecoderAnimatedHeifDecoder.Factory })
            } else {
                assertNull(decoders.find { it is ImageDecoderAnimatedHeifDecoder.Factory })
            }
            assertNotNull(decoders.find { it is SvgDecoder.Factory })
            assertNotNull(decoders.find { it is BlurHashDecoder.Factory })
            assertNull(decoders.find { it is DoNothingDecoder.Factory })

            assertEquals(0, interceptors.size)
            assertNull(interceptors.find { it is DoNothingInterceptor })
        }
    }
}