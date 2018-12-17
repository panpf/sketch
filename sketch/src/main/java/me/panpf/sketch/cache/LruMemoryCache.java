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

import me.panpf.sketch.SLog;
import me.panpf.sketch.drawable.SketchRefBitmap;
import me.panpf.sketch.util.LruCache;
import me.panpf.sketch.util.SketchUtils;

/**
 * 根据最少使用规则释放缓存的内存缓存管理器
 */
public class LruMemoryCache implements MemoryCache {
    private static final String NAME = "LruMemoryCache";

    private final LruCache<String, SketchRefBitmap> cache;
    private Context context;
    private boolean closed;
    private boolean disabled;

    /**
     * 创建根据最少使用规则释放缓存的内存缓存管理器
     *
     * @param context {@link Context}
     * @param maxSize 最大容量
     */
    public LruMemoryCache(Context context, int maxSize) {
        context = context.getApplicationContext();
        this.context = context;
        this.cache = new RefBitmapLruCache(maxSize);
    }

    @Override
    public synchronized void put(@NonNull String key, @NonNull SketchRefBitmap refBitmap) {
        if (closed) {
            return;
        }

        if (disabled) {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_CACHE)) {
                SLog.d(NAME, "Disabled. Unable put, key=%s", key);
            }
            return;
        }

        if (cache.get(key) != null) {
            SLog.w(NAME, String.format("Exist. key=%s", key));
            return;
        }

        int oldCacheSize = 0;
        if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_CACHE)) {
            oldCacheSize = cache.size();
        }

        cache.put(key, refBitmap);

        if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_CACHE)) {
            SLog.d(NAME, "put. beforeCacheSize=%s. %s. afterCacheSize=%s",
                    Formatter.formatFileSize(context, oldCacheSize), refBitmap.getInfo(),
                    Formatter.formatFileSize(context, cache.size()));
        }
    }

    @Override
    public synchronized SketchRefBitmap get(@NonNull String key) {
        if (closed) {
            return null;
        }

        if (disabled) {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_CACHE)) {
                SLog.d(NAME, "Disabled. Unable get, key=%s", key);
            }
            return null;
        }

        return cache.get(key);
    }

    @Override
    public synchronized SketchRefBitmap remove(@NonNull String key) {
        if (closed) {
            return null;
        }

        if (disabled) {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_CACHE)) {
                SLog.d(NAME, "Disabled. Unable remove, key=%s", key);
            }
            return null;
        }

        SketchRefBitmap refBitmap = cache.remove(key);
        if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_CACHE)) {
            SLog.d(NAME, "remove. memoryCacheSize: %s",
                    Formatter.formatFileSize(context, cache.size()));
        }
        return refBitmap;
    }

    @Override
    public synchronized long getSize() {
        if (closed) {
            return 0;
        }

        return cache.size();
    }

    @Override
    public long getMaxSize() {
        return cache.maxSize();
    }

    @Override
    public synchronized void trimMemory(int level) {
        if (closed) {
            return;
        }

        long memoryCacheSize = getSize();

        if (level >= android.content.ComponentCallbacks2.TRIM_MEMORY_MODERATE) {
            cache.evictAll();
        } else if (level >= android.content.ComponentCallbacks2.TRIM_MEMORY_BACKGROUND) {
            cache.trimToSize(cache.maxSize() / 2);
        }

        long releasedSize = memoryCacheSize - getSize();
        SLog.w(NAME, "trimMemory. level=%s, released: %s",
                SketchUtils.getTrimLevelName(level), Formatter.formatFileSize(context, releasedSize));
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

        SLog.w(NAME, "clear. before size: %s", Formatter.formatFileSize(context, cache.size()));
        cache.evictAll();
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

        cache.evictAll();
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("%s(maxSize=%s)", NAME, Formatter.formatFileSize(context, getMaxSize()));
    }

    private static class RefBitmapLruCache extends LruCache<String, SketchRefBitmap> {

        public RefBitmapLruCache(int maxSize) {
            super(maxSize);
        }

        @Override
        public SketchRefBitmap put(String key, SketchRefBitmap refBitmap) {
            refBitmap.setIsCached(NAME + ":put", true);
            return super.put(key, refBitmap);
        }

        @Override
        public int sizeOf(String key, SketchRefBitmap refBitmap) {
            int bitmapSize = refBitmap.getByteCount();
            return bitmapSize == 0 ? 1 : bitmapSize;
        }

        @Override
        protected void entryRemoved(boolean evicted, String key, SketchRefBitmap oldRefBitmap, SketchRefBitmap newRefBitmap) {
            oldRefBitmap.setIsCached(NAME + ":entryRemoved", false);
        }
    }
}