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

package me.xiaoapn.easy.imageloader.execute.task;

import java.util.concurrent.FutureTask;

import me.xiaoapn.easy.imageloader.Configuration;
import me.xiaoapn.easy.imageloader.ImageLoader;
import me.xiaoapn.easy.imageloader.display.BitmapType;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.ImageView;

public class LoadFutureTask extends FutureTask<BitmapDrawable> {
	private Request request;
	private ImageLoader imageLoader;
	private Configuration configuration;
	private LoadBitmapTask loadBitmapTask;
	
	public LoadFutureTask(LoadBitmapTask loadBitmapTask) {
		super(loadBitmapTask);
		this.request = loadBitmapTask.getRequest();
		this.imageLoader = loadBitmapTask.getImageLoader();
		this.configuration = loadBitmapTask.getImageLoader().getConfiguration();
		this.loadBitmapTask = loadBitmapTask;
	}

	@Override
	protected void done() {
		if(!isCancelled()){
			BitmapDrawable bitmapDrawable = null;
			try {
				bitmapDrawable = get();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			/* 如果需要缓存的话就内存中的话 */
			if(request.getOptions().getCacheConfig().isCacheInMemory() && bitmapDrawable != null){
				configuration.getBitmapCacher().put(request.getId(), bitmapDrawable);
			}
			
			//尝试取出ImageView并显示
			ImageView imageView = loadBitmapTask.getImageView();
			if (imageView != null) {
				if(bitmapDrawable != null){
					configuration.getHandler().post(new DisplayBitmapTask(imageLoader, imageView, bitmapDrawable, BitmapType.SUCCESS, false, request));
				}else{
					configuration.getHandler().post(new DisplayBitmapTask(imageLoader, imageView, request.getOptions().getFailureDrawable(), BitmapType.FAILURE, false, request));
				}
			}else{
				if(configuration.isDebugMode()){
					Log.e(configuration.getLogTag(), new StringBuffer().append(loadBitmapTask.getLogName()).append("：").append("已取消绑定关系").append("：").append(request.getName()).toString());
				}
			}
		}else{
			if(configuration.isDebugMode()){
				Log.e(configuration.getLogTag(), new StringBuffer().append(loadBitmapTask.getLogName()).append("：").append("已取消").append(request.getName()).toString());
			}
		}
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return super.cancel(mayInterruptIfRunning);
	}
}
