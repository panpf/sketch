package com.github.panpf.sketch.componentloadertest.android.test

import com.github.panpf.sketch.decode.internal.AnimatedWebpDecoderProvider
import com.github.panpf.sketch.decode.internal.ApkIconDecoderProvider
import com.github.panpf.sketch.decode.internal.FFmpegVideoFrameDecoderProvider
import com.github.panpf.sketch.decode.internal.GifDecoderProvider
import com.github.panpf.sketch.decode.internal.ImageDecoderAnimatedHeifDecoderProvider
import com.github.panpf.sketch.decode.internal.KoralGifDecoderProvider
import com.github.panpf.sketch.decode.internal.SvgDecoderProvider
import com.github.panpf.sketch.decode.internal.VideoFrameDecoderProvider
import com.github.panpf.sketch.fetch.internal.AppIconUriFetcherProvider
import com.github.panpf.sketch.fetch.internal.ComposeResourceUriFetcherProvider
import com.github.panpf.sketch.fetch.internal.HurlHttpUriFetcherProvider
import com.github.panpf.sketch.fetch.internal.KtorHttpUriFetcherProvider
import com.github.panpf.sketch.fetch.internal.OkHttpHttpUriFetcherProvider
import com.github.panpf.sketch.util.ComponentLoader
import kotlin.test.Test
import kotlin.test.assertNotNull

class ComponentLoaderTest {

    @Test
    fun testFetchers() {
        val fetcherProviderList = ComponentLoader.fetchers
        assertNotNull(fetcherProviderList.find { it is AppIconUriFetcherProvider })
        assertNotNull(fetcherProviderList.find { it is ComposeResourceUriFetcherProvider })
        assertNotNull(fetcherProviderList.find { it is KtorHttpUriFetcherProvider })
        assertNotNull(fetcherProviderList.find { it is HurlHttpUriFetcherProvider })
        assertNotNull(fetcherProviderList.find { it is OkHttpHttpUriFetcherProvider })
    }

    @Test
    fun testDecoders() {
        val decoderProviderList = ComponentLoader.decoders
        assertNotNull(decoderProviderList.find { it is KoralGifDecoderProvider })
        assertNotNull(decoderProviderList.find { it is ImageDecoderAnimatedHeifDecoderProvider })
        assertNotNull(decoderProviderList.find { it is ApkIconDecoderProvider })
        assertNotNull(decoderProviderList.find { it is VideoFrameDecoderProvider })
        assertNotNull(decoderProviderList.find { it is FFmpegVideoFrameDecoderProvider })
        assertNotNull(decoderProviderList.find { it is GifDecoderProvider })
        assertNotNull(decoderProviderList.find { it is AnimatedWebpDecoderProvider })
        assertNotNull(decoderProviderList.find { it is SvgDecoderProvider })
    }
}