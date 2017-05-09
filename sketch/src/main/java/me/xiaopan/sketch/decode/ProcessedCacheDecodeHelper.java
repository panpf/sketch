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

import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.SLogType;
import me.xiaopan.sketch.SketchMonitor;
import me.xiaopan.sketch.cache.BitmapPool;
import me.xiaopan.sketch.cache.BitmapPoolUtils;
import me.xiaopan.sketch.drawable.ImageAttrs;
import me.xiaopan.sketch.feature.ImageOrientationCorrector;
import me.xiaopan.sketch.request.LoadRequest;

/**
 * 解码经过处理的缓存图片时只需原封不动读取，然后读取原图的类型、宽高信息即可
 */
public class ProcessedCacheDecodeHelper implements DecodeHelper {
    private static final String LOG_NAME = "ProcessedCacheDecodeHelper";

    @Override
    public boolean match(LoadRequest request, DataSource dataSource, ImageType imageType, BitmapFactory.Options boundOptions) {
        return dataSource instanceof ProcessedCacheDataSource;
    }

    @Override
    public DecodeResult decode(LoadRequest request, DataSource dataSource, ImageType imageType,
                               BitmapFactory.Options boundOptions, BitmapFactory.Options decodeOptions, int orientation) {
        decodeOptions.outWidth = boundOptions.outWidth;
        decodeOptions.outHeight = boundOptions.outHeight;
        decodeOptions.outMimeType = boundOptions.outMimeType;

        decodeOptions.inSampleSize = 1;

        // Set inBitmap from bitmap pool
        if (BitmapPoolUtils.sdkSupportInBitmap() && !request.getOptions().isBitmapPoolDisabled()) {
            BitmapPool bitmapPool = request.getConfiguration().getBitmapPool();
            BitmapPoolUtils.setInBitmapFromPool(decodeOptions, bitmapPool);
        }

        Bitmap bitmap = null;
        try {
            bitmap = DefaultImageDecoder.decodeBitmap(dataSource, decodeOptions);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();

            // TODO: 2017/5/9 过滤异常message，准确的识别出inBitmap异常
            // 要是因为inBitmap而解码失败就记录日志并再此尝试
            if (BitmapPoolUtils.sdkSupportInBitmap()) {
                if (!request.getOptions().isBitmapPoolDisabled() && decodeOptions.inBitmap != null) {
                    SketchMonitor sketchMonitor = request.getConfiguration().getMonitor();

                    BitmapPool bitmapPool = request.getConfiguration().getBitmapPool();
                    BitmapPoolUtils.inBitmapThrow(e, decodeOptions, sketchMonitor, bitmapPool,
                            request.getUri(), boundOptions.outWidth, boundOptions.outHeight);

                    decodeOptions.inBitmap = null;
                    try {
                        bitmap = DefaultImageDecoder.decodeBitmap(dataSource, decodeOptions);
                    } catch (Throwable error) {
                        error.printStackTrace();
                        sketchMonitor.onDecodeNormalImageError(error, request,
                                boundOptions.outWidth, boundOptions.outHeight, boundOptions.outMimeType);
                    }
                }
            }
        } catch (Throwable error) {
            error.printStackTrace();
            SketchMonitor sketchMonitor = request.getConfiguration().getMonitor();
            sketchMonitor.onDecodeNormalImageError(error, request,
                    boundOptions.outWidth, boundOptions.outHeight, boundOptions.outMimeType);
        }

        // 过滤掉无效的图片
        if (bitmap == null || bitmap.isRecycled()) {
            DefaultImageDecoder.decodeError(request, dataSource, LOG_NAME);
            return null;
        }

        // 过滤宽高小于等于1的图片
        if (bitmap.getWidth() <= 1 || bitmap.getHeight() <= 1) {
            SLog.w(SLogType.REQUEST, LOG_NAME,
                    "image width or height less than or equal to 1px. imageSize: %dx%d. bitmapSize: %dx%d. %s",
                    boundOptions.outWidth, boundOptions.outHeight, bitmap.getWidth(), bitmap.getHeight(), request.getKey());
            bitmap.recycle();
            DefaultImageDecoder.decodeError(request, dataSource, LOG_NAME);
            return null;
        }

        // 由于是读取的经过处理的缓存图片，因此要重新读取原图的类型、宽高信息
        DataSource originFileDataSource = null;
        try {
            originFileDataSource = DataSourceFactory.makeDataSourceByRequest(request, true, LOG_NAME);
        } catch (DecodeException e) {
            e.printStackTrace();
        }
        BitmapFactory.Options originImageOptions = null;
        if (originFileDataSource != null) {
            originImageOptions = new BitmapFactory.Options();
            originImageOptions.inJustDecodeBounds = true;
            try {
                DefaultImageDecoder.decodeBitmap(originFileDataSource, originImageOptions);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        ImageAttrs imageAttrs;
        if (originImageOptions != null && !TextUtils.isEmpty(originImageOptions.outMimeType)) {
            // Read image orientation
            int originImageOrientation = 0;
            if (request.getOptions().isCorrectImageOrientation()) {
                ImageOrientationCorrector imageOrientationCorrector = request.getConfiguration().getImageOrientationCorrector();
                originImageOrientation = imageOrientationCorrector.readImageRotateDegrees(originImageOptions.outMimeType, originFileDataSource);
            }
            imageAttrs = new ImageAttrs(originImageOptions.outMimeType, originImageOptions.outWidth, originImageOptions.outHeight, originImageOrientation);
        } else {
            imageAttrs = new ImageAttrs(boundOptions.outMimeType, boundOptions.outWidth, boundOptions.outHeight, orientation);
        }

        // 成功
        DefaultImageDecoder.decodeSuccess(bitmap, boundOptions.outWidth, boundOptions.outHeight,
                decodeOptions.inSampleSize, request, LOG_NAME);
        return new BitmapDecodeResult(imageAttrs, bitmap).setBanProcess(true);
    }
}
