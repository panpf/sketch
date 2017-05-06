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

import me.xiaopan.sketch.SketchMonitor;
import me.xiaopan.sketch.cache.BitmapPoolUtils;
import me.xiaopan.sketch.process.ImageProcessor;
import me.xiaopan.sketch.request.BaseRequest;
import me.xiaopan.sketch.request.ErrorCause;
import me.xiaopan.sketch.request.LoadOptions;
import me.xiaopan.sketch.request.LoadRequest;

public class ProcessImageResultProcessor implements ResultProcessor {

    @Override
    public void process(LoadRequest request, DecodeResult result) throws DecodeException {
        if (result.isBanProcess()) {
            return;
        }

        if (!(result instanceof BitmapDecodeResult)) {
            return;
        }

        BitmapDecodeResult bitmapDecodeResult = (BitmapDecodeResult) result;
        Bitmap bitmap = bitmapDecodeResult.getBitmap();
        if (bitmap == null) {
            return;
        }

        LoadOptions loadOptions = request.getOptions();
        ImageProcessor imageProcessor = loadOptions.getImageProcessor();
        if (imageProcessor == null) {
            return;
        }

        request.setStatus(BaseRequest.Status.PROCESSING);

        Bitmap newBitmap = null;
        try {
            newBitmap = imageProcessor.process(
                    request.getSketch(), bitmap,
                    loadOptions.getResize(), loadOptions.isForceUseResize(),
                    loadOptions.isLowQualityImage());
        } catch (Throwable e) {
            e.printStackTrace();
            SketchMonitor sketchMonitor = request.getConfiguration().getMonitor();
            sketchMonitor.onProcessImageError(e, request.getKey(), imageProcessor);
        }

        if (newBitmap != null && !newBitmap.isRecycled()) {
            if (newBitmap != bitmap) {
                BitmapPoolUtils.freeBitmapToPool(bitmap, request.getConfiguration().getBitmapPool());
                bitmapDecodeResult.setBitmap(newBitmap);
            }
            result.setProcessed(true);
        } else {
            throw new DecodeException(null, ErrorCause.PROCESS_IMAGE_FAIL);
        }
    }
}
