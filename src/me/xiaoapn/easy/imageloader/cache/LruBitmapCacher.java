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

import me.xiaoapn.easy.imageloader.util.GeneralUtils;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.util.LruCache;

/**
 * 使用Lru算法来缓存位图
 */
public class LruBitmapCacher implements BitmapCacher {
	private LruCache<String, BitmapDrawable> bitmapLruCache;
	
	public LruBitmapCacher(){
		bitmapLruCache = new LruCache<String, BitmapDrawable> ((int) (Runtime.getRuntime().maxMemory()/8)){
			@Override
			protected int sizeOf(String key, BitmapDrawable value) {
				final int bitmapSize = getBitmapSize(value); 
				return bitmapSize == 0 ? 1 : bitmapSize;
			}

		    /**
		     * Get the size in bytes of a bitmap in a BitmapDrawable.
		     * @param value
		     * @return size in bytes
		     */
		    @TargetApi(12)
		    public int getBitmapSize(BitmapDrawable value) {
		        Bitmap bitmap = value.getBitmap();
		        if (GeneralUtils.hasHoneycombMR1()) {
		            return bitmap.getByteCount();
		        }else{
		        	return bitmap.getRowBytes() * bitmap.getHeight();
		        }
		    }
		};
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