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
import com.github.panpf.sketch.fetch.AppIconUriFetcher
import com.github.panpf.sketch.fetch.BlurHashUriFetcher
import com.github.panpf.sketch.fetch.ComposeResourceUriFetcher
import com.github.panpf.sketch.fetch.HurlHttpUriFetcher
import com.github.panpf.sketch.fetch.KtorHttpUriFetcher
import com.github.panpf.sketch.fetch.OkHttpHttpUriFetcher
import com.github.panpf.sketch.test.utils.DoNothingComponentProvider
import com.github.panpf.sketch.test.utils.DoNothingDecoder
import com.github.panpf.sketch.test.utils.DoNothingFetcher
import com.github.panpf.sketch.test.utils.DoNothingInterceptor
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.AnimatedWebpComponentProvider
import com.github.panpf.sketch.util.ApkIconComponentProvider
import com.github.panpf.sketch.util.AppIconComponentProvider
import com.github.panpf.sketch.util.BlurHashComponentProvider
import com.github.panpf.sketch.util.ComponentLoader
import com.github.panpf.sketch.util.ComposeResourceComponentProvider
import com.github.panpf.sketch.util.FFmpegVideoComponentProvider
import com.github.panpf.sketch.util.GifComponentProvider
import com.github.panpf.sketch.util.HurlHttpComponentProvider
import com.github.panpf.sketch.util.ImageDecoderAnimatedHeifComponentProvider
import com.github.panpf.sketch.util.KoralGifComponentProvider
import com.github.panpf.sketch.util.KtorHttpComponentProvider
import com.github.panpf.sketch.util.OkHttpHttpComponentProvider
import com.github.panpf.sketch.util.SvgComponentProvider
import com.github.panpf.sketch.util.VideoComponentProvider
import com.github.panpf.sketch.util.toComponentRegistry
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ComponentLoaderTest {

    @Test
    fun testComponentProviders() {
        val componentProviders = ComponentLoader.componentProviders
        assertEquals(15, componentProviders.size)
        assertNotNull(componentProviders.find { it is AppIconComponentProvider })
        assertNotNull(componentProviders.find { it is ComposeResourceComponentProvider })
        assertNotNull(componentProviders.find { it is KtorHttpComponentProvider })
        assertNotNull(componentProviders.find { it is HurlHttpComponentProvider })
        assertNotNull(componentProviders.find { it is OkHttpHttpComponentProvider })
        assertNotNull(componentProviders.find { it is KoralGifComponentProvider })
        assertNotNull(componentProviders.find { it is ImageDecoderAnimatedHeifComponentProvider })
        assertNotNull(componentProviders.find { it is ApkIconComponentProvider })
        assertNotNull(componentProviders.find { it is VideoComponentProvider })
        assertNotNull(componentProviders.find { it is FFmpegVideoComponentProvider })
        assertNotNull(componentProviders.find { it is GifComponentProvider })
        assertNotNull(componentProviders.find { it is AnimatedWebpComponentProvider })
        assertNotNull(componentProviders.find { it is SvgComponentProvider })
        assertNotNull(componentProviders.find { it is BlurHashComponentProvider })
        assertNotNull(componentProviders.find { it is DoNothingComponentProvider })
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
         * ignoredComponentProviders
         */
        ComponentLoader.toComponentRegistry(
            context = context,
            ignoredComponentProviders = listOf(DoNothingComponentProvider::class),
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