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

package me.xiaopan.sketch;

import android.graphics.drawable.Drawable;

import me.xiaopan.sketch.display.ImageDisplayer;

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
     * 设置是否将图片缓存在内存中
     * @param cacheInMemory 是否将图片缓存在内存中（默认是）
     */
    void setCacheInMemory(boolean cacheInMemory);

    /**
     * 设置图片显示器（用于在图片加载完成后显示图片）
     * @param imageDisplayer 图片显示器
     */
    void setImageDisplayer(ImageDisplayer imageDisplayer);

    /**
     * 设置失败图片持有器
     * @param failureImageHolder 失败图片持有器
     */
    void setFailureImageHolder(ImageHolder failureImageHolder);

    /**
     * 设置暂停下载图片
     * @param pauseDownloadImageHolder 暂停下载图片
     */
    void setPauseDownloadImageHolder(ImageHolder pauseDownloadImageHolder);

    /**
     * 获取失败时显示的图片
     * @return 失败时显示的图片
     */
    Drawable getFailureDrawable();

    /**
     * 获取暂停下载时显示的图片
     * @return 暂停下载时显示的图片
     */
    Drawable getPauseDownloadDrawable();

    /**
     * 设置显示监听器
     * @param displayListener 显示监听器
     */
    void setDisplayListener(DisplayListener displayListener);

    /**
     * 设置固定尺寸，用于显示图片的时候用
     * @param fixedSize 固定尺寸
     */
    void setFixedSize(FixedSize fixedSize);
}