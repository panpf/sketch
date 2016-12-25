/*
 * Copyright (C) 2013 Peng fei Pan <sky@xiaopan.me>
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

package me.xiaopan.sketch.state;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

import me.xiaopan.sketch.drawable.LoadingDrawable;
import me.xiaopan.sketch.drawable.ShapeBitmapDrawable;
import me.xiaopan.sketch.request.DisplayOptions;
import me.xiaopan.sketch.request.ImageViewInterface;
import me.xiaopan.sketch.request.ShapeSize;
import me.xiaopan.sketch.shaper.ImageShaper;

/**
 * 使用当前ImageView正在显示的图片作为状态图片
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

    @Override
    public Drawable getDrawable(Context context, ImageViewInterface imageViewInterface, DisplayOptions displayOptions) {
        Drawable drawable = imageViewInterface.getDrawable();

        if (drawable != null && drawable instanceof LayerDrawable) {
            LayerDrawable layerDrawable = (LayerDrawable) drawable;
            drawable = layerDrawable.getDrawable(layerDrawable.getNumberOfLayers() - 1);
        }

        if (drawable != null && drawable instanceof LoadingDrawable) {
            drawable = ((LoadingDrawable) drawable).getWrappedDrawable();
        }

        if (drawable != null) {
            ShapeSize shapeSize = displayOptions.getShapeSize();
            ImageShaper imageShaper = displayOptions.getImageShaper();
            if (shapeSize != null || imageShaper != null) {
                if (drawable instanceof ShapeBitmapDrawable) {
                    drawable = new ShapeBitmapDrawable(context, ((ShapeBitmapDrawable) drawable).getBitmapDrawable(), shapeSize, imageShaper);
                } else if (drawable instanceof BitmapDrawable) {
                    drawable = new ShapeBitmapDrawable(context, (BitmapDrawable) drawable, shapeSize, imageShaper);
                }
            }
        }

        if (drawable == null && whenEmptyImage != null) {
            drawable = whenEmptyImage.getDrawable(context, imageViewInterface, displayOptions);
        }

        return drawable;
    }
}
