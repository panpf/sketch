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

/**
 * 加载任务Runable
 */
class NetworkLoadTask implements Runnable {
	private ImageLoader imageLoader;	//图片加载器
	private LoadRequest loadRequest;	//加载请求
	
	/**
	 * 创建一个加载图片任务
	 * @param loadRequest 加载请求
	 */
	public NetworkLoadTask(ImageLoader imageLoader, LoadRequest loadRequest){
		this.imageLoader = imageLoader;
		this.loadRequest = loadRequest;
	}
	
	@Override
	public void run() {
		//如果本地文件可以用
		if(GeneralUtils.isAvailableOfFile(loadRequest.getCacheFile(), loadRequest.getOptions() != null?loadRequest.getOptions().getCachePeriodOfValidity():0, imageLoader, loadRequest.getName())){
			imageLoader.getConfiguration().log("从本地加载图片："+loadRequest.getName());
			loadRequest.setResultBitmap(GeneralUtils.getBitmapLoader(loadRequest.getOptions()).onDecodeFile(loadRequest.getCacheFile(), loadRequest.getImageView(), imageLoader));
		}else{
			if(GeneralUtils.isNotEmpty(loadRequest.getUrl())){
				imageLoader.getConfiguration().log("从网络加载图片："+loadRequest.getName());
				new ImageDownloader(loadRequest.getName(), loadRequest.getUrl(), loadRequest.getCacheFile(), loadRequest.getOptions().getMaxRetryCount(), imageLoader.getConfiguration().getHttpClient(), imageLoader, new OnCompleteListener() {
					@Override
					public void onFailed() {
						loadRequest.setResultBitmap(null);
						resultHandle();
					}
					
					@Override
					public void onComplete(byte[] data) {
						loadRequest.setResultBitmap(GeneralUtils.getBitmapLoader(loadRequest.getOptions()).onDecodeByteArray(data, loadRequest.getImageView(), imageLoader));
						resultHandle();
					}
					
					@Override
					public void onComplete(File cacheFile) {
						loadRequest.setResultBitmap(GeneralUtils.getBitmapLoader(loadRequest.getOptions()).onDecodeFile(cacheFile, loadRequest.getImageView(), imageLoader));
						resultHandle();
					}
				}).execute();
			}else{
				imageLoader.getConfiguration().log("所有条件均不满足，加载结果为null："+loadRequest.getName(), true);
				loadRequest.setResultBitmap(null);
				resultHandle();
			}
		}
	}
	
	private void resultHandle(){
		/* 尝试缓存到内存中 */
		if(loadRequest.getResultBitmap() != null && loadRequest.getOptions() != null && loadRequest.getOptions().isCacheInMemory()){
			imageLoader.getConfiguration().getBitmapCacher().put(loadRequest.getId(), loadRequest.getResultBitmap());
		}
		
		imageLoader.getConfiguration().getHandler().post(new LoadResultHandleRunnable(imageLoader, loadRequest));
	}
}
