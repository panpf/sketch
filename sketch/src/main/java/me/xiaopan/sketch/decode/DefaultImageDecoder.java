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

import java.io.File;
import java.text.DecimalFormat;

import me.xiaopan.sketch.LogType;
import me.xiaopan.sketch.SLog;
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
        if (LogType.BASE.isEnabled()) {
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
                SLog.w(LogType.BASE, logName, "unknown uri is %s", request.getUri());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        if (LogType.TIME.isEnabled()) {
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
                SLog.d(LogType.BASE, logName, "decode use time %dms, average %sms. %s",
                        useTime, decimalFormat.format((double) useTimeCount / decodeCount), request.getId());
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
                SLog.w(LogType.BASE, logName, "unknown uri is %s", request.getUri());
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
        Options boundOptions = new Options();
        boundOptions.inJustDecodeBounds = true;
        decodeHelper.decode(boundOptions);

        // 过滤掉原图宽高小于等于1的图片
        if (boundOptions.outWidth <= 1 || boundOptions.outHeight <= 1) {
            if (LogType.BASE.isEnabled()) {
                SLog.e(LogType.BASE, logName, "image width or height less than or equal to 1px. imageSize: %dx%d. %s",
                        boundOptions.outWidth, boundOptions.outHeight, request.getId());
            }
            decodeHelper.onDecodeError();
            return null;
        }

        final ImageFormat imageFormat = ImageFormat.valueOfMimeType(boundOptions.outMimeType);

        // Try decode gif image
        DecodeResult gifResult = gifImage(request, decodeHelper, boundOptions, imageFormat);
        if (gifResult != null) {
            return gifResult;
        }

        Options decodeOptions = new Options();

        // Set whether priority is given to quality or speed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1
                && request.getOptions().isInPreferQualityOverSpeed()) {
            decodeOptions.inPreferQualityOverSpeed = true;
        }

        // Setup preferred bitmap config
        Bitmap.Config newConfig = request.getOptions().getBitmapConfig();
        if (newConfig == null && imageFormat != null) {
            newConfig = imageFormat.getConfig(request.getOptions().isLowQualityImage());
        }
        if (newConfig != null) {
            decodeOptions.inPreferredConfig = newConfig;
        }

        // Try decode image by thumbnail mode
        if (!disableProcess) {
            DecodeResult thumbnailModeResult = thumbnailMode(request, decodeHelper, imageFormat, boundOptions, decodeOptions);
            if (thumbnailModeResult != null) {
                return thumbnailModeResult;
            }
        }

        return normal(request, decodeHelper, imageFormat, boundOptions, decodeOptions, disableProcess);
    }

    private DecodeResult gifImage(LoadRequest request, DecodeHelper decodeHelper, Options boundOptions, ImageFormat imageFormat) {
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
            gifDrawable.setOriginWidth(boundOptions.outWidth);
            gifDrawable.setOriginHeight(boundOptions.outHeight);
            gifDrawable.setMimeType(boundOptions.outMimeType);
            return new DecodeResult(boundOptions.outWidth, boundOptions.outHeight, boundOptions.outMimeType, gifDrawable);
        } catch (Throwable e) {
            e.printStackTrace();
            SketchMonitor sketchMonitor = request.getSketch().getConfiguration().getMonitor();
            sketchMonitor.onDecodeGifImageError(e, request, boundOptions.outWidth, boundOptions.outHeight, boundOptions.outMimeType);
            return null;
        }
    }

    private DecodeResult thumbnailMode(LoadRequest request, DecodeHelper decodeHelper, ImageFormat imageFormat,
                                       Options boundOptions, Options decodeOptions) {
        decodeOptions.outWidth = boundOptions.outWidth;
        decodeOptions.outHeight = boundOptions.outHeight;
        decodeOptions.outMimeType = boundOptions.outMimeType;

        // 要想使用缩略图功能需要配置开启缩略图功能、配置resize并且图片格式和系统版本支持BitmapRegionDecoder才行
        LoadOptions loadOptions = request.getOptions();
        if (!loadOptions.isThumbnailMode() || loadOptions.getResize() == null
                || !SketchUtils.sdkSupportBitmapRegionDecoder()
                || !SketchUtils.formatSupportBitmapRegionDecoder(imageFormat)) {
            if (loadOptions.isThumbnailMode() && loadOptions.getResize() == null) {
                SLog.e(logName, "thumbnailMode need resize ");
            }
            return null;
        }

        // 缩略图模式强制质量优先
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1
                && !decodeOptions.inPreferQualityOverSpeed) {
            decodeOptions.inPreferQualityOverSpeed = true;
        }

        // 只有原始图片的宽高比和resize的宽高比相差3倍的时候才能使用略略图方式读取图片
        Resize resize = loadOptions.getResize();
        ImageSizeCalculator sizeCalculator = request.getSketch().getConfiguration().getImageSizeCalculator();
        if (!sizeCalculator.canUseThumbnailMode(boundOptions.outWidth, boundOptions.outHeight, resize.getWidth(), resize.getHeight())) {
            return null;
        }

        // 计算resize区域在原图中的对应区域
        ResizeCalculator resizeCalculator = request.getSketch().getConfiguration().getResizeCalculator();
        ResizeCalculator.Result result = resizeCalculator.calculator(boundOptions.outWidth, boundOptions.outHeight, resize.getWidth(), resize.getHeight(), resize.getScaleType(), false);

        boolean supportLargeImage = SketchUtils.supportLargeImage(request, imageFormat);

        // 根据resize的大小和原图中对应区域的大小计算缩小倍数，这样会得到一个较为清晰的缩略图
        decodeOptions.inSampleSize = sizeCalculator.calculateInSampleSize(result.srcRect.width(), result.srcRect.height(),
                resize.getWidth(), resize.getHeight(), supportLargeImage);

        if (!loadOptions.isBitmapPoolDisabled() && SketchUtils.sdkSupportInBitmapForRegionDecoder()) {
            BitmapPool bitmapPool = request.getSketch().getConfiguration().getBitmapPool();
            SketchUtils.setInBitmapFromPoolForRegionDecoder(decodeOptions, result.srcRect, bitmapPool);
        }

        Bitmap bitmap = null;
        try {
            bitmap = decodeHelper.decodeRegion(result.srcRect, decodeOptions);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();

            if (SketchUtils.sdkSupportInBitmapForRegionDecoder()) {
                if (!loadOptions.isBitmapPoolDisabled()) {
                    BitmapPool bitmapPool = request.getSketch().getConfiguration().getBitmapPool();
                    SketchMonitor monitor = request.getSketch().getConfiguration().getMonitor();
                    SketchUtils.inBitmapThrowForRegionDecoder(e, decodeOptions, monitor, bitmapPool,
                            request.getUri(), boundOptions.outWidth, boundOptions.outHeight, result.srcRect);
                }

                // 要是因为inBitmap而解码失败就再此尝试
                if (decodeOptions.inBitmap != null) {
                    decodeOptions.inBitmap = null;
                    try {
                        bitmap = decodeHelper.decodeRegion(result.srcRect, decodeOptions);
                    } catch (Throwable error) {
                        error.printStackTrace();
                        SketchMonitor sketchMonitor = request.getSketch().getConfiguration().getMonitor();
                        sketchMonitor.onDecodeNormalImageError(error, request, boundOptions.outWidth, boundOptions.outHeight, boundOptions.outMimeType);
                    }
                }
            }
        } catch (Throwable error) {
            error.printStackTrace();
            SketchMonitor sketchMonitor = request.getSketch().getConfiguration().getMonitor();
            sketchMonitor.onDecodeNormalImageError(error, request, boundOptions.outWidth, boundOptions.outHeight, boundOptions.outMimeType);
        }

        // 过滤掉无效的图片
        if (bitmap == null || bitmap.isRecycled()) {
            decodeHelper.onDecodeError();
            return null;
        }

        // 过滤宽高小于等于1的图片
        if (bitmap.getWidth() <= 1 || bitmap.getHeight() <= 1) {
            if (LogType.BASE.isEnabled()) {
                SLog.w(LogType.BASE, logName, "image width or height less than or equal to 1px. imageSize: %dx%d. bitmapSize: %dx%d. %s",
                        boundOptions.outWidth, boundOptions.outHeight, bitmap.getWidth(), bitmap.getHeight(), request.getId());
            }
            bitmap.recycle();
            decodeHelper.onDecodeError();
            return null;
        }

        // 成功
        decodeHelper.onDecodeSuccess(bitmap, boundOptions.outWidth, boundOptions.outHeight, boundOptions.outMimeType, decodeOptions.inSampleSize);
        return new DecodeResult(boundOptions.outWidth, boundOptions.outHeight, boundOptions.outMimeType, bitmap).setCanCacheInDiskCache(true);
    }

    private DecodeResult normal(LoadRequest request, DecodeHelper decodeHelper, ImageFormat imageFormat,
                                Options boundOptions, Options decodeOptions, boolean processDisabled) {
        decodeOptions.outWidth = boundOptions.outWidth;
        decodeOptions.outHeight = boundOptions.outHeight;
        decodeOptions.outMimeType = boundOptions.outMimeType;

        // Calculate inSampleSize according to max size
        if (!processDisabled) {
            MaxSize maxSize = request.getOptions().getMaxSize();
            if (maxSize != null) {
                boolean supportLargeImage = SketchUtils.supportLargeImage(request, imageFormat);
                ImageSizeCalculator imageSizeCalculator = request.getSketch().getConfiguration().getImageSizeCalculator();
                decodeOptions.inSampleSize = imageSizeCalculator.calculateInSampleSize(boundOptions.outWidth, boundOptions.outHeight,
                        maxSize.getWidth(), maxSize.getHeight(), supportLargeImage);
            }
        }

        // Set inBitmap from bitmap pool
        if (!request.getOptions().isBitmapPoolDisabled() && SketchUtils.sdkSupportInBitmap()) {
            BitmapPool bitmapPool = request.getSketch().getConfiguration().getBitmapPool();
            SketchUtils.setInBitmapFromPool(decodeOptions, bitmapPool);
        }

        Bitmap bitmap = null;
        try {
            bitmap = decodeHelper.decode(decodeOptions);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();

            if (SketchUtils.sdkSupportInBitmap()) {
                if (!request.getOptions().isBitmapPoolDisabled()) {
                    SketchMonitor sketchMonitor = request.getSketch().getConfiguration().getMonitor();
                    BitmapPool bitmapPool = request.getSketch().getConfiguration().getBitmapPool();
                    SketchUtils.inBitmapThrow(e, decodeOptions, sketchMonitor, bitmapPool, request.getUri(), boundOptions.outWidth, boundOptions.outHeight);
                }

                // 要是因为inBitmap而解码失败就再此尝试
                if (decodeOptions.inBitmap != null) {
                    decodeOptions.inBitmap = null;
                    try {
                        bitmap = decodeHelper.decode(decodeOptions);
                    } catch (Throwable error) {
                        error.printStackTrace();
                        SketchMonitor sketchMonitor = request.getSketch().getConfiguration().getMonitor();
                        sketchMonitor.onDecodeNormalImageError(error, request, boundOptions.outWidth, boundOptions.outHeight, boundOptions.outMimeType);
                    }
                }
            }
        } catch (Throwable error) {
            error.printStackTrace();
            SketchMonitor sketchMonitor = request.getSketch().getConfiguration().getMonitor();
            sketchMonitor.onDecodeNormalImageError(error, request, boundOptions.outWidth, boundOptions.outHeight, boundOptions.outMimeType);
        }

        // 过滤掉无效的图片
        if (bitmap == null || bitmap.isRecycled()) {
            decodeHelper.onDecodeError();
            return null;
        }

        // 过滤宽高小于等于1的图片
        if (bitmap.getWidth() <= 1 || bitmap.getHeight() <= 1) {
            if (LogType.BASE.isEnabled()) {
                SLog.w(LogType.BASE, logName, "image width or height less than or equal to 1px. imageSize: %dx%d. bitmapSize: %dx%d. %s",
                        boundOptions.outWidth, boundOptions.outHeight, bitmap.getWidth(), bitmap.getHeight(), request.getId());
            }
            bitmap.recycle();
            decodeHelper.onDecodeError();
            return null;
        }

        // 成功
        decodeHelper.onDecodeSuccess(bitmap, boundOptions.outWidth, boundOptions.outHeight, boundOptions.outMimeType, decodeOptions.inSampleSize);

        ImageSizeCalculator sizeCalculator = request.getSketch().getConfiguration().getImageSizeCalculator();
        boolean canUseCacheProcessedImageInDisk = sizeCalculator.canUseCacheProcessedImageInDisk(decodeOptions.inSampleSize);
        return new DecodeResult(boundOptions.outWidth, boundOptions.outHeight, boundOptions.outMimeType, bitmap)
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