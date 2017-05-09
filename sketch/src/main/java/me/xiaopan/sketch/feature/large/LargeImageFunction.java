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

package me.xiaopan.sketch.feature.large;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.SLogType;
import me.xiaopan.sketch.SketchImageView;
import me.xiaopan.sketch.decode.ImageType;
import me.xiaopan.sketch.drawable.LoadingDrawable;
import me.xiaopan.sketch.drawable.SketchDrawable;
import me.xiaopan.sketch.feature.zoom.ImageZoomer;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 大图功能
 */
public class LargeImageFunction extends SketchImageView.Function implements ImageZoomer.OnMatrixChangeListener, LargeImageViewer.Callback {
    private static final String NAME = "LargeImageFunction";

    private SketchImageView imageView;
    private LargeImageViewer largeImageViewer;

    private Matrix tempDrawMatrix;
    private Rect tempVisibleRect;

    private String imageUri;

    public LargeImageFunction(SketchImageView imageView) {
        this.imageView = imageView;
        this.largeImageViewer = new LargeImageViewer(imageView.getContext(), this);

        if (!SketchUtils.sdkSupportBitmapRegionDecoder()) {
            if (SLogType.LARGE.isEnabled()) {
                SLog.w(SLogType.LARGE, NAME, "large image function the minimum support to GINGERBREAD_MR1");
            }
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
    public void onDraw(Canvas canvas) {
        if (!SketchUtils.sdkSupportBitmapRegionDecoder()) {
            return;
        }

        if (largeImageViewer.isReady()) {
            largeImageViewer.draw(canvas);
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
    public boolean onDrawableChanged(String callPosition, Drawable oldDrawable, Drawable newDrawable) {
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

        if (!largeImageViewer.isReady() && !largeImageViewer.isInitializing()) {
            if (SLogType.LARGE.isEnabled()) {
                SLog.w(SLogType.LARGE, NAME, "largeImageViewer not available. onMatrixChanged. %s", imageUri);
            }
            return;
        }

        if (imageZoomer.getRotateDegrees() % 90 != 0) {
            if (SLogType.LARGE.isEnabled()) {
                SLog.w(SLogType.LARGE, NAME, "rotate degrees must be in multiples of 90. %s", imageUri);
            }
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

        largeImageViewer.update(tempDrawMatrix, tempVisibleRect, imageZoomer.getDrawableSize(),
                imageZoomer.getImageViewSize(), imageZoomer.isZooming());
    }

    private void resetImage() {
        if (!SketchUtils.sdkSupportBitmapRegionDecoder()) {
            return;
        }

        Drawable previewDrawable = SketchUtils.getLastDrawable(imageView.getDrawable());
        SketchDrawable sketchDrawable = null;
        boolean drawableQualified = false;
        if (previewDrawable != null && previewDrawable instanceof SketchDrawable && !(previewDrawable instanceof LoadingDrawable)) {
            sketchDrawable = (SketchDrawable) previewDrawable;
            final int previewWidth = previewDrawable.getIntrinsicWidth();
            final int previewHeight = previewDrawable.getIntrinsicHeight();
            final int imageWidth = sketchDrawable.getOriginWidth();
            final int imageHeight = sketchDrawable.getOriginHeight();

            drawableQualified = previewWidth < imageWidth || previewHeight < imageHeight;
            drawableQualified &= SketchUtils.sdkSupportBitmapRegionDecoder();
            drawableQualified &= SketchUtils.formatSupportBitmapRegionDecoder(ImageType.valueOfMimeType(sketchDrawable.getMimeType()));

            if (drawableQualified) {
                if (SLogType.LARGE.isEnabled()) {
                    SLog.d(SLogType.LARGE, NAME, "Use large image function. previewDrawableSize: %dx%d, imageSize: %dx%d, mimeType: %s. %s",
                            previewWidth, previewHeight, imageWidth, imageHeight, sketchDrawable.getMimeType(), sketchDrawable.getKey());
                }
            } else {
                if (SLogType.LARGE.isEnabled()) {
                    SLog.w(SLogType.LARGE, NAME, "Don't need to use large image function. previewDrawableSize: %dx%d, imageSize: %dx%d, mimeType: %s. %s",
                            previewWidth, previewHeight, imageWidth, imageHeight, sketchDrawable.getMimeType(), sketchDrawable.getKey());
                }
            }
        }

        if (drawableQualified) {
            imageUri = sketchDrawable.getUri();
            largeImageViewer.setImage(imageUri, sketchDrawable.getOrientation() != 0);
        } else {
            imageUri = null;
            largeImageViewer.setImage(null, false);
        }
    }

    public void recycle(String why) {
        if (!SketchUtils.sdkSupportBitmapRegionDecoder()) {
            return;
        }

        largeImageViewer.recycle(why);
    }

    @Override
    public void invalidate() {
        if (!SketchUtils.sdkSupportBitmapRegionDecoder()) {
            return;
        }

        imageView.invalidate();
    }

    @Override
    public void updateMatrix() {
        ImageZoomer imageZoomer = imageView.isSupportZoom() ? imageView.getImageZoomer() : null;
        if (imageZoomer != null) {
            onMatrixChanged(imageZoomer);
        }
    }

    public LargeImageViewer getLargeImageViewer() {
        return largeImageViewer;
    }
}
