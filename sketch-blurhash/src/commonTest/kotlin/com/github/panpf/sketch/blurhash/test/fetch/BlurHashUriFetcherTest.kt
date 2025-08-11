package com.github.panpf.sketch.blurhash.test.fetch

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.fetch.BlurHashUriFetcher
import com.github.panpf.sketch.fetch.BlurHashUtil
import com.github.panpf.sketch.fetch.isBlurHashUri
import com.github.panpf.sketch.fetch.newBlurHashUri
import com.github.panpf.sketch.fetch.supportBlurHash
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.BlurHashDataSource
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.toUri
import kotlinx.coroutines.test.runTest
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class BlurHashUriFetcherTest {

    @Test
    fun testSupportBlurHash() {
        ComponentRegistry.Builder().apply {
            build().apply {
                assertEquals(
                    "ComponentRegistry(" +
                            "fetcherFactoryList=[]," +
                            "decoderFactoryList=[]," +
                            "requestInterceptorList=[]," +
                            "decodeInterceptorList=[]" +
                            ")",
                    toString()
                )
            }

            supportBlurHash()
            build().apply {
                assertEquals(
                    "ComponentRegistry(" +
                            "fetcherFactoryList=[BlurHashUriFetcher]," +
                            "decoderFactoryList=[]," +
                            "requestInterceptorList=[]," +
                            "decodeInterceptorList=[]" +
                            ")",
                    toString()
                )
            }

            supportBlurHash()
            build().apply {
                assertEquals(
                    "ComponentRegistry(" +
                            "fetcherFactoryList=[BlurHashUriFetcher,BlurHashUriFetcher]," +
                            "decoderFactoryList=[]," +
                            "requestInterceptorList=[]," +
                            "decodeInterceptorList=[]" +
                            ")",
                    toString()
                )
            }
        }
    }

    @Test
    fun testNewBlurHashUri() {
        assertEquals(
            expected = "blurhash://LEHV6nWB2yk8pyo0adR*.7kCMdnj",
            actual = newBlurHashUri("LEHV6nWB2yk8pyo0adR*.7kCMdnj")
        )

        assertEquals(
            expected = "blurhash://|HF5?xYk^6#M9wKSW@j=#*@-5b,1J5O[V=R:s;w[@[or[k6.O[TLtJnNnO};FxngOZE3NgNHsps,jMFxS#OtcXnzRjxZxHj]OYNeR:JCs9xunhwIbeIpNaxHNGr;v}aeo0Xmt6XS\$et6#*\$ft6nhxHnNV@w{nOenwfNHo0",
            actual = newBlurHashUri("|HF5?xYk^6#M9wKSW@j=#*@-5b,1J5O[V=R:s;w[@[or[k6.O[TLtJnNnO};FxngOZE3NgNHsps,jMFxS#OtcXnzRjxZxHj]OYNeR:JCs9xunhwIbeIpNaxHNGr;v}aeo0Xmt6XS\$et6#*\$ft6nhxHnNV@w{nOenwfNHo0")
        )

        assertEquals(
            expected = "blurhash://LEHV6nWB2yk8pyo0adR*.7kCMdnj&width=100&height=100",
            actual = newBlurHashUri("LEHV6nWB2yk8pyo0adR*.7kCMdnj", 100, 100)
        )

        assertEquals(
            expected = "blurhash://UEHLh[WB2yk8pyoJadR*.7kCMdnjS#M|%1%2&width=200&height=150",
            actual = newBlurHashUri("UEHLh[WB2yk8pyoJadR*.7kCMdnjS#M|%1%2", 200, 150)
        )

        val invalidBlurHashString = "SEHV6nWB2yk8pyo0adR*.7kCMdnj"
        assertFalse(BlurHashUtil.isValid(invalidBlurHashString))

        assertFailsWith(IllegalArgumentException::class) {
            newBlurHashUri(invalidBlurHashString)
        }

        assertFailsWith(IllegalArgumentException::class) {
            newBlurHashUri("moon.jpeg")
        }

        assertFailsWith(IllegalArgumentException::class) {
            newBlurHashUri(invalidBlurHashString, 100, 100)
        }

        assertFailsWith(IllegalArgumentException::class) {
            newBlurHashUri("moon.jpeg", 100, 100)
        }

        assertFailsWith(IllegalArgumentException::class) {
            newBlurHashUri("LEHV6nWB2yk8pyo0adR*.7kCMdnj", -100, 100)
        }
    }

    @Test
    fun testIsBlurHashUri() {
        assertEquals(
            expected = true,
            actual = isBlurHashUri("blurhash://UEHLh[WB2yk8pyoJadR*.7kCMdnjS#M|%1%2".toUri())
        )
        assertEquals(
            expected = true,
            actual = isBlurHashUri("blurhash://|6PZfSi_.AyE8^m+%gt,o~_3t7t7R*WBs,ofR-a#*0o#DgR4.Tt,ITVYZ~_3R*D%xt%MIpRj%0oJMcV@%itSI9R5x]tRbcIot7-:IoM{%LoeIVjuNHoft7M{RkxuozM{ae%1WBg4tRV@M{kCxuog?vWB9Et7-=NGM{xaae".toUri())
        )

        // Test URIs with query parameters
        assertEquals(
            expected = true,
            actual = isBlurHashUri("blurhash://UEHLh[WB2yk8pyoJadR*.7kCMdnjS#M|%1%2&width=100&height=100".toUri())
        )
        assertEquals(
            expected = true,
            actual = isBlurHashUri("blurhash://LEHV6nWB2yk8pyo0adR*.7kCMdnj&width=200&height=150".toUri())
        )
        assertEquals(
            expected = true,
            actual = isBlurHashUri("blurhash://|6PZfSi_.AyE8^m+%gt,o~_3t7t7R*WBs,ofR-a#*0o#DgR4.Tt,ITVYZ~_3R*D%xt%MIpRj%0oJMcV@%itSI9R5x]tRbcIot7-:IoM{%LoeIVjuNHoft7M{RkxuozM{ae%1WBg4tRV@M{kCxuog?vWB9Et7-=NGM{xaae&width=300&height=400".toUri())
        )

        assertEquals(
            expected = false,
            actual = isBlurHashUri("blurhash:/UEHLh[WB2yk8pyoJadR*.7kCMdnjS#M|%1%2".toUri())
        )
        assertEquals(
            expected = false,
            actual = isBlurHashUri("blurhash:///UEHLh[WB2yk8pyoJadR*.7kCMdnjS#M|%1%2".toUri())
        )
        assertEquals(
            expected = false,
            actual = isBlurHashUri("file://compose_resource/composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpgg?from=home".toUri())
        )
        assertEquals(
            expected = false,
            actual = isBlurHashUri("LEHV6nWB2yk8pyo0adR*.7kCMdnj".toUri())
        )
        assertEquals(
            expected = false,
            actual = isBlurHashUri("blurhash://AEHLh[WB2yk8pyoJadR*.7kCMdnjS#M|%1%2".toUri())
        )
        // Test invalid blurHash with query parameters
        assertEquals(
            expected = false,
            actual = isBlurHashUri("blurhash://AEHLh[WB2yk8pyoJadR*.7kCMdnjS#M|%1%2&width=100&height=100".toUri())
        )
    }

    @Test
    fun testCompanion() {
        assertEquals("blurhash", BlurHashUriFetcher.SCHEME)
    }

    @OptIn(ExperimentalEncodingApi::class)
    @Test
    fun testFetch() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val fetcherFactory = BlurHashUriFetcher.Factory()
        val blurgashUri =
            "blurhash://e6PZfSi_.AyE8^_3t7t7R*WB*0o#DgR4.T_3R*D%xt%MMcV@%itSI9"

        val fetcher = fetcherFactory.create(
            ImageRequest(context, blurgashUri)
                .toRequestContext(sketch, Size(100, 100))
        )!!
        val source = fetcher.fetch().getOrThrow().dataSource
        assertTrue(source is BlurHashDataSource)
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = BlurHashUriFetcher(
            blurHashString = "K6PZfSi_.A_3t7t7*0o#Dg&width=100&height=100"
        )
        val element11 = BlurHashUriFetcher(
            blurHashString = "K6PZfSi_.A_3t7t7*0o#Dg&width=100&height=100"
        )
        val element2 = BlurHashUriFetcher(
            blurHashString = "KGF5?xYk^6@-5c,1@[or[Q&width=100&height=100"
        )
        val element3 = BlurHashUriFetcher(
            blurHashString = "K6PZfSi_.A_3t7t7*0o#Dg&width=200&height=200"
        )

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element2, element3)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element1.hashCode(), element3.hashCode())
        assertNotEquals(element2.hashCode(), element3.hashCode())
    }

    @Test
    fun testToString() {
        val base64UriFetcher = BlurHashUriFetcher(
            blurHashString = "K6PZfSi_.A_3t7t7*0o#Dg&width=100&height=100"
        )
        assertEquals(
            expected = "BlurHashUriFetcher(blurHash='K6PZfSi_.A_3t7t7*0o#Dg', size=100x100)",
            actual = base64UriFetcher.toString()
        )
    }

    @Test
    fun testFactoryCreate() {
        val (context, sketch) = getTestContextAndSketch()
        val fetcherFactory = BlurHashUriFetcher.Factory()

        val blurHashUri1 = "blurhash://e6PZfSi_.AyE8^_3t7t7R*WB*0o#DgR4.T_3R*D%xt%MMcV@%itSI9"
        val blurHashUri2 = "blurhash://VEHLh[WB2yk8\$NpyoJadR*=s.7kCMdnjx]S#M|%1%2EN"
        assertNotEquals(blurHashUri1, blurHashUri2)
        fetcherFactory.create(
            ImageRequest(context, blurHashUri1)
                .toRequestContext(sketch, Size(200, 200)),
        )!!.apply {
            assertEquals("e6PZfSi_.AyE8^_3t7t7R*WB*0o#DgR4.T_3R*D%xt%MMcV@%itSI9", blurHashString)
            assertEquals(Size(200, 200), size)
        }
        fetcherFactory.create(
            ImageRequest(context, blurHashUri2)
                .toRequestContext(sketch, Size(100, 100))
        )!!.apply {
            assertEquals("VEHLh[WB2yk8\$NpyoJadR*=s.7kCMdnjx]S#M|%1%2EN", blurHashString)
            assertEquals(Size(100, 100), size)
        }
        val invalidBlurHashString = "A6PZfSi_.AyE8^_3t7t7R*WB*0o#DgR4.T_3R*D%xt%MMcV@%itSI9"
        assertFalse(BlurHashUtil.isValid(invalidBlurHashString))

        val blurHashErrorUri1 = "blurhash://$invalidBlurHashString"
        val blurHashErrorUri2 = "blurhash:///e6PZfSi_.AyE8^_3t7t7R*WB*0o#DgR4.T_3R*D%xt%MMcV@%itSI9"
        val blurHashErrorUri3 = "data:image/png;base54,4y2u1412421089084901240129"
        val errorSize = Size.Empty

        assertNull(
            fetcherFactory.create(
                ImageRequest(context, blurHashErrorUri1)
                    .toRequestContext(sketch, Size(100, 100))
            )
        )
        assertNull(
            fetcherFactory.create(
                ImageRequest(context, blurHashErrorUri2)
                    .toRequestContext(sketch, Size(100, 100))
            )
        )
        assertNull(
            fetcherFactory.create(
                ImageRequest(context, blurHashErrorUri3)
                    .toRequestContext(sketch, Size(100, 100))
            )
        )
        assertNull(
            fetcherFactory.create(
                ImageRequest(context, blurHashUri1)
                    .toRequestContext(sketch, errorSize)
            )
        )

        val blurHashUriWithSize1 =
            "blurhash://e6PZfSi_.AyE8^_3t7t7R*WB*0o#DgR4.T_3R*D%xt%MMcV@%itSI9&width=150&height=200"
        val blurHashUriWithSize2 =
            "blurhash://VEHLh[WB2yk8\$NpyoJadR*=s.7kCMdnjx]S#M|%1%2EN&width=300&height=250"

        fetcherFactory.create(
            ImageRequest(context, blurHashUriWithSize1)
                .toRequestContext(sketch, Size(100, 100)),
        )!!.apply {
            assertEquals("e6PZfSi_.AyE8^_3t7t7R*WB*0o#DgR4.T_3R*D%xt%MMcV@%itSI9", blurHashString)
            assertEquals(Size(150, 200), size)
        }

        fetcherFactory.create(
            ImageRequest(context, blurHashUriWithSize2)
                .toRequestContext(sketch, Size(400, 400))
        )!!.apply {
            assertEquals("VEHLh[WB2yk8\$NpyoJadR*=s.7kCMdnjx]S#M|%1%2EN", blurHashString)
            assertEquals(Size(300, 250), size)
        }

        // Test URI with invalid query parameters (should fall back to request context size)
        val blurHashUriWithInvalidParams =
            "blurhash://e6PZfSi_.AyE8^_3t7t7R*WB*0o#DgR4.T_3R*D%xt%MMcV@%itSI9&width=invalid&height=200"
        fetcherFactory.create(
            ImageRequest(context, blurHashUriWithInvalidParams)
                .toRequestContext(sketch, Size(500, 600))
        )!!.apply {
            assertEquals("e6PZfSi_.AyE8^_3t7t7R*WB*0o#DgR4.T_3R*D%xt%MMcV@%itSI9", blurHashString)
            assertEquals(Size(500, 600), size) // Falls back to request context size
        }

        // Test URI with partial query parameters (should fall back to request context size)
        val blurHashUriWithPartialParams =
            "blurhash://e6PZfSi_.AyE8^_3t7t7R*WB*0o#DgR4.T_3R*D%xt%MMcV@%itSI9&width=100"
        fetcherFactory.create(
            ImageRequest(context, blurHashUriWithPartialParams)
                .toRequestContext(sketch, Size(700, 800))
        )!!.apply {
            assertEquals("e6PZfSi_.AyE8^_3t7t7R*WB*0o#DgR4.T_3R*D%xt%MMcV@%itSI9", blurHashString)
            assertEquals(Size(700, 800), size) // Falls back to request context size
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