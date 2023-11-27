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
package com.github.panpf.sketch.gif.movie.test.gif.util

import android.graphics.Bitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.gif.util.allocationByteCountCompat
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GifMovieUtilsTest {

    @Test
    fun testAllocationByteCountCompat() {
        Assert.assertEquals(
            110 * 210 * 4,
            Bitmap.createBitmap(110, 210, Bitmap.Config.ARGB_8888).allocationByteCountCompat
        )

        Assert.assertEquals(
            110 * 210 * 2,
            Bitmap.createBitmap(110, 210, Bitmap.Config.RGB_565).allocationByteCountCompat
        )

        Assert.assertEquals(
            0,
            Bitmap.createBitmap(110, 210, Bitmap.Config.RGB_565)
                .apply { recycle() }
                .allocationByteCountCompat
        )
    }
}