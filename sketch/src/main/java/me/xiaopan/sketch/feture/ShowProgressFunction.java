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

package me.xiaopan.sketch.feture;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import me.xiaopan.sketch.request.CancelCause;
import me.xiaopan.sketch.request.FailedCause;
import me.xiaopan.sketch.request.ImageFrom;

/**
 * 显示下载进度功能，会在ImageView上面显示一个黑色半透明蒙层显示下载进度，蒙层会随着进度渐渐变小
 */
public class ShowProgressFunction implements ImageViewFunction {
    private static final String NAME = "ShowProgressFunction";

    private static final int NONE = -1;
    private static final int DEFAULT_PROGRESS_COLOR = 0x22000000;

    private View view;
    private ImageShapeFunction imageShapeFunction;

    protected int downloadProgressColor = DEFAULT_PROGRESS_COLOR;
    protected Paint progressPaint;
    protected float progress = NONE;

    public ShowProgressFunction(View view, ImageShapeFunction imageShapeFunction) {
        this.view = view;
        this.imageShapeFunction = imageShapeFunction;
    }

    @Override
    public void onAttachedToWindow() {

    }

    @Override
    public void onDisplay() {
        onDisplayStarted();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {

    }

    @Override
    public void onDraw(Canvas canvas) {
        if (progress == NONE) {
            return;
        }

        boolean applyMaskClip = imageShapeFunction.getClipPath() != null;
        if (applyMaskClip) {
            canvas.save();
            try {
                canvas.clipPath(imageShapeFunction.getClipPath());
            } catch (UnsupportedOperationException e) {
                Log.e(NAME, "The current environment doesn't support clipPath has shut down automatically hardware acceleration");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                }
                e.printStackTrace();
            }
        }

        if (progressPaint == null) {
            progressPaint = new Paint();
            progressPaint.setColor(downloadProgressColor);
            progressPaint.setAntiAlias(true);
        }
        canvas.drawRect(view.getPaddingLeft(), view.getPaddingTop() + (progress * view.getHeight()), view.getWidth() - view.getPaddingLeft() - view.getPaddingRight(), view.getHeight() - view.getPaddingTop() - view.getPaddingBottom(), progressPaint);

        if (applyMaskClip) {
            canvas.restore();
        }
    }

    @Override
    public boolean onDetachedFromWindow() {
        return false;
    }

    @Override
    public boolean onDrawableChanged(String callPosition, Drawable oldDrawable, Drawable newDrawable) {
        return false;
    }

    @Override
    public boolean onDisplayStarted() {
        progress = 0;
        return true;
    }

    @Override
    public boolean onUpdateDownloadProgress(int totalLength, int completedLength) {
        progress = (float) completedLength / totalLength;
        return true;
    }

    @Override
    public boolean onDisplayCompleted(ImageFrom imageFrom, String mimeType) {
        progress = NONE;
        return true;
    }

    @Override
    public boolean onDisplayFailed(FailedCause failedCause) {
        progress = NONE;
        return true;
    }

    @Override
    public boolean onCanceled(CancelCause cancelCause) {
        progress = NONE;
        return false;
    }

    @SuppressWarnings("unused")
    public void setDownloadProgressColor(int downloadProgressColor) {
        this.downloadProgressColor = downloadProgressColor;
        if (progressPaint != null) {
            progressPaint.setColor(downloadProgressColor);
        }
    }
}
