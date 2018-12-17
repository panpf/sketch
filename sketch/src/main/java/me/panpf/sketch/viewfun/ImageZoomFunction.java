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

package me.panpf.sketch.viewfun;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import android.view.MotionEvent;
import android.widget.ImageView;

import me.panpf.sketch.zoom.ImageZoomer;

/**
 * {@link ImageView} 缩放功能
 */
public class ImageZoomFunction extends ViewFunction {

    private ImageZoomer zoomer;

    public ImageZoomFunction(FunctionPropertyView view) {
        this.zoomer = new ImageZoomer(view);
    }

    @Override
    public void onAttachedToWindow() {
        zoomer.reset("onAttachedToWindow");
    }

    @Override
    public void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        zoomer.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        return zoomer.onTouchEvent(event);
    }

    @Override
    public boolean onDrawableChanged(@NonNull String callPosition, Drawable oldDrawable, Drawable newDrawable) {
        zoomer.reset("onDrawableChanged");
        return false;
    }

    @Override
    public void onSizeChanged(int left, int top, int right, int bottom) {
        zoomer.reset("onSizeChanged");
    }

    @Override
    public boolean onDetachedFromWindow() {
        recycle("onDetachedFromWindow");
        return false;
    }

    void recycle(@NonNull String why) {
        zoomer.recycle(why);
    }

    @NonNull
    public ImageView.ScaleType getScaleType() {
        return zoomer.getScaleType();
    }

    public void setScaleType(@NonNull ImageView.ScaleType scaleType) {
        zoomer.setScaleType(scaleType);
    }

    @NonNull
    public ImageZoomer getZoomer() {
        return zoomer;
    }
}
