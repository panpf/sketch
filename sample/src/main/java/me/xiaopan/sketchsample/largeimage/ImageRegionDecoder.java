package me.xiaopan.sketchsample.largeimage;

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
import me.xiaopan.sketch.request.UriScheme;
import me.xiaopan.sketch.util.SketchUtils;

public class ImageRegionDecoder {
    private static final String NAME = "ImageRegionDecoder";
    private final Object decodeLock = new Object();
    private Context context;
    private String uri;
    private int imageWidth;
    private int imageHeight;
    private BitmapRegionDecoder decoder;

    public ImageRegionDecoder(Context context, String uri) {
        this.context = context;
        this.uri = uri;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public int getImageWidth() {
        return imageWidth;
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

    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
    public void init() {
        if (isReady()) {
            return;
        }

        UriScheme uriScheme = UriScheme.valueOfUri(uri);
        if (uriScheme == null) {
            Log.e(Sketch.TAG, NAME + ". unknown uri: " + uri);
            return;
        }

        if (uriScheme == UriScheme.NET) {
            DiskCache.Entry diskCacheEntry = Sketch.with(context).getConfiguration().getDiskCache().get(uri);
            if (diskCacheEntry == null) {
                Log.e(Sketch.TAG, NAME + ". not found disk cache: " + uri);
                return;
            }
            String diskCacheFilePath = diskCacheEntry.getFile().getPath();
            try {
                decoder = BitmapRegionDecoder.newInstance(diskCacheFilePath, false);
            } catch (IOException e) {
                e.printStackTrace();
                if (e instanceof FileNotFoundException) {
                    Log.e(Sketch.TAG, NAME + ". not found disk cache file: " + uri);
                }
            }
        } else if (uriScheme == UriScheme.FILE) {
            String filePath = UriScheme.FILE.crop(uri);
            try {
                decoder = BitmapRegionDecoder.newInstance(filePath, false);
            } catch (IOException e) {
                e.printStackTrace();
                if (e instanceof FileNotFoundException) {
                    Log.e(Sketch.TAG, NAME + ". not found file: " + uri);
                }
            }
        } else if (uriScheme == UriScheme.CONTENT) {
            Uri fileUri = Uri.parse(UriScheme.CONTENT.crop(uri));
            InputStream inputStream;
            try {
                inputStream = context.getContentResolver().openInputStream(fileUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.e(Sketch.TAG, NAME + ". not found content file: " + uri);
                return;
            }
            try {
                decoder = BitmapRegionDecoder.newInstance(inputStream, false);
            } catch (IOException e) {
                e.printStackTrace();
                SketchUtils.close(inputStream);
            }
        } else if (uriScheme == UriScheme.ASSET) {
            String assetFileName = UriScheme.ASSET.crop(uri);
            InputStream inputStream;
            try {
                inputStream = context.getAssets().open(assetFileName);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(Sketch.TAG, NAME + ". not found asset file: " + uri);
                return;
            }
            try {
                decoder = BitmapRegionDecoder.newInstance(inputStream, false);
            } catch (IOException e) {
                e.printStackTrace();
                SketchUtils.close(inputStream);
            }
        } else if (uriScheme == UriScheme.DRAWABLE) {
            int drawableResId;
            try {
                drawableResId = Integer.valueOf(UriScheme.DRAWABLE.crop(uri));
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return;
            }
            InputStream inputStream;
            try {
                inputStream = context.getResources().openRawResource(drawableResId);
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
                Log.e(Sketch.TAG, NAME + ". not found drawable: " + uri);
                return;
            }
            try {
                decoder = BitmapRegionDecoder.newInstance(inputStream, false);
            } catch (IOException e) {
                e.printStackTrace();
                SketchUtils.close(inputStream);
            }
        } else {
            Log.e(Sketch.TAG, NAME + ". new uri type: " + uriScheme.name() + ", uri: " + uri);
            return;
        }

        if (decoder != null) {
            imageWidth = decoder.getWidth();
            imageHeight = decoder.getHeight();
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
