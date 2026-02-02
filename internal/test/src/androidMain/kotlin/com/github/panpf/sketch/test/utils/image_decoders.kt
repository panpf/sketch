/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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

package com.github.panpf.sketch.test.utils

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build.VERSION_CODES
import androidx.annotation.RequiresApi
import com.github.panpf.sketch.source.DataSource
import okio.buffer
import java.nio.ByteBuffer

@RequiresApi(VERSION_CODES.P)
fun DataSource.decodeImageUseImageDecoder(
    sampleSize: Int? = null,
    mutable: Boolean? = null
): Bitmap {
    val byteArray = openSource().buffer().use { it.readByteArray() }
    return ImageDecoder.decodeBitmap(
        ImageDecoder.createSource(ByteBuffer.wrap(byteArray))
    ) { decoder, _, _ ->
        if (sampleSize != null) {
            decoder.setTargetSampleSize(sampleSize)
        }
        if (mutable != null) {
            decoder.isMutableRequired = mutable
        }
    }
}