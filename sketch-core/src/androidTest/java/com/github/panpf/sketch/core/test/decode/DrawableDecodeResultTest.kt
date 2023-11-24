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
package com.github.panpf.sketch.core.test.decode

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.datasource.DataFrom.LOCAL
import com.github.panpf.sketch.decode.DrawableDecodeResult
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.internal.createInSampledTransformed
import com.github.panpf.sketch.transform.createRotateTransformed
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DrawableDecodeResultTest {

    @Test
    fun testConstructor() {
        val newDrawable = ColorDrawable(Color.RED)
        val imageInfo = ImageInfo(3000, 500, "image/png", 0)
        val transformedList = listOf(createInSampledTransformed(4), createRotateTransformed(45))
        DrawableDecodeResult(
            newDrawable,
            imageInfo,
            LOCAL,
            transformedList,
            mapOf("age" to "16")
        ).apply {
            Assert.assertTrue(newDrawable === drawable)
            Assert.assertEquals(
                "ImageInfo(width=3000, height=500, mimeType='image/png', exifOrientation=UNDEFINED)",
                imageInfo.toString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertEquals(
                "InSampledTransformed(4), RotateTransformed(45)",
                this.transformedList?.joinToString()
            )
            Assert.assertEquals(
                mapOf("age" to "16"),
                this.extras
            )
        }
    }
}