/*
 * Copyright (C) 2017 Peng fei Pan <sky@panpf.me>
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

package me.panpf.sketch.zoom;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import me.panpf.sketch.drawable.SketchDrawable;
import me.panpf.sketch.drawable.SketchLoadingDrawable;
import me.panpf.sketch.util.SketchUtils;

public class Sizes {
    public Size viewSize = new Size(); // ImageView 尺寸
    public Size imageSize = new Size();    // 原始图尺寸
    public Size drawableSize = new Size(); // 预览图尺寸

    void resetSizes(ImageView imageView) {
        final int imageViewWidth = imageView.getWidth() - imageView.getPaddingLeft() - imageView.getPaddingRight();
        final int imageViewHeight = imageView.getHeight() - imageView.getPaddingTop() - imageView.getPaddingBottom();
        if (imageViewWidth == 0 || imageViewHeight == 0) {
            return;
        }

        Drawable drawable = SketchUtils.getLastDrawable(imageView.getDrawable());
        if (drawable == null) {
            return;
        }

        final int drawableWidth = drawable.getIntrinsicWidth();
        final int drawableHeight = drawable.getIntrinsicHeight();
        if (drawableWidth == 0 || drawableHeight == 0) {
            return;
        }

        viewSize.set(imageViewWidth, imageViewHeight);
        drawableSize.set(drawableWidth, drawableHeight);
        if (drawable instanceof SketchDrawable && !(drawable instanceof SketchLoadingDrawable)) {
            SketchDrawable sketchDrawable = (SketchDrawable) drawable;
            imageSize.set(sketchDrawable.getOriginWidth(), sketchDrawable.getOriginHeight());
        } else {
            imageSize.set(drawableWidth, drawableHeight);
        }
    }

    void clean() {
        viewSize.set(0, 0);
        imageSize.set(0, 0);
        drawableSize.set(0, 0);
    }

    boolean isEmpty() {
        return viewSize.isEmpty() || imageSize.isEmpty() || drawableSize.isEmpty();
    }
}
