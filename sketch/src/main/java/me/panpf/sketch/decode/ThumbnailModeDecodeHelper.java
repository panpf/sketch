/*
 * Copyright (C) 2017 Peng fei Pan <sky@panpf.me>
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

package me.panpf.sketch.decode;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import me.panpf.sketch.ErrorTracker;
import me.panpf.sketch.SLog;
import me.panpf.sketch.cache.BitmapPool;
import me.panpf.sketch.cache.BitmapPoolUtils;
import me.panpf.sketch.datasource.DataSource;
import me.panpf.sketch.request.ErrorCause;
import me.panpf.sketch.request.LoadOptions;
import me.panpf.sketch.request.LoadRequest;
import me.panpf.sketch.request.Resize;
import me.panpf.sketch.util.SketchUtils;

/**
 * 缩略图模式解码协助器，当开启缩略图模式并且满足使用缩略图模式的条件时会使用此协助器来解码
 * <p>
 * 解码时会根据 resize 的尺寸并使用 {@link BitmapRegionDecoder} 读取原图中的部分区域来得到更清晰的缩略图
 */
public class ThumbnailModeDecodeHelper extends DecodeHelper {
    private static final String NAME = "ThumbnailModeDecodeHelper";

    /**
     * 要想使用缩略图功能需要配置开启缩略图功能、配置resize并且图片格式和系统版本支持BitmapRegionDecoder才行
     */
    @Override
    public boolean match(@NonNull LoadRequest request, @NonNull DataSource dataSource, @Nullable ImageType imageType, @NonNull BitmapFactory.Options boundOptions) {
        LoadOptions loadOptions = request.getOptions();
        if (!loadOptions.isThumbnailMode() || !SketchUtils.formatSupportBitmapRegionDecoder(imageType)) {
            return false;
        }

        Resize resize = loadOptions.getResize();
        if (resize == null) {
            SLog.e(NAME, "thumbnailMode need resize ");
            return false;
        }

        // 只有原始图片的宽高比和resize的宽高比相差3倍的时候才能使用缩略图方式读取图片
        ImageSizeCalculator sizeCalculator = request.getConfiguration().getSizeCalculator();
        return sizeCalculator.canUseThumbnailMode(boundOptions.outWidth, boundOptions.outHeight,
                resize.getWidth(), resize.getHeight());
    }

    @NonNull
    @Override
    public DecodeResult decode(@NonNull LoadRequest request, @NonNull DataSource dataSource, @Nullable ImageType imageType, @NonNull BitmapFactory.Options boundOptions,
                               @NonNull BitmapFactory.Options decodeOptions, int exifOrientation) throws DecodeException {

        ImageOrientationCorrector orientationCorrector = request.getConfiguration().getOrientationCorrector();
        orientationCorrector.rotateSize(boundOptions, exifOrientation);

        // 缩略图模式强制质量优先
        if (!decodeOptions.inPreferQualityOverSpeed) {
            decodeOptions.inPreferQualityOverSpeed = true;
        }

        // 计算resize区域在原图中的对应区域
        LoadOptions loadOptions = request.getOptions();
        Resize resize = loadOptions.getResize();
        ResizeCalculator resizeCalculator = request.getConfiguration().getResizeCalculator();
        ResizeCalculator.Mapping mapping = resizeCalculator.calculator(boundOptions.outWidth, boundOptions.outHeight,
                resize.getWidth(), resize.getHeight(), resize.getScaleType(), false);


        // 根据resize的大小和原图中对应区域的大小计算缩小倍数，这样会得到一个较为清晰的缩略图
        ImageSizeCalculator sizeCalculator = request.getConfiguration().getSizeCalculator();
        boolean smallerThumbnail = sizeCalculator.canUseSmallerThumbnails(request, imageType);
        decodeOptions.inSampleSize = sizeCalculator.calculateInSampleSize(mapping.srcRect.width(), mapping.srcRect.height(),
                resize.getWidth(), resize.getHeight(), smallerThumbnail);

        orientationCorrector.reverseRotate(mapping.srcRect, boundOptions.outWidth, boundOptions.outHeight, exifOrientation);

        if (BitmapPoolUtils.sdkSupportInBitmapForRegionDecoder() && !loadOptions.isBitmapPoolDisabled()) {
            BitmapPool bitmapPool = request.getConfiguration().getBitmapPool();
            BitmapPoolUtils.setInBitmapFromPoolForRegionDecoder(decodeOptions, mapping.srcRect, bitmapPool);
        }

        Bitmap bitmap;
        try {
            bitmap = ImageDecodeUtils.decodeRegionBitmap(dataSource, mapping.srcRect, decodeOptions);
        } catch (Throwable tr) {
            ErrorTracker errorTracker = request.getConfiguration().getErrorTracker();
            BitmapPool bitmapPool = request.getConfiguration().getBitmapPool();
            if (ImageDecodeUtils.isInBitmapDecodeError(tr, decodeOptions, true)) {
                ImageDecodeUtils.recycleInBitmapOnDecodeError(errorTracker, bitmapPool, request.getUri(),
                        boundOptions.outWidth, boundOptions.outHeight, boundOptions.outMimeType, tr, decodeOptions, true);

                try {
                    bitmap = ImageDecodeUtils.decodeRegionBitmap(dataSource, mapping.srcRect, decodeOptions);
                } catch (Throwable throwable1) {
                    errorTracker.onDecodeNormalImageError(throwable1, request, boundOptions.outWidth,
                            boundOptions.outHeight, boundOptions.outMimeType);
                    throw new DecodeException("InBitmap retry", tr, ErrorCause.DECODE_UNKNOWN_EXCEPTION);
                }
            } else if (ImageDecodeUtils.isSrcRectDecodeError(tr, boundOptions.outWidth, boundOptions.outHeight, mapping.srcRect)) {
                errorTracker.onDecodeRegionError(request.getUri(), boundOptions.outWidth, boundOptions.outHeight,
                        boundOptions.outMimeType, tr, mapping.srcRect, decodeOptions.inSampleSize);
                throw new DecodeException("Because srcRect", tr, ErrorCause.DECODE_UNKNOWN_EXCEPTION);
            } else {
                errorTracker.onDecodeNormalImageError(tr, request, boundOptions.outWidth,
                        boundOptions.outHeight, boundOptions.outMimeType);
                throw new DecodeException(tr, ErrorCause.DECODE_UNKNOWN_EXCEPTION);
            }
        }

        // 过滤掉无效的图片
        if (bitmap == null || bitmap.isRecycled()) {
            ImageDecodeUtils.decodeError(request, dataSource, NAME, "Bitmap invalid", null);
            throw new DecodeException("Bitmap invalid", ErrorCause.DECODE_RESULT_BITMAP_INVALID);
        }

        // 过滤宽高小于等于1的图片
        if (bitmap.getWidth() <= 1 || bitmap.getHeight() <= 1) {
            String cause = String.format(Locale.US, "Bitmap width or height less than or equal to 1px. imageSize: %dx%d. bitmapSize: %dx%d",
                    boundOptions.outWidth, boundOptions.outHeight, bitmap.getWidth(), bitmap.getHeight());
            ImageDecodeUtils.decodeError(request, dataSource, NAME, cause, null);
            bitmap.recycle();
            throw new DecodeException(cause, ErrorCause.DECODE_RESULT_BITMAP_SIZE_INVALID);
        }

        ImageAttrs imageAttrs = new ImageAttrs(boundOptions.outMimeType, boundOptions.outWidth, boundOptions.outHeight, exifOrientation);
        BitmapDecodeResult result = new BitmapDecodeResult(imageAttrs, bitmap).setProcessed(true);

        try {
            correctOrientation(orientationCorrector, result, exifOrientation, request);
        } catch (CorrectOrientationException e) {
            throw new DecodeException(e, ErrorCause.DECODE_CORRECT_ORIENTATION_FAIL);
        }

        ImageDecodeUtils.decodeSuccess(bitmap, boundOptions.outWidth, boundOptions.outHeight, decodeOptions.inSampleSize, request, NAME);
        return result;
    }
}
