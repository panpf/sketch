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
package com.github.panpf.sketch.process

import android.graphics.Bitmap
import com.github.panpf.sketch.Key
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.Resize

/**
 * 用于在图片读取到内存后对图片进行修改
 */
interface ImageProcessor : Key {
    /**
     * 修改图片并返回修改后的图片，请不要回收原图片
     *
     * @param sketch          [Sketch]
     * @param bitmap          [Bitmap]. 原图片
     * @param resize          [Resize]. resize 决定了新图片的尺寸
     * @param lowQualityImage 是否使用低质量的图片
     * @return [Bitmap]. 修改后的图片，可以是一个新的 [Bitmap]，也可以在原 [Bitmap] 基础上修改
     */
    fun process(sketch: Sketch, bitmap: Bitmap, resize: Resize?, lowQualityImage: Boolean): Bitmap
}