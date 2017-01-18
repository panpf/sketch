package me.xiaopan.sketch.drawable;

import android.graphics.Bitmap;

import me.xiaopan.sketch.util.SketchUtils;

public abstract class SketchBitmap {

    protected Bitmap bitmap;
    private ImageInfo imageInfo;

    protected SketchBitmap(Bitmap bitmap, ImageInfo imageInfo) {
        if (bitmap == null || bitmap.isRecycled()) {
            throw new IllegalArgumentException("bitmap is null or recycled");
        }

        this.bitmap = bitmap;
        this.imageInfo = imageInfo;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public String getKey() {
        return imageInfo.getKey();
    }

    public String getUri() {
        return imageInfo.getUri();
    }

    public int getOriginWidth() {
        return imageInfo.getOriginWidth();
    }

    public int getOriginHeight() {
        return imageInfo.getOriginHeight();
    }

    public String getMimeType() {
        return imageInfo.getMimeType();
    }

    public abstract String getInfo();

    public int getByteCount() {
        return SketchUtils.getBitmapByteSize(getBitmap());
    }

    public Bitmap.Config getBitmapConfig() {
        return bitmap != null ? bitmap.getConfig() : null;
    }
}
