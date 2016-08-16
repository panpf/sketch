package me.xiaopan.sketch.feature.large;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.os.Build;

import java.io.InputStream;

import me.xiaopan.sketch.decode.ImageFormat;
import me.xiaopan.sketch.util.SketchUtils;

public class ImageRegionDecoder {
    private final Object decodeLock = new Object();

    private int imageWidth;
    private int imageHeight;
    private String imageUri;
    private ImageFormat imageFormat;

    private InputStream sourceInputStream;
    private BitmapRegionDecoder regionDecoder;

    ImageRegionDecoder(String imageUri, int imageWidth, int imageHeight, ImageFormat imageFormat, BitmapRegionDecoder regionDecoder) {
        this.imageUri = imageUri;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.imageFormat = imageFormat;
        this.regionDecoder = regionDecoder;
    }

    ImageRegionDecoder(String imageUri, int imageWidth, int imageHeight, ImageFormat imageFormat, BitmapRegionDecoder regionDecoder, InputStream sourceInputStream) {
        this.imageUri = imageUri;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.imageFormat = imageFormat;
        this.regionDecoder = regionDecoder;
        this.sourceInputStream = sourceInputStream;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
    public Bitmap decodeRegion(Rect srcRect, BitmapFactory.Options options) {
        synchronized (decodeLock) {
            if (isReady()) {
                return regionDecoder.decodeRegion(srcRect, options);
            } else {
                return null;
            }
        }
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public ImageFormat getImageFormat() {
        return imageFormat;
    }

    public String getImageUri() {
        return imageUri;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
    public boolean isReady() {
        return regionDecoder != null && !regionDecoder.isRecycled();
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
    public void recycle() {
        if (isReady()) {
            regionDecoder.recycle();
            regionDecoder = null;
            if (sourceInputStream != null) {
                SketchUtils.close(sourceInputStream);
                sourceInputStream = null;
            }
        }
    }
}
