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
import android.graphics.drawable.Drawable;
import android.text.format.Formatter;
import android.util.Log;

import me.xiaopan.android.spear.RecycleDrawable;
import me.xiaopan.android.spear.Spear;
import me.xiaopan.android.spear.util.LruCache;

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
		if(!(value instanceof RecycleDrawable)){
			throw new IllegalArgumentException("drawable must be implemented RecycleDrawable");
		}
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
	public long getSize() {
		return drawableLruCache.size();
	}

	@Override
	public long getMaxSize() {
		return drawableLruCache.maxSize();
	}

	@Override
	public synchronized void clear() {
		if(Spear.isDebugMode()){
			Log.i(Spear.TAG, NAME + " - " + "clear" + " - "  + "before clean MemoryCacheSize: "+ Formatter.formatFileSize(context, drawableLruCache.size()));
		}
		drawableLruCache.evictAll();
	}

	private class DrawableLruCache extends LruCache<String, Drawable> {

		public DrawableLruCache(int maxSize) {
			super(maxSize);
		}

		@Override
		public Drawable put(String key, Drawable value) {
			((RecycleDrawable) value).setIsCached(NAME+":put", true);
			return super.put(key, value);
		}

		@Override
		protected int sizeOf(String key, Drawable value) {
			int bitmapSize = ((RecycleDrawable) value).getSize();
			return bitmapSize == 0 ? 1 : bitmapSize;
		}

		@Override
		protected void entryRemoved(boolean evicted, String key, Drawable oldValue, Drawable newValue) {
			((RecycleDrawable) oldValue).setIsCached(NAME+":entryRemoved", false);
		}
	}
}