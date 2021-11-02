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

package com.github.panpf.sketch.state;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.panpf.sketch.SketchView;
import com.github.panpf.sketch.drawable.SketchShapeBitmapDrawable;
import com.github.panpf.sketch.request.DisplayOptions;
import com.github.panpf.sketch.request.ShapeSize;
import com.github.panpf.sketch.shaper.ImageShaper;

/**
 * 给什么图片显示什么图片，支持 {@link ShapeSize} 和 {@link ImageShaper}
 */
public class DrawableStateImage implements StateImage {
    @Nullable
    private Drawable originDrawable;
    private int resId = -1;

    public DrawableStateImage(@NonNull Drawable drawable) {
        this.originDrawable = drawable;
    }

    public DrawableStateImage(int resId) {
        this.resId = resId;
    }

    @Nullable
    @Override
    public Drawable getDrawable(@NonNull Context context, @NonNull SketchView sketchView, @NonNull DisplayOptions displayOptions) {
        Drawable drawable = originDrawable;
        if (drawable == null && resId != -1) {
            drawable = context.getResources().getDrawable(resId);
        }

        ShapeSize shapeSize = displayOptions.getShapeSize();
        ImageShaper imageShaper = displayOptions.getShaper();
        if ((shapeSize != null || imageShaper != null) && drawable instanceof BitmapDrawable) {
            drawable = new SketchShapeBitmapDrawable(context, (BitmapDrawable) drawable, shapeSize, imageShaper);
        }

        return drawable;
    }

    @Nullable
    public Drawable getOriginDrawable() {
        return originDrawable;
    }

    public int getResId() {
        return resId;
    }
}
