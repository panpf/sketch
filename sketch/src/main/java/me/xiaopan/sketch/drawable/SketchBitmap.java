package me.xiaopan.sketch.drawable;

import android.graphics.Bitmap;

import me.xiaopan.sketch.util.SketchUtils;

public abstract class SketchBitmap {

    protected Bitmap bitmap;

    private String key;
    private String uri;
    private int originWidth;
    private int originHeight;
    private String mimeType;

    protected SketchBitmap(Bitmap bitmap, String key, String uri, int originWidth, int originHeight, String mimeType) {
        if (bitmap == null || bitmap.isRecycled()) {
            throw new IllegalArgumentException("bitmap is null or recycled");
        }

        this.bitmap = bitmap;
        this.key = key;
        this.uri = uri;
        this.originWidth = originWidth;
        this.originHeight = originHeight;
        this.mimeType = mimeType;
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
        return originWidth;
    }

    public int getOriginHeight() {
        return originHeight;
    }

    public String getMimeType() {
        return mimeType;
    }

    public abstract String getInfo();

    public int getByteCount() {
        return SketchUtils.getBitmapByteSize(getBitmap());
    }

    public Bitmap.Config getBitmapConfig() {
        return bitmap != null ? bitmap.getConfig() : null;
    }
}
