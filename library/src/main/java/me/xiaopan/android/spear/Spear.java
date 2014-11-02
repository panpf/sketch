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
import android.net.Uri;
import android.widget.ImageView;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import me.xiaopan.android.spear.cache.disk.DiskCache;
import me.xiaopan.android.spear.cache.disk.LruDiskCache;
import me.xiaopan.android.spear.cache.memory.LruMemoryCache;
import me.xiaopan.android.spear.cache.memory.MemoryCache;
import me.xiaopan.android.spear.decode.DefaultImageDecoder;
import me.xiaopan.android.spear.decode.ImageDecoder;
import me.xiaopan.android.spear.download.HttpClientImageDownloader;
import me.xiaopan.android.spear.download.ImageDownloader;
import me.xiaopan.android.spear.execute.DefaultRequestExecutor;
import me.xiaopan.android.spear.execute.RequestExecutor;
import me.xiaopan.android.spear.request.DisplayCallbackHandler;
import me.xiaopan.android.spear.request.DisplayHelper;
import me.xiaopan.android.spear.request.DisplayRequest;
import me.xiaopan.android.spear.request.DownloadHelper;
import me.xiaopan.android.spear.request.DownloadListener;
import me.xiaopan.android.spear.request.LoadHelper;
import me.xiaopan.android.spear.request.LoadListener;
import me.xiaopan.android.spear.request.RequestOptions;
import me.xiaopan.android.spear.util.AsyncDrawable;
import me.xiaopan.android.spear.util.DefaultImageSizeCalculator;
import me.xiaopan.android.spear.util.DisplayHelperManager;
import me.xiaopan.android.spear.util.ImageSizeCalculator;
import me.xiaopan.android.spear.util.Scheme;

/**
 * 图片加载器，可以从网络或者本地加载图片，并且支持自动清除缓存
 */
public class Spear {
    public static final String LOG_TAG= Spear.class.getSimpleName();
	private static Spear instance;

    private Context context;	//上下文

    private boolean debugMode;	//调试模式，在控制台输出日志
    private DiskCache diskCache;    // 磁盘缓存器
    private MemoryCache memoryCache;	//图片缓存器
    private ImageDecoder imageDecoder;	//图片解码器
    private DisplayHelperManager displayHelperManager;
    private ImageDownloader imageDownloader;	//图片下载器
    private RequestExecutor requestExecutor;	//请求执行器
    private ImageSizeCalculator imageSizeCalculator; // 图片尺寸计算器
    private DisplayCallbackHandler displayCallbackHandler;	//显示相关回调处理器

	private Spear(Context context){
        this.context = context;
        this.diskCache = new LruDiskCache(context);
        this.memoryCache = new LruMemoryCache();
        this.imageDecoder = new DefaultImageDecoder();
        this.displayHelperManager = new DisplayHelperManager();
        this.imageDownloader = new HttpClientImageDownloader();
        this.requestExecutor = new DefaultRequestExecutor.Builder().build();
        this.imageSizeCalculator = new DefaultImageSizeCalculator();
        this.displayCallbackHandler = new DisplayCallbackHandler();
	}

    /**
     * 下载
     * @param uri 支持以下2种类型
     * <blockquote>“http://site.com/image.png“  // from Web
     * <br>“https://site.com/image.png“ // from Web
     * </blockquote>
     * @param downloadListener 下载监听器
     * @return DownloadRequest.Helper 你可以继续设置一些参数，最后调用fire()方法开始下载
     */
	public DownloadHelper download(String uri, DownloadListener downloadListener){
		 return new DownloadHelper(this, uri).listener(downloadListener);
	}



    /**
     * 加载
     * @param uri 支持以下6种类型
     * <blockquote>“http://site.com/image.png“  // from Web
     * <br>“https://site.com/image.png“ // from Web
     * <br>“file:///mnt/sdcard/image.png“ // from SD card
     * <br>“content://media/external/audio/albumart/13“ // from content provider
     * <br>“assets://image.png“ // from assets
     * <br>“drawable://" + R.drawable.image // from drawables
     * </blockquote>
     * @param loadListener 加载监听器
     * @return LoadRequest.Helper 你可以继续设置一些参数，最后调用fire()方法开始加载
     */
	public LoadHelper load(String uri, LoadListener loadListener){
        return new LoadHelper(this, uri).listener(loadListener);
	}
    
    /**
     * 加载
     * @param imageFile 图片文件
     * @param loadListener 加载监听器
     * @return LoadRequest.Helper 你可以继续设置一些参数，最后调用fire()方法开始加载
     */
	public LoadHelper load(File imageFile, LoadListener loadListener){
        return new LoadHelper(this, Scheme.FILE.createUri(imageFile.getPath())).listener(loadListener);
	}

    /**
     * 加载
     * @param drawableResId 图片资源ID
     * @param loadListener 加载监听器
     * @return LoadRequest.Helper 你可以继续设置一些参数，最后调用fire()方法开始加载
     */
	public LoadHelper load(int drawableResId, LoadListener loadListener){
        return new LoadHelper(this, Scheme.DRAWABLE.createUri(String.valueOf(drawableResId))).listener(loadListener);
	}

    /**
     * 加载
     * @param uri 图片资源URI
     * @param loadListener 加载监听器
     * @return LoadRequest.Helper 你可以继续设置一些参数，最后调用fire()方法开始加载
     */
	public LoadHelper load(Uri uri, LoadListener loadListener){
        return new LoadHelper(this, uri.toString()).listener(loadListener);
	}



    /**
     * 显示图片
     * @param uri 支持以下6种类型
     * <blockquote>“http://site.com/image.png“  // from Web
     * <br>“https://site.com/image.png“ // from Web
     * <br>“file:///mnt/sdcard/image.png“ // from SD card
     * <br>“content://media/external/audio/albumart/13“ // from content provider
     * <br>“assets://image.png“ // from assets
     * <br>“drawable://" + R.drawable.image // from drawables
     * </blockquote>
     * @param imageView 显示图片的视图
     * @return DisplayRequest.Helper 你可以继续设置一些参数，最后调用fire()方法开始显示
     */
    public DisplayHelper display(String uri, ImageView imageView){
        return displayHelperManager.getDisplayHelper(this, uri, imageView);
    }

    /**
     * 显示图片
     * @param imageFile 图片文件
     * @param imageView 显示图片的视图
     * @return DisplayRequest.Helper 你可以继续设置一些参数，最后调用fire()方法开始显示
     */
    public DisplayHelper display(File imageFile, ImageView imageView){
        return displayHelperManager.getDisplayHelper(this, Scheme.FILE.createUri(imageFile.getPath()), imageView);
    }

    /**
     * 显示图片
     * @param drawableResId 图片资源ID
     * @param imageView 显示图片的视图
     * @return DisplayRequest.Helper 你可以继续设置一些参数，最后调用fire()方法开始显示
     */
    public DisplayHelper display(int drawableResId, ImageView imageView){
        return displayHelperManager.getDisplayHelper(this, Scheme.DRAWABLE.createUri(String.valueOf(drawableResId)), imageView);
    }

    /**
     * 显示图片
     * @param uri 图片资源URI
     * @param imageView 显示图片的视图
     * @return DisplayRequest.Helper 你可以继续设置一些参数，最后调用fire()方法开始显示
     */
    public DisplayHelper display(Uri uri, ImageView imageView){
        return displayHelperManager.getDisplayHelper(this, uri.toString(), imageView);
    }
	
    /**
     * 清除内存缓存和磁盘缓存
     */
    public void clearAllCache() {
        clearMemoryCache();
        clearDiskCache();
    }

    /**
     * 清除内存缓存
     */
    public void clearMemoryCache() {
        if(memoryCache == null){
            return;
        }
        memoryCache.clear();
    }

    /**
     * 清除磁盘缓存
     */
    public void clearDiskCache() {
        if(diskCache == null){
            return;
        }
        diskCache.clear();
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
     * 获取上下文
     * @return 上下文
     */
    public Context getContext() {
        return context;
    }

    /**
     * 获取请求执行器
     * @return 请求执行器
     */
    public RequestExecutor getRequestExecutor() {
        return requestExecutor;
    }

    /**
     * 设置请求执行器
     * @param requestExecutor 请求执行器
     */
    public Spear setRequestExecutor(RequestExecutor requestExecutor) {
        if(requestExecutor != null){
            this.requestExecutor = requestExecutor;
        }
        return this;
    }

    /**
     * 获取磁盘缓存器
     * @return 磁盘缓存器
     */
    public DiskCache getDiskCache() {
        return diskCache;
    }

    /**
     * 设置磁盘缓存器
     * @param diskCache 磁盘缓存器
     */
    public Spear setDiskCache(DiskCache diskCache) {
        if(diskCache != null){
            this.diskCache = diskCache;
        }
        return this;
    }

    /**
     * 获取内存缓存器
     * @return 内存缓存器
     */
    public MemoryCache getMemoryCache() {
        return memoryCache;
    }

    /**
     * 设置内存缓存器
     * @param memoryCache 内存缓存器
     */
    public Spear setMemoryCache(MemoryCache memoryCache) {
        if(memoryCache != null){
            this.memoryCache = memoryCache;
        }
        return this;
    }

    /**
     * 获取位图解码器
     * @return 位图解码器
     */
    public ImageDecoder getImageDecoder() {
        return imageDecoder;
    }

    /**
     * 设置位图解码器
     * @param imageDecoder 位图解码器
     */
    public Spear setImageDecoder(ImageDecoder imageDecoder) {
        if(imageDecoder != null){
            this.imageDecoder = imageDecoder;
        }
        return this;
    }

    /**
     * 获取显示相关回调处理器
     * @return 显示相关回调处理器
     */
    public DisplayCallbackHandler getDisplayCallbackHandler() {
        return displayCallbackHandler;
    }

    /**
     * 是否开启调试模式
     * @return 是否开启调试模式，开启调试模式后会在控制台输出LOG
     */
    public boolean isDebugMode() {
        return debugMode;
    }

    /**
     * 设置是否开启调试模式
     * @param debugMode 是否开启调试模式，开启调试模式后会在控制台输出LOG
     */
    public Spear setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
        return this;
    }

    /**
     * 获取图片下载器
     */
    public ImageDownloader getImageDownloader() {
        return imageDownloader;
    }

    /**
     * 设置图片下载器
     * @param imageDownloader 图片下载器
     */
    public Spear setImageDownloader(ImageDownloader imageDownloader) {
        if(imageDownloader != null){
            this.imageDownloader = imageDownloader;
        }
        return this;
    }

    /**
     * 获取图片尺寸计算器
     * @return 图片尺寸计算器
     */
    public ImageSizeCalculator getImageSizeCalculator() {
        return imageSizeCalculator;
    }

    /**
     * 获取图片尺寸计算器
     * @param imageSizeCalculator 图片尺寸计算器
     */
    public Spear setImageSizeCalculator(ImageSizeCalculator imageSizeCalculator) {
        if(imageSizeCalculator != null){
            this.imageSizeCalculator = imageSizeCalculator;
        }
        return this;
    }

    /**
     * 取消
     * @param imageView ImageView
     * @return true：当前ImageView有正在执行的任务并且取消成功；false：当前ImageView没有正在执行的任务
     */
    public static boolean cancel(ImageView imageView) {
        final DisplayRequest displayRequest = AsyncDrawable.getDisplayRequestByAsyncDrawable(imageView);
        if (displayRequest != null) {
            displayRequest.cancel();
            return true;
        }else{
            return false;
        }
    }

    /**
     * 获取选项
     * @param optionsName 选项名称
     * @return 选项
     */
    public static RequestOptions getOptions(Enum<?> optionsName){
        return OptionsMapInstanceHolder.OPTIONS_MAP.get(optionsName);
    }

    /**
     * 放入选项
     * @param optionsName 选项名称
     * @param options 选项
     */
    public static void putOptions(Enum<?> optionsName, RequestOptions options){
        OptionsMapInstanceHolder.OPTIONS_MAP.put(optionsName, options);
    }

    /**
     * 选项集合持有器
     */
    private static class OptionsMapInstanceHolder{
        private static final Map<Object, RequestOptions> OPTIONS_MAP = new HashMap<Object, RequestOptions>();
    }

    /**
     * 获取DisplayHelper管理器
     * @return DisplayHelper管理器
     */
    public DisplayHelperManager getDisplayHelperManager() {
        return displayHelperManager;
    }

    /**
     * 获取图片加载器的实例
     * @param context 用来初始化配置
     * @return 图片加载器的实例
     */
    public static Spear with(Context context){
        if(instance == null){
            synchronized (Spear.class){
                if(instance == null){
                    instance = new Spear(context);
                }
            }
        }
        return instance;
    }
}