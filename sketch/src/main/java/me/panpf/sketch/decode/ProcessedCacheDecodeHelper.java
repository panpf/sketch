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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import java.util.Locale;

import me.panpf.sketch.ErrorTracker;
import me.panpf.sketch.cache.BitmapPool;
import me.panpf.sketch.cache.BitmapPoolUtils;
import me.panpf.sketch.datasource.DataSource;
import me.panpf.sketch.datasource.DiskCacheDataSource;
import me.panpf.sketch.request.ErrorCause;
import me.panpf.sketch.request.LoadRequest;
import me.panpf.sketch.uri.GetDataSourceException;
import me.panpf.sketch.util.ExifInterface;

/**
 * 解码经过处理的缓存图片时只需原封不动读取，然后读取原图的类型、宽高信息即可
 */
public class ProcessedCacheDecodeHelper extends DecodeHelper {
    private static final String NAME = "ProcessedCacheDecodeHelper";

    @Override
    public boolean match(@NonNull LoadRequest request, @NonNull DataSource dataSource, @Nullable ImageType imageType, @NonNull BitmapFactory.Options boundOptions) {
        return dataSource instanceof DiskCacheDataSource && ((DiskCacheDataSource) dataSource).isFromProcessedCache();
    }

    @NonNull
    @Override
    public DecodeResult decode(@NonNull LoadRequest request, @NonNull DataSource dataSource, @Nullable ImageType imageType,
                               @NonNull BitmapFactory.Options boundOptions, @NonNull BitmapFactory.Options decodeOptions, int exifOrientation) throws DecodeException {
        decodeOptions.inSampleSize = 1;

        // Set inBitmap from bitmap pool
        if (!request.getOptions().isBitmapPoolDisabled()) {
            BitmapPool bitmapPool = request.getConfiguration().getBitmapPool();
            BitmapPoolUtils.setInBitmapFromPool(decodeOptions,
                    boundOptions.outWidth, boundOptions.outHeight, boundOptions.outMimeType, bitmapPool);
        }

        Bitmap bitmap;
        try {
            bitmap = ImageDecodeUtils.decodeBitmap(dataSource, decodeOptions);
        } catch (Throwable tr) {
            ErrorTracker errorTracker = request.getConfiguration().getErrorTracker();
            BitmapPool bitmapPool = request.getConfiguration().getBitmapPool();
            if (ImageDecodeUtils.isInBitmapDecodeError(tr, decodeOptions, false)) {
                ImageDecodeUtils.recycleInBitmapOnDecodeError(errorTracker, bitmapPool, request.getUri(),
                        boundOptions.outWidth, boundOptions.outHeight, boundOptions.outMimeType, tr, decodeOptions, false);

                try {
                    bitmap = ImageDecodeUtils.decodeBitmap(dataSource, decodeOptions);
                } catch (Throwable throwable1) {
                    errorTracker.onDecodeNormalImageError(throwable1, request, boundOptions.outWidth, boundOptions.outHeight, boundOptions.outMimeType);
                    throw new DecodeException("InBitmap retry", tr, ErrorCause.DECODE_UNKNOWN_EXCEPTION);
                }
            } else {
                errorTracker.onDecodeNormalImageError(tr, request, boundOptions.outWidth, boundOptions.outHeight, boundOptions.outMimeType);
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

        // 由于是读取的经过处理的缓存图片，因此要重新读取原图的类型、宽高信息
        DataSource originFileDataSource;
        try {
            originFileDataSource = request.getDataSource();
        } catch (GetDataSourceException e) {
            ImageDecodeUtils.decodeError(request, null, NAME, "Unable create DataSource", e);
            throw new DecodeException(e, ErrorCause.DECODE_UNABLE_CREATE_DATA_SOURCE);
        }

        BitmapFactory.Options originImageOptions = new BitmapFactory.Options();
        originImageOptions.inJustDecodeBounds = true;
        try {
            ImageDecodeUtils.decodeBitmap(originFileDataSource, originImageOptions);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        ImageOrientationCorrector orientationCorrector = request.getConfiguration().getOrientationCorrector();

        ImageAttrs imageAttrs;
        if (!TextUtils.isEmpty(originImageOptions.outMimeType)) {
            // Read image orientation
            int realExifOrientation = ExifInterface.ORIENTATION_UNDEFINED;
            if (!request.getOptions().isCorrectImageOrientationDisabled()) {
                realExifOrientation = orientationCorrector.readExifOrientation(originImageOptions.outMimeType, originFileDataSource);
            }

            imageAttrs = new ImageAttrs(originImageOptions.outMimeType, originImageOptions.outWidth, originImageOptions.outHeight, realExifOrientation);
        } else {
            imageAttrs = new ImageAttrs(boundOptions.outMimeType, boundOptions.outWidth, boundOptions.outHeight, exifOrientation);
        }

        orientationCorrector.rotateSize(imageAttrs, imageAttrs.getExifOrientation());

        ImageDecodeUtils.decodeSuccess(bitmap, boundOptions.outWidth, boundOptions.outHeight, decodeOptions.inSampleSize, request, NAME);
        return new BitmapDecodeResult(imageAttrs, bitmap).setBanProcess(true);
    }
}
