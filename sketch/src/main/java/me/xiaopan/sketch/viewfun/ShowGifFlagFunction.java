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

package me.xiaopan.sketch.viewfun;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import me.xiaopan.sketch.SketchImageView;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 显示GIF图标识功能，使用者指定一个小图标，如果当前显示的图片是GIF图就会在ImageView的右下角显示这个小图标
 */
public class ShowGifFlagFunction extends SketchImageView.Function {
    private SketchImageView imageView;
    private Drawable gifFlagDrawable;

    private boolean gifImage;
    private float iconDrawLeft;
    private float iconDrawTop;
    private Drawable lastDrawable;
    private int cacheViewWidth;
    private int cacheViewHeight;

    public ShowGifFlagFunction(SketchImageView imageView, Drawable gifFlagDrawable) {
        this.imageView = imageView;

        this.gifFlagDrawable = gifFlagDrawable;
        this.gifFlagDrawable.setBounds(0, 0, this.gifFlagDrawable.getIntrinsicWidth(), this.gifFlagDrawable.getIntrinsicHeight());
    }

    @Override
    public void onDraw(Canvas canvas) {
        Drawable drawable = imageView.getDrawable();
        if (drawable != lastDrawable) {
            gifImage = SketchUtils.isGifImage(drawable);
            lastDrawable = drawable;
        }

        if (!gifImage) {
            return;
        }

        if (cacheViewWidth != imageView.getWidth() || cacheViewHeight != imageView.getHeight()) {
            cacheViewWidth = imageView.getWidth();
            cacheViewHeight = imageView.getHeight();
            iconDrawLeft = imageView.getWidth() - imageView.getPaddingRight() - gifFlagDrawable.getIntrinsicWidth();
            iconDrawTop = imageView.getHeight() - imageView.getPaddingBottom() - gifFlagDrawable.getIntrinsicHeight();
        }

        canvas.save();
        canvas.translate(iconDrawLeft, iconDrawTop);
        gifFlagDrawable.draw(canvas);
        canvas.restore();
    }
}
