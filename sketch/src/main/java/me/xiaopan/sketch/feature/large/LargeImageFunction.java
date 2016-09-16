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
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.SketchImageView;
import me.xiaopan.sketch.decode.ImageFormat;
import me.xiaopan.sketch.drawable.BindDrawable;
import me.xiaopan.sketch.drawable.SketchDrawable;
import me.xiaopan.sketch.feature.zoom.ImageZoomer;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 大图功能
 */
// TODO: 16/9/11 各种设置的方法挪到这里，确保唯一入口的地位
public class LargeImageFunction extends SketchImageView.Function implements ImageZoomer.OnMatrixChangedListener, LargeImageViewer.Callback {
    private static final String NAME = "LargeImageFunction";

    private SketchImageView imageView;
    private LargeImageViewer largeImageViewer;

    private Matrix tempDrawMatrix;
    private Rect tempVisibleRect;
    private Point tempPreviewDrawableSize;
    private Point tempImageViewSize;

    private String imageUri;

    public LargeImageFunction(SketchImageView imageView) {
        this.imageView = imageView;
        largeImageViewer = new LargeImageViewer(imageView.getContext(), this);
        if (!imageView.isSupportZoom()) {
            imageView.setSupportZoom(true);
        }
        imageView.getImageZoomer().addOnMatrixChangeListener(this);
    }

    @Override
    public void onAttachedToWindow() {
        if (!SketchUtils.isSupportLargeImageByAPIVersion()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". large image function the minimum support to GINGERBREAD_MR1. onAttachedToWindow");
            }
            return;
        }

        resetImage();
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (!SketchUtils.isSupportLargeImageByAPIVersion()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". large image function the minimum support to GINGERBREAD_MR1. onDraw");
            }
            return;
        }

        if (largeImageViewer.isReady()) {
            largeImageViewer.draw(canvas);
        }
    }

    @Override
    public boolean onDetachedFromWindow() {
        if (!SketchUtils.isSupportLargeImageByAPIVersion()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". large image function the minimum support to GINGERBREAD_MR1. onDetachedFromWindow");
            }
            return false;
        }

        recycle("onDetachedFromWindow");
        return false;
    }

    @Override
    public boolean onDrawableChanged(String callPosition, Drawable oldDrawable, Drawable newDrawable) {
        if (!SketchUtils.isSupportLargeImageByAPIVersion()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". large image function the minimum support to GINGERBREAD_MR1. onDrawableChanged");
            }
            return false;
        }

        resetImage();
        return false;
    }

    @Override
    public void onMatrixChanged(ImageZoomer imageZoomer) {
        if (!SketchUtils.isSupportLargeImageByAPIVersion()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". large image function the minimum support to GINGERBREAD_MR1. onMatrixChanged");
            }
            return;
        }

        if (!largeImageViewer.isReady() && !largeImageViewer.isInitializing()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". largeImageViewer not available. onMatrixChanged. " + imageUri);
            }
            return;
        }

        if (tempDrawMatrix == null) {
            tempDrawMatrix = new Matrix();
            tempVisibleRect = new Rect();
            tempPreviewDrawableSize = new Point();
            tempImageViewSize = new Point();
        }

        tempDrawMatrix.reset();
        tempVisibleRect.setEmpty();
        tempPreviewDrawableSize.set(0, 0);
        tempImageViewSize.set(0, 0);

        imageZoomer.getDrawMatrix(tempDrawMatrix);
        imageZoomer.getVisibleRect(tempVisibleRect);
        tempPreviewDrawableSize.set(imageZoomer.getDrawableWidth(), imageZoomer.getDrawableHeight());
        tempImageViewSize.set(imageZoomer.getImageViewWidth(), imageZoomer.getImageViewHeight());

        largeImageViewer.update(tempDrawMatrix, tempVisibleRect, tempPreviewDrawableSize, tempImageViewSize, imageZoomer.isZooming());
    }

    private void resetImage() {
        if (!SketchUtils.isSupportLargeImageByAPIVersion()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". large image function the minimum support to GINGERBREAD_MR1. resetImage");
            }
            return;
        }

        Drawable drawable = SketchUtils.getLastDrawable(imageView.getDrawable());
        boolean drawableQualified = false;
        if (drawable != null && drawable instanceof SketchDrawable && !(drawable instanceof BindDrawable)) {
            SketchDrawable sketchDrawable = (SketchDrawable) drawable;
            drawableQualified = drawable.getIntrinsicWidth() < sketchDrawable.getOriginWidth();
            drawableQualified |= drawable.getIntrinsicHeight() < sketchDrawable.getOriginHeight();

            String mimeType = sketchDrawable.getMimeType();
            drawableQualified &= SketchUtils.isSupportLargeImage(ImageFormat.valueOfMimeType(mimeType));

            if (drawableQualified) {
                if (Sketch.isDebugMode()) {
                    Log.d(Sketch.TAG, NAME + ". Use large image function" +
                            ". previewDrawableSize: " + drawable.getIntrinsicWidth() + "x" + drawable.getIntrinsicHeight() +
                            ", originImageSize: " + sketchDrawable.getOriginWidth() + "x" + sketchDrawable.getOriginHeight() +
                            ", mimeType: " + mimeType +
                            ". " + sketchDrawable.getImageId());
                }
            } else {
                if (Sketch.isDebugMode()) {
                    Log.w(Sketch.TAG, NAME + ". Don't need to use large image function" +
                            ". previewDrawableSize: " + drawable.getIntrinsicWidth() + "x" + drawable.getIntrinsicHeight() +
                            ", originImageSize: " + sketchDrawable.getOriginWidth() + "x" + sketchDrawable.getOriginHeight() +
                            ", mimeType: " + mimeType +
                            ". " + sketchDrawable.getImageId());
                }
            }
        }

        if (drawableQualified) {
            imageUri = ((SketchDrawable) drawable).getImageUri();
            largeImageViewer.setImage(imageUri);
        } else {
            imageUri = null;
            largeImageViewer.setImage(null);
        }
    }

    public void recycle(String why) {
        if (!SketchUtils.isSupportLargeImageByAPIVersion()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". large image function the minimum support to GINGERBREAD_MR1. recycle");
            }
            return;
        }

        largeImageViewer.recycle(why);
    }

    @Override
    public void invalidate() {
        if (!SketchUtils.isSupportLargeImageByAPIVersion()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". large image function the minimum support to GINGERBREAD_MR1. invalidate");
            }
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
