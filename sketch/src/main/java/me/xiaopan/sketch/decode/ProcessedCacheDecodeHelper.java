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
import android.text.TextUtils;

import me.xiaopan.sketch.ErrorTracker;
import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.SLogType;
import me.xiaopan.sketch.cache.BitmapPool;
import me.xiaopan.sketch.cache.BitmapPoolUtils;
import me.xiaopan.sketch.drawable.ImageAttrs;
import me.xiaopan.sketch.request.LoadRequest;
import me.xiaopan.sketch.util.ExifInterface;

/**
 * 解码经过处理的缓存图片时只需原封不动读取，然后读取原图的类型、宽高信息即可
 */
public class ProcessedCacheDecodeHelper extends DecodeHelper {
    private static final String LOG_NAME = "ProcessedCacheDecodeHelper";

    @Override
    public boolean match(LoadRequest request, DataSource dataSource, ImageType imageType, BitmapFactory.Options boundOptions) {
        return dataSource instanceof ProcessedCacheDataSource;
    }

    @Override
    public DecodeResult decode(LoadRequest request, DataSource dataSource, ImageType imageType,
                               BitmapFactory.Options boundOptions, BitmapFactory.Options decodeOptions, int exifOrientation) {
        decodeOptions.inSampleSize = 1;

        // Set inBitmap from bitmap pool
        if (BitmapPoolUtils.sdkSupportInBitmap() && !request.getOptions().isBitmapPoolDisabled()) {
            BitmapPool bitmapPool = request.getConfiguration().getBitmapPool();
            BitmapPoolUtils.setInBitmapFromPool(decodeOptions,
                    boundOptions.outWidth, boundOptions.outHeight, boundOptions.outMimeType, bitmapPool);
        }

        Bitmap bitmap = null;
        try {
            bitmap = ImageDecodeUtils.decodeBitmap(dataSource, decodeOptions);
        } catch (Throwable throwable) {
            throwable.printStackTrace();

            ErrorTracker errorTracker = request.getConfiguration().getErrorTracker();
            BitmapPool bitmapPool = request.getConfiguration().getBitmapPool();
            if (ImageDecodeUtils.isInBitmapDecodeError(throwable, decodeOptions, false)) {
                ImageDecodeUtils.recycleInBitmapOnDecodeError(errorTracker, bitmapPool, request.getUri(),
                        boundOptions.outWidth, boundOptions.outHeight, boundOptions.outMimeType, throwable, decodeOptions, false);

                try {
                    bitmap = ImageDecodeUtils.decodeBitmap(dataSource, decodeOptions);
                } catch (Throwable throwable1) {
                    throwable1.printStackTrace();

                    errorTracker.onDecodeNormalImageError(throwable1, request,
                            boundOptions.outWidth, boundOptions.outHeight, boundOptions.outMimeType);
                }
            } else {
                errorTracker.onDecodeNormalImageError(throwable, request,
                        boundOptions.outWidth, boundOptions.outHeight, boundOptions.outMimeType);
            }
        }

        // 过滤掉无效的图片
        if (bitmap == null || bitmap.isRecycled()) {
            ImageDecodeUtils.decodeError(request, dataSource, LOG_NAME);
            return null;
        }

        // 过滤宽高小于等于1的图片
        if (bitmap.getWidth() <= 1 || bitmap.getHeight() <= 1) {
            SLog.fw(SLogType.REQUEST, LOG_NAME,
                    "image width or height less than or equal to 1px. imageSize: %dx%d. bitmapSize: %dx%d. %s",
                    boundOptions.outWidth, boundOptions.outHeight, bitmap.getWidth(), bitmap.getHeight(), request.getKey());
            bitmap.recycle();
            ImageDecodeUtils.decodeError(request, dataSource, LOG_NAME);
            return null;
        }

        // 由于是读取的经过处理的缓存图片，因此要重新读取原图的类型、宽高信息
        DataSource originFileDataSource = null;
        try {
            originFileDataSource = DataSourceFactory.makeDataSource(request.getContext(), request.getUriInfo(), request.getDownloadResult());
        } catch (DecodeException e) {
            e.printStackTrace();
        }
        BitmapFactory.Options originImageOptions = null;
        if (originFileDataSource != null) {
            originImageOptions = new BitmapFactory.Options();
            originImageOptions.inJustDecodeBounds = true;
            try {
                ImageDecodeUtils.decodeBitmap(originFileDataSource, originImageOptions);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        ImageOrientationCorrector orientationCorrector = request.getConfiguration().getImageOrientationCorrector();

        ImageAttrs imageAttrs;
        if (originImageOptions != null && !TextUtils.isEmpty(originImageOptions.outMimeType)) {
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

        ImageDecodeUtils.decodeSuccess(bitmap, boundOptions.outWidth, boundOptions.outHeight, decodeOptions.inSampleSize, request, LOG_NAME);
        return new BitmapDecodeResult(imageAttrs, bitmap).setBanProcess(true);
    }
}
