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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.widget.ImageView.ScaleType;
import me.xiaopan.android.imageloader.display.BitmapDisplayer;
import me.xiaopan.android.imageloader.display.FadeInBitmapDisplayer;
import me.xiaopan.android.imageloader.process.BitmapProcessor;
import me.xiaopan.android.imageloader.task.load.LoadOptions;
import me.xiaopan.android.imageloader.util.ImageSize;

/**
 * 显示选项
 */
public class DisplayOptions extends LoadOptions {
    private Context context;
	private boolean enableMemoryCache = true;	//是否每次加载图片的时候先从内存中去找，并且加载完成后将图片缓存在内存中
	private BitmapDisplayer bitmapDisplayer;	//位图显示器
	private DrawableHolder emptyDrawableHolder;	//当uri为空时显示的图片
	private DrawableHolder loadingDrawableHolder;	//当正在加载时显示的图片
	private DrawableHolder failureDrawableHolder;	//当加载失败时显示的图片
	
	public DisplayOptions(Context context) {
        this.context = context;
		this.bitmapDisplayer = new FadeInBitmapDisplayer();
		this.emptyDrawableHolder = new DrawableHolder();
		this.loadingDrawableHolder = new DrawableHolder();
		this.failureDrawableHolder = new DrawableHolder();
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        super.setMaxImageSize(new ImageSize(displayMetrics.widthPixels, displayMetrics.heightPixels));
	}

	/**
	 * 是否将Bitmap缓存到内存中
	 * @return true：会将解码得到的Bitmap缓存到内存中，并在显示的时候首先从内存中去取
	 */
	public boolean isEnableMemoryCache() {
		return enableMemoryCache;
	}

	/**
	 * 设置是否将Bitmap缓存到内存中
	 * @param enableMemoryCache true：会将解码得到的Bitmap缓存到内存中，并在显示的时候首先从内存中去取
	 */
	public void setEnableMemoryCache(boolean enableMemoryCache) {
		this.enableMemoryCache = enableMemoryCache;
	}

	/**
	 * 获取加载地址为空时显示的图片，此图片是经过BitmapProcessor处理之后的
	 * @return 当uri为null或空时显示的图片
	 */
	public BitmapDrawable getEmptyDrawable() {
		if(emptyDrawableHolder.getDrawable() == null && emptyDrawableHolder.getResId() > 0){
			Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), emptyDrawableHolder.getResId());
			if(bitmap != null){
				if(getBitmapProcessor() != null){
					Bitmap newBitmap = getBitmapProcessor().process(bitmap, ScaleType.CENTER_CROP, new ImageSize(bitmap.getWidth(), bitmap.getHeight()));
					if(newBitmap != bitmap){
						bitmap.recycle();
                        bitmap = newBitmap;
					}
				}
				emptyDrawableHolder.setDrawable(new BitmapDrawable(context.getResources(), bitmap));
			}
		}
		return emptyDrawableHolder.getDrawable();
	}

	/**
	 * 设置加载地址为空时显示的图片的资源ID，此图片在通过get方法返回之前会经过BitmapProcessor处理
	 * @param resId 当uri为null或空是显示的图片的资源ID
	 */
	public void setEmptyDrawableResId(int resId) {
		emptyDrawableHolder.setResId(resId);
		if(emptyDrawableHolder.getDrawable() != null){
			if(!emptyDrawableHolder.getDrawable().getBitmap().isRecycled()){
				emptyDrawableHolder.getDrawable().getBitmap().recycle();
			}
			emptyDrawableHolder.setDrawable(null);
		}
	}

	/**
	 * 获取加载中图片，此图片是经过BitmapProcessor处理之后的
	 * @return 正在加载时显示的图片
	 */
	public BitmapDrawable getLoadingDrawable() {
		if(loadingDrawableHolder.getDrawable() == null && loadingDrawableHolder.getResId() > 0){
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), loadingDrawableHolder.getResId());
			if(bitmap != null){
				if(getBitmapProcessor() != null){
					Bitmap newBitmap = getBitmapProcessor().process(bitmap, ScaleType.CENTER_CROP, new ImageSize(bitmap.getWidth(), bitmap.getHeight()));
                    if(newBitmap != bitmap){
                        bitmap.recycle();
                        bitmap = newBitmap;
                    }
				}
				loadingDrawableHolder.setDrawable(new BitmapDrawable(context.getResources(), bitmap));
			}
		}
		return loadingDrawableHolder.getDrawable();
	}

	/**
	 * 设置加载中图片的资源ID，此图片在通过get方法返回之前会经过BitmapProcessor处理
	 * @param resId 在加载时显示的图片的资源ID
	 */
	public void setLoadingDrawableResId(int resId) {
		loadingDrawableHolder.setResId(resId);
		if(loadingDrawableHolder.getDrawable() != null){
			if(!loadingDrawableHolder.getDrawable().getBitmap().isRecycled()){
				loadingDrawableHolder.getDrawable().getBitmap().recycle();
			}
			loadingDrawableHolder.setDrawable(null);
		}
	}

	/**
	 * 获取加载失败图片，此图片是经过BitmapProcessor处理之后的
	 * @return 加载失败时显示的图片
	 */
	public BitmapDrawable getFailureDrawable() {
		if(failureDrawableHolder.getDrawable() == null && failureDrawableHolder.getResId() > 0){
			Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), failureDrawableHolder.getResId());
			if(bitmap != null){
				if(getBitmapProcessor() != null){
					Bitmap newBitmap = getBitmapProcessor().process(bitmap, ScaleType.CENTER_CROP, new ImageSize(bitmap.getWidth(), bitmap.getHeight()));
					if(newBitmap != bitmap){
						bitmap.recycle();
						bitmap = newBitmap;
					}
				}
				failureDrawableHolder.setDrawable(new BitmapDrawable(context.getResources(), bitmap));
			}
		}
		return failureDrawableHolder.getDrawable();
	}

	/**
	 * 设置加载失败图片的资源ID，此图片在通过get方法返回之前会经过BitmapProcessor处理
	 * @param resId 在加载失败时显示的图片
	 */
	public void setFailureDrawableResId(int resId) {
		failureDrawableHolder.setResId(resId);
		if(failureDrawableHolder.getDrawable() != null){
			if(!failureDrawableHolder.getDrawable().getBitmap().isRecycled()){
				failureDrawableHolder.getDrawable().getBitmap().recycle();
			}
			failureDrawableHolder.setDrawable(null);
		}
	}

	/**
	 * 获取位图显示器
	 * @return 图片显示器，用来最后关头显示图片
	 */
	public BitmapDisplayer getBitmapDisplayer() {
		if(bitmapDisplayer == null){
			bitmapDisplayer = new FadeInBitmapDisplayer();
		}
		return bitmapDisplayer;
	}

	/**
	 * 设置位图显示器
	 * @param bitmapDisplayer 图片显示器，用来最后关头显示图片
	 */
	public void setBitmapDisplayer(BitmapDisplayer bitmapDisplayer) {
		this.bitmapDisplayer = bitmapDisplayer;
	}

    @Override
    public void setBitmapProcessor(BitmapProcessor bitmapProcessor) {
        super.setBitmapProcessor(bitmapProcessor);
        emptyDrawableHolder.setDrawable(null);
        loadingDrawableHolder.setDrawable(null);
        failureDrawableHolder.setDrawable(null);
    }

	/**
	 * 将当前的DisplayOptions拷贝一份
	 * @return DisplayOptions
	 */
	public DisplayOptions copy(){
		DisplayOptions displayOptions = new DisplayOptions(context);
		displayOptions.setMaxRetryCount(getMaxRetryCount());
        displayOptions.setDiskCachePeriodOfValidity(getDiskCachePeriodOfValidity());
        displayOptions.setEnableDiskCache(isEnableDiskCache());
		displayOptions.setEnableMemoryCache(enableMemoryCache);
        displayOptions.setBitmapProcessor(getBitmapProcessor() != null?getBitmapProcessor().copy():null);
        displayOptions.setBitmapDisplayer(bitmapDisplayer != null?bitmapDisplayer.copy():null);
        displayOptions.setEmptyDrawableResId(emptyDrawableHolder.getResId());
        displayOptions.setFailureDrawableResId(failureDrawableHolder.getResId());
        displayOptions.setLoadingDrawableResId(loadingDrawableHolder.getResId());
        displayOptions.setMaxImageSize(getMaxImageSize() != null ? getMaxImageSize().copy() : null);
		return displayOptions;
	}
	
	private class DrawableHolder {
		private int resId;	//当正在加载时显示的图片
		private BitmapDrawable drawable;	//当加载地址为空时显示的图片
		
		public int getResId() {
			return resId;
		}

		public void setResId(int resId) {
			this.resId = resId;
		}

		public BitmapDrawable getDrawable() {
			return drawable;
		}

		public void setDrawable(BitmapDrawable drawable) {
			this.drawable = drawable;
		}
	}
}
