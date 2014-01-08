/*
 * Copyright 2013 Peng fei Pan
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

package me.xiaoapn.easy.imageloader.cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;

/**
 * 使用Lru算法来缓存位图
 */
public class BitmapLruCacher implements BitmapCacher {
	private BitmapLruCache bitmapLruCache;
	
	public BitmapLruCacher(){
		bitmapLruCache = new BitmapLruCache((int) (Runtime.getRuntime().maxMemory()/8));
	}
	
	@Override
	public void put(String key, BitmapDrawable bitmapDrawable) {
		synchronized (bitmapLruCache) {
			bitmapLruCache.put(key, bitmapDrawable);
		}
	}

	@Override
	public BitmapDrawable get(String key) {
		synchronized (bitmapLruCache) {
			return bitmapLruCache.get(key);
		}
	}

	@Override
	public Bitmap getBitmapFromReusableSet(Options options) {
		return bitmapLruCache.getBitmapFromReusableSet(options);
	}

	@Override
	public BitmapDrawable remove(String key) {
		synchronized (bitmapLruCache) {
			return bitmapLruCache.remove(key);
		}
	}

	@Override
	public void clear() {
		synchronized (bitmapLruCache) {
			bitmapLruCache.evictAll();
		}
	}
}