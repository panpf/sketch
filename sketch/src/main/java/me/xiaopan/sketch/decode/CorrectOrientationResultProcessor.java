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

import me.xiaopan.sketch.Configuration;
import me.xiaopan.sketch.cache.BitmapPoolUtils;
import me.xiaopan.sketch.feature.ImageOrientationCorrector;
import me.xiaopan.sketch.request.ErrorCause;
import me.xiaopan.sketch.request.LoadRequest;

public class CorrectOrientationResultProcessor implements ResultProcessor {

    @Override
    public void process(LoadRequest request, DecodeResult result) throws DecodeException {
        if (result.isBanProcess()) {
            return;
        }

        if (!(result instanceof BitmapDecodeResult)) {
            return;
        }

        if (request.getOptions().isCorrectImageOrientationDisabled()) {
            return;
        }

        Configuration configuration = request.getConfiguration();
        ImageOrientationCorrector orientationCorrector = configuration.getImageOrientationCorrector();

        int exifOrientation = result.getImageAttrs().getExifOrientation();
        if (!orientationCorrector.hasRotate(exifOrientation)) {
            return;
        }

        BitmapDecodeResult bitmapDecodeResult = (BitmapDecodeResult) result;
        Bitmap bitmap = bitmapDecodeResult.getBitmap();
        if (bitmap == null) {
            return;
        }

        Bitmap newBitmap = orientationCorrector.rotate(bitmap, exifOrientation, configuration.getBitmapPool());
        if (newBitmap != null && newBitmap != bitmap) {
            if (!newBitmap.isRecycled()) {
                BitmapPoolUtils.freeBitmapToPool(bitmap, configuration.getBitmapPool());
                bitmapDecodeResult.setBitmap(newBitmap);

                orientationCorrector.rotateSize(result, exifOrientation);
                result.setProcessed(true);
            } else {
                throw new DecodeException(String.format("%s: %s. %s", ErrorCause.CORRECT_ORIENTATION_FAIL.name(),
                        ImageOrientationCorrector.toName(exifOrientation), request.getUri()), ErrorCause.CORRECT_ORIENTATION_FAIL);
            }
        }
    }
}
