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
// TODO: 16/9/11 不再需要wait updateParams了，因为初始化完成后会主动更新，最后也不再需要updateParams了，只需在LargeImageFunction中缓存这些数据即可
public class LargeImageFunction extends SketchImageView.Function implements ImageZoomer.OnMatrixChangedListener, LargeImageViewer.Callback {
    private static final String NAME = "LargeImageFunction";

    private SketchImageView imageView;
    private LargeImageViewer largeImageViewer;
    private UpdateParams updateParams;

    private String imageUri;

    public LargeImageFunction(SketchImageView imageView) {
        this.imageView = imageView;
        this.updateParams = new UpdateParams();
        largeImageViewer = new LargeImageViewer(imageView.getContext(), this);
        if (!imageView.isSupportZoom()) {
            imageView.setSupportZoom(true);
        }
        imageView.getImageZoomFunction().getImageZoomer().addOnMatrixChangeListener(this);
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

        Drawable drawable = imageView.getDrawable();
        if (drawable != null) {
            updateParams.reset();

            imageZoomer.getDrawMatrix(updateParams.drawMatrix);
            imageZoomer.getVisibleRect(updateParams.visibleRect);
            updateParams.setPreviewDrawableSize(imageZoomer.getDrawableWidth(), imageZoomer.getDrawableHeight());
            updateParams.setImageViewSize(imageZoomer.getImageViewWidth(), imageZoomer.getImageViewHeight());

            largeImageViewer.update(updateParams);
        } else {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". drawable is null. onMatrixChanged. " + imageUri);
            }
            largeImageViewer.update(null);
        }
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
        ImageZoomer imageZoomer = imageView.isSupportZoom() ? imageView.getImageZoomFunction().getImageZoomer() : null;
        if (imageZoomer != null) {
            onMatrixChanged(imageZoomer);
        }
    }

    public LargeImageViewer getLargeImageViewer() {
        return largeImageViewer;
    }
}
