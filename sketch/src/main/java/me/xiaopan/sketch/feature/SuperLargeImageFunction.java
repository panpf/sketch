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
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

import me.xiaopan.sketch.SketchImageView;
import me.xiaopan.sketch.feature.large.SuperLargeImageViewer;
import me.xiaopan.sketch.feature.zoom.ImageZoomer;
import me.xiaopan.sketch.request.CancelCause;
import me.xiaopan.sketch.request.DisplayParams;
import me.xiaopan.sketch.request.FailedCause;
import me.xiaopan.sketch.request.ImageFrom;
import me.xiaopan.sketch.request.UriScheme;

/**
 * 显示超级大图功能
 */
// todo BitmapRegionDecoder从api10 GINGERBREAD_MR1才开始支持
// TODO: 16/8/9 BitmapRegionDecoder仅支持jpg，png，bmp等图片
public class SuperLargeImageFunction implements ImageViewFunction, ImageZoomer.OnMatrixChangedListener {
    private SketchImageView imageView;
    private SuperLargeImageViewer superLargeImageViewer;

    public SuperLargeImageFunction(SketchImageView imageView) {
        this.imageView = imageView;
        superLargeImageViewer = new SuperLargeImageViewer(imageView);
        if (!imageView.isEnableZoomFunction()) {
            imageView.setEnableZoomFunction(true);
        }
        imageView.getImageZoomFunction().getImageZoomer().addOnMatrixChangeListener(this);
    }

    @Override
    public void onAttachedToWindow() {
        superLargeImageViewer.onAttachedToWindow();
    }

    @Override
    public boolean onDisplay(UriScheme uriScheme) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        superLargeImageViewer.onLayout(changed, left, top, right, bottom);
    }

    @Override
    public void onDraw(Canvas canvas) {
        superLargeImageViewer.onDraw(canvas);
    }

    @Override
    public boolean onDetachedFromWindow() {
        superLargeImageViewer.onDetachedFromWindow();
        return false;
    }

    @Override
    public boolean onDrawableChanged(String callPosition, Drawable oldDrawable, Drawable newDrawable) {
        if (newDrawable != null) {
            DisplayParams displayParams = imageView.getDisplayParams();
            superLargeImageViewer.setImage(displayParams != null ? displayParams.attrs.getUri() : null);
        } else {
            superLargeImageViewer.setImage(null);
        }
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

    @Override
    public void onMatrixChanged(RectF displayRect) {
        Drawable drawable = imageView.getDrawable();
        if (drawable != null) {
            ImageZoomer imageZoomer = imageView.getImageZoomFunction().getImageZoomer();
            Matrix drawMatrix = imageZoomer.getDrawMatrix();
            RectF visibleRect = imageZoomer.getVisibleRect();
            int drawableWidth = imageZoomer.getDrawableWidth();
            int drawableHeight = imageZoomer.getDrawableHeight();
            superLargeImageViewer.update(drawMatrix, visibleRect, drawableWidth, drawableHeight);
        }
    }

    public void destroy(){

    }
}
