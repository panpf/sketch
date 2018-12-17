/*
 * Copyright (C) 2013 Peng fei Pan <sky@panpf.me>
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

import me.panpf.sketch.cache.BitmapPoolUtils;
import me.panpf.sketch.datasource.DataSource;
import me.panpf.sketch.request.LoadRequest;

/**
 * 解码图片
 */
public abstract class DecodeHelper {

    /**
     * 当前 {@link DecodeHelper} 是否可以解码指定类型的图片
     *
     * @param request      {@link LoadRequest}
     * @param dataSource   {@link DataSource}. 图片数据源，用于读取图片数据
     * @param imageType    {@link ImageType}. 图片类型
     * @param boundOptions {@link BitmapFactory.Options}. 图片尺寸和类型信息
     * @return true：可以
     */
    abstract boolean match(@NonNull LoadRequest request, @NonNull DataSource dataSource,
                           @Nullable ImageType imageType, @NonNull BitmapFactory.Options boundOptions);

    /**
     * 解码图片
     *
     * @param request         {@link LoadRequest}
     * @param dataSource      {@link DataSource}. 图片数据源，用于读取图片数据
     * @param imageType       {@link ImageType}. 图片类型
     * @param boundOptions    {@link BitmapFactory.Options}. 图片尺寸和类型信息
     * @param decodeOptions   {@link BitmapFactory.Options}. 图片解码选项，在这之前会配置好
     *                        {@link BitmapFactory.Options#inPreferredConfig} 和 {@link BitmapFactory.Options#inPreferQualityOverSpeed} 属性
     * @param exifOrientation 图片方向
     * @return {@link DecodeResult} 解码结果
     * @throws DecodeException 解码失败
     */
    @NonNull
    abstract DecodeResult decode(@NonNull LoadRequest request, @NonNull DataSource dataSource, @Nullable ImageType imageType,
                                 @NonNull BitmapFactory.Options boundOptions, @NonNull BitmapFactory.Options decodeOptions, int exifOrientation) throws DecodeException;

    /**
     * 共子类纠正图片方向
     *
     * @param orientationCorrector {@link ImageOrientationCorrector} 图片方向纠正器
     * @param decodeResult         {@link DecodeResult} 解码结果
     * @param exifOrientation      图片方向
     * @param request              {@link LoadRequest}
     * @throws CorrectOrientationException 纠正方向失败了
     */
    protected void correctOrientation(@NonNull ImageOrientationCorrector orientationCorrector, @NonNull DecodeResult decodeResult,
                                      int exifOrientation, @NonNull LoadRequest request) throws CorrectOrientationException {
        if (!(decodeResult instanceof BitmapDecodeResult)) {
            return;
        }

        BitmapDecodeResult bitmapDecodeResult = (BitmapDecodeResult) decodeResult;

        Bitmap bitmap = bitmapDecodeResult.getBitmap();
        Bitmap newBitmap = orientationCorrector.rotate(bitmap, exifOrientation, request.getConfiguration().getBitmapPool());
        if (newBitmap != null && newBitmap != bitmap) {
            if (!newBitmap.isRecycled()) {
                BitmapPoolUtils.freeBitmapToPool(bitmap, request.getConfiguration().getBitmapPool());
                bitmapDecodeResult.setBitmap(newBitmap);

                bitmapDecodeResult.setProcessed(true);
            } else {
                throw new CorrectOrientationException("Bitmap recycled. exifOrientation=" + ImageOrientationCorrector.toName(exifOrientation));
            }
        }
    }
}
