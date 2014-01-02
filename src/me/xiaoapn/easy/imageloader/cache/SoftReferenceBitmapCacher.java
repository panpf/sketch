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

/**
 * 使用软引用的方式来缓存位图
 */
public class SoftReferenceBitmapCacher implements BitmapCacher {
	private ConcurrentHashMap<String, SoftReference<Bitmap>> bitmapCacheMap;
	
	public SoftReferenceBitmapCacher(){
		bitmapCacheMap = new ConcurrentHashMap<String, SoftReference<Bitmap>>();
	}
	
	@Override
	public void put(String key, Bitmap bitmap) {
		bitmapCacheMap.put(key, new SoftReference<Bitmap>(bitmap));
	}

	@Override
	public Bitmap get(String key) {
		SoftReference<Bitmap> bitmapReference = bitmapCacheMap.get(key);
		if(bitmapReference != null){
			Bitmap bitmap = bitmapReference.get();
			if(bitmap == null){
				bitmapCacheMap.remove(key);
			}
			return bitmap;
		}else{
			return null;
		}
	}

	@Override
	public Bitmap remove(String key) {
		SoftReference<Bitmap> bitmapReference = bitmapCacheMap.remove(key);
		return bitmapReference != null?bitmapReference.get():null;
	}

	@Override
	public void clear() {
		bitmapCacheMap.clear();
	}
}