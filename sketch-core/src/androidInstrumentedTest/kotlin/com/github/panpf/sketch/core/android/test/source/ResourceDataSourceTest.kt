package com.github.panpf.sketch.core.android.test.source

import android.content.res.Resources.NotFoundException
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.source.ResourceDataSource
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.asOrThrow
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.md5
import com.github.panpf.tools4j.test.ktx.assertThrow
import okio.Closeable
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResourceDataSourceTest {

    @Test
    fun testConstructor() {
        val context = getTestContext()
        ResourceDataSource(
            resources = context.resources,
            packageName = context.packageName,
            resId = com.github.panpf.sketch.test.utils.core.R.drawable.ic_launcher
        ).apply {
            Assert.assertEquals(
                com.github.panpf.sketch.test.utils.core.R.drawable.ic_launcher,
                this.resId
            )
            Assert.assertEquals(LOCAL, this.dataFrom)
        }
    }

    // TODO test: key

    @Test
    fun testNewInputStream() {
        val context = getTestContext()
        ResourceDataSource(
            resources = context.resources,
            packageName = context.packageName,
            resId = com.github.panpf.sketch.test.utils.core.R.drawable.ic_launcher
        ).apply {
            openSource().asOrThrow<Closeable>().close()
        }

        assertThrow(NotFoundException::class) {
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
    fun testFile() {
        val (context, sketch) = getTestContextAndSketch()
        ResourceDataSource(
            resources = context.resources,
            packageName = context.packageName,
            resId = com.github.panpf.sketch.test.utils.core.R.drawable.ic_launcher
        ).apply {
            val file = getFile(sketch)
            Assert.assertEquals(
                (key + "_data_source").md5() + ".0",
                file.name
            )
        }
    }

    // TODO equals and hashCode

    @Test
    fun testToString() {
        val context = getTestContext()
        ResourceDataSource(
            resources = context.resources,
            packageName = context.packageName,
            resId = com.github.panpf.sketch.test.utils.core.R.drawable.ic_launcher
        ).apply {
            Assert.assertEquals(
                "ResourceDataSource(packageName='${context.packageName}', resId=${com.github.panpf.sketch.test.utils.core.R.drawable.ic_launcher})",
                toString()
            )
        }

        ResourceDataSource(
            resources = context.resources,
            packageName = context.packageName,
            resId = 42
        ).apply {
            Assert.assertEquals(
                "ResourceDataSource(packageName='${context.packageName}', resId=42)",
                toString()
            )
        }
    }
}