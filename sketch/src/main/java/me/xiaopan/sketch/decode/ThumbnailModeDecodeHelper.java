/*
 * Copyright (C) 2017 Peng fei Pan <sky@xiaopan.me>
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
import android.graphics.BitmapFactory;
import android.os.Build;

import me.xiaopan.sketch.ErrorTracker;
import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.SLogType;
import me.xiaopan.sketch.cache.BitmapPool;
import me.xiaopan.sketch.cache.BitmapPoolUtils;
import me.xiaopan.sketch.drawable.ImageAttrs;
import me.xiaopan.sketch.request.LoadOptions;
import me.xiaopan.sketch.request.LoadRequest;
import me.xiaopan.sketch.request.Resize;
import me.xiaopan.sketch.util.SketchUtils;

public class ThumbnailModeDecodeHelper extends DecodeHelper {
    private static final String LOG_NAME = "ThumbnailModeDecodeHelper";

    /**
     * 要想使用缩略图功能需要配置开启缩略图功能、配置resize并且图片格式和系统版本支持BitmapRegionDecoder才行
     */
    @Override
    public boolean match(LoadRequest request, DataSource dataSource, ImageType imageType, BitmapFactory.Options boundOptions) {
        LoadOptions loadOptions = request.getOptions();
        if (!loadOptions.isThumbnailMode()
                || !SketchUtils.sdkSupportBitmapRegionDecoder()
                || !SketchUtils.formatSupportBitmapRegionDecoder(imageType)) {
            return false;
        }

        Resize resize = loadOptions.getResize();
        if (resize == null) {
            SLog.e(LOG_NAME, "thumbnailMode need resize ");
            return false;
        }

        // 只有原始图片的宽高比和resize的宽高比相差3倍的时候才能使用缩略图方式读取图片
        ImageSizeCalculator sizeCalculator = request.getConfiguration().getImageSizeCalculator();
        return sizeCalculator.canUseThumbnailMode(boundOptions.outWidth, boundOptions.outHeight,
                resize.getWidth(), resize.getHeight());
    }

    @Override
    public DecodeResult decode(LoadRequest request, DataSource dataSource, ImageType imageType, BitmapFactory.Options boundOptions,
                               BitmapFactory.Options decodeOptions, int exifOrientation) throws DecodeException {

        ImageOrientationCorrector orientationCorrector = request.getConfiguration().getImageOrientationCorrector();
        orientationCorrector.rotateSize(boundOptions, exifOrientation);

        // 缩略图模式强制质量优先
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1
                && !decodeOptions.inPreferQualityOverSpeed) {
            decodeOptions.inPreferQualityOverSpeed = true;
        }

        // 计算resize区域在原图中的对应区域
        LoadOptions loadOptions = request.getOptions();
        Resize resize = loadOptions.getResize();
        ResizeCalculator resizeCalculator = request.getConfiguration().getResizeCalculator();
        ResizeCalculator.Mapping mapping = resizeCalculator.calculator(boundOptions.outWidth, boundOptions.outHeight,
                resize.getWidth(), resize.getHeight(), resize.getScaleType(), false);

        boolean supportLargeImage = SketchUtils.supportLargeImage(request, imageType);

        // 根据resize的大小和原图中对应区域的大小计算缩小倍数，这样会得到一个较为清晰的缩略图
        ImageSizeCalculator sizeCalculator = request.getConfiguration().getImageSizeCalculator();
        decodeOptions.inSampleSize = sizeCalculator.calculateInSampleSize(mapping.srcRect.width(), mapping.srcRect.height(),
                resize.getWidth(), resize.getHeight(), supportLargeImage);

        orientationCorrector.reverseRotate(mapping.srcRect, boundOptions.outWidth, boundOptions.outHeight, exifOrientation);

        if (BitmapPoolUtils.sdkSupportInBitmapForRegionDecoder() && !loadOptions.isBitmapPoolDisabled()) {
            BitmapPool bitmapPool = request.getConfiguration().getBitmapPool();
            BitmapPoolUtils.setInBitmapFromPoolForRegionDecoder(decodeOptions, mapping.srcRect, bitmapPool);
        }

        Bitmap bitmap = null;
        try {
            bitmap = ImageDecodeUtils.decodeRegionBitmap(dataSource, mapping.srcRect, decodeOptions);
        } catch (Throwable throwable) {
            throwable.printStackTrace();

            ErrorTracker errorTracker = request.getConfiguration().getErrorTracker();
            BitmapPool bitmapPool = request.getConfiguration().getBitmapPool();
            if (ImageDecodeUtils.isInBitmapDecodeError(throwable, decodeOptions, true)) {
                ImageDecodeUtils.recycleInBitmapOnDecodeError(errorTracker, bitmapPool, request.getUri(),
                        boundOptions.outWidth, boundOptions.outHeight, boundOptions.outMimeType, throwable, decodeOptions, true);

                try {
                    bitmap = ImageDecodeUtils.decodeRegionBitmap(dataSource, mapping.srcRect, decodeOptions);
                } catch (Throwable throwable1) {
                    throwable1.printStackTrace();

                    errorTracker.onDecodeNormalImageError(throwable1, request, boundOptions.outWidth,
                            boundOptions.outHeight, boundOptions.outMimeType);
                }
            } else if (ImageDecodeUtils.isSrcRectDecodeError(throwable, boundOptions.outWidth, boundOptions.outHeight, mapping.srcRect)) {
                errorTracker.onDecodeRegionError(request.getUri(), boundOptions.outWidth, boundOptions.outHeight,
                        boundOptions.outMimeType, throwable, mapping.srcRect, decodeOptions.inSampleSize);
            } else {
                errorTracker.onDecodeNormalImageError(throwable, request, boundOptions.outWidth,
                        boundOptions.outHeight, boundOptions.outMimeType);
            }
        }

        // 过滤掉无效的图片
        if (bitmap == null || bitmap.isRecycled()) {
            ImageDecodeUtils.decodeError(request, dataSource, LOG_NAME);
            return null;
        }

        // 过滤宽高小于等于1的图片
        if (bitmap.getWidth() <= 1 || bitmap.getHeight() <= 1) {
            if (SLogType.REQUEST.isEnabled()) {
                SLog.fw(SLogType.REQUEST, LOG_NAME,
                        "image width or height less than or equal to 1px. imageSize: %dx%d. bitmapSize: %dx%d. %s",
                        boundOptions.outWidth, boundOptions.outHeight, bitmap.getWidth(), bitmap.getHeight(), request.getKey());
            }
            bitmap.recycle();
            ImageDecodeUtils.decodeError(request, dataSource, LOG_NAME);
            return null;
        }

        ImageAttrs imageAttrs = new ImageAttrs(boundOptions.outMimeType, boundOptions.outWidth, boundOptions.outHeight, exifOrientation);
        BitmapDecodeResult result = new BitmapDecodeResult(imageAttrs, bitmap).setProcessed(true);

        correctOrientation(orientationCorrector, result, exifOrientation, request);

        ImageDecodeUtils.decodeSuccess(bitmap, boundOptions.outWidth, boundOptions.outHeight, decodeOptions.inSampleSize, request, LOG_NAME);
        return result;
    }
}
