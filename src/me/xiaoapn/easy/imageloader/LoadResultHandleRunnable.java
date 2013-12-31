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

import android.view.animation.Animation;
import android.widget.ImageView;

/**
 * 加载结果处理Runnable
 */
class LoadResultHandleRunnable implements Runnable {
	private ImageLoader imageLoader;
	private LoadRequest loadRequest;
	
	public LoadResultHandleRunnable(ImageLoader imageLoader, LoadRequest loadRequest){
		this.imageLoader = imageLoader;
		this.loadRequest = loadRequest;
	}
	
	@Override
	public void run() {
		/* 遍历显示视图集合，找到其绑定的地址同当前下载的地址一样的图片视图，并将结果显示到图片视图上 */
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
					if(loadRequest.getId().equals(tagObject.toString())){
						imageView.clearAnimation();//先清除之前所有的动画
						//如果图片加载成功
						if(loadRequest.getResultBitmap() != null){
							Animation animation = null;
							if(loadRequest.getOptions() != null && loadRequest.getOptions().getShowAnimationListener() != null){
								animation = loadRequest.getOptions().getShowAnimationListener().onGetShowAnimation();
							}
							if(animation != null){
								imageView.setAnimation(animation);
							}
							imageView.setImageBitmap(loadRequest.getResultBitmap());
							imageLoader.getConfiguration().log("加载成功："+loadRequest.getName());
						}else{
							if(loadRequest.getOptions() != null){
								if(loadRequest.getOptions().getLoadFailureImageResource() > 0){
									imageView.setImageResource(loadRequest.getOptions().getLoadFailureImageResource());
								}else{
									imageView.setImageBitmap(null);
								}
							}else{
								imageView.setImageBitmap(null);
							}
							imageLoader.getConfiguration().log("加载失败："+loadRequest.getName(), true);
						}
						imageView.setTag(null);
						iterator.remove();
					}
				}else{
					iterator.remove();
				}
			}
		}
		
		/* 将当前下载对象从正在下载集合中删除 */
		imageLoader.getLoadingRequestSet().remove(loadRequest.getId());
		
		/* 从等待队列中取出等待加载的请求并尝试加载 */
		LoadRequest waitImageLoadRequest;
		synchronized (imageLoader.getConfiguration().getWaitingRequestCircle()) {
			waitImageLoadRequest = imageLoader.getConfiguration().getWaitingRequestCircle().poll();
		}
		if(waitImageLoadRequest != null){
			imageLoader.load(waitImageLoadRequest);
		}
	}
}
