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

package me.xiaopan.android.spear.cache;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;

import me.xiaopan.android.spear.util.LruCache;
import me.xiaopan.android.spear.util.RecyclingBitmapDrawable;

/**
 * 使用Lru算法来缓存位图
 */
public class LruMemoryCache implements MemoryCache {
	private LruCache<String, BitmapDrawable> bitmapLruCache;

    public LruMemoryCache(LruCache<String, BitmapDrawable> bitmapLruCache) {
        this.bitmapLruCache = bitmapLruCache;
    }

	public LruMemoryCache(int maxSize){
        this(new BitmapLruCache(maxSize));
	}

    public LruMemoryCache(){
		this((int) (Runtime.getRuntime().maxMemory()/8));
	}
	
	@Override
	public synchronized void put(String key, BitmapDrawable bitmapDrawable) {
		bitmapLruCache.put(key, bitmapDrawable);
	}

	@Override
	public synchronized BitmapDrawable get(String key) {
		return bitmapLruCache.get(key);
	}

	@Override
	public synchronized BitmapDrawable remove(String key) {
		return bitmapLruCache.remove(key);
	}

	@Override
	public synchronized void clear() {
		bitmapLruCache.evictAll();
	}

    private static class BitmapLruCache extends LruCache<String, BitmapDrawable> {

        public BitmapLruCache(int maxSize) {
            super(maxSize);
        }

        @Override
        protected int sizeOf(String key, BitmapDrawable value) {
            int bitmapSize;
            Bitmap bitmap = value.getBitmap();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
                bitmapSize =  bitmap.getByteCount();
            }else{
                bitmapSize = bitmap.getRowBytes() * bitmap.getHeight();
            }
            return bitmapSize == 0 ? 1 : bitmapSize;
        }

        @Override
        protected void entryRemoved(boolean evicted, String key, BitmapDrawable oldValue, BitmapDrawable newValue) {
            if(RecyclingBitmapDrawable.class.isInstance(oldValue)){
                ((RecyclingBitmapDrawable) oldValue).setIsCached(false);
            }
        }
    }
}