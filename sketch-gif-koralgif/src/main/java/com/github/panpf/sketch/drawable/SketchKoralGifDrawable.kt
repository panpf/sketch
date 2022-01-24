package com.github.panpf.sketch.drawable

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

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.appcompat.graphics.drawable.DrawableWrapper
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.request.DataFrom

/**
 * 增加了从 BitmapPool 中寻找可复用 Bitmap 的功能以及图片的信息
 */
@SuppressLint("RestrictedApi")
class SketchKoralGifDrawable(
    override val requestKey: String,
    override val requestUri: String,
    private val imageInfo: ImageInfo,
    override val imageDataFrom: DataFrom,
    private val gifDrawable: ReuseGifDrawable,
) : DrawableWrapper(gifDrawable), SketchAnimatableDrawable {

    override val imageWidth: Int
        get() = imageInfo.width

    override val imageHeight: Int
        get() = imageInfo.height

    override val imageMimeType: String
        get() = imageInfo.mimeType

    override val imageExifOrientation: Int
        get() = imageInfo.exifOrientation

    override val bitmapWidth: Int
        get() = gifDrawable.bitmapWidth

    override val bitmapHeight: Int
        get() = gifDrawable.bitmapHeight

    override val bitmapByteCount: Int
        get() = gifDrawable.bitmapByteCount

    override val bitmapConfig: Bitmap.Config?
        get() = gifDrawable.bitmapConfig

    override fun start() = gifDrawable.start()

    override fun stop() = gifDrawable.stop()

    override fun isRunning(): Boolean = gifDrawable.isRunning

    override fun registerAnimationCallback(callback: Animatable2Compat.AnimationCallback) {
        gifDrawable.registerAnimationCallback(callback)
    }

    override fun unregisterAnimationCallback(callback: Animatable2Compat.AnimationCallback): Boolean {
        return gifDrawable.unregisterAnimationCallback(callback)
    }

    override fun clearAnimationCallbacks() = gifDrawable.clearAnimationCallbacks()
}