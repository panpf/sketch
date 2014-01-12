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

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
	 * 获取可再度使用的Bitmap
	 * @param options
	 * @return
	 */
	public Bitmap getBitmapFromReusableSet(BitmapFactory.Options options);
	
	/**
	 * 根据给定的key删除位图
	 * @param key
	 * @return
	 */
	public BitmapDrawable remove(String key);
	
	/**
	 * 清除内存缓存
	 */
	public void clearMenoryCache();
	
	/**
	 * 清除磁盘缓存
	 * @param context
	 */
	public void clearDiskCache(Context context);
	
	/**
	 * 清除所有缓存
	 * @param context
	 */
	public void clearAllCache(Context context);
	
	/**
	 * 获取缓存文件
	 * @param context
	 * @param fileName
	 * @return
	 */
	public File getDiskCacheFile(Context context, String fileName);
	
	/**
	 * 获取磁盘缓存目录
	 * @param diskCacheDirectory
	 */
	public void setDiskCacheDirectory(File diskCacheDirectory);
	
	/**
	 * 设置磁盘缓存最大容量
	 * @param diskCacheMaxSize
	 */
	public void setDiskCacheMaxSize(long diskCacheMaxSize);
	
	/**
	 * 设置缓存文件长度
	 * @param file
	 * @param fileLength
	 * @throws IOException
	 */
	public void setCacheFileLength(File file, long fileLength) throws IOException;
}