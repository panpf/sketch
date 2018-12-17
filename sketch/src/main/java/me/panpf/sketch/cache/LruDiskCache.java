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

import android.content.Context;
import androidx.annotation.NonNull;
import android.text.format.Formatter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantLock;

import me.panpf.sketch.Configuration;
import me.panpf.sketch.SLog;
import me.panpf.sketch.util.DiskLruCache;
import me.panpf.sketch.util.NoSpaceException;
import me.panpf.sketch.util.SketchMD5Utils;
import me.panpf.sketch.util.SketchUtils;
import me.panpf.sketch.util.UnableCreateDirException;
import me.panpf.sketch.util.UnableCreateFileException;

/**
 * 根据最少使用规则释放缓存的磁盘缓存管理器
 */
public class LruDiskCache implements DiskCache {
    private static final String NAME = "LruDiskCache";

    private int maxSize;
    private int appVersionCode;
    private File cacheDir;
    private Context context;
    private DiskLruCache cache;
    private Configuration configuration;
    private boolean closed;
    private boolean disabled;
    private Map<String, ReentrantLock> editLockMap;

    /**
     * 创建根据最少使用规则释放缓存的磁盘缓存管理器
     *
     * @param context        {@link Context}
     * @param configuration  {@link Configuration}
     * @param appVersionCode app 版本，用于删除旧缓存，想要主动删除旧缓存时更新这个值即可
     * @param maxSize        最大容量
     */
    public LruDiskCache(Context context, Configuration configuration, int appVersionCode, int maxSize) {
        context = context.getApplicationContext();
        this.context = context;
        this.maxSize = maxSize;
        this.appVersionCode = appVersionCode;
        this.configuration = configuration;
        this.cacheDir = SketchUtils.getDefaultSketchCacheDir(context, DISK_CACHE_DIR_NAME, true);
    }

    /**
     * 检查磁盘缓存器是否可用
     */
    protected boolean checkDiskCache() {
        return cache != null && !cache.isClosed();
    }

    /**
     * 检查缓存目录是否存在并可用
     */
    protected boolean checkCacheDir() {
        return cacheDir != null && cacheDir.exists();
    }

    /**
     * 安装磁盘缓存
     */
    protected synchronized void installDiskCache() {
        if (closed) {
            return;
        }

        // 旧的要关闭
        if (cache != null) {
            try {
                cache.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            cache = null;
        }

        // 创建缓存目录，然后检查空间并创建个文件测试一下
        try {
            cacheDir = SketchUtils.buildCacheDir(context, DISK_CACHE_DIR_NAME, true, DISK_CACHE_RESERVED_SPACE_SIZE, true, true, 10);
        } catch (NoSpaceException | UnableCreateDirException | UnableCreateFileException e) {
            e.printStackTrace();
            configuration.getErrorTracker().onInstallDiskCacheError(e, cacheDir);
            return;
        }

        if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_CACHE)) {
            SLog.d(NAME, "diskCacheDir: %s", cacheDir.getPath());
        }

        try {
            cache = DiskLruCache.open(cacheDir, appVersionCode, 1, maxSize);
        } catch (IOException e) {
            e.printStackTrace();
            configuration.getErrorTracker().onInstallDiskCacheError(e, cacheDir);
        }
    }

    // 这个方法性能优先，因此不加synchronized
    @Override
    public boolean exist(@NonNull String key) {
        if (closed) {
            return false;
        }

        if (disabled) {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_CACHE)) {
                SLog.d(NAME, "Disabled. Unable judge exist, key=%s", key);
            }
            return false;
        }

        // 这个方法性能优先，因此不检查缓存目录
        if (!checkDiskCache()) {
            installDiskCache();
            if (!checkDiskCache()) {
                return false;
            }
        }

        try {
            return cache.exist(keyEncode(key));
        } catch (DiskLruCache.ClosedException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public synchronized Entry get(@NonNull String key) {
        if (closed) {
            return null;
        }

        if (disabled) {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_CACHE)) {
                SLog.d(NAME, "Disabled. Unable get, key=%s", key);
            }
            return null;
        }

        if (!checkDiskCache() || !checkCacheDir()) {
            installDiskCache();
            if (!checkDiskCache()) {
                return null;
            }
        }

        DiskLruCache.SimpleSnapshot snapshot = null;
        try {
            snapshot = cache.getSimpleSnapshot(keyEncode(key));
        } catch (IOException | DiskLruCache.ClosedException e) {
            e.printStackTrace();
        }
        return snapshot != null ? new LruDiskCacheEntry(key, snapshot) : null;
    }

    @Override
    public synchronized Editor edit(@NonNull String key) {
        if (closed) {
            return null;
        }

        if (disabled) {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_CACHE)) {
                SLog.d(NAME, "Disabled. Unable edit, key=%s", key);
            }
            return null;
        }

        if (!checkDiskCache() || !checkCacheDir()) {
            installDiskCache();
            if (!checkDiskCache()) {
                return null;
            }
        }

        DiskLruCache.Editor diskEditor = null;
        try {
            diskEditor = cache.edit(keyEncode(key));
        } catch (IOException e) {
            e.printStackTrace();

            // 发生异常的时候（比如SD卡被拔出，导致不能使用），尝试重装DiskLruCache，能显著提高遇错恢复能力
            installDiskCache();
            if (!checkDiskCache()) {
                return null;
            }

            try {
                diskEditor = cache.edit(keyEncode(key));
            } catch (IOException | DiskLruCache.ClosedException e1) {
                e1.printStackTrace();
            }
        } catch (DiskLruCache.ClosedException e) {
            e.printStackTrace();

            // 旧的关闭了，必须要重装DiskLruCache
            installDiskCache();
            if (!checkDiskCache()) {
                return null;
            }

            try {
                diskEditor = cache.edit(keyEncode(key));
            } catch (IOException | DiskLruCache.ClosedException e1) {
                e1.printStackTrace();
            }
        }
        return diskEditor != null ? new LruDiskCacheEditor(diskEditor) : null;
    }

    @NonNull
    @Override
    public synchronized File getCacheDir() {
        return cacheDir;
    }

    @Override
    public long getMaxSize() {
        return maxSize;
    }

    @NonNull
    @Override
    public String keyEncode(@NonNull String key) {
        // 由于DiskLruCache会在key后面加序列号，因此这里不用再对apk文件的名称做特殊处理了
//        if (SketchUtils.checkSuffix(key, ".apk")) {
//            key += ".icon";
//        }
        return SketchMD5Utils.md5(key);
    }

    @Override
    public synchronized long getSize() {
        if (closed) {
            return 0;
        }

        if (!checkDiskCache()) {
            return 0;
        }

        return cache.size();
    }

    @Override
    public boolean isDisabled() {
        return disabled;
    }

    @Override
    public void setDisabled(boolean disabled) {
        if (this.disabled != disabled) {
            this.disabled = disabled;
            if (disabled) {
                SLog.w(NAME, "setDisabled. %s", true);
            } else {
                SLog.w(NAME, "setDisabled. %s", false);
            }
        }
    }

    @Override
    public synchronized void clear() {
        if (closed) {
            return;
        }

        if (cache != null) {
            try {
                cache.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
            cache = null;
        }

        installDiskCache();
    }

    @Override
    public synchronized boolean isClosed() {
        return closed;
    }

    @Override
    public synchronized void close() {
        if (closed) {
            return;
        }

        closed = true;

        if (cache != null) {
            try {
                cache.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            cache = null;
        }
    }

    @NonNull
    @Override
    public synchronized ReentrantLock getEditLock(@NonNull String key) {
        if (editLockMap == null) {
            synchronized (this) {
                if (editLockMap == null) {
                    editLockMap = new WeakHashMap<>();
                }
            }
        }

        ReentrantLock lock = editLockMap.get(key);
        if (lock == null) {
            lock = new ReentrantLock();
            editLockMap.put(key, lock);
        }
        return lock;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("%s(maxSize=%s,appVersionCode=%d,cacheDir=%s)",
                NAME, Formatter.formatFileSize(context, maxSize), appVersionCode, cacheDir.getPath());
    }

    public static class LruDiskCacheEntry implements Entry {
        private String key;
        private DiskLruCache.SimpleSnapshot snapshot;

        public LruDiskCacheEntry(String key, DiskLruCache.SimpleSnapshot snapshot) {
            this.key = key;
            this.snapshot = snapshot;
        }

        @NonNull
        @Override
        public InputStream newInputStream() throws IOException {
            return snapshot.newInputStream(0);
        }

        @NonNull
        @Override
        public File getFile() {
            return snapshot.getFile(0);
        }

        @NonNull
        @Override
        public String getKey() {
            return key;
        }

        @Override
        public boolean delete() {
            try {
                snapshot.getDiskLruCache().remove(snapshot.getKey());
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } catch (DiskLruCache.ClosedException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public static class LruDiskCacheEditor implements Editor {
        private DiskLruCache.Editor diskEditor;

        public LruDiskCacheEditor(DiskLruCache.Editor diskEditor) {
            this.diskEditor = diskEditor;
        }

        @Override
        public OutputStream newOutputStream() throws IOException {
            return diskEditor.newOutputStream(0);
        }

        @Override
        public void commit() throws IOException, DiskLruCache.EditorChangedException, DiskLruCache.ClosedException, DiskLruCache.FileNotExistException {
            diskEditor.commit();
        }

        @Override
        public void abort() {
            try {
                diskEditor.abort();
            } catch (IOException | DiskLruCache.FileNotExistException | DiskLruCache.EditorChangedException e) {
                e.printStackTrace();
            }
        }
    }
}
