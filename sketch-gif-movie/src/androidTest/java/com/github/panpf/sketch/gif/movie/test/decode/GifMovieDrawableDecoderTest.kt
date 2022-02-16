package com.github.panpf.sketch.gif.movie.test.decode

import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.AssetDataSource
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.GifMovieDrawableDecoder
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.DataFrom
import com.github.panpf.sketch.request.DataFrom.LOCAL
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.internal.ImageRequest
import com.github.panpf.sketch.request.internal.RequestExtras
import com.github.panpf.sketch.target.Target
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.io.FileDescriptor
import java.io.InputStream

@RunWith(AndroidJUnit4::class)
class GifMovieDrawableDecoderTest {

    @Test
    fun testFactory() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)

        // normal
        val request = DisplayRequest(newAssetUri("sample_anim.gif"), TestTarget())
        val fetchResult = FetchResult(AssetDataSource(sketch, request, "sample_anim.gif"), null)
        Assert.assertNotNull(
            GifMovieDrawableDecoder.Factory().create(sketch, request, RequestExtras(), fetchResult)
        )

        // not gif
        val request1 = DisplayRequest(newAssetUri("sample.png"), TestTarget())
        val fetchResult1 = FetchResult(AssetDataSource(sketch, request1, "sample.png"), null)
        Assert.assertNull(
            GifMovieDrawableDecoder.Factory().create(sketch, request1, RequestExtras(), fetchResult1)
        )

        // disabledAnimationDrawable true
        val request2 = DisplayRequest(newAssetUri("sample_anim.gif"), TestTarget()) {
            disabledAnimationDrawable()
        }
        val fetchResult2 = FetchResult(ErrorDataSource(sketch, request2, LOCAL), null)
        Assert.assertNull(
            GifMovieDrawableDecoder.Factory().create(sketch, request2, RequestExtras(), fetchResult2)
        )

        // mimeType error
        val request3 = DisplayRequest(newAssetUri("sample_anim.gif"), TestTarget())
        val fetchResult3 = FetchResult(
            AssetDataSource(sketch, request3, "sample_anim.gif"),
            "image/jpeg",
        )
        Assert.assertNotNull(
            GifMovieDrawableDecoder.Factory().create(sketch, request3, RequestExtras(), fetchResult3)
        )
    }

    @Test
    fun testDecodeDrawable() {
        // todo Write test cases
    }

    private class TestTarget : Target

    private class ErrorDataSource(
        override val sketch: Sketch,
        override val request: ImageRequest,
        override val dataFrom: DataFrom
    ) : DataSource {
        override fun length(): Long = throw UnsupportedOperationException("Unsupported length()")

        override fun newFileDescriptor(): FileDescriptor =
            throw UnsupportedOperationException("Unsupported newFileDescriptor()")

        override fun newInputStream(): InputStream =
            throw UnsupportedOperationException("Unsupported newInputStream()")
    }
}