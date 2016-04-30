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
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;

public class ShowGifFlagFunction implements ImageViewFunction{
    private View view;

    protected boolean isGifDrawable;
    protected float gifDrawableLeft = -1;
    protected float gifDrawableTop = -1;
    protected Drawable gifFlagDrawable;

    public ShowGifFlagFunction(View view, Drawable gifFlagDrawable) {
        this.view = view;

        this.gifFlagDrawable = gifFlagDrawable;
        this.gifFlagDrawable.setBounds(0, 0, this.gifFlagDrawable.getIntrinsicWidth(), this.gifFlagDrawable.getIntrinsicHeight());
    }

    @Override
    public void onDisplay() {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        initLeftAndTop();
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (!isGifDrawable) {
            return;
        }

        if(gifDrawableLeft == -1 || gifDrawableTop == -1){
            initLeftAndTop();
        }

        canvas.save();
        canvas.translate(gifDrawableLeft, gifDrawableTop);
        gifFlagDrawable.draw(canvas);
        canvas.restore();
    }

    @Override
    public boolean onDisplayStarted() {
        return false;
    }

    @Override
    public boolean onUpdateDownloadProgress(int totalLength, int completedLength) {
        return false;
    }

    @Override
    public boolean onDisplayCompleted(ImageFrom imageFrom, String mimeType) {
        return false;
    }

    @Override
    public boolean onDisplayFailed(FailedCause failedCause) {
        return false;
    }

    @Override
    public boolean onCanceled(CancelCause cancelCause) {
        return false;
    }

    public void setIsGifDrawable(boolean gifDrawable) {
        isGifDrawable = gifDrawable;
    }

    public Drawable getGifFlagDrawable() {
        return gifFlagDrawable;
    }

    private void initLeftAndTop(){
        gifDrawableLeft = view.getWidth() - view.getPaddingRight() - gifFlagDrawable.getIntrinsicWidth();
        gifDrawableTop = view.getHeight() - view.getPaddingBottom() - gifFlagDrawable.getIntrinsicHeight();
    }
}
