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

package me.xiaopan.android.imageloader.task;

import me.xiaopan.android.imageloader.display.BitmapDisplayer;
import me.xiaopan.android.imageloader.display.FadeInBitmapDisplayer;
import me.xiaopan.android.imageloader.process.BitmapProcessor;
import me.xiaopan.android.imageloader.util.ImageLoaderUtils;
import me.xiaopan.android.imageloader.util.ImageSize;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView.ScaleType;

/**
 * 显示选项
 */
public class DisplayOptions{
	private Context context;	//上下文
	private int maxRetryCount;	//最大重试次数
	private int diskCachePeriodOfValidity;	//磁盘缓存有效期，单位毫秒
	private boolean enableMenoryCache;	//是否每次加载图片的时候先从内存中去找，并且加载完成后将图片缓存在内存中
	private boolean enableDiskCache;	//是否需要将图片缓存到磁盘
	private ImageSize maxImageSize;	//最大图片尺寸
	private BitmapProcessor bitmapProcessor;	//位图处理器
	private BitmapDisplayer bitmapDisplayer;	//位图显示器
	private DrawableHolder emptyDrawableHolder;	//当uri为空时显示的图片
	private DrawableHolder loadingDrawableHolder;	//当正在加载时显示的图片
	private DrawableHolder failureDrawableHolder;	//当加载失败时显示的图片
	
	public DisplayOptions(Context context) {
		this.context = context;
		this.emptyDrawableHolder = new DrawableHolder();
		this.loadingDrawableHolder = new DrawableHolder();
		this.failureDrawableHolder = new DrawableHolder();
		setEnableMenoryCache(true)
		.setEnableDiskCache(true)
		.setMaxImageSize(new ImageSize(context.getResources().getDisplayMetrics().widthPixels, context.getResources().getDisplayMetrics().heightPixels))
		.setBitmapDisplayer(new FadeInBitmapDisplayer())
		.setMaxRetryCount(2);
	}

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
	public DisplayOptions setEnableMenoryCache(boolean enableMenoryCache) {
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
	public DisplayOptions setEnableDiskCache(boolean enableDiskCache) {
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
	public DisplayOptions setDiskCachePeriodOfValidity(int diskCachePeriodOfValidity) {
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
	public DisplayOptions setMaxRetryCount(int maxRetryCount) {
		this.maxRetryCount = maxRetryCount;
		return this;
	}

	/**
	 * 获取加载地址为空时显示的图片
	 * @return
	 */
	public BitmapDrawable getEmptyDrawable() {
		if(emptyDrawableHolder.getDrawable() == null && emptyDrawableHolder.getResId() > 0){
			Bitmap bitmap = ImageLoaderUtils.bitmapCopy(BitmapFactory.decodeResource(context.getResources(), emptyDrawableHolder.getResId()));
			if(bitmap != null){
				if(bitmapProcessor != null){
					Bitmap newBitmap = bitmapProcessor.process(bitmap, ScaleType.CENTER_CROP, new ImageSize(bitmap.getWidth(), bitmap.getHeight()));
					if(newBitmap != bitmap){
						bitmap.recycle();
						bitmap = newBitmap;
					}
				}
				emptyDrawableHolder.setDrawable(new BitmapDrawable(context.getResources(), bitmap));
			}
		}
		return emptyDrawableHolder.getDrawable();
	}

	/**
	 * 设置加载地址为空时显示的图片
	 * @param resId
	 */
	public DisplayOptions setEmptyDrawableResId(int resId) {
		emptyDrawableHolder.setResId(resId);
		if(emptyDrawableHolder.getDrawable() != null){
			if(!emptyDrawableHolder.getDrawable().getBitmap().isRecycled()){
				emptyDrawableHolder.getDrawable().getBitmap().recycle();
			}
			emptyDrawableHolder.setDrawable(null);
		}
		return this;
	}

	/**
	 * 获取加载中图片
	 * @return
	 */
	public BitmapDrawable getLoadingDrawable() {
		if(loadingDrawableHolder.getDrawable() == null && loadingDrawableHolder.getResId() > 0){
			Bitmap bitmap = ImageLoaderUtils.bitmapCopy(BitmapFactory.decodeResource(context.getResources(), loadingDrawableHolder.getResId()));
			if(bitmap != null){
				if(bitmapProcessor != null){
					Bitmap newBitmap = bitmapProcessor.process(bitmap, ScaleType.CENTER_CROP, new ImageSize(bitmap.getWidth(), bitmap.getHeight()));
					if(newBitmap != bitmap){
						bitmap.recycle();
						bitmap = newBitmap;
					}
				}
				loadingDrawableHolder.setDrawable(new BitmapDrawable(context.getResources(), bitmap));
			}
		}
		return loadingDrawableHolder.getDrawable();
	}

	/**
	 * 设置加载中图片
	 * @param resId
	 */
	public DisplayOptions setLoadingDrawableResId(int resId) {
		loadingDrawableHolder.setResId(resId);
		if(loadingDrawableHolder.getDrawable() != null){
			if(!loadingDrawableHolder.getDrawable().getBitmap().isRecycled()){
				loadingDrawableHolder.getDrawable().getBitmap().recycle();
			}
			loadingDrawableHolder.setDrawable(null);
		}
		return this;
	}

	/**
	 * 获取加载失败图片
	 * @return
	 */
	public BitmapDrawable getFailureDrawable() {
		if(failureDrawableHolder.getDrawable() == null && failureDrawableHolder.getResId() > 0){
			Bitmap bitmap = ImageLoaderUtils.bitmapCopy(BitmapFactory.decodeResource(context.getResources(), failureDrawableHolder.getResId()));
			if(bitmap != null){
				if(bitmapProcessor != null){
					Bitmap newBitmap = bitmapProcessor.process(bitmap, ScaleType.CENTER_CROP, new ImageSize(bitmap.getWidth(), bitmap.getHeight()));
					if(newBitmap != bitmap){
						bitmap.recycle();
						bitmap = newBitmap;
					}
				}
				failureDrawableHolder.setDrawable(new BitmapDrawable(context.getResources(), bitmap));
			}
		}
		return failureDrawableHolder.getDrawable();
	}

	/**
	 * 设置加载失败图片
	 * @param resId
	 */
	public DisplayOptions setFailureDrawableResId(int resId) {
		failureDrawableHolder.setResId(resId);
		if(failureDrawableHolder.getDrawable() != null){
			if(!failureDrawableHolder.getDrawable().getBitmap().isRecycled()){
				failureDrawableHolder.getDrawable().getBitmap().recycle();
			}
			failureDrawableHolder.setDrawable(null);
		}
		return this;
	}
	
	/**
	 * 获取最大尺寸
	 * @return
	 */
	public ImageSize getMaxImageSize() {
		return maxImageSize;
	}

	/**
	 * 设置最大尺寸
	 * @param maxImageSize
	 */
	public DisplayOptions setMaxImageSize(ImageSize maxImageSize) {
		this.maxImageSize = maxImageSize;
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
	public DisplayOptions setBitmapProcessor(BitmapProcessor bitmapProcessor) {
		this.bitmapProcessor = bitmapProcessor;
		emptyDrawableHolder.setDrawable(null);
		loadingDrawableHolder.setDrawable(null);
		failureDrawableHolder.setDrawable(null);
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
	public DisplayOptions setBitmapDisplayer(BitmapDisplayer bitmapDisplayer) {
		this.bitmapDisplayer = bitmapDisplayer;
		return this;
	}

	/**
	 * 将当前的DisplayOptions拷贝一份
	 * @return
	 */
	public DisplayOptions copy(){
		return new DisplayOptions(context)
		.setMaxRetryCount(maxRetryCount)
		.setDiskCachePeriodOfValidity(diskCachePeriodOfValidity)
		.setEnableMenoryCache(enableMenoryCache)
		.setEnableDiskCache(enableDiskCache)
		.setBitmapProcessor(bitmapProcessor != null?bitmapProcessor.copy():null)
		.setBitmapDisplayer(bitmapDisplayer != null?bitmapDisplayer.copy():null)
		.setEmptyDrawableResId(emptyDrawableHolder.getResId())
		.setFailureDrawableResId(failureDrawableHolder.getResId())
		.setLoadingDrawableResId(loadingDrawableHolder.getResId())
		.setMaxImageSize(maxImageSize != null?maxImageSize.copy():null);
	}
}
