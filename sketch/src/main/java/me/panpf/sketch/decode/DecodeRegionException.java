package me.panpf.sketch.decode;

import android.graphics.Rect;

import androidx.annotation.NonNull;

import me.panpf.sketch.SketchException;

public class DecodeRegionException extends SketchException {
    @NonNull
    private String imageUri;
    private int imageWidth;
    private int imageHeight;
    @NonNull
    private String imageMimeType;
    @NonNull
    private Rect srcRect;
    private int inSampleSize;

    public DecodeRegionException(@NonNull Throwable cause, @NonNull String imageUri, int imageWidth,
                                 int imageHeight, @NonNull String imageMimeType, @NonNull Rect srcRect, int inSampleSize) {
        super(cause);
        this.imageUri = imageUri;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.imageMimeType = imageMimeType;
        this.srcRect = srcRect;
        this.inSampleSize = inSampleSize;
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

    @NonNull
    public Rect getSrcRect() {
        return srcRect;
    }

    public int getInSampleSize() {
        return inSampleSize;
    }
}
