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
package com.github.panpf.sketch3.common.cache.disk

import com.github.panpf.sketch3.util.DiskLruCache
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

/**
 * 磁盘缓存管理器
 */
interface DiskCache {

    /**
     * 是否存在指定 encodedKey 的缓存
     *
     * @param encodedKey 缓存 encodedKey
     */
    fun exist(encodedKey: String): Boolean

    /**
     * 获取指定 encodedKey 的缓存
     *
     * @param encodedKey 缓存 encodedKey
     * @return [Entry] 缓存实体，用于读取缓存数据
     */
    operator fun get(encodedKey: String): Entry?

    /**
     * 编辑指定 uri 的缓存
     *
     * @param encodedKey 缓存 encodedKey
     * @return [Editor] 缓存编辑器
     */
    fun edit(encodedKey: String): Editor?

    /**
     * 获取缓存目录
     *
     * @return [File]
     */
    val cacheDir: File

    /**
     * 获取最大容量（默认为 100M）
     */
    val maxSize: Long

    /**
     * 将 key 进行转码
     *
     * @param key 缓存 key
     * @return 转码后的 key
     */
    fun encodeKey(key: String): String

    /**
     * 获取已用容量
     */
    val size: Long

    /**
     * 是否已禁用
     */
    var isDisabled: Boolean

    /**
     * 清除所有缓存
     */
    fun clear()

    /**
     * 是否已关闭
     */
    val isClosed: Boolean

    /**
     * 关闭，关闭后就彻底不能用了，如果你只是想暂时的关闭就使用 [.setDisabled]
     */
    fun close()

    /**
     * 磁盘缓存实体
     */
    interface Entry {
        /**
         * 创建输入流
         *
         * @return [InputStream]
         * @throws IOException IO 异常
         */
        @Throws(IOException::class)
        fun newInputStream(): InputStream

        /**
         * 获取缓存文件
         *
         * @return [File]
         */
        val file: File

        /**
         * 获取缓存 key
         *
         * @return 缓存 key，未转码的
         */
        val key: String

        /**
         * 删除实体
         *
         * @return true：删除成功
         */
        fun delete(): Boolean
    }

    /**
     * 磁盘缓存编辑器
     */
    interface Editor {
        /**
         * 创建一个输出流，用于写出文件
         *
         * @return [OutputStream]
         * @throws IOException IO 异常
         */
        @Throws(IOException::class)
        fun newOutputStream(): OutputStream

        /**
         * 写完提交
         *
         * @throws IOException                         IO 异常
         * @throws DiskLruCache.EditorChangedException 编辑器已经改变
         * @throws DiskLruCache.ClosedException        已经关闭了
         * @throws DiskLruCache.FileNotExistException  文件被删除了
         */
        @Throws(
            IOException::class,
            DiskLruCache.EditorChangedException::class,
            DiskLruCache.ClosedException::class,
            DiskLruCache.FileNotExistException::class
        )
        fun commit()

        /**
         * 中断编辑
         */
        fun abort()
    }
}