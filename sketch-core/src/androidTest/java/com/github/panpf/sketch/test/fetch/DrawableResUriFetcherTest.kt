package com.github.panpf.sketch.test.fetch

import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.DrawableResDataSource
import com.github.panpf.sketch.fetch.DrawableResUriFetcher
import com.github.panpf.sketch.fetch.newDrawableResUri
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.request.LoadRequest
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DrawableResUriFetcherTest {

    @Test
    fun testNewUri() {
        Assert.assertEquals(
            "drawable.resource://5251512",
            newDrawableResUri(5251512).toString()
        )
        Assert.assertEquals(
            "drawable.resource://5251513",
            newDrawableResUri(5251513).toString()
        )
    }

    @Test
    fun testFactory() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)
        val fetcherFactory = DrawableResUriFetcher.Factory()
        val drawableResUri = newDrawableResUri(5251512)
        val httpUri = "http://sample.com/sample.jpg"
        val contentUri = "content://sample_app/sample"

        fetcherFactory.create(sketch, LoadRequest.new(drawableResUri))!!.apply {
            Assert.assertEquals(5251512, drawableResId)
        }
        fetcherFactory.create(sketch, DisplayRequest.new(drawableResUri))!!.apply {
            Assert.assertEquals(5251512, drawableResId)
        }
        Assert.assertNull(fetcherFactory.create(sketch, DownloadRequest.new(drawableResUri)))
        Assert.assertNull(fetcherFactory.create(sketch, LoadRequest.new(httpUri)))
        Assert.assertNull(fetcherFactory.create(sketch, LoadRequest.new(contentUri)))
    }

    @Test
    fun testFetch() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)
        val fetcherFactory = DrawableResUriFetcher.Factory()
        val drawableResUri = newDrawableResUri(5251512)

        val fetcher = fetcherFactory.create(sketch, LoadRequest.new(drawableResUri))!!
        val source = runBlocking {
            fetcher.fetch().source
        }
        Assert.assertTrue(source is DrawableResDataSource)
    }
}