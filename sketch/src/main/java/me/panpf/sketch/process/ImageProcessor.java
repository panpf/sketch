/*
 * Copyright (C) 2013 Peng fei Pan <sky@panpf.me>
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

package me.panpf.sketch.process;

import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import me.panpf.sketch.Key;
import me.panpf.sketch.Sketch;
import me.panpf.sketch.request.Resize;

/**
 * 用于在图片读取到内存后对图片进行修改
 */
public interface ImageProcessor extends Key {
    /**
     * 修改图片并返回修改后的图片，请不要回收原图片
     *
     * @param sketch          {@link Sketch}
     * @param bitmap          {@link Bitmap}. 原图片
     * @param resize          {@link Resize}. resize 决定了新图片的尺寸
     * @param lowQualityImage 是否使用低质量的图片
     * @return {@link Bitmap}. 修改后的图片，可以是一个新的 {@link Bitmap}，也可以在原 {@link Bitmap} 基础上修改
     */
    @NonNull
    Bitmap process(@NonNull Sketch sketch, @NonNull Bitmap bitmap, @Nullable Resize resize, boolean lowQualityImage);
}
