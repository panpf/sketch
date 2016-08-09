package me.xiaopan.sketch.feature.large;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.cache.DiskCache;
import me.xiaopan.sketch.decode.ImageFormat;
import me.xiaopan.sketch.request.UriScheme;
import me.xiaopan.sketch.util.SketchUtils;

public class ImageRegionDecoder {
    private static final String NAME = "ImageRegionDecoder";
    private final Object decodeLock = new Object();
    private Context context;
    private String imageUri;
    private int imageWidth;
    private int imageHeight;
    private ImageFormat imageFormat;
    private BitmapRegionDecoder decoder;

    public ImageRegionDecoder(Context context, String imageUri) {
        this.context = context.getApplicationContext();
        this.imageUri = imageUri;
    }

    public Context getContext() {
        return context;
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
        return decoder != null && !decoder.isRecycled();
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
    public void recycle() {
        if (isReady()) {
            decoder.recycle();
            decoder = null;
        }
    }

    public void init() throws NumberFormatException, IOException {
        if (isReady()) {
            return;
        }

        UriScheme uriScheme = UriScheme.valueOfUri(imageUri);
        if (uriScheme == null) {
            Log.e(Sketch.TAG, NAME + ". unknown uri: " + imageUri);
            return;
        }

        if (uriScheme == UriScheme.NET) {
            initByHttp(imageUri);
        } else if (uriScheme == UriScheme.FILE) {
            initByFile(UriScheme.FILE.crop(imageUri));
        } else if (uriScheme == UriScheme.CONTENT) {
            initByContent(Uri.parse(UriScheme.CONTENT.crop(imageUri)));
        } else if (uriScheme == UriScheme.ASSET) {
            initByAsset(UriScheme.ASSET.crop(imageUri));
        } else if (uriScheme == UriScheme.DRAWABLE) {
            initByDrawable(Integer.valueOf(UriScheme.DRAWABLE.crop(imageUri)));
        } else {
            Log.e(Sketch.TAG, NAME + ". new uri type: " + uriScheme.name() + ", uri: " + imageUri);
        }
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
    private void initByHttp(String uri) throws IOException {
        DiskCache.Entry diskCacheEntry = Sketch.with(context).getConfiguration().getDiskCache().get(uri);
        if (diskCacheEntry == null) {
            Log.e(Sketch.TAG, NAME + ". not found disk cache: " + uri);
            return;
        }
        String diskCacheFilePath = diskCacheEntry.getFile().getPath();

        // 解析宽高和类型
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(diskCacheFilePath, options);
        imageWidth = options.outWidth;
        imageHeight = options.outHeight;
        imageFormat = ImageFormat.valueOfMimeType(options.outMimeType);

        // 初始化BitmapRegionDecoder
        try {
            decoder = BitmapRegionDecoder.newInstance(diskCacheFilePath, false);
        } catch (IOException e) {
            if (e instanceof FileNotFoundException) {
                Log.e(Sketch.TAG, NAME + ". not found disk cache file: " + uri);
            } else {
                Log.e(Sketch.TAG, NAME + ". init BitmapRegionDecoder failed: " + uri);
            }
            throw e;
        }
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
    private void initByFile(String filePath) throws IOException {
        // 解析宽高和类型
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        imageWidth = options.outWidth;
        imageHeight = options.outHeight;
        imageFormat = ImageFormat.valueOfMimeType(options.outMimeType);

        // 初始化BitmapRegionDecoder
        try {
            decoder = BitmapRegionDecoder.newInstance(filePath, false);
        } catch (IOException e) {
            if (e instanceof FileNotFoundException) {
                Log.e(Sketch.TAG, NAME + ". not found file: " + imageUri);
            } else {
                Log.e(Sketch.TAG, NAME + ". init BitmapRegionDecoder failed: " + imageUri);
            }
            throw e;
        }
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
    private void initByContent(Uri uri) throws IOException {
        // 解析宽高和类型
        InputStream inputStream;
        try {
            inputStream = context.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            Log.e(Sketch.TAG, NAME + ". not found content file: " + uri);
            throw e;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, null, options);
        SketchUtils.close(inputStream);
        imageWidth = options.outWidth;
        imageHeight = options.outHeight;
        imageFormat = ImageFormat.valueOfMimeType(options.outMimeType);

        // 初始化BitmapRegionDecoder
        try {
            inputStream = context.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            Log.e(Sketch.TAG, NAME + ". not found content file: " + uri);
            throw e;
        }
        try {
            decoder = BitmapRegionDecoder.newInstance(inputStream, false);
        } catch (IOException e) {
            SketchUtils.close(inputStream);
            Log.e(Sketch.TAG, NAME + ". init BitmapRegionDecoder failed: " + uri);
            throw e;
        }
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
    private void initByAsset(String fileName) throws IOException {
        // 解析宽高和类型
        InputStream inputStream;
        try {
            inputStream = context.getAssets().open(fileName);
        } catch (IOException e) {
            Log.e(Sketch.TAG, NAME + ". not found asset file: " + imageUri);
            throw e;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, null, options);
        SketchUtils.close(inputStream);
        imageWidth = options.outWidth;
        imageHeight = options.outHeight;
        imageFormat = ImageFormat.valueOfMimeType(options.outMimeType);

        // 初始化BitmapRegionDecoder
        try {
            inputStream = context.getAssets().open(fileName);
        } catch (IOException e) {
            Log.e(Sketch.TAG, NAME + ". not found asset file: " + imageUri);
            throw e;
        }
        try {
            decoder = BitmapRegionDecoder.newInstance(inputStream, false);
        } catch (IOException e) {
            Log.e(Sketch.TAG, NAME + ". init BitmapRegionDecoder failed: " + imageUri);
            SketchUtils.close(inputStream);
            throw e;
        }
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
    private void initByDrawable(int drawableResId) throws IOException {
        // 解析宽高和类型
        InputStream inputStream;
        try {
            inputStream = context.getResources().openRawResource(drawableResId);
        } catch (Resources.NotFoundException e) {
            Log.e(Sketch.TAG, NAME + ". not found drawable: " + imageUri);
            throw e;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, null, options);
        SketchUtils.close(inputStream);
        imageWidth = options.outWidth;
        imageHeight = options.outHeight;
        imageFormat = ImageFormat.valueOfMimeType(options.outMimeType);

        // 初始化BitmapRegionDecoder
        try {
            inputStream = context.getResources().openRawResource(drawableResId);
        } catch (Resources.NotFoundException e) {
            Log.e(Sketch.TAG, NAME + ". not found drawable: " + imageUri);
            throw e;
        }
        try {
            decoder = BitmapRegionDecoder.newInstance(inputStream, false);
        } catch (IOException e) {
            SketchUtils.close(inputStream);
            Log.e(Sketch.TAG, NAME + ". init BitmapRegionDecoder failed: " + imageUri);
            throw e;
        }
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
    public Bitmap decodeRegion(Rect srcRect, BitmapFactory.Options options) {
        synchronized (decodeLock) {
            if (!isReady()) {
                return null;
            }

            return decoder.decodeRegion(srcRect, options);
        }
    }
}
