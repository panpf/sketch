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

import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;

/**
 * 使用软引用的方式来缓存位图
 */
public class BitmapSoftReferenceCacher implements BitmapCacher {
	private ConcurrentHashMap<String, SoftReference<BitmapDrawable>> bitmapCacheMap;
	
	public BitmapSoftReferenceCacher(){
		bitmapCacheMap = new ConcurrentHashMap<String, SoftReference<BitmapDrawable>>();
	}
	
	@Override
	public void put(String key, BitmapDrawable bitmapDrawable) {
		bitmapCacheMap.put(key, new SoftReference<BitmapDrawable>(bitmapDrawable));
	}

	@Override
	public BitmapDrawable get(String key) {
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
	public Bitmap getBitmapFromReusableSet(Options options) {
		return null;
	}

	@Override
	public BitmapDrawable remove(String key) {
		SoftReference<BitmapDrawable> bitmapReference = bitmapCacheMap.remove(key);
		return bitmapReference != null?bitmapReference.get():null;
	}

	@Override
	public void clear() {
		bitmapCacheMap.clear();
	}
}