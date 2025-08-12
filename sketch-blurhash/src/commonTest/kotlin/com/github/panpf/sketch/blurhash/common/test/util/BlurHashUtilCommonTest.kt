package com.github.panpf.sketch.blurhash.common.test.util

import com.github.panpf.sketch.fetch.BlurHashUriFetcher
import com.github.panpf.sketch.fetch.newBlurHashUri
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.blurHashMemoryCacheKey
import com.github.panpf.sketch.util.resolveBlurHashBitmapSize
import com.github.panpf.sketch.util.toUri
import kotlin.test.Test
import kotlin.test.assertEquals

class BlurHashUtilCommonTest {

    @Test
    fun testResolveBlurHashBitmapSize() {
        assertEquals(
            expected = Size(width = 505, height = 707),
            actual = resolveBlurHashBitmapSize(
                blurHashUri = newBlurHashUri(
                    blurHash = "UEHLh[WB2yk8pyoJadR*.7kCMdnjS#M|%1%2",
                    width = 101,
                    height = 202
                ).toUri(),
                size = Size(width = 505, height = 707)
            )
        )
        assertEquals(
            expected = Size(width = 101, height = 202),
            actual = resolveBlurHashBitmapSize(
                blurHashUri = newBlurHashUri(
                    blurHash = "UEHLh[WB2yk8pyoJadR*.7kCMdnjS#M|%1%2",
                    width = 101,
                    height = 202
                ).toUri(),
                size = Size.Empty
            )
        )
        assertEquals(
            expected = Size(width = 101, height = 202),
            actual = resolveBlurHashBitmapSize(
                blurHashUri = newBlurHashUri(
                    blurHash = "UEHLh[WB2yk8pyoJadR*.7kCMdnjS#M|%1%2",
                    width = 101,
                    height = 202
                ).toUri(),
                size = null
            )
        )

        assertEquals(
            expected = Size(width = 101, height = 202),
            actual = resolveBlurHashBitmapSize(
                blurHashUri = null,
                size = Size(width = 101, height = 202)
            )
        )
        assertEquals(
            expected = Size(width = 100, height = 100),
            actual = resolveBlurHashBitmapSize(blurHashUri = null, size = Size.Empty)
        )
        assertEquals(
            expected = Size(width = 100, height = 100),
            actual = resolveBlurHashBitmapSize(blurHashUri = null, size = null)
        )

        assertEquals(
            expected = Size(width = 100, height = 100),
            actual = resolveBlurHashBitmapSize(blurHashUri = "".toUri(), size = null)
        )
        assertEquals(
            expected = Size(width = 100, height = 100),
            actual = resolveBlurHashBitmapSize(
                blurHashUri = newBlurHashUri(blurHash = "UEHLh[WB2yk8pyoJadR*.7kCMdnjS#M|%1%2").toUri(),
                size = null
            )
        )
        assertEquals(
            expected = Size(width = 100, height = 100),
            actual = resolveBlurHashBitmapSize(
                blurHashUri = newBlurHashUri(
                    blurHash = "UEHLh[WB2yk8pyoJadR*.7kCMdnjS#M|%1%2",
                    width = 101,
                    height = 202
                ).replace(BlurHashUriFetcher.SCHEME, "resource").toUri(),
                size = null
            )
        )
        assertEquals(
            expected = Size(width = 100, height = 100),
            actual = resolveBlurHashBitmapSize(
                blurHashUri = newBlurHashUri(
                    blurHash = "UEHLh[WB2yk8pyoJadR*.7kCMdnjS#M|%1%2",
                    width = 0,
                    height = 202
                ).toUri(),
                size = null
            )
        )
        assertEquals(
            expected = Size(width = 100, height = 100),
            actual = resolveBlurHashBitmapSize(
                blurHashUri = "blurhash://UEHLh[WB2yk8pyoJadR*.7kCMdnjS#M|%1%2?width=0&height=202".toUri(),
                size = null
            )
        )
        assertEquals(
            expected = Size(width = 100, height = 100),
            actual = resolveBlurHashBitmapSize(
                blurHashUri = newBlurHashUri(
                    blurHash = "UEHLh[WB2yk8pyoJadR*.7kCMdnjS#M|%1%2",
                    width = 101,
                    height = 0
                ).toUri(),
                size = null
            )
        )
    }

    @Test
    fun testBlurHashMemoryCacheKey() {
        assertEquals(
            expected = "blurhash://UEHLh%5BWB2yk8pyoJadR*.7kCMdnjS%23M%7C%251%252?width=100&height=100",
            actual = blurHashMemoryCacheKey("UEHLh[WB2yk8pyoJadR*.7kCMdnjS#M|%1%2", Size(100, 100))
        )
        assertEquals(
            expected = "blurhash://blurhash%3A%2F%2FUEHLh%255BWB2yk8pyoJadR*.7kCMdnjS%2523M%257C%25251%25252?width=100&height=100",
            actual = blurHashMemoryCacheKey(
                blurHash = newBlurHashUri("UEHLh[WB2yk8pyoJadR*.7kCMdnjS#M|%1%2"),
                size = Size(100, 100)
            )
        )
    }
}