package com.github.panpf.sketch.core.android.test.source

import android.content.res.Resources.NotFoundException
import com.github.panpf.sketch.fetch.newResourceUri
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.source.ResourceDataSource

@RunWith(AndroidJUnit4::class)
class ResourceDataSourceTest {

    @Test
    fun testConstructor() {
        val (context, sketch) = getTestContextAndNewSketch()
        val request = ImageRequest(
            context,
            newResourceUri(com.github.panpf.sketch.test.utils.R.drawable.ic_launcher)
        )
        ResourceDataSource(
            sketch = sketch,
            request = request,
            packageName = context.packageName,
            resources = context.resources,
            resId = com.github.panpf.sketch.test.utils.R.drawable.ic_launcher
        ).apply {
            Assert.assertTrue(sketch === this.sketch)
            Assert.assertTrue(request === this.request)
            Assert.assertEquals(
                com.github.panpf.sketch.test.utils.R.drawable.ic_launcher,
                this.resId
            )
            Assert.assertEquals(LOCAL, this.dataFrom)
        }
    }

    @Test
    fun testNewInputStream() {
        val (context, sketch) = getTestContextAndNewSketch()
        ResourceDataSource(
            sketch = sketch,
            request = ImageRequest(
                context,
                newResourceUri(com.github.panpf.sketch.test.utils.R.drawable.ic_launcher)
            ),
            packageName = context.packageName,
            resources = context.resources,
            resId = com.github.panpf.sketch.test.utils.R.drawable.ic_launcher
        ).apply {
            openSource().close()
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
        val (context, sketch) = getTestContextAndNewSketch()
        ResourceDataSource(
            sketch = sketch,
            request = ImageRequest(
                context,
                newResourceUri(com.github.panpf.sketch.test.utils.R.drawable.ic_launcher)
            ),
            packageName = context.packageName,
            resources = context.resources,
            resId = com.github.panpf.sketch.test.utils.R.drawable.ic_launcher
        ).apply {
            val file = getFile()
            Assert.assertEquals("0257c278c299ae9196d4e58fbf234e56.0", file.name)
        }
    }

    @Test
    fun testToString() {
        val (context, sketch) = getTestContextAndNewSketch()
        ResourceDataSource(
            sketch = sketch,
            request = ImageRequest(
                context,
                newResourceUri(com.github.panpf.sketch.test.utils.R.drawable.ic_launcher)
            ),
            packageName = context.packageName,
            resources = context.resources,
            resId = com.github.panpf.sketch.test.utils.R.drawable.ic_launcher
        ).apply {
            Assert.assertEquals(
                "ResourceDataSource(${com.github.panpf.sketch.test.utils.R.drawable.ic_launcher})",
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