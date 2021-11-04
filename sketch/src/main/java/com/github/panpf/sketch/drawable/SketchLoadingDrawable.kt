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
import android.graphics.drawable.Drawable
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.ImageFrom
import com.github.panpf.sketch.util.DrawableWrapper
import com.github.panpf.sketch.util.ExifInterface
import java.lang.ref.WeakReference

/**
 * 加载中占位图专用 [Drawable]，可绑定请求
 */
class SketchLoadingDrawable(drawable: Drawable?, displayRequest: DisplayRequest) :
    DrawableWrapper(drawable), SketchRefDrawable {

    private val weakReference: WeakReference<DisplayRequest> = WeakReference(displayRequest)
    private var refDrawable: SketchRefDrawable? = null
    private var sketchDrawable: SketchDrawable? = null

    val request: DisplayRequest?
        get() = weakReference.get()

    override val isRecycled: Boolean
        get() = refDrawable?.isRecycled != false
    override val key: String?
        get() = sketchDrawable?.key
    override val uri: String?
        get() = sketchDrawable?.uri
    override val originWidth: Int
        get() = sketchDrawable?.originWidth ?: 0
    override val originHeight: Int
        get() = sketchDrawable?.originHeight ?: 0
    override val mimeType: String?
        get() = sketchDrawable?.mimeType
    override val exifOrientation: Int
        get() = sketchDrawable?.exifOrientation ?: ExifInterface.ORIENTATION_UNDEFINED
    override val byteCount: Int
        get() = sketchDrawable?.byteCount ?: 0
    override val bitmapConfig: Bitmap.Config?
        get() = sketchDrawable?.bitmapConfig
    override val imageFrom: ImageFrom?
        get() = sketchDrawable?.imageFrom
    override val info: String?
        get() = sketchDrawable?.info

    init {
        if (drawable is SketchRefDrawable) {
            refDrawable = drawable
        }
        if (drawable is SketchDrawable) {
            sketchDrawable = drawable
        }
    }

    override fun setIsDisplayed(callingStation: String, displayed: Boolean) {
        refDrawable?.setIsDisplayed(callingStation, displayed)
    }

    override fun setIsWaitingUse(callingStation: String, waitingUse: Boolean) {
        refDrawable?.setIsWaitingUse(callingStation, waitingUse)
    }
}