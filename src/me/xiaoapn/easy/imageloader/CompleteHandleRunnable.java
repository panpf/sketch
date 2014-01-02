/*
 * Copyright 2013 Peng fei Pan
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

package me.xiaoapn.easy.imageloader;

import java.util.Iterator;

import android.util.Log;
import android.view.animation.Animation;
import android.widget.ImageView;

/**
 * 加载结果处理Runnable
 */
class CompleteHandleRunnable implements Runnable {
	private ImageLoader imageLoader;
	private Request request;
	
	public CompleteHandleRunnable(ImageLoader imageLoader, Request request){
		this.imageLoader = imageLoader;
		this.request = request;
	}
	
	@Override
	public void run() {
		if(imageLoader.getConfiguration().isDebugMode()){
			Log.e(imageLoader.getConfiguration().getLogTag()+":CompleteHandleRunnable", ":完成处理："+request.getName());
		}
		
		/* 遍历ImageView集合，找到其绑定的地址同当前下载的地址一样的图片视图，并将结果显示到ImageView上 */
		Iterator<ImageView> iterator = imageLoader.getLoadingImageViewSet().iterator();
		ImageView imageView;
		Object tagObject;
		while(iterator.hasNext()){
			imageView = iterator.next();
			if(imageView != null){
				tagObject = imageView.getTag();
				//如果当前ImageView有要显示的图片，入如果没有的话就将其从等待集合中移除
				if(tagObject != null){
					//如果当前ImageView就是要找的
					if(request.getId().equals(tagObject.toString())){
						imageView.clearAnimation();//先清除之前所有的动画
						//如果图片加载成功
						if(request.getResultBitmap() != null){
							Animation animation = null;
							if(request.getOptions() != null && request.getOptions().getShowAnimationListener() != null){
								animation = request.getOptions().getShowAnimationListener().onGetShowAnimation();
							}
							if(animation != null){
								imageView.setAnimation(animation);
							}
							imageView.setImageBitmap(request.getResultBitmap());
							if(imageLoader.getConfiguration().isDebugMode()){
								Log.d(imageLoader.getConfiguration().getLogTag(), "加载成功："+request.getName());
							}
						}else{
							if(request.getOptions() != null){
								if(request.getOptions().getLoadFailureImageResource() > 0){
									imageView.setImageResource(request.getOptions().getLoadFailureImageResource());
								}else{
									imageView.setImageBitmap(null);
								}
							}else{
								imageView.setImageBitmap(null);
							}
							if(imageLoader.getConfiguration().isDebugMode()){
								Log.e(imageLoader.getConfiguration().getLogTag(), "加载失败："+request.getName());
							}
						}
						imageView.setTag(null);
						iterator.remove();
					}
				}else{
					iterator.remove();
				}
			}
		}
		
		/* 从等待队列中取出等待加载的请求并尝试加载 */
		Request waitImageLoadRequest;
		synchronized (imageLoader.getConfiguration().getBufferPool()) {
			waitImageLoadRequest = imageLoader.getConfiguration().getBufferPool().poll();
		}
		if(waitImageLoadRequest != null){
			imageLoader.load(waitImageLoadRequest);
		}
	}
}
