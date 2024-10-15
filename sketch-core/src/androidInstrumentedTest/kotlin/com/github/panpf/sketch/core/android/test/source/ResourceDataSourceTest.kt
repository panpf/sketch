package com.github.panpf.sketch.core.android.test.source

import android.content.res.Resources.NotFoundException
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.fetch.newResourceUri
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.source.ResourceDataSource
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.asOrThrow
import com.github.panpf.sketch.test.utils.getTestContext
import okio.Closeable
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class ResourceDataSourceTest {

    @Test
    fun testConstructor() {
        val context = getTestContext()
        ResourceDataSource(
            resources = context.resources,
            packageName = context.packageName,
            resId = com.github.panpf.sketch.test.R.drawable.ic_launcher
        ).apply {
            assertEquals(
                com.github.panpf.sketch.test.R.drawable.ic_launcher,
                this.resId
            )
            assertEquals(LOCAL, this.dataFrom)
        }
    }

    @Test
    fun testKey() {
        val context = getTestContext()
        ResourceDataSource(
            resources = context.resources,
            packageName = context.packageName,
            resId = com.github.panpf.sketch.test.R.drawable.ic_launcher
        ).apply {
            assertEquals(
                newResourceUri(packageName = packageName, resId = resId),
                key
            )
        }
    }

    @Test
    fun testOpenSource() {
        val context = getTestContext()
        ResourceDataSource(
            resources = context.resources,
            packageName = context.packageName,
            resId = com.github.panpf.sketch.test.R.drawable.ic_launcher
        ).apply {
            openSource().asOrThrow<Closeable>().close()
        }

        assertFailsWith(NotFoundException::class) {
            ResourceDataSource(
                resources = context.resources,
                packageName = context.packageName,
                resId = 42
            ).apply {
                openSource()
            }
        }
    }

    @Test
    fun testGetFile() {
        val (context, sketch) = getTestContextAndSketch()
        ResourceDataSource(
            resources = context.resources,
            packageName = context.packageName,
            resId = com.github.panpf.sketch.test.R.drawable.ic_launcher
        ).getFile(sketch).apply {
            assertTrue(actual = toString().contains("/${DiskCache.DownloadBuilder.SUB_DIRECTORY_NAME}/"))
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val context = getTestContext()
        val element1 = ResourceDataSource(
            resources = context.resources,
            packageName = context.packageName,
            resId = com.github.panpf.sketch.test.R.drawable.ic_launcher
        )
        val element11 = ResourceDataSource(
            resources = context.resources,
            packageName = context.packageName,
            resId = com.github.panpf.sketch.test.R.drawable.ic_launcher
        )
        val element2 = ResourceDataSource(
            resources = context.resources,
            packageName = context.packageName + "1",
            resId = 42
        )
        val element3 = ResourceDataSource(
            resources = context.resources,
            packageName = context.packageName,
            resId = 43
        )

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element2, element3)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element1.hashCode(), element3.hashCode())
        assertNotEquals(element2.hashCode(), element3.hashCode())
    }

    @Test
    fun testToString() {
        val context = getTestContext()
        ResourceDataSource(
            resources = context.resources,
            packageName = context.packageName,
            resId = com.github.panpf.sketch.test.R.drawable.ic_launcher
        ).apply {
            assertEquals(
                "ResourceDataSource(packageName='${context.packageName}', resId=${com.github.panpf.sketch.test.R.drawable.ic_launcher})",
                toString()
            )
        }

        ResourceDataSource(
            resources = context.resources,
            packageName = context.packageName,
            resId = 42
        ).apply {
            assertEquals(
                "ResourceDataSource(packageName='${context.packageName}', resId=42)",
                toString()
            )
        }
    }
}