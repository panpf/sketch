package com.github.panpf.sketch.test.datasource

import android.content.res.Resources
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.test.R
import com.github.panpf.sketch.datasource.ResourceDataSource
import com.github.panpf.sketch.fetch.newResourceUri
import com.github.panpf.sketch.request.DataFrom
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.tools4j.test.ktx.assertThrow
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResourceDataSourceTest {

    @Test
    fun testConstructor() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)
        val request = LoadRequest(context.newResourceUri(R.drawable.ic_launcher))
        ResourceDataSource(
            sketch = sketch,
            request = request,
            resources = context.resources,
            drawableId = R.drawable.ic_launcher
        ).apply {
            Assert.assertTrue(sketch === this.sketch)
            Assert.assertTrue(request === this.request)
            Assert.assertEquals(R.drawable.ic_launcher, this.drawableId)
            Assert.assertEquals(DataFrom.LOCAL, this.from)
        }
    }

    @Test
    fun testLength() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)
        ResourceDataSource(
            sketch = sketch,
            request = LoadRequest(context.newResourceUri(R.drawable.ic_launcher)),
            resources = context.resources,
            drawableId = R.drawable.ic_launcher
        ).apply {
            Assert.assertEquals(14792, length())
        }

        assertThrow(Resources.NotFoundException::class) {
            ResourceDataSource(
                sketch = sketch,
                request = LoadRequest(context.newResourceUri(42)),
                resources = context.resources,
                drawableId = 42
            ).apply {
                length()
            }
        }
    }

    @Test
    fun testNewFileDescriptor() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)
        ResourceDataSource(
            sketch = sketch,
            request = LoadRequest(context.newResourceUri(R.drawable.ic_launcher)),
            resources = context.resources,
            drawableId = R.drawable.ic_launcher
        ).apply {
            Assert.assertNotNull(newFileDescriptor())
        }

        assertThrow(Resources.NotFoundException::class) {
            ResourceDataSource(
                sketch = sketch,
                request = LoadRequest(context.newResourceUri(42)),
                resources = context.resources,
                drawableId = 42
            ).apply {
                newFileDescriptor()
            }
        }
    }

    @Test
    fun testNewInputStream() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)
        ResourceDataSource(
            sketch = sketch,
            request = LoadRequest(context.newResourceUri(R.drawable.ic_launcher)),
            resources = context.resources,
            drawableId = R.drawable.ic_launcher
        ).apply {
            newInputStream().close()
        }

        assertThrow(Resources.NotFoundException::class) {
            ResourceDataSource(
                sketch = sketch,
                request = LoadRequest(context.newResourceUri(42)),
                resources = context.resources,
                drawableId = 42
            ).apply {
                newInputStream()
            }
        }
    }

    @Test
    fun testToString() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)
        ResourceDataSource(
            sketch = sketch,
            request = LoadRequest(context.newResourceUri(R.drawable.ic_launcher)),
            resources = context.resources,
            drawableId = R.drawable.ic_launcher
        ).apply {
            Assert.assertEquals(
                "ResourceDataSource(drawableId=${R.drawable.ic_launcher})",
                toString()
            )
        }

        ResourceDataSource(
            sketch = sketch,
            request = LoadRequest(context.newResourceUri(42)),
            resources = context.resources,
            drawableId = 42
        ).apply {
            Assert.assertEquals("ResourceDataSource(drawableId=42)", toString())
        }
    }
}