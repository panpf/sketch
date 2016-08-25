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
import android.os.Build;
import android.util.Log;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.SketchImageView;
import me.xiaopan.sketch.drawable.BindDrawable;
import me.xiaopan.sketch.drawable.SketchDrawable;
import me.xiaopan.sketch.feature.large.SuperLargeImageViewer;
import me.xiaopan.sketch.feature.large.UpdateParams;
import me.xiaopan.sketch.feature.zoom.ImageZoomer;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 显示超级大图功能
 */
// TODO: 16/8/9 BitmapRegionDecoder仅支持jpg，png，bmp等图片
public class SuperLargeImageFunction extends SketchImageView.Function implements ImageZoomer.OnMatrixChangedListener, SuperLargeImageViewer.Callback {
    private static final String NAME = "SuperLargeImageFunction";

    private SketchImageView imageView;
    private SuperLargeImageViewer superLargeImageViewer;

    public SuperLargeImageFunction(SketchImageView imageView) {
        this.imageView = imageView;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
            superLargeImageViewer = new SuperLargeImageViewer(imageView.getContext(), this);
            if (!imageView.isEnableZoomFunction()) {
                imageView.setEnableZoomFunction(true);
            }
            imageView.getImageZoomFunction().getImageZoomer().addOnMatrixChangeListener(this);
        }
    }

    @Override
    public void onAttachedToWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
            resetImage();
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
            if (superLargeImageViewer.isAvailable()) {
                superLargeImageViewer.draw(canvas);
            }
        }
    }

    @Override
    public boolean onDetachedFromWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
            recycle();
        }
        return false;
    }

    @Override
    public boolean onDrawableChanged(String callPosition, Drawable oldDrawable, Drawable newDrawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
            resetImage();
        }
        return false;
    }

    @Override
    public void onMatrixChanged(ImageZoomer imageZoomer) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
            if (superLargeImageViewer.isAvailable() || superLargeImageViewer.isInitializing()) {
                Drawable drawable = imageView.getDrawable();
                if (drawable != null) {
                    UpdateParams updateParams = superLargeImageViewer.getUpdateParams();
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
    }

    private void resetImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
            Drawable drawable = SketchUtils.getLastDrawable(imageView.getDrawable());
            boolean drawableQualified = false;
            if (drawable != null && drawable instanceof SketchDrawable && !(drawable instanceof BindDrawable)) {
                SketchDrawable sketchDrawable = (SketchDrawable) drawable;
                drawableQualified = drawable.getIntrinsicWidth() < sketchDrawable.getOriginWidth();
                drawableQualified |= drawable.getIntrinsicHeight() < sketchDrawable.getOriginHeight();

                if (drawableQualified) {
                    if (Sketch.isDebugMode()) {
                        Log.d(Sketch.TAG, NAME + ". Use large figure function" +
                                ". previewDrawable: " + drawable.getIntrinsicWidth() + "x" + drawable.getIntrinsicHeight() +
                                ", originImage: " + sketchDrawable.getOriginWidth() + "x" + sketchDrawable.getOriginHeight() +
                                ". " + sketchDrawable.getImageId());
                    }
                } else {
                    if (Sketch.isDebugMode()) {
                        Log.w(Sketch.TAG, NAME + ". Don't need to use large figure function" +
                                ". previewDrawable: " + drawable.getIntrinsicWidth() + "x" + drawable.getIntrinsicHeight() +
                                ", originImage: " + sketchDrawable.getOriginWidth() + "x" + sketchDrawable.getOriginHeight() +
                                ". " + sketchDrawable.getImageId());
                    }
                }
            }

            if (drawableQualified) {
                superLargeImageViewer.setImage(((SketchDrawable) drawable).getImageUri());
            } else {
                superLargeImageViewer.setImage(null);
            }
        }
    }

    public void recycle() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
            superLargeImageViewer.recycle();
        }
    }

    @Override
    public void invalidate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
            imageView.invalidate();
        }
    }
}
