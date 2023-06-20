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
package com.github.panpf.sketch.target

import android.graphics.drawable.Drawable
import androidx.annotation.MainThread
import com.github.panpf.sketch.request.DisplayRequest

/**
 * Target dedicated to [DisplayRequest], which requires Drawable
 */
interface DisplayTarget : Target {

    /**
     * If display counting is not supported, memory caching will not be used.
     * If true is returned, call the SketchCountBitmapDrawable.countBitmap.setIsDisplayed() method to record the number of impressions.
     * Otherwise, there will be image confusion and crashes
     */
    val supportDisplayCount: Boolean

    /**
     * Called when the request starts.
     */
    @MainThread
    fun onStart(placeholder: Drawable?) {
    }

    /**
     * Called if the request completes successfully.
     */
    @MainThread
    fun onSuccess(result: Drawable) {
    }

    /**
     * Called if an error occurs while executing the request.
     */
    @MainThread
    fun onError(error: Drawable?) {
    }
}