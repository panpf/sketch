package com.github.panpf.sketch.svg.test.decode.internal

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.source.AssetDataSource
import com.github.panpf.sketch.decode.internal.isSvg
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.images.MyImages
import com.github.panpf.sketch.test.singleton.sketch
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SvgDecodeUtilsTest {

    @Test
    fun test() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch

        // normal
        val request = ImageRequest(context, MyImages.svg.uri)
        val fetchResult =
            FetchResult(AssetDataSource(sketch, request, MyImages.svg.fileName), null)
        Assert.assertTrue(fetchResult.headerBytes.isSvg())

        // error
        val request1 = ImageRequest(context, MyImages.png.uri)
        val fetchResult1 =
            FetchResult(AssetDataSource(sketch, request1, MyImages.png.fileName), null)
        Assert.assertFalse(fetchResult1.headerBytes.isSvg())
    }
}