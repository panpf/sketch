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

package me.xiaoapn.easy.imageloader;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * 使用Lru算法来缓存位图
 */
public class BitmapLruCacher implements BitmapCacher {
	private LruCache<String, Bitmap> bitmapLruCache;
	
	public BitmapLruCacher(){
		bitmapLruCache = new LruCache<String, Bitmap> ((int) (Runtime.getRuntime().maxMemory()/1024/8)){
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes() * value.getHeight() / 1024;
			}
		};
	}
	
	@Override
	public void put(String key, Bitmap bitmap) {
		bitmapLruCache.put(key, bitmap);
	}

	@Override
	public Bitmap get(String key) {
		return bitmapLruCache.get(key);
	}

	@Override
	public Bitmap remove(String key) {
		return bitmapLruCache.remove(key);
	}

	@Override
	public void clear() {
		bitmapLruCache.evictAll();
	}
}