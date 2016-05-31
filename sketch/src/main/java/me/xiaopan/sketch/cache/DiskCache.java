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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import me.xiaopan.sketch.Identifier;
import me.xiaopan.sketch.util.DiskLruCache;

/**
 * 磁盘缓存器
 */
public interface DiskCache extends Identifier {

    /**
     * 是否存在
     */
    boolean exist(String uri);

    /**
     * 获取缓存实体
     */
    Entry get(String uri);

    /**
     * 编辑缓存
     */
    Editor edit(String uri);

    /**
     * 获取缓存目录
     */
    File getCacheDir();

    /**
     * 获取最大容量（默认为100M）
     */
    long getMaxSize();

    /**
     * 将uri地址进行转码作为缓存文件的名字
     */
    String uriToDiskCacheKey(String uri);

    /**
     * 获取已用容量
     */
    long getSize();

    /**
     * 清除缓存
     */
    void clear();

    /**
     * 关闭
     */
    void close();

    interface Entry {
        InputStream newInputStream() throws IOException;

        File getFile();

        String getUri();

        boolean delete();
    }

    interface Editor {
        OutputStream newOutputStream() throws IOException;

        void commit() throws IOException, DiskLruCache.EditorChangedException;

        void abort() throws IOException, DiskLruCache.EditorChangedException;
    }
}