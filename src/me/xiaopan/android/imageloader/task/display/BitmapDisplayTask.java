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

package me.xiaopan.android.imageloader.task.display;

import me.xiaopan.android.imageloader.Configuration;
import me.xiaopan.android.imageloader.ImageLoader;
import me.xiaopan.android.imageloader.display.BitmapDisplayer.BitmapType;
import me.xiaopan.android.imageloader.task.Task;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

public abstract class BitmapDisplayTask extends Task {
	private static final String NAME= BitmapDisplayTask.class.getSimpleName();
	private DisplayRequest displayRequest;
	
	public BitmapDisplayTask(DisplayRequest displayRequest, BitmapDisplayCallable bitmapLoadCallable) {
		super(displayRequest, bitmapLoadCallable);
		this.displayRequest = displayRequest;
	}
	
	@Override
	protected void done() {
		if(!isCancelled()){
			BitmapDrawable bitmapDrawable = null;
			try {
				bitmapDrawable = (BitmapDrawable) get();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//尝试取出ImageView并显示
			if (!displayRequest.getImageViewHolder().isCollected()) {
				if(bitmapDrawable != null && !bitmapDrawable.getBitmap().isRecycled()){
					displayRequest.getConfiguration().getHandler().post(new BitmapDisplayRunnable(displayRequest, bitmapDrawable, BitmapType.SUCCESS));
				}else{
					displayRequest.getConfiguration().getHandler().post(new BitmapDisplayRunnable(displayRequest, displayRequest.getDisplayOptions().getFailureDrawable(), BitmapType.FAILURE));
				}
			}else{
				if(displayRequest.getConfiguration().isDebugMode()){
					Log.e(ImageLoader.LOG_TAG, new StringBuffer(NAME).append("：").append("已解除绑定关系").append("；").append(displayRequest.getName()).toString());
				}
				if(displayRequest.getDisplayListener() != null){
					displayRequest.getConfiguration().getHandler().post(new Runnable() {
						@Override
						public void run() {
							displayRequest.getDisplayListener().onCancelled(displayRequest.getImageUri(), displayRequest.getImageViewHolder().getImageView());
						}
					});
				}
			}
		}else{
			if(displayRequest.getConfiguration().isDebugMode()){
				Log.e(ImageLoader.LOG_TAG, new StringBuffer(NAME).append("：").append("已取消").append("；").append(displayRequest.getName()).toString());
			}
			if(displayRequest.getDisplayListener() != null){
				displayRequest.getConfiguration().getHandler().post(new Runnable() {
					@Override
					public void run() {
						displayRequest.getDisplayListener().onCancelled(displayRequest.getImageUri(), displayRequest.getImageViewHolder().getImageView());
					}
				});
			}
		}
	}

	/**
	 * 获取请求
	 * @return
	 */
	public DisplayRequest getDisplayRequest() {
		return displayRequest;
	}

	/**
	 * 获取配置
	 * @return
	 */
	public Configuration getConfiguration() {
		return displayRequest.getConfiguration();
	}

	/**
     * 获取与给定ImageView关联的任务
     * @param imageView 
     * @return 
     */
	public static DisplayRequest getDisplayRequest(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                return ((AsyncDrawable) drawable).getDisplayRequest();
            }
        }
        return null;
    }

    /**
     * 取消显示请求
     * @param imageLoader
     * @param imageView
     * @return true：当前ImageView有正在执行的任务并且取消成功；false：当前ImageView没有正在执行的任务
     */
    public static boolean cancelDisplayRequest(ImageView imageView) {
        final DisplayRequest displayRequest = getDisplayRequest(imageView);
        if (displayRequest != null) {
    		displayRequest.cancel(true);
            if (displayRequest.getConfiguration().isDebugMode()) {
                Log.w(ImageLoader.LOG_TAG, new StringBuffer().append("取消加载任务").append("；").append(displayRequest.getName()).toString());
            }
            return true;
        }else{
        	return false;
        }
    }

    /**
     * 取消潜在的任务
     * @param displayRequest
     * @param imageView
     * @return true：取消成功；false：ImageView所关联的任务就是所需的无需取消
     */
    public static boolean cancelPotentialBitmapLoadTask(DisplayRequest displayRequest, ImageView imageView) {
        final DisplayRequest potentialDisplayRequest = getDisplayRequest(imageView);
        boolean cancelled = true;
        if (potentialDisplayRequest != null) {
            final String requestId = potentialDisplayRequest.getId();
        	if (requestId != null && requestId.equals(displayRequest.getId())) {
                cancelled = false;
            }else{
        		potentialDisplayRequest.cancel(true);
            	cancelled = true;
            }
            if(displayRequest.getConfiguration().isDebugMode()){
            	Log.w(ImageLoader.LOG_TAG, new StringBuffer().append((cancelled?"取消":"无需取消")+"潜在的加载任务").append("；").append("ImageViewCode").append("=").append(imageView.hashCode()).append("；").append(potentialDisplayRequest.getName()).toString());
            }
        }
        return cancelled;
    }
}
