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

/**
 * Set Drawable to RemoteViews
 */
class RemoteViewsDisplayTarget(
    private val remoteViews: RemoteViews,
    @IdRes private val imageViewId: Int,
    private val ignoreNullDrawable: Boolean = false,
    private val onUpdated: () -> Unit,
) : DisplayTarget {

    override val supportDisplayCount: Boolean = false

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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as RemoteViewsDisplayTarget
        if (remoteViews != other.remoteViews) return false
        if (imageViewId != other.imageViewId) return false
        if (ignoreNullDrawable != other.ignoreNullDrawable) return false
        if (onUpdated != other.onUpdated) return false
        return true
    }

    override fun hashCode(): Int {
        var result = remoteViews.hashCode()
        result = 31 * result + imageViewId
        result = 31 * result + ignoreNullDrawable.hashCode()
        result = 31 * result + onUpdated.hashCode()
        return result
    }

    override fun toString(): String {
        return "RemoteViewsDisplayTarget(remoteViews=$remoteViews, imageViewId=$imageViewId, ignoreNullDrawable=$ignoreNullDrawable, onUpdated=$onUpdated)"
    }
}
