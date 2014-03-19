/*
 * Copyright (C) 2013 Peng fei Pan <sky@xiaopan.me>
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

package me.xiaopan.android.imageloader.task;

import java.util.concurrent.FutureTask;

import me.xiaopan.android.imageloader.Configuration;
import me.xiaopan.android.imageloader.ImageLoader;
import me.xiaopan.android.imageloader.display.BitmapDisplayer.BitmapType;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

public abstract class BitmapLoadTask extends FutureTask<BitmapDrawable> {
	private static final String NAME= BitmapLoadTask.class.getSimpleName();
	private Request request;
	private Configuration configuration;
	
	public BitmapLoadTask(Request request, Configuration configuration, BitmapLoadCallable bitmapLoadCallable) {
		super(bitmapLoadCallable);
		this.request = request;
		this.configuration = configuration;
		this.request.getImageViewAware().setBitmapLoadTask(this);
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
			
			//尝试取出ImageView并显示
			if (!request.getImageViewAware().isCollected()) {
				if(bitmapDrawable != null && !bitmapDrawable.getBitmap().isRecycled()){
					configuration.getHandler().post(new BitmapDisplayRunnable(request, bitmapDrawable, BitmapType.SUCCESS, configuration));
				}else{
					configuration.getHandler().post(new BitmapDisplayRunnable(request, request.getDisplayOptions().getFailureDrawable(), BitmapType.FAILURE, configuration));
				}
			}else{
				if(configuration.isDebugMode()){
					Log.e(ImageLoader.LOG_TAG, new StringBuffer(NAME).append("：").append("已解除绑定关系").append("；").append(request.getName()).toString());
				}
				if(request.getImageLoadListener() != null){
					configuration.getHandler().post(new Runnable() {
						@Override
						public void run() {
							request.getImageLoadListener().onCancelled(request.getImageUri(), request.getImageViewAware().getImageView());
						}
					});
				}
			}
		}else{
			if(configuration.isDebugMode()){
				Log.e(ImageLoader.LOG_TAG, new StringBuffer(NAME).append("：").append("已取消").append("；").append(request.getName()).toString());
			}
			if(request.getImageLoadListener() != null){
				configuration.getHandler().post(new Runnable() {
					@Override
					public void run() {
						request.getImageLoadListener().onCancelled(request.getImageUri(), request.getImageViewAware().getImageView());
					}
				});
			}
		}
	}

	/**
	 * 获取请求
	 * @return
	 */
	public Request getRequest() {
		return request;
	}

	/**
	 * 获取配置
	 * @return
	 */
	public Configuration getConfiguration() {
		return configuration;
	}

	/**
     * 获取与给定ImageView关联的任务
     * @param imageView 
     * @return 
     */
	public static BitmapLoadTask getBitmapLoadTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapLoadTask();
            }
        }
        return null;
    }

    /**
     * 取消加载工作
     * @param imageLoader
     * @param imageView
     * @return true：当前ImageView有正在执行的任务并且取消成功；false：当前ImageView没有正在执行的任务
     */
    public static boolean cancelBitmapLoadTask(ImageView imageView) {
        final BitmapLoadTask bitmapLoadTask = getBitmapLoadTask(imageView);
        if (bitmapLoadTask != null) {
            bitmapLoadTask.cancel(true);
            if (bitmapLoadTask.getConfiguration().isDebugMode()) {
                Log.w(ImageLoader.LOG_TAG, new StringBuffer().append("取消加载任务").append("；").append(bitmapLoadTask.getRequest().getName()).toString());
            }
            return true;
        }else{
        	return false;
        }
    }

    /**
     * 取消潜在的任务
     * @param request
     * @param imageView
     * @param configuration
     * @return true：取消成功；false：ImageView所关联的任务就是所需的无需取消
     */
    public static boolean cancelPotentialBitmapLoadTask(Request request, ImageView imageView, Configuration configuration) {
        final BitmapLoadTask potentialBitmapLoadTask = getBitmapLoadTask(imageView);
        boolean cancelled = true;
        if (potentialBitmapLoadTask != null) {
            final String requestId = potentialBitmapLoadTask.getRequest().getId();
        	if (requestId != null && requestId.equals(request.getId())) {
                cancelled = false;
            }else{
            	potentialBitmapLoadTask.cancel(true);
            	cancelled = true;
            }
            if(configuration.isDebugMode()){
            	Log.w(ImageLoader.LOG_TAG, new StringBuffer().append((cancelled?"取消":"无需取消")+"潜在的加载任务").append("；").append("ImageViewCode").append("=").append(imageView.hashCode()).append("；").append(potentialBitmapLoadTask.getRequest().getName()).toString());
            }
        }
        return cancelled;
    }
}
