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

package me.xiaoapn.easy.imageloader.execute;

import java.io.File;

import me.xiaoapn.easy.imageloader.ImageLoader;
import me.xiaoapn.easy.imageloader.download.ImageDownloader;
import me.xiaoapn.easy.imageloader.download.OnCompleteListener;
import me.xiaoapn.easy.imageloader.util.GeneralUtils;
import android.util.Log;
import android.widget.ImageView;

/**
 * Url加载任务Runable
 */
public class UrlLoadTask extends LoadBitmapTask{
	private ImageLoader imageLoader;	//图片加载器
	private UrlRequest urlRequest;	//加载请求
	private String result;
	
	/**
	 * 创建一个加载图片任务
	 * @param urlRequest 加载请求
	 */
	public UrlLoadTask(ImageLoader imageLoader, UrlRequest urlRequest, ImageView imageView){
		super(imageLoader, urlRequest, imageView);
		this.imageLoader = imageLoader;
		this.urlRequest = urlRequest;
	}
	
	@Override
	public String call() {
		if(GeneralUtils.isAvailableOfFile(urlRequest.getCacheFile(), urlRequest.getOptions().getCacheConfig().getDiskCachePeriodOfValidity(), imageLoader, urlRequest.getName())){
			if(imageLoader.getConfiguration().isDebugMode()){
				Log.d(imageLoader.getConfiguration().getLogTag(), new StringBuffer(getLogName()).append("：").append("从本地缓存加载开始").append("：").append(urlRequest.getName()).toString());
			}
			urlRequest.setResultBitmap(imageLoader.getConfiguration().getBitmapLoader().onFromFileLoad(urlRequest.getCacheFile(), urlRequest.getImageView(), imageLoader));
			return UrlLoadTask.super.call();
		}else{
			if(GeneralUtils.isNotEmpty(urlRequest.getImageUrl())){
				if(imageLoader.getConfiguration().isDebugMode()){
					Log.d(imageLoader.getConfiguration().getLogTag(), new StringBuffer(getLogName()).append("：").append("从网络加载开始").append("：").append(urlRequest.getName()).toString());
				}
				new ImageDownloader(urlRequest.getName(), urlRequest.getImageUrl(), urlRequest.getCacheFile(), urlRequest.getOptions().getMaxRetryCount(), imageLoader.getConfiguration().getHttpClient(), imageLoader, new OnCompleteListener() {
					@Override
					public void onFailed() {
						urlRequest.setResultBitmap(null);
						result = UrlLoadTask.super.call();
					}
					
					@Override
					public void onComplete(byte[] data) {
						urlRequest.setResultBitmap(imageLoader.getConfiguration().getBitmapLoader().onFromByteArrayLoad(data, urlRequest.getImageView(), imageLoader));
						result = UrlLoadTask.super.call();
					}
					
					@Override
					public void onComplete(File cacheFile) {
						urlRequest.setResultBitmap(imageLoader.getConfiguration().getBitmapLoader().onFromFileLoad(cacheFile, urlRequest.getImageView(), imageLoader));
						result = UrlLoadTask.super.call();
					}
				}).execute();
				return result;
			}else{
				if(imageLoader.getConfiguration().isDebugMode()){
					Log.w(imageLoader.getConfiguration().getLogTag(), new StringBuffer(getLogName()).append("：").append("所有条件均不满足").append("：").append(urlRequest.getName()).toString());
				}
				urlRequest.setResultBitmap(null);
				return UrlLoadTask.super.call();
			}
		}
	}
}
