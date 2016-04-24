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

package me.xiaopan.sketch;

import me.xiaopan.sketch.cache.DiskCache;

/**
 * 本地图片预处理器
 */
public interface LocalImagePreprocessor {
    /**
     * 是不是特殊的本地图片
     */
    boolean isSpecific(LoadRequest loadRequest);

    /**
     * 获取特殊本地图片的本地缓存实体
     */
    DiskCache.Entry getDiskCacheEntry(LoadRequest loadRequest);

    /**
     * 获取标识符
     *
     * @return 标识符
     */
    String getIdentifier();

    /**
     * 追加标识符
     */
    StringBuilder appendIdentifier(StringBuilder builder);
}
