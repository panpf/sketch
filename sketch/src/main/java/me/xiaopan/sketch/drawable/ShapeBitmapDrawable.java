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

package me.xiaopan.sketch.drawable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.feature.ResizeCalculator;
import me.xiaopan.sketch.request.ImageFrom;
import me.xiaopan.sketch.request.ShapeSize;
import me.xiaopan.sketch.shaper.ImageShaper;

/**
 * 可以改变BitmapDrawable的形状和尺寸
 * <p>
 * fixedSize用来改变尺寸，如果bitmap的尺寸和fixedSize的比例不一致，那么就仅显示bitmap的中间部分（参考CENTER_CROP的效果）
 * </p>
 * <p>
 * shapeImage用来改变形状
 * </p>
 */
public class ShapeBitmapDrawable extends Drawable implements RefDrawable {
    private static final int DEFAULT_PAINT_FLAGS = Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG;

    private BitmapDrawable bitmapDrawable;
    private ShapeSize shapeSize;
    private ImageShaper imageShaper;

    private Paint paint;
    private Rect srcRect;
    private BitmapShader bitmapShader;

    private RefDrawable refDrawable;
    private SketchDrawable sketchDrawable;

    private ResizeCalculator resizeCalculator;

    public ShapeBitmapDrawable(Context context, BitmapDrawable bitmapDrawable, ShapeSize shapeSize, ImageShaper imageShaper) {
        Bitmap bitmap = bitmapDrawable.getBitmap();
        if (bitmap == null || bitmap.isRecycled()) {
            throw new IllegalArgumentException(bitmap == null ? "bitmap is null" : "bitmap recycled");
        }

        if (shapeSize == null && imageShaper == null) {
            throw new IllegalArgumentException("shapeSize is null and shapeImage is null");
        }

        this.bitmapDrawable = bitmapDrawable;
        this.paint = new Paint(DEFAULT_PAINT_FLAGS);
        this.srcRect = new Rect();
        this.resizeCalculator = Sketch.with(context).getConfiguration().getResizeCalculator();

        setShapeSize(shapeSize);
        setImageShaper(imageShaper);

        if (bitmapDrawable instanceof RefDrawable) {
            this.refDrawable = (RefDrawable) bitmapDrawable;
        }

        if (bitmapDrawable instanceof SketchDrawable) {
            this.sketchDrawable = (SketchDrawable) bitmapDrawable;
        }

        if (bitmapDrawable instanceof RefBitmapDrawable) {
            ((RefBitmapDrawable) bitmapDrawable).setLogName("ShapeBitmapDrawable");
        }
    }

    @SuppressWarnings("unused")
    public ShapeBitmapDrawable(Context context, BitmapDrawable bitmapDrawable, ShapeSize shapeSize) {
        this(context, bitmapDrawable, shapeSize, null);
    }

    @SuppressWarnings("unused")
    public ShapeBitmapDrawable(Context context, BitmapDrawable bitmapDrawable, ImageShaper imageShaper) {
        this(context, bitmapDrawable, null, imageShaper);
    }

    @Override
    public void draw(@SuppressWarnings("NullableProblems") Canvas canvas) {
        Rect bounds = getBounds();
        Bitmap bitmap = bitmapDrawable.getBitmap();
        if (bounds.isEmpty() || bitmap == null || bitmap.isRecycled()) {
            return;
        }

        if (imageShaper != null && bitmapShader != null) {
            imageShaper.draw(canvas, paint, bounds);
        } else {
            canvas.drawBitmap(bitmap, srcRect != null && !srcRect.isEmpty() ? srcRect : null, bounds, paint);
        }
    }

    @Override
    public int getIntrinsicWidth() {
        return shapeSize != null ? shapeSize.getWidth() : bitmapDrawable.getIntrinsicWidth();
    }

    @Override
    public int getIntrinsicHeight() {
        return shapeSize != null ? shapeSize.getHeight() : bitmapDrawable.getIntrinsicHeight();
    }

    @Override
    public int getAlpha() {
        return paint.getAlpha();
    }

    @Override
    public void setAlpha(int alpha) {
        final int oldAlpha = paint.getAlpha();
        if (alpha != oldAlpha) {
            paint.setAlpha(alpha);
            invalidateSelf();
        }
    }

    @Override
    public ColorFilter getColorFilter() {
        return paint.getColorFilter();
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        paint.setColorFilter(cf);
        invalidateSelf();
    }

    @Override
    public void setDither(boolean dither) {
        paint.setDither(dither);
        invalidateSelf();
    }

    @Override
    public void setFilterBitmap(boolean filter) {
        paint.setFilterBitmap(filter);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        Bitmap bitmap = bitmapDrawable.getBitmap();
        return (bitmap.hasAlpha() || paint.getAlpha() < 255) ? PixelFormat.TRANSLUCENT : PixelFormat.OPAQUE;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);

        int boundsWidth = bounds.width();
        int boundsHeight = bounds.height();
        int bitmapWidth = bitmapDrawable.getBitmap().getWidth();
        int bitmapHeight = bitmapDrawable.getBitmap().getHeight();

        if (boundsWidth == 0 || boundsHeight == 0 || bitmapWidth == 0 || bitmapHeight == 0) {
            srcRect.setEmpty();
        } else if ((float) bitmapWidth / (float) bitmapHeight == (float) boundsWidth / (float) boundsHeight) {
            srcRect.set(0, 0, bitmapWidth, bitmapHeight);
        } else {
            ImageView.ScaleType scaleType = shapeSize != null ? shapeSize.getScaleType() : ImageView.ScaleType.FIT_CENTER;
            ResizeCalculator.Result result = resizeCalculator.calculator(bitmapWidth, bitmapHeight, boundsWidth, boundsHeight, scaleType, true);
            srcRect.set(result.srcRect);
        }

        if (imageShaper != null && bitmapShader != null) {
            float widthScale = (float) boundsWidth / bitmapWidth;
            float heightScale = (float) boundsHeight / bitmapHeight;

            // 缩放图片充满bounds
            Matrix shaderMatrix = new Matrix();
            float scale = Math.max(widthScale, heightScale);
            shaderMatrix.postScale(scale, scale);

            // 显示图片中间部分
            if (srcRect != null && !srcRect.isEmpty()) {
                shaderMatrix.postTranslate(-srcRect.left * scale, -srcRect.top * scale);
            }

            imageShaper.onUpdateShaderMatrix(shaderMatrix, bounds, bitmapWidth, bitmapHeight, shapeSize, srcRect);
            bitmapShader.setLocalMatrix(shaderMatrix);
            paint.setShader(bitmapShader);
        }
    }

    @SuppressWarnings("unused")
    public BitmapDrawable getBitmapDrawable() {
        return bitmapDrawable;
    }

    @SuppressWarnings("unused")
    public ShapeSize getShapeSize() {
        return shapeSize;
    }

    public void setShapeSize(ShapeSize shapeSize) {
        this.shapeSize = shapeSize;
        invalidateSelf();
    }

    @SuppressWarnings("unused")
    public ImageShaper getImageShaper() {
        return imageShaper;
    }

    public void setImageShaper(ImageShaper imageShaper) {
        this.imageShaper = imageShaper;

        if (this.imageShaper != null) {
            if (bitmapShader == null) {
                bitmapShader = new BitmapShader(bitmapDrawable.getBitmap(), Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
                paint.setShader(bitmapShader);
            }
        } else {
            if (bitmapShader != null) {
                bitmapShader = null;
                paint.setShader(null);
            }
        }

        invalidateSelf();
    }

    @Override
    public String getKey() {
        return sketchDrawable != null ? sketchDrawable.getKey() : null;
    }

    @Override
    public String getUri() {
        return sketchDrawable != null ? sketchDrawable.getUri() : null;
    }

    @Override
    public int getOriginWidth() {
        return sketchDrawable != null ? sketchDrawable.getOriginWidth() : 0;
    }

    @Override
    public int getOriginHeight() {
        return sketchDrawable != null ? sketchDrawable.getOriginHeight() : 0;
    }

    @Override
    public String getMimeType() {
        return sketchDrawable != null ? sketchDrawable.getMimeType() : null;
    }

    @Override
    public int getOrientation() {
        return sketchDrawable != null ? sketchDrawable.getOrientation() : 0;
    }

    @Override
    public int getByteCount() {
        return sketchDrawable != null ? sketchDrawable.getByteCount() : 0;
    }

    @Override
    public Bitmap.Config getBitmapConfig() {
        return sketchDrawable != null ? sketchDrawable.getBitmapConfig() : null;
    }

    @Override
    public ImageFrom getImageFrom() {
        return sketchDrawable != null ? sketchDrawable.getImageFrom() : null;
    }

    @Override
    public void setImageFrom(ImageFrom imageFrom) {
        if (sketchDrawable != null) {
            sketchDrawable.setImageFrom(imageFrom);
        }
    }

    @Override
    public String getInfo() {
        return sketchDrawable != null ? sketchDrawable.getInfo() : null;
    }

    @Override
    public void setIsDisplayed(String callingStation, boolean displayed) {
        if (refDrawable != null) {
            refDrawable.setIsDisplayed(callingStation, displayed);
        }
    }

    @Override
    public void setIsCached(String callingStation, boolean cached) {
        if (refDrawable != null) {
            refDrawable.setIsCached(callingStation, cached);
        }
    }

    @Override
    public void setIsWaitingUse(String callingStation, boolean waitingUse) {
        if (refDrawable != null) {
            refDrawable.setIsWaitingUse(callingStation, waitingUse);
        }
    }

    @Override
    public boolean isRecycled() {
        return refDrawable == null || refDrawable.isRecycled();
    }
}