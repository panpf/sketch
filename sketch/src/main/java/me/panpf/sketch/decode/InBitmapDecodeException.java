package me.panpf.sketch.decode;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import me.panpf.sketch.SketchException;

public class InBitmapDecodeException extends SketchException {
    @NonNull
    private String imageUri;
    private int imageWidth;
    private int imageHeight;
    private @NonNull String imageMimeType;
    private int inSampleSize;
    @NonNull
    private Bitmap inBitmap;

    public InBitmapDecodeException(@NonNull Throwable cause, @NonNull String imageUri, int imageWidth, int imageHeight, @NonNull String imageMimeType, int inSampleSize, @NonNull Bitmap inBitmap) {
        super(cause);
        this.imageUri = imageUri;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.imageMimeType = imageMimeType;
        this.inSampleSize = inSampleSize;
        this.inBitmap = inBitmap;
    }

    @NonNull
    @Override
    public synchronized Throwable getCause() {
        return super.getCause();
    }

    @NonNull
    public String getImageUri() {
        return imageUri;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    @NonNull
    public String getImageMimeType() {
        return imageMimeType;
    }

    public int getInSampleSize() {
        return inSampleSize;
    }

    @NonNull
    public Bitmap getInBitmap() {
        return inBitmap;
    }
}
