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

import me.xiaoapn.easy.imageloader.task.AsyncDrawable;
import me.xiaoapn.easy.imageloader.task.BitmapLoadTask;
import me.xiaoapn.easy.imageloader.task.ImageViewAware;
import me.xiaoapn.easy.imageloader.task.Options;
import me.xiaoapn.easy.imageloader.task.Request;
import me.xiaoapn.easy.imageloader.util.ImageSize;
import me.xiaoapn.easy.imageloader.util.ImageSizeUtils;
import me.xiaoapn.easy.imageloader.util.Scheme;
import me.xiaoapn.easy.imageloader.util.Utils;
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
		if(imageView == null){
			if(getConfiguration().isDebugMode()){
				Log.e(getConfiguration().getLogTag(), "imageView不能为null");
			}
			return;
		}

		if(options == null){
			options = getConfiguration().getDefaultOptions();
		}
		
		if(Utils.isEmpty(imageUri)){
			imageView.setImageDrawable(options.getEmptyDrawable());
			if(getConfiguration().isDebugMode()){
				Log.e(getConfiguration().getLogTag(), new StringBuffer(LOG_NAME).append("：").append("imageUri不能为null或空").append("；").append("ImageViewCode").append("=").append(imageView.hashCode()).toString());
			}
			return;
		}
		
		if(Scheme.ofUri(imageUri) == Scheme.UNKNOWN){
			imageView.setImageDrawable(options.getFailureDrawable());
			if(getConfiguration().isDebugMode()){
				Log.e(getConfiguration().getLogTag(), new StringBuffer(LOG_NAME).append("：").append("未知的协议格式").append("URI").append("=").append(imageUri).append("；").append("ImageViewCode").append("=").append(imageView.hashCode()).toString());
			}
			return;
		}
		
		//计算目标尺寸并创建请求
		ImageViewAware imageViewAware = new ImageViewAware(imageView);
		ImageSize targetSize = ImageSizeUtils.defineTargetSizeForView(imageViewAware, options.getMaxSize());
		Request request = new Request(Utils.createId(Utils.encodeUrl(imageUri), targetSize, options.getBitmapProcessor()), imageUri, imageUri, options, targetSize);
		
		//尝试显示
		if(request.getOptions().getCacheConfig().isCacheInMemory()){
			BitmapDrawable cacheBitmap = getConfiguration().getBitmapCacher().get(request.getId());
			if(cacheBitmap != null){
				imageView.setImageDrawable(cacheBitmap);
				if(getConfiguration().isDebugMode()){
					Log.i(configuration.getLogTag(), new StringBuffer(LOG_NAME).append("：").append("显示成功 - 内存").append("；").append("ImageViewCode").append("=").append(imageView.hashCode()).append("；").append(request.getName()).toString());
				}
				return;
			}
		}
		
		//尝试取消正在加载的任务
		if(BitmapLoadTask.cancelPotentialBitmapLoadTask(request, imageView, getConfiguration())){
			//创建加载任务
			BitmapLoadTask bitmapLoadTask = new BitmapLoadTask(request, imageViewAware, getConfiguration().getTaskExecutor().getLockById(request.getId()), getConfiguration());
			
			//显示默认图片
			BitmapDrawable loadingBitmapDrawable = request.getOptions().getLoadingDrawable();
			AsyncDrawable loadingAsyncDrawable = new AsyncDrawable(getConfiguration().getContext().getResources(), loadingBitmapDrawable != null?loadingBitmapDrawable.getBitmap():null, bitmapLoadTask);
			imageView.setImageDrawable(loadingAsyncDrawable);
			
			//提交任务
			getConfiguration().getTaskExecutor().execute(bitmapLoadTask, getConfiguration());
		}
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
	 * @param cacheFile 缓存文件
	 * @param imageView 显示图片的视图
	 */
	public void display(String imageUri, ImageView imageView){
		display(imageUri, imageView, null);
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
