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

import java.io.File;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * 加载请求
 */
class LoadRequest {
	private String id;	//ID
	private String name;	//名称，用于在输出log时区分不同的请求
	
	private String url;	//图片下载地址
	private File cacheFile;	//本地缓存文件
	
	private Options options;	//加载选项
	private ImageView imageView;	//显示图片的视图
	
	private boolean local;	//本地

	private Bitmap resultBitmap;	//加载结果Bitmap
	
	private LoadRequest(){
	}
	
	/**
	 * 获取ID
	 * @return ID
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * 设置ID
	 * @param id ID
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * 获取图片视图
	 * @return 图片视图
	 */
	public ImageView getImageView() {
		return imageView;
	}
	
	/**
	 * 设置图片视图
	 * @param imageView 图片视图
	 */
	public void setImageView(ImageView imageView) {
		this.imageView = imageView;
	}

	/**
	 * 获取图片下载地址
	 * @return 图片下载地址
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * 设置图片下载地址
	 * @param url 图片下载地址
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * 获取本地缓存文件
	 * @return 本地缓存文件
	 */
	public File getCacheFile() {
		return cacheFile;
	}

	/**
	 * 设置本地缓存文件
	 * @param cacheFile 本地缓存文件
	 */
	public void setCacheFile(File cacheFile) {
		this.cacheFile = cacheFile;
	}
	
	/**
	 * 获取加载选项
	 * @return
	 */
	public Options getOptions() {
		return options;
	}

	/**
	 * 设置加载选项
	 * @param options
	 */
	public void setOptions(Options options) {
		this.options = options;
	}

	public Bitmap getResultBitmap() {
		return resultBitmap;
	}

	public void setResultBitmap(Bitmap resultBitmap) {
		this.resultBitmap = resultBitmap;
	}

	/**
	 * 获取名称，用于在输出log时区分不同的请求
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置名称，用于在输出log时区分不同的请求
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * 是否是本地的
	 * @return
	 */
	public boolean isLocal() {
		return local;
	}

	/**
	 * 设置是否是本地的
	 * @param local
	 */
	public void setLocal(boolean local) {
		this.local = local;
	}

	/**
	 * 加载请求创建器
	 */
	public static class Builder{
		private LoadRequest loadRequest;
		
		public Builder(String id, String name, String url, ImageView imageView, Options options){
			loadRequest = new LoadRequest();
			setId(id);
			setName(name);
			setUrl(url);
			setImageView(imageView);
			setOptions(options);
		}
		
		/**
		 * 设置ID
		 * @param id ID
		 */
		public Builder setId(String id) {
			loadRequest.setId(id);
			return this;
		}
		
		/**
		 * 设置图片视图
		 * @param imageView 图片视图
		 */
		public Builder setImageView(ImageView imageView) {
			loadRequest.setImageView(imageView);
			return this;
		}

		/**
		 * 设置图片下载地址
		 * @param url 图片下载地址
		 */
		public Builder setUrl(String url) {
			loadRequest.setUrl(url);
			return this;
		}

		/**
		 * 设置本地缓存文件
		 * @param cacheFile 本地缓存文件
		 */
		public Builder setCacheFile(File cacheFile) {
			loadRequest.setCacheFile(cacheFile);
			return this;
		}
		
		/**
		 * 设置加载选项
		 * @param options
		 */
		public Builder setOptions(Options options) {
			loadRequest.setOptions(options);
			return this;
		}

		/**
		 * 设置名称，用于在输出log时区分不同的请求
		 * @param name
		 * @return
		 */
		public Builder setName(String name) {
			loadRequest.setName(name);
			return this;
		}

		/**
		 * 设置是否是本地的
		 * @param local
		 */
		public Builder setLocal(boolean local) {
			loadRequest.setLocal(local);
			return this;
		}
		
		public LoadRequest create(){
			return loadRequest;
		}
	}
}
