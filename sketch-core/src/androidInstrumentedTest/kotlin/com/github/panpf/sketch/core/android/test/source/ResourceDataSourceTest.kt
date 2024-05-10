package com.github.panpf.sketch.core.android.test.source

import android.content.res.Resources.NotFoundException
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.fetch.newResourceUri
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.source.ResourceDataSource
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.asOrThrow
import com.github.panpf.sketch.util.sha256String
import com.github.panpf.tools4j.test.ktx.assertThrow
import okio.Closeable
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResourceDataSourceTest {

    @Test
    fun testConstructor() {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(
            context,
            newResourceUri(com.github.panpf.sketch.test.utils.core.R.drawable.ic_launcher)
        )
        ResourceDataSource(
            sketch = sketch,
            request = request,
            packageName = context.packageName,
            resources = context.resources,
            resId = com.github.panpf.sketch.test.utils.core.R.drawable.ic_launcher
        ).apply {
            Assert.assertTrue(sketch === this.sketch)
            Assert.assertTrue(request === this.request)
            Assert.assertEquals(
                com.github.panpf.sketch.test.utils.core.R.drawable.ic_launcher,
                this.resId
            )
            Assert.assertEquals(LOCAL, this.dataFrom)
        }
    }

    @Test
    fun testNewInputStream() {
        val (context, sketch) = getTestContextAndSketch()
        ResourceDataSource(
            sketch = sketch,
            request = ImageRequest(
                context,
                newResourceUri(com.github.panpf.sketch.test.utils.core.R.drawable.ic_launcher)
            ),
            packageName = context.packageName,
            resources = context.resources,
            resId = com.github.panpf.sketch.test.utils.core.R.drawable.ic_launcher
        ).apply {
            openSource().asOrThrow<Closeable>().close()
        }

        assertThrow(NotFoundException::class) {
            ResourceDataSource(
                sketch = sketch,
                request = ImageRequest(context, newResourceUri(42)),
                packageName = context.packageName,
                resources = context.resources,
                resId = 42
            ).apply {
                openSource()
            }
        }
    }

    @Test
    fun testFile() {
        val (context, sketch) = getTestContextAndSketch()
        ResourceDataSource(
            sketch = sketch,
            request = ImageRequest(
                context,
                newResourceUri(com.github.panpf.sketch.test.utils.core.R.drawable.ic_launcher)
            ),
            packageName = context.packageName,
            resources = context.resources,
            resId = com.github.panpf.sketch.test.utils.core.R.drawable.ic_launcher
        ).apply {
            val file = getFile()
            Assert.assertEquals(
                (request.uriString + "_data_source").sha256String() + ".0",
                file.name
            )
        }
    }

    @Test
    fun testToString() {
        val (context, sketch) = getTestContextAndSketch()
        ResourceDataSource(
            sketch = sketch,
            request = ImageRequest(
                context,
                newResourceUri(com.github.panpf.sketch.test.utils.core.R.drawable.ic_launcher)
            ),
            packageName = context.packageName,
            resources = context.resources,
            resId = com.github.panpf.sketch.test.utils.core.R.drawable.ic_launcher
        ).apply {
            Assert.assertEquals(
                "ResourceDataSource(${com.github.panpf.sketch.test.utils.core.R.drawable.ic_launcher})",
                toString()
            )
        }

        ResourceDataSource(
            sketch = sketch,
            request = ImageRequest(context, newResourceUri(42)),
            packageName = context.packageName,
            resources = context.resources,
            resId = 42
        ).apply {
            Assert.assertEquals("ResourceDataSource(42)", toString())
        }
    }
}