/*
 * Copyright (C) 2016 Peng fei Pan <sky@xiaopan.me>
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

package me.xiaopan.sketch.shaper;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import me.xiaopan.sketch.request.ShapeSize;

/**
 * 用于绘制时改变图片的形状
 */
public interface ImageShaper {
    /**
     * 获取形状 Path
     */
    @NonNull
    Path getPath(@NonNull Rect bounds);

    void onUpdateShaderMatrix(@NonNull Matrix matrix, @NonNull Rect bounds, int bitmapWidth, int bitmapHeight, @Nullable ShapeSize shapeSize, @NonNull Rect srcRect);

    /**
     * 绘制
     */
    void draw(@NonNull Canvas canvas, @NonNull Paint paint, @NonNull Rect bounds);
}
