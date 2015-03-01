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

package me.xiaopan.android.spear.request;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;

import me.xiaopan.android.spear.display.ImageDisplayer;
import me.xiaopan.android.spear.process.ImageProcessor;
import me.xiaopan.android.spear.util.DrawableHolder;
import me.xiaopan.android.spear.util.ImageViewHolder;
import me.xiaopan.android.spear.util.RecyclingBitmapDrawable;

/**
 * 显示请求
 */
public class DisplayRequest extends LoadRequest{
    private static final String NAME = "DisplayCallbackHandler";
    public static final boolean DEFAULT_ENABLE_MEMORY_CACHE = true;

    /* 显示请求用到的属性 */
    private String id;	//内存缓存ID
    private boolean enableMemoryCache = DEFAULT_ENABLE_MEMORY_CACHE;	//是否每次加载图片的时候先从内存中去找，并且加载完成后将图片缓存在内存中
    private ImageDisplayer imageDisplayer;	//图片显示器
    private DrawableHolder loadFailDrawableHolder;	//当加载失败时显示的图片
    private DisplayListener displayListener;	//监听器

    /* 辅助的属性 */
    private boolean resizeByImageViewLayoutSizeAndFromDisplayer;
    private ImageViewHolder imageViewHolder;	//ImageView持有器
    private BitmapDrawable resultBitmap;
    private ImageFrom imageFrom;

    /**
     * 获取请求ID
     * @return 请求ID
     */
	public String getId() {
		return id;
	}

    /**
     * 设置请求ID
     * @param id 请求ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取ImageView持有器
     * @return ImageView持有器
     */
	public ImageViewHolder getImageViewHolder() {
		return imageViewHolder;
	}

    /**
     * 设置ImageView持有器
     * @param imageViewHolder ImageView持有器
     */
    public void setImageViewHolder(ImageViewHolder imageViewHolder) {
        this.imageViewHolder = imageViewHolder;
    }

    /**
     * 设置是否开启内存缓存（默认开启）
     * @param enableMemoryCache 是否开启内存缓存
     */
    public void setEnableMemoryCache(boolean enableMemoryCache) {
        this.enableMemoryCache = enableMemoryCache;
    }

    /**
     * 获取图片显示器（用于在图片加载完成后显示图片）
     * @return 图片显示器
     */
    public ImageDisplayer getImageDisplayer() {
        return imageDisplayer;
    }

    /**
     * 设置图片显示器（用于在图片加载完成后显示图片）
     * @param imageDisplayer 图片显示器
     */
    public void setImageDisplayer(ImageDisplayer imageDisplayer) {
        this.imageDisplayer = imageDisplayer;
    }

    /**
     * 设置加载失败图片持有期器
     * @param loadFailDrawableHolder 加载失败图片持有期器
     */
    public void setLoadFailDrawableHolder(DrawableHolder loadFailDrawableHolder) {
        this.loadFailDrawableHolder = loadFailDrawableHolder;
    }

    /**
     * 获取加载失败时显示的图片
     * @return 加载失败时显示的图片
     */
    public BitmapDrawable getLoadFailDrawable() {
        if(loadFailDrawableHolder == null){
            return null;
        }
        ImageProcessor processor;
        if(getImageProcessor() != null){
            processor = getImageProcessor();
        }else if(getResize() != null){
            processor = spear.getConfiguration().getDefaultCutImageProcessor();
        }else{
            processor = null;
        }
        return loadFailDrawableHolder.getDrawable(spear.getConfiguration().getContext(), getResize(), getScaleType(), processor, resizeByImageViewLayoutSizeAndFromDisplayer);
    }

    /**
     * 获取显示监听器
     * @return 显示监听器
     */
    public DisplayListener getDisplayListener() {
        return displayListener;
    }

    /**
     * 设置显示监听器
     * @param displayListener 显示监听器
     */
    public void setDisplayListener(DisplayListener displayListener) {
        this.displayListener = displayListener;
    }

    /**
     * 获取结果图片
     * @return 结果图片
     */
    public BitmapDrawable getResultBitmap() {
        return resultBitmap;
    }

    /**
     * 设置结果图片
     * @param resultBitmap 结果图片
     */
    public void setResultBitmap(BitmapDrawable resultBitmap) {
        this.resultBitmap = resultBitmap;
    }

    /**
     * 获取结果图片来源
     * @return 结果图片来源
     */
    public ImageFrom getImageFrom() {
        return imageFrom;
    }

    /**
     * 设置结果图片来源
     * @param imageFrom 结果图片来源
     */
    public void setImageFrom(ImageFrom imageFrom) {
        this.imageFrom = imageFrom;
    }

    /**
     * 设置resize是否来自ImageView的LayoutSize并且来自Displayer
     * @param resizeByImageViewLayoutSizeAndFromDisplayer resize是否来自ImageView的LayoutSize并且来自Displayer
     */
    public void setResizeByImageViewLayoutSizeAndFromDisplayer(boolean resizeByImageViewLayoutSizeAndFromDisplayer) {
        this.resizeByImageViewLayoutSizeAndFromDisplayer = resizeByImageViewLayoutSizeAndFromDisplayer;
    }

    @Override
    public boolean isCanceled() {
        boolean isCanceled = super.isCanceled();
        if(!isCanceled){
            isCanceled = imageViewHolder != null && imageViewHolder.isCollected();
            if(isCanceled){
                toCanceledStatus();
            }
        }
        return isCanceled;
    }

    @Override
    public void handleUpdateProgress(int totalLength, int completedLength) {
        if(progressListener != null){
            spear.getConfiguration().getDisplayCallbackHandler().updateProgressCallback(this, totalLength, completedLength);
        }
    }

    @Override
    public void handleLoadCompleted(Bitmap bitmap, ImageFrom imageFrom) {
        //创建BitmapDrawable并放入内存缓存
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            resultBitmap = new BitmapDrawable(spear.getConfiguration().getContext().getResources(), bitmap);
        } else {
            resultBitmap = new RecyclingBitmapDrawable(spear.getConfiguration().getContext().getResources(), bitmap);
        }
        if(enableMemoryCache){
            if(resultBitmap instanceof RecyclingBitmapDrawable){
                ((RecyclingBitmapDrawable) resultBitmap).setIsCached(true);
            }
            spear.getConfiguration().getMemoryCache().put(id, resultBitmap);
        }

        // 显示
        toCompletedStatus();
    }

    @Override
    public void toCompletedStatus() {
        spear.getConfiguration().getDisplayCallbackHandler().completeCallback(this);
    }

    @Override
    public void toFailedStatus(FailureCause failureCause) {
        spear.getConfiguration().getDisplayCallbackHandler().failCallback(this);
    }

    @Override
    public void toCanceledStatus() {
        this.status = Status.CANCELED;
        if(displayListener != null){
            spear.getConfiguration().getDisplayCallbackHandler().cancelCallback(displayListener);
        }
    }
}