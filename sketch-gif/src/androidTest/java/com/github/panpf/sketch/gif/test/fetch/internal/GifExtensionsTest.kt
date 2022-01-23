package com.github.panpf.sketch.gif.test.fetch.internal

import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.AssetDataSource
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.internal.isGif
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.LoadRequest
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GifExtensionsTest {

    @Test
    fun test() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)

        // normal
        val request = LoadRequest(newAssetUri("sample_anim.gif"))
        val fetchResult = FetchResult(AssetDataSource(sketch, request, "sample_anim.gif"), null)
        Assert.assertTrue(fetchResult.headerBytes.isGif())

        // not gif
        val request1 = LoadRequest(newAssetUri("sample.png"))
        val fetchResult1 = FetchResult(AssetDataSource(sketch, request1, "sample.png"), null)
        Assert.assertFalse(fetchResult1.headerBytes.isGif())
    }
}