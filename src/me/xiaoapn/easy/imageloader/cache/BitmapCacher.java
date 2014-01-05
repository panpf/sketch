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

import android.graphics.drawable.BitmapDrawable;

/**
 * 位图缓存适配器
 */
public interface BitmapCacher {
	/**
	 * 放进去一个位图
	 * @param key
	 * @param bitmapDrawable
	 * @return
	 */
	public void put(String key, BitmapDrawable bitmapDrawable);
	
	/**
	 * 根据给定的key获取位图
	 * @param key
	 * @return
	 */
	public BitmapDrawable get(String key);
	
	/**
	 * 根据给定的key删除位图
	 * @param key
	 * @return
	 */
	public BitmapDrawable remove(String key);
	
	/**
	 * 清除所有的位图
	 */
	public void clear();
}