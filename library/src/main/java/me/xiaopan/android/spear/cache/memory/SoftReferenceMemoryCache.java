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

import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 使用软引用的方式来缓存位图
 */
public class SoftReferenceMemoryCache implements MemoryCache{
	private ConcurrentHashMap<String, SoftReference<BitmapDrawable>> bitmapCacheMap;
	
	public SoftReferenceMemoryCache(){
		bitmapCacheMap = new ConcurrentHashMap<String, SoftReference<BitmapDrawable>>();
	}
	
	@Override
	public synchronized void put(String key, BitmapDrawable bitmapDrawable) {
		bitmapCacheMap.put(key, new SoftReference<BitmapDrawable>(bitmapDrawable));
	}

	@Override
	public synchronized BitmapDrawable get(String key) {
		SoftReference<BitmapDrawable> bitmapReference = bitmapCacheMap.get(key);
		if(bitmapReference != null){
			BitmapDrawable bitmapDrawable = bitmapReference.get();
			if(bitmapDrawable == null){
				bitmapCacheMap.remove(key);
			}
			return bitmapDrawable;
		}else{
			return null;
		}
	}

	@Override
	public synchronized BitmapDrawable remove(String key) {
		SoftReference<BitmapDrawable> bitmapReference = bitmapCacheMap.remove(key);
		return bitmapReference != null?bitmapReference.get():null;
	}

	@Override
	public synchronized void clear() {
		bitmapCacheMap.clear();
	}
}