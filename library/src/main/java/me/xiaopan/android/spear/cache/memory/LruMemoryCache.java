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

package me.xiaopan.android.spear.cache.memory;

import android.graphics.drawable.BitmapDrawable;

import me.xiaopan.android.spear.util.LruCache;

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
}