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
 * 决定请求的处理深度
 */
public enum RequestLevel {
    /**
     * 正常的情况，内存没有就从本地加载，本地还没有就从网络加载，适用于所有类型的图片
     */
    NET(2),

    /**
     * 只从内存或本地加载图片，如果本地还没有就结束处理，适用于网络图片
     */
    LOCAL(1),

    /**
     * 只从内存中加载图片，如果内存缓存中没有就结束处理，适用于所有类型的图片，对加载请求和下载请求不起作用
     */
    MEMORY(0);

    private int level;

    RequestLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}