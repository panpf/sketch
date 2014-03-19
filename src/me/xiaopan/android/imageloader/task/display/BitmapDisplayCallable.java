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

import java.util.concurrent.Callable;

import me.xiaopan.android.imageloader.decode.InputStreamCreator;
import me.xiaopan.android.imageloader.util.ImageLoaderUtils;
import me.xiaopan.android.imageloader.util.RecyclingBitmapDrawable;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public abstract class BitmapDisplayCallable implements Callable<Object> {
	protected DisplayRequest displayRequest;
	
	public BitmapDisplayCallable(DisplayRequest displayRequest) {
		this.displayRequest = displayRequest;
	}

	@Override
	public Object call() throws Exception {
		if(displayRequest.getReentrantLock() != null){
			displayRequest.getReentrantLock().lock();	//先获取锁，防止重复执行请求
		}
		BitmapDrawable bitmapDrawable = null;
		try{
			bitmapDrawable = displayRequest.getConfiguration().getBitmapCacher().get(displayRequest.getId());	//先尝试从缓存中去取对应的位图
			if(bitmapDrawable == null){
				//解码
				InputStreamCreator inputStreamCreator = getInputStreamCreator();
				Bitmap bitmap = null;
				if(inputStreamCreator != null){
					bitmap = displayRequest.getConfiguration().getBitmapDecoder().decode(displayRequest, inputStreamCreator);
				}
				if(bitmap != null && !bitmap.isRecycled()){
					//处理位图
					if(displayRequest.getDisplayOptions().getBitmapProcessor() != null){
						ImageView imageView = displayRequest.getImageViewHolder().getImageView();
						Bitmap newBitmap = displayRequest.getDisplayOptions().getBitmapProcessor().process(bitmap, imageView != null?imageView.getScaleType():ScaleType.CENTER_CROP, displayRequest.getTargetSize());
						if(newBitmap != bitmap){
							bitmap.recycle();
							bitmap = newBitmap;
						}
					}
					
					//创建BitmapDrawable
					if (ImageLoaderUtils.hasHoneycomb()) {
						bitmapDrawable = new BitmapDrawable(displayRequest.getConfiguration().getContext().getResources(), bitmap);
					} else {
						bitmapDrawable = new RecyclingBitmapDrawable(displayRequest.getConfiguration().getContext().getResources(), bitmap);
					}
					
					//放入内存缓存中
					if(displayRequest.getDisplayOptions().isEnableMenoryCache()){
						displayRequest.getConfiguration().getBitmapCacher().put(displayRequest.getId(), bitmapDrawable);
					}
				}else{
					onFailed();
				}
			}
		}catch(Throwable throwable){
			throwable.printStackTrace();
		}finally{
			if(displayRequest.getReentrantLock() != null){
				displayRequest.getReentrantLock().unlock();	//释放锁
			}
		}
		return bitmapDrawable;
	}
	
	public abstract InputStreamCreator getInputStreamCreator();
	public abstract void onFailed();
}
