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
package com.github.panpf.sketch.test.decode.internal

import androidx.exifinterface.media.ExifInterface
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.decode.internal.createExifOrientationTransformed
import com.github.panpf.sketch.decode.internal.createInSampledTransformed
import com.github.panpf.sketch.decode.internal.createResizeTransformed
import com.github.panpf.sketch.decode.internal.exifOrientationName
import com.github.panpf.sketch.decode.internal.getExifOrientationTransformed
import com.github.panpf.sketch.decode.internal.getInSampledTransformed
import com.github.panpf.sketch.decode.internal.getResizeTransformed
import com.github.panpf.sketch.resize.Resize
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TransformedsTest {

    @Test
    fun testInSampledTransformed() {
        Assert.assertEquals("InSampledTransformed(1)", createInSampledTransformed(1))
        Assert.assertEquals("InSampledTransformed(2)", createInSampledTransformed(2))
        Assert.assertEquals("InSampledTransformed(4)", createInSampledTransformed(4))
        Assert.assertEquals("InSampledTransformed(8)", createInSampledTransformed(8))

        Assert.assertEquals(null, listOf<String>().getInSampledTransformed())
        Assert.assertEquals(
            "InSampledTransformed(2)",
            listOf(createInSampledTransformed(2)).getInSampledTransformed()
        )
        Assert.assertEquals(
            "InSampledTransformed(16)",
            listOf(
                "disruptive1",
                createInSampledTransformed(16),
                "disruptive2"
            ).getInSampledTransformed()
        )
    }

    @Test
    fun testExifOrientationTransformed() {
        Assert.assertEquals(
            "ExifOrientationTransformed(${exifOrientationName(ExifInterface.ORIENTATION_NORMAL)})",
            createExifOrientationTransformed(ExifInterface.ORIENTATION_NORMAL)
        )
        Assert.assertEquals(
            "ExifOrientationTransformed(${exifOrientationName(ExifInterface.ORIENTATION_ROTATE_90)})",
            createExifOrientationTransformed(ExifInterface.ORIENTATION_ROTATE_90)
        )
        Assert.assertEquals(
            "ExifOrientationTransformed(${exifOrientationName(ExifInterface.ORIENTATION_ROTATE_180)})",
            createExifOrientationTransformed(ExifInterface.ORIENTATION_ROTATE_180)
        )
        Assert.assertEquals(
            "ExifOrientationTransformed(${exifOrientationName(ExifInterface.ORIENTATION_TRANSVERSE)})",
            createExifOrientationTransformed(ExifInterface.ORIENTATION_TRANSVERSE)
        )

        Assert.assertEquals(null, listOf<String>().getExifOrientationTransformed())
        Assert.assertEquals(
            "ExifOrientationTransformed(${exifOrientationName(ExifInterface.ORIENTATION_NORMAL)})",
            listOf(createExifOrientationTransformed(ExifInterface.ORIENTATION_NORMAL)).getExifOrientationTransformed()
        )
        Assert.assertEquals(
            "ExifOrientationTransformed(${exifOrientationName(ExifInterface.ORIENTATION_ROTATE_180)})",
            listOf(
                "disruptive1",
                createExifOrientationTransformed(ExifInterface.ORIENTATION_ROTATE_180),
                "disruptive2"
            ).getExifOrientationTransformed()
        )
    }

    @Test
    fun testResizeTransformed() {
        Assert.assertEquals(
            "ResizeTransformed(${Resize(100, 100).key})",
            createResizeTransformed(Resize(100, 100))
        )
        Assert.assertEquals(
            "ResizeTransformed(${Resize(200, 200).key})",
            createResizeTransformed(Resize(200, 200))
        )
        Assert.assertEquals(
            "ResizeTransformed(${Resize(300, 300).key})",
            createResizeTransformed(Resize(300, 300))
        )
        Assert.assertEquals(
            "ResizeTransformed(${Resize(400, 400).key})",
            createResizeTransformed(Resize(400, 400))
        )

        Assert.assertEquals(null, listOf<String>().getResizeTransformed())
        Assert.assertEquals(
            "ResizeTransformed(${Resize(200, 200).key})",
            listOf(createResizeTransformed(Resize(200, 200))).getResizeTransformed()
        )
        Assert.assertEquals(
            "ResizeTransformed(${Resize(500, 500).key})",
            listOf(
                "disruptive1",
                createResizeTransformed(Resize(500, 500)),
                "disruptive2"
            ).getResizeTransformed()
        )
    }
}