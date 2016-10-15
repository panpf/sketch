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

package me.xiaopan.sketch.feature;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

import me.xiaopan.sketch.SketchImageView;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 显示GIF图标识功能，使用者指定一个小图标，如果当前显示的图片是GIF图就会在ImageView的右下角显示这个小图标
 */
public class ShowGifFlagFunction extends SketchImageView.Function {
    private View view;

    protected boolean isGifImage;
    protected float gifDrawableLeft = -1;
    protected float gifDrawableTop = -1;
    protected Drawable gifFlagDrawable;

    public ShowGifFlagFunction(View view, Drawable gifFlagDrawable) {
        this.view = view;

        this.gifFlagDrawable = gifFlagDrawable;
        this.gifFlagDrawable.setBounds(0, 0, this.gifFlagDrawable.getIntrinsicWidth(), this.gifFlagDrawable.getIntrinsicHeight());
    }

    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        initLeftAndTop();
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (!isGifImage) {
            return;
        }

        if (gifDrawableLeft == -1 || gifDrawableTop == -1) {
            initLeftAndTop();
        }

        canvas.save();
        canvas.translate(gifDrawableLeft, gifDrawableTop);
        gifFlagDrawable.draw(canvas);
        canvas.restore();
    }

    @Override
    public boolean onDetachedFromWindow() {
        // drawable都已经被清空了，GIF标识当然要重置了
        isGifImage = false;
        return false;
    }

    @Override
    public boolean onDrawableChanged(String callPosition, Drawable oldDrawable, Drawable newDrawable) {
        boolean oldIsGifDrawable = isGifImage;
        isGifImage = SketchUtils.isGifImage(newDrawable);
        return isGifImage != oldIsGifDrawable;
    }

    public Drawable getGifFlagDrawable() {
        return gifFlagDrawable;
    }

    private void initLeftAndTop() {
        gifDrawableLeft = view.getWidth() - view.getPaddingRight() - gifFlagDrawable.getIntrinsicWidth();
        gifDrawableTop = view.getHeight() - view.getPaddingBottom() - gifFlagDrawable.getIntrinsicHeight();
    }
}
