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

import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.decode.ImageType;
import me.xiaopan.sketch.drawable.SketchDrawable;
import me.xiaopan.sketch.drawable.SketchLoadingDrawable;
import me.xiaopan.sketch.util.SketchUtils;
import me.xiaopan.sketch.viewfun.huge.HugeImageViewer;
import me.xiaopan.sketch.viewfun.zoom.ImageZoomer;

/**
 * 超级大图功能
 */
public class HugeImageFunction extends ViewFunction implements ImageZoomer.OnMatrixChangeListener, HugeImageViewer.Callback {
    private static final String NAME = "HugeImageFunction";

    private FunctionPropertyView view;
    private HugeImageViewer hugeImageViewer;

    private Matrix tempDrawMatrix;
    private Rect tempVisibleRect;

    private String imageUri;

    public HugeImageFunction(FunctionPropertyView view) {
        this.view = view;
        this.hugeImageViewer = new HugeImageViewer(view.getContext(), this);

        if (!SketchUtils.sdkSupportBitmapRegionDecoder()) {
            SLog.e(NAME, "huge image function the minimum support to GINGERBREAD_MR1");
        }
    }

    /**
     * 绑定图片缩放器
     */
    public void bindImageZoomer(ImageZoomer imageZoomer) {
        if (imageZoomer == null) {
            throw new IllegalStateException("imageZoomer is null");
        }

        // 当缩放功能产生变化时回调大图功能
        imageZoomer.addOnMatrixChangeListener(this);

        // 大图功能的开关对缩放功能的缩放比例的计算有影响，因此需要更新一下缩放功能
        imageZoomer.update();
    }

    @Override
    public void onAttachedToWindow() {
        if (!SketchUtils.sdkSupportBitmapRegionDecoder()) {
            return;
        }

        resetImage();
    }

    @Override
    public void onDraw(@NonNull Canvas canvas) {
        if (!SketchUtils.sdkSupportBitmapRegionDecoder()) {
            return;
        }

        if (hugeImageViewer.isReady()) {
            hugeImageViewer.draw(canvas);
        }
    }

    @Override
    public boolean onDetachedFromWindow() {
        if (!SketchUtils.sdkSupportBitmapRegionDecoder()) {
            return false;
        }

        recycle("onDetachedFromWindow");
        return false;
    }

    @Override
    public boolean onDrawableChanged(@NonNull String callPosition, Drawable oldDrawable, Drawable newDrawable) {
        if (!SketchUtils.sdkSupportBitmapRegionDecoder()) {
            return false;
        }

        resetImage();
        return false;
    }

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

    private void resetImage() {
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

    public void recycle(String why) {
        if (!SketchUtils.sdkSupportBitmapRegionDecoder()) {
            return;
        }

        hugeImageViewer.recycle(why);
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
        ImageZoomer imageZoomer = view.isZoomEnabled() ? view.getImageZoomer() : null;
        if (imageZoomer != null) {
            onMatrixChanged(imageZoomer);
        }
    }

    public HugeImageViewer getHugeImageViewer() {
        return hugeImageViewer;
    }
}
