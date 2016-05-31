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
import android.os.Environment;
import android.text.format.Formatter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import me.xiaopan.sketch.util.DiskLruCache;
import me.xiaopan.sketch.util.SketchUtils;

public class LruDiskCache implements DiskCache {
    private static final String DISK_CACHE_DIR_NAME = "sketch";
    private static final int DISK_CACHE_MAX_SIZE = 100 * 1024 * 1024;

    protected String logName = "LruDiskCache";

    private Context context;
    private File cacheDir;
    private int maxSize;
    private int appVersionCode;

    private DiskLruCache cache;

    public LruDiskCache(Context context, int appVersionCode, int maxSize) {
        this.context = context;
        this.maxSize = maxSize;
        this.appVersionCode = appVersionCode;
        reset();
    }

    public static LruDiskCache open(Context context) {
        // appVersionCode固定死，因为当appVersionCode改变时DiskLruCache会清除旧的缓存，可我们不需要这个功能
        return new LruDiskCache(context, 1, DISK_CACHE_MAX_SIZE);
    }

    private synchronized void reset() {
        if (cache != null) {
            try {
                cache.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            cache = null;
        }

        // 缓存目录名字加上进程名字的后缀，不同的进程不同缓存目录，以兼容多进程
        String diskCacheDirName = DISK_CACHE_DIR_NAME;
        String simpleProcessName = SketchUtils.getSimpleProcessName(context);
        if (simpleProcessName != null) {
            try {
                diskCacheDirName += URLEncoder.encode(simpleProcessName, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        int count = 0;
        while (true) {
            File appCacheDir = null;
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                appCacheDir = context.getExternalCacheDir();
            }
            if (appCacheDir == null) {
                appCacheDir = context.getCacheDir();
            }

            String finalDiskCacheDirName = count == 0 ? diskCacheDirName : diskCacheDirName + count;
            cacheDir = new File(appCacheDir, finalDiskCacheDirName);

            // 尝试删除旧的缓存文件，删除依据就是缓存目录下面有没有journal文件没有的话就是旧版的缓存需要删除
            SketchUtils.deleteOldCacheFiles(cacheDir);

            try {
                cache = DiskLruCache.open(cacheDir, appVersionCode, 1, maxSize);
                break;
            } catch (IOException e) {
                e.printStackTrace();
                if (count < 10) {
                    count++;
                } else {
                    break;
                }
            }
        }
    }

    @Override
    public synchronized boolean exist(String uri) {
        // 缓存目录不存在就重建，提高自我恢复能力
        if (cache == null || cacheDir == null || !cacheDir.exists()) {
            reset();
        }

        return cache != null && cache.exist(uriToDiskCacheKey(uri));
    }

    @Override
    public synchronized Entry get(String uri) {
        // 缓存目录不存在就重建，提高自我恢复能力
        if (cache == null || cacheDir == null || !cacheDir.exists()) {
            reset();
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
    public synchronized Editor edit(String uri) {
        // 缓存目录不存在就重建，提高自我恢复能力
        if (cache == null || cacheDir == null || !cacheDir.exists()) {
            reset();
        }

        DiskLruCache.Editor diskEditor = null;
        try {
            diskEditor = cache != null ? cache.edit(uriToDiskCacheKey(uri)) : null;
        } catch (IOException e) {
            e.printStackTrace();
            // 发生异常的时候（比如SD卡被拔出，导致不能使用），尝试重建DiskLryCache，能显著提高遇错恢复能力
            reset();
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
    public synchronized long getSize() {
        return cache != null ? cache.size() : 0;
    }

    @Override
    public synchronized void clear() {
        if (cache != null) {
            try {
                cache.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        reset();
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
    public String getIdentifier() {
        return appendIdentifier(new StringBuilder()).toString();
    }

    @Override
    public StringBuilder appendIdentifier(StringBuilder builder) {
        return builder.append(logName)
                .append("(")
                .append("dir").append("=").append(cacheDir != null ? cacheDir.getPath() : null)
                .append(",")
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

    public static class LruDiskCacheEditor implements Editor{
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
        public void abort() throws IOException, DiskLruCache.EditorChangedException {
            diskEditor.abort();
        }
    }
}
