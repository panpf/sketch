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

import com.github.panpf.sketch.request.LoadRequest

/**
 * 缓存经过处理的图片，方便下次直接读取，加快速度
 */
class ProcessedResultCacheProcessor : ResultProcessor {
    override fun process(request: LoadRequest, result: DecodeResult) {
        if (result.isBanProcess) {
            return
        }
        if (result !is BitmapDecodeResult) {
            return
        }
        val transformCacheManager = request.configuration.transformCacheManager
        if (!transformCacheManager.canUse(request.options)) {
            return
        }
        if (!result.isProcessed) {
            return
        }
        transformCacheManager.saveToDiskCache(request, result.bitmap)
    }
}