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

import android.graphics.drawable.BitmapDrawable;

import me.xiaopan.android.spear.display.ImageDisplayer;
import me.xiaopan.android.spear.execute.RequestExecutor;
import me.xiaopan.android.spear.process.ImageProcessor;
import me.xiaopan.android.spear.util.DrawableHolder;
import me.xiaopan.android.spear.util.FailureCause;
import me.xiaopan.android.spear.util.ImageViewHolder;

/**
 * 显示请求
 */
public class DisplayRequest extends LoadRequest{
    public static final boolean DEFAULT_ENABLE_MEMORY_CACHE = true;

    /* 显示请求用到的属性 */
    private String memoryCacheId;	//内存缓存ID
    private boolean enableMemoryCache = DEFAULT_ENABLE_MEMORY_CACHE;	//是否每次加载图片的时候先从内存中去找，并且加载完成后将图片缓存在内存中
    private ImageDisplayer imageDisplayer;	//图片显示器
    private DrawableHolder loadFailDrawableHolder;	//当加载失败时显示的图片
    private DisplayListener displayListener;	//监听器

    /* 辅助的属性 */
    private boolean resizeByImageViewLayoutSizeAndFromDisplayer;
    private FailureCause failureCause;
    private ImageViewHolder imageViewHolder;	//ImageView持有器
    private BitmapDrawable resultBitmap;
    private DisplayListener.ImageFrom imageFrom;

    /**
     * 获取内存缓存ID，此ID用来在内存缓存Bitmap时作为其KEY
     * @return ID
     */
	public String getMemoryCacheId() {
		return memoryCacheId;
	}

    /**
     * 设置内存缓存ID
     * @param memoryCacheId 内存缓存ID
     */
    public void setMemoryCacheId(String memoryCacheId) {
        this.memoryCacheId = memoryCacheId;
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
     * 是否开启内存缓存（默认开启）
     * @return 是否开启内存缓存
     */
    public boolean isEnableMemoryCache() {
        return enableMemoryCache;
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
     * 获取加载失败图片持有期器
     * @return 加载失败图片持有期器
     */
    public DrawableHolder getLoadFailDrawableHolder() {
        return loadFailDrawableHolder;
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
            processor = getSpear().getConfiguration().getDefaultCutImageProcessor();
        }else{
            processor = null;
        }
        return loadFailDrawableHolder.getDrawable(getSpear().getConfiguration().getContext(), getResize(), getScaleType(), processor, resizeByImageViewLayoutSizeAndFromDisplayer);
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
     * 获取失败原因
     * @return 失败原因
     */
    public FailureCause getFailureCause() {
        return failureCause;
    }

    /**
     * 设置失败原因
     * @param failureCause 失败原因
     */
    public void setFailureCause(FailureCause failureCause) {
        this.failureCause = failureCause;
    }

    /**
     * 获取结果图片来源
     * @return 结果图片来源
     */
    public DisplayListener.ImageFrom getImageFrom() {
        return imageFrom;
    }

    /**
     * 设置结果图片来源
     * @param imageFrom 结果图片来源
     */
    public void setImageFrom(DisplayListener.ImageFrom imageFrom) {
        this.imageFrom = imageFrom;
    }

    /**
     * resize是否来自ImageView的LayoutSize并且来自Displayer
     */
    public boolean isResizeByImageViewLayoutSizeAndFromDisplayer() {
        return resizeByImageViewLayoutSizeAndFromDisplayer;
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
                setStatus(Status.CANCELED);
            }
        }
        return isCanceled;
    }

    @Override
    public void dispatch(RequestExecutor requestExecutor) {
        setLoadListener(new DisplayJoinLoadListener(this));
        super.dispatch(requestExecutor);
    }

    @Override
    public void updateProgress(int totalLength, int completedLength) {
        if(getProgressListener() != null){
            getSpear().getConfiguration().getDisplayCallbackHandler().updateProgressCallback(this, totalLength, completedLength);
        }
    }
}
