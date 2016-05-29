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
import android.os.Build;
import android.util.Log;

import java.io.File;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.cache.DiskCache;
import me.xiaopan.sketch.request.ImageFrom;
import me.xiaopan.sketch.request.LoadRequest;
import me.xiaopan.sketch.request.MaxSize;
import me.xiaopan.sketch.request.UriScheme;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 默认的图片解码器
 */
public class DefaultImageDecoder implements ImageDecoder {
    private volatile static long decodeCount;
    private volatile static long useTimeCount;
    protected String logName = "DefaultImageDecoder";

    public static DecodeResult decodeFromHelper(LoadRequest loadRequest, DecodeHelper decodeHelper, String logName) {
        // just decode bounds
        Options options = new Options();

        // 设置优先考虑质量还是速度
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1
                && loadRequest.getOptions().isInPreferQualityOverSpeed()) {
            options.inPreferQualityOverSpeed = true;
        }

        // 读取图片的宽高以及格式信息
        options.inJustDecodeBounds = true;
        decodeHelper.decode(options);
        options.inJustDecodeBounds = false;

        String mimeType = options.outMimeType;
        ImageFormat imageFormat = ImageFormat.valueOfMimeType(mimeType);

        // setup bitmap config
        if (loadRequest.getOptions().getBitmapConfig() != null) {
            // by user
            options.inPreferredConfig = loadRequest.getOptions().getBitmapConfig();
        } else if (imageFormat != null) {
            // best bitmap config by MimeType
            options.inPreferredConfig = imageFormat.getConfig(loadRequest.getOptions().isLowQualityImage());
        }

        // decode gif image
        if (imageFormat != null && imageFormat == ImageFormat.GIF && loadRequest.getOptions().isDecodeGifImage()) {
            try {
                return new DecodeResult(mimeType, decodeHelper.getGifDrawable());
            } catch (UnsatisfiedLinkError e) {
                Log.e(Sketch.TAG, "Didn't find “libpl_droidsonroids_gif.so” file, unable to process the GIF images, has automatically according to the common image decoding, and has set up a closed automatically decoding GIF image feature. If you need to decode the GIF figure please go to “https://github.com/xiaopansky/sketch” to download “libpl_droidsonroids_gif.so” file and put in your project");
                e.printStackTrace();
            } catch (ExceptionInInitializerError e) {
                Log.e(Sketch.TAG, "Didn't find “libpl_droidsonroids_gif.so” file, unable to process the GIF images, has automatically according to the common image decoding, and has set up a closed automatically decoding GIF image feature. If you need to decode the GIF figure please go to “https://github.com/xiaopansky/sketch” to download “libpl_droidsonroids_gif.so” file and put in your project");
                e.printStackTrace();
            } catch (Throwable e) {
                Log.e(Sketch.TAG, "When decoding GIF figure some unknown exception, has shut down automatically GIF picture decoding function");
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
                options.inSampleSize = loadRequest.getSketch().getConfiguration().getImageSizeCalculator().calculateInSampleSize(options.outWidth, options.outHeight, maxSize.getWidth(), maxSize.getHeight());
            }

            // Decoding and exclude the width or height of 1 pixel image
            bitmap = decodeHelper.decode(options);
            if (bitmap != null && (bitmap.getWidth() == 1 || bitmap.getHeight() == 1)) {
                if (Sketch.isDebugMode()) {
                    Log.w(Sketch.TAG, SketchUtils.concat(logName, " - ", "bitmap width or height is 1px", " - ", "ImageSize: ", originalSize.x, "x", originalSize.y, " - ", "BitmapSize: ", bitmap.getWidth(), "x", bitmap.getHeight(), " - ", loadRequest.getRequestAttrs().getName()));
                }
                bitmap.recycle();
                bitmap = null;
            }
        } else {
            if (Sketch.isDebugMode()) {
                Log.e(Sketch.TAG, SketchUtils.concat(logName, " - ", "image width or height is 1px", " - ", "ImageSize: ", originalSize.x, "x", originalSize.y, " - ", loadRequest.getRequestAttrs().getName()));
            }
        }

        // Results the callback
        if (bitmap != null && !bitmap.isRecycled()) {
            decodeHelper.onDecodeSuccess(bitmap, originalSize, options.inSampleSize);
        } else {
            bitmap = null;
            decodeHelper.onDecodeFailed();
        }

        return bitmap != null ? new DecodeResult(mimeType, bitmap) : null;
    }

    @Override
    public DecodeResult decode(LoadRequest loadRequest) {
        long startTime = 0;
        if (Sketch.isDebugMode()) {
            startTime = System.currentTimeMillis();
        }

        DecodeResult result = null;
        try {
            if (loadRequest.getRequestAttrs().getUriScheme() == UriScheme.NET) {
                result = decodeHttpOrHttps(loadRequest);
            } else if (loadRequest.getRequestAttrs().getUriScheme() == UriScheme.FILE) {
                result = decodeFile(loadRequest);
            } else if (loadRequest.getRequestAttrs().getUriScheme() == UriScheme.CONTENT) {
                result = decodeContent(loadRequest);
            } else if (loadRequest.getRequestAttrs().getUriScheme() == UriScheme.ASSET) {
                result = decodeAsset(loadRequest);
            } else if (loadRequest.getRequestAttrs().getUriScheme() == UriScheme.DRAWABLE) {
                result = decodeDrawable(loadRequest);
            } else {
                Log.w(Sketch.TAG, SketchUtils.concat(logName, " - ", "unknown uri is ", loadRequest.getRequestAttrs().getUri()));
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        if (Sketch.isDebugMode()) {
            long useTime = System.currentTimeMillis() - startTime;
            synchronized (DefaultImageDecoder.this) {
                if ((Long.MAX_VALUE - decodeCount) < 1 || (Long.MAX_VALUE - useTimeCount) < useTime) {
                    decodeCount = 0;
                    useTimeCount = 0;
                }
                decodeCount++;
                useTimeCount += useTime;
                Log.d(Sketch.TAG, SketchUtils.concat(logName, " - ", "UseTime: ", useTime, "ms", ", ", "average: ", (double) useTimeCount / decodeCount, "ms", " - ", loadRequest.getRequestAttrs().getId()));
            }
        }

        return result;
    }

    @Override
    public String getIdentifier() {
        return logName;
    }

    @Override
    public StringBuilder appendIdentifier(StringBuilder builder) {
        return builder.append(logName);
    }

    public DecodeResult decodeHttpOrHttps(LoadRequest loadRequest) {
        DecodeResult decodeResult = null;

        if (loadRequest.getDownloadResult() != null) {
            DiskCache.Entry diskCacheEntry = loadRequest.getDownloadResult().getDiskCacheEntry();
            if (diskCacheEntry != null) {
                decodeResult = decodeFromHelper(loadRequest, new CacheFileDecodeHelper(diskCacheEntry, loadRequest), logName);
            }

            byte[] imageData = loadRequest.getDownloadResult().getImageData();
            if (imageData != null && imageData.length > 0) {
                decodeResult = decodeFromHelper(loadRequest, new ByteArrayDecodeHelper(imageData, loadRequest), logName);
            }

            if (decodeResult != null) {
                decodeResult.setImageFrom(loadRequest.getDownloadResult().isFromNetwork() ? ImageFrom.NETWORK : ImageFrom.DISK_CACHE);
            }
        }

        return decodeResult;
    }

    public DecodeResult decodeFile(LoadRequest loadRequest) {
        DecodeResult decodeResult;

        DiskCache.Entry diskCacheEntry = loadRequest.getDownloadResult() != null ? loadRequest.getDownloadResult().getDiskCacheEntry() : null;
        if (diskCacheEntry != null) {
            decodeResult = decodeFromHelper(loadRequest, new CacheFileDecodeHelper(diskCacheEntry, loadRequest), logName);
            if (decodeResult != null) {
                decodeResult.setImageFrom(ImageFrom.DISK_CACHE);
            }
        } else {
            decodeResult = decodeFromHelper(loadRequest, new FileDecodeHelper(new File(loadRequest.getRequestAttrs().getRealUri()), loadRequest), logName);
            if (decodeResult != null) {
                decodeResult.setImageFrom(ImageFrom.LOCAL);
            }
        }

        return decodeResult;
    }

    public DecodeResult decodeContent(LoadRequest loadRequest) {
        DecodeResult decodeResult = decodeFromHelper(loadRequest, new ContentDecodeHelper(Uri.parse(loadRequest.getRequestAttrs().getRealUri()), loadRequest), logName);
        if (decodeResult != null) {
            decodeResult.setImageFrom(ImageFrom.LOCAL);
        }
        return decodeResult;
    }

    public DecodeResult decodeAsset(LoadRequest loadRequest) {
        DecodeResult decodeResult = decodeFromHelper(loadRequest, new AssetsDecodeHelper(loadRequest.getRequestAttrs().getRealUri(), loadRequest), logName);
        if (decodeResult != null) {
            decodeResult.setImageFrom(ImageFrom.LOCAL);
        }
        return decodeResult;
    }

    public DecodeResult decodeDrawable(LoadRequest loadRequest) {
        DecodeResult decodeResult = decodeFromHelper(loadRequest, new DrawableDecodeHelper(Integer.valueOf(loadRequest.getRequestAttrs().getRealUri()), loadRequest), logName);
        if (decodeResult != null) {
            decodeResult.setImageFrom(ImageFrom.LOCAL);
        }
        return decodeResult;
    }
}