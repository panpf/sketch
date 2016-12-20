package me.xiaopan.sketch.drawable;

import android.content.ContentResolver;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import me.xiaopan.sketch.cache.BitmapPool;
import me.xiaopan.sketch.request.ImageFrom;
import me.xiaopan.sketch.util.SketchUtils;
import pl.droidsonroids.gif.GifDrawable;

public class SketchGifDrawable extends GifDrawable implements SketchDrawable {
    protected String logName = "SketchGifDrawable";

    private String imageId;
    private String imageUri;
    private int originWidth;
    private int originHeight;
    private String mimeType;
    private ImageFrom imageFrom;

    public SketchGifDrawable(BitmapPool bitmapPool, AssetFileDescriptor afd) throws IOException {
        super(bitmapPool, afd);
    }

    public SketchGifDrawable(BitmapPool bitmapPool, AssetManager assets, String assetName) throws IOException {
        super(bitmapPool, assets, assetName);
    }

    public SketchGifDrawable(BitmapPool bitmapPool, ByteBuffer buffer) throws IOException {
        super(bitmapPool, buffer);
    }

    public SketchGifDrawable(BitmapPool bitmapPool, byte[] bytes) throws IOException {
        super(bitmapPool, bytes);
    }

    public SketchGifDrawable(BitmapPool bitmapPool, FileDescriptor fd) throws IOException {
        super(bitmapPool, fd);
    }

    public SketchGifDrawable(BitmapPool bitmapPool, File file) throws IOException {
        super(bitmapPool, file);
    }

    public SketchGifDrawable(BitmapPool bitmapPool, String filePath) throws IOException {
        super(bitmapPool, filePath);
    }

    public SketchGifDrawable(BitmapPool bitmapPool, Resources res, int id) throws Resources.NotFoundException, IOException {
        super(bitmapPool, res, id);
    }

    public SketchGifDrawable(BitmapPool bitmapPool, ContentResolver resolver, Uri uri) throws IOException {
        super(bitmapPool, resolver, uri);
    }

    public SketchGifDrawable(BitmapPool bitmapPool, InputStream stream) throws IOException {
        super(bitmapPool, stream);
    }

    @Override
    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    @Override
    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    @Override
    public int getImageWidth() {
        return originWidth;
    }

    public void setOriginWidth(int originWidth) {
        this.originWidth = originWidth;
    }

    @Override
    public int getImageHeight() {
        return originHeight;
    }

    public void setOriginHeight(int originHeight) {
        this.originHeight = originHeight;
    }

    @Override
    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @Override
    public ImageFrom getImageFrom() {
        return imageFrom;
    }

    @Override
    public void setImageFrom(ImageFrom imageFrom) {
        this.imageFrom = imageFrom;
    }

    @Override
    public String getInfo() {
        return SketchUtils.makeImageInfo(logName, getBitmap(), mimeType, getAllocationByteCount());
    }

    @Override
    public int getByteCount() {
        return getFrameByteCount();
    }

    @Override
    public Bitmap.Config getBitmapConfig() {
        Bitmap bitmap = getBitmap();
        return bitmap != null ? bitmap.getConfig() : null;
    }
}
