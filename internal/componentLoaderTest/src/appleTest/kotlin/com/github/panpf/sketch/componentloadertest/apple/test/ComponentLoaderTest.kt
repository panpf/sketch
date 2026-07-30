package com.github.panpf.sketch.componentloadertest.apple.test

import com.github.panpf.sketch.decode.BlurHashDecoder
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.decode.SkiaAnimatedWebpDecoder
import com.github.panpf.sketch.decode.SkiaGifDecoder
import com.github.panpf.sketch.decode.SvgDecoder
import com.github.panpf.sketch.fetch.BlurHashUriFetcher
import com.github.panpf.sketch.fetch.ComposeResourceUriFetcher
import com.github.panpf.sketch.fetch.KtorHttpUriFetcher
import com.github.panpf.sketch.test.utils.DoNothingComponentProvider
import com.github.panpf.sketch.test.utils.DoNothingDecoder
import com.github.panpf.sketch.test.utils.DoNothingFetcher
import com.github.panpf.sketch.test.utils.DoNothingInterceptor
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.AnimatedWebpComponentProvider
import com.github.panpf.sketch.util.BlurHashComponentProvider
import com.github.panpf.sketch.util.ComponentLoader
import com.github.panpf.sketch.util.ComponentProvider
import com.github.panpf.sketch.util.ComposeResourceComponentProvider
import com.github.panpf.sketch.util.GifComponentProvider
import com.github.panpf.sketch.util.KtorHttpComponentProvider
import com.github.panpf.sketch.util.SvgComponentProvider
import com.github.panpf.sketch.util.toComponentRegistry
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ComponentLoaderTest {

    @Test
    fun testComponentProviders() {
        val componentProviders = ComponentLoader.componentProviders
        assertEquals(8, componentProviders.size)
        assertNotNull(componentProviders.find { it is ComposeResourceComponentProvider })
        assertNotNull(componentProviders.find { it is KtorHttpComponentProvider })
        assertNotNull(componentProviders.find { it is GifComponentProvider })
        assertNotNull(componentProviders.find { it is AnimatedWebpComponentProvider })
        assertNotNull(componentProviders.find { it is SvgComponentProvider })
        assertNotNull(componentProviders.find { it is BlurHashComponentProvider })
        assertNotNull(componentProviders.find { it::class == videoComponentProviderClass })
        assertNotNull(componentProviders.find { it is DoNothingComponentProvider })
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

            val expectedDecoders = listOf(
                SkiaGifDecoder.Factory(),
                SkiaAnimatedWebpDecoder.Factory(),
                SvgDecoder.Factory(),
                BlurHashDecoder.Factory(),
            ) + platformVideoDecoderFactories + DoNothingDecoder.Factory()
            assertEquals(expectedDecoders.size, decoders.size)
            expectedDecoders.forEach { expected ->
                assertNotNull(decoders.find { it == expected })
            }

            assertEquals(1, interceptors.size)
            assertNotNull(interceptors.find { it is DoNothingInterceptor })
        }

        // ignoredComponentProviders
        ComponentLoader.toComponentRegistry(
            context = context,
            ignoredComponentProviders = listOf(DoNothingComponentProvider::class),
        ).apply {
            assertEquals(3, fetchers.size)
            assertNotNull(fetchers.find { it is ComposeResourceUriFetcher.Factory })
            assertNotNull(fetchers.find { it is KtorHttpUriFetcher.Factory })
            assertNotNull(fetchers.find { it is BlurHashUriFetcher.Factory })
            assertNull(fetchers.find { it is DoNothingFetcher.Factory })

            val expectedDecoders = listOf(
                SkiaGifDecoder.Factory(),
                SkiaAnimatedWebpDecoder.Factory(),
                SvgDecoder.Factory(),
                BlurHashDecoder.Factory(),
            ) + platformVideoDecoderFactories
            assertEquals(expectedDecoders.size, decoders.size)
            expectedDecoders.forEach { expected ->
                assertNotNull(decoders.find { it == expected })
            }
            assertNull(decoders.find { it is DoNothingDecoder.Factory })

            assertEquals(0, interceptors.size)
            assertNull(interceptors.find { it is DoNothingInterceptor })
        }
    }
}

internal expect val videoComponentProviderClass: KClass<out ComponentProvider>

internal expect val platformVideoDecoderFactories: List<Decoder.Factory>
