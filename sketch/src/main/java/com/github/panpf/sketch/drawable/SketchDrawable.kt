/*
 * Copyright (C) 2019 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.drawable

import android.graphics.Bitmap
import com.github.panpf.sketch.decode.Transformed
import com.github.panpf.sketch.request.DataFrom

interface SketchDrawable {

    val requestKey: String?

    val requestUri: String


    val imageWidth: Int

    val imageHeight: Int

    val imageMimeType: String?

    val imageExifOrientation: Int

    val imageDataFrom: DataFrom?


    val bitmapWidth: Int

    val bitmapHeight: Int

    val bitmapByteCount: Int

    val bitmapConfig: Bitmap.Config?

    val transformedList: List<Transformed>?
}