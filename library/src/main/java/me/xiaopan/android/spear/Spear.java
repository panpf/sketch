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
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import me.xiaopan.android.spear.util.AsyncDrawable;

/**
 * 图片加载器，可以从网络或者本地加载图片，并且支持自动清除缓存
 */
public class Spear {
    public static final String TAG = "Spear";

    private static Spear instance;
    private static boolean debugMode;	//调试模式，在控制台输出日志
    private static Map<Object, RequestOptions> optionsMap;

    private Configuration configuration;
    private boolean pauseLoadNewImage;   // 暂停加载新图片，开启后将只从内存缓存中找寻图片，只影响display请求
    private boolean pauseDownloadNewImage;   // 暂停下载新图片，开启后将不再从网络下载新图片，只影响display请求

	private Spear(Context context){
        this.configuration = new Configuration(context);
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

    /**
     * 获取配置对象
     * @return 配置对象
     */
    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * 设置是否暂停加载新图片，开启后将只从内存缓存中找寻图片，只影响display请求
     * @param pauseLoadNewImage 是否暂停加载新图片，开启后将只从内存缓存中找寻图片，只影响display请求
     */
    public void setPauseLoadNewImage(boolean pauseLoadNewImage) {
        if(this.pauseLoadNewImage == pauseLoadNewImage){
            return;
        }
        this.pauseLoadNewImage = pauseLoadNewImage;
        if(isDebugMode()){
            if(this.pauseLoadNewImage){
                Log.w(TAG, "pauseLoadNewImage");
            }else{
                Log.d(TAG, "resumeLoadNewImage");
            }
        }
    }

    /**
     * 是否暂停加载新图片，开启后将只从内存缓存中找寻图片，只影响display请求
     * @return 是否暂停加载新图片，开启后将只从内存缓存中找寻图片，只影响display请求
     */
    public boolean isPauseLoadNewImage() {
        return pauseLoadNewImage;
    }

    /**
     * 是否暂停下载图片，开启后将不再从网络下载图片，只影响display请求
     * @return 暂停下载图片，开启后将不再从网络下载图片，只影响display请求
     */
    public boolean isPauseDownloadNewImage() {
        return pauseDownloadNewImage;
    }

    /**
     * 设置暂停下载图片，开启后将不再从网络下载图片，只影响display请求
     * @param pauseDownloadNewImage 暂停下载图片，开启后将不再从网络下载图片，只影响display请求
     */
    public void setPauseDownloadNewImage(boolean pauseDownloadNewImage) {
        if(this.pauseDownloadNewImage == pauseDownloadNewImage){
            return;
        }
        this.pauseDownloadNewImage = pauseDownloadNewImage;
        if(isDebugMode()){
            if(this.pauseDownloadNewImage){
                Log.w(TAG, "pauseDownloadNewImage");
            }else{
                Log.d(TAG, "resumeDownloadImage");
            }
        }
    }



    /**
     * 下载
     * @param uri 图片Uri，支持以下几种
     * <blockquote>“http://site.com/image.png“  // from Web
     * <br>“https://site.com/image.png“ // from Web
     * </blockquote>
     * @param downloadListener 下载监听器
     * @return DownloadHelper 你可以继续设置一些参数，最后调用fire()方法开始下载
     */
	public DownloadHelper download(String uri, DownloadListener downloadListener){
		 return configuration.getHelperFactory().newDownloadHelper(this, uri).listener(downloadListener);
	}



    /**
     * 加载
     * @param uri 图片Uri，支持以下几种
     * <blockquote>"http://site.com/image.png"; // from Web
     * <br>"https://site.com/image.png"; // from Web
     * <br>"/mnt/sdcard/image.png"; // from SD card
     * <br>"/mnt/sdcard/app.apk"; // from SD card apk file
     * <br>"content://media/external/audio/albumart/13"; // from content provider
     * <br>"asset://image.png"; // from assets
     * <br>"drawable://" + R.drawable.image; // from drawables (only images, non-9patch)
     * </blockquote>
     * @param loadListener 加载监听器
     * @return LoadHelper 你可以继续设置一些参数，最后调用fire()方法开始加载
     */
	public LoadHelper load(String uri, LoadListener loadListener){
        return configuration.getHelperFactory().newLoadHelper(this, uri).listener(loadListener);
	}
    
    /**
     * 加载
     * @param imageFile 图片文件
     * @param loadListener 加载监听器
     * @return LoadHelper 你可以继续设置一些参数，最后调用fire()方法开始加载
     */
	public LoadHelper load(File imageFile, LoadListener loadListener){
        return configuration.getHelperFactory().newLoadHelper(this, imageFile.getPath()).listener(loadListener);
	}

    /**
     * 加载
     * @param drawableResId 图片资源ID
     * @param loadListener 加载监听器
     * @return LoadHelper 你可以继续设置一些参数，最后调用fire()方法开始加载
     */
	public LoadHelper load(int drawableResId, LoadListener loadListener){
        return configuration.getHelperFactory().newLoadHelper(this, UriScheme.DRAWABLE.createUri(String.valueOf(drawableResId))).listener(loadListener);
	}

    /**
     * 加载
     * @param uri 图片资源URI
     * @param loadListener 加载监听器
     * @return LoadHelper 你可以继续设置一些参数，最后调用fire()方法开始加载
     */
	public LoadHelper load(Uri uri, LoadListener loadListener){
        return configuration.getHelperFactory().newLoadHelper(this, uri.toString()).listener(loadListener);
	}



    /**
     * 显示图片
     * @param uri 图片Uri，支持以下几种
     * <blockquote>"http://site.com/image.png"; // from Web
     * <br>"https://site.com/image.png"; // from Web
     * <br>"/mnt/sdcard/image.png"; // from SD card
     * <br>"/mnt/sdcard/app.apk"; // from SD card apk file
     * <br>"content://media/external/audio/albumart/13"; // from content provider
     * <br>"asset://image.png"; // from assets
     * <br>"drawable://" + R.drawable.image; // from drawables (only images, non-9patch)
     * </blockquote>
     * @param imageView 显示图片的视图
     * @return DisplayHelper 你可以继续设置一些参数，最后调用fire()方法开始显示
     */
    public DisplayHelper display(String uri, ImageView imageView){
        return configuration.getDisplayHelperManager().getDisplayHelper(this, uri, imageView);
    }

    /**
     * 显示图片
     * @param imageFile 图片文件
     * @param imageView 显示图片的视图
     * @return DisplayHelper 你可以继续设置一些参数，最后调用fire()方法开始显示
     */
    public DisplayHelper display(File imageFile, ImageView imageView){
        return configuration.getDisplayHelperManager().getDisplayHelper(this, imageFile.getPath(), imageView);
    }

    /**
     * 显示图片
     * @param drawableResId 图片资源ID
     * @param imageView 显示图片的视图
     * @return DisplayHelper 你可以继续设置一些参数，最后调用fire()方法开始显示
     */
    public DisplayHelper display(int drawableResId, ImageView imageView){
        return configuration.getDisplayHelperManager().getDisplayHelper(this, UriScheme.DRAWABLE.createUri(String.valueOf(drawableResId)), imageView);
    }

    /**
     * 显示图片
     * @param uri 图片资源URI
     * @param imageView 显示图片的视图
     * @return DisplayHelper 你可以继续设置一些参数，最后调用fire()方法开始显示
     */
    public DisplayHelper display(Uri uri, ImageView imageView){
        return configuration.getDisplayHelperManager().getDisplayHelper(this, uri.toString(), imageView);
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
        if(optionsMap == null){
            return null;
        }
        return optionsMap.get(optionsName);
    }

    /**
     * 放入选项
     * @param optionsName 选项名称
     * @param options 选项
     */
    public static void putOptions(Enum<?> optionsName, RequestOptions options){
        if(optionsMap == null){
            synchronized (Spear.class){
                if(optionsMap == null){
                    optionsMap = new HashMap<Object, RequestOptions>();
                }
            }
        }
        optionsMap.put(optionsName, options);
    }

    /**
     * 是否开启调试模式
     * @return 是否开启调试模式，开启调试模式后会在控制台输出LOG
     */
    public static boolean isDebugMode() {
        return debugMode;
    }

    /**
     * 设置是否开启调试模式
     * @param debugMode 是否开启调试模式，开启调试模式后会在控制台输出LOG
     */
    public static void setDebugMode(boolean debugMode) {
        Spear.debugMode = debugMode;
    }
}