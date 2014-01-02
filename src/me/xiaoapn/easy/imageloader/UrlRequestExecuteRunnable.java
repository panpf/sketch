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

import me.xiaoapn.easy.imageloader.ImageDownloader.OnCompleteListener;
import android.util.Log;

/**
 * Url加载任务Runable
 */
class UrlRequestExecuteRunnable extends RequestExecuteRunnable{
	private ImageLoader imageLoader;	//图片加载器
	private UrlRequest urlRequest;	//加载请求
	
	/**
	 * 创建一个加载图片任务
	 * @param urlRequest 加载请求
	 */
	public UrlRequestExecuteRunnable(ImageLoader imageLoader, UrlRequest urlRequest){
		super(imageLoader, urlRequest);
		this.imageLoader = imageLoader;
		this.urlRequest = urlRequest;
	}
	
	@Override
	public void run() {
		if(GeneralUtils.isAvailableOfFile(urlRequest.getCacheFile(), urlRequest.getOptions() != null?urlRequest.getOptions().getCachePeriodOfValidity():0, imageLoader, urlRequest.getName())){
			if(imageLoader.getConfiguration().isDebugMode()){
				Log.i(imageLoader.getConfiguration().getLogTag()+":UrlRequestExecuteRunnable", "从本地缓存加载开始："+urlRequest.getName());
			}
			urlRequest.setResultBitmap(GeneralUtils.getBitmapLoader(urlRequest.getOptions()).onDecodeFile(urlRequest.getCacheFile(), urlRequest.getImageView(), imageLoader));
			if(imageLoader.getConfiguration().isDebugMode()){
				Log.i(imageLoader.getConfiguration().getLogTag()+":UrlRequestExecuteRunnable", "从本地缓存加载完成："+urlRequest.getName());
			}
			UrlRequestExecuteRunnable.super.run();
		}else{
			if(GeneralUtils.isNotEmpty(urlRequest.getImageUrl())){
				if(imageLoader.getConfiguration().isDebugMode()){
					Log.d(imageLoader.getConfiguration().getLogTag()+":UrlRequestExecuteRunnable", "从网络加载开始："+urlRequest.getName());
				}
				new ImageDownloader(urlRequest.getName(), urlRequest.getImageUrl(), urlRequest.getCacheFile(), urlRequest.getOptions().getMaxRetryCount(), imageLoader.getConfiguration().getHttpClient(), imageLoader, new OnCompleteListener() {
					@Override
					public void onFailed() {
						urlRequest.setResultBitmap(null);
						if(imageLoader.getConfiguration().isDebugMode()){
							Log.e(imageLoader.getConfiguration().getLogTag()+":UrlRequestExecuteRunnable", "从网络加载失败："+urlRequest.getName());
						}
						UrlRequestExecuteRunnable.super.run();
					}
					
					@Override
					public void onComplete(byte[] data) {
						urlRequest.setResultBitmap(GeneralUtils.getBitmapLoader(urlRequest.getOptions()).onDecodeByteArray(data, urlRequest.getImageView(), imageLoader));
						if(imageLoader.getConfiguration().isDebugMode()){
							Log.d(imageLoader.getConfiguration().getLogTag()+":UrlRequestExecuteRunnable", "从网络加载成功（Byte）："+urlRequest.getName());
						}
						UrlRequestExecuteRunnable.super.run();
					}
					
					@Override
					public void onComplete(File cacheFile) {
						urlRequest.setResultBitmap(GeneralUtils.getBitmapLoader(urlRequest.getOptions()).onDecodeFile(cacheFile, urlRequest.getImageView(), imageLoader));
						if(imageLoader.getConfiguration().isDebugMode()){
							Log.d(imageLoader.getConfiguration().getLogTag()+":UrlRequestExecuteRunnable", "从网络加载成功（File）："+urlRequest.getName());
						}
						UrlRequestExecuteRunnable.super.run();
					}
				}).execute();
			}else{
				urlRequest.setResultBitmap(null);
				if(imageLoader.getConfiguration().isDebugMode()){
					Log.e(imageLoader.getConfiguration().getLogTag()+":UrlRequestExecuteRunnable", "从网络加载失败，因为所有条件均不满足："+urlRequest.getName());
				}
				UrlRequestExecuteRunnable.super.run();
			}
		}
	}
}
