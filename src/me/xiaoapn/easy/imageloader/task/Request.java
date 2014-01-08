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

package me.xiaoapn.easy.imageloader.task;

import me.xiaoapn.easy.imageloader.Configuration;
import me.xiaoapn.easy.imageloader.Options;
import me.xiaoapn.easy.imageloader.util.GeneralUtils;
import me.xiaoapn.easy.imageloader.util.ImageSize;
import me.xiaoapn.easy.imageloader.util.Scheme;

/**
 * 加载请求
 */
public class Request {
	private String id;	//ID
	private String name;	//名称，用于在输出log时区分不同的请求
	/**
	 * 支持以下5种Uri
	 * <blockquote>String imageUri = "http://site.com/image.png"; // from Web
	 * <br>String imageUri = "file:///mnt/sdcard/image.png"; // from SD card
	 * <br>String imageUri = "content://media/external/audio/albumart/13"; // from content provider
	 * <br>String imageUri = "assets://image.png"; // from assets
	 * <br>String imageUri = "drawable://" + R.drawable.image; // from drawables (only images, non-9patch)
	 * </blockquote>
	 */
	private String imageUri;
	private Options options;	//加载选项
	private ImageSize targetSize;	//目标尺寸
	
	public Request(String id, String name, String uri, Options options, ImageSize targetSize) {
		this.id = id;
		this.imageUri = uri;
		this.name = name;
		this.options = options;
		this.targetSize = targetSize;
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
	 * 获取Uri，支持以下5种Uri
	 * <blockquote>String imageUri = "http://site.com/image.png"; // from Web
	 * <br>String imageUri = "file:///mnt/sdcard/image.png"; // from SD card
	 * <br>String imageUri = "content://media/external/audio/albumart/13"; // from content provider
	 * <br>String imageUri = "assets://image.png"; // from assets
	 * <br>String imageUri = "drawable://" + R.drawable.image; // from drawables (only images, non-9patch)
	 * </blockquote>
	 * @return
	 */
	public String getImageUri() {
		return imageUri;
	}

	/**
	 * 设置Uri，支持以下5种Uri
	 * <blockquote>String imageUri = "http://site.com/image.png"; // from Web
	 * <br>String imageUri = "file:///mnt/sdcard/image.png"; // from SD card
	 * <br>String imageUri = "content://media/external/audio/albumart/13"; // from content provider
	 * <br>String imageUri = "assets://image.png"; // from assets
	 * <br>String imageUri = "drawable://" + R.drawable.image; // from drawables (only images, non-9patch)
	 * </blockquote>
	 * @param imageUri
	 */
	public void setImageUri(String imageUri) {
		this.imageUri = imageUri;
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
	
	/**
	 * 获取目标尺寸
	 * @return
	 */
	public ImageSize getTargetSize() {
		return targetSize;
	}

	/**
	 * 设置目标尺寸
	 * @param targetSize
	 */
	public void setTargetSize(ImageSize targetSize) {
		this.targetSize = targetSize;
	}
	
	/**
	 * 是否是从网络加载的
	 * @param configuration
	 * @return
	 */
	public boolean isNetworkLoad(Configuration configuration){
		boolean result = false;
		Scheme scheme = Scheme.ofUri(imageUri);
		if(scheme == Scheme.HTTP  || scheme == Scheme.HTTPS){
			result = !GeneralUtils.isAvailableOfFile(GeneralUtils.getCacheFile(configuration, getOptions(), GeneralUtils.encodeUrl(getImageUri())), getOptions().getCacheConfig().getDiskCachePeriodOfValidity(), configuration, getName());
		}
		return result;
	}
}
