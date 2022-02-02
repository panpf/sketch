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

import androidx.annotation.MainThread
import com.github.panpf.sketch.cache.CountBitmap
import com.github.panpf.sketch.request.DataFrom

class SketchCountBitmapDrawable constructor(
    private val countBitmap: CountBitmap,
    imageDataFrom: DataFrom,
) : SketchBitmapDrawable(
    countBitmap.requestKey,
    countBitmap.imageUri,
    countBitmap.imageInfo,
    countBitmap.exifOrientation,
    imageDataFrom,
    countBitmap.transformedList,
    countBitmap.bitmap!!
), SketchCountDrawable {

    override val isRecycled: Boolean
        get() = countBitmap.isRecycled

    @MainThread
    override fun setIsDisplayed(callingStation: String, displayed: Boolean) {
        countBitmap.setIsDisplayed(callingStation, displayed)
    }

    @MainThread
    override fun setIsWaiting(callingStation: String, waitingUse: Boolean) {
        countBitmap.setIsWaiting(callingStation, waitingUse)
    }
}