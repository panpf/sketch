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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.format.Formatter;
import android.util.Log;

import me.xiaopan.android.spear.RecycleDrawable;
import me.xiaopan.android.spear.Spear;
import me.xiaopan.android.spear.util.LruCache;
import pl.droidsonroids.gif.GifDrawable;

/**
 * 使用Lru算法来缓存位图
 */
public class LruMemoryCache implements MemoryCache {
	private static final String NAME = "LruMemoryCache";
	private Context context;
	private LruCache<String, Drawable> drawableLruCache;

	public LruMemoryCache(Context context, int maxSize){
		this.context = context;
        this.drawableLruCache = new DrawableLruCache(maxSize);
		if(Spear.isDebugMode()){
			Log.i(Spear.TAG, NAME + " - " + "MemoryCacheMaxSize: "+ Formatter.formatFileSize(context, maxSize));
		}
	}

    public LruMemoryCache(Context context){
		this(context, (int) (Runtime.getRuntime().maxMemory()/8));
	}
	
	@Override
	public synchronized void put(String key, Drawable value) {
		drawableLruCache.put(key, value);
		if(Spear.isDebugMode()){
			Log.i(Spear.TAG, NAME + " - " + "put" + " - " + "MemoryCacheSize: "+ Formatter.formatFileSize(context, drawableLruCache.size()));
		}
	}

	@Override
	public synchronized Drawable get(String key) {
		return drawableLruCache.get(key);
	}

	@Override
	public synchronized Drawable remove(String key) {
		Drawable drawable = drawableLruCache.remove(key);
		if(Spear.isDebugMode()){
			Log.i(Spear.TAG, NAME + " - " + "remove" + " - "  + "MemoryCacheSize: "+ Formatter.formatFileSize(context, drawableLruCache.size()));
		}
		return drawable;
	}

	@Override
	public synchronized void clear() {
		drawableLruCache.evictAll();
		if(Spear.isDebugMode()){
			Log.i(Spear.TAG, NAME + " - " + "clear" + " - "  + "MemoryCacheSize: "+ Formatter.formatFileSize(context, drawableLruCache.size()));
		}
	}

	private class DrawableLruCache extends LruCache<String, Drawable> {

		public DrawableLruCache(int maxSize) {
			super(maxSize);
		}

		@Override
		public Drawable put(String key, Drawable value) {
			if(value instanceof RecycleDrawable){
				((RecycleDrawable) value).setIsCached(NAME+" - put", true);
			}
			return super.put(key, value);
		}

		@Override
		protected int sizeOf(String key, Drawable value) {
			int bitmapSize;
			if(value instanceof BitmapDrawable){
				Bitmap bitmap = ((BitmapDrawable) value).getBitmap();
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
					bitmapSize = bitmap.getAllocationByteCount();
				} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
					bitmapSize =  bitmap.getByteCount();
				}else{
					bitmapSize = bitmap.getRowBytes() * bitmap.getHeight();
				}
			}else if(value instanceof GifDrawable){
				return (int) ((GifDrawable) value).getAllocationByteCount();
			}else{
				return 0;
			}
			return bitmapSize == 0 ? 1 : bitmapSize;
		}

		@Override
		protected void entryRemoved(boolean evicted, String key, Drawable oldValue, Drawable newValue) {
			if(RecycleDrawable.class.isInstance(oldValue)){
				((RecycleDrawable) oldValue).setIsCached(NAME+" - entryRemoved", false);
			}
		}
	}
}