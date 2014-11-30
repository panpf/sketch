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

import android.content.Context;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import me.xiaopan.android.spear.cache.DiskCache;
import me.xiaopan.android.spear.cache.LruDiskCache;
import me.xiaopan.android.spear.cache.LruMemoryCache;
import me.xiaopan.android.spear.cache.MemoryCache;
import me.xiaopan.android.spear.decode.DefaultImageDecoder;
import me.xiaopan.android.spear.decode.ImageDecoder;
import me.xiaopan.android.spear.download.HttpUrlConnectionImageDownloader;
import me.xiaopan.android.spear.download.ImageDownloader;
import me.xiaopan.android.spear.execute.DefaultRequestExecutor;
import me.xiaopan.android.spear.execute.RequestExecutor;
import me.xiaopan.android.spear.request.DisplayCallbackHandler;
import me.xiaopan.android.spear.request.RequestOptions;
import me.xiaopan.android.spear.util.BaseDefaultProperty;
import me.xiaopan.android.spear.util.DefaultImageSizeCalculator;
import me.xiaopan.android.spear.util.DefaultProperty;
import me.xiaopan.android.spear.util.DisplayHelperManager;
import me.xiaopan.android.spear.util.ImageSizeCalculator;

public class Configuration {
    private Context context;	//上下文
    private DiskCache diskCache;    // 磁盘缓存器
    private MemoryCache memoryCache;	//图片缓存器
    private ImageDecoder imageDecoder;	//图片解码器
    private ImageDownloader imageDownloader;	//图片下载器
    private DefaultProperty defaultProperty;    // 默认属性
    private RequestExecutor requestExecutor;	//请求执行器
    private ImageSizeCalculator imageSizeCalculator; // 图片尺寸计算器
    private DisplayHelperManager displayHelperManager;  // DisplayHelper管理器
    private DisplayCallbackHandler displayCallbackHandler;	//显示相关回调处理器

    public Configuration(Context context){
        this.context = context;
        this.diskCache = new LruDiskCache(context);
        this.memoryCache = new LruMemoryCache();
        this.imageDecoder = new DefaultImageDecoder();
        this.imageDownloader = new HttpUrlConnectionImageDownloader();
        this.defaultProperty = new BaseDefaultProperty();
        this.requestExecutor = new DefaultRequestExecutor.Builder().build();
        this.imageSizeCalculator = new DefaultImageSizeCalculator();
        this.displayHelperManager = new DisplayHelperManager();
        this.displayCallbackHandler = new DisplayCallbackHandler();
    }

    /**
     * 获取上下文
     * @return 上下文
     */
    public Context getContext() {
        return context;
    }

    /**
     * 获取默认的属性
     * @return 默认属性
     */
    public DefaultProperty getDefaultProperty() {
        return defaultProperty;
    }

    /**
     * 获取请求执行器
     * @return 请求执行器
     */
    public RequestExecutor getRequestExecutor() {
        return requestExecutor;
    }

    /**
     * 获取磁盘缓存器
     * @return 磁盘缓存器
     */
    public DiskCache getDiskCache() {
        return diskCache;
    }

    /**
     * 获取内存缓存器
     * @return 内存缓存器
     */
    public MemoryCache getMemoryCache() {
        return memoryCache;
    }

    /**
     * 获取位图解码器
     * @return 位图解码器
     */
    public ImageDecoder getImageDecoder() {
        return imageDecoder;
    }

    /**
     * 获取显示相关回调处理器
     * @return 显示相关回调处理器
     */
    public DisplayCallbackHandler getDisplayCallbackHandler() {
        return displayCallbackHandler;
    }

    /**
     * 获取图片下载器
     */
    public ImageDownloader getImageDownloader() {
        return imageDownloader;
    }

    /**
     * 获取图片尺寸计算器
     * @return 图片尺寸计算器
     */
    public ImageSizeCalculator getImageSizeCalculator() {
        return imageSizeCalculator;
    }

    /**
     * 获取DisplayHelper管理器
     * @return DisplayHelper管理器
     */
    public DisplayHelperManager getDisplayHelperManager() {
        return displayHelperManager;
    }

    /**
     * 根据URI获取缓存文件
     */
    public File getCacheFileByUri(String uri){
        if(diskCache == null){
            return null;
        }
        return diskCache.getCacheFileByUri(uri);
    }



    /**
     * 设置默认的属性
     * @param defaultProperty 默认的属性
     */
    public void setDefaultProperty(DefaultProperty defaultProperty) {
        this.defaultProperty = defaultProperty;
    }

    /**
     * 设置请求执行器
     * @param requestExecutor 请求执行器
     */
    public Configuration setRequestExecutor(RequestExecutor requestExecutor) {
        if(requestExecutor != null){
            this.requestExecutor = requestExecutor;
        }
        return this;
    }

    /**
     * 设置磁盘缓存器
     * @param diskCache 磁盘缓存器
     */
    public Configuration setDiskCache(DiskCache diskCache) {
        if(diskCache != null){
            this.diskCache = diskCache;
        }
        return this;
    }

    /**
     * 设置内存缓存器
     * @param memoryCache 内存缓存器
     */
    public Configuration setMemoryCache(MemoryCache memoryCache) {
        if(memoryCache != null){
            this.memoryCache = memoryCache;
        }
        return this;
    }

    /**
     * 设置位图解码器
     * @param imageDecoder 位图解码器
     */
    public Configuration setImageDecoder(ImageDecoder imageDecoder) {
        if(imageDecoder != null){
            this.imageDecoder = imageDecoder;
        }
        return this;
    }

    /**
     * 设置图片下载器
     * @param imageDownloader 图片下载器
     */
    public Configuration setImageDownloader(ImageDownloader imageDownloader) {
        if(imageDownloader != null){
            this.imageDownloader = imageDownloader;
        }
        return this;
    }

    /**
     * 获取图片尺寸计算器
     * @param imageSizeCalculator 图片尺寸计算器
     */
    public Configuration setImageSizeCalculator(ImageSizeCalculator imageSizeCalculator) {
        if(imageSizeCalculator != null){
            this.imageSizeCalculator = imageSizeCalculator;
        }
        return this;
    }

    /**
     * 清除内存缓存和磁盘缓存
     */
    public void clearAllCache() {
        if(memoryCache != null){
            memoryCache.clear();
        }
        if(diskCache != null){
            diskCache.clear();
        }
    }
}
