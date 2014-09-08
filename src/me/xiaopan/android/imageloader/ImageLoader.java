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

package me.xiaopan.android.imageloader;

import java.io.File;

import me.xiaopan.android.imageloader.cache.disk.DiskCache;
import me.xiaopan.android.imageloader.task.display.AsyncDrawable;
import me.xiaopan.android.imageloader.task.display.DisplayListener;
import me.xiaopan.android.imageloader.task.display.DisplayOptions;
import me.xiaopan.android.imageloader.task.display.DisplayRequest;
import me.xiaopan.android.imageloader.task.display.ImageViewHolder;
import me.xiaopan.android.imageloader.task.download.DownloadListener;
import me.xiaopan.android.imageloader.task.download.DownloadOptions;
import me.xiaopan.android.imageloader.task.download.DownloadRequest;
import me.xiaopan.android.imageloader.task.load.LoadListener;
import me.xiaopan.android.imageloader.task.load.LoadOptions;
import me.xiaopan.android.imageloader.task.load.LoadRequest;
import me.xiaopan.android.imageloader.util.ImageLoaderUtils;
import me.xiaopan.android.imageloader.util.ImageSize;
import me.xiaopan.android.imageloader.util.Scheme;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.ImageView;

/**
 * 图片加载器，可以从网络或者本地加载图片，并且支持自动清除缓存
 */
public class ImageLoader{
    public static final String LOG_TAG= ImageLoader.class.getSimpleName();
    private static DownloadOptions defaultDownloadOptions;
    private static LoadOptions defaultLoadOptions;
    private static DisplayOptions defaultDisplayOptions;
	private static ImageLoader instance; 
	private Configuration configuration;	//配置
	
	public ImageLoader(Context context){
		configuration = new Configuration(context);
	}
	
	/**
	 * 获取图片加载器的实例
	 * @param context 用来初始化配置
	 * @return 图片加载器的实例
	 */
	public static synchronized ImageLoader getInstance(Context context){
		if(instance == null){
			instance = new ImageLoader(context);
		}
		return instance;
	}

    /**
     * 下载
     * @param imageUri 只支持HTTP和HTTPS
     * @param downloadOptions 配置缓存以及失败重试等选项
     * @param downloadListener 监听下载过程
     */
	public void download(String imageUri, DownloadOptions downloadOptions, DownloadListener downloadListener){
		 // 验证参数的合法性
		if(ImageLoaderUtils.isEmpty(imageUri)){
			new NullPointerException("imageUri不能为null或空").printStackTrace();
			if(downloadListener != null) downloadListener.onFailure();
            return;
		}

		// 过滤掉不支持的协议格式
        Scheme scheme = Scheme.valueOfUri(imageUri);
        if(scheme != Scheme.HTTP || scheme != Scheme.HTTPS){
            if(configuration.isDebugMode()) Log.e(ImageLoader.LOG_TAG, new StringBuilder(LOG_TAG).append("：").append("download()方法只处理http或https协议格式").append(" URI").append("=").append(imageUri).toString());
            if(downloadListener != null) downloadListener.onFailure();
            return;
        }
		
		// 初始化请求
		DownloadRequest downloadRequest = new DownloadRequest(imageUri, configuration);
		downloadRequest.setName(imageUri.toString());
		downloadRequest.setScheme(scheme);
		if(downloadListener != null)downloadListener.onStart();
		downloadRequest.setDownloadListener(downloadListener);
		if(downloadOptions == null){
			if(defaultDownloadOptions == null){
				defaultDownloadOptions = new DownloadOptions();
			}
			downloadOptions = defaultDownloadOptions;
		}
		downloadRequest.setDownloadOptions(downloadOptions);
		
		//执行请求
		configuration.getRequestExecutor().execute(downloadRequest);
	}

    /**
     * 下载
     * @param imageUri 只支持HTTP和HTTPS
     * @param downloadOptionsName 下载选项的名称，你通过configuration.putOptions()方法放进去的DownloadOptions在这里指定一样的名称就可以直接使用
     * @param downloadListener 监听下载过程
     */
    public void download(String imageUri, Enum<?> downloadOptionsName, DownloadListener downloadListener){
        download(imageUri, (DownloadOptions) configuration.getOptions(downloadOptionsName), downloadListener);
    }


    /**
     * 下载
     * @param imageUri 只支持HTTP和HTTPS
     * @param downloadListener 监听下载过程
     */
    public void download(String imageUri, DownloadListener downloadListener){
        download(imageUri, defaultDownloadOptions, downloadListener);
    }

    
    /**
     * 加载
     * @param imageUri 支持以下5种Uri
     * <blockquote>String imageUri = "http://site.com/image.png"; // from Web
     * <br>String imageUri = "file:///mnt/sdcard/image.png"; // from SD card
     * <br>String imageUri = "content://media/external/audio/albumart/13"; // from content provider
     * <br>String imageUri = "assets://image.png"; // from assets
     * <br>String imageUri = "drawable://" + R.drawable.image; // from drawables (only images, non-9patch)
     * </blockquote>
     * @param loadOptions 配置缓存、失败重试、最大尺寸以及处理器
     * @param loadListener 监听加载过程
     */
	public void load(String imageUri, LoadOptions loadOptions, LoadListener loadListener){
		// 验证参数的合法性
		if(ImageLoaderUtils.isEmpty(imageUri)){
			new NullPointerException("imageUri不能为null或空").printStackTrace();
			if(loadListener != null) loadListener.onFailure();
            return;
		}

        // 过滤掉未知协议的URI
        Scheme scheme = Scheme.valueOfUri(imageUri);
        if(scheme == Scheme.UNKNOWN){
            if(configuration.isDebugMode()) Log.e(ImageLoader.LOG_TAG, new StringBuilder(LOG_TAG).append("：").append("未知的协议格式").append("URI").append("=").append(imageUri).toString());
            if(loadListener != null) loadListener.onFailure();
            return;
        }
        
        //初始化请求
        LoadRequest loadRequest = new LoadRequest(imageUri, configuration);
        loadRequest.setName(imageUri.toString());
        loadRequest.setScheme(scheme);
        if(loadListener != null) loadListener.onStart();
        loadRequest.setLoadListener(loadListener);
        if(loadOptions == null){
        	if(defaultLoadOptions == null){
        		defaultLoadOptions = new LoadOptions();
        	}
        	loadOptions = defaultLoadOptions;
        }
        loadRequest.setLoadOptions(loadOptions);
        loadRequest.setDecodeMaxSize(loadOptions.getDecodeMaxSize());
        loadRequest.setProcessSize(loadOptions.getProcessSize());

        //执行请求
        configuration.getRequestExecutor().execute(loadRequest);
	}

    /**
     * 加载
     * @param imageUri 支持以下5种Uri
     * <blockquote>String imageUri = "http://site.com/image.png"; // from Web
     * <br>String imageUri = "file:///mnt/sdcard/image.png"; // from SD card
     * <br>String imageUri = "content://media/external/audio/albumart/13"; // from content provider
     * <br>String imageUri = "assets://image.png"; // from assets
     * <br>String imageUri = "drawable://" + R.drawable.image; // from drawables (only images, non-9patch)
     * </blockquote>
     * @param loadOptionsName 加载选项的名称，你通过configuration.putOptions()方法放进去的LoadOptions在这里指定一样的名称就可以直接使用
     * @param loadListener 监听加载过程
     */
    public void load(String imageUri, Enum<?> loadOptionsName, LoadListener loadListener){
        load(imageUri, (LoadOptions) configuration.getOptions(loadOptionsName), loadListener);
    }

    /**
     * 加载
     * @param imageUri 支持以下5种Uri
     * <blockquote>String imageUri = "http://site.com/image.png"; // from Web
     * <br>String imageUri = "file:///mnt/sdcard/image.png"; // from SD card
     * <br>String imageUri = "content://media/external/audio/albumart/13"; // from content provider
     * <br>String imageUri = "assets://image.png"; // from assets
     * <br>String imageUri = "drawable://" + R.drawable.image; // from drawables (only images, non-9patch)
     * </blockquote>
     * @param loadListener 监听加载过程
     */
    public void load(String imageUri, LoadListener loadListener){
        load(imageUri, defaultLoadOptions, loadListener);
    }
    
    /**
     * 加载
     * @param imageFile 图片文件
     * @param loadOptions 配置缓存、失败重试、最大尺寸以及处理器
     * @param loadListener 监听加载过程
     */
	public void load(File imageFile, LoadOptions loadOptions, LoadListener loadListener){
        load(Scheme.FILE.createUri(imageFile.getPath()), loadOptions, loadListener);
	}

    /**
     * 加载
     * @param imageFile 图片文件
     * @param loadOptionsName 加载选项的名称，你通过configuration.putOptions()方法放进去的LoadOptions在这里指定一样的名称就可以直接使用
     * @param loadListener 监听加载过程
     */
    public void load(File imageFile, Enum<?> loadOptionsName, LoadListener loadListener){
        load(Scheme.FILE.createUri(imageFile.getPath()), (LoadOptions) configuration.getOptions(loadOptionsName), loadListener);
    }

    /**
     * 加载
     * @param imageFile 图片文件
     * @param loadListener 监听加载过程
     */
    public void load(File imageFile, LoadListener loadListener){
        load(Scheme.FILE.createUri(imageFile.getPath()), defaultLoadOptions, loadListener);
    }
    
    
    /**
     * 加载
     * @param imageResId 图片资源ID
     * @param loadOptions 配置缓存、失败重试、最大尺寸以及处理器
     * @param loadListener 监听加载过程
     */
	public void load(int imageResId, LoadOptions loadOptions, LoadListener loadListener){
        load(Scheme.DRAWABLE.createUri(String.valueOf(imageResId)), loadOptions, loadListener);
	}

    /**
     * 加载
     * @param imageResId 图片资源ID
     * @param loadOptionsName 加载选项的名称，你通过configuration.putOptions()方法放进去的LoadOptions在这里指定一样的名称就可以直接使用
     * @param loadListener 监听加载过程
     */
    public void load(int imageResId, Enum<?> loadOptionsName, LoadListener loadListener){
        load(Scheme.DRAWABLE.createUri(String.valueOf(imageResId)), (LoadOptions) configuration.getOptions(loadOptionsName), loadListener);
    }

    /**
     * 加载
     * @param imageResId 图片资源ID
     * @param loadListener 监听加载过程
     */
    public void load(int imageResId, LoadListener loadListener){
        load(Scheme.DRAWABLE.createUri(String.valueOf(imageResId)), defaultLoadOptions, loadListener);
    }

    
    /**
     * 显示图片
     * @param imageUri 图片Uri，支持以下5种Uri
     * <blockquote>String imageUri = "http://site.com/image.png"; // from Web
     * <br>String imageUri = "file:///mnt/sdcard/image.png"; // from SD card
     * <br>String imageUri = "content://media/external/audio/albumart/13"; // from content provider
     * <br>String imageUri = "assets://image.png"; // from assets
     * <br>String imageUri = "drawable://" + R.drawable.image; // from drawables (only images, non-9patch)
     * </blockquote>
     * @param imageView 显示图片的视图
     * @param displayOptions 显示选项
     * @param displayListener 显示监听器
     */
    public void display(String imageUri, ImageView imageView, DisplayOptions displayOptions, DisplayListener displayListener){
    	// 过滤掉为null的ImageView
		if(imageView == null){
			new NullPointerException("imageView不能为null").printStackTrace();
			if(displayListener != null) displayListener.onFailure();
            return;
		}

		// 防止DisplayOptions为null
		if(displayOptions == null){
			if(defaultDisplayOptions == null){
				defaultDisplayOptions = new DisplayOptions(configuration.getContext());
			}
			displayOptions = defaultDisplayOptions;
		}
    	
    	// 过滤掉为空的URI
        if(ImageLoaderUtils.isEmpty(imageUri)){
        	imageView.clearAnimation();
            imageView.setImageDrawable(displayOptions.getEmptyUriDrawable());
            if(configuration.isDebugMode()) Log.e(ImageLoader.LOG_TAG, new StringBuilder(LOG_TAG).append("：").append("imageUri不能为null或空").append("；").append("ImageViewCode").append("=").append(imageView.hashCode()).toString());
            if(displayListener != null) displayListener.onFailure();
            return;
        }

        // 过滤掉未知协议的URI
        Scheme scheme = Scheme.valueOfUri(imageUri);
        if(scheme == Scheme.UNKNOWN){
        	imageView.clearAnimation();
            imageView.setImageDrawable(displayOptions.getLoadFailDrawable());
            if(configuration.isDebugMode()) Log.e(ImageLoader.LOG_TAG, new StringBuilder(LOG_TAG).append("：").append("未知的协议格式").append("URI").append("=").append(imageUri).append("；").append("ImageViewCode").append("=").append(imageView.hashCode()).toString());
            if(displayListener != null) displayListener.onFailure();
            return;
        }
        
        if(displayListener != null) displayListener.onStart();

        // 计算解码尺寸和处理尺寸
        ImageSize decodeMaxSize = ImageSize.createDecodeSize(imageView, displayOptions.getDecodeMaxSize());
        ImageSize processSize = ImageSize.createProcessSize(imageView, displayOptions.getProcessSize());
        
        //创建请求
        String requestId = DisplayRequest.createId(ImageLoaderUtils.encodeUrl(imageUri.toString()), decodeMaxSize, processSize, displayOptions.getProcessor());
        DisplayRequest displayRequest = new DisplayRequest(requestId, imageUri, configuration);

        //尝试显示
        if(displayOptions.isEnableMemoryCache()){
            BitmapDrawable cacheDrawable = configuration.getMemoryCache().get(displayRequest.getId());
            if(cacheDrawable != null){
            	imageView.clearAnimation();
                imageView.setImageDrawable(cacheDrawable);
                if(displayListener != null){
                    displayListener.onSuccess(imageUri, imageView, cacheDrawable);
                }
                return;
            }
        }

        //试图取消当前ImageView上正在加载的请求，true：取消成功；false：无需取消
        displayRequest.setDisplayListener(displayListener);
        if(!cancelPotentialDisplayRequest(imageView, displayRequest)){
            return;
        }

        //初始化请求
        displayRequest.setName(imageUri.toString());
        displayRequest.setScheme(scheme);
        displayRequest.setDecodeMaxSize(decodeMaxSize);
        displayRequest.setProcessSize(processSize);
        displayRequest.setDisplayOptions(displayOptions);
        displayRequest.setImageViewHolder(new ImageViewHolder(imageView));
        displayRequest.setScaleType(displayOptions.getScaleType()!=null?displayOptions.getScaleType():imageView.getScaleType());

        //显示默认图片
        BitmapDrawable loadingBitmapDrawable = displayRequest.getDisplayOptions().getLoadingDrawable();
    	imageView.clearAnimation();
        imageView.setImageDrawable(new AsyncDrawable(configuration.getContext().getResources(), loadingBitmapDrawable != null?loadingBitmapDrawable.getBitmap():null, displayRequest));

        //执行请求
        configuration.getRequestExecutor().execute(displayRequest);
    }

    /**
     * 显示图片
     * @param imageUri 图片Uri，支持以下5种Uri
     * <blockquote>String imageUri = "http://site.com/image.png"; // from Web
     * <br>String imageUri = "file:///mnt/sdcard/image.png"; // from SD card
     * <br>String imageUri = "content://media/external/audio/albumart/13"; // from content provider
     * <br>String imageUri = "assets://image.png"; // from assets
     * <br>String imageUri = "drawable://" + R.drawable.image; // from drawables (only images, non-9patch)
     * </blockquote>
     * @param imageView 显示图片的视图
     * @param displayOptionsName 显示选项的名称，你通过configuration.putOptions()方法放进去的DisplayOptions在这里指定一样的名称就可以直接使用
     * @param displayListener 显示监听器
     */
    public void display(String imageUri, ImageView imageView, Enum<?> displayOptionsName, DisplayListener displayListener){
        display(imageUri, imageView, (DisplayOptions) configuration.getOptions(displayOptionsName), displayListener);
    }

    /**
     * 显示图片
     * @param imageUri 图片Uri，支持以下5种Uri
     * <blockquote>
     *         String imageUri = "http://site.com/image.png"; // from Web
     * <br>String imageUri = "file:///mnt/sdcard/image.png"; // from SD card
     * <br>String imageUri = "content://media/external/audio/albumart/13"; // from content provider
     * <br>String imageUri = "assets://image.png"; // from assets
     * <br>String imageUri = "drawable://" + R.drawable.image; // from drawables (only images, non-9patch)
     * </blockquote>
     * @param imageView 显示图片的视图
     * @param displayOptions 显示选项
     */
    public void display(String imageUri, ImageView imageView, DisplayOptions displayOptions){
        display(imageUri, imageView, displayOptions, null);
    }

    /**
     * 显示图片
     * @param imageUri 图片Uri，支持以下5种Uri
     * <blockquote>
     *         String imageUri = "http://site.com/image.png"; // from Web
     * <br>String imageUri = "file:///mnt/sdcard/image.png"; // from SD card
     * <br>String imageUri = "content://media/external/audio/albumart/13"; // from content provider
     * <br>String imageUri = "assets://image.png"; // from assets
     * <br>String imageUri = "drawable://" + R.drawable.image; // from drawables (only images, non-9patch)
     * </blockquote>
     * @param imageView 显示图片的视图
     * @param displayOptionsName 显示选项的名称，你通过configuration.putOptions()方法放进去的DisplayOptions在这里指定一样的名称就可以直接使用
     */
    public void display(String imageUri, ImageView imageView, Enum<?> displayOptionsName){
        display(imageUri, imageView, (DisplayOptions) configuration.getOptions(displayOptionsName), null);
    }

    /**
     * 显示图片
     * @param imageUri 图片Uri，支持以下5种Uri
     * <blockquote>String imageUri = "http://site.com/image.png"; // from Web
     * <br>String imageUri = "file:///mnt/sdcard/image.png"; // from SD card
     * <br>String imageUri = "content://media/external/audio/albumart/13"; // from content provider
     * <br>String imageUri = "assets://image.png"; // from assets
     * <br>String imageUri = "drawable://" + R.drawable.image; // from drawables (only images, non-9patch)
     * </blockquote>
     * @param imageView 显示图片的视图
     * @param displayListener 显示监听器
     */
    public void display(String imageUri, ImageView imageView, DisplayListener displayListener){
        display(imageUri, imageView, defaultDisplayOptions, displayListener);
    }

    /**
     * 显示图片
     * @param imageUri 图片Uri，支持以下5种Uri
     * <blockquote>String imageUri = "http://site.com/image.png"; // from Web
     * <br>String imageUri = "file:///mnt/sdcard/image.png"; // from SD card
     * <br>String imageUri = "content://media/external/audio/albumart/13"; // from content provider
     * <br>String imageUri = "assets://image.png"; // from assets
     * <br>String imageUri = "drawable://" + R.drawable.image; // from drawables (only images, non-9patch)
     * </blockquote>
     * @param imageView 显示图片的视图
     */
    public void display(String imageUri, ImageView imageView){
        display(imageUri, imageView, defaultDisplayOptions, null);
    }

    
    /**
     * 显示图片
     * @param imageFile 图片文件
     * @param imageView 显示图片的视图
     * @param displayOptions 显示选项
     * @param displayListener 显示监听器
     */
    public void display(File imageFile, ImageView imageView, DisplayOptions displayOptions, DisplayListener displayListener){
        display(Scheme.FILE.createUri(imageFile.getPath()), imageView, displayOptions, displayListener);
    }

    /**
     * 显示图片
     * @param imageFile 图片文件
     * @param imageView 显示图片的视图
     * @param displayOptionsName 显示选项的名称，你通过configuration.putOptions()方法放进去的DisplayOptions在这里指定一样的名称就可以直接使用
     * @param displayListener 显示监听器
     */
    public void display(File imageFile, ImageView imageView, Enum<?> displayOptionsName, DisplayListener displayListener){
        display(Scheme.FILE.createUri(imageFile.getPath()), imageView, (DisplayOptions) configuration.getOptions(displayOptionsName), displayListener);
    }

    /**
     * 显示图片
     * @param imageFile 图片文件
     * @param imageView 显示图片的视图
     * @param displayOptions 显示选项
     */
    public void display(File imageFile, ImageView imageView, DisplayOptions displayOptions){
        display(Scheme.FILE.createUri(imageFile.getPath()), imageView, displayOptions, null);
    }

    /**
     * 显示图片
     * @param imageFile 图片文件
     * @param imageView 显示图片的视图
     * @param displayOptionsName 显示选项的名称，你通过configuration.putOptions()方法放进去的DisplayOptions在这里指定一样的名称就可以直接使用
     */
    public void display(File imageFile, ImageView imageView, Enum<?> displayOptionsName){
        display(Scheme.FILE.createUri(imageFile.getPath()), imageView, (DisplayOptions) configuration.getOptions(displayOptionsName), null);
    }

    /**
     * 显示图片
     * @param imageFile 图片文件
     * @param imageView 显示图片的视图
     * @param displayListener 显示监听器
     */
    public void display(File imageFile, ImageView imageView, DisplayListener displayListener){
        display(Scheme.FILE.createUri(imageFile.getPath()), imageView, defaultDisplayOptions, displayListener);
    }

    /**
     * 显示图片
     * @param imageFile 图片文件
     * @param imageView 显示图片的视图
     */
    public void display(File imageFile, ImageView imageView){
        display(Scheme.FILE.createUri(imageFile.getPath()), imageView, defaultDisplayOptions, null);
    }

    
    /**
     * 显示图片
     * @param imageResId 图片资源ID
     * @param imageView 显示图片的视图
     * @param displayOptions 显示选项
     * @param displayListener 显示监听器
     */
    public void display(int imageResId, ImageView imageView, DisplayOptions displayOptions, DisplayListener displayListener){
        display(Scheme.DRAWABLE.createUri(String.valueOf(imageResId)), imageView, displayOptions, displayListener);
    }

    /**
     * 显示图片
     * @param imageResId 图片资源ID
     * @param imageView 显示图片的视图
     * @param displayOptionsName 显示选项的名称，你通过configuration.putOptions()方法放进去的DisplayOptions在这里指定一样的名称就可以直接使用
     * @param displayListener 显示监听器
     */
    public void display(int imageResId, ImageView imageView, Enum<?> displayOptionsName, DisplayListener displayListener){
        display(Scheme.DRAWABLE.createUri(String.valueOf(imageResId)), imageView, (DisplayOptions) configuration.getOptions(displayOptionsName), displayListener);
    }

    /**
     * 显示图片
     * @param imageResId 图片资源ID
     * @param imageView 显示图片的视图
     * @param displayOptions 显示选项
     */
    public void display(int imageResId, ImageView imageView, DisplayOptions displayOptions){
        display(Scheme.DRAWABLE.createUri(String.valueOf(imageResId)), imageView, displayOptions, null);
    }

    /**
     * 显示图片
     * @param imageResId 图片资源ID
     * @param imageView 显示图片的视图
     * @param displayOptionsName 显示选项的名称，你通过configuration.putOptions()方法放进去的DisplayOptions在这里指定一样的名称就可以直接使用
     */
    public void display(int imageResId, ImageView imageView, Enum<?> displayOptionsName){
        display(Scheme.DRAWABLE.createUri(String.valueOf(imageResId)), imageView, (DisplayOptions) configuration.getOptions(displayOptionsName), null);
    }

    /**
     * 显示图片
     * @param imageResId 图片资源ID
     * @param imageView 显示图片的视图
     * @param displayListener 显示监听器
     */
    public void display(int imageResId, ImageView imageView, DisplayListener displayListener){
        display(Scheme.DRAWABLE.createUri(String.valueOf(imageResId)), imageView, defaultDisplayOptions, displayListener);
    }

    /**
     * 显示图片
     * @param imageResId 图片资源ID
     * @param imageView 显示图片的视图
     */
    public void display(int imageResId, ImageView imageView){
        display(Scheme.DRAWABLE.createUri(String.valueOf(imageResId)), imageView, defaultDisplayOptions, null);
    }
	
    
	/**
	 * 获取配置
	 * @return ImageLoader配置
	 */
	public Configuration getConfiguration() {
		return configuration;
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
        configuration.getMemoryCache().clear();
    }

    /**
     * 清除磁盘缓存
     */
    public void clearDiskCache() {
        configuration.getDiskCache().clear();
    }
    
    /**
     * 根据URI获取缓存文件
     */
    public File getCacheFileByUri(String uri){
    	if(configuration == null) return null;
    	DiskCache diskCache = configuration.getDiskCache();
		if(diskCache == null) return null;
		return diskCache.getCacheFileByUri(uri);
    }

    /**
     * 取消显示请求
     * @param imageView
     * @return true：当前ImageView有正在执行的任务并且取消成功；false：当前ImageView没有正在执行的任务
     */
    public static boolean cancelDisplayRequest(ImageView imageView) {
        final DisplayRequest displayRequest = AsyncDrawable.getDisplayRequestByAsyncDrawable(imageView);
        if (displayRequest != null) {
            displayRequest.cancel(true);
            return true;
        }else{
            return false;
        }
    }

    /**
     * 取消潜在的请求
     * @return true：取消成功；false：ImageView所关联的任务就是所需的无需取消
     */
    private static boolean cancelPotentialDisplayRequest(ImageView imageView, DisplayRequest displayRequest) {
        final DisplayRequest potentialDisplayRequest = AsyncDrawable.getDisplayRequestByAsyncDrawable(imageView);
        boolean cancelled = true;
        if (potentialDisplayRequest != null) {
            final String requestId = potentialDisplayRequest.getId();
            if (requestId != null && requestId.equals(displayRequest.getId())) {
                potentialDisplayRequest.setDisplayListener(displayRequest.getDisplayListener());
                cancelled = false;
            }else{
                potentialDisplayRequest.cancel(true);
                cancelled = true;
            }
            if(!cancelled && potentialDisplayRequest.getConfiguration().isDebugMode()){
                Log.d(ImageLoader.LOG_TAG, new StringBuilder(ImageLoader.LOG_TAG).append("：").append("无需取消").append("；").append("ImageViewCode").append("=").append(imageView.hashCode()).append("；").append(potentialDisplayRequest.getName()).toString());
            }
        }
        return cancelled;
    }
}
