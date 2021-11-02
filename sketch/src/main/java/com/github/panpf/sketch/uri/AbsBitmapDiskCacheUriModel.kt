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
package com.github.panpf.sketch.uri

import android.content.Context
import com.github.panpf.sketch.uri.AbsDiskCacheUriModel
import android.graphics.Bitmap
import com.github.panpf.sketch.util.SketchUtils
import com.github.panpf.sketch.cache.BitmapPoolUtils
import com.github.panpf.sketch.Sketch
import java.io.OutputStream
import java.lang.Exception

abstract class AbsBitmapDiskCacheUriModel : AbsDiskCacheUriModel<Bitmap>() {

    @Throws(Exception::class)
    override fun outContent(bitmap: Bitmap, outputStream: OutputStream) {
        bitmap.compress(SketchUtils.bitmapConfigToCompressFormat(bitmap.config), 100, outputStream)
    }

    override fun closeContent(bitmap: Bitmap, context: Context) {
        BitmapPoolUtils.freeBitmapToPool(bitmap, Sketch.with(context).configuration.bitmapPool)
    }
}