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
public class BitmapLruCacher extends BitmapDiskCacher {
	private BitmapLruCache bitmapLruCache;
	
	public BitmapLruCacher(){
		bitmapLruCache = new BitmapLruCache((int) (Runtime.getRuntime().maxMemory()/8));
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
	public synchronized Bitmap getBitmapFromReusableSet(Options options) {
		return bitmapLruCache.getBitmapFromReusableSet(options);
	}

	@Override
	public synchronized BitmapDrawable remove(String key) {
		return bitmapLruCache.remove(key);
	}

	@Override
	public synchronized void clearMenoryCache() {
		bitmapLruCache.evictAll();
	}
}