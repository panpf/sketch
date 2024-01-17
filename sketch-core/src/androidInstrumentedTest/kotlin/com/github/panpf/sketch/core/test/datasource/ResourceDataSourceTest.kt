/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.panpf.sketch.core.test.datasource

import android.content.res.Resources
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.test.utils.getTestContextAndNewSketch
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.datasource.ResourceDataSource
import com.github.panpf.sketch.fetch.newResourceUri
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.tools4j.test.ktx.assertThrow
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

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
            Assert.assertEquals(DataFrom.LOCAL, this.dataFrom)
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
            openInputStream().close()
        }

        assertThrow(Resources.NotFoundException::class) {
            ResourceDataSource(
                sketch = sketch,
                request = ImageRequest(context, newResourceUri(42)),
                packageName = context.packageName,
                resources = context.resources,
                resId = 42
            ).apply {
                openInputStream()
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