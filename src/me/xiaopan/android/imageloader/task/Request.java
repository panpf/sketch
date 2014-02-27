/*
 * Copyright 2014 Peng fei Pan
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

package me.xiaopan.android.imageloader.task;

import me.xiaopan.android.imageloader.util.ImageSize;

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
	
	private ImageViewAware imageViewAware;
	private ImageLoadListener imageLoadListener;
	
	private Request() {}

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
	
	public ImageViewAware getImageViewAware() {
		return imageViewAware;
	}

	public void setImageViewAware(ImageViewAware imageViewAware) {
		this.imageViewAware = imageViewAware;
	}

	public ImageLoadListener getImageLoadListener() {
		return imageLoadListener;
	}

	public void setImageLoadListener(ImageLoadListener imageLoadListener) {
		this.imageLoadListener = imageLoadListener;
	}

	public static class Builder{
		Request request;
		
		public Builder(){
			request = new Request();
		}
		
		/**
		 * 设置ID
		 * @param id ID
		 */
		public Builder setId(String id) {
			request.setId(id);
			return this;
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
		public Builder setImageUri(String imageUri) {
			request.setImageUri(imageUri);
			return this;
		}

		/**
		 * 设置名称，用于在输出log时区分不同的请求
		 * @param name
		 */
		public Builder setName(String name) {
			request.setName(name);
			return this;
		}
		
		/**
		 * 设置加载选项
		 * @param options
		 */
		public Builder setOptions(Options options) {
			request.setOptions(options);
			return this;
		}
		
		/**
		 * 设置目标尺寸
		 * @param targetSize
		 */
		public Builder setTargetSize(ImageSize targetSize) {
			request.setTargetSize(targetSize);
			return this;
		}
		
		public Builder setImageViewAware(ImageViewAware imageViewAware) {
			request.setImageViewAware(imageViewAware);
			return this;
		}

		public Builder setImageLoadListener(ImageLoadListener imageLoadListener) {
			request.setImageLoadListener(imageLoadListener);
			return this;
		}
		
		public Request build(){
			return request;
		}
	}
}
