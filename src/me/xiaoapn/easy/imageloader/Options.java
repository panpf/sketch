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

package me.xiaoapn.easy.imageloader;

import me.xiaoapn.easy.imageloader.cache.CacheConfig;
import me.xiaoapn.easy.imageloader.display.BitmapDisplayer;
import me.xiaoapn.easy.imageloader.display.FadeInBitmapDisplayer;
import me.xiaoapn.easy.imageloader.util.ImageSize;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

/**
 * 加载选项
 */
public class Options{
	private int maxRetryCount;	//最大重试次数
	private BitmapDrawable emptyDrawable;	//当加载地址为空时显示的图片
	private BitmapDrawable loadingDrawable;	//正在加载时显示的图片
	private BitmapDrawable failureDrawable;	//加载失败时显示的图片
	private ImageSize maxSize;	//最大尺寸
	private CacheConfig cacheConfig;	//缓存配置
	private BitmapDisplayer bitmapDisplayer;	//位图显示器
	
	private Options(){}
	
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
	public void setMaxRetryCount(int maxRetryCount) {
		this.maxRetryCount = maxRetryCount;
	}

	/**
	 * 获取加载地址为空时显示的图片
	 * @return
	 */
	public BitmapDrawable getEmptyDrawable() {
		return emptyDrawable;
	}

	/**
	 * 设置加载地址为空时显示的图片
	 * @param emptyDrawable
	 */
	public void setEmptyDrawable(BitmapDrawable emptyDrawable) {
		this.emptyDrawable = emptyDrawable;
	}
	
	/**
	 * 设置加载地址为空时显示的图片
	 * @param resources
	 * @param resId
	 */
	public void setEmptyDrawable(Resources resources, int resId) {
		this.emptyDrawable = new BitmapDrawable(resources, BitmapFactory.decodeResource(resources, resId));
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
	public void setLoadingDrawable(BitmapDrawable loadingDrawable) {
		this.loadingDrawable = loadingDrawable;
	}

	/**
	 * 设置加载中图片
	 * @param resources
	 * @param resId
	 */
	public void setLoadingDrawable(Resources resources, int resId) {
		this.loadingDrawable = new BitmapDrawable(resources, BitmapFactory.decodeResource(resources, resId));
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
	public void setFailureDrawable(BitmapDrawable failureDrawable) {
		this.failureDrawable = failureDrawable;
	}
	
	/**
	 * 设置加载失败图片
	 * @param resources
	 * @param resId
	 */
	public void setFailureDrawable(Resources resources, int resId) {
		this.failureDrawable = new BitmapDrawable(resources, BitmapFactory.decodeResource(resources, resId));
	}
	
	/**
	 * 获取最大尺寸
	 * @return
	 */
	public ImageSize getMaxSize() {
		return maxSize;
	}

	/**
	 * 设置最大尺寸
	 * @param maxSize
	 */
	public void setMaxSize(ImageSize maxSize) {
		this.maxSize = maxSize;
	}
	
	/**
	 * 获取缓存配置
	 * @return
	 */
	public CacheConfig getCacheConfig() {
		if(cacheConfig == null){
			cacheConfig = new CacheConfig.Builder().build();
		}
		return cacheConfig;
	}

	/**
	 * 设置缓存配置
	 * @param cacheConfig
	 */
	public void setCacheConfig(CacheConfig cacheConfig) {
		this.cacheConfig = cacheConfig;
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
	public void setBitmapDisplayer(BitmapDisplayer bitmapDisplayer) {
		this.bitmapDisplayer = bitmapDisplayer;
	}

	/**
	 * 将当前Options拷贝一份
	 * @return
	 */
	public Options copy(){
		return new Options.Builder()
		.setBitmapDisplayer(bitmapDisplayer)
		.setCacheConfig(cacheConfig.copy())
		.setEmptyDrawable(emptyDrawable)
		.setFailureDrawable(failureDrawable)
		.setLoadingDrawable(loadingDrawable)
		.setMaxSize(maxSize.copy())
		.setMaxRetryCount(maxRetryCount)
		.build();
	}
	
	/**
	 * 加载选项创建器
	 */
	public static class Builder{
		private Options options = null;
		
		public Builder(){
			options = new Options();
		}
		
		/**
		 * 设置最大重试次数
		 * @param maxRetryCount 最大重试次数
		 */
		public Builder setMaxRetryCount(int maxRetryCount) {
			options.setMaxRetryCount(maxRetryCount);
			return this;
		}

		/**
		 * 设置缓存配置
		 * @param cacheConfig
		 */
		public Builder setCacheConfig(CacheConfig cacheConfig) {
			options.setCacheConfig(cacheConfig);
			return this;
		}
		
		/**
		 * 设置加载地址为空时显示的图片
		 * @param emptyDrawable
		 */
		public Builder setEmptyDrawable(BitmapDrawable emptyDrawable) {
			options.setEmptyDrawable(emptyDrawable);
			return this;
		}
		
		/**
		 * 设置加载地址为空时显示的图片
		 * @param resources
		 * @param resId
		 */
		public Builder setEmptyDrawable(Resources resources, int resId) {
			options.setEmptyDrawable(resources, resId);
			return this;
		}

		/**
		 * 设置加载中图片
		 * @param loadingDrawable
		 */
		public Builder setLoadingDrawable(BitmapDrawable loadingDrawable) {
			options.setLoadingDrawable(loadingDrawable);
			return this;
		}

		/**
		 * 设置加载中图片
		 * @param resources
		 * @param resId
		 */
		public Builder setLoadingDrawable(Resources resources, int resId) {
			options.setLoadingDrawable(resources, resId);
			return this;
		}

		/**
		 * 设置加载失败图片
		 * @param failureDrawable
		 */
		public Builder setFailureDrawable(BitmapDrawable failureDrawable) {
			options.setFailureDrawable(failureDrawable);
			return this;
		}
		
		/**
		 * 设置加载失败图片
		 * @param resources
		 * @param resId
		 */
		public Builder setFailureDrawable(Resources resources, int resId) {
			options.setFailureDrawable(resources, resId);
			return this;
		}
		
		/**
		 * 设置最大尺寸
		 * @param maxSize
		 */
		public Builder setMaxSize(ImageSize maxSize) {
			options.setMaxSize(maxSize);
			return this;
		}
		
		/**
		 * 设置位图显示器
		 * @param bitmapDisplayer
		 */
		public Builder setBitmapDisplayer(BitmapDisplayer bitmapDisplayer) {
			options.setBitmapDisplayer(bitmapDisplayer);
			return this;
		}
		
		/**
		 * 创建
		 * @return
		 */
		public Options build(){
			return options;
		}
	}
}
