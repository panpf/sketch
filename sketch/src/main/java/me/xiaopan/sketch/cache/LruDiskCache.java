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

    private synchronized void reset() {
        if (cache != null) {
            try {
                cache.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            cache = null;
        }

        File appCacheDir = context.getExternalCacheDir();
        if (appCacheDir == null) {
            appCacheDir = context.getCacheDir();
        }

        // 缓存目录名字加上进程名字的后缀，不同的进程不同缓存目录，以兼容多进程
        String diskCacheDirName = DISK_CACHE_DIR_NAME;
        String simpleProcessName = SketchUtils.getSimpleProcessName(context);
        if (simpleProcessName != null) {
            diskCacheDirName += URLEncoder.encode(simpleProcessName);
        }

        cacheDir = new File(appCacheDir, diskCacheDirName);

        // 尝试删除旧的缓存文件，删除依据就是缓存目录下面有没有journal文件没有的话就是旧版的缓存需要删除
        SketchUtils.deleteOldCacheFiles(cacheDir);

        try {
            cache = DiskLruCache.open(cacheDir, appVersionCode, 1, maxSize);
        } catch (IOException e) {
            e.printStackTrace();

            // 换目录名称
            int count = 0;
            while (count < 10) {
                cacheDir = new File(appCacheDir, diskCacheDirName + count);
                try {
                    cache = DiskLruCache.open(cacheDir, appVersionCode, 1, maxSize);
                    break;
                } catch (IOException e1) {
                    e1.printStackTrace();
                    count++;
                }
            }

            // 试了一千次都没找到可以用的那就崩吧
            if (cache == null) {
                throw new RuntimeException("cacheDir disable. " + cacheDir.getPath());
            }
        }
    }

    @Override
    public synchronized boolean exist(String uri) {
        // 缓存目录不存在就重建，提高自我恢复能力
        if (!cacheDir.exists()) {
            reset();
        }

        return cache.exist(uriToDiskCacheKey(uri));
    }

    @Override
    public synchronized Entry get(String uri) {
        // 缓存目录不存在就重建，提高自我恢复能力
        if (!cacheDir.exists()) {
            reset();
        }

        DiskLruCache.SimpleSnapshot snapshot = null;
        try {
            snapshot = cache.getSimpleSnapshot(uriToDiskCacheKey(uri));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return snapshot != null ? new LruDiskCacheEntry(uri, snapshot) : null;
    }

    @Override
    public synchronized DiskLruCache.Editor edit(String uri) {
        // 缓存目录不存在就重建，提高自我恢复能力
        if (!cacheDir.exists()) {
            reset();
        }

        try {
            return cache.edit(uriToDiskCacheKey(uri));
        } catch (IOException e) {
            e.printStackTrace();
            // 发生异常的时候（比如SD卡被拔出，导致不能使用），尝试重建DiskLryCache，能显著提高遇错恢复能力
            reset();
            try {
                return cache.edit(uriToDiskCacheKey(uri));
            } catch (IOException e1) {
                e1.printStackTrace();
                return null;
            }
        }
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
        return cache.size();
    }

    @Override
    public synchronized void clear() {
        try {
            cache.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
        reset();
    }

    @Override
    public void close() {
        try {
            cache.close();
        } catch (IOException e) {
            e.printStackTrace();
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
                .append("dir").append("=").append(cacheDir.getPath())
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

    public static LruDiskCache open(Context context) {
        // appVersionCode固定死，因为当appVersionCode改变时DiskLruCache会清除旧的缓存，可我们不需要这个功能
        return new LruDiskCache(context, 1, DISK_CACHE_MAX_SIZE);
    }
}
