package me.xiaopan.sketch.drawable;

import android.graphics.Bitmap;

import me.xiaopan.sketch.util.SketchUtils;

public abstract class SketchBitmap {

    protected Bitmap bitmap;

    private String imageId;
    private String imageUri;
    private int originWidth;
    private int originHeight;
    private String mimeType;

    protected SketchBitmap(Bitmap bitmap, String imageId, String imageUri, int originWidth, int originHeight, String mimeType) {
        if (bitmap == null || bitmap.isRecycled()) {
            throw new IllegalArgumentException("bitmap is null or recycled");
        }

        this.bitmap = bitmap;
        this.imageId = imageId;
        this.imageUri = imageUri;
        this.originWidth = originWidth;
        this.originHeight = originHeight;
        this.mimeType = mimeType;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public String getImageId() {
        return imageId;
    }

    public String getImageUri() {
        return imageUri;
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
