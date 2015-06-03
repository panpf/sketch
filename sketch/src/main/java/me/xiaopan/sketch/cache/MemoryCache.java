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

package me.xiaopan.sketch.cache;

import android.graphics.drawable.Drawable;

/**
 * 内存缓存器
 */
public interface MemoryCache {
	/**
	 * 放进去一张图片
	 * @param key 键
	 * @param value 值
	 */
	void put(String key, Drawable value);
	
	/**
	 * 根据给定的key获取图片
	 * @param key 键
	 */
	Drawable get(String key);
	
	/**
	 * 根据给定的key删除图片
	 * @param key 键
	 */
	Drawable remove(String key);

	/**
	 * 获取已用容量
	 * @return 已用容量
	 */
	long getSize();

	/**
	 * 获取最大容量
	 * @return 最大容量
	 */
	long getMaxSize();
	
	/**
	 * 清除内存缓存
	 */
	void clear();

	/**
	 * 获取标识符
	 * @return 标识符
	 */
	String getIdentifier();

	/**
	 * 追加标识符
	 */
	StringBuilder appendIdentifier(StringBuilder builder);
}