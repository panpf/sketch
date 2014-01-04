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

import me.xiaoapn.easy.imageloader.execute.AsyncDrawable;
import me.xiaoapn.easy.imageloader.execute.task.DisplayBitmapTask;
import me.xiaoapn.easy.imageloader.execute.task.LoadBitmapTask;
import me.xiaoapn.easy.imageloader.execute.task.Request;
import me.xiaoapn.easy.imageloader.util.GeneralUtils;
import me.xiaoapn.easy.imageloader.util.ImageSize;
import me.xiaoapn.easy.imageloader.util.ImageSizeUtils;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

/**
 * 图片加载器，可以从网络或者本地加载图片，并且支持自动清除缓存
 */
public class ImageLoader{
	private Configuration configuration;	//配置
	
	/**
	 * 初始化
	 * @param context
	 */
	public void init(Context context){
		if(configuration != null){
			throw new IllegalStateException("Have been initialized");
		}
		configuration = new Configuration.Builder(context).build();
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
	 */
	public final void display(String imageUri, ImageView imageView, Options options){
		if(options == null){
			options = getConfiguration().getDefaultOptions();
		}
		
		if(imageView == null){
			if(getConfiguration().isDebugMode()){
				Log.e(getConfiguration().getLogTag(), "imageView不能为null");
			}
			return;
		}
		if(GeneralUtils.isEmpty(imageUri)){
			imageView.setImageBitmap(options.getEmptyBitmap());
			if(getConfiguration().isDebugMode()){
				Log.e(getConfiguration().getLogTag(), "imageUrl不能为null");
			}
			return;
		}
		
		ImageSize targetSize = ImageSizeUtils.defineTargetSizeForView(imageView, options.getMaxSize().getWidth(), options.getMaxSize().getHeight());
		Request request = new Request(GeneralUtils.createId(GeneralUtils.encodeUrl(imageUri), targetSize), imageUri, imageUri, options, targetSize);
		if(!show(request, imageView) && LoadBitmapTask.cancelPotentialBitmapLoadTask(this, request, imageView)){
			LoadBitmapTask bitmapLoadTask = new LoadBitmapTask(this, request, imageView);
			imageView.setImageDrawable(new AsyncDrawable(getConfiguration().getContext().getResources(), request.getOptions().getLoadingBitmap(), bitmapLoadTask));
			getConfiguration().getTaskExecutor().execute(bitmapLoadTask.getFutureTask());
		}
	}
	
	/**
	 * 显示图片
	 * @param imageUrl 图片下载地址
	 * @param cacheFile 缓存文件
	 * @param imageView 显示图片的视图
	 */
	public void display(String imageUrl, ImageView imageView){
		display(imageUrl, imageView, null);
	}
	
	/**
	 * 显示图片
	 * @param imageFile 图片文件
	 * @param imageView 显示图片的视图
	 * @param options 加载选项
	 */
	public void display(File imageFile, ImageView imageView, Options options){
		display(Uri.fromFile(imageFile).toString(), imageView, options);
	}
	
	/**
	 * 显示图片
	 * @param imageFile 图片文件
	 * @param imageView 显示图片的视图
	 */
	public void display(File imageFile, ImageView imageView){
		display(imageFile, imageView, null);
	}
	
	/**
	 * 显示图片
	 * @param request 请求
	 * @param imageView
	 * @return true：图片缓存中有图片并且已经显示了；false：缓存中没有对应的图片，需要重新加载
	 */
	private boolean show(Request request, ImageView imageView){
		//如果不需要从缓存中读取，就直接显示默认图片并结束
		if(!request.getOptions().getCacheConfig().isCacheInMemory()){
			return false;
		}
		
		//如果内存缓存中没有对应的Bitmap，就直接显示默认图片并结束
		Bitmap cacheBitmap = getConfiguration().getBitmapCacher().get(request.getId());
		if(cacheBitmap == null){
			return false;
		}
		
		//显示图片
		getConfiguration().getHandler().post(new DisplayBitmapTask(this, imageView, cacheBitmap, request.getOptions(), request.getName(), true));
		if(getConfiguration().isDebugMode()){
			Log.d(getConfiguration().getLogTag(), "从缓存中加载："+request.getName());
		}
		return true;
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
