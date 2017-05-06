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

import me.xiaopan.sketch.feature.ProcessedImageCache;
import me.xiaopan.sketch.request.LoadRequest;

/**
 * 缓存经过处理的图片
 */
public class ProcessedCacheResultProcessor implements ResultProcessor {

    @Override
    public void process(LoadRequest request, DecodeResult result) {
        if (result.isBanProcess()) {
            return;
        }

        if (!(result instanceof BitmapDecodeResult)) {
            return;
        }

        ProcessedImageCache processedImageCache = request.getConfiguration().getProcessedImageCache();
        if (!processedImageCache.canUse(request.getOptions())) {
            return;
        }

        if (!result.isProcessed()) {
            return;
        }

        BitmapDecodeResult bitmapDecodeResult = (BitmapDecodeResult) result;
        processedImageCache.saveProcessedImageToDiskCache(request, bitmapDecodeResult.getBitmap());
    }
}
