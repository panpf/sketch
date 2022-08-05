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
import android.widget.RemoteViews
import androidx.annotation.IdRes
import androidx.core.graphics.drawable.toBitmap

class RemoteViewsDisplayTarget(
    private val remoteViews: RemoteViews,
    @IdRes private val imageViewId: Int,
    private val ignoreNullDrawable: Boolean = false,
    private val onUpdated: () -> Unit,
) : DisplayTarget {

    override fun onStart(placeholder: Drawable?) {
        if (placeholder != null || !ignoreNullDrawable) {
            setDrawable(placeholder)
        }
    }

    override fun onError(error: Drawable?) {
        if (error != null || !ignoreNullDrawable) {
            setDrawable(error)
        }
    }

    override fun onSuccess(result: Drawable) = setDrawable(result)

    private fun setDrawable(result: Drawable?) {
        remoteViews.setImageViewBitmap(imageViewId, result?.toBitmap())
        onUpdated()
    }
}
