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

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import me.xiaopan.sketch.util.SketchUtils;

/**
 * 图片加载器，可以从网络或者本地加载图片，并且支持自动清除缓存
 */
public class Sketch {
    public static final String TAG = "Sketch";

    private static Sketch instance;
    private static boolean debugMode;	//调试模式，在控制台输出日志
    private static Map<Enum<?>, RequestOptions> optionsMap;

    private Configuration configuration;

	private Sketch(Context context){
        Log.i(TAG, SketchUtils.concat("Sketch", " ", BuildConfig.BUILD_TYPE, " ", BuildConfig.VERSION_NAME, "(", BuildConfig.VERSION_CODE, ")"));
        this.configuration = new Configuration(context);
    }

    public static Sketch with(Context context){
        if(instance == null){
            synchronized (Sketch.class){
                if(instance == null){
                    instance = new Sketch(context);
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
     * 下载图片
     * @param uri 图片Uri，支持以下几种
     * <blockquote>“http://site.com/image.png“  // from Web
     * <br>“https://site.com/image.png“ // from Web
     * </blockquote>
     * @param downloadListener 下载监听器
     * @return DownloadHelper 你可以继续设置一些参数，最后调用fire()方法开始下载
     */
	public DownloadHelper download(String uri, DownloadListener downloadListener){
		 return configuration.getHelperFactory().getDownloadHelper(this, uri).listener(downloadListener);
	}



    /**
     * 根据URI加载图片
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
        return configuration.getHelperFactory().getLoadHelper(this, uri).listener(loadListener);
	}

    /**
     * 加载Asset中的图片
     * @param fileName 文件名称
     * @param loadListener 加载监听器
     * @return LoadHelper 你可以继续设置一些参数，最后调用fire()方法开始加载
     */
    public LoadHelper loadFromAsset(String fileName, LoadListener loadListener){
        return configuration.getHelperFactory().getLoadHelper(this, UriScheme.ASSET.createUri(fileName)).listener(loadListener);
    }
    
    /**
     * 加载资源中的图片
     * @param drawableResId 图片资源ID
     * @param loadListener 加载监听器
     * @return LoadHelper 你可以继续设置一些参数，最后调用fire()方法开始加载
     */
	public LoadHelper loadFromResource(int drawableResId, LoadListener loadListener){
        return configuration.getHelperFactory().getLoadHelper(this, UriScheme.DRAWABLE.createUri(String.valueOf(drawableResId))).listener(loadListener);
	}

    /**
     * 加载URI指向的图片
     * @param uri 图片URI
     * @param loadListener 加载监听器
     * @return LoadHelper 你可以继续设置一些参数，最后调用fire()方法开始加载
     */
	public LoadHelper loadFromURI(Uri uri, LoadListener loadListener){
        return configuration.getHelperFactory().getLoadHelper(this, uri.toString()).listener(loadListener);
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
     * @param sketchImageViewInterface 显示图片的视图
     * @return DisplayHelper 你可以继续设置一些参数，最后调用fire()方法开始显示
     */
    public DisplayHelper display(String uri, SketchImageViewInterface sketchImageViewInterface){
        return configuration.getHelperFactory().getDisplayHelper(this, uri, sketchImageViewInterface);
    }

    /**
     * 显示Asset中的图片
     * @param fileName 文件名称
     * @param sketchImageViewInterface 显示图片的视图
     * @return DisplayHelper 你可以继续设置一些参数，最后调用fire()方法开始显示
     */
    public DisplayHelper displayFromAsset(String fileName, SketchImageViewInterface sketchImageViewInterface){
        return configuration.getHelperFactory().getDisplayHelper(this, UriScheme.ASSET.createUri(fileName), sketchImageViewInterface);
    }

    /**
     * 显示资源中的图片
     * @param drawableResId 图片资源ID
     * @param sketchImageViewInterface 显示图片的视图
     * @return DisplayHelper 你可以继续设置一些参数，最后调用fire()方法开始显示
     */
    public DisplayHelper displayFromResource(int drawableResId, SketchImageViewInterface sketchImageViewInterface){
        return configuration.getHelperFactory().getDisplayHelper(this, UriScheme.DRAWABLE.createUri(String.valueOf(drawableResId)), sketchImageViewInterface);
    }

    /**
     * 显示URI指向的图片
     * @param uri 图片URI
     * @param sketchImageViewInterface 显示图片的视图
     * @return DisplayHelper 你可以继续设置一些参数，最后调用fire()方法开始显示
     */
    public DisplayHelper displayFromURI(Uri uri, SketchImageViewInterface sketchImageViewInterface){
        return configuration.getHelperFactory().getDisplayHelper(this, uri!=null?uri.toString():null, sketchImageViewInterface);
    }

    /**
     * 显示图片，主要用于配合SketchImageView兼容RecyclerView
     * @param displayParams 参数集
     * @param sketchImageViewInterface 显示图片的视图
     * @return DisplayHelper 你可以继续设置一些参数，最后调用fire()方法开始显示
     */
    public DisplayHelper display(DisplayParams displayParams, SketchImageViewInterface sketchImageViewInterface){
        return configuration.getHelperFactory().getDisplayHelper(this, displayParams, sketchImageViewInterface);
    }



    /**
     * 取消
     * @param sketchImageViewInterface ImageView
     * @return true：当前ImageView有正在执行的任务并且取消成功；false：当前ImageView没有正在执行的任务
     */
    public static boolean cancel(SketchImageViewInterface sketchImageViewInterface) {
        final DisplayRequest displayRequest = BindFixedRecycleBitmapDrawable.getDisplayRequestBySketchImageInterface(sketchImageViewInterface);
        if (displayRequest != null && !displayRequest.isFinished()) {
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
            synchronized (Sketch.class){
                if(optionsMap == null){
                    optionsMap = new HashMap<Enum<?>, RequestOptions>();
                }
            }
        }
        optionsMap.put(optionsName, options);
    }

    /**
     * 获取OptionMap
     * @return OptionMap
     */
    public static Map<Enum<?>, RequestOptions> getOptionsMap() {
        return optionsMap;
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
        Sketch.debugMode = debugMode;
    }
}