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
import android.graphics.drawable.BitmapDrawable
import com.github.panpf.sketch.request.ImageFrom

class SketchBitmapDrawable(refBitmap: SketchRefBitmap, imageFrom: ImageFrom) :
    BitmapDrawable(null, refBitmap.bitmap), SketchRefDrawable {

    private val refBitmap: SketchRefBitmap
    override val imageFrom: ImageFrom
    override val key: String
        get() = refBitmap.key
    override val uri: String
        get() = refBitmap.uri
    override val originWidth: Int
        get() = refBitmap.attrs.width
    override val originHeight: Int
        get() = refBitmap.attrs.height
    override val mimeType: String
        get() = refBitmap.attrs.mimeType
    override val exifOrientation: Int
        get() = refBitmap.attrs.exifOrientation
    override val info: String
        get() = refBitmap.info
    override val byteCount: Int
        get() = refBitmap.byteCount
    override val bitmapConfig: Bitmap.Config?
        get() = refBitmap.bitmapConfig

    override val isRecycled: Boolean
        get() = refBitmap.isRecycled

    init {
        require(!refBitmap.isRecycled) { "refBitmap recycled. " + refBitmap.info }
        this.refBitmap = refBitmap
        this.imageFrom = imageFrom

        // 这一步很重要，让BitmapDrawable的density和Bitmap的density保持一致
        // 这样getIntrinsicWidth()和getIntrinsicHeight()方法得到的就是bitmap的真实的（未经过缩放）尺寸
        setTargetDensity(refBitmap.bitmap!!.density)
    }

    override fun setIsDisplayed(callingStation: String, displayed: Boolean) {
        refBitmap.setIsDisplayed(callingStation, displayed)
    }

    override fun setIsWaitingUse(callingStation: String, waitingUse: Boolean) {
        refBitmap.setIsWaitingUse(callingStation, waitingUse)
    }
}