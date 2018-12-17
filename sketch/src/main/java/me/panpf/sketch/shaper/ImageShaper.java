/*
 * Copyright (C) 2016 Peng fei Pan <sky@panpf.me>
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

package me.panpf.sketch.shaper;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import me.panpf.sketch.request.ShapeSize;

/**
 * 用于绘制时改变图片的形状
 */
public interface ImageShaper {
    /**
     * 获取形状 Path
     */
    @NonNull
    Path getPath(@NonNull Rect bounds);

    /**
     * {@link Shader} 的 {@link Matrix} 更新时回调
     *
     * @param matrix       {@link Shader} 的 {@link Matrix}
     * @param bounds       {@link Rect}. 绘制区域的边界位置
     * @param bitmapWidth  bitmap 宽
     * @param bitmapHeight bitmap 高
     * @param shapeSize    {@link ShapeSize}
     * @param srcRect      {@link Rect}. 原图中的位置
     */
    void onUpdateShaderMatrix(@NonNull Matrix matrix, @NonNull Rect bounds, int bitmapWidth, int bitmapHeight, @Nullable ShapeSize shapeSize, @NonNull Rect srcRect);

    /**
     * 绘制
     *
     * @param canvas {@link Canvas}
     * @param paint  {@link Paint}
     * @param bounds {@link Rect}. 绘制区域的边界位置
     */
    void draw(@NonNull Canvas canvas, @NonNull Paint paint, @NonNull Rect bounds);
}
