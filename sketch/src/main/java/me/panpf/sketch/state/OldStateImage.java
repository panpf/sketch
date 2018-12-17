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

package me.panpf.sketch.state;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.ImageView;

import me.panpf.sketch.SketchView;
import me.panpf.sketch.drawable.SketchLoadingDrawable;
import me.panpf.sketch.drawable.SketchShapeBitmapDrawable;
import me.panpf.sketch.request.DisplayOptions;
import me.panpf.sketch.request.ShapeSize;
import me.panpf.sketch.shaper.ImageShaper;
import me.panpf.sketch.util.SketchUtils;

/**
 * 使用当前 {@link ImageView} 正在显示的图片作为状态图片
 */
@SuppressWarnings("unused")
public class OldStateImage implements StateImage {
    private StateImage whenEmptyImage;

    @SuppressWarnings("unused")
    public OldStateImage(StateImage whenEmptyImage) {
        this.whenEmptyImage = whenEmptyImage;
    }

    public OldStateImage() {
    }

    @Nullable
    @Override
    public Drawable getDrawable(@NonNull Context context, @NonNull SketchView sketchView, @NonNull DisplayOptions displayOptions) {
        Drawable drawable = SketchUtils.getLastDrawable(sketchView.getDrawable());

        if (drawable != null && drawable instanceof SketchLoadingDrawable) {
            drawable = ((SketchLoadingDrawable) drawable).getWrappedDrawable();
        }

        if (drawable != null) {
            ShapeSize shapeSize = displayOptions.getShapeSize();
            ImageShaper imageShaper = displayOptions.getShaper();
            if (shapeSize != null || imageShaper != null) {
                if (drawable instanceof SketchShapeBitmapDrawable) {
                    drawable = new SketchShapeBitmapDrawable(context, ((SketchShapeBitmapDrawable) drawable).getBitmapDrawable(), shapeSize, imageShaper);
                } else if (drawable instanceof BitmapDrawable) {
                    drawable = new SketchShapeBitmapDrawable(context, (BitmapDrawable) drawable, shapeSize, imageShaper);
                }
            }
        }

        if (drawable == null && whenEmptyImage != null) {
            drawable = whenEmptyImage.getDrawable(context, sketchView, displayOptions);
        }

        return drawable;
    }
}
