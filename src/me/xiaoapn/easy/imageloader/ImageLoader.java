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
import java.util.HashSet;
import java.util.Set;

import me.xiaoapn.easy.imageloader.execute.DisplayBitmapTask;
import me.xiaoapn.easy.imageloader.execute.FileRequest;
import me.xiaoapn.easy.imageloader.execute.FileRequestExecuteRunnable;
import me.xiaoapn.easy.imageloader.execute.Request;
import me.xiaoapn.easy.imageloader.execute.UrlRequest;
import me.xiaoapn.easy.imageloader.execute.UrlRequestExecuteRunnable;
import me.xiaoapn.easy.imageloader.util.GeneralUtils;
import me.xiaoapn.easy.imageloader.util.ImageSize;
import me.xiaoapn.easy.imageloader.util.ImageSizeUtils;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

/**
 * 图片加载器，可以从网络或者本地加载图片，并且支持自动清除缓存
 */
public class ImageLoader{
	private Set<String> loadingIdSet;	//正在加载的Url列表，用来防止同一个URL被重复加载
	private Configuration configuration;	//配置
	private Set<ImageView> loadingImageViewSet;	//图片视图集合，这个集合里的每个尚未加载完成的视图身上都会携带有他要显示的图片的地址，当每一个图片加载完成之后都会在这个列表中遍历找到所有携带有这个这个图片的地址的视图，并把图片显示到这个视图上
	
	/**
	 * 创建图片加载器
	 */
	public ImageLoader(){
		loadingImageViewSet = new HashSet<ImageView>();//初始化图片视图集合
		loadingIdSet = new HashSet<String>();//初始化加载中URL集合
	}
	
	/**
	 * 初始化
	 * @param context
	 */
	public void init(Context context){
		if(configuration != null){
			throw new IllegalStateException("Have been initialized");
		}
		configuration = new Configuration.Builder(context).create();
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
	 * @param imageUrl 图片下载地址，如果本地缓存文件不存在将从网络获取
	 * @param imageView 显示图片的视图
	 * @param options 加载选项
	 */
	public final void display(String imageUrl, ImageView imageView, Options options){
		if(GeneralUtils.isEmpty(imageUrl)){
			exceptionHandle(imageView, options);
			if(getConfiguration().isDebugMode()){
				Log.e(getConfiguration().getLogTag(), "imageUrl不能为null");
			}
			return;
		}
		if(imageView == null){
			exceptionHandle(imageView, options);
			if(getConfiguration().isDebugMode()){
				Log.e(getConfiguration().getLogTag(), "imageView不能为null");
			}
			return;
		}
		
		if(options == null){
			options = getConfiguration().getDefaultOptions();
		}
		
		ImageSize targetSize = ImageSizeUtils.defineTargetSizeForView(imageView, getConfiguration().getMaxImageSize().getWidth(), getConfiguration().getMaxImageSize().getHeight());
		UrlRequest urlRequest = new UrlRequest(GeneralUtils.createId(GeneralUtils.encodeUrl(imageUrl), targetSize), imageUrl, imageUrl, null, imageView, options);
		urlRequest.setTargetSize(targetSize);
		if(!tryShow(urlRequest)){
			urlRequest.setCacheFile(GeneralUtils.getCacheFile(getConfiguration(), options, urlRequest.getId()));
			load(urlRequest);
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
		if(imageFile == null){
			exceptionHandle(imageView, options);
			if(getConfiguration().isDebugMode()){
				Log.e(getConfiguration().getLogTag(), "imageFile不能为null");
			}
			return;
		}
		if(imageView == null){
			exceptionHandle(imageView, options);
			if(getConfiguration().isDebugMode()){
				Log.e(getConfiguration().getLogTag(), "imageView不能为null");
			}
			return;
		}
		
		if(options == null){
			options = getConfiguration().getDefaultOptions();
		}
		
		ImageSize targetSize = ImageSizeUtils.defineTargetSizeForView(imageView, getConfiguration().getMaxImageSize().getWidth(), getConfiguration().getMaxImageSize().getHeight());
		FileRequest urlRequest = new FileRequest(GeneralUtils.createId(GeneralUtils.encodeUrl(imageFile.getPath()), targetSize), imageFile.getPath(), imageFile, imageView, options);
		urlRequest.setTargetSize(targetSize);
		if(!tryShow(urlRequest)){
			load(urlRequest);
		}
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
	 * 尝试显示图片
	 * @param request 请求
	 * @return true：图片缓存中有图片并且已经显示了；false：缓存中没有对应的图片，需要重新加载
	 */
	private boolean tryShow(Request request){
		//如果需要从缓存中读取，就尝试从缓存中读取并显示
		if(request.getOptions() != null && request.getOptions().isCacheInMemory()){
			Bitmap cacheBitmap = getConfiguration().getBitmapCacher().get(request.getId());
			if(cacheBitmap != null){
				loadingIdSet.remove(request.getId());
				loadingImageViewSet.remove(request.getImageView());
				request.getImageView().setTag(null);	//清空绑定关系
				
				getConfiguration().getHandler().post(new DisplayBitmapTask(request.getImageView(), cacheBitmap, request.getOptions(), true));
				
				cacheBitmap = null;
				if(getConfiguration().isDebugMode()){
					Log.d(getConfiguration().getLogTag(), "从缓存中加载："+request.getName());
				}
				return true;
			}
		}
		
		//如果不从缓存中读取或者缓存中没有对应的图片，就显示默认图片
		if(request.getOptions() != null && request.getOptions().getLoadingImageResource() > 0){
			request.getImageView().setImageResource(request.getOptions().getLoadingImageResource());
		}else{
			request.getImageView().setImageDrawable(null);
		}
		return false;
	}
	
	/**
	 * 尝试加载
	 * @param request
	 * @return true：已经开始加载或正在加载；false：已经达到最大负荷
	 */
	public void load(Request request){
		request.getImageView().setTag(request.getId());	//将ImageView和当前图片绑定，以便在下载完成后通过此ID来找到此ImageView
		loadingImageViewSet.add(request.getImageView());	//先将当前ImageView存起来
		
		if(!loadingIdSet.contains(request.getId())){		//如果当前图片没有正在加载
			if(loadingIdSet.size() < getConfiguration().getThreadPoolSize()){	//如果尚未达到最大负荷，就开启线程加载
				loadingIdSet.add(request.getId());
				if(request instanceof FileRequest){
					getConfiguration().getThreadPool().submit(new FileRequestExecuteRunnable(this, (FileRequest) request));
				}else if(request instanceof UrlRequest){
					getConfiguration().getThreadPool().submit(new UrlRequestExecuteRunnable(this, (UrlRequest) request));
				}
			}else{
				synchronized (getConfiguration().getBufferPool()) {	//否则，加到等待队列中
					getConfiguration().getBufferPool().add(request);
				}
				if(getConfiguration().isDebugMode()){
					Log.w(getConfiguration().getLogTag(), "进入等待区："+request.getName());
				}
			}
		}else{
			if(getConfiguration().isDebugMode()){
				Log.w(getConfiguration().getLogTag(), "正在加载中："+request.getName());
			}
		}
	}
	
	/**
	 * 异常处理
	 * @param imageView
	 * @param options
	 */
	private void exceptionHandle(ImageView imageView, Options options){
		if(imageView != null){
			imageView.setTag(null);
			if(options != null && options.getLoadFailureImageResource() > 0){
				imageView.setImageResource(options.getLoadFailureImageResource());
			}else{
				imageView.setImageDrawable(null);
			}
		}
	}
	
	/**
	 * 获取加载中显示视图集合
	 * @return
	 */
	public final Set<ImageView> getLoadingImageViewSet() {
		return loadingImageViewSet;
	}

	/**
	 * 获取加载中请求ID集合
	 * @return
	 */
	public final Set<String> getLoadingIdSet() {
		return loadingIdSet;
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