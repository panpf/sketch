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
package com.github.panpf.sketch.decode

import android.graphics.Bitmap
import android.text.format.Formatter
import com.github.panpf.sketch.SLog
import com.github.panpf.sketch.cache.BitmapPoolUtils
import com.github.panpf.sketch.request.BaseRequest
import com.github.panpf.sketch.request.LoadRequest

class ProcessImageResultProcessor : ResultProcessor {
    @Throws(ProcessException::class)
    override fun process(request: LoadRequest, result: DecodeResult) {
        if (result.isBanProcess) {
            return
        }
        if (result !is BitmapDecodeResult) {
            return
        }
        val bitmap = result.bitmap
        val loadOptions = request.options
        val imageProcessor = loadOptions.processor ?: return
        request.setStatus(BaseRequest.Status.PROCESSING)
        var newBitmap: Bitmap? = null
        try {
            newBitmap = imageProcessor.process(
                request.sketch,
                bitmap,
                loadOptions.resize,
                loadOptions.isLowQualityImage
            )
        } catch (e: Throwable) {
            e.printStackTrace()
            val application = request.configuration.context
            SLog.emf(
                "ProcessImageResultProcessor",
                "onProcessImageError. imageUri: %s. processor: %s. " +
                        "appMemoryInfo: maxMemory=%s, freeMemory=%s, totalMemory=%s",
                request.key,
                imageProcessor.toString(),
                Formatter.formatFileSize(application, Runtime.getRuntime().maxMemory()),
                Formatter.formatFileSize(application, Runtime.getRuntime().freeMemory()),
                Formatter.formatFileSize(application, Runtime.getRuntime().totalMemory())
            )
            request.configuration.callback.onError(
                ProcessImageException(
                    e,
                    request.key,
                    imageProcessor
                )
            )
        }
        if (newBitmap != null && !newBitmap.isRecycled) {
            if (newBitmap != bitmap) {
                BitmapPoolUtils.freeBitmapToPool(bitmap, request.configuration.bitmapPool)
                result.bitmap = newBitmap
            }
            result.isProcessed = true
        } else {
            throw ProcessException("Process result bitmap null or recycled")
        }
    }
}