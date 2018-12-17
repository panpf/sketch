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

package me.panpf.sketch.cache;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.locks.ReentrantLock;

import me.panpf.sketch.util.DiskLruCache;

/**
 * 磁盘缓存管理器
 */
public interface DiskCache {
    String DISK_CACHE_DIR_NAME = "sketch";
    int DISK_CACHE_MAX_SIZE = 100 * 1024 * 1024;
    int DISK_CACHE_RESERVED_SPACE_SIZE = 200 * 1024 * 1024;

    /**
     * 是否存在指定 key 的缓存
     *
     * @param key 缓存 key
     */
    boolean exist(@NonNull String key);

    /**
     * 获取指定 key 的缓存
     *
     * @param key 缓存 key
     * @return {@link Entry} 缓存实体，用于读取缓存数据
     */
    @Nullable
    Entry get(@NonNull String key);

    /**
     * 编辑指定 uri 的缓存
     *
     * @param key 缓存 key
     * @return {@link Editor} 缓存编辑器
     */
    @Nullable
    Editor edit(@NonNull String key);

    /**
     * 获取缓存目录
     *
     * @return {@link File}
     */
    @NonNull
    @SuppressWarnings("unused")
    File getCacheDir();

    /**
     * 获取最大容量（默认为 100M）
     */
    long getMaxSize();

    /**
     * 将 key 进行转码
     *
     * @param key 缓存 key
     * @return 转码后的 key
     */
    @NonNull
    String keyEncode(@NonNull String key);

    /**
     * 获取已用容量
     */
    long getSize();

    /**
     * 是否已禁用
     */
    @SuppressWarnings("unused")
    boolean isDisabled();

    /**
     * 设置是否禁用
     *
     * @param disabled 是否禁用
     */
    void setDisabled(boolean disabled);

    /**
     * 清除所有缓存
     */
    void clear();

    /**
     * 是否已关闭
     */
    @SuppressWarnings("unused")
    boolean isClosed();

    /**
     * 关闭，关闭后就彻底不能用了，如果你只是想暂时的关闭就使用 {@link #setDisabled(boolean)}
     */
    void close();

    /**
     * 获取编辑锁
     *
     * @param key 缓存 key
     * @return {@link ReentrantLock}. 编辑锁
     */
    @NonNull
    ReentrantLock getEditLock(@NonNull String key);

    /**
     * 磁盘缓存实体
     */
    interface Entry {
        /**
         * 创建输入流
         *
         * @return {@link InputStream}
         * @throws IOException IO 异常
         */
        @NonNull
        InputStream newInputStream() throws IOException;

        /**
         * 获取缓存文件
         *
         * @return {@link File}
         */
        @NonNull
        File getFile();

        /**
         * 获取缓存 key
         *
         * @return 缓存 key，未转码的
         */
        @NonNull
        String getKey();

        /**
         * 删除实体
         *
         * @return true：删除成功
         */
        boolean delete();
    }

    /**
     * 磁盘缓存编辑器
     */
    interface Editor {
        /**
         * 创建一个输出流，用于写出文件
         *
         * @return {@link OutputStream}
         * @throws IOException IO 异常
         */
        OutputStream newOutputStream() throws IOException;

        /**
         * 写完提交
         *
         * @throws IOException                         IO 异常
         * @throws DiskLruCache.EditorChangedException 编辑器已经改变
         * @throws DiskLruCache.ClosedException        已经关闭了
         * @throws DiskLruCache.FileNotExistException  文件被删除了
         */
        void commit() throws IOException, DiskLruCache.EditorChangedException, DiskLruCache.ClosedException, DiskLruCache.FileNotExistException;

        /**
         * 中断编辑
         */
        void abort();
    }
}