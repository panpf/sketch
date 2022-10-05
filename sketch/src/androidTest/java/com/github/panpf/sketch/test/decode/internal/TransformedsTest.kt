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

import android.graphics.Rect
import androidx.exifinterface.media.ExifInterface
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.decode.internal.createExifOrientationTransformed
import com.github.panpf.sketch.decode.internal.createInSampledTransformed
import com.github.panpf.sketch.decode.internal.createResizeTransformed
import com.github.panpf.sketch.decode.internal.createSubsamplingTransformed
import com.github.panpf.sketch.decode.internal.getExifOrientationTransformed
import com.github.panpf.sketch.decode.internal.getInSampledTransformed
import com.github.panpf.sketch.decode.internal.getResizeTransformed
import com.github.panpf.sketch.decode.internal.getSubsamplingTransformed
import com.github.panpf.sketch.resize.Resize
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TransformedsTest {

    @Test
    fun testSubsamplingTransformed() {
        Assert.assertEquals(
            "SubsamplingTransformed(1,2,3,4)",
            createSubsamplingTransformed(Rect(1, 2, 3, 4))
        )
        Assert.assertEquals(
            "SubsamplingTransformed(2,3,4,5)",
            createSubsamplingTransformed(Rect(2, 3, 4, 5))
        )
        Assert.assertEquals(
            "SubsamplingTransformed(3,4,5,6)",
            createSubsamplingTransformed(Rect(3, 4, 5, 6))
        )
        Assert.assertEquals(
            "SubsamplingTransformed(4,5,6,7)",
            createSubsamplingTransformed(Rect(4, 5, 6, 7))
        )

        Assert.assertEquals(null, listOf<String>().getSubsamplingTransformed())
        Assert.assertEquals(
            "SubsamplingTransformed(1,2,3,4)",
            listOf(createSubsamplingTransformed(Rect(1, 2, 3, 4))).getSubsamplingTransformed()
        )
        Assert.assertEquals(
            "SubsamplingTransformed(1,2,3,4)",
            listOf(
                "disruptive1",
                createSubsamplingTransformed(Rect(1, 2, 3, 4)),
                "disruptive2"
            ).getSubsamplingTransformed()
        )
    }

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
            "ExifOrientationTransformed(NORMAL)",
            createExifOrientationTransformed(ExifInterface.ORIENTATION_NORMAL)
        )
        Assert.assertEquals(
            "ExifOrientationTransformed(ROTATE_90)",
            createExifOrientationTransformed(ExifInterface.ORIENTATION_ROTATE_90)
        )
        Assert.assertEquals(
            "ExifOrientationTransformed(ROTATE_180)",
            createExifOrientationTransformed(ExifInterface.ORIENTATION_ROTATE_180)
        )
        Assert.assertEquals(
            "ExifOrientationTransformed(TRANSVERSE)",
            createExifOrientationTransformed(ExifInterface.ORIENTATION_TRANSVERSE)
        )

        Assert.assertEquals(null, listOf<String>().getExifOrientationTransformed())
        Assert.assertEquals(
            "ExifOrientationTransformed(NORMAL)",
            listOf(createExifOrientationTransformed(ExifInterface.ORIENTATION_NORMAL)).getExifOrientationTransformed()
        )
        Assert.assertEquals(
            "ExifOrientationTransformed(ROTATE_180)",
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
            "ResizeTransformed(100x100,LESS_PIXELS,CENTER_CROP)",
            createResizeTransformed(Resize(100, 100))
        )
        Assert.assertEquals(
            "ResizeTransformed(200x200,LESS_PIXELS,CENTER_CROP)",
            createResizeTransformed(Resize(200, 200))
        )
        Assert.assertEquals(
            "ResizeTransformed(300x300,LESS_PIXELS,CENTER_CROP)",
            createResizeTransformed(Resize(300, 300))
        )
        Assert.assertEquals(
            "ResizeTransformed(400x400,LESS_PIXELS,CENTER_CROP)",
            createResizeTransformed(Resize(400, 400))
        )

        Assert.assertEquals(null, listOf<String>().getResizeTransformed())
        Assert.assertEquals(
            "ResizeTransformed(200x200,LESS_PIXELS,CENTER_CROP)",
            listOf(createResizeTransformed(Resize(200, 200))).getResizeTransformed()
        )
        Assert.assertEquals(
            "ResizeTransformed(500x500,LESS_PIXELS,CENTER_CROP)",
            listOf(
                "disruptive1",
                createResizeTransformed(Resize(500, 500)),
                "disruptive2"
            ).getResizeTransformed()
        )
    }
}