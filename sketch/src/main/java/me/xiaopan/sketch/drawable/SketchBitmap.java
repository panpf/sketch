package me.xiaopan.sketch.drawable;

import android.graphics.Bitmap;

import me.xiaopan.sketch.util.SketchUtils;

public class SketchBitmap {
    private static final String LOG_NAME = "SketchBitmap";

    public Bitmap bitmap;

    private String imageId;
    private String imageUri;
    private int originWidth;
    private int originHeight;
    private String mimeType;

    public SketchBitmap(Bitmap bitmap, String imageId, String imageUri, int originWidth, int originHeight, String mimeType) {
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

    public SketchBitmap(Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled()) {
            throw new IllegalArgumentException("bitmap is null or recycled");
        }

        this.bitmap = bitmap;
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

    public String getInfo() {
        Bitmap bitmap = getBitmap();
        return SketchUtils.makeImageInfo(LOG_NAME, bitmap, mimeType, SketchUtils.getBitmapByteSize(bitmap));
    }

    public int getByteCount() {
        return SketchUtils.getBitmapByteSize(getBitmap());
    }

    public Bitmap.Config getBitmapConfig() {
        Bitmap bitmap = getBitmap();
        return bitmap != null ? bitmap.getConfig() : null;
    }
}
