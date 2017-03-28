package me.xiaopan.sketch.drawable;

import android.graphics.Bitmap;

import me.xiaopan.sketch.util.SketchUtils;

public abstract class SketchBitmap {

    private String key;
    private String uri;
    protected Bitmap bitmap;
    private ImageAttrs imageAttrs;

    protected SketchBitmap(Bitmap bitmap, String key, String uri, ImageAttrs imageAttrs) {
        if (bitmap == null || bitmap.isRecycled()) {
            throw new IllegalArgumentException("bitmap is null or recycled");
        }

        this.bitmap = bitmap;
        this.key = key;
        this.uri = uri;
        this.imageAttrs = imageAttrs;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public String getKey() {
        return key;
    }

    public String getUri() {
        return uri;
    }

    public int getOriginWidth() {
        return imageAttrs.getOriginWidth();
    }

    public int getOriginHeight() {
        return imageAttrs.getOriginHeight();
    }

    public String getMimeType() {
        return imageAttrs.getMimeType();
    }

    public ImageAttrs getImageAttrs() {
        return imageAttrs;
    }

    public abstract String getInfo();

    public int getByteCount() {
        return SketchUtils.getBitmapByteSize(getBitmap());
    }

    public Bitmap.Config getBitmapConfig() {
        return bitmap != null ? bitmap.getConfig() : null;
    }
}
