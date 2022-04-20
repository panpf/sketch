package com.github.panpf.sketch.extensions.test.fetch

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.fetch.AppIconUriFetcher.AppIconDataSource
import com.github.panpf.sketch.fetch.AppIconUriFetcher.Factory
import com.github.panpf.sketch.fetch.newAppIconUri
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.test.contextAndSketch
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppIconUriFetcherTest {

    @Test
    fun testNewUri() {
        Assert.assertEquals(
            "app.icon://packageName/12412",
            newAppIconUri("packageName", 12412)
        )
        Assert.assertEquals(
            "app.icon://packageName1/12413",
            newAppIconUri("packageName1", 12413)
        )
    }

    @Test
    fun testFactory() {
        val (context, sketch) = contextAndSketch()
        val fetcherFactory = Factory()
        val appIconUri = "app.icon://packageName/12412"
        val contentUri = "content://sample_app/sample"

        fetcherFactory.create(sketch, LoadRequest(context, appIconUri))!!.apply {
            Assert.assertEquals("packageName", packageName)
            Assert.assertEquals(12412, versionCode)
        }
        fetcherFactory.create(sketch, DisplayRequest(context, appIconUri))!!.apply {
            Assert.assertEquals("packageName", packageName)
            Assert.assertEquals(12412, versionCode)
        }
        fetcherFactory.create(sketch, DownloadRequest(context, appIconUri))!!.apply {
            Assert.assertEquals("packageName", packageName)
            Assert.assertEquals(12412, versionCode)
        }
        Assert.assertNull(fetcherFactory.create(sketch, LoadRequest(context, contentUri)))
    }

    @Test
    fun testFetch() {
        val (context, sketch) = contextAndSketch()
        val fetcherFactory = Factory()
        val appIconUri = newAppIconUri(
            context.packageName,
            context.packageManager.getPackageInfo(context.packageName, 0).versionCode
        )

        val fetcher = fetcherFactory.create(sketch, LoadRequest(context, appIconUri))!!
        val source = runBlocking {
            fetcher.fetch().dataSource
        }
        Assert.assertTrue(source is AppIconDataSource)
    }
}