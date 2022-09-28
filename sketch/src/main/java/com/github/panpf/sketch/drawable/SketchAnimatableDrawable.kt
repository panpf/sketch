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
package com.github.panpf.sketch.drawable

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.drawable.internal.AnimatableDrawableWrapper
import com.github.panpf.sketch.util.Size

/**
 * Provide unified Callback support for Animatable2, Animatable2Compat, Animatable
 */
@SuppressLint("RestrictedApi")
class SketchAnimatableDrawable constructor(
    private val animatableDrawable: Drawable,
    override val imageUri: String,
    override val requestKey: String,
    override val requestCacheKey: String,
    override val imageInfo: ImageInfo,
    override val dataFrom: DataFrom,
    override val transformedList: List<String>?,
    override val extras: Map<String, String>?,
) : AnimatableDrawableWrapper(animatableDrawable), SketchDrawable {

    @SuppressLint("RestrictedApi")
    override fun mutate(): SketchAnimatableDrawable {
        val mutateDrawable = wrappedDrawable.mutate()
        return if (mutateDrawable !== wrappedDrawable) {
            SketchAnimatableDrawable(
                animatableDrawable = mutateDrawable,
                imageUri = imageUri,
                requestKey = requestKey,
                requestCacheKey = requestCacheKey,
                imageInfo = imageInfo,
                dataFrom = dataFrom,
                transformedList = transformedList,
                extras = extras,
            )
        } else {
            this
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SketchAnimatableDrawable) return false
        if (animatableDrawable != other.animatableDrawable) return false
        if (imageUri != other.imageUri) return false
        if (requestKey != other.requestKey) return false
        if (requestCacheKey != other.requestCacheKey) return false
        if (imageInfo != other.imageInfo) return false
        if (dataFrom != other.dataFrom) return false
        if (transformedList != other.transformedList) return false
        return true
    }

    override fun hashCode(): Int {
        var result = animatableDrawable.hashCode()
        result = 31 * result + imageUri.hashCode()
        result = 31 * result + requestKey.hashCode()
        result = 31 * result + requestCacheKey.hashCode()
        result = 31 * result + imageInfo.hashCode()
        result = 31 * result + dataFrom.hashCode()
        result = 31 * result + (transformedList?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        val size = Size(intrinsicWidth, intrinsicHeight)
        return "SketchAnimatableDrawable($animatableDrawable,${size},${imageInfo.toShortString()},$dataFrom,$transformedList,$extras,'$requestKey')"
    }
}