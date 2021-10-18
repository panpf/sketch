/*
 * Copyright (C) 2019 panpf <panpfpanpf@outlook.com>
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

package com.github.panpf.sketch.decode;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.format.Formatter;

import androidx.annotation.NonNull;

import com.github.panpf.sketch.SLog;
import com.github.panpf.sketch.cache.BitmapPoolUtils;
import com.github.panpf.sketch.process.ImageProcessor;
import com.github.panpf.sketch.request.BaseRequest;
import com.github.panpf.sketch.request.LoadOptions;
import com.github.panpf.sketch.request.LoadRequest;

public class ProcessImageResultProcessor implements ResultProcessor {

    @Override
    public void process(@NonNull LoadRequest request, @NonNull DecodeResult result) throws ProcessException {
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
        ImageProcessor imageProcessor = loadOptions.getProcessor();
        if (imageProcessor == null) {
            return;
        }

        request.setStatus(BaseRequest.Status.PROCESSING);

        Bitmap newBitmap = null;
        try {
            newBitmap = imageProcessor.process(request.getSketch(), bitmap, loadOptions.getResize(), loadOptions.isLowQualityImage());
        } catch (Throwable e) {
            e.printStackTrace();
            Context application = request.getConfiguration().getContext();
            SLog.emf("ProcessImageResultProcessor", "onProcessImageError. imageUri: %s. processor: %s. " +
                            "appMemoryInfo: maxMemory=%s, freeMemory=%s, totalMemory=%s",
                    request.getKey(), imageProcessor.toString(),
                    Formatter.formatFileSize(application, Runtime.getRuntime().maxMemory()),
                    Formatter.formatFileSize(application, Runtime.getRuntime().freeMemory()),
                    Formatter.formatFileSize(application, Runtime.getRuntime().totalMemory()));
            request.getConfiguration().getCallback().onError(new ProcessImageException(e, request.getKey(), imageProcessor));
        }

        if (newBitmap != null && !newBitmap.isRecycled()) {
            if (newBitmap != bitmap) {
                BitmapPoolUtils.freeBitmapToPool(bitmap, request.getConfiguration().getBitmapPool());
                bitmapDecodeResult.setBitmap(newBitmap);
            }
            result.setProcessed(true);
        } else {
            throw new ProcessException("Process result bitmap null or recycled");
        }
    }
}
