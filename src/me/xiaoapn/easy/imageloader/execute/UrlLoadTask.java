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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import me.xiaoapn.easy.imageloader.ImageLoader;
import me.xiaoapn.easy.imageloader.download.ImageDownloader;
import me.xiaoapn.easy.imageloader.download.OnCompleteListener;
import me.xiaoapn.easy.imageloader.util.GeneralUtils;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

/**
 * Url加载任务Runable
 */
public class UrlLoadTask extends LoadBitmapTask{
	private ImageLoader imageLoader;	//图片加载器
	private UrlRequest urlRequest;	//加载请求
	
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
	protected Bitmap loadBitmap() {
		final BitmapHolder bitmapHolder = new BitmapHolder();
		
		if(GeneralUtils.isAvailableOfFile(urlRequest.getCacheFile(), urlRequest.getOptions().getCacheConfig().getDiskCachePeriodOfValidity(), imageLoader, urlRequest.getName())){
			BufferedInputStream inputStream = null;
			try {
				inputStream = new BufferedInputStream(new FileInputStream(urlRequest.getCacheFile()));
				bitmapHolder.bitmap = imageLoader.getConfiguration().getBitmapLoader().decode(inputStream, urlRequest.getTargetSize(), imageLoader, urlRequest.getName());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}finally{
				if(inputStream != null){
					try {
						inputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}else{
			if(GeneralUtils.isNotEmpty(urlRequest.getImageUrl())){
				if(imageLoader.getConfiguration().isDebugMode()){
					Log.d(imageLoader.getConfiguration().getLogTag(), new StringBuffer(getLogName()).append("：").append("从网络加载开始").append("：").append(urlRequest.getName()).toString());
				}
				new ImageDownloader(urlRequest.getName(), urlRequest.getImageUrl(), urlRequest.getCacheFile(), urlRequest.getOptions().getMaxRetryCount(), imageLoader.getConfiguration().getHttpClient(), imageLoader, new OnCompleteListener() {
					@Override
					public void onFailed() {
						
					}
					
					@Override
					public void onComplete(byte[] data) {
						BufferedInputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(data));
						bitmapHolder.bitmap = imageLoader.getConfiguration().getBitmapLoader().decode(inputStream, urlRequest.getTargetSize(), imageLoader, urlRequest.getName());
						try {
							inputStream.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
					@Override
					public void onComplete(File cacheFile) {
						BufferedInputStream inputStream = null;
						try {
							inputStream = new BufferedInputStream(new FileInputStream(cacheFile));
							bitmapHolder.bitmap = imageLoader.getConfiguration().getBitmapLoader().decode(inputStream, urlRequest.getTargetSize(), imageLoader, urlRequest.getName());
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}finally{
							if(inputStream != null){
								try {
									inputStream.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}).execute();
			}else{
				if(imageLoader.getConfiguration().isDebugMode()){
					Log.w(imageLoader.getConfiguration().getLogTag(), new StringBuffer(getLogName()).append("：").append("所有条件均不满足").append("：").append(urlRequest.getName()).toString());
				}
			}
		}
		return bitmapHolder.bitmap;
	}
	
	private class BitmapHolder{
		Bitmap bitmap;
	}
}
