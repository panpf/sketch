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

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import me.xiaopan.sketch.request.FixedSize;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 可以显示固定尺寸的Drawable，如果bitmap的尺寸和设置的固定尺寸比例不一致，那么就截取bitmap的中间部分显示（参考CENTER_CROP的效果）
 */
public class FixedSizeBitmapDrawable extends Drawable implements RefDrawable {
    private static final int DEFAULT_PAINT_FLAGS = Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG;

    private Bitmap bitmap;
    private FixedSize fixedSize;

    private Paint paint;
    private Rect srcRect;

    private RefDrawable refDrawable;

    public FixedSizeBitmapDrawable(BitmapDrawable drawable, FixedSize fixedSize) {
        this.bitmap = drawable.getBitmap();
        this.paint = new Paint(DEFAULT_PAINT_FLAGS);
        this.fixedSize = fixedSize;
        this.srcRect = new Rect();

        if (bitmap == null || bitmap.isRecycled()) {
            throw new IllegalArgumentException(bitmap == null ? "bitmap is null" : "bitmap recycled");
        }

        if (fixedSize == null) {
            throw new IllegalArgumentException("fixedSize is null");
        }

        if (drawable instanceof RefDrawable) {
            this.refDrawable = (RefDrawable) drawable;
        }

        if (drawable instanceof RefBitmapDrawable) {
            ((RefBitmapDrawable) drawable).setLogName("FixedSizeBitmapDrawable");
        }
    }

    @Override
    public void draw(Canvas canvas) {
        Rect destRect = getBounds();
        if (destRect.isEmpty() || bitmap == null || bitmap.isRecycled() || srcRect.isEmpty()) {
            return;
        }

        canvas.drawBitmap(bitmap, srcRect, destRect, paint);
    }

    @Override
    public int getIntrinsicWidth() {
        return fixedSize.getWidth();
    }

    @Override
    public int getIntrinsicHeight() {
        return fixedSize.getHeight();
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
        return (bitmap.hasAlpha() || paint.getAlpha() < 255) ? PixelFormat.TRANSLUCENT : PixelFormat.OPAQUE;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);

        int boundsWidth = bounds.width();
        int boundsHeight = bounds.height();
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();

        if (boundsWidth == 0 || boundsHeight == 0 || bitmapWidth == 0 || bitmapHeight == 0) {
            srcRect.setEmpty();
        } else if ((float) bitmapWidth / (float) bitmapHeight == (float) boundsWidth / (float) boundsHeight) {
            srcRect.set(0, 0, bitmapWidth, bitmapHeight);
        } else {
            SketchUtils.mapping(bitmapWidth, bitmapHeight, boundsWidth, boundsHeight, srcRect);
        }
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    @SuppressWarnings("unused")
    public FixedSize getFixedSize() {
        return fixedSize;
    }

    @Override
    public String getImageId() {
        return refDrawable != null ? refDrawable.getImageId() : null;
    }

    @Override
    public String getImageUri() {
        return refDrawable != null ? refDrawable.getImageUri() : null;
    }

    @Override
    public int getImageWidth() {
        return refDrawable != null ? refDrawable.getImageWidth() : 0;
    }

    @Override
    public int getImageHeight() {
        return refDrawable != null ? refDrawable.getImageHeight() : 0;
    }

    @Override
    public String getMimeType() {
        return refDrawable != null ? refDrawable.getMimeType() : null;
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
    public void setIsWaitDisplay(String callingStation, boolean waitDisplay) {
        if (refDrawable != null) {
            refDrawable.setIsWaitDisplay(callingStation, waitDisplay);
        }
    }

    @Override
    public boolean isRecycled() {
        return refDrawable == null || refDrawable.isRecycled();
    }

    @Override
    public void recycle() {
        if (refDrawable != null) {
            refDrawable.recycle();
        }
    }

    @Override
    public String getInfo() {
        return refDrawable != null ? refDrawable.getInfo() : null;
    }

    @Override
    public int getByteCount() {
        return refDrawable != null ? refDrawable.getByteCount() : 0;
    }

    @Override
    public Bitmap.Config getBitmapConfig() {
        return refDrawable != null ? refDrawable.getBitmapConfig() : null;
    }
}