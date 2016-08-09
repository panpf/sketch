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
import android.view.MotionEvent;
import android.widget.ImageView;

import me.xiaopan.sketch.feature.zoom.ImageZoomer;
import me.xiaopan.sketch.request.CancelCause;
import me.xiaopan.sketch.request.FailedCause;
import me.xiaopan.sketch.request.ImageFrom;
import me.xiaopan.sketch.request.UriScheme;

/**
 * ImageView缩放功能
 */
public class ImageZoomFunction implements ImageViewFunction {
    private ImageView imageView;

    private ImageZoomer imageZoomer;
    private boolean fromSuperLargeImagViewer;

    public ImageZoomFunction(ImageView imageView) {
        this.imageView = imageView;
        imageZoomer = new ImageZoomer(imageView, true);
    }

    @Override
    public void onAttachedToWindow() {
        imageZoomer.update();
    }

    @Override
    public boolean onDisplay(UriScheme uriScheme) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return imageZoomer.onTouch(imageView, event);
    }

    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {

    }

    @Override
    public void onDraw(Canvas canvas) {

    }

    @Override
    public boolean onDetachedFromWindow() {
        return false;
    }

    @Override
    public boolean onDrawableChanged(String callPosition, Drawable oldDrawable, Drawable newDrawable) {
        imageZoomer.update();
        return false;
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

    public void destroy(){

    }

    public ImageZoomer getImageZoomer() {
        return imageZoomer;
    }

    public boolean isFromSuperLargeImagViewer() {
        return fromSuperLargeImagViewer;
    }

    public void setFromSuperLargeImagViewer(boolean fromSuperLargeImagViewer) {
        this.fromSuperLargeImagViewer = fromSuperLargeImagViewer;
    }
}
