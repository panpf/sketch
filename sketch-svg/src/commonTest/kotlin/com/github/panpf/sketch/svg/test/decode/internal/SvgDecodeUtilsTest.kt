package com.github.panpf.sketch.svg.test.decode.internal

import com.github.panpf.sketch.decode.internal.isSvg
import com.github.panpf.sketch.images.MyImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SvgDecodeUtilsTest {

    @Test
    fun test() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        // normal
        val request = ImageRequest(context, MyImages.svg.uri)
        val fetchResult = sketch.components.newFetcherOrThrow(request).fetch().getOrThrow()
        assertTrue(fetchResult.headerBytes.isSvg())

        // error
        val request1 = ImageRequest(context, MyImages.png.uri)
        val fetchResult1 = sketch.components.newFetcherOrThrow(request1).fetch().getOrThrow()
        assertFalse(fetchResult1.headerBytes.isSvg())
    }
}