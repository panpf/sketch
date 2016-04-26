/*
 * Copyright (C) 2013 Peng fei Pan <sky@xiaopan.me>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.xiaopan.sketch.decode;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.graphics.Point;
import android.net.Uri;
import android.util.Log;

import java.io.File;

import me.xiaopan.sketch.ImageFormat;
import me.xiaopan.sketch.LoadRequest;
import me.xiaopan.sketch.MaxSize;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.UriScheme;
import me.xiaopan.sketch.cache.DiskCache;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 默认的图片解码器
 */
public class DefaultImageDecoder implements ImageDecoder {
    private static final String NAME = "DefaultImageDecoder";

    public static Object decodeFromHelper(LoadRequest loadRequest, DecodeHelper decodeHelper) {
        // just decode bounds
        Options options = new Options();
        options.inJustDecodeBounds = true;
        decodeHelper.decode(options);
        options.inJustDecodeBounds = false;

        // setup best bitmap config by MimeType
        loadRequest.setMimeType(options.outMimeType);
        ImageFormat imageFormat = ImageFormat.valueOfMimeType(options.outMimeType);
        if (imageFormat != null) {
            options.inPreferredConfig = imageFormat.getConfig(loadRequest.getOptions().isLowQualityImage());
        }

        // decode gif image
        if (imageFormat != null && imageFormat == ImageFormat.GIF && loadRequest.getOptions().isDecodeGifImage()) {
            try {
                return decodeHelper.getGifDrawable();
            } catch (UnsatisfiedLinkError e) {
                Log.e(Sketch.TAG, "Didn't find “libpl_droidsonroids_gif.so” file, unable to process the GIF images, has automatically according to the common image decoding, and has set up a closed automatically decoding GIF image feature. If you need to decode the GIF figure please go to “https://github.com/xiaopansky/sketch” to download “libpl_droidsonroids_gif.so” file and put in your project");
                loadRequest.getAttrs().getSketch().getConfiguration().setDecodeGifImage(false);
                e.printStackTrace();
            } catch (ExceptionInInitializerError e) {
                Log.e(Sketch.TAG, "Didn't find “libpl_droidsonroids_gif.so” file, unable to process the GIF images, has automatically according to the common image decoding, and has set up a closed automatically decoding GIF image feature. If you need to decode the GIF figure please go to “https://github.com/xiaopansky/sketch” to download “libpl_droidsonroids_gif.so” file and put in your project");
                loadRequest.getAttrs().getSketch().getConfiguration().setDecodeGifImage(false);
                e.printStackTrace();
            } catch (Throwable e) {
                Log.e(Sketch.TAG, "When decoding GIF figure some unknown exception, has shut down automatically GIF picture decoding function");
                loadRequest.getAttrs().getSketch().getConfiguration().setDecodeGifImage(false);
                e.printStackTrace();
            }
        }

        // decode normal image
        Bitmap bitmap = null;
        Point originalSize = new Point(options.outWidth, options.outHeight);
        if (options.outWidth != 1 && options.outHeight != 1) {
            // calculate inSampleSize
            MaxSize maxSize = loadRequest.getOptions().getMaxSize();
            if (maxSize != null) {
                options.inSampleSize = loadRequest.getAttrs().getSketch().getConfiguration().getImageSizeCalculator().calculateInSampleSize(options.outWidth, options.outHeight, maxSize.getWidth(), maxSize.getHeight());
            }

            // Decoding and exclude the width or height of 1 pixel image
            bitmap = decodeHelper.decode(options);
            if (bitmap != null && (bitmap.getWidth() == 1 || bitmap.getHeight() == 1)) {
                if (Sketch.isDebugMode()) {
                    Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "bitmap width or height is 1px", " - ", "ImageSize: ", originalSize.x, "x", originalSize.y, " - ", "BitmapSize: ", bitmap.getWidth(), "x", bitmap.getHeight(), " - ", loadRequest.getAttrs().getName()));
                }
                bitmap.recycle();
                bitmap = null;
            }
        } else {
            if (Sketch.isDebugMode()) {
                Log.e(Sketch.TAG, SketchUtils.concat(NAME, " - ", "image width or height is 1px", " - ", "ImageSize: ", originalSize.x, "x", originalSize.y, " - ", loadRequest.getAttrs().getName()));
            }
        }

        // Results the callback
        if (bitmap != null && !bitmap.isRecycled()) {
            decodeHelper.onDecodeSuccess(bitmap, originalSize, options.inSampleSize);
        } else {
            bitmap = null;
            decodeHelper.onDecodeFailed();
        }

        return bitmap;
    }

    @Override
    public Object decode(LoadRequest loadRequest) {
        try {
            if (loadRequest.getAttrs().getUriScheme() == UriScheme.HTTP || loadRequest.getAttrs().getUriScheme() == UriScheme.HTTPS) {
                return decodeHttpOrHttps(loadRequest);
            } else if (loadRequest.getAttrs().getUriScheme() == UriScheme.FILE) {
                return decodeFile(loadRequest);
            } else if (loadRequest.getAttrs().getUriScheme() == UriScheme.CONTENT) {
                return decodeContent(loadRequest);
            } else if (loadRequest.getAttrs().getUriScheme() == UriScheme.ASSET) {
                return decodeAsset(loadRequest);
            } else if (loadRequest.getAttrs().getUriScheme() == UriScheme.DRAWABLE) {
                return decodeDrawable(loadRequest);
            } else {
                return null;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getIdentifier() {
        return NAME;
    }

    @Override
    public StringBuilder appendIdentifier(StringBuilder builder) {
        return builder.append(NAME);
    }

    public Object decodeHttpOrHttps(LoadRequest loadRequest) {
        DiskCache.Entry diskCacheEntry = loadRequest.getDiskCacheEntry();
        if (diskCacheEntry != null) {
            return decodeFromHelper(loadRequest, new CacheFileDecodeHelper(diskCacheEntry, loadRequest));
        }

        byte[] imageData = loadRequest.getImageData();
        if (imageData != null && imageData.length > 0) {
            return decodeFromHelper(loadRequest, new ByteArrayDecodeHelper(imageData, loadRequest));
        }

        return null;
    }

    public Object decodeFile(LoadRequest loadRequest) {
        DiskCache.Entry diskCacheEntry = loadRequest.getDiskCacheEntry();
        if (diskCacheEntry != null) {
            return decodeFromHelper(loadRequest, new CacheFileDecodeHelper(diskCacheEntry, loadRequest));
        } else {
            return decodeFromHelper(loadRequest, new FileDecodeHelper(new File(loadRequest.getAttrs().getUri()), loadRequest));
        }
    }

    public Object decodeContent(LoadRequest loadRequest) {
        return decodeFromHelper(loadRequest, new ContentDecodeHelper(Uri.parse(loadRequest.getAttrs().getUri()), loadRequest));
    }

    public Object decodeAsset(LoadRequest loadRequest) {
        return decodeFromHelper(loadRequest, new AssetsDecodeHelper(UriScheme.ASSET.crop(loadRequest.getAttrs().getUri()), loadRequest));
    }

    public Object decodeDrawable(LoadRequest loadRequest) {
        return decodeFromHelper(loadRequest, new DrawableDecodeHelper(Integer.valueOf(UriScheme.DRAWABLE.crop(loadRequest.getAttrs().getUri())), loadRequest));
    }
}