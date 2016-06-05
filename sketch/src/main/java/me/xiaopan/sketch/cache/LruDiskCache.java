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
import me.xiaopan.sketch.util.SketchUtils;

public class LruDiskCache implements DiskCache {
    protected String logName = "LruDiskCache";

    private int maxSize;
    private int appVersionCode;
    private File cacheDir;
    private Context context;
    private DiskLruCache cache;
    private Configuration configuration;
    private Map<String, ReentrantLock> diskCacheEditorLocks;

    public LruDiskCache(Context context, Configuration configuration, int appVersionCode, int maxSize) {
        this.context = context;
        this.maxSize = maxSize;
        this.appVersionCode = appVersionCode;
        this.configuration = configuration;
    }

    /**
     * 安装磁盘缓存，当缓存目录不存在的时候回再次安装
     */
    private synchronized void installDiskCache(boolean force) {
        // 好好的就不安装了
        if (!force && cache != null && cacheDir != null && cacheDir.exists()) {
            return;
        }

        if (cache != null) {
            try {
                cache.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            cache = null;
        }

        try {
            cacheDir = SketchUtils.getCacheDir(context, DISK_CACHE_DIR_NAME, true, DISK_CACHE_RESERVED_SPACE_SIZE, true, true, 10);
        } catch (SketchUtils.NoSpaceException e) {
            e.printStackTrace();
            cacheDir = e.dir;

            if (configuration.getErrorCallback() != null) {
                configuration.getErrorCallback().onInstallDiskCacheFailed(e, cacheDir);
            }
            return;
        }

        try {
            cache = DiskLruCache.open(cacheDir, appVersionCode, 1, maxSize);
        } catch (IOException e) {
            e.printStackTrace();

            if (configuration.getErrorCallback() != null) {
                configuration.getErrorCallback().onInstallDiskCacheFailed(e, cacheDir);
            }
        }
    }

    @Override
    public boolean exist(String uri) {
        // 这里有些特殊，只有当没有尝试安装过的时候才会尝试安装，这是由于目前此方法只在helper中用到，而Helper对性能要求较高
        if(cacheDir == null){
            installDiskCache(false);
        }

        return cache != null && cache.exist(uriToDiskCacheKey(uri));
    }

    @Override
    public Entry get(String uri) {
        installDiskCache(false);

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
        installDiskCache(false);

        DiskLruCache.Editor diskEditor = null;
        try {
            diskEditor = cache != null ? cache.edit(uriToDiskCacheKey(uri)) : null;
        } catch (IOException e) {
            e.printStackTrace();
            // 发生异常的时候（比如SD卡被拔出，导致不能使用），尝试重建DiskLruCache，能显著提高遇错恢复能力
            installDiskCache(true);
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
        installDiskCache(false);
        return cacheDir;
    }

    @Override
    public long getMaxSize() {
        return maxSize;
    }

    @Override
    public String uriToDiskCacheKey(String uri) {
        if (SketchUtils.checkSuffix(uri, ".apk")) {
            uri += ".icon";
        }
        try {
            return URLEncoder.encode(uri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public long getSize() {
        return cache != null ? cache.size() : 0;
    }

    @Override
    public void clear() {
        if (cache != null) {
            try {
                cache.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        installDiskCache(true);
    }

    @Override
    public void close() {
        if (cache != null) {
            try {
                cache.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public synchronized ReentrantLock getEditorLock(String key) {
        if (key == null) {
            return null;
        }
        if (diskCacheEditorLocks == null) {
            synchronized (LruDiskCache.this) {
                if (diskCacheEditorLocks == null) {
                    diskCacheEditorLocks = Collections.synchronizedMap(new WeakHashMap<String, ReentrantLock>());
                }
            }
        }
        ReentrantLock lock = diskCacheEditorLocks.get(key);
        if (lock == null) {
            lock = new ReentrantLock();
            diskCacheEditorLocks.put(key, lock);
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
        public void abort()  {
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
