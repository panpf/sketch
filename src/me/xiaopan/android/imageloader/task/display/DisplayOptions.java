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

import me.xiaopan.android.imageloader.display.BitmapDisplayer;
import me.xiaopan.android.imageloader.display.FadeInBitmapDisplayer;
import me.xiaopan.android.imageloader.process.BitmapProcessor;
import me.xiaopan.android.imageloader.task.load.LoadOptions;
import me.xiaopan.android.imageloader.util.ImageSize;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.widget.ImageView.ScaleType;

/**
 * 显示选项
 */
public class DisplayOptions extends LoadOptions {
    private Context context;
	private boolean enableMemoryCache = true;	//是否每次加载图片的时候先从内存中去找，并且加载完成后将图片缓存在内存中
	private BitmapDisplayer displayer;	//位图显示器
    private boolean processLoadingDrawable = true;
    private boolean processLoadFailDrawable = true;
    private boolean processEmptyUriDrawable = true;
	private DrawableHolder emptyUriDrawableHolder;	//当uri为空时显示的图片
	private DrawableHolder loadingDrawableHolder;	//当正在加载时显示的图片
	private DrawableHolder loadFailDrawableHolder;	//当加载失败时显示的图片
	
	public DisplayOptions(Context context) {
        this.context = context;
		this.displayer = new FadeInBitmapDisplayer();
		this.emptyUriDrawableHolder = new DrawableHolder();
		this.loadingDrawableHolder = new DrawableHolder();
		this.loadFailDrawableHolder = new DrawableHolder();
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        super.setMaxSize(new ImageSize(displayMetrics.widthPixels, displayMetrics.heightPixels));
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
	public DisplayOptions setEnableMemoryCache(boolean enableMemoryCache) {
		this.enableMemoryCache = enableMemoryCache;
        return this;
	}

	/**
	 * 获取uri为null或空时显示的图片，此图片是经过BitmapProcessor处理之后的
	 * @return uri为null或空时显示的图片
	 */
	public BitmapDrawable getEmptyUriDrawable() {
		if(emptyUriDrawableHolder.getDrawable() == null && emptyUriDrawableHolder.getResId() > 0){
			Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), emptyUriDrawableHolder.getResId());
			if(bitmap != null){
				if(processEmptyUriDrawable && getProcessor() != null){
					Bitmap newBitmap = getProcessor().process(bitmap, ScaleType.CENTER_CROP, new ImageSize(bitmap.getWidth(), bitmap.getHeight()));
					if(newBitmap != bitmap){
						bitmap.recycle();
                        bitmap = newBitmap;
					}
				}
				emptyUriDrawableHolder.setDrawable(new BitmapDrawable(context.getResources(), bitmap));
			}
		}
		return emptyUriDrawableHolder.getDrawable();
	}

	/**
	 * 设置uri为null或空时显示的图片的资源ID，此图片在通过get方法返回之前会经过BitmapProcessor处理
	 * @param resId uri为null或空是显示的图片的资源ID
	 */
	public DisplayOptions setEmptyUriDrawable(int resId) {
		emptyUriDrawableHolder.setResId(resId);
		if(emptyUriDrawableHolder.getDrawable() != null){
			if(!emptyUriDrawableHolder.getDrawable().getBitmap().isRecycled()){
				emptyUriDrawableHolder.getDrawable().getBitmap().recycle();
			}
			emptyUriDrawableHolder.setDrawable(null);
		}
        return this;
	}

	/**
	 * 获取默认图片，此图片是经过BitmapProcessor处理之后的
	 * @return 默认图片
	 */
	public BitmapDrawable getLoadingDrawable() {
		if(loadingDrawableHolder.getDrawable() == null && loadingDrawableHolder.getResId() > 0){
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), loadingDrawableHolder.getResId());
			if(bitmap != null){
				if(processLoadingDrawable && getProcessor() != null){
					Bitmap newBitmap = getProcessor().process(bitmap, ScaleType.CENTER_CROP, new ImageSize(bitmap.getWidth(), bitmap.getHeight()));
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
	 * 设置默认图片的资源ID，此图片在通过get方法返回之前会经过BitmapProcessor处理
	 * @param resId 默认图片的资源ID
	 */
	public DisplayOptions setLoadingDrawable(int resId) {
		loadingDrawableHolder.setResId(resId);
		if(loadingDrawableHolder.getDrawable() != null){
			if(!loadingDrawableHolder.getDrawable().getBitmap().isRecycled()){
				loadingDrawableHolder.getDrawable().getBitmap().recycle();
			}
			loadingDrawableHolder.setDrawable(null);
		}
        return this;
	}

	/**
	 * 获取加载失败时显示的图片，此图片是经过BitmapProcessor处理之后的
	 * @return 加载失败时显示的图片
	 */
	public BitmapDrawable getLoadFailDrawable() {
		if(loadFailDrawableHolder.getDrawable() == null && loadFailDrawableHolder.getResId() > 0){
			Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), loadFailDrawableHolder.getResId());
			if(bitmap != null){
				if(processLoadFailDrawable && getProcessor() != null){
					Bitmap newBitmap = getProcessor().process(bitmap, ScaleType.CENTER_CROP, new ImageSize(bitmap.getWidth(), bitmap.getHeight()));
					if(newBitmap != bitmap){
						bitmap.recycle();
						bitmap = newBitmap;
					}
				}
				loadFailDrawableHolder.setDrawable(new BitmapDrawable(context.getResources(), bitmap));
			}
		}
		return loadFailDrawableHolder.getDrawable();
	}

	/**
	 * 设置加载失败时显示的图片的资源ID，此图片在通过get方法返回之前会经过BitmapProcessor处理
	 * @param resId 在加载失败时显示的图片的资源ID
	 */
	public DisplayOptions setLoadFailDrawable(int resId) {
		loadFailDrawableHolder.setResId(resId);
		if(loadFailDrawableHolder.getDrawable() != null){
			if(!loadFailDrawableHolder.getDrawable().getBitmap().isRecycled()){
				loadFailDrawableHolder.getDrawable().getBitmap().recycle();
			}
			loadFailDrawableHolder.setDrawable(null);
		}
        return this;
	}

	/**
	 * 获取位图显示器
	 * @return 图片显示器，用来最后关头显示图片
	 */
	public BitmapDisplayer getDisplayer() {
		if(displayer == null){
			displayer = new FadeInBitmapDisplayer();
		}
		return displayer;
	}

	/**
	 * 设置位图显示器
	 * @param displayer 图片显示器，用来最后关头显示图片
	 */
	public DisplayOptions setDisplayer(BitmapDisplayer displayer) {
		this.displayer = displayer;
        return this;
	}

    @Override
    public DisplayOptions setProcessor(BitmapProcessor processor) {
        super.setProcessor(processor);
        if(processLoadingDrawable){
            loadingDrawableHolder.setDrawable(null);
        }
        if(processLoadFailDrawable){
            loadFailDrawableHolder.setDrawable(null);
        }
        if(processEmptyUriDrawable){
            emptyUriDrawableHolder.setDrawable(null);
        }
        return this;
    }

    @Override
    public DisplayOptions setScaleType(ScaleType scaleType) {
        super.setScaleType(scaleType);
        return this;
    }

    @Override
    public DisplayOptions setMaxSize(ImageSize maxSize) {
        super.setMaxSize(maxSize);
        return this;
    }

    @Override
    public DisplayOptions setEnableDiskCache(boolean enableDiskCache) {
        super.setEnableDiskCache(enableDiskCache);
        return this;
    }

    @Override
    public DisplayOptions setDiskCachePeriodOfValidity(long diskCachePeriodOfValidity) {
        super.setDiskCachePeriodOfValidity(diskCachePeriodOfValidity);
        return this;
    }

    @Override
    public DisplayOptions setMaxRetryCount(int maxRetryCount) {
        super.setMaxRetryCount(maxRetryCount);
        return this;
    }

    @Override
	public DisplayOptions setProcessSize(ImageSize processSize) {
		super.setProcessSize(processSize);
        return this;
	}

	/**
     * 设置是否使用BitmapProcessor处理加载中图片
     * @param processLoadingDrawable 是否使用BitmapProcessor来处理加载中图片
     */
    public DisplayOptions setProcessLoadingDrawable(boolean processLoadingDrawable) {
        this.processLoadingDrawable = processLoadingDrawable;
        return this;
    }

    /**
     * 设置是否使用BitmapProcessor处理加载失败图片
     * @param processLoadFailDrawable 是否使用BitmapProcessor来处理加载失败图片
     */
    public DisplayOptions setProcessLoadFailDrawable(boolean processLoadFailDrawable) {
        this.processLoadFailDrawable = processLoadFailDrawable;
        return this;
    }

    /**
     * 设置是否使用BitmapProcessor处理URI为空图片
     * @param processEmptyUriDrawable 是否使用BitmapProcessor来处理URI为空图片
     */
    public DisplayOptions setProcessEmptyUriDrawable(boolean processEmptyUriDrawable) {
        this.processEmptyUriDrawable = processEmptyUriDrawable;
        return this;
    }

    @Override
	public DisplayOptions copy(){
        return new DisplayOptions(context)
            .setMaxRetryCount(getMaxRetryCount())
            .setDiskCachePeriodOfValidity(getDiskCachePeriodOfValidity())
            .setEnableDiskCache(isEnableDiskCache())

            .setScaleType(getScaleType())
            .setMaxSize(getMaxSize() != null ? getMaxSize().copy() : null)
            .setProcessor(getProcessor() != null ? getProcessor().copy() : null)

            .setEnableMemoryCache(enableMemoryCache)
            .setDisplayer(displayer != null ? displayer.copy() : null)
            .setEmptyUriDrawable(emptyUriDrawableHolder.getResId())
            .setLoadFailDrawable(loadFailDrawableHolder.getResId())
            .setLoadingDrawable(loadingDrawableHolder.getResId())
            .setProcessLoadingDrawable(processLoadingDrawable)
            .setProcessLoadFailDrawable(processLoadFailDrawable)
            .setProcessEmptyUriDrawable(processEmptyUriDrawable);
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
