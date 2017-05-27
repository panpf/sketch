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
import android.graphics.BitmapFactory;

import me.xiaopan.sketch.cache.BitmapPoolUtils;
import me.xiaopan.sketch.request.ErrorCause;
import me.xiaopan.sketch.request.LoadRequest;

public abstract class DecodeHelper {
    abstract boolean match(LoadRequest request, DataSource dataSource, ImageType imageType, BitmapFactory.Options boundOptions);

    abstract DecodeResult decode(LoadRequest request, DataSource dataSource, ImageType imageType, BitmapFactory.Options boundOptions,
                                 BitmapFactory.Options decodeOptions, int exifOrientation) throws DecodeException;

    protected void correctOrientation(ImageOrientationCorrector orientationCorrector, DecodeResult decodeResult,
                                      int exifOrientation, LoadRequest request) throws DecodeException {
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
                throw new DecodeException(String.format("%s: %s. %s", ErrorCause.CORRECT_ORIENTATION_FAIL.name(),
                        ImageOrientationCorrector.toName(exifOrientation), request.getUri()), ErrorCause.CORRECT_ORIENTATION_FAIL);
            }
        }
    }
}
