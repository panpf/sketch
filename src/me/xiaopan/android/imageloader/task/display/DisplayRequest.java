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

package me.xiaopan.android.imageloader.task.display;

import java.util.concurrent.locks.ReentrantLock;

import me.xiaopan.android.imageloader.task.TaskRequest;
import me.xiaopan.android.imageloader.util.ImageSize;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

/**
 * 显示请求
 */
public class DisplayRequest extends TaskRequest{
	private String id;	//ID
	private String imageUri;	//Uri
	private ImageSize targetSize;	//目标尺寸
	private ReentrantLock reentrantLock;	//执行锁
	private DisplayListener displayListener;	//监听器
	private DisplayOptions displayOptions;	//显示选项
	private ImageViewHolder imageViewHolder;	//ImageView持有器
	
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
	public DisplayRequest setImageUri(String imageUri) {
		this.imageUri = imageUri;
		return this;
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
	public DisplayRequest setTargetSize(ImageSize targetSize) {
		this.targetSize = targetSize;
		return this;
	}

	/**
	 * 获取同步锁
	 * @return
	 */
	public ReentrantLock getReentrantLock() {
		return reentrantLock;
	}

	/**
	 * 设置同步锁
	 * @param reentrantLock
	 */
	public void setReentrantLock(ReentrantLock reentrantLock) {
		this.reentrantLock = reentrantLock;
	}

	/**
	 * 获取显示监听器
	 * @return
	 */
	public DisplayListener getDisplayListener() {
		return displayListener;
	}

	/**
	 * 设置显示监听器
	 * @param imageLoadListener
	 */
	public DisplayRequest setDisplayListener(DisplayListener imageLoadListener) {
		this.displayListener = imageLoadListener;
		return this;
	}
	
	/**
	 * 获取显示选项
	 * @return
	 */
	public DisplayOptions getDisplayOptions() {
		return displayOptions;
	}

	/**
	 * 设置显示选项
	 * @param displayOptions
	 */
	public DisplayRequest setDisplayOptions(DisplayOptions displayOptions) {
		this.displayOptions = displayOptions;
		return this;
	}
	
	/**
	 * 获取ImageView持有器
	 * @return
	 */
	public ImageViewHolder getImageViewHolder() {
		return imageViewHolder;
	}

	/**
	 * 设置ImageView持有器
	 * @param imageViewHolder
	 */
	public void setImageViewHolder(ImageViewHolder imageViewHolder) {
		this.imageViewHolder = imageViewHolder;
	}

	/**
	 * 显示监听器
	 */
	public interface DisplayListener {
		public void onStarted(String imageUri, ImageView imageView);
		public void onFailed(String imageUri, ImageView imageView);
		public void onComplete(String imageUri, ImageView imageView, BitmapDrawable drawable);
		public void onCancelled(String imageUri, ImageView imageView);
	}
}
