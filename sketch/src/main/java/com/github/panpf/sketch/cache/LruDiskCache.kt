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
package com.github.panpf.sketch.cache

import android.content.Context
import android.os.Environment
import com.github.panpf.sketch.cache.DiskCache.Editor
import com.github.panpf.sketch.cache.DiskCache.Entry
import com.github.panpf.sketch.util.DiskLruCache
import com.github.panpf.sketch.util.Logger
import com.github.panpf.sketch.util.MD5Utils
import com.github.panpf.sketch.util.formatFileSize
import kotlinx.coroutines.sync.Mutex
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.Locale
import java.util.WeakHashMap

/**
 * Create a disk cache manager that releases the cache according to the least used rule
 *
 * @param context           [Context]
 * @param versionCode       Version, used to delete the old cache, update this value when you want to actively delete the old cache
 * @param maxSize           Maximum capacity
 */
class LruDiskCache(
    context: Context,
    val logger: Logger,
    private val versionCode: Int = 1,
    maxSize: Int = DISK_CACHE_MAX_SIZE,
) : DiskCache {

    companion object {
        private const val MODULE = "LruDiskCache"
        const val DISK_CACHE_DIR_NAME = "sketch"
        const val DISK_CACHE_MAX_SIZE = 512 * 1024 * 1024
        const val DISK_CACHE_RESERVED_SPACE_SIZE = 200 * 1024 * 1024
    }

    private val appContext: Context = context.applicationContext
    private val cacheDirCreator = DiskCacheDirCreator(context)

    @get:Synchronized
    private var cacheDirHolder: File = cacheDirCreator.getSafeCacheDir(DISK_CACHE_DIR_NAME)
    private var cache: DiskLruCache? = null
    private val editMutexLockMap: MutableMap<String, Mutex> = WeakHashMap()

    override val maxSize: Long = maxSize.toLong()

    var errorCallback: ErrorCallback? = null

    @get:Synchronized
    override val cacheDir: File
        get() = cacheDirHolder

    @get:Synchronized
    override var isClosed = false
        private set

    override var isDisabled: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                logger.w(MODULE, "setDisabled. $value")
            }
        }

    /**
     * 检查磁盘缓存器是否可用
     */
    private fun checkDiskCache(): Boolean {
        return cache != null && !cache!!.isClosed
    }

    /**
     * 检查缓存目录是否存在并可用
     */
    private fun checkCacheDir(): Boolean {
        return cacheDir.exists()
    }

    // 这个方法性能优先，因此不加synchronized
    override fun exist(encodedKey: String): Boolean {
        if (isClosed) {
            logger.e(MODULE, "Closed. Unable judge exist, key=$encodedKey")
            return false
        }
        if (isDisabled) {
            logger.w(MODULE, "Disabled. Unable judge exist, key=$encodedKey")
            return false
        }

        // 这个方法性能优先，因此不检查缓存目录
        if (!checkDiskCache()) {
            installDiskCache()
            if (!checkDiskCache()) {
                return false
            }
        }
        return try {
            cache!!.exist(encodeKey(encodedKey))
        } catch (e: DiskLruCache.ClosedException) {
            e.printStackTrace()
            false
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    @Synchronized
    override fun get(encodedKey: String): Entry? {
        if (isClosed) {
            logger.e(MODULE, "Closed. Unable get, key=$encodedKey")
            return null
        }
        if (isDisabled) {
            logger.w(MODULE, "Disabled. Unable get, key=$encodedKey")
            return null
        }
        if (!checkDiskCache() || !checkCacheDir()) {
            installDiskCache()
            if (!checkDiskCache()) {
                return null
            }
        }
        var snapshot: DiskLruCache.SimpleSnapshot? = null
        try {
            snapshot = cache!!.getSimpleSnapshot(encodeKey(encodedKey))
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: DiskLruCache.ClosedException) {
            e.printStackTrace()
        }
        return snapshot?.let { LruDiskCacheEntry(encodedKey, it) }
    }

    @Synchronized
    override fun edit(encodedKey: String): Editor? {
        if (isClosed) {
            logger.e(MODULE, "Closed. Unable edit, key=$encodedKey")
            return null
        }
        if (isDisabled) {
            logger.w(MODULE, "Disabled. Unable edit, key=$encodedKey")
            return null
        }
        if (!checkDiskCache() || !checkCacheDir()) {
            installDiskCache()
            if (!checkDiskCache()) {
                return null
            }
        }
        var diskEditor: DiskLruCache.Editor? = null
        try {
            diskEditor = cache!!.edit(encodeKey(encodedKey))
        } catch (e: IOException) {
            e.printStackTrace()

            // 发生异常的时候（比如SD卡被拔出，导致不能使用），尝试重装DiskLruCache，能显著提高遇错恢复能力
            installDiskCache()
            if (!checkDiskCache()) {
                return null
            }
            try {
                diskEditor = cache!!.edit(encodeKey(encodedKey))
            } catch (e1: IOException) {
                e1.printStackTrace()
            } catch (e1: DiskLruCache.ClosedException) {
                e1.printStackTrace()
            }
        } catch (e: DiskLruCache.ClosedException) {
            e.printStackTrace()

            // 旧的关闭了，必须要重装DiskLruCache
            installDiskCache()
            if (!checkDiskCache()) {
                return null
            }
            try {
                diskEditor = cache!!.edit(encodeKey(encodedKey))
            } catch (e1: IOException) {
                e1.printStackTrace()
            } catch (e1: DiskLruCache.ClosedException) {
                e1.printStackTrace()
            }
        }
        return diskEditor?.let { LruDiskCacheEditor(it) }
    }

    override fun encodeKey(key: String): String {
        return MD5Utils.md5(key)
    }

    @get:Synchronized
    override val size: Long
        get() = if (isClosed) 0 else {
            if (checkDiskCache()) cache!!.size() else 0
        }

    @Synchronized
    override fun clear() {
        if (isClosed) {
            logger.e(MODULE, "Closed. Unable clear")
            return
        }
        if (cache != null) {
            try {
                cache!!.delete()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            cache = null
        }
        installDiskCache()
    }

    @Synchronized
    override fun close() {
        if (isClosed) {
            logger.e(MODULE, "Closed. Unable close")
            return
        }
        isClosed = true
        if (cache != null) {
            try {
                cache!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            cache = null
        }
    }

    @Synchronized
    override fun editLock(encodedKey: String): Mutex {
        return editMutexLockMap[encodedKey] ?: Mutex().apply {
            this@LruDiskCache.editMutexLockMap[encodedKey] = this
        }
    }

    override fun toString(): String {
        return String.format(
            Locale.US,
            "%s(maxSize=%s,appVersionCode=%d,cacheDir=%s)",
            MODULE,
            maxSize.formatFileSize(),
            versionCode,
            cacheDir.path
        )
    }

    /**
     * 安装磁盘缓存
     */
    // todo 抛出异常
    @Synchronized
    private fun installDiskCache() {
        if (isClosed) {
            return
        }

        // 旧的要关闭
        if (cache != null) {
            try {
                cache?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            cache = null
        }

        // 创建缓存目录，然后检查空间并创建个文件测试一下
        cacheDirHolder = try {
            cacheDirCreator.buildCacheDir(
                dirName = DISK_CACHE_DIR_NAME,
                minSpaceSize = DISK_CACHE_RESERVED_SPACE_SIZE.toLong(),
                cleanOnNoSpace = true,
                cleanOldCacheFiles = true,
                expandNumber = 10
            )
        } catch (e: IOException) {
            e.printStackTrace()
            logger.e(
                MODULE,
                "Install disk cache error. %s: %s. SDCardState: %s. cacheDir: %s".format(
                    e.javaClass.simpleName,
                    e.message ?: "",
                    Environment.getExternalStorageState(),
                    cacheDir.path
                )
            )
            errorCallback?.onInstallDiskCacheError(cacheDir, e)
            return
        }
        logger.d(MODULE) {
            "diskCacheDir: %s".format(cacheDir.path)
        }
        try {
            cache = DiskLruCache.open(cacheDir, versionCode, 1, maxSize)
        } catch (e: IOException) {
            e.printStackTrace()
            logger.e(
                MODULE,
                "Install disk cache error. %s: %s. SDCardState: %s. cacheDir: %s".format(
                    e.javaClass.simpleName,
                    e.message ?: "",
                    Environment.getExternalStorageState(),
                    cacheDir.path
                )
            )
            errorCallback?.onInstallDiskCacheError(cacheDir, e)
        }
    }

    class LruDiskCacheEntry(
        override val key: String,
        private val snapshot: DiskLruCache.SimpleSnapshot
    ) :
        Entry {
        @Throws(IOException::class)
        override fun newInputStream(): InputStream {
            return snapshot.newInputStream(0)
        }

        override val file: File
            get() = snapshot.getFile(0)

        override fun delete(): Boolean {
            return try {
                snapshot.diskLruCache.remove(snapshot.key)
                true
            } catch (e: IOException) {
                e.printStackTrace()
                false
            } catch (e: DiskLruCache.ClosedException) {
                e.printStackTrace()
                false
            }
        }
    }

    class LruDiskCacheEditor(private val diskEditor: DiskLruCache.Editor) : Editor {
        @Throws(IOException::class)
        override fun newOutputStream(): OutputStream {
            return diskEditor.newOutputStream(0)
        }

        @Throws(
            IOException::class,
            DiskLruCache.EditorChangedException::class,
            DiskLruCache.ClosedException::class,
            DiskLruCache.FileNotExistException::class
        )
        override fun commit() {
            diskEditor.commit()
        }

        override fun abort() {
            try {
                diskEditor.abort()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: DiskLruCache.FileNotExistException) {
                e.printStackTrace()
            } catch (e: DiskLruCache.EditorChangedException) {
                e.printStackTrace()
            }
        }
    }

    fun interface ErrorCallback {
        fun onInstallDiskCacheError(dir: File, throwable: Throwable)
    }
}