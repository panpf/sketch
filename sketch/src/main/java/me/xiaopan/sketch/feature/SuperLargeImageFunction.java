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

import me.xiaopan.sketch.SketchImageView;
import me.xiaopan.sketch.decode.ImageFormat;
import me.xiaopan.sketch.drawable.BindDrawable;
import me.xiaopan.sketch.feature.large.SuperLargeImageViewer;
import me.xiaopan.sketch.feature.zoom.ImageZoomer;
import me.xiaopan.sketch.request.DisplayParams;

/**
 * 显示超级大图功能
 */
// TODO: 16/8/14 BitmapRegionDecoder从api10 GINGERBREAD_MR1才开始支持
// TODO: 16/8/9 BitmapRegionDecoder仅支持jpg，png，bmp等图片
// TODO: 16/8/17 原始尺寸小于屏幕宽高2倍图片就不使用超大图功能
public class SuperLargeImageFunction extends SketchImageView.Function implements ImageZoomer.OnMatrixChangedListener, SuperLargeImageViewer.Callback {
    private SketchImageView imageView;
    private SuperLargeImageViewer superLargeImageViewer;

    public SuperLargeImageFunction(SketchImageView imageView) {
        this.imageView = imageView;
        superLargeImageViewer = new SuperLargeImageViewer(imageView.getContext(), this);
        if (!imageView.isEnableZoomFunction()) {
            imageView.setEnableZoomFunction(true);
        }
        imageView.getImageZoomFunction().getImageZoomer().addOnMatrixChangeListener(this);
    }

    @Override
    public void onAttachedToWindow() {
        resetImage();
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (superLargeImageViewer.isAvailable()) {
            superLargeImageViewer.draw(canvas);
        }
    }

    @Override
    public boolean onDetachedFromWindow() {
        recycle();
        return false;
    }

    @Override
    public boolean onDrawableChanged(String callPosition, Drawable oldDrawable, Drawable newDrawable) {
        resetImage();
        return false;
    }

    @Override
    public void onMatrixChanged(ImageZoomer imageZoomer) {
        if (superLargeImageViewer.isAvailable() || superLargeImageViewer.isInitializing()) {
            Drawable drawable = imageView.getDrawable();
            if (drawable != null) {
                SuperLargeImageViewer.UpdateParams updateParams = superLargeImageViewer.getUpdateParams();
                imageZoomer.getDrawMatrix(updateParams.getDrawMatrix());
                imageZoomer.getVisibleRect(updateParams.getVisibleRect());
                updateParams.setPreviewDrawableSize(imageZoomer.getDrawableWidth(), imageZoomer.getDrawableHeight());
                updateParams.setImageViewSize(imageZoomer.getImageViewWidth(), imageZoomer.getImageViewHeight());
                superLargeImageViewer.update(updateParams);
            } else {
                superLargeImageViewer.update(null);
            }
        }
    }

    private void resetImage() {
        Drawable drawable = imageView.getDrawable();
        DisplayParams displayParams = imageView.getDisplayParams();
        if (drawable != null && !(drawable instanceof BindDrawable) && displayParams != null) {
            superLargeImageViewer.setImage(displayParams.attrs.getUri());
        } else {
            superLargeImageViewer.setImage(null);
        }
    }

    private void recycle() {
        superLargeImageViewer.recycle();
    }

    public void destroy() {
        recycle();
    }

    @Override
    public void invalidate() {
        imageView.invalidate();
    }

    @Override
    public void initCompleted(int imageWidth, int imageHeight, ImageFormat imageFormat) {

    }

    @Override
    public void initFailed() {

    }
}
