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

package me.xiaopan.android.spear;

import android.graphics.drawable.BitmapDrawable;

import me.xiaopan.android.spear.display.ImageDisplayer;

/**
 * 显示请求
 */
public interface DisplayRequest extends LoadRequest{
    /**
     * 获取内存缓存ID
     * @return 内存缓存ID
     */
	String getMemoryCacheId();

    /**
     * 设置是否开启内存缓存（默认开启）
     * @param enableMemoryCache 是否开启内存缓存
     */
    void setEnableMemoryCache(boolean enableMemoryCache);

    /**
     * 设置图片显示器（用于在图片加载完成后显示图片）
     * @param imageDisplayer 图片显示器
     */
    void setImageDisplayer(ImageDisplayer imageDisplayer);

    /**
     * 设置加载失败图片持有期器
     * @param loadFailDrawableHolder 加载失败图片持有期器
     */
    void setLoadFailDrawableHolder(DrawableHolder loadFailDrawableHolder);

    /**
     * 设置暂停下载图片
     * @param pauseDownloadDrawableHolder 暂停下载图片
     */
    void setPauseDownloadDrawableHolder(DrawableHolder pauseDownloadDrawableHolder);

    /**
     * 获取加载失败时显示的图片
     * @return 加载失败时显示的图片
     */
    BitmapDrawable getLoadFailDrawable();

    /**
     * 获取暂停下载时显示的图片
     * @return 暂停下载时显示的图片
     */
    BitmapDrawable getPauseDownloadDrawable();

    /**
     * 设置显示监听器
     * @param displayListener 显示监听器
     */
    void setDisplayListener(DisplayListener displayListener);

    /**
     * 设置请求处理级别
     * @param requestHandleLevel 请求处理级别
     */
    void setRequestHandleLevel(RequestHandleLevel requestHandleLevel);

    /**
     * 在主线程处理完成
     */
    void handleCompletedOnMainThread();

    /**
     * 执行失败
     */
    void handleFailedOnMainThread();

    /**
     * 在主线程处理取消
     */
    void handleCanceledOnMainThread();

    /**
     * 在主线程处理暂停下载
     */
    void handlePauseDownloadOnMainThread();

    /**
     * 在主线程更新进度
     */
    void updateProgressOnMainThread(int totalLength, int completedLength);

    /**
     * 设置Level是否来自暂停下载
     * @param levelFromPauseDownload Level是否来自暂停下载
     */
    void setLevelFromPauseDownload(boolean levelFromPauseDownload);

    /**
     * 是否是一张gif图
     */
    void setThisIsGifImage(boolean loadGifDrawable);
}