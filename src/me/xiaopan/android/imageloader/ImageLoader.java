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

import me.xiaopan.android.imageloader.task.display.AsyncDrawable;
import me.xiaopan.android.imageloader.task.display.BitmapDisplayTask;
import me.xiaopan.android.imageloader.task.display.DisplayOptions;
import me.xiaopan.android.imageloader.task.display.DisplayRequest;
import me.xiaopan.android.imageloader.task.display.DisplayRequest.DisplayListener;
import me.xiaopan.android.imageloader.task.display.ImageViewHolder;
import me.xiaopan.android.imageloader.task.download.DownloadOptions;
import me.xiaopan.android.imageloader.task.download.DownloadRequest;
import me.xiaopan.android.imageloader.task.download.DownloadRequest.DownloadListener;
import me.xiaopan.android.imageloader.task.load.LoadOptions;
import me.xiaopan.android.imageloader.task.load.LoadRequest;
import me.xiaopan.android.imageloader.util.ImageLoaderUtils;
import me.xiaopan.android.imageloader.util.ImageSize;
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
	 * @param context 用来初始化配置
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
	 * @param uri 图片Uri，支持以下5种Uri
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
	public final void display(String uri, ImageView imageView, DisplayOptions displayOptions, DisplayListener displayListener){
		//过滤掉空的ImageView
		if(imageView == null){
			throw new IllegalArgumentException("imageView不能为null");
		}

		//初始化一下
		if(displayListener != null) displayListener.onStart();
		if(displayOptions == null){
			displayOptions = new DisplayOptions(configuration.getContext());
		}
		
		//过滤掉空的URI
		if(ImageLoaderUtils.isEmpty(uri)){
			imageView.setImageDrawable(displayOptions.getEmptyDrawable());
			if(configuration.isDebugMode()) Log.e(ImageLoader.LOG_TAG, new StringBuffer(LOG_TAG).append("：").append("uri不能为null或空").append("；").append("ImageViewCode").append("=").append(imageView.hashCode()).toString());
			if(displayListener != null) displayListener.onFailure();
			return;
		}
		
		//过滤掉非法的URI
		Scheme scheme = Scheme.ofUri(uri);
		if(scheme == Scheme.UNKNOWN){
			imageView.setImageDrawable(displayOptions.getFailureDrawable());
			if(configuration.isDebugMode()) Log.e(ImageLoader.LOG_TAG, new StringBuffer(LOG_TAG).append("：").append("未知的协议格式").append("URI").append("=").append(uri).append("；").append("ImageViewCode").append("=").append(imageView.hashCode()).toString());
			if(displayListener != null) displayListener.onFailure();
			return;
		}
		
		//计算目标尺寸并创建请求
		ImageViewHolder imageViewHolder = new ImageViewHolder(imageView);
		ImageSize targetSize = ImageSize.defineTargetSizeForView(imageViewHolder, displayOptions.getMaxImageSize());
		DisplayRequest displayRequest = new DisplayRequest(ImageLoaderUtils.createId(ImageLoaderUtils.encodeUrl(uri), targetSize, displayOptions.getBitmapProcessor()), uri);
		displayRequest.setName(uri);
		
		//尝试显示
		if(displayOptions.isEnableMenoryCache()){
			BitmapDrawable cacheDrawable = configuration.getBitmapCacher().get(displayRequest.getId());
			if(cacheDrawable != null){
				imageView.setImageDrawable(cacheDrawable);
				if(configuration.isDebugMode()){
					Log.i(ImageLoader.LOG_TAG, new StringBuffer(LOG_TAG).append("：").append("显示成功 - 内存").append("；").append("ImageViewCode").append("=").append(imageView.hashCode()).append("；").append(displayRequest.getName()).toString());
				}
				if(displayListener != null){
					displayListener.onComplete(uri, imageView, cacheDrawable);
				}
				return;
			}
		}
		
		//尝试取消正在加载的请求
		if(!BitmapDisplayTask.cancelPotentialDisplayRequest(displayRequest, imageView)){
			return;
		}
		
		//初始化请求
		displayRequest.setTargetSize(targetSize);
		displayRequest.setConfiguration(configuration);
		displayRequest.setDisplayListener(displayListener);
		displayRequest.setDisplayOptions(displayOptions);
		imageViewHolder.setDisplayRequest(displayRequest);
		displayRequest.setImageViewHolder(imageViewHolder);
        displayRequest.setScaleType(imageView.getScaleType());

		//显示默认图片
		BitmapDrawable loadingBitmapDrawable = displayRequest.getDisplayOptions().getLoadingDrawable();
		imageView.setImageDrawable(new AsyncDrawable(configuration.getContext().getResources(), loadingBitmapDrawable != null?loadingBitmapDrawable.getBitmap():null, displayRequest));
		
		//执行请求
		configuration.getTaskExecutor().execute(displayRequest);
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
     * @param url 只支持HTTP
     * @param downloadOptions 配置缓存以及失败重试等选项
     * @param downloadListener 监听下载过程
     */
	public void download(String url, DownloadOptions downloadOptions, DownloadListener downloadListener){
		//过滤掉空的URI
		if(ImageLoaderUtils.isEmpty(url)){
            throw new IllegalArgumentException("url不能为null或空");
		}
		
		if(downloadListener != null){
			downloadListener.onStart();
		}
		
		//初始化下载请求
		DownloadRequest downloadRequest = new DownloadRequest(url);
		downloadRequest.setName(url);
		downloadRequest.setConfiguration(configuration);
		downloadRequest.setDownloadListener(downloadListener);
		downloadRequest.setDownloadOptions(downloadOptions != null?downloadOptions:new DownloadOptions());
		
		//执行请求
		getConfiguration().getTaskExecutor().execute(downloadRequest);
	}

    /**
     * 下载
     * @param url 只支持HTTP
     * @param downloadListener 监听下载过程
     */
	public void download(String url, DownloadListener downloadListener){
		download(url, null, downloadListener);
	}

    /**
     * 加载
     * @param uri 支持以下5种Uri
     * <blockquote>String imageUri = "http://site.com/image.png"; // from Web
     * <br>String imageUri = "file:///mnt/sdcard/image.png"; // from SD card
     * <br>String imageUri = "content://media/external/audio/albumart/13"; // from content provider
     * <br>String imageUri = "assets://image.png"; // from assets
     * <br>String imageUri = "drawable://" + R.drawable.image; // from drawables (only images, non-9patch)
     * </blockquote>
     * @param loadOptions 配置缓存、失败重试、最大尺寸以及处理器
     * @param loadListener 监听加载过程
     */
	public void load(String uri, LoadOptions loadOptions, LoadRequest.LoadListener loadListener){
		
	}
	
	/**
	 * 获取配置
	 * @return ImageLoader配置
	 */
	public Configuration getConfiguration() {
		return configuration;
	}
} 
