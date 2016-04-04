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

package me.xiaopan.sketch.cache;

import java.io.File;

import me.xiaopan.sketch.util.DiskLruCache;

/**
 * 磁盘缓存器
 */
public interface DiskCache {

    /**
     * 获取缓存文件
     *
     * @param uri 图片uri
     * @return null：没有
     */
    File getCacheFile(String uri);

    /**
     * 编辑缓存
     *
     * @param uri 图片uri
     * @return 编辑器
     */
    DiskLruCache.Editor edit(String uri);

    /**
     * 获取缓存目录
     *
     * @return 缓存目录
     */
    File getCacheDir();

    /**
     * 获取最大容量
     *
     * @return 最大容量，默认为100M
     */
    long getMaxSize();

    /**
     * 将uri地址进行转码作为缓存文件的名字
     *
     * @param uri 图片uri
     * @return 文件名字
     */
    String uriToFileName(String uri);

    /**
     * 获取已用容量
     *
     * @return 已用容量
     */
    long getSize();

    /**
     * 清除缓存
     */
    void clear();

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