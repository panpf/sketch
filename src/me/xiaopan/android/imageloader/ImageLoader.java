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

package me.xiaopan.android.imageloader;

import java.io.File;

import me.xiaopan.android.imageloader.task.AsyncDrawable;
import me.xiaopan.android.imageloader.task.BitmapLoadTask;
import me.xiaopan.android.imageloader.task.ImageLoadListener;
import me.xiaopan.android.imageloader.task.ImageViewAware;
import me.xiaopan.android.imageloader.task.Options;
import me.xiaopan.android.imageloader.task.Request;
import me.xiaopan.android.imageloader.task.assets.AssetsBitmapLoadTask;
import me.xiaopan.android.imageloader.task.content.ContentBitmapLoadTask;
import me.xiaopan.android.imageloader.task.drawable.DrawableBitmapLoadTask;
import me.xiaopan.android.imageloader.task.file.FileBitmapLoadTask;
import me.xiaopan.android.imageloader.task.http.HttpBitmapLoadTask;
import me.xiaopan.android.imageloader.util.ImageSize;
import me.xiaopan.android.imageloader.util.ImageSizeUtils;
import me.xiaopan.android.imageloader.util.Scheme;
import me.xiaopan.android.imageloader.util.Utils;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

/**
 * 图片加载器，可以从网络或者本地加载图片，并且支持自动清除缓存
 */
public class ImageLoader{
	private static final String LOG_NAME= ImageLoader.class.getSimpleName();
	private Configuration configuration;	//配置
	
	/**
	 * 初始化
	 * @param context
	 */
	public void init(Context context){
		if(configuration != null){
			throw new IllegalStateException("Have been initialized");
		}
		configuration = new Configuration(context);
	}
	
	/**
	 * 实例持有器
	 */
	private static final class ImageLoaderInstanceHolder{
		private static ImageLoader instance = new ImageLoader();
	}
	
	/**
	 * 获取图片加载器的实例
	 * @return 图片加载器的实例
	 */
	public static ImageLoader getInstance(){
		return ImageLoaderInstanceHolder.instance;
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
	 * @param options 加载选项
	 * @param imageLoadListener 加载监听器
	 */
	public final void display(String imageUri, ImageView imageView, Options options, ImageLoadListener imageLoadListener){
		if(imageView == null){
			if(getConfiguration().isDebugMode()){
				Log.e(getConfiguration().getLogTag(), "imageView不能为null");
			}
			return;
		}

		if(options == null){
			options = getConfiguration().getDefaultOptions();
		}
		
		if(imageLoadListener != null){
			imageLoadListener.onStarted(imageUri, imageView);
		}
		
		if(Utils.isEmpty(imageUri)){
			imageView.setImageDrawable(options.getEmptyDrawable());
			if(getConfiguration().isDebugMode()){
				Log.e(getConfiguration().getLogTag(), new StringBuffer(LOG_NAME).append("：").append("imageUri不能为null或空").append("；").append("ImageViewCode").append("=").append(imageView.hashCode()).toString());
			}
			if(imageLoadListener != null){
				imageLoadListener.onFailed(imageUri, imageView);
			}
			return;
		}
		
		Scheme scheme = Scheme.ofUri(imageUri);
		if(scheme == Scheme.UNKNOWN){
			imageView.setImageDrawable(options.getFailureDrawable());
			if(getConfiguration().isDebugMode()){
				Log.e(getConfiguration().getLogTag(), new StringBuffer(LOG_NAME).append("：").append("未知的协议格式").append("URI").append("=").append(imageUri).append("；").append("ImageViewCode").append("=").append(imageView.hashCode()).toString());
			}
			if(imageLoadListener != null){
				imageLoadListener.onFailed(imageUri, imageView);
			}
			return;
		}
		
		//计算目标尺寸并创建请求
		ImageViewAware imageViewAware = new ImageViewAware(imageView);
		ImageSize targetSize = ImageSizeUtils.defineTargetSizeForView(imageViewAware, options.getImageMaxSize());
		String requestId = Utils.createId(Utils.encodeUrl(imageUri), targetSize, options.getBitmapProcessor());
		String requestName = imageUri;
		
		Request request = new Request.Builder()
			.setId(requestId)
			.setName(requestName)
			.setImageUri(imageUri)
			.setTargetSize(targetSize)
			.setOptions(options)
			.setImageViewAware(imageViewAware)
			.setImageLoadListener(imageLoadListener)
			.build();
		
		//尝试显示
		if(request.getOptions().isEnableMenoryCache()){
			BitmapDrawable cacheDrawable = getConfiguration().getBitmapCacher().get(request.getId());
			if(cacheDrawable != null){
				imageView.setImageDrawable(cacheDrawable);
				if(getConfiguration().isDebugMode()){
					Log.i(configuration.getLogTag(), new StringBuffer(LOG_NAME).append("：").append("显示成功 - 内存").append("；").append("ImageViewCode").append("=").append(imageView.hashCode()).append("；").append(request.getName()).toString());
				}
				if(imageLoadListener != null){
					imageLoadListener.onComplete(imageUri, imageView, cacheDrawable);
				}
				return;
			}
		}
		
		//尝试取消正在加载的任务
		if(BitmapLoadTask.cancelPotentialBitmapLoadTask(request, imageView, getConfiguration())){
			//创建新的加载任务
			BitmapLoadTask bitmapLoadTask = null;
			switch(scheme){
				case HTTP :
				case HTTPS : 
					bitmapLoadTask = new HttpBitmapLoadTask(request, getConfiguration().getTaskExecutor().getLockByRequestId(request.getId()), configuration);
					break;
				case FILE : 
					bitmapLoadTask = new FileBitmapLoadTask(request, getConfiguration().getTaskExecutor().getLockByRequestId(request.getId()), configuration);
					break;
				case ASSETS : 
					bitmapLoadTask = new AssetsBitmapLoadTask(request, getConfiguration().getTaskExecutor().getLockByRequestId(request.getId()), configuration);
					break;
				case CONTENT : 
					bitmapLoadTask = new ContentBitmapLoadTask(request, getConfiguration().getTaskExecutor().getLockByRequestId(request.getId()), configuration);
					break;
				case DRAWABLE : 
					bitmapLoadTask = new DrawableBitmapLoadTask(request, getConfiguration().getTaskExecutor().getLockByRequestId(request.getId()), configuration);
					break;
				default:
					break;
			}
			
			if(bitmapLoadTask != null){
				//显示默认图片
				BitmapDrawable loadingBitmapDrawable = request.getOptions().getLoadingDrawable();
				AsyncDrawable loadingAsyncDrawable = new AsyncDrawable(getConfiguration().getContext().getResources(), loadingBitmapDrawable != null?loadingBitmapDrawable.getBitmap():null, bitmapLoadTask);
				imageView.setImageDrawable(loadingAsyncDrawable);
				
				//提交任务
				getConfiguration().getTaskExecutor().execute(bitmapLoadTask, getConfiguration());
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
	 * @param optionsName 加载选项的名称，你通过getConfiguration().putOptions()方法放进去的Options在这里指定一样的名称就可以直接使用
	 * @param imageLoadListener 加载监听器
	 */
	public final void display(String imageUri, ImageView imageView, Enum<?> optionsName, ImageLoadListener imageLoadListener){
		display(imageUri, imageView, getConfiguration().getOptions(optionsName), imageLoadListener);
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
	 * @param options 加载选项
	 */
	public void display(String imageUri, ImageView imageView, Options options){
		display(imageUri, imageView, options, null);
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
	 * @param optionsName 加载选项的名称，你通过getConfiguration().putOptions()方法放进去的Options在这里指定一样的名称就可以直接使用
	 */
	public void display(String imageUri, ImageView imageView, Enum<?> optionsName){
		display(imageUri, imageView, getConfiguration().getOptions(optionsName), null);
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
	 * @param imageLoadListener 加载监听器
	 */
	public void display(String imageUri, ImageView imageView, ImageLoadListener imageLoadListener){
		display(imageUri, imageView, getConfiguration().getDefaultOptions(), imageLoadListener);
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
	 */
	public void display(String imageUri, ImageView imageView){
		display(imageUri, imageView, getConfiguration().getDefaultOptions(), null);
	}
	
	/**
	 * 显示图片
	 * @param imageFile 图片文件
	 * @param imageView 显示图片的视图
	 * @param options 加载选项
	 * @param imageLoadListener 加载监听器
	 */
	public void display(File imageFile, ImageView imageView, Options options, ImageLoadListener imageLoadListener){
		display(Uri.fromFile(imageFile).toString(), imageView, options, imageLoadListener);
	}
	
	/**
	 * 显示图片
	 * @param imageFile 图片文件
	 * @param imageView 显示图片的视图
	 * @param optionsName 加载选项的名称，你通过getConfiguration().putOptions()方法放进去的Options在这里指定一样的名称就可以直接使用
	 * @param imageLoadListener 加载监听器
	 */
	public void display(File imageFile, ImageView imageView, Enum<?> optionsName, ImageLoadListener imageLoadListener){
		display(Uri.fromFile(imageFile).toString(), imageView, getConfiguration().getOptions(optionsName), imageLoadListener);
	}
	
	/**
	 * 显示图片
	 * @param imageFile 图片文件
	 * @param imageView 显示图片的视图
	 * @param options 加载选项
	 */
	public void display(File imageFile, ImageView imageView, Options options){
		display(Uri.fromFile(imageFile).toString(), imageView, options, null);
	}
	
	/**
	 * 显示图片
	 * @param imageFile 图片文件
	 * @param imageView 显示图片的视图
	 * @param optionsName 加载选项的名称，你通过getConfiguration().putOptions()方法放进去的Options在这里指定一样的名称就可以直接使用
	 */
	public void display(File imageFile, ImageView imageView, Enum<?> optionsName){
		display(Uri.fromFile(imageFile).toString(), imageView, getConfiguration().getOptions(optionsName), null);
	}
	
	/**
	 * 显示图片
	 * @param imageFile 图片文件
	 * @param imageView 显示图片的视图
	 * @param imageLoadListener 加载监听器
	 */
	public void display(File imageFile, ImageView imageView, ImageLoadListener imageLoadListener){
		display(Uri.fromFile(imageFile).toString(), imageView, getConfiguration().getDefaultOptions(), imageLoadListener);
	}
	
	/**
	 * 显示图片
	 * @param imageFile 图片文件
	 * @param imageView 显示图片的视图
	 */
	public void display(File imageFile, ImageView imageView){
		display(imageFile, imageView, getConfiguration().getDefaultOptions(), null);
	}
	
	/**
	 * 获取配置
	 * @return
	 */
	public Configuration getConfiguration() {
		if(configuration == null){
			throw new IllegalStateException("必须在使用ImageLoader之前调用ImageLoader.getInstance().init(Context)初始化，推荐在Application中调用");
		}
		return configuration;
	}
	
	/**
	 * 设置配置
	 * @param configuration
	 */
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
} 
