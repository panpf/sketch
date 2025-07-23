package com.github.panpf.sketch.blurhash.test.fetch

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.fetch.*
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.BlurhashDataSource
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.createARGBBitmap
import com.github.panpf.sketch.test.utils.toPreviewBitmap
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.installPixels
import com.github.panpf.sketch.util.toUri
import kotlinx.coroutines.test.runTest
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.test.*
import kotlin.time.measureTime

class BlurhashUriFetcherTest {

    @Test
    fun testSupportBlurhash() {
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

            supportBlurhash()
            build().apply {
                assertEquals(
                    "ComponentRegistry(" +
                            "fetcherFactoryList=[BlurhashUriFetcher]," +
                            "decoderFactoryList=[]," +
                            "requestInterceptorList=[]," +
                            "decodeInterceptorList=[]" +
                            ")",
                    toString()
                )
            }

            supportBlurhash()
            build().apply {
                assertEquals(
                    "ComponentRegistry(" +
                            "fetcherFactoryList=[BlurhashUriFetcher,BlurhashUriFetcher]," +
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
    fun testNewBlurhashUri() {
        assertEquals(
            expected = "blurhash://LEHV6nWB2yk8pyo0adR*.7kCMdnj",
            actual = newBlurhashUri("LEHV6nWB2yk8pyo0adR*.7kCMdnj")
        )

        assertEquals(
            expected = "blurhash://|HF5?xYk^6#M9wKSW@j=#*@-5b,1J5O[V=R:s;w[@[or[k6.O[TLtJnNnO};FxngOZE3NgNHsps,jMFxS#OtcXnzRjxZxHj]OYNeR:JCs9xunhwIbeIpNaxHNGr;v}aeo0Xmt6XS\$et6#*\$ft6nhxHnNV@w{nOenwfNHo0",
            actual = newBlurhashUri("|HF5?xYk^6#M9wKSW@j=#*@-5b,1J5O[V=R:s;w[@[or[k6.O[TLtJnNnO};FxngOZE3NgNHsps,jMFxS#OtcXnzRjxZxHj]OYNeR:JCs9xunhwIbeIpNaxHNGr;v}aeo0Xmt6XS\$et6#*\$ft6nhxHnNV@w{nOenwfNHo0")
        )

        assertFailsWith(IllegalArgumentException::class) {
            newBlurhashUri("SEHV6nWB2yk8pyo0adR*.7kCMdnj")
        }

        assertFailsWith(IllegalArgumentException::class) {
            newBlurhashUri("moon.jpeg")
        }
    }

    @Test
    fun testIsBlurhashUri() {
        assertEquals(
            expected = true,
            actual = isBlurHashUri("blurhash://UEHLh[WB2yk8pyoJadR*.7kCMdnjS#M|%1%2".toUri())
        )
        assertEquals(
            expected = true,
            actual = isBlurHashUri("blurhash://|6PZfSi_.AyE8^m+%gt,o~_3t7t7R*WBs,ofR-a#*0o#DgR4.Tt,ITVYZ~_3R*D%xt%MIpRj%0oJMcV@%itSI9R5x]tRbcIot7-:IoM{%LoeIVjuNHoft7M{RkxuozM{ae%1WBg4tRV@M{kCxuog?vWB9Et7-=NGM{xaae".toUri())
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
    }

    @Test
    fun testCompanion() {
        assertEquals("blurhash", BlurhashUriFetcher.SCHEME)
    }

    @OptIn(ExperimentalEncodingApi::class)
    @Test
    fun testFetch() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val fetcherFactory = BlurhashUriFetcher.Factory()
        val blurgashUri =
            "blurhash://e6PZfSi_.AyE8^_3t7t7R*WB*0o#DgR4.T_3R*D%xt%MMcV@%itSI9"

        val fetcher = fetcherFactory.create(
            ImageRequest(context, blurgashUri)
                .toRequestContext(sketch, Size(100, 100))
        )!!
        val source = fetcher.fetch().getOrThrow().dataSource
        assertTrue(source is BlurhashDataSource)
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = BlurhashUriFetcher(
            blurHashString = "K6PZfSi_.A_3t7t7*0o#Dg",
            size = Size(100, 100),
        )
        val element11 = BlurhashUriFetcher(
            blurHashString = "K6PZfSi_.A_3t7t7*0o#Dg",
            size = Size(100, 100),
        )
        val element2 = BlurhashUriFetcher(
            blurHashString = "KGF5?xYk^6@-5c,1@[or[Q",
            size = Size(100, 100),
        )
        val element3 = BlurhashUriFetcher(
            blurHashString = "K6PZfSi_.A_3t7t7*0o#Dg",
            size = Size(200, 200),
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
        val base64UriFetcher = BlurhashUriFetcher(
            blurHashString = "K6PZfSi_.A_3t7t7*0o#Dg",
            size = Size(100, 100),
        )
        assertEquals(
            expected = "BlurHashUriFetcher(blurHash='K6PZfSi_.A_3t7t7*0o#Dg', size=100x100)",
            actual = base64UriFetcher.toString()
        )
    }

    @Test
    fun testFactoryCreate() {
        val (context, sketch) = getTestContextAndSketch()
        val fetcherFactory = BlurhashUriFetcher.Factory()

        val blurhashUri1 = "blurhash://e6PZfSi_.AyE8^_3t7t7R*WB*0o#DgR4.T_3R*D%xt%MMcV@%itSI9"
        val blurhashUri2 = "blurhash://VEHLh[WB2yk8\$NpyoJadR*=s.7kCMdnjx]S#M|%1%2EN"
        assertNotEquals(blurhashUri1, blurhashUri2)
        fetcherFactory.create(
            ImageRequest(context, blurhashUri1)
                .toRequestContext(sketch, Size(200, 200)),
        )!!.apply {
            assertEquals("e6PZfSi_.AyE8^_3t7t7R*WB*0o#DgR4.T_3R*D%xt%MMcV@%itSI9", blurHashString)
            assertEquals(Size(200, 200), size)
        }
        fetcherFactory.create(
            ImageRequest(context, blurhashUri2)
                .toRequestContext(sketch, Size(100, 100))
        )!!.apply {
            assertEquals("VEHLh[WB2yk8\$NpyoJadR*=s.7kCMdnjx]S#M|%1%2EN", blurHashString)
            assertEquals(Size(100, 100), size)
        }
        val blurhashErrorUri1 = "blurhash://A6PZfSi_.AyE8^_3t7t7R*WB*0o#DgR4.T_3R*D%xt%MMcV@%itSI9"
        val blurhashErrorUri2 = "blurhash:///e6PZfSi_.AyE8^_3t7t7R*WB*0o#DgR4.T_3R*D%xt%MMcV@%itSI9"
        val blurhashErrorUri3 = "data:image/png;base54,4y2u1412421089084901240129"
        val errorSize = Size.Empty

        assertNull(
            fetcherFactory.create(
                ImageRequest(context, blurhashErrorUri1)
                    .toRequestContext(sketch, Size(100, 100))
            )
        )
        assertNull(
            fetcherFactory.create(
                ImageRequest(context, blurhashErrorUri2)
                    .toRequestContext(sketch, Size(100, 100))
            )
        )
        assertNull(
            fetcherFactory.create(
                ImageRequest(context, blurhashErrorUri3)
                    .toRequestContext(sketch, Size(100, 100))
            )
        )
        assertNull(
            fetcherFactory.create(
                ImageRequest(context, blurhashUri1)
                    .toRequestContext(sketch, errorSize)
            )
        )
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val element1 = BlurhashUriFetcher.Factory()
        val element11 = BlurhashUriFetcher.Factory()

        assertEquals(element1, element11)
        assertNotEquals(element1, Any())
        assertNotEquals(element1, null as Any?)

        assertEquals(element1.hashCode(), element11.hashCode())
    }

    @Test
    fun testFactoryToString() {
        assertEquals(expected = "BlurhashUriFetcher", actual = BlurhashUriFetcher.Factory().toString())
    }

    val hashes: List<String> = listOf(
        "LEHLh[WB2yk8pyoJadR*.7kCMdnj",
        "LGF5?xYk^6#M@-5c,1J5@[or[Q6.",
        "L6PZfSi_.AyE_3t7t7R**0o#DgR4",
        "LKN]Rv%2Tw=w]~RBVZRi};RPxuwH",
        "LgG[[{-;xuM{~q%MayM{M{t7RjWB",
        "UEHLh[WB2yk8pyoJadR*.7kCMdnjS#M|%1%2",
        "UHF5?xYk^6#M@-5b,1J5@[or[k6.};FxngOZ",
        "U6PZfSi_.AyE_3t7t7R**0o#DgR4_3R*D%xt",
        "UKN]Rv%2Tw=w]~RBVZRi};RPxuwHtLOtxZ%g",
        "UgG[[{-;xuM{~q%MayM{M{t7RjWBt7t7j[ay",
    )
    val sizes: List<Pair<Int, Int>> = listOf(
        100 to 100,
        100 to 1000,
        200 to 1000,
        1000 to 100,
        1000 to 200,
        500 to 500,
        1000 to 1000,
    )

    @OptIn(ExperimentalEncodingApi::class)
    @Test
    fun testFetchBlur() = runTest {
        val measureTime = measureTime {
            for (blurhash in hashes) {
                for (size in sizes) {
                    val decoded = BlurhashUtil.decodeByte(blurhash, size.first, size.second)
                    val createBitmap = createARGBBitmap(size.first, size.second)
                    createBitmap.installPixels(decoded)
//                    val toPreviewBitmap = createBitmap.toPreviewBitmap()
                }
            }
        }
        print("Time taken: $measureTime")
    }

    @OptIn(ExperimentalEncodingApi::class)
    @Test
    fun testFetchBlurByteArray() = runTest {
        val blurhash = "LEHV6nWB2yk8pyo0adR*.7kCMdnj"
        BlurhashUtil.isValid(blurhash)
        val decoded = BlurhashUtil.decodeByte(blurhash, 1000, 1000)
        val createBitmap = createARGBBitmap(1000, 1000)
        createBitmap.installPixels(decoded)
        val toPreviewBitmap = createBitmap.toPreviewBitmap()
        print("decodeResult: $toPreviewBitmap")
    }
}