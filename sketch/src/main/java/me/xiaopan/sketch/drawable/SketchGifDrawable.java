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

import me.xiaopan.sketch.util.SketchUtils;
import pl.droidsonroids.gif.GifDrawable;

public class SketchGifDrawable extends GifDrawable implements SketchDrawable {
    protected String logName = "SketchGifDrawable";

    private String imageId;
    private String imageUri;
    private int originWidth;
    private int originHeight;
    private String mimeType;

    public SketchGifDrawable(AssetFileDescriptor afd) throws IOException {
        super(afd);
    }

    public SketchGifDrawable(AssetManager assets, String assetName) throws IOException {
        super(assets, assetName);
    }

    public SketchGifDrawable(ByteBuffer buffer) throws IOException {
        super(buffer);
    }

    public SketchGifDrawable(byte[] bytes) throws IOException {
        super(bytes);
    }

    public SketchGifDrawable(FileDescriptor fd) throws IOException {
        super(fd);
    }

    public SketchGifDrawable(File file) throws IOException {
        super(file);
    }

    public SketchGifDrawable(String filePath) throws IOException {
        super(filePath);
    }

    public SketchGifDrawable(Resources res, int id) throws Resources.NotFoundException, IOException {
        super(res, id);
    }

    public SketchGifDrawable(ContentResolver resolver, Uri uri) throws IOException {
        super(resolver, uri);
    }

    public SketchGifDrawable(InputStream stream) throws IOException {
        super(stream);
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
    public int getOriginWidth() {
        return originWidth;
    }

    public void setOriginWidth(int originWidth) {
        this.originWidth = originWidth;
    }

    @Override
    public int getOriginHeight() {
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
    public String getInfo() {
        return SketchUtils.getInfo(logName, getBitmap(), mimeType, getAllocationByteCount());
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
