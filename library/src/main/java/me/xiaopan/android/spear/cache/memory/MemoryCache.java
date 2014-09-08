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

/**
 * 内存缓存器
 */
public interface MemoryCache {
	/**
	 * 放进去一个位图
	 * @param key 键
	 * @param bitmapDrawable 值
	 */
	public void put(String key, BitmapDrawable bitmapDrawable);
	
	/**
	 * 根据给定的key获取位图
	 * @param key 键
	 */
	public BitmapDrawable get(String key);
	
	/**
	 * 根据给定的key删除位图
	 * @param key 键
	 */
	public BitmapDrawable remove(String key);
	
	/**
	 * 清除内存缓存
	 */
	public void clear();
}