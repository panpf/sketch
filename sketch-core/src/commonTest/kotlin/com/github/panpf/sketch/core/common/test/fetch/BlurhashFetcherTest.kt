package com.github.panpf.sketch.core.common.test.fetch

import com.github.panpf.sketch.fetch.BlurhashUtil
import com.github.panpf.sketch.test.utils.createARGBBitmap
import com.github.panpf.sketch.test.utils.toPreviewBitmap
import com.github.panpf.sketch.util.installPixels
import kotlinx.coroutines.test.runTest
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.test.Test
import kotlin.time.measureTime

class BlurhashFetcherTest {


    val hashes :List<String> = listOf(
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
    val sizes :List<Pair<Int,Int>> = listOf(
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