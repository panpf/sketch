package me.xiaopan.sketch.feature.large;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;

public class DecodeParams {
    private Rect srcRect;
    private int inSampleSize;

    private RectF visibleRect;
    private float scale;

    private Bitmap bitmap;

    public void set(Rect srcRect, int inSampleSize, RectF visibleRect, float scale) {
        if (this.srcRect == null) {
            this.srcRect = new Rect();
        }
        if (srcRect != null) {
            this.srcRect.set(srcRect);
        } else {
            this.srcRect.setEmpty();
        }
        this.inSampleSize = inSampleSize;

        if (this.visibleRect == null) {
            this.visibleRect = new RectF();
        }
        if (srcRect != null) {
            this.visibleRect.set(visibleRect);
        } else {
            this.visibleRect.setEmpty();
        }
        this.scale = scale;
    }

    public void set(DecodeParams decodeParams) {
        if (decodeParams != null) {
            set(decodeParams.srcRect, decodeParams.inSampleSize, decodeParams.visibleRect, decodeParams.scale);
        } else {
            set(null, 0, null, 0);
        }
    }

    public boolean isEmpty() {
        return srcRect == null || srcRect.isEmpty() ||
                inSampleSize == 0 ||
                visibleRect == null || visibleRect.isEmpty() ||
                scale == 0;
    }

    public int getInSampleSize() {
        return inSampleSize;
    }

    public float getScale() {
        return scale;
    }

    public Rect getSrcRect() {
        return srcRect;
    }

    public RectF getVisibleRect() {
        return visibleRect;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
