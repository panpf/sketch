package com.github.panpf.sketch.blurhash.common.test.fetch

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.fetch.BlurHashUriFetcher
import com.github.panpf.sketch.fetch.isBlurHashUri
import com.github.panpf.sketch.fetch.newBlurHashUri
import com.github.panpf.sketch.fetch.readSizeFromBlurHashUri
import com.github.panpf.sketch.fetch.supportBlurHash
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.BlurHashDataSource
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.toUri
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class BlurHashUriFetcherTest {

    @Test
    fun testSupportBlurHash() {
        ComponentRegistry.Builder().apply {
            build().apply {
                assertEquals(
                    "ComponentRegistry(" +
                            "fetchers=[]," +
                            "decoders=[]," +
                            "interceptors=[]" +
                            ")",
                    toString()
                )
            }

            supportBlurHash()
            build().apply {
                assertEquals(
                    "ComponentRegistry(" +
                            "fetchers=[BlurHashUriFetcher]," +
                            "decoders=[]," +
                            "interceptors=[]" +
                            ")",
                    toString()
                )
            }

            supportBlurHash()
            build().apply {
                assertEquals(
                    "ComponentRegistry(" +
                            "fetchers=[BlurHashUriFetcher,BlurHashUriFetcher]," +
                            "decoders=[]," +
                            "interceptors=[]" +
                            ")",
                    toString()
                )
            }
        }
    }

    @Test
    fun testNewBlurHashUri() {
        assertEquals(
            expected = "blurhash://UEHLh%5BWB2yk8pyoJadR*.7kCMdnjS%23M%7C%251%252",
            actual = newBlurHashUri("UEHLh[WB2yk8pyoJadR*.7kCMdnjS#M|%1%2")
        )
        assertEquals(
            expected = "blurhash://UEHLh%5BWB2yk8pyoJadR*.7kCMdnjS%23M%7C%251%252",
            actual = newBlurHashUri("UEHLh[WB2yk8pyoJadR*.7kCMdnjS#M|%1%2", null, 202)
        )
        assertEquals(
            expected = "blurhash://UEHLh%5BWB2yk8pyoJadR*.7kCMdnjS%23M%7C%251%252",
            actual = newBlurHashUri("UEHLh[WB2yk8pyoJadR*.7kCMdnjS#M|%1%2", 202, null)
        )
        assertEquals(
            expected = "blurhash://UEHLh%5BWB2yk8pyoJadR*.7kCMdnjS%23M%7C%251%252",
            actual = newBlurHashUri("UEHLh[WB2yk8pyoJadR*.7kCMdnjS#M|%1%2", null, null)
        )
        assertEquals(
            expected = "blurhash://UEHLh%5BWB2yk8pyoJadR*.7kCMdnjS%23M%7C%251%252",
            actual = newBlurHashUri("UEHLh[WB2yk8pyoJadR*.7kCMdnjS#M|%1%2", 0, 202)
        )
        assertEquals(
            expected = "blurhash://UEHLh%5BWB2yk8pyoJadR*.7kCMdnjS%23M%7C%251%252",
            actual = newBlurHashUri("UEHLh[WB2yk8pyoJadR*.7kCMdnjS#M|%1%2", 202, 0)
        )
        assertEquals(
            expected = "blurhash://UEHLh%5BWB2yk8pyoJadR*.7kCMdnjS%23M%7C%251%252",
            actual = newBlurHashUri("UEHLh[WB2yk8pyoJadR*.7kCMdnjS#M|%1%2", 0, 0)
        )
        assertEquals(
            expected = "blurhash://UEHLh%5BWB2yk8pyoJadR*.7kCMdnjS%23M%7C%251%252?width=101&height=202",
            actual = newBlurHashUri("UEHLh[WB2yk8pyoJadR*.7kCMdnjS#M|%1%2", 101, 202)
        )

        assertEquals(
            expected = "blurhash://blurhash%3A%2F%2FUEHLh%255BWB2yk8pyoJadR*.7kCMdnjS%2523M%257C%25251%25252%3Fwidth%3D101%26height%3D202?width=101&height=202",
            actual = newBlurHashUri(
                blurHash = newBlurHashUri("UEHLh[WB2yk8pyoJadR*.7kCMdnjS#M|%1%2", 101, 202),
                width = 101,
                height = 202
            )
        )
    }

    @Test
    fun testIsBlurHashUri() {
        assertEquals(
            expected = true,
            actual = isBlurHashUri("blurhash://UEHLh[WB2yk8pyoJadR*.7kCMdnjS#M|%1%2".toUri())
        )
        assertEquals(
            expected = false,
            actual = isBlurHashUri("blurhash1://UEHLh[WB2yk8pyoJadR*.7kCMdnjS#M|%1%2".toUri())
        )

        assertEquals(
            expected = true,
            actual = isBlurHashUri("blurhash://UEHLh[WB2yk8pyoJadR*.7kCMdnjS#M|%1%2")
        )
        assertEquals(
            expected = false,
            actual = isBlurHashUri("blurhash1://UEHLh[WB2yk8pyoJadR*.7kCMdnjS#M|%1%2")
        )
    }

    @Test
    fun testReadSizeFromBlurHashUri() {
        assertEquals(
            expected = Size(101, 202),
            actual = readSizeFromBlurHashUri(
                "blurhash://UEHLh%5BWB2yk8pyoJadR*.7kCMdnjS%23M%7C%251%252?width=101&height=202".toUri()
            )
        )
        assertEquals(
            expected = null,
            actual = readSizeFromBlurHashUri(
                "blurhash1://UEHLh%5BWB2yk8pyoJadR*.7kCMdnjS%23M%7C%251%252?width=101&height=202".toUri()
            )
        )
        assertEquals(
            expected = null,
            actual = readSizeFromBlurHashUri(
                "blurhash://UEHLh%5BWB2yk8pyoJadR*.7kCMdnjS%23M%7C%251%252".toUri()
            )
        )
        assertEquals(
            expected = null,
            actual = readSizeFromBlurHashUri(
                "blurhash://UEHLh%5BWB2yk8pyoJadR*.7kCMdnjS%23M%7C%251%252?width=101".toUri()
            )
        )
        assertEquals(
            expected = null,
            actual = readSizeFromBlurHashUri(
                "blurhash://UEHLh%5BWB2yk8pyoJadR*.7kCMdnjS%23M%7C%251%252?height=202".toUri()
            )
        )
        assertEquals(
            expected = null,
            actual = readSizeFromBlurHashUri(
                "blurhash://UEHLh%5BWB2yk8pyoJadR*.7kCMdnjS%23M%7C%251%252?width=abc&height=202".toUri()
            )
        )
        assertEquals(
            expected = null,
            actual = readSizeFromBlurHashUri(
                "blurhash://UEHLh%5BWB2yk8pyoJadR*.7kCMdnjS%23M%7C%251%252?width=101&height=abc".toUri()
            )
        )
    }

    @Test
    fun testCompanion() {
        assertEquals("blurhash", BlurHashUriFetcher.SCHEME)
    }

    @Test
    fun testConstructor() {
        BlurHashUriFetcher("blurhash://UEHLh%5BWB2yk8pyoJadR*.7kCMdnjS%23M%7C%251%252?height=202".toUri())
    }

    @Test
    fun testFetch() = runTest {
        val blurHashUri = newBlurHashUri("UEHLh[WB2yk8pyoJadR*.7kCMdnjS#M|%1%2").toUri()
        val fetcher = BlurHashUriFetcher(blurHashUri)
        val fetchResult = fetcher.fetch().getOrThrow()
        assertEquals(BlurHashDataSource(fetcher.blurHashUri), fetchResult.dataSource)
        assertEquals("image/jpeg", fetchResult.mimeType)
    }

    @Test
    fun testEqualsAndHashCode() {
        val blurHashUri = newBlurHashUri("UEHLh[WB2yk8pyoJadR*.7kCMdnjS#M|%1%2").toUri()
        val blurHashUri2 = newBlurHashUri("L9HL7nxu00WB~qj[ayfQ00WB~qj[").toUri()
        val element1 = BlurHashUriFetcher(blurHashUri)
        val element11 = BlurHashUriFetcher(blurHashUri)
        val element2 = BlurHashUriFetcher(blurHashUri2)

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
    }

    @Test
    fun testToString() {
        val blurHashUri = newBlurHashUri("UEHLh[WB2yk8pyoJadR*.7kCMdnjS#M|%1%2").toUri()
        assertEquals(
            expected = "BlurHashUriFetcher(blurHashUri='$blurHashUri')",
            actual = BlurHashUriFetcher(blurHashUri).toString()
        )
    }

    @Test
    fun testFactoryCreate() {
        val (context, sketch) = getTestContextAndSketch()
        val fetcherFactory = BlurHashUriFetcher.Factory()

        val blurHashUri1 = "blurhash://e6PZfSi_.AyE8^_3t7t7R*WB*0o#DgR4.T_3R*D%xt%MMcV@%itSI9"
        val blurHashUri2 = "blurhash://VEHLh[WB2yk8\$NpyoJadR*=s.7kCMdnjx]S#M|%1%2EN"
        assertNotEquals(blurHashUri1, blurHashUri2)

        ImageRequest(
            context = context,
            uri = "blurhash://UEHLh%5BWB2yk8pyoJadR*.7kCMdnjS%23M%7C%251%252?height=202"
        ).toRequestContext(sketch, Size(200, 200))
            .let { fetcherFactory.create(it) }
            .apply {
                assertEquals(
                    expected = BlurHashUriFetcher("blurhash://UEHLh%5BWB2yk8pyoJadR*.7kCMdnjS%23M%7C%251%252?height=202".toUri()),
                    actual = this
                )
            }

        ImageRequest(
            context = context,
            uri = "blurhash1://UEHLh%5BWB2yk8pyoJadR*.7kCMdnjS%23M%7C%251%252?height=202"
        ).toRequestContext(sketch, Size(200, 200))
            .let { fetcherFactory.create(it) }
            .apply {
                assertEquals(
                    expected = null,
                    actual = this
                )
            }

        ImageRequest(
            context = context,
            uri = "UEHLh%5BWB2yk8pyoJadR*.7kCMdnjS%23M%7C%251%252?height=202"
        ).toRequestContext(sketch, Size(200, 200))
            .let { fetcherFactory.create(it) }
            .apply {
                assertEquals(
                    expected = null,
                    actual = this
                )
            }

        ImageRequest(
            context = context,
            uri = "UEHLh%5BWB2yk8pyoJadR*.7kCMdnjS%23M%7C%251%252"
        ).toRequestContext(sketch, Size(200, 200))
            .let { fetcherFactory.create(it) }
            .apply {
                assertEquals(
                    expected = null,
                    actual = this
                )
            }
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val element1 = BlurHashUriFetcher.Factory()
        val element11 = BlurHashUriFetcher.Factory()

        assertEquals(element1, element11)
        assertNotEquals(element1, Any())
        assertNotEquals(element1, null as Any?)

        assertEquals(element1.hashCode(), element11.hashCode())
    }

    @Test
    fun testFactoryToString() {
        assertEquals(
            expected = "BlurHashUriFetcher",
            actual = BlurHashUriFetcher.Factory().toString()
        )
    }
}