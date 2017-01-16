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

import me.xiaopan.sketch.SLogType;
import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.drawable.RefBitmap;
import me.xiaopan.sketch.util.LruCache;
import me.xiaopan.sketch.util.SketchUtils;

public class LruMemoryCache implements MemoryCache {
    private final LruCache<String, RefBitmap> cache;
    protected String logName = "LruMemoryCache";
    private Context context;
    private boolean closed;
    private boolean disabled;

    public LruMemoryCache(Context context, int maxSize) {
        context = context.getApplicationContext();
        this.context = context;
        this.cache = new RefBitmapLruCache(this, maxSize);
    }

    @Override
    public synchronized void put(String key, RefBitmap refBitmap) {
        if (closed) {
            return;
        }

        if (disabled) {
            SLog.w(SLogType.CACHE, logName, "Disabled. Unable put, key=%s", key);
            return;
        }

        if (cache.get(key) != null) {
            SLog.w(SLogType.CACHE, logName, String.format("Exist. key=%s", key));
            return;
        }

        int oldCacheSize = 0;
        if (SLogType.CACHE.isEnabled()) {
            oldCacheSize = cache.size();
        }
        cache.put(key, refBitmap);
        SLog.i(SLogType.CACHE, logName, "put. beforeCacheSize=%s. %s. afterCacheSize=%s",
                Formatter.formatFileSize(context, oldCacheSize), refBitmap.getInfo(),
                Formatter.formatFileSize(context, cache.size()));
    }

    @Override
    public synchronized RefBitmap get(String key) {
        if (closed) {
            return null;
        }

        if (disabled) {
            SLog.w(SLogType.CACHE, logName, "Disabled. Unable get, key=%s", key);
            return null;
        }

        return cache.get(key);
    }

    @Override
    public synchronized RefBitmap remove(String key) {
        if (closed) {
            return null;
        }

        if (disabled) {
            SLog.w(SLogType.CACHE, logName, "Disabled. Unable remove, key=%s", key);
            return null;
        }

        RefBitmap refBitmap = cache.remove(key);
        SLog.i(SLogType.CACHE, logName, "remove. memoryCacheSize: %s",
                Formatter.formatFileSize(context, cache.size()));
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
        SLog.w(SLogType.CACHE, logName, "trimMemory. level=%s, released: %s",
                SketchUtils.getTrimLevelName(level), Formatter.formatFileSize(context, releasedSize));
    }

    @Override
    public boolean isDisabled() {
        return disabled;
    }

    @Override
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
        if (disabled) {
            SLog.w(SLogType.CACHE, logName, "setDisabled. %s", true);
        } else {
            SLog.i(SLogType.CACHE, logName, "setDisabled. %s", false);
        }
    }

    @Override
    public synchronized void clear() {
        if (closed) {
            return;
        }

        SLog.w(SLogType.CACHE, logName, "clear. before size: %s",
                Formatter.formatFileSize(context, cache.size()));
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

    @Override
    public String getKey() {
        return String.format("%s(maxSize=%s)", logName, Formatter.formatFileSize(context, getMaxSize()));
    }

    private static class RefBitmapLruCache extends LruCache<String, RefBitmap> {
        private LruMemoryCache cache;

        public RefBitmapLruCache(LruMemoryCache cache, int maxSize) {
            super(maxSize);
            this.cache = cache;
        }

        @Override
        public RefBitmap put(String key, RefBitmap refBitmap) {
            refBitmap.setIsCached(cache.logName + ":put", true);
            return super.put(key, refBitmap);
        }

        @Override
        public int sizeOf(String key, RefBitmap refBitmap) {
            int bitmapSize = refBitmap.getByteCount();
            return bitmapSize == 0 ? 1 : bitmapSize;
        }

        @Override
        protected void entryRemoved(boolean evicted, String key, RefBitmap oldRefBitmap, RefBitmap newRefBitmap) {
            oldRefBitmap.setIsCached(cache.logName + ":entryRemoved", false);
        }
    }
}