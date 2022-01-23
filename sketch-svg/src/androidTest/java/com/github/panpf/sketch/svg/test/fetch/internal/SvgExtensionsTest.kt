package com.github.panpf.sketch.svg.test.fetch.internal

import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.AssetDataSource
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.internal.isSvg
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.LoadRequest
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SvgExtensionsTest {

    @Test
    fun test() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)

        // normal
        val request = LoadRequest(newAssetUri("sample.svg"))
        val fetchResult = FetchResult(AssetDataSource(sketch, request, "sample.svg"), null)
        Assert.assertTrue(fetchResult.headerBytes.isSvg())

        // not svg
        val request1 = LoadRequest(newAssetUri("sample.png"))
        val fetchResult1 = FetchResult(AssetDataSource(sketch, request1, "sample.png"), null)
        Assert.assertFalse(fetchResult1.headerBytes.isSvg())
    }
}