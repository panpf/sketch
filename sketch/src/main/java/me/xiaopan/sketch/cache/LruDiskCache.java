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

import android.content.Context;
import android.text.format.Formatter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantLock;

import me.xiaopan.sketch.Configuration;
import me.xiaopan.sketch.util.DiskLruCache;
import me.xiaopan.sketch.util.NoSpaceException;
import me.xiaopan.sketch.util.SketchUtils;
import me.xiaopan.sketch.util.UnableCreateDirException;
import me.xiaopan.sketch.util.UnableCreateFileException;

public class LruDiskCache implements DiskCache {
    protected String logName = "LruDiskCache";

    private int maxSize;
    private int appVersionCode;
    private File cacheDir;
    private Context context;
    private DiskLruCache cache;
    private Configuration configuration;
    private Map<String, ReentrantLock> editLockMap;
    private boolean closed;

    public LruDiskCache(Context context, Configuration configuration, int appVersionCode, int maxSize) {
        this.context = context;
        this.maxSize = maxSize;
        this.appVersionCode = appVersionCode;
        this.configuration = configuration;
        this.cacheDir = SketchUtils.getSketchCacheDir(context, true, DISK_CACHE_DIR_NAME);
    }

    private boolean checkCache(boolean checkCacheDir) {
        if (checkCacheDir) {
            return cache != null && !cache.isClosed() && cacheDir != null && cacheDir.exists();
        } else {
            return cache != null && !cache.isClosed();
        }
    }

    /**
     * 安装磁盘缓存
     */
    private synchronized void reinstallDiskCache() {
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

        // 重置缓存目录
        cacheDir = SketchUtils.getSketchCacheDir(context, true, DISK_CACHE_DIR_NAME);

        // 创建缓存目录，然后检查空间并创建个文件测试一下
        try {
            cacheDir = SketchUtils.buildCacheDir(context, cacheDir, DISK_CACHE_RESERVED_SPACE_SIZE, true, true, 10);
        } catch (NoSpaceException e) {
            e.printStackTrace();
            configuration.getErrorCallback().onInstallDiskCacheFailed(e, cacheDir);
            return;
        } catch (UnableCreateDirException e) {
            e.printStackTrace();
            configuration.getErrorCallback().onInstallDiskCacheFailed(e, cacheDir);
            return;
        } catch (UnableCreateFileException e) {
            e.printStackTrace();
            configuration.getErrorCallback().onInstallDiskCacheFailed(e, cacheDir);
            return;
        }

        try {
            cache = DiskLruCache.open(cacheDir, appVersionCode, 1, maxSize);
        } catch (IOException e) {
            e.printStackTrace();
            configuration.getErrorCallback().onInstallDiskCacheFailed(e, cacheDir);
        }
    }

    @Override
    public boolean exist(String uri) {
        if (closed) {
            return false;
        }

        // 这里有些特殊，只有当没有尝试安装过的时候才会尝试安装，这是由于目前此方法只在helper中用到，而Helper对性能要求较高
        if (!checkCache(false)) {
            reinstallDiskCache();
        }

        return cache != null && cache.exist(uriToDiskCacheKey(uri));
    }

    @Override
    public Entry get(String uri) {
        if (closed) {
            return null;
        }

        if (!checkCache(true)) {
            reinstallDiskCache();
        }

        DiskLruCache.SimpleSnapshot snapshot = null;
        try {
            snapshot = cache != null ? cache.getSimpleSnapshot(uriToDiskCacheKey(uri)) : null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return snapshot != null ? new LruDiskCacheEntry(uri, snapshot) : null;
    }

    @Override
    public Editor edit(String uri) {
        if (closed) {
            return null;
        }

        if (!checkCache(true)) {
            reinstallDiskCache();
        }

        DiskLruCache.Editor diskEditor = null;
        try {
            diskEditor = cache != null ? cache.edit(uriToDiskCacheKey(uri)) : null;
        } catch (IOException e) {
            e.printStackTrace();
            // 发生异常的时候（比如SD卡被拔出，导致不能使用），尝试重建DiskLruCache，能显著提高遇错恢复能力
            reinstallDiskCache();
            try {
                diskEditor = cache != null ? cache.edit(uriToDiskCacheKey(uri)) : null;
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return diskEditor != null ? new LruDiskCacheEditor(diskEditor) : null;
    }

    @Override
    public File getCacheDir() {
        return cacheDir;
    }

    @Override
    public long getMaxSize() {
        return maxSize;
    }

    @Override
    public String uriToDiskCacheKey(String uri) {
        // 由于DiskLruCache会在文件名后面加序列号，因此这里不用再处理了
//        if (SketchUtils.checkSuffix(uri, ".apk")) {
//            uri += ".icon";
//        }
        try {
            return URLEncoder.encode(uri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public long getSize() {
        if (closed) {
            return 0;
        }

        return cache != null ? cache.size() : 0;
    }

    @Override
    public void clear() {
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

        reinstallDiskCache();
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public void close() {
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

        if (editLockMap != null) {
            editLockMap.clear();
            editLockMap = null;
        }
    }

    @Override
    public synchronized ReentrantLock getEditLock(String key) {
        if (closed) {
            return null;
        }

        if (key == null) {
            return null;
        }
        if (editLockMap == null) {
            synchronized (LruDiskCache.this) {
                if (editLockMap == null) {
                    editLockMap = Collections.synchronizedMap(new WeakHashMap<String, ReentrantLock>());
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

    @Override
    public String getIdentifier() {
        return appendIdentifier(new StringBuilder()).toString();
    }

    @Override
    public StringBuilder appendIdentifier(StringBuilder builder) {
        return builder.append(logName)
                .append("(")
                .append("maxSize").append("=").append(Formatter.formatFileSize(context, maxSize))
                .append(",")
                .append("appVersionCode").append("=").append(appVersionCode)
                .append(",")
                .append("cacheDir").append("=").append(cacheDir.getPath())
                .append(")");
    }

    public static class LruDiskCacheEntry implements Entry {
        private String uri;
        private DiskLruCache.SimpleSnapshot snapshot;

        public LruDiskCacheEntry(String uri, DiskLruCache.SimpleSnapshot snapshot) {
            this.uri = uri;
            this.snapshot = snapshot;
        }

        @Override
        public InputStream newInputStream() throws IOException {
            return snapshot.newInputStream(0);
        }

        @Override
        public File getFile() {
            return snapshot.getFile(0);
        }

        @Override
        public String getUri() {
            return uri;
        }

        @Override
        public boolean delete() {
            try {
                snapshot.getDiskLruCache().remove(snapshot.getKey());
                return true;
            } catch (IOException e) {
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
        public void commit() throws IOException, DiskLruCache.EditorChangedException {
            diskEditor.commit();
        }

        @Override
        public void abort() {
            try {
                diskEditor.abort();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DiskLruCache.EditorChangedException e) {
                e.printStackTrace();
            }
        }
    }
}
