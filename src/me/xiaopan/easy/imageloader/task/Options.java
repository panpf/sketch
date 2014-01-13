/*
 * Copyright 2013 Peng fei Pan
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.xiaopan.easy.imageloader.task;

import me.xiaopan.easy.imageloader.display.BitmapDisplayer;
import me.xiaopan.easy.imageloader.display.FadeInBitmapDisplayer;
import me.xiaopan.easy.imageloader.process.BitmapProcessor;
import me.xiaopan.easy.imageloader.util.ImageSize;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

/**
 * 加载选项
 */
public class Options{
	private int maxRetryCount;	//最大重试次数
	private int diskCachePeriodOfValidity;	//磁盘缓存有效期，单位毫秒
	private boolean enableMenoryCache;	//是否每次加载图片的时候先从内存中去找，并且加载完成后将图片缓存在内存中
	private boolean enableDiskCache;	//是否需要将图片缓存到磁盘
	private ImageSize imageMaxSize;	//图片最大尺寸
	private BitmapProcessor bitmapProcessor;	//位图处理器
	private BitmapDisplayer bitmapDisplayer;	//位图显示器
	private BitmapDrawable emptyDrawable;	//当加载地址为空时显示的图片
	private BitmapDrawable loadingDrawable;	//正在加载时显示的图片
	private BitmapDrawable failureDrawable;	//加载失败时显示的图片
	
	/**
	 * 是否将Bitmap缓存到内存中
	 * @return
	 */
	public boolean isEnableMenoryCache() {
		return enableMenoryCache;
	}

	/**
	 * 设置是否将Bitmap缓存到内存中
	 * @param enableMenoryCache
	 */
	public Options setEnableMenoryCache(boolean enableMenoryCache) {
		this.enableMenoryCache = enableMenoryCache;
		return this;
	}

	/**
	 * 是否将网络上的图片缓存到本地，缓存到本地后当内存中的Bitmap被回收就可以从本地读取，而不必再从网络上下载
	 * @return
	 */
	public boolean isEnableDiskCache() {
		return enableDiskCache;
	}

	/**
	 * 设置是否将网络上的图片缓存到本地，缓存到本地后当内存中的Bitmap被回收就可以从本地读取，而不必再从网络上下载
	 * @param enableDiskCache
	 */
	public Options setEnableDiskCache(boolean enableDiskCache) {
		this.enableDiskCache = enableDiskCache;
		return this;
	}

	/**
	 * 获取本地缓存文件的有效时间，单位毫秒
	 * @return
	 */
	public int getDiskCachePeriodOfValidity() {
		return diskCachePeriodOfValidity;
	}

	/**
	 * 设置本地缓存文件的有效时间，单位毫秒
	 * @param diskCachePeriodOfValidity
	 */
	public Options setDiskCachePeriodOfValidity(int diskCachePeriodOfValidity) {
		this.diskCachePeriodOfValidity = diskCachePeriodOfValidity;
		return this;
	}
	
	/**
	 * 获取最大重试次数
	 * @return 最大重试次数
	 */
	public int getMaxRetryCount() {
		return maxRetryCount;
	}
	
	/**
	 * 设置最大重试次数
	 * @param maxRetryCount 最大重试次数
	 */
	public Options setMaxRetryCount(int maxRetryCount) {
		this.maxRetryCount = maxRetryCount;
		return this;
	}

	/**
	 * 获取加载地址为空时显示的图片
	 * @return
	 */
	public BitmapDrawable getEmptyDrawable() {
		return emptyDrawable != null?emptyDrawable:failureDrawable;
	}

	/**
	 * 设置加载地址为空时显示的图片
	 * @param emptyDrawable
	 */
	public Options setEmptyDrawable(BitmapDrawable emptyDrawable) {
		this.emptyDrawable = emptyDrawable;
		return this;
	}
	
	/**
	 * 设置加载地址为空时显示的图片
	 * @param resources
	 * @param resId
	 */
	public Options setEmptyDrawable(Resources resources, int resId) {
		this.emptyDrawable = new BitmapDrawable(resources, BitmapFactory.decodeResource(resources, resId));
		return this;
	}

	/**
	 * 获取加载中图片
	 * @return
	 */
	public BitmapDrawable getLoadingDrawable() {
		return loadingDrawable;
	}

	/**
	 * 设置加载中图片
	 * @param loadingDrawable
	 */
	public Options setLoadingDrawable(BitmapDrawable loadingDrawable) {
		this.loadingDrawable = loadingDrawable;
		return this;
	}

	/**
	 * 设置加载中图片
	 * @param resources
	 * @param resId
	 */
	public Options setLoadingDrawable(Resources resources, int resId) {
		this.loadingDrawable = new BitmapDrawable(resources, BitmapFactory.decodeResource(resources, resId));
		return this;
	}

	/**
	 * 获取加载失败图片
	 * @return
	 */
	public BitmapDrawable getFailureDrawable() {
		return failureDrawable;
	}

	/**
	 * 设置加载失败图片
	 * @param failureDrawable
	 */
	public Options setFailureDrawable(BitmapDrawable failureDrawable) {
		this.failureDrawable = failureDrawable;
		return this;
	}
	
	/**
	 * 设置加载失败图片
	 * @param resources
	 * @param resId
	 */
	public Options setFailureDrawable(Resources resources, int resId) {
		this.failureDrawable = new BitmapDrawable(resources, BitmapFactory.decodeResource(resources, resId));
		return this;
	}
	
	/**
	 * 获取最大尺寸
	 * @return
	 */
	public ImageSize getImageMaxSize() {
		return imageMaxSize;
	}

	/**
	 * 设置最大尺寸
	 * @param imageMaxSize
	 */
	public Options setImageMaxSize(ImageSize imageMaxSize) {
		this.imageMaxSize = imageMaxSize;
		return this;
	}
	
	/**
	 * 获取位图处理器
	 * @return
	 */
	public BitmapProcessor getBitmapProcessor() {
		return bitmapProcessor;
	}

	/**
	 * 设置位图处理器
	 * @param bitmapProcessor
	 */
	public Options setBitmapProcessor(BitmapProcessor bitmapProcessor) {
		this.bitmapProcessor = bitmapProcessor;
		return this;
	}

	/**
	 * 获取位图显示器
	 * @return
	 */
	public BitmapDisplayer getBitmapDisplayer() {
		if(bitmapDisplayer == null){
			bitmapDisplayer = new FadeInBitmapDisplayer();
		}
		return bitmapDisplayer;
	}

	/**
	 * 设置位图显示器
	 * @param bitmapDisplayer
	 */
	public Options setBitmapDisplayer(BitmapDisplayer bitmapDisplayer) {
		this.bitmapDisplayer = bitmapDisplayer;
		return this;
	}

	/**
	 * 将当前Options拷贝一份
	 * @return
	 */
	public Options copy(){
		return new Options()
		.setMaxRetryCount(maxRetryCount)
		.setDiskCachePeriodOfValidity(diskCachePeriodOfValidity)
		.setEnableMenoryCache(enableMenoryCache)
		.setEnableDiskCache(enableDiskCache)
		.setBitmapProcessor(bitmapProcessor != null?bitmapProcessor.copy():null)
		.setBitmapDisplayer(bitmapDisplayer != null?bitmapDisplayer.copy():null)
		.setEmptyDrawable(emptyDrawable)
		.setFailureDrawable(failureDrawable)
		.setLoadingDrawable(loadingDrawable)
		.setImageMaxSize(imageMaxSize != null?imageMaxSize.copy():null);
	}
}
