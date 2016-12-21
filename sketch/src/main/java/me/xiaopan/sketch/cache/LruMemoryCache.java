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
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantLock;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.drawable.RefBitmap;
import me.xiaopan.sketch.util.LruCache;
import me.xiaopan.sketch.util.SketchUtils;

public class LruMemoryCache implements MemoryCache {
    private final LruCache<String, RefBitmap> cache;
    protected String logName = "LruMemoryCache";
    private Context context;
    private Map<String, ReentrantLock> editLockMap;
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
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, logName + ". Disabled. Unable put, key=" + key);
            }
            return;
        }

        if (cache.get(key) != null) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, logName + ". Exist. key=" + key);
            }
            return;
        }

        int oldCacheSize = 0;
        if (Sketch.isDebugMode()) {
            oldCacheSize = cache.size();
        }
        cache.put(key, refBitmap);
        if (Sketch.isDebugMode()) {
            int newCacheSize = cache.size();
            Log.i(Sketch.TAG, SketchUtils.concat(logName,
                    ". put",
                    ". beforeCacheSize=", Formatter.formatFileSize(context, oldCacheSize),
                    ". ", refBitmap.getInfo(),
                    ". afterCacheSize=", Formatter.formatFileSize(context, newCacheSize)));
        }
    }

    @Override
    public synchronized RefBitmap get(String key) {
        if (closed) {
            return null;
        }

        if (disabled) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, logName + ". Disabled. Unable get, key=" + key);
            }
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
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, logName + ". Disabled. Unable remove, key=" + key);
            }
            return null;
        }

        RefBitmap refBitmap = cache.remove(key);
        if (Sketch.isDebugMode()) {
            Log.i(Sketch.TAG, SketchUtils.concat(logName,
                    ". remove",
                    ". memoryCacheSize: ", Formatter.formatFileSize(context, cache.size())));
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
        if (Sketch.isDebugMode()) {
            Log.w(Sketch.TAG, SketchUtils.concat(logName,
                    ". trimMemory",
                    ". level=", SketchUtils.getTrimLevelName(level),
                    ", released: ", Formatter.formatFileSize(context, releasedSize)));
        }
    }

    @Override
    public boolean isDisabled() {
        return disabled;
    }

    @Override
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
        if (Sketch.isDebugMode()) {
            if (disabled) {
                Log.w(Sketch.TAG, SketchUtils.concat(logName, ". setDisabled. " + true));
            } else {
                Log.i(Sketch.TAG, SketchUtils.concat(logName, ". setDisabled. " + false));
            }
        }
    }

    @Override
    public synchronized void clear() {
        if (closed) {
            return;
        }

        if (Sketch.isDebugMode()) {
            Log.w(Sketch.TAG, SketchUtils.concat(logName,
                    ". clear",
                    ". before clean memoryCacheSize: ", Formatter.formatFileSize(context, cache.size())));
        }
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
            synchronized (LruMemoryCache.this) {
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
        return appendIdentifier(null, new StringBuilder()).toString();
    }

    @Override
    public StringBuilder appendIdentifier(String join, StringBuilder builder) {
        if (!TextUtils.isEmpty(join)) {
            builder.append(join);
        }
        return builder.append(logName)
                .append("(")
                .append("maxSize=").append(Formatter.formatFileSize(context, getMaxSize()))
                .append(")");
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