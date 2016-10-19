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

        // 要想使用大图功能就必须开启缩放功能
        if (!imageView.isSupportZoom()) {
            throw new IllegalStateException("Use large image function must be open before the zoom function");
        }

        // 当缩放功能产生变化时回调大图功能
        ImageZoomer imageZoomer = imageView.getImageZoomer();
        imageZoomer.addOnMatrixChangeListener(this);

        // 大图功能的开关对缩放功能的缩放比例的计算有影响，因此需要更新一下缩放功能
        imageZoomer.update();

        if (!SketchUtils.isSupportBRDByApi()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". large image function the minimum support to GINGERBREAD_MR1");
            }
        }
    }

    @Override
    public void onAttachedToWindow() {
        if (!SketchUtils.isSupportBRDByApi()) {
            return;
        }

        resetImage();
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (!SketchUtils.isSupportBRDByApi()) {
            return;
        }

        if (largeImageViewer.isReady()) {
            largeImageViewer.draw(canvas);
        }
    }

    @Override
    public boolean onDetachedFromWindow() {
        if (!SketchUtils.isSupportBRDByApi()) {
            return false;
        }

        recycle("onDetachedFromWindow");
        return false;
    }

    @Override
    public boolean onDrawableChanged(String callPosition, Drawable oldDrawable, Drawable newDrawable) {
        if (!SketchUtils.isSupportBRDByApi()) {
            return false;
        }

        resetImage();
        return false;
    }

    @Override
    public void onMatrixChanged(ImageZoomer imageZoomer) {
        if (!SketchUtils.isSupportBRDByApi()) {
            return;
        }

        if (!largeImageViewer.isReady() && !largeImageViewer.isInitializing()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". largeImageViewer not available. onMatrixChanged. " + imageUri);
            }
            return;
        }

        if (imageZoomer.getRotateDegrees() % 90 != 0) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". rotate degrees must be in multiples of 90. " + imageUri);
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
        if (!SketchUtils.isSupportBRDByApi()) {
            return;
        }

        Drawable previewDrawable = SketchUtils.getLastDrawable(imageView.getDrawable());
        boolean drawableQualified = false;
        if (previewDrawable != null && previewDrawable instanceof SketchDrawable && !(previewDrawable instanceof BindDrawable)) {
            SketchDrawable sketchDrawable = (SketchDrawable) previewDrawable;
            final int previewWidth = previewDrawable.getIntrinsicWidth();
            final int previewHeight = previewDrawable.getIntrinsicHeight();
            final int imageWidth = sketchDrawable.getImageWidth();
            final int imageHeight = sketchDrawable.getImageHeight();

            drawableQualified = previewWidth < imageWidth || previewHeight < imageHeight;
            drawableQualified &= SketchUtils.isSupportBRDByApi();
            drawableQualified &= SketchUtils.isSupportBRDByImageFormat(ImageFormat.valueOfMimeType(sketchDrawable.getMimeType()));

            if (drawableQualified) {
                if (Sketch.isDebugMode()) {
                    Log.d(Sketch.TAG, NAME + ". Use large image function" +
                            ". previewDrawableSize: " + previewWidth + "x" + previewHeight +
                            ", imageSize: " + imageWidth + "x" + imageHeight +
                            ", mimeType: " + sketchDrawable.getMimeType() +
                            ". " + sketchDrawable.getImageId());
                }
            } else {
                if (Sketch.isDebugMode()) {
                    Log.w(Sketch.TAG, NAME + ". Don't need to use large image function" +
                            ". previewDrawableSize: " + previewWidth + "x" + previewHeight +
                            ", imageSize: " + imageWidth + "x" + imageHeight +
                            ", mimeType: " + sketchDrawable.getMimeType() +
                            ". " + sketchDrawable.getImageId());
                }
            }
        }

        if (drawableQualified) {
            imageUri = ((SketchDrawable) previewDrawable).getImageUri();
            largeImageViewer.setImage(imageUri);
        } else {
            imageUri = null;
            largeImageViewer.setImage(null);
        }
    }

    public void recycle(String why) {
        if (!SketchUtils.isSupportBRDByApi()) {
            return;
        }

        largeImageViewer.recycle(why);
    }

    @Override
    public void invalidate() {
        if (!SketchUtils.isSupportBRDByApi()) {
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
