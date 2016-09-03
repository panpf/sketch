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
import android.util.Log;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.SketchImageView;
import me.xiaopan.sketch.decode.ImageFormat;
import me.xiaopan.sketch.drawable.BindDrawable;
import me.xiaopan.sketch.drawable.SketchDrawable;
import me.xiaopan.sketch.feature.large.LargeImageViewer;
import me.xiaopan.sketch.feature.large.UpdateParams;
import me.xiaopan.sketch.feature.zoom.ImageZoomer;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 大图功能
 */
public class LargeImageFunction extends SketchImageView.Function implements ImageZoomer.OnMatrixChangedListener, LargeImageViewer.Callback {
    private static final String NAME = "LargeImageFunction";

    private SketchImageView imageView;
    private LargeImageViewer largeImageViewer;

    public LargeImageFunction(SketchImageView imageView) {
        this.imageView = imageView;
        if (SketchUtils.isSupportLargeImageByAPIVersion()) {
            largeImageViewer = new LargeImageViewer(imageView.getContext(), this);
            if (!imageView.isSupportZoom()) {
                imageView.setSupportZoom(true);
            }
            imageView.getImageZoomFunction().getImageZoomer().addOnMatrixChangeListener(this);
        }
    }

    @Override
    public void onAttachedToWindow() {
        if (SketchUtils.isSupportLargeImageByAPIVersion()) {
            resetImage();
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (SketchUtils.isSupportLargeImageByAPIVersion()) {
            if (largeImageViewer.isAvailable()) {
                largeImageViewer.draw(canvas);
            }
        }
    }

    @Override
    public boolean onDetachedFromWindow() {
        if (SketchUtils.isSupportLargeImageByAPIVersion()) {
            recycle("onDetachedFromWindow");
        }
        return false;
    }

    @Override
    public boolean onDrawableChanged(String callPosition, Drawable oldDrawable, Drawable newDrawable) {
        if (SketchUtils.isSupportLargeImageByAPIVersion()) {
            resetImage();
        }
        return false;
    }

    @Override
    public void onMatrixChanged(ImageZoomer imageZoomer) {
        if (SketchUtils.isSupportLargeImageByAPIVersion()) {
            if (largeImageViewer.isAvailable() || largeImageViewer.isInitializing()) {
                Drawable drawable = imageView.getDrawable();
                if (drawable != null) {
                    UpdateParams updateParams = largeImageViewer.getUpdateParams();
                    imageZoomer.getDrawMatrix(updateParams.drawMatrix);
                    imageZoomer.getVisibleRect(updateParams.visibleRect);
                    updateParams.setPreviewDrawableSize(imageZoomer.getDrawableWidth(), imageZoomer.getDrawableHeight());
                    updateParams.setImageViewSize(imageZoomer.getImageViewWidth(), imageZoomer.getImageViewHeight());
                    largeImageViewer.update(updateParams);
                } else {
                    largeImageViewer.update(null);
                }
            }
        }
    }

    private void resetImage() {
        if (SketchUtils.isSupportLargeImageByAPIVersion()) {
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
                largeImageViewer.setImage(((SketchDrawable) drawable).getImageUri());
            } else {
                largeImageViewer.setImage(null);
            }
        }
    }

    public void recycle(String why) {
        if (SketchUtils.isSupportLargeImageByAPIVersion()) {
            largeImageViewer.recycle(why);
        }
    }

    @Override
    public void invalidate() {
        if (SketchUtils.isSupportLargeImageByAPIVersion()) {
            imageView.invalidate();
        }
    }

    public LargeImageViewer getLargeImageViewer() {
        return largeImageViewer;
    }
}
