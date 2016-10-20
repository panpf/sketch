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
import android.graphics.drawable.Drawable;

import me.xiaopan.sketch.request.FixedSize;
import me.xiaopan.sketch.util.SketchUtils;

// TODO: 16/10/20 搞一个专用的FixedSizeBitmapDrawable
public class FixedSizeRefBitmapDrawable extends Drawable implements RefDrawable {
    private static final int DEFAULT_PAINT_FLAGS = Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG;

    private int bitmapWidth;
    private int bitmapHeight;
    private Rect srcRect;
    private Rect destRect;
    private Paint paint;
    private FixedSize fixedSize;
    private Bitmap bitmap;
    private RefBitmapDrawable wrapperDrawable;

    public FixedSizeRefBitmapDrawable(RefBitmapDrawable wrapperDrawable, FixedSize fixedSize) {
        this.wrapperDrawable = wrapperDrawable;
        if (wrapperDrawable != null) {
            wrapperDrawable.setLogName("FixedSizeRefBitmapDrawable");
        }
        this.bitmap = wrapperDrawable != null ? wrapperDrawable.getBitmap() : null;
        if (bitmap != null) {
            this.bitmapWidth = bitmap.getWidth();
            this.bitmapHeight = bitmap.getHeight();
            this.paint = new Paint(DEFAULT_PAINT_FLAGS);
            this.destRect = new Rect(0, 0, bitmapWidth, bitmapHeight);
            this.fixedSize = fixedSize;
            if (fixedSize == null) {
                this.srcRect = new Rect(0, 0, bitmapWidth, bitmapHeight);
                setBounds(0, 0, bitmapWidth, bitmapHeight);
            } else {
                int fixedWidth = fixedSize.getWidth();
                int fixedHeight = fixedSize.getHeight();
                if (bitmapWidth == 0 || bitmapHeight == 0) {
                    this.srcRect = new Rect(0, 0, 0, 0);
                } else if ((float) bitmapWidth / (float) bitmapHeight == (float) fixedWidth / (float) fixedHeight) {
                    this.srcRect = new Rect(0, 0, bitmapWidth, bitmapHeight);
                } else {
                    this.srcRect = new Rect();
                    SketchUtils.mapping(bitmapWidth, bitmapHeight, fixedWidth, fixedHeight, srcRect);
                }
                setBounds(0, 0, fixedSize.getWidth(), fixedSize.getHeight());
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (bitmap != null && !bitmap.isRecycled() && srcRect != null && destRect != null && paint != null) {
            canvas.drawBitmap(bitmap, srcRect, destRect, paint);
        }
    }

    @Override
    public int getIntrinsicWidth() {
        return fixedSize != null ? fixedSize.getWidth() : bitmapWidth;
    }

    @Override
    public int getIntrinsicHeight() {
        return fixedSize != null ? fixedSize.getHeight() : bitmapHeight;
    }

    @Override
    public int getAlpha() {
        return paint != null ? paint.getAlpha() : super.getAlpha();
    }

    @Override
    public void setAlpha(int alpha) {
        if (paint != null) {
            final int oldAlpha = paint.getAlpha();
            if (alpha != oldAlpha) {
                paint.setAlpha(alpha);
                invalidateSelf();
            }
        }
    }

    @Override
    public ColorFilter getColorFilter() {
        return paint != null ? paint.getColorFilter() : null;
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        if (paint != null) {
            paint.setColorFilter(cf);
            invalidateSelf();
        }
    }

    @Override
    public void setDither(boolean dither) {
        if (paint != null) {
            paint.setDither(dither);
            invalidateSelf();
        }
    }

    @Override
    public void setFilterBitmap(boolean filter) {
        if (paint != null) {
            paint.setFilterBitmap(filter);
            invalidateSelf();
        }
    }

    @Override
    public int getOpacity() {
        return (bitmap == null || paint == null || bitmap.hasAlpha() || paint.getAlpha() < 255) ? PixelFormat.TRANSLUCENT : PixelFormat.OPAQUE;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        if (destRect != null) {
            destRect.set(0, 0, bounds.width(), bounds.height());
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
        return wrapperDrawable != null ? wrapperDrawable.getImageId() : null;
    }

    @Override
    public String getImageUri() {
        return wrapperDrawable != null ? wrapperDrawable.getImageUri() : null;
    }

    @Override
    public int getImageWidth() {
        return wrapperDrawable != null ? wrapperDrawable.getImageWidth() : 0;
    }

    @Override
    public int getImageHeight() {
        return wrapperDrawable != null ? wrapperDrawable.getImageHeight() : 0;
    }

    @Override
    public String getMimeType() {
        return wrapperDrawable != null ? wrapperDrawable.getMimeType() : null;
    }

    @Override
    public void setIsDisplayed(String callingStation, boolean displayed) {
        if (wrapperDrawable != null) {
            wrapperDrawable.setIsDisplayed(callingStation, displayed);
        }
    }

    @Override
    public void setIsCached(String callingStation, boolean cached) {
        if (wrapperDrawable != null) {
            wrapperDrawable.setIsCached(callingStation, cached);
        }
    }

    @Override
    public void setIsWaitDisplay(String callingStation, boolean waitDisplay) {
        if (wrapperDrawable != null) {
            wrapperDrawable.setIsWaitDisplay(callingStation, waitDisplay);
        }
    }

    @Override
    public boolean isRecycled() {
        return wrapperDrawable == null || wrapperDrawable.isRecycled();
    }

    @Override
    public void recycle() {
        if (wrapperDrawable != null) {
            wrapperDrawable.recycle();
        }
    }

    @Override
    public String getInfo() {
        return wrapperDrawable != null ? wrapperDrawable.getInfo() : null;
    }

    @Override
    public int getByteCount() {
        return wrapperDrawable != null ? wrapperDrawable.getByteCount() : 0;
    }

    @Override
    public Bitmap.Config getBitmapConfig() {
        return wrapperDrawable != null ? wrapperDrawable.getBitmapConfig() : null;
    }
}