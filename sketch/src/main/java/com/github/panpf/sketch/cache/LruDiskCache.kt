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
import android.text.format.Formatter
import com.github.panpf.sketch.Configuration
import com.github.panpf.sketch.SLog
import com.github.panpf.sketch.SLog.Companion.dmf
import com.github.panpf.sketch.SLog.Companion.emf
import com.github.panpf.sketch.SLog.Companion.isLoggable
import com.github.panpf.sketch.SLog.Companion.wmf
import com.github.panpf.sketch.util.*
import com.github.panpf.sketch.util.SketchMD5Utils.md5
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import java.util.concurrent.locks.ReentrantLock

/**
 * 创建一个根据最少使用规则释放缓存的磁盘缓存管理器
 *
 * @param context        [Context]
 * @param configuration  [Configuration]
 * @param appVersionCode app 版本，用于删除旧缓存，想要主动删除旧缓存时更新这个值即可
 * @param maxSize        最大容量
 */
class LruDiskCache(
    context: Context,
    private val configuration: Configuration,
    private val appVersionCode: Int,
    maxSize: Int
) : DiskCache {

    companion object {
        private const val NAME = "LruDiskCache"
    }

    override val maxSize: Long = maxSize.toLong()

    @get:Synchronized
    private var cacheDirHolder: File =
        SketchUtils.getDefaultSketchCacheDir(
            context.applicationContext,
            DiskCache.DISK_CACHE_DIR_NAME,
            true
        )

    @get:Synchronized
    override val cacheDir: File
        get() = cacheDirHolder
    private val appContext: Context = context.applicationContext
    private var cache: DiskLruCache? = null

    @get:Synchronized
    override var isClosed = false
        private set
    override var isDisabled: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                wmf(NAME, "setDisabled. %s", value)
            }
        }
    private var editLockMap: MutableMap<String, ReentrantLock>? = null

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

    /**
     * 安装磁盘缓存
     */
    @Synchronized
    private fun installDiskCache() {
        if (isClosed) {
            return
        }

        // 旧的要关闭
        if (cache != null) {
            try {
                cache!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            cache = null
        }

        // 创建缓存目录，然后检查空间并创建个文件测试一下
        cacheDirHolder = try {
            SketchUtils.buildCacheDir(
                context = appContext,
                dirName = DiskCache.DISK_CACHE_DIR_NAME,
                compatManyProcess = true,
                minSpaceSize = DiskCache.DISK_CACHE_RESERVED_SPACE_SIZE.toLong(),
                cleanOnNoSpace = true,
                cleanOldCacheFiles = true,
                expandNumber = 10
            )
        } catch (e: NoSpaceException) {
            e.printStackTrace()
            emf(
                NAME,
                "Install disk cache error. %s: %s. SDCardState: %s. cacheDir: %s",
                e.javaClass.simpleName,
                e.message!!,
                Environment.getExternalStorageState(),
                cacheDir.path
            )
            configuration.callback.onError(InstallDiskCacheException(e, cacheDir))
            return
        } catch (e: UnableCreateDirException) {
            e.printStackTrace()
            emf(
                NAME,
                "Install disk cache error. %s: %s. SDCardState: %s. cacheDir: %s",
                e.javaClass.simpleName,
                e.message!!,
                Environment.getExternalStorageState(),
                cacheDir.path
            )
            configuration.callback.onError(InstallDiskCacheException(e, cacheDir))
            return
        } catch (e: UnableCreateFileException) {
            e.printStackTrace()
            emf(
                NAME,
                "Install disk cache error. %s: %s. SDCardState: %s. cacheDir: %s",
                e.javaClass.simpleName,
                e.message!!,
                Environment.getExternalStorageState(),
                cacheDir.path
            )
            configuration.callback.onError(InstallDiskCacheException(e, cacheDir))
            return
        }
        if (isLoggable(SLog.DEBUG)) {
            dmf(NAME, "diskCacheDir: %s", cacheDir.path)
        }
        try {
            cache = DiskLruCache.open(cacheDir, appVersionCode, 1, maxSize)
        } catch (e: IOException) {
            e.printStackTrace()
            emf(
                NAME,
                "Install disk cache error. %s: %s. SDCardState: %s. cacheDir: %s",
                e.javaClass.simpleName,
                e.message!!,
                Environment.getExternalStorageState(),
                cacheDir.path
            )
            configuration.callback.onError(InstallDiskCacheException(e, cacheDir))
        }
    }

    // 这个方法性能优先，因此不加synchronized
    override fun exist(key: String): Boolean {
        if (isClosed) {
            return false
        }
        if (isDisabled) {
            if (isLoggable(SLog.DEBUG)) {
                dmf(NAME, "Disabled. Unable judge exist, key=%s", key)
            }
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
            cache!!.exist(keyEncode(key))
        } catch (e: DiskLruCache.ClosedException) {
            e.printStackTrace()
            false
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    @Synchronized
    override fun get(key: String): DiskCache.Entry? {
        if (isClosed) {
            return null
        }
        if (isDisabled) {
            if (isLoggable(SLog.DEBUG)) {
                dmf(NAME, "Disabled. Unable get, key=%s", key)
            }
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
            snapshot = cache!!.getSimpleSnapshot(keyEncode(key))
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: DiskLruCache.ClosedException) {
            e.printStackTrace()
        }
        return snapshot?.let { LruDiskCacheEntry(key, it) }
    }

    @Synchronized
    override fun edit(key: String): DiskCache.Editor? {
        if (isClosed) {
            return null
        }
        if (isDisabled) {
            if (isLoggable(SLog.DEBUG)) {
                dmf(NAME, "Disabled. Unable edit, key=%s", key)
            }
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
            diskEditor = cache!!.edit(keyEncode(key))
        } catch (e: IOException) {
            e.printStackTrace()

            // 发生异常的时候（比如SD卡被拔出，导致不能使用），尝试重装DiskLruCache，能显著提高遇错恢复能力
            installDiskCache()
            if (!checkDiskCache()) {
                return null
            }
            try {
                diskEditor = cache!!.edit(keyEncode(key))
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
                diskEditor = cache!!.edit(keyEncode(key))
            } catch (e1: IOException) {
                e1.printStackTrace()
            } catch (e1: DiskLruCache.ClosedException) {
                e1.printStackTrace()
            }
        }
        return diskEditor?.let { LruDiskCacheEditor(it) }
    }

    override fun keyEncode(key: String): String {
        // 由于DiskLruCache会在key后面加序列号，因此这里不用再对apk文件的名称做特殊处理了
//        if (SketchUtils.checkSuffix(key, ".apk")) {
//            key += ".icon";
//        }
        return md5(key)
    }

    @get:Synchronized
    override val size: Long
        get() {
            if (isClosed) {
                return 0
            }
            return if (!checkDiskCache()) {
                0
            } else cache!!.size()
        }

    @Synchronized
    override fun clear() {
        if (isClosed) {
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
    override fun getEditLock(key: String): ReentrantLock {
        if (editLockMap == null) {
            synchronized(this) {
                if (editLockMap == null) {
                    editLockMap = WeakHashMap()
                }
            }
        }
        var lock = editLockMap!![key]
        if (lock == null) {
            lock = ReentrantLock()
            editLockMap!![key] = lock
        }
        return lock
    }

    override fun toString(): String {
        return String.format(
            Locale.US,
            "%s(maxSize=%s,appVersionCode=%d,cacheDir=%s)",
            NAME,
            Formatter.formatFileSize(appContext, maxSize),
            appVersionCode,
            cacheDir.path
        )
    }

    class LruDiskCacheEntry(
        override val key: String,
        private val snapshot: DiskLruCache.SimpleSnapshot
    ) :
        DiskCache.Entry {
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

    class LruDiskCacheEditor(private val diskEditor: DiskLruCache.Editor) : DiskCache.Editor {
        @Throws(IOException::class)
        override fun newOutputStream(): OutputStream? {
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
}