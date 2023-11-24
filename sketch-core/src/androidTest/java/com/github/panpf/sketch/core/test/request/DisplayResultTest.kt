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
package com.github.panpf.sketch.core.test.request

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.exifinterface.media.ExifInterface
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.datasource.DataFrom.LOCAL
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.resize.Scale.END_CROP
import com.github.panpf.sketch.core.test.getTestContext
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.transform.createCircleCropTransformed
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DisplayResultTest {

    @Test
    fun test() {
        val context = getTestContext()
        val request1 = DisplayRequest(context, "http://sample.com/sample.jpeg")

        DisplayResult.Success(
            request = request1,
            requestKey = request1.toRequestContext().key,
            requestCacheKey = request1.toRequestContext().cacheKey,
            drawable = ColorDrawable(Color.BLACK),
            imageInfo = ImageInfo(100, 100, "image/jpeg", ExifInterface.ORIENTATION_ROTATE_90),
            dataFrom = LOCAL,
            transformedList = listOf(createCircleCropTransformed(END_CROP)),
            extras = mapOf("age" to "16"),
        ).apply {
            Assert.assertSame(request1, request)
            Assert.assertTrue(drawable is ColorDrawable)
            Assert.assertEquals(
                ImageInfo(
                    width = 100,
                    height = 100,
                    mimeType = "image/jpeg",
                    exifOrientation = ExifInterface.ORIENTATION_ROTATE_90
                ),
                imageInfo
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertEquals(listOf(createCircleCropTransformed(END_CROP)), transformedList)
            Assert.assertEquals(mapOf("age" to "16"), extras)
        }

        DisplayResult.Error(request1, ColorDrawable(Color.BLACK), Exception("")).apply {
            Assert.assertSame(request1, request)
            Assert.assertTrue(drawable is ColorDrawable)
            Assert.assertTrue(throwable is Exception)
        }
    }
}