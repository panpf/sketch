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

package me.panpf.sketch.request;

/**
 * 图片来源
 */
public enum ImageFrom {
    /**
     * 网络
     */
    NETWORK,

    /**
     * 磁盘缓存
     */
    DISK_CACHE,

    /**
     * 本地图片
     */
    LOCAL,

    /**
     * 内存缓存
     */
    MEMORY_CACHE,

    /**
     * 内存
     */
    MEMORY,
}
