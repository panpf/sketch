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

import me.xiaoapn.easy.imageloader.decode.BitmapLoader;
import me.xiaoapn.easy.imageloader.display.BitmapDisplayer;

/**
 * 加载选项
 */
public class Options implements Cloneable{
	private int loadingImageResource = -1;	//正在加载时显示的图片的资源ID
	private int loadFailureImageResource = -1;	//加载失败时显示的图片的资源ID
	private int maxRetryCount = -1;	//最大重试次数
	private int cachePeriodOfValidity;	//缓存有效期，单位毫秒
	private boolean isCacheInMemory;	//是否每次加载图片的时候先从内存中去找，并且加载完成后将图片缓存在内存中
	private boolean isCacheInLocal;	//是否需要将图片缓存到本地
	private String cacheDirectory;	//缓存目录
	private BitmapLoader bitmapLoader;	//位图加载器
	private BitmapDisplayer bitmapDisplayer;	//位图显示器
	
	private Options(){
		
	}
	
	/**
	 * 获取正在加载时显示的图片的资源ID
	 * @return 正在加载时显示的图片的资源ID
	 */
	public int getLoadingImageResource() {
		return loadingImageResource;
	}
	
	/**
	 * 设置正在加载时显示的图片的资源ID
	 * @param loadingImageResource 正在加载时显示的图片的资源ID
	 */
	public void setLoadingImageResource(int loadingImageResource) {
		this.loadingImageResource = loadingImageResource;
	}
	
	/**
	 * 获取加载失败时显示的图片的资源ID
	 * @return 加载失败时显示的图片的资源ID
	 */
	public int getLoadFailureImageResource() {
		return loadFailureImageResource;
	}

	/**
	 * 设置加载失败时显示的图片的资源ID
	 * @param loadFailureImageResource 加载失败时显示的图片的资源ID
	 */
	public void setLoadFailureImageResource(int loadFailureImageResource) {
		this.loadFailureImageResource = loadFailureImageResource;
	}
	
	/**
	 * 获取缓存有效期，单位毫秒
	 * @return 
	 */
	public int getCachePeriodOfValidity() {
		return cachePeriodOfValidity;
	}

	/**
	 * 设置缓存有效期，单位毫秒
	 * @param cachePeriodOfValidity
	 */
	public void setCachePeriodOfValidity(int cachePeriodOfValidity) {
		this.cachePeriodOfValidity = cachePeriodOfValidity;
	}

	/**
	 * 判断是否每次加载图片的时候先从内存中去找，并且加载完成后将图片缓存在内存中
	 * @return 
	 */
	public boolean isCacheInMemory() {
		return isCacheInMemory;
	}
	
	/**
	 * 设置是否每次加载图片的时候先从内存中去找，并且加载完成后将图片缓存在内存中
	 * @param isCacheInMemory 是否每次加载图片的时候先从内存中去找，并且加载完成后将图片缓存在内存中
	 */
	public void setCacheInMemory(boolean isCacheInMemory) {
		this.isCacheInMemory = isCacheInMemory;
	}

	/**
	 * 判断是否缓存到本地
	 * @return 是否缓存到本地
	 */
	public boolean isCacheInLocal() {
		return isCacheInLocal;
	}

	/**
	 * 设置是否缓存到本地
	 * @param isCacheInLocal 是否缓存到本地
	 */
	public void setCacheInLocal(boolean isCacheInLocal) {
		this.isCacheInLocal = isCacheInLocal;
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
	public void setMaxRetryCount(int maxRetryCount) {
		this.maxRetryCount = maxRetryCount;
	}

	/**
	 * 获取缓存目录
	 * @return 缓存目录
	 */
	public String getCacheDirectory() {
		return cacheDirectory;
	}

	/**
	 * 设置缓存目录
	 * @param cacheDirectory 缓存目录
	 */
	public void setCacheDirectory(String cacheDirectory) {
		this.cacheDirectory = cacheDirectory;
	}

	/**
	 * 获取位图加载器
	 * @return 位图加载器
	 */
	public BitmapLoader getBitmapLoader() {
		return bitmapLoader;
	}

	/**
	 * 设置位图加载器
	 * @param bitmapHandler 位图加载器
	 */
	public void setBitmapLoader(BitmapLoader bitmapHandler) {
		this.bitmapLoader = bitmapHandler;
	}
	
	/**
	 * 获取位图显示器
	 * @return
	 */
	public BitmapDisplayer getBitmapDisplayer() {
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
	 * 加载选项创建器
	 */
	public static class Builder{
		private Options options = null;
		
		public Builder(){
			options = new Options();
		}
		
		/**
		 * 设置正在加载时显示的图片的资源ID
		 * @param loadingImageResource 正在加载时显示的图片的资源ID
		 */
		public Builder setLoadingImageResource(int loadingImageResource) {
			options.setLoadingImageResource(loadingImageResource);
			return this;
		}
		
		/**
		 * 设置加载失败时显示的图片的资源ID
		 * @param loadFailureImageResource 加载失败时显示的图片的资源ID
		 */
		public Builder setLoadFailureImageResource(int loadFailureImageResource) {
			options.setLoadFailureImageResource(loadFailureImageResource);
			return this;
		}

		/**
		 * 设置缓存有效期，单位毫秒
		 * @param cachePeriodOfValidity
		 */
		public Builder setCachePeriodOfValidity(int cachePeriodOfValidity) {
			options.setCachePeriodOfValidity(cachePeriodOfValidity);
			return this;
		}

		/**
		 * 设置是否每次加载图片的时候先从内存中去找，并且加载完成后将图片缓存在内存中
		 * @param isCachedInMemory 是否每次加载图片的时候先从内存中去找，并且加载完成后将图片缓存在内存中
		 */
		public Builder setCachedInMemory(boolean isCachedInMemory) {
			options.setCacheInMemory(isCachedInMemory);
			return this;
		}

		/**
		 * 设置是否缓存到本地
		 * @param isCacheInLocal 是否缓存到本地
		 */
		public Builder setCacheInLocal(boolean isCacheInLocal) {
			options.setCacheInLocal(isCacheInLocal);
			return this;
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
		 * 设置缓存目录
		 * @param cacheDirectory 缓存目录
		 */
		public Builder setCacheDirectory(String cacheDirectory) {
			options.setCacheDirectory(cacheDirectory);
			return this;
		}

		/**
		 * 设置位图加载器
		 * @param bitmapHandler 位图加载器
		 */
		public Builder setBitmapLoader(BitmapLoader bitmapHandler) {
			options.setBitmapLoader(bitmapHandler);
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
		public Options create(){
			return options;
		}
	}

	/**
	 * 将当前Options拷贝一份
	 * @return
	 */
	public Options copy(){
		return new Options.Builder()
		.setBitmapLoader(getBitmapLoader())
		.setCachedInMemory(isCacheInMemory())
		.setCacheDirectory(getCacheDirectory())
		.setCacheInLocal(isCacheInLocal())
		.setLoadFailureImageResource(getLoadFailureImageResource())
		.setLoadingImageResource(getLoadingImageResource())
		.setMaxRetryCount(getMaxRetryCount())
		.setBitmapDisplayer(getBitmapDisplayer())
		.create();
	}
}
