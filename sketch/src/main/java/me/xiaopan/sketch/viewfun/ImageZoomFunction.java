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
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.widget.ImageView;

import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.decode.ImageType;
import me.xiaopan.sketch.drawable.SketchDrawable;
import me.xiaopan.sketch.drawable.SketchLoadingDrawable;
import me.xiaopan.sketch.util.SketchUtils;
import me.xiaopan.sketch.zoom.ImageZoomer;
import me.xiaopan.sketch.zoom.huge.HugeImageViewer;

/**
 * ImageView 缩放功能
 */
public class ImageZoomFunction extends ViewFunction {
    private static final String NAME = "ImageZoomFunction";

    private FunctionPropertyView view;

    private String imageUri;
    private ImageZoomer imageZoomer;
    private HugeImageViewer hugeImageViewer;

    private Matrix tempDrawMatrix;
    private Rect tempVisibleRect;

    public ImageZoomFunction(FunctionPropertyView view) {
        this.view = view;

        ZoomMatrixChangeListener zoomMatrixChangeListener = new ZoomMatrixChangeListener();
        this.imageZoomer = new ImageZoomer(view, true);
        imageZoomer.addOnMatrixChangeListener(zoomMatrixChangeListener);

        this.hugeImageViewer = new HugeImageViewer(view.getContext(), new HugeCallback(zoomMatrixChangeListener));

        if (!SketchUtils.sdkSupportBitmapRegionDecoder()) {
            SLog.e(NAME, "huge image function the minimum support to GINGERBREAD_MR1");
        }
    }

    @Override
    public void onAttachedToWindow() {
        imageZoomer.init(view, true);
        resetHugeImage();
    }

    @Override
    public void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        imageZoomer.draw(canvas);

        if (SketchUtils.sdkSupportBitmapRegionDecoder()) {
            if (hugeImageViewer.isReady()) {
                hugeImageViewer.draw(canvas);
            }
        }
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        return imageZoomer.onTouch(view, event);
    }

    @Override
    public boolean onDetachedFromWindow() {
        recycle("onDetachedFromWindow");
        return false;
    }

    @Override
    public boolean onDrawableChanged(@NonNull String callPosition, Drawable oldDrawable, Drawable newDrawable) {
        imageZoomer.update();
        resetHugeImage();
        return false;
    }

    @Override
    public void onSizeChanged(int left, int top, int right, int bottom) {
        imageZoomer.update();
    }

    private void resetHugeImage() {
        if (!SketchUtils.sdkSupportBitmapRegionDecoder()) {
            return;
        }

        Drawable previewDrawable = SketchUtils.getLastDrawable(view.getDrawable());
        SketchDrawable sketchDrawable = null;
        boolean drawableQualified = false;
        if (previewDrawable != null && previewDrawable instanceof SketchDrawable && !(previewDrawable instanceof SketchLoadingDrawable)) {
            sketchDrawable = (SketchDrawable) previewDrawable;
            final int previewWidth = previewDrawable.getIntrinsicWidth();
            final int previewHeight = previewDrawable.getIntrinsicHeight();
            final int imageWidth = sketchDrawable.getOriginWidth();
            final int imageHeight = sketchDrawable.getOriginHeight();

            drawableQualified = previewWidth < imageWidth || previewHeight < imageHeight;
            drawableQualified &= SketchUtils.sdkSupportBitmapRegionDecoder();
            drawableQualified &= SketchUtils.formatSupportBitmapRegionDecoder(ImageType.valueOfMimeType(sketchDrawable.getMimeType()));

            if (drawableQualified) {
                if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_HUGE_IMAGE)) {
                    SLog.d(NAME, "Use huge image function. previewDrawableSize: %dx%d, imageSize: %dx%d, mimeType: %s. %s",
                            previewWidth, previewHeight, imageWidth, imageHeight, sketchDrawable.getMimeType(), sketchDrawable.getKey());
                }
            } else {
                if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_HUGE_IMAGE)) {
                    SLog.d(NAME, "Don't need to use huge image function. previewDrawableSize: %dx%d, imageSize: %dx%d, mimeType: %s. %s",
                            previewWidth, previewHeight, imageWidth, imageHeight, sketchDrawable.getMimeType(), sketchDrawable.getKey());
                }
            }
        }

        if (drawableQualified) {
            imageUri = sketchDrawable.getUri();
            hugeImageViewer.setImage(imageUri, view.getOptions().isCorrectImageOrientationDisabled());
        } else {
            imageUri = null;
            hugeImageViewer.setImage(null, false);
        }
    }

    public ImageView.ScaleType getScaleType() {
        return imageZoomer.getScaleType();
    }

    public void setScaleType(ImageView.ScaleType scaleType) {
        imageZoomer.setScaleType(scaleType);
    }

    public void recycle(String why) {
        imageZoomer.cleanup();

        if (SketchUtils.sdkSupportBitmapRegionDecoder()) {
            hugeImageViewer.recycle(why);
        }
    }

    public ImageZoomer getImageZoomer() {
        return imageZoomer;
    }

    @SuppressWarnings("unused")
    public HugeImageViewer getHugeImageViewer() {
        return hugeImageViewer;
    }

    private class HugeCallback implements HugeImageViewer.Callback {
        private ZoomMatrixChangeListener zoomMatrixChangeListener;

        public HugeCallback(ZoomMatrixChangeListener zoomMatrixChangeListener) {
            this.zoomMatrixChangeListener = zoomMatrixChangeListener;
        }

        @Override
        public void invalidate() {
            if (!SketchUtils.sdkSupportBitmapRegionDecoder()) {
                return;
            }

            view.invalidate();
        }

        @Override
        public void updateMatrix() {
            zoomMatrixChangeListener.onMatrixChanged(imageZoomer);
        }
    }

    private class ZoomMatrixChangeListener implements ImageZoomer.OnMatrixChangeListener {

        @Override
        public void onMatrixChanged(ImageZoomer imageZoomer) {
            if (!SketchUtils.sdkSupportBitmapRegionDecoder()) {
                return;
            }

            if (!hugeImageViewer.isReady() && !hugeImageViewer.isInitializing()) {
                if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_HUGE_IMAGE)) {
                    SLog.d(NAME, "hugeImageViewer not available. onMatrixChanged. %s", imageUri);
                }
                return;
            }

            if (imageZoomer.getRotateDegrees() % 90 != 0) {
                SLog.w(NAME, "rotate degrees must be in multiples of 90. %s", imageUri);
                return;
            }

            if (tempDrawMatrix == null) {
                tempDrawMatrix = new Matrix();
                tempVisibleRect = new Rect();
            }

            tempDrawMatrix.reset();
            tempVisibleRect.setEmpty();

            imageZoomer.getDrawMatrix(tempDrawMatrix);
            imageZoomer.getVisibleRect(tempVisibleRect);

            hugeImageViewer.update(tempDrawMatrix, tempVisibleRect, imageZoomer.getDrawableSize(),
                    imageZoomer.getImageViewSize(), imageZoomer.isZooming());
        }
    }
}
