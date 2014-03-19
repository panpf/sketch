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

package me.xiaopan.android.imageloader;

import java.io.File;

import me.xiaopan.android.imageloader.task.BitmapLoadTask;
import me.xiaopan.android.imageloader.task.display.AssetsBitmapDisplayTask;
import me.xiaopan.android.imageloader.task.display.AsyncDrawable;
import me.xiaopan.android.imageloader.task.display.ContentBitmapDisplayTask;
import me.xiaopan.android.imageloader.task.display.DisplayOptions;
import me.xiaopan.android.imageloader.task.display.DisplayRequest;
import me.xiaopan.android.imageloader.task.display.DisplayRequest.DisplayListener;
import me.xiaopan.android.imageloader.task.display.DrawableBitmapDisplayTask;
import me.xiaopan.android.imageloader.task.display.FileBitmapDisplayTask;
import me.xiaopan.android.imageloader.task.display.HttpBitmapDisplayTask;
import me.xiaopan.android.imageloader.task.display.ImageViewHolder;
import me.xiaopan.android.imageloader.util.ImageLoaderUtils;
import me.xiaopan.android.imageloader.util.ImageSize;
import me.xiaopan.android.imageloader.util.ImageSizeUtils;
import me.xiaopan.android.imageloader.util.Scheme;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

/**
 * 图片加载器，可以从网络或者本地加载图片，并且支持自动清除缓存
 */
public class ImageLoader{
	public static final String LOG_TAG= ImageLoader.class.getSimpleName();
	private static ImageLoader instance; 
	private Configuration configuration;	//配置
	
	public ImageLoader(Context context){
		configuration = new Configuration(context);
	}
	
	/**
	 * 获取图片加载器的实例
	 * @param context
	 * @return 图片加载器的实例
	 */
	public static ImageLoader getInstance(Context context){
		if(instance == null){
			instance = new ImageLoader(context);
		}
		return instance;
	}
	
	/**
	 * 显示图片
	 * @param imageUri 图片Uri，支持以下5种Uri
	 * <blockquote>String imageUri = "http://site.com/image.png"; // from Web
	 * <br>String imageUri = "file:///mnt/sdcard/image.png"; // from SD card
	 * <br>String imageUri = "content://media/external/audio/albumart/13"; // from content provider
	 * <br>String imageUri = "assets://image.png"; // from assets
	 * <br>String imageUri = "drawable://" + R.drawable.image; // from drawables (only images, non-9patch)
	 * </blockquote>
	 * @param imageView 显示图片的视图
	 * @param displayOptions 显示选项
	 * @param displayListener 显示监听器
	 */
	public final void display(String imageUri, ImageView imageView, DisplayOptions displayOptions, DisplayListener displayListener){
		if(imageView == null){
			if(configuration.isDebugMode()){
				Log.e(ImageLoader.LOG_TAG, "imageView不能为null");
			}
			return;
		}

		if(displayOptions == null){
			displayOptions = new DisplayOptions(configuration.getContext());
		}
		
		if(displayListener != null){
			displayListener.onStarted(imageUri, imageView);
		}
		
		if(ImageLoaderUtils.isEmpty(imageUri)){
			imageView.setImageDrawable(displayOptions.getEmptyDrawable());
			if(configuration.isDebugMode()){
				Log.e(ImageLoader.LOG_TAG, new StringBuffer(LOG_TAG).append("：").append("imageUri不能为null或空").append("；").append("ImageViewCode").append("=").append(imageView.hashCode()).toString());
			}
			if(displayListener != null){
				displayListener.onFailed(imageUri, imageView);
			}
			return;
		}
		
		Scheme scheme = Scheme.ofUri(imageUri);
		if(scheme == Scheme.UNKNOWN){
			imageView.setImageDrawable(displayOptions.getFailureDrawable());
			if(configuration.isDebugMode()){
				Log.e(ImageLoader.LOG_TAG, new StringBuffer(LOG_TAG).append("：").append("未知的协议格式").append("URI").append("=").append(imageUri).append("；").append("ImageViewCode").append("=").append(imageView.hashCode()).toString());
			}
			if(displayListener != null){
				displayListener.onFailed(imageUri, imageView);
			}
			return;
		}
		
		//计算目标尺寸并创建请求
		ImageViewHolder imageViewHolder = new ImageViewHolder(imageView);
		ImageSize targetSize = ImageSizeUtils.defineTargetSizeForView(imageViewHolder, displayOptions.getMaxImageSize());
		String requestId = ImageLoaderUtils.createId(ImageLoaderUtils.encodeUrl(imageUri), targetSize, displayOptions.getBitmapProcessor());
		String requestName = imageUri;
		
		DisplayRequest displayRequest = new DisplayRequest();
		displayRequest.setId(requestId);
		displayRequest.setName(requestName);
		displayRequest.setImageUri(imageUri);
		displayRequest.setTargetSize(targetSize);
		displayRequest.setConfiguration(configuration);
		displayRequest.setDisplayListener(displayListener);
		displayRequest.setDisplayOptions(displayOptions);
		displayRequest.setImageViewHolder(imageViewHolder);
		
		//尝试显示
		if(displayRequest.getDisplayOptions().isEnableMenoryCache()){
			BitmapDrawable cacheDrawable = configuration.getBitmapCacher().get(displayRequest.getId());
			if(cacheDrawable != null){
				imageView.setImageDrawable(cacheDrawable);
				if(configuration.isDebugMode()){
					Log.i(ImageLoader.LOG_TAG, new StringBuffer(LOG_TAG).append("：").append("显示成功 - 内存").append("；").append("ImageViewCode").append("=").append(imageView.hashCode()).append("；").append(displayRequest.getName()).toString());
				}
				if(displayListener != null){
					displayListener.onComplete(imageUri, imageView, cacheDrawable);
				}
				return;
			}
		}
		
		//尝试取消正在加载的任务
		if(BitmapLoadTask.cancelPotentialBitmapLoadTask(displayRequest, imageView)){
			//创建新的加载任务
			BitmapLoadTask bitmapLoadTask = null;
			switch(scheme){
				case HTTP :
				case HTTPS : 
					displayRequest.setReentrantLock(configuration.getTaskExecutor().getLockByRequestId(displayRequest.getId()));
					bitmapLoadTask = new HttpBitmapDisplayTask(displayRequest);
					break;
				case FILE : 
					displayRequest.setReentrantLock(configuration.getTaskExecutor().getLockByRequestId(displayRequest.getId()));
					bitmapLoadTask = new FileBitmapDisplayTask(displayRequest);
					break;
				case ASSETS : 
					displayRequest.setReentrantLock(configuration.getTaskExecutor().getLockByRequestId(displayRequest.getId()));
					bitmapLoadTask = new AssetsBitmapDisplayTask(displayRequest);
					break;
				case CONTENT : 
					displayRequest.setReentrantLock(configuration.getTaskExecutor().getLockByRequestId(displayRequest.getId()));
					bitmapLoadTask = new ContentBitmapDisplayTask(displayRequest);
					break;
				case DRAWABLE : 
					displayRequest.setReentrantLock(configuration.getTaskExecutor().getLockByRequestId(displayRequest.getId()));
					bitmapLoadTask = new DrawableBitmapDisplayTask(displayRequest);
					break;
				default:
					break;
			}
			
			if(bitmapLoadTask != null){
				//显示默认图片
				BitmapDrawable loadingBitmapDrawable = displayRequest.getDisplayOptions().getLoadingDrawable();
				AsyncDrawable loadingAsyncDrawable = new AsyncDrawable(configuration.getContext().getResources(), loadingBitmapDrawable != null?loadingBitmapDrawable.getBitmap():null, bitmapLoadTask);
				imageView.setImageDrawable(loadingAsyncDrawable);
				
				//提交任务
				configuration.getTaskExecutor().execute(bitmapLoadTask, configuration);
			}
		}
	}
	
	/**
	 * 显示图片
	 * @param imageUri 图片Uri，支持以下5种Uri
	 * <blockquote>String imageUri = "http://site.com/image.png"; // from Web
	 * <br>String imageUri = "file:///mnt/sdcard/image.png"; // from SD card
	 * <br>String imageUri = "content://media/external/audio/albumart/13"; // from content provider
	 * <br>String imageUri = "assets://image.png"; // from assets
	 * <br>String imageUri = "drawable://" + R.drawable.image; // from drawables (only images, non-9patch)
	 * </blockquote>
	 * @param imageView 显示图片的视图
	 * @param displayOptionsName 显示选项的名称，你通过configuration.putDisplayOptions()方法放进去的DisplayOptions在这里指定一样的名称就可以直接使用
	 * @param displayListener 显示监听器
	 */
	public final void display(String imageUri, ImageView imageView, Enum<?> displayOptionsName, DisplayListener displayListener){
		display(imageUri, imageView, configuration.getDisplayOptions(displayOptionsName), displayListener);
	}
	
	/**
	 * 显示图片
	 * @param imageUri 图片Uri，支持以下5种Uri
	 * <blockquote>
	 *         String imageUri = "http://site.com/image.png"; // from Web
	 * <br>String imageUri = "file:///mnt/sdcard/image.png"; // from SD card
	 * <br>String imageUri = "content://media/external/audio/albumart/13"; // from content provider
	 * <br>String imageUri = "assets://image.png"; // from assets
	 * <br>String imageUri = "drawable://" + R.drawable.image; // from drawables (only images, non-9patch)
	 * </blockquote>
	 * @param imageView 显示图片的视图
	 * @param displayOptions 显示选项
	 */
	public void display(String imageUri, ImageView imageView, DisplayOptions displayOptions){
		display(imageUri, imageView, displayOptions, null);
	}
	
	/**
	 * 显示图片
	 * @param imageUri 图片Uri，支持以下5种Uri
	 * <blockquote>
	 *         String imageUri = "http://site.com/image.png"; // from Web
	 * <br>String imageUri = "file:///mnt/sdcard/image.png"; // from SD card
	 * <br>String imageUri = "content://media/external/audio/albumart/13"; // from content provider
	 * <br>String imageUri = "assets://image.png"; // from assets
	 * <br>String imageUri = "drawable://" + R.drawable.image; // from drawables (only images, non-9patch)
	 * </blockquote>
	 * @param imageView 显示图片的视图
	 * @param displayOptionsName 显示选项的名称，你通过configuration.putDisplayOptions()方法放进去的DisplayOptions在这里指定一样的名称就可以直接使用
	 */
	public void display(String imageUri, ImageView imageView, Enum<?> displayOptionsName){
		display(imageUri, imageView, configuration.getDisplayOptions(displayOptionsName), null);
	}
	
	/**
	 * 显示图片
	 * @param imageFile 图片文件
	 * @param imageView 显示图片的视图
	 * @param displayOptions 显示选项
	 * @param displayListener 显示监听器
	 */
	public void display(File imageFile, ImageView imageView, DisplayOptions displayOptions, DisplayListener displayListener){
		display(Uri.fromFile(imageFile).toString(), imageView, displayOptions, displayListener);
	}
	
	/**
	 * 显示图片
	 * @param imageFile 图片文件
	 * @param imageView 显示图片的视图
	 * @param displayOptionsName 显示选项的名称，你通过configuration.putDisplayOptions()方法放进去的DisplayOptions在这里指定一样的名称就可以直接使用
	 * @param displayListener 显示监听器
	 */
	public void display(File imageFile, ImageView imageView, Enum<?> displayOptionsName, DisplayListener displayListener){
		display(Uri.fromFile(imageFile).toString(), imageView, configuration.getDisplayOptions(displayOptionsName), displayListener);
	}
	
	/**
	 * 显示图片
	 * @param imageFile 图片文件
	 * @param imageView 显示图片的视图
	 * @param displayOptions 显示选项
	 */
	public void display(File imageFile, ImageView imageView, DisplayOptions displayOptions){
		display(Uri.fromFile(imageFile).toString(), imageView, displayOptions, null);
	}
	
	/**
	 * 显示图片
	 * @param imageFile 图片文件
	 * @param imageView 显示图片的视图
	 * @param displayOptionsName 显示选项的名称，你通过configuration.putDisplayOptions()方法放进去的DisplayOptions在这里指定一样的名称就可以直接使用
	 */
	public void display(File imageFile, ImageView imageView, Enum<?> displayOptionsName){
		display(Uri.fromFile(imageFile).toString(), imageView, configuration.getDisplayOptions(displayOptionsName), null);
	}
	
	/**
	 * 下载
	 */
	public void download(){
		
	}
	
	/**
	 * 加载
	 */
	public void load(){
		
	}
	
	/**
	 * 获取配置
	 * @return
	 */
	public Configuration getConfiguration() {
		return configuration;
	}
} 
