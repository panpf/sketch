package com.github.panpf.sketch.svg.test.decode.internal

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.datasource.AssetDataSource
import com.github.panpf.sketch.decode.internal.isSvg
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.sketch
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
        val request = LoadRequest(context, newAssetUri("sample.svg"))
        val fetchResult = FetchResult(AssetDataSource(sketch, request, "sample.svg"), null)
        Assert.assertTrue(fetchResult.headerBytes.isSvg())

        // error
        val request1 = LoadRequest(context, newAssetUri("sample.png"))
        val fetchResult1 = FetchResult(AssetDataSource(sketch, request1, "sample.png"), null)
        Assert.assertFalse(fetchResult1.headerBytes.isSvg())
    }
}