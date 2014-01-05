package me.xiaoapn.easy.imageloader.execute.task;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;

import me.xiaoapn.easy.imageloader.ImageLoader;
import me.xiaoapn.easy.imageloader.decode.FileNewBitmapInputStreamListener;
import me.xiaoapn.easy.imageloader.decode.OnNewBitmapInputStreamListener;
import me.xiaoapn.easy.imageloader.download.ImageDownloader;
import me.xiaoapn.easy.imageloader.download.OnCompleteListener;
import me.xiaoapn.easy.imageloader.execute.AsyncDrawable;
import me.xiaoapn.easy.imageloader.util.GeneralUtils;
import me.xiaoapn.easy.imageloader.util.IoUtils;
import me.xiaoapn.easy.imageloader.util.Scheme;

import org.apache.http.client.HttpClient;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

/**
 * 加载任务
 */
public class LoadBitmapTask implements Callable<BitmapDrawable>{
	private static final String ERROR_UNSUPPORTED_SCHEME = "UIL doesn't support scheme(protocol) by default [%s]. You should implement this support yourself (BaseImageDownloader.getStreamFromOtherSource(...))";
	private String logName;
	private Request request;
	private ImageLoader imageLoader;
	private LoadFutureTask futureTask;
	private WeakReference<ImageView> imageViewReference;

	public LoadBitmapTask(ImageLoader imageLoader, Request request, ImageView imageView) {
		this.request = request;
		this.logName = getClass().getSimpleName();
		this.imageLoader = imageLoader;
		this.imageViewReference = new WeakReference<ImageView>(imageView);
		this.futureTask = new LoadFutureTask(this);
	}
	
	@Override
	public BitmapDrawable call() {
		BitmapDrawable bitmapDrawable = null;
		
		final Scheme scheme = Scheme.ofUri(request.getImageUri());
		if(scheme != Scheme.UNKNOWN){
			/* 初始化输入流获取监听器并解码图片 */
			OnNewBitmapInputStreamListener newBitmapInputStreamListener = null;
			if(scheme == Scheme.HTTP || scheme == Scheme.HTTPS){
				if(request.getOptions().getCacheConfig().isCacheInDisk()){
					final File cacheFile = GeneralUtils.getCacheFile(imageLoader.getConfiguration(), request.getOptions(), GeneralUtils.encodeUrl(request.getImageUri()));
					if(GeneralUtils.isAvailableOfFile(cacheFile, request.getOptions().getCacheConfig().getDiskCachePeriodOfValidity(), imageLoader, request.getName())){
						newBitmapInputStreamListener = new FileNewBitmapInputStreamListener(cacheFile);
					}else{
						newBitmapInputStreamListener = getNetNewBitmapInputStreamListener(request.getName(), request.getImageUri(), cacheFile, request.getOptions().getMaxRetryCount(), imageLoader.getConfiguration().getHttpClient());
					}
				}else{
					newBitmapInputStreamListener = getNetNewBitmapInputStreamListener(request.getName(), request.getImageUri(), null, request.getOptions().getMaxRetryCount(), imageLoader.getConfiguration().getHttpClient());
				}
			}else{
				newBitmapInputStreamListener = new OnNewBitmapInputStreamListener() {
					@Override
					public InputStream onNewBitmapInputStream() {
						return getBitmapInputStream(imageLoader.getConfiguration().getContext(), scheme, request.getImageUri());
					}
				};
			}
			
			Bitmap bitmap = imageLoader.getConfiguration().getBitmapDecoder().decode(newBitmapInputStreamListener, request.getTargetSize(), imageLoader, request.getName());
			if(bitmap != null){
				bitmapDrawable = new BitmapDrawable(imageLoader.getConfiguration().getResources(), bitmap);
			}
		}
		
		return bitmapDrawable;
	}
	
	/**
	 * 获取Log名称
	 * @return
	 */
	protected String getLogName() {
		return logName;
	}

    /**
     * 获取请求
     * @return
     */
    public Request getRequest() {
		return request;
	}
    
	/**
	 * 获取FutureTask
	 * @return
	 */
	public LoadFutureTask getFutureTask() {
		return futureTask;
	}
	
	public ImageLoader getImageLoader() {
		return imageLoader;
	}

	/**
     * 获取ImageView
     */
	public ImageView getImageView() {
        final ImageView imageView = imageViewReference.get();
        if (this == getBitmapLoadTask(imageView)) {
            return imageView;
        }else{
        	return null;
        }
    }
	
	/**
	 * 关联ImageView
	 * @param imageView
	 */
	public void relevanceImageView(ImageView imageView){
		if(imageView.getDrawable() != null && imageView.getDrawable() instanceof AsyncDrawable){
			((AsyncDrawable) imageView.getDrawable()).setBitmapLoadTask(this);
			imageViewReference = new WeakReference<ImageView>(imageView);
		}
	}
    
    /**
     * 获取与给定ImageView关联的任务
     * @param imageView 
     * @return 
     */
    public static LoadBitmapTask getBitmapLoadTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapLoadTask();
            }
        }
        return null;
    }

    /**
     * 取消加载工作
     * @param imageLoader
     * @param imageView
     * @return true：当前ImageView有正在执行的任务并且取消成功；false：当前ImageView没有正在执行的任务
     */
    public static boolean cancelBitmapLoadTask(ImageLoader imageLoader, ImageView imageView) {
        final LoadBitmapTask bitmapLoadTask = getBitmapLoadTask(imageView);
        if (bitmapLoadTask != null) {
            bitmapLoadTask.getFutureTask().cancel(true);
            if (imageLoader.getConfiguration().isDebugMode()) {
                Log.w(imageLoader.getConfiguration().getLogTag(), new StringBuffer().append("取消加载任务").append("：").append(bitmapLoadTask.getRequest().getName()).toString());
            }
            return true;
        }else{
        	return false;
        }
    }

    /**
     * 取消潜在的任务
     * @param imageLoader
     * @param request
     * @param imageView
     * @return true：取消成功；false：ImageView所关联的任务就是所需的无需取消
     */
    public static boolean cancelPotentialBitmapLoadTask(ImageLoader imageLoader, Request request, ImageView imageView) {
        final LoadBitmapTask bitmapLoadTask = getBitmapLoadTask(imageView);
        if (bitmapLoadTask != null) {
            final String requestId = bitmapLoadTask.getRequest().getId();
            if (requestId == null || !requestId.equals(request.getId())) {
                bitmapLoadTask.getFutureTask().cancel(true);
                if(imageLoader.getConfiguration().isDebugMode()){
                	Log.w(imageLoader.getConfiguration().getLogTag(), new StringBuffer().append("取消潜在的加载任务").append("：").append(bitmapLoadTask.getRequest().getName()).toString());
                }
            } else {
            	bitmapLoadTask.relevanceImageView(imageView);
                return false;
            }
        }
        return true;
    }
    
    /**
     * 获取网络输入流监听器
     * @param requestName
     * @param imageUrl
     * @param cacheFile
     * @param maxRetryCount
     * @param httpClient
     * @return
     */
    private OnNewBitmapInputStreamListener getNetNewBitmapInputStreamListener(String requestName, String imageUrl, File cacheFile, int maxRetryCount, HttpClient httpClient){
    	final NewBitmapInputStreamListenerHolder holder = new NewBitmapInputStreamListenerHolder();
    	new ImageDownloader(requestName, imageUrl, cacheFile, maxRetryCount, httpClient, imageLoader, new OnCompleteListener() {
			@Override
			public void onFailed() {}
			
			@Override
			public void onComplete(final byte[] data) {
				holder.newBitmapInputStreamListener = new OnNewBitmapInputStreamListener() {
					@Override
					public InputStream onNewBitmapInputStream() {
						return new BufferedInputStream(new ByteArrayInputStream(data), IoUtils.BUFFER_SIZE);
					}
				};
			}
			
			@Override
			public void onComplete(final File cacheFile) {
				holder.newBitmapInputStreamListener = new OnNewBitmapInputStreamListener() {
					@Override
					public InputStream onNewBitmapInputStream() {
						try {
							return new BufferedInputStream(new FileInputStream(cacheFile), IoUtils.BUFFER_SIZE);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
							return null;
						}
					}
				};
			}
		}).execute();
    	return holder.newBitmapInputStreamListener;
    }
    
    /**
     * 获取位图输入流
     * @param context
     * @param scheme
     * @param imageUri
     * @return
     */
    private InputStream getBitmapInputStream(Context context, Scheme scheme, String imageUri){
    	switch (scheme) {
			case FILE:
				return getStreamFromFile(Scheme.FILE.crop(imageUri));
			case CONTENT:
				return getStreamFromContent(context, Scheme.CONTENT.crop(imageUri));
			case ASSETS:
				return getStreamFromAssets(context, Scheme.ASSETS.crop(imageUri));
			case DRAWABLE:
				return getStreamFromDrawable(context, Scheme.DRAWABLE.crop(imageUri));
			default:
				if(imageLoader.getConfiguration().isDebugMode()){
					Log.d(imageLoader.getConfiguration().getLogTag(), new StringBuffer(logName).append("：").append(String.format(ERROR_UNSUPPORTED_SCHEME, imageUri)).toString());
				}
				return null;
		}
    }
    
    /**
     * 从本地文件获取输入流
     * @param filePath
     * @return
     * @throws IOException
     */
    private InputStream getStreamFromFile(String filePath) {
		return new FileNewBitmapInputStreamListener(new File(filePath)).onNewBitmapInputStream();
	}
    
	/**
	 * 从Content获取输入流
	 * @param context
	 * @param contentUri
	 * @return
	 */
	private InputStream getStreamFromContent(Context context, String contentUri) {
		try{
			ContentResolver res = context.getContentResolver();
			Uri uri = Uri.parse(contentUri);
			return res.openInputStream(uri);
		}catch(FileNotFoundException e){
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 从Assets获取输入流
	 * @param context
	 * @param assetsFilePath
	 * @return
	 */
	private InputStream getStreamFromAssets(Context context, String assetsFilePath){
		try{
			return context.getAssets().open(assetsFilePath);
		}catch(IOException e){
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 从Drawable获取输入流
	 * @param context
	 * @param drawableId
	 * @return
	 */
	private InputStream getStreamFromDrawable(Context context, String drawableIdString) {
		int drawableId = Integer.parseInt(drawableIdString);
		
		BitmapDrawable drawable = (BitmapDrawable) context.getResources().getDrawable(drawableId);
		Bitmap bitmap = drawable.getBitmap();

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 0, os);
		return new ByteArrayInputStream(os.toByteArray());
	}
	
	private class NewBitmapInputStreamListenerHolder{
		OnNewBitmapInputStreamListener newBitmapInputStreamListener;
	}
}