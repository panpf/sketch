package com.github.panpf.sketch.video.test.request

import android.media.MediaMetadataRetriever
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.videoFrameMicros
import com.github.panpf.sketch.request.videoFrameMillis
import com.github.panpf.sketch.request.videoFrameOption
import com.github.panpf.sketch.request.videoFramePercentDuration
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VideoFrameExtensionsTest {

    @Test
    fun testVideoFrameMicros() {
        val context = InstrumentationRegistry.getInstrumentation().context

        (LoadRequest(context, newAssetUri("sample.mp4")) as ImageRequest).apply {
            Assert.assertNull(videoFrameMicros)
        }
        (LoadRequest(context, newAssetUri("sample.mp4")) {
            (this as ImageRequest.Builder).videoFrameMicros(1000000)
        } as ImageRequest).apply {
            Assert.assertEquals(1000000L, videoFrameMicros)
        }
        (LoadRequest(context, newAssetUri("sample.mp4")) {
            (this as ImageRequest.Builder).videoFrameMillis(1000)
        } as ImageRequest).apply {
            Assert.assertEquals(1000000L, videoFrameMicros)
        }

        LoadRequest(context, newAssetUri("sample.mp4")).apply {
            Assert.assertNull(videoFrameMicros)
        }
        LoadRequest(context, newAssetUri("sample.mp4")) {
            videoFrameMicros(1000000)
        }.apply {
            Assert.assertEquals(1000000L, videoFrameMicros)
        }
        LoadRequest(context, newAssetUri("sample.mp4")) {
            videoFrameMillis(1000)
        }.apply {
            Assert.assertEquals(1000000L, videoFrameMicros)
        }

        DisplayRequest(context, newAssetUri("sample.mp4")).apply {
            Assert.assertNull(videoFrameMicros)
        }
        DisplayRequest(context, newAssetUri("sample.mp4")) {
            videoFrameMicros(1000000)
        }.apply {
            Assert.assertEquals(1000000L, videoFrameMicros)
        }
        DisplayRequest(context, newAssetUri("sample.mp4")) {
            videoFrameMillis(1000)
        }.apply {
            Assert.assertEquals(1000000L, videoFrameMicros)
        }

        ImageOptions().apply {
            Assert.assertNull(videoFrameMicros)
        }
        ImageOptions {
            videoFrameMicros(1000000)
        }.apply {
            Assert.assertEquals(1000000L, videoFrameMicros)
        }
        ImageOptions {
            videoFrameMillis(1000)
        }.apply {
            Assert.assertEquals(1000000L, videoFrameMicros)
        }

        val key1 = LoadRequest(context, newAssetUri("sample.mp4")).key
        val key2 = LoadRequest(context, newAssetUri("sample.mp4")) {
            videoFrameMillis(500)
        }.key
        Assert.assertNotEquals(key1, key2)

        val cacheKey1 = LoadRequest(context, newAssetUri("sample.mp4")).cacheKey
        val cacheKey2 = LoadRequest(context, newAssetUri("sample.mp4")) {
            videoFrameMillis(500)
        }.cacheKey
        Assert.assertNotEquals(cacheKey1, cacheKey2)
    }

    @Test
    fun testVideoPercentDuration() {
        val context = InstrumentationRegistry.getInstrumentation().context

        LoadRequest(context, newAssetUri("sample.mp4")).apply {
            Assert.assertNull(videoFramePercentDuration)
        }
        LoadRequest(context, newAssetUri("sample.mp4")) {
            videoFramePercentDuration(0.45f)
        }.apply {
            Assert.assertEquals(0.45f, videoFramePercentDuration)
        }

        DisplayRequest(context, newAssetUri("sample.mp4")).apply {
            Assert.assertNull(videoFramePercentDuration)
        }
        DisplayRequest(context, newAssetUri("sample.mp4")) {
            videoFramePercentDuration(0.45f)
        }.apply {
            Assert.assertEquals(0.45f, videoFramePercentDuration)
        }

        ImageOptions().apply {
            Assert.assertNull(videoFramePercentDuration)
        }
        ImageOptions {
            videoFramePercentDuration(0.45f)
        }.apply {
            Assert.assertEquals(0.45f, videoFramePercentDuration)
        }

        val key1 = LoadRequest(context, newAssetUri("sample.mp4")).key
        val key2 = LoadRequest(context, newAssetUri("sample.mp4")) {
            videoFramePercentDuration(0.45f)
        }.key
        Assert.assertNotEquals(key1, key2)

        val cacheKey1 = LoadRequest(context, newAssetUri("sample.mp4")).cacheKey
        val cacheKey2 = LoadRequest(context, newAssetUri("sample.mp4")) {
            videoFramePercentDuration(0.45f)
        }.cacheKey
        Assert.assertNotEquals(cacheKey1, cacheKey2)
    }

    @Test
    fun testVideoOption() {
        val context = InstrumentationRegistry.getInstrumentation().context

        LoadRequest(context, newAssetUri("sample.mp4")).apply {
            Assert.assertNull(videoFrameOption)
        }
        LoadRequest(context, newAssetUri("sample.mp4")) {
            videoFrameOption(MediaMetadataRetriever.OPTION_NEXT_SYNC)
        }.apply {
            Assert.assertEquals(MediaMetadataRetriever.OPTION_NEXT_SYNC, videoFrameOption)
        }

        DisplayRequest(context, newAssetUri("sample.mp4")).apply {
            Assert.assertNull(videoFrameOption)
        }
        DisplayRequest(context, newAssetUri("sample.mp4")) {
            videoFrameOption(MediaMetadataRetriever.OPTION_NEXT_SYNC)
        }.apply {
            Assert.assertEquals(MediaMetadataRetriever.OPTION_NEXT_SYNC, videoFrameOption)
        }

        ImageOptions().apply {
            Assert.assertNull(videoFrameOption)
        }
        ImageOptions {
            videoFrameOption(MediaMetadataRetriever.OPTION_NEXT_SYNC)
        }.apply {
            Assert.assertEquals(MediaMetadataRetriever.OPTION_NEXT_SYNC, videoFrameOption)
        }

        val key1 = LoadRequest(context, newAssetUri("sample.mp4")).key
        val key2 = LoadRequest(context, newAssetUri("sample.mp4")) {
            videoFrameOption(MediaMetadataRetriever.OPTION_NEXT_SYNC)
        }.key
        Assert.assertNotEquals(key1, key2)

        val cacheKey1 = LoadRequest(context, newAssetUri("sample.mp4")).cacheKey
        val cacheKey2 = LoadRequest(context, newAssetUri("sample.mp4")) {
            videoFrameOption(MediaMetadataRetriever.OPTION_NEXT_SYNC)
        }.cacheKey
        Assert.assertNotEquals(cacheKey1, cacheKey2)
    }
}