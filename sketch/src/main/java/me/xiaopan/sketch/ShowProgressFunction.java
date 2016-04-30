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

package me.xiaopan.sketch;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class ShowProgressFunction implements ImageViewFunction{
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
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {

    }

    @Override
    public void draw(Canvas canvas) {
        if (progress == NONE) {
            return;
        }

        boolean applyMaskClip = imageShapeFunction.getClipPath() != null;
        if (applyMaskClip) {
            canvas.save();
            try {
                canvas.clipPath(imageShapeFunction.getClipPath());
            } catch (UnsupportedOperationException e) {
                Log.e(SketchImageView.NAME, "The current environment doesn't support clipPath has shut down automatically hardware acceleration");
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
    public boolean onDisplayStarted() {
        setProgress(0);
        return true;
    }

    @Override
    public boolean onDisplayCompleted(ImageFrom imageFrom, String mimeType) {
        setProgress(NONE);
        return true;
    }

    @Override
    public boolean onDisplayFailed(FailedCause failedCause) {
        setProgress(NONE);
        return true;
    }

    @SuppressWarnings("unused")
    public void setDownloadProgressColor(int downloadProgressColor) {
        this.downloadProgressColor = downloadProgressColor;
        if (progressPaint != null) {
            progressPaint.setColor(downloadProgressColor);
        }
    }

    public void setProgress(float progress){
        this.progress = progress;
    }
}
