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
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.text.DecimalFormat;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.SketchMonitor;
import me.xiaopan.sketch.cache.BitmapPool;
import me.xiaopan.sketch.cache.DiskCache;
import me.xiaopan.sketch.drawable.SketchGifDrawable;
import me.xiaopan.sketch.feature.ImageSizeCalculator;
import me.xiaopan.sketch.feature.ResizeCalculator;
import me.xiaopan.sketch.request.DataSource;
import me.xiaopan.sketch.request.ImageFrom;
import me.xiaopan.sketch.request.LoadOptions;
import me.xiaopan.sketch.request.LoadRequest;
import me.xiaopan.sketch.request.MaxSize;
import me.xiaopan.sketch.request.Resize;
import me.xiaopan.sketch.request.UriScheme;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 默认的图片解码器
 */
public class DefaultImageDecoder implements ImageDecoder {
    private volatile static long decodeCount;
    private volatile static long useTimeCount;
    private static DecimalFormat decimalFormat;

    protected String logName = "DefaultImageDecoder";

    @Override
    public DecodeResult decode(LoadRequest request) {
        long startTime = 0;
        if (Sketch.isDebugMode()) {
            startTime = System.currentTimeMillis();
        }

        DecodeResult result = null;
        try {
            UriScheme uriScheme = request.getUriScheme();
            if (uriScheme == UriScheme.NET) {
                result = decodeHttpOrHttps(request);
            } else if (uriScheme == UriScheme.FILE) {
                result = decodeFile(request);
            } else if (uriScheme == UriScheme.CONTENT) {
                result = decodeContent(request);
            } else if (uriScheme == UriScheme.ASSET) {
                result = decodeAsset(request);
            } else if (uriScheme == UriScheme.DRAWABLE) {
                result = decodeDrawable(request);
            } else {
                Log.w(Sketch.TAG, SketchUtils.concat(logName, ". unknown uri is ", request.getUri()));
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
                if (decimalFormat == null) {
                    decimalFormat = new DecimalFormat("#.##");
                }
                Log.d(Sketch.TAG, SketchUtils.concat(logName,
                        ". decode use time ", useTime, "ms",
                        ", average ", decimalFormat.format((double) useTimeCount / decodeCount), "ms",
                        ". ", request.getId()));
            }
        }

        return result;
    }

    @Override
    public Options decodeBounds(LoadRequest request) {
        try {
            DecodeHelper decodeHelper = null;
            UriScheme uriScheme = request.getUriScheme();
            if (uriScheme == UriScheme.NET) {
                DiskCache diskCache = request.getSketch().getConfiguration().getDiskCache();
                DiskCache.Entry entry = diskCache.get(request.getDiskCacheKey());
                if (entry != null) {
                    decodeHelper = new CacheFileDecodeHelper(entry, request);
                }
            } else if (uriScheme == UriScheme.FILE) {
                decodeHelper = new FileDecodeHelper(new File(request.getRealUri()), request);
            } else if (uriScheme == UriScheme.CONTENT) {
                decodeHelper = new ContentDecodeHelper(Uri.parse(request.getRealUri()), request);
            } else if (uriScheme == UriScheme.ASSET) {
                decodeHelper = new AssetsDecodeHelper(request.getRealUri(), request);
            } else if (uriScheme == UriScheme.DRAWABLE) {
                decodeHelper = new DrawableDecodeHelper(Integer.valueOf(request.getRealUri()), request);
            } else {
                Log.w(Sketch.TAG, SketchUtils.concat(logName, ". unknown uri is ", request.getUri()));
            }

            if (decodeHelper != null) {
                Options options = new Options();
                options.inJustDecodeBounds = true;
                decodeHelper.decode(options);
                return options;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    private DecodeResult decodeFromHelper(LoadRequest request, DecodeHelper decodeHelper, boolean disableProcess) {
        // Decode bounds and mime info
        Options options = new Options();
        options.inJustDecodeBounds = true;
        decodeHelper.decode(options);
        options.inJustDecodeBounds = false;

        // 过滤掉原图宽高小于等于1的图片
        if (options.outWidth <= 1 || options.outHeight <= 1) {
            if (Sketch.isDebugMode()) {
                Log.e(Sketch.TAG, SketchUtils.concat(logName,
                        ". image width or height less than or equal to 1px",
                        ". imageSize: ", options.outWidth, "x", options.outHeight,
                        ". ", request.getId()));
            }
            decodeHelper.onDecodeError();
            return null;
        }

        final ImageFormat imageFormat = ImageFormat.valueOfMimeType(options.outMimeType);

        // Try decode gif image
        DecodeResult gifResult = gifImage(request, decodeHelper, options.outWidth,
                options.outHeight, options.outMimeType, imageFormat);
        if (gifResult != null) {
            return gifResult;
        }

        // Set whether priority is given to quality or speed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1
                && request.getOptions().isInPreferQualityOverSpeed()) {
            options.inPreferQualityOverSpeed = true;
        }

        // Setup preferred bitmap config
        Bitmap.Config newConfig = request.getOptions().getBitmapConfig();
        if (newConfig == null && imageFormat != null) {
            newConfig = imageFormat.getConfig(request.getOptions().isLowQualityImage());
        }
        if (newConfig != null) {
            options.inPreferredConfig = newConfig;
        }

        // Try decode image by thumbnail mode
        if (!disableProcess) {
            DecodeResult thumbnailModeResult = thumbnailMode(request, decodeHelper, imageFormat, options);
            if (thumbnailModeResult != null) {
                return thumbnailModeResult;
            }
        }

        return normal(request, decodeHelper, imageFormat, options, disableProcess);
    }

    private DecodeResult gifImage(LoadRequest request, DecodeHelper decodeHelper,
                                  int outWidth, int outHeight, String outMimeType,
                                  ImageFormat imageFormat) {
        if (imageFormat == null || imageFormat != ImageFormat.GIF || !request.getOptions().isDecodeGifImage()) {
            return null;
        }

        try {
            BitmapPool bitmapPool = request.getSketch().getConfiguration().getBitmapPool();
            SketchGifDrawable gifDrawable = decodeHelper.getGifDrawable(bitmapPool);
            if (gifDrawable == null) {
                return null;
            }

            gifDrawable.setImageId(request.getId());
            gifDrawable.setImageUri(request.getUri());
            gifDrawable.setOriginWidth(outWidth);
            gifDrawable.setOriginHeight(outHeight);
            gifDrawable.setMimeType(outMimeType);
            return new DecodeResult(outWidth, outHeight, outMimeType, gifDrawable);
        } catch (Throwable e) {
            e.printStackTrace();
            SketchMonitor sketchMonitor = request.getSketch().getConfiguration().getMonitor();
            sketchMonitor.onDecodeGifImageError(e, request, outWidth, outHeight, outMimeType);
            return null;
        }
    }

    private DecodeResult thumbnailMode(LoadRequest request, DecodeHelper decodeHelper, ImageFormat imageFormat, Options options) {
        // 要想使用缩略图功能需要配置开启缩略图功能、配置resize并且图片格式和系统版本支持BitmapRegionDecoder才行
        LoadOptions loadOptions = request.getOptions();
        if (!loadOptions.isThumbnailMode() || loadOptions.getResize() == null
                || !SketchUtils.sdkSupportBitmapRegionDecoder()
                || !SketchUtils.formatSupportBitmapRegionDecoder(imageFormat)) {
            if (loadOptions.isThumbnailMode() && loadOptions.getResize() == null) {
                Log.e(Sketch.TAG, "thumbnailMode need resize ");
            }
            return null;
        }

        // 缩略图模式强制质量优先
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1
                && !options.inPreferQualityOverSpeed) {
            options.inPreferQualityOverSpeed = true;
        }

        // 只有原始图片的宽高比和resize的宽高比相差3倍的时候才能使用略略图方式读取图片
        Resize resize = loadOptions.getResize();
        ImageSizeCalculator sizeCalculator = request.getSketch().getConfiguration().getImageSizeCalculator();
        if (!sizeCalculator.canUseThumbnailMode(options.outWidth, options.outHeight, resize.getWidth(), resize.getHeight())) {
            return null;
        }

        // 计算resize区域在原图中的对应区域
        ResizeCalculator resizeCalculator = request.getSketch().getConfiguration().getResizeCalculator();
        ResizeCalculator.Result result = resizeCalculator.calculator(options.outWidth, options.outHeight, resize.getWidth(), resize.getHeight(), resize.getScaleType(), false);

        boolean supportLargeImage = SketchUtils.supportLargeImage(request, imageFormat);

        // 根据resize的大小和原图中对应区域的大小计算缩小倍数，这样会得到一个较为清晰的缩略图
        options.inSampleSize = sizeCalculator.calculateInSampleSize(result.srcRect.width(), result.srcRect.height(),
                resize.getWidth(), resize.getHeight(), supportLargeImage);

        if (!loadOptions.isDisableBitmapPool() && SketchUtils.sdkSupportInBitmapForRegionDecoder()) {
            BitmapPool bitmapPool = request.getSketch().getConfiguration().getBitmapPool();
            SketchUtils.setInBitmapFromPoolForRegionDecoder(options, result.srcRect, bitmapPool);
        }

        Bitmap bitmap = null;
        try {
            bitmap = decodeHelper.decodeRegion(result.srcRect, options);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();

            // inBitmap不能使用时会抛出IllegalArgumentException，在这里直接关闭不再使用inBitmap功能
            if (!loadOptions.isDisableBitmapPool() && SketchUtils.sdkSupportInBitmapForRegionDecoder()) {
                BitmapPool bitmapPool = request.getSketch().getConfiguration().getBitmapPool();
                SketchMonitor monitor = request.getSketch().getConfiguration().getMonitor();
                SketchUtils.inBitmapThrowForRegionDecoder(e, options, monitor, bitmapPool,
                        request.getUri(), options.outWidth, options.outHeight, result.srcRect);
            }
        }

        // 过滤掉无效的图片
        if (bitmap == null || bitmap.isRecycled()) {
            decodeHelper.onDecodeError();
            return null;
        }

        // 过滤宽高小于等于1的图片
        if (bitmap.getWidth() <= 1 || bitmap.getHeight() <= 1) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(logName,
                        ". image width or height less than or equal to 1px",
                        ". imageSize: ", options.outWidth, "x", options.outHeight,
                        ". bitmapSize: ", bitmap.getWidth(), "x", bitmap.getHeight(),
                        ". ", request.getId()));
            }
            bitmap.recycle();
            decodeHelper.onDecodeError();
            return null;
        }

        // 成功
        decodeHelper.onDecodeSuccess(bitmap, options.outWidth, options.outHeight, options.outMimeType, options.inSampleSize);
        return new DecodeResult(options.outWidth, options.outHeight, options.outMimeType, bitmap).setCanCacheInDiskCache(true);
    }

    private DecodeResult normal(LoadRequest request, DecodeHelper decodeHelper,
                                ImageFormat imageFormat, Options options, boolean disableProcess) {
        // Calculate inSampleSize according to max size
        if (!disableProcess) {
            MaxSize maxSize = request.getOptions().getMaxSize();
            if (maxSize != null) {
                boolean supportLargeImage = SketchUtils.supportLargeImage(request, imageFormat);
                ImageSizeCalculator imageSizeCalculator = request.getSketch().getConfiguration().getImageSizeCalculator();
                options.inSampleSize = imageSizeCalculator.calculateInSampleSize(options.outWidth, options.outHeight,
                        maxSize.getWidth(), maxSize.getHeight(), supportLargeImage);
            }
        }

        // Set inBitmap from bitmap pool
        if (!request.getOptions().isDisableBitmapPool() && SketchUtils.sdkSupportInBitmap()) {
            BitmapPool bitmapPool = request.getSketch().getConfiguration().getBitmapPool();
            SketchUtils.setInBitmapFromPool(options, bitmapPool);
        }

        Bitmap bitmap;
        try {
            bitmap = decodeHelper.decode(options);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            if (!request.getOptions().isDisableBitmapPool() && SketchUtils.sdkSupportInBitmap()) {
                SketchMonitor sketchMonitor = request.getSketch().getConfiguration().getMonitor();
                BitmapPool bitmapPool = request.getSketch().getConfiguration().getBitmapPool();
                SketchUtils.inBitmapThrow(e, options, sketchMonitor, bitmapPool, request.getUri(), options.outWidth, options.outHeight);
            }
            return null;
        } catch (Throwable error) {
            error.printStackTrace();
            SketchMonitor sketchMonitor = request.getSketch().getConfiguration().getMonitor();
            sketchMonitor.onDecodeNormalImageError(error, request, options.outWidth, options.outHeight, options.outMimeType);
            decodeHelper.onDecodeError();
            return null;
        }

        // 过滤掉无效的图片
        if (bitmap == null || bitmap.isRecycled()) {
            decodeHelper.onDecodeError();
            return null;
        }

        // 过滤宽高小于等于1的图片
        if (bitmap.getWidth() <= 1 || bitmap.getHeight() <= 1) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(logName,
                        ". image width or height less than or equal to 1px",
                        ". imageSize: ", options.outWidth, "x", options.outHeight,
                        ". bitmapSize: ", bitmap.getWidth(), "x", bitmap.getHeight(),
                        ". ", request.getId()));
            }
            bitmap.recycle();
            decodeHelper.onDecodeError();
            return null;
        }

        // 成功
        decodeHelper.onDecodeSuccess(bitmap, options.outWidth, options.outHeight, options.outMimeType, options.inSampleSize);

        ImageSizeCalculator sizeCalculator = request.getSketch().getConfiguration().getImageSizeCalculator();
        boolean canUseCacheProcessedImageInDisk = sizeCalculator.canUseCacheProcessedImageInDisk(options.inSampleSize);
        return new DecodeResult(options.outWidth, options.outHeight, options.outMimeType, bitmap)
                .setCanCacheInDiskCache(canUseCacheProcessedImageInDisk);
    }

    private DecodeResult decodeFromDataSource(LoadRequest request, DataSource dataSource) {
        DecodeResult decodeResult = null;

        DiskCache.Entry diskCacheEntry = dataSource.getDiskCacheEntry();
        byte[] imageData = dataSource.getImageData();

        if (diskCacheEntry != null) {
            DecodeHelper decodeHelper = new CacheFileDecodeHelper(diskCacheEntry, request);
            decodeResult = decodeFromHelper(request, decodeHelper, dataSource.isDisableProcess());
        } else if (imageData != null && imageData.length > 0) {
            DecodeHelper decodeHelper = new ByteArrayDecodeHelper(imageData, request);
            decodeResult = decodeFromHelper(request, decodeHelper, dataSource.isDisableProcess());
        }

        if (decodeResult != null) {
            decodeResult.setImageFrom(dataSource.getImageFrom());
        }

        return decodeResult;
    }

    private DecodeResult decodeHttpOrHttps(LoadRequest request) {
        DecodeResult decodeResult = null;

        DataSource dataSource = request.getDataSource();
        if (dataSource != null) {
            decodeResult = decodeFromDataSource(request, dataSource);
        }

        return decodeResult;
    }

    private DecodeResult decodeFile(LoadRequest request) {
        DecodeResult decodeResult;

        DataSource dataSource = request.getDataSource();
        if (dataSource != null) {
            decodeResult = decodeFromDataSource(request, dataSource);
        } else {
            DecodeHelper decodeHelper = new FileDecodeHelper(new File(request.getRealUri()), request);
            decodeResult = decodeFromHelper(request, decodeHelper, false);
            if (decodeResult != null) {
                decodeResult.setImageFrom(ImageFrom.LOCAL);
            }
        }

        return decodeResult;
    }

    private DecodeResult decodeContent(LoadRequest request) {
        DecodeResult decodeResult;

        DataSource dataSource = request.getDataSource();
        if (dataSource != null) {
            decodeResult = decodeFromDataSource(request, dataSource);
        } else {
            DecodeHelper decodeHelper = new ContentDecodeHelper(Uri.parse(request.getRealUri()), request);
            decodeResult = decodeFromHelper(request, decodeHelper, false);
            if (decodeResult != null) {
                decodeResult.setImageFrom(ImageFrom.LOCAL);
            }
        }

        return decodeResult;
    }

    private DecodeResult decodeAsset(LoadRequest request) {
        DecodeResult decodeResult;

        DataSource dataSource = request.getDataSource();
        if (dataSource != null) {
            decodeResult = decodeFromDataSource(request, dataSource);
        } else {
            DecodeHelper decodeHelper = new AssetsDecodeHelper(request.getRealUri(), request);
            decodeResult = decodeFromHelper(request, decodeHelper, false);
            if (decodeResult != null) {
                decodeResult.setImageFrom(ImageFrom.LOCAL);
            }
        }

        return decodeResult;
    }

    private DecodeResult decodeDrawable(LoadRequest request) {
        DecodeResult decodeResult;

        DataSource dataSource = request.getDataSource();
        if (dataSource != null) {
            decodeResult = decodeFromDataSource(request, dataSource);
        } else {
            DecodeHelper decodeHelper = new DrawableDecodeHelper(Integer.valueOf(request.getRealUri()), request);
            decodeResult = decodeFromHelper(request, decodeHelper, false);
            if (decodeResult != null) {
                decodeResult.setImageFrom(ImageFrom.LOCAL);
            }
        }

        return decodeResult;
    }

    @Override
    public String getIdentifier() {
        return logName;
    }

    @Override
    public StringBuilder appendIdentifier(String join, StringBuilder builder) {
        if (!TextUtils.isEmpty(join)) {
            builder.append(join);
        }
        return builder.append(logName);
    }
}