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

import android.widget.ImageView;

/**
 * 加载请求
 */
class UrlRequest extends Request{
	private String imageUrl;	//图片下载地址
	private File cacheFile;	//缓存文件
	
	public UrlRequest(String id, String name, String imageUrl, File cacheFile, ImageView imageView, Options options) {
		super(id, name, imageView, options);
		this.imageUrl = imageUrl;
		this.cacheFile = cacheFile;
	}

	/**
	 * 获取图片下载地址
	 * @return
	 */
	public String getImageUrl() {
		return imageUrl;
	}

	/**
	 * 设置图片下载地址
	 * @param imageUrl
	 */
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	/**
	 * 获取缓存文件
	 * @return 缓存文件
	 */
	public File getCacheFile() {
		return cacheFile;
	}

	/**
	 * 设置缓存文件
	 * @param cacheFile 缓存文件
	 */
	public void setCacheFile(File cacheFile) {
		this.cacheFile = cacheFile;
	}
}
