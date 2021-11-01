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
package com.github.panpf.sketch.display

import android.graphics.drawable.Drawable
import com.github.panpf.sketch.SketchView

/**
 * 图片显示器，用来在加载完成后显示图片
 */
interface ImageDisplayer {
    /**
     * 显示
     */
    fun display(sketchView: SketchView, newDrawable: Drawable)

    /**
     * 获取持续时间，单位毫秒
     */
    val duration: Int

    /**
     * 只要涉及到显示图片就得使用 [ImageDisplayer]（显示从内存里取出的缓存图片时也不例外）
     */
    val isAlwaysUse: Boolean

    companion object {
        const val DEFAULT_ANIMATION_DURATION = 400
    }
}