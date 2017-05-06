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

        if (!request.getOptions().isCorrectImageOrientation()) {
            return;
        }

        int orientation = result.getImageAttrs().getOrientation();
        if (orientation == 0) {
            return;
        }

        BitmapDecodeResult bitmapDecodeResult = (BitmapDecodeResult) result;
        Bitmap bitmap = bitmapDecodeResult.getBitmap();
        if (bitmap == null) {
            return;
        }

        Configuration configuration = request.getConfiguration();
        ImageOrientationCorrector corrector = configuration.getImageOrientationCorrector();
        Bitmap newBitmap = corrector.rotate(bitmap, orientation, configuration.getBitmapPool());

        if (newBitmap != null && !newBitmap.isRecycled()) {
            if (newBitmap != bitmap) {
                BitmapPoolUtils.freeBitmapToPool(bitmap, configuration.getBitmapPool());
                bitmapDecodeResult.setBitmap(newBitmap);
            }

            corrector.rotateSize(result, orientation);

            result.setProcessed(true);
        } else {
            throw new DecodeException(null, ErrorCause.CORRECT_ORIENTATION_FAIL);
        }
    }
}
