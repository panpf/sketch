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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * 图片加载器，可以从网络或者本地加载图片，并且支持自动清除缓存
 */
public class ImageLoader{
	private Bitmap tempCacheBitmap;	//临时存储缓存的图片
	private Set<String> loadingRequestSet;	//正在加载的Url列表，用来防止同一个URL被重复加载
	private Configuration configuration;	//配置
	private Set<ImageView> loadingImageViewSet;	//图片视图集合，这个集合里的每个尚未加载完成的视图身上都会携带有他要显示的图片的地址，当每一个图片加载完成之后都会在这个列表中遍历找到所有携带有这个这个图片的地址的视图，并把图片显示到这个视图上
	
	/**
	 * 创建图片加载器
	 */
	public ImageLoader(){
		loadingImageViewSet = new HashSet<ImageView>();//初始化图片视图集合
		loadingRequestSet = new HashSet<String>();//初始化加载中URL集合
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
	 * 加载图片
	 * @param url 图片下载地址，如果本地缓存文件不存在将从网络获取
	 * @param imageView 显示图片的视图
	 * @param options 加载选项
	 */
	public final void load(String url, ImageView imageView, Options options){
		if(GeneralUtils.isNotEmpty(url) && imageView != null){
			try {
				String id = URLEncoder.encode(url, "UTF-8");
				String name = url;
				if(!tryShowImage(id, name, imageView, options)){	//尝试显示图片，如果显示失败了就尝试加载
					tryLoad(new LoadRequest.Builder(id, url, imageView).setName(name).setCacheFile(getConfiguration().getCacheFile(imageView.getContext(), options, id)).setOptions(options).create());
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}else{
			if(imageView != null){
				imageView.setTag(null);
				if(options != null && options.getLoadingImageResource() > 0){
					imageView.setImageResource(options.getLoadingImageResource());
				}else{
					imageView.setImageDrawable(null);
				}
			}
		}
	}
	
	/**
	 * 加载图片，会使用默认的加载选项，你可以通过ImageLoader.getConfiguration().getDefaultOptions()来配置默认加载选项
	 * @param url 图片下载地址
	 * @param showImageView 显示图片的视图
	 */
	public final void load(String url, ImageView showImageView){
		load(url, showImageView, getConfiguration().getDefaultOptions());
	}
	
	/**
	 * 加载图片
	 * @param localFile 本地图片文件，如果本地文件不存在会尝试从imageUrl下载图片并创建localFile
	 * @param imageView 显示图片的视图
	 * @param url 图片下载地址，如果本地图片文件不存在将从网络获取
	 * @param options 加载选项
	 */
	public final void load(File localFile, ImageView imageView, String url, Options options){
		if((localFile != null || GeneralUtils.isNotEmpty(url)) && imageView != null){
			try{
				String name = localFile.getPath();
				String id = URLEncoder.encode(name, "UTF-8");
				if(!tryShowImage(id, name, imageView, options)){	//尝试显示图片，如果显示失败了就尝试加载
					tryLoad(new LoadRequest.Builder(id, url, imageView).setLocal(true).setName(name).setCacheFile(localFile).setOptions(options).create());
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}else{
			if(imageView != null){
				imageView.setTag(null);
				if(options != null && options.getLoadingImageResource() > 0){
					imageView.setImageResource(options.getLoadingImageResource());
				}else{
					imageView.setImageDrawable(null);
				}
			}
		}
	}
	
	/**
	 * 加载图片，会使用默认的加载选项，你可以通过ImageLoader.getConfiguration().getDefaultOptions()来配置默认加载选项
	 * @param localFile 本地图片文件，如果本地文件不存在会尝试从imageUrl下载图片并创建localFile
	 * @param showImageView 显示图片的视图
	 * @param url 图片下载地址，如果本地图片文件不存在将从网络获取
	 */
	public final void load(File localFile, ImageView showImageView, String url){
		load(localFile, showImageView, url, getConfiguration().getDefaultOptions());
	}
	
	/**
	 * 加载图片
	 * @param localFile 本地图片文件
	 * @param showImageView 显示图片的视图
	 * @param options 加载选项
	 */
	public final void load(File localFile, ImageView showImageView, Options options){
		load(localFile, showImageView, null, options);
	}
	
	/**
	 * 加载图片，会使用默认的加载选项，你可以通过ImageLoader.getConfiguration().getDefaultOptions()来配置默认加载选项
	 * @param localFile 本地图片文件
	 * @param showImageView 显示图片的视图
	 */
	public final void load(File localFile, ImageView showImageView){
		load(localFile, showImageView, null, getConfiguration().getDefaultOptions());
	}
	
	/**
	 * 尝试显示图片
	 * @param id ID，根据此ID从缓存中获取图片
	 * @param showImageView 显示视图
	 * @param options 加载选项
	 * @return true：图片缓存中有图片并且已经显示了；false：缓存中没有对应的图片，需要开启新线程从网络或本地加载
	 */
	private boolean tryShowImage(String id, String name, ImageView showImageView, Options options){
		//如果需要从缓存中读取，就根据地址从缓存中获取图片，如果缓存中存在相对的图片就显示，否则显示默认图片或者显示空
		if(options != null && options.isCacheInMemory() && (tempCacheBitmap = getConfiguration().getBitmapCacher().get(id)) != null){
			showImageView.setTag(null);	//清空绑定关系
			getConfiguration().log("从缓存中加载图片："+name);
			loadingImageViewSet.remove(showImageView);
			showImageView.clearAnimation();
			showImageView.setImageBitmap(tempCacheBitmap);
			tempCacheBitmap = null;
			return true;
		}else{
			showImageView.setTag(id);	//将ImageView和当前图片绑定，以便在下载完成后通过此ID来找到此ImageView
			if(options != null && options.getLoadingImageResource() > 0){
				showImageView.setImageResource(options.getLoadingImageResource());
			}else{
				showImageView.setImageDrawable(null);
			}
			return false;
		}
	}
	
	/**
	 * 尝试加载
	 * @param loadRequest
	 */
	void tryLoad(LoadRequest loadRequest){
		loadingImageViewSet.add(loadRequest.getImageView());	//先将当前ImageView存起来
		if(!loadingRequestSet.contains(loadRequest.getId())){		//如果当前图片没有正在加载
			if(loadingRequestSet.size() < getConfiguration().getMaxThreadNumber()){	//如果尚未达到最大负荷，就开启线程加载
				loadingRequestSet.add(loadRequest.getId());
				getConfiguration().getThreadPool().submit(new LoadTaskRunable(this, loadRequest));
			}else{
				synchronized (getConfiguration().getWaitingRequestCircle()) {	//否则，加到等待队列中
					getConfiguration().getWaitingRequestCircle().add(loadRequest);
				}
			}
		}
	}
	
	/**
	 * 获取加载中显示视图集合
	 * @return
	 */
	final Set<ImageView> getLoadingImageViewSet() {
		return loadingImageViewSet;
	}

	/**
	 * 获取加载中请求ID集合
	 * @return
	 */
	final Set<String> getLoadingRequestSet() {
		return loadingRequestSet;
	}

	/**
	 * 获取配置
	 * @return
	 */
	public Configuration getConfiguration() {
		if(configuration == null){
			configuration = new Configuration.Builder().create();
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