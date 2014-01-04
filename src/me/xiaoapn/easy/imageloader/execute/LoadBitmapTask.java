package me.xiaoapn.easy.imageloader.execute;

import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import me.xiaoapn.easy.imageloader.ImageLoader;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

/**
 * 加载任务
 */
public abstract class LoadBitmapTask implements Callable<Bitmap>{
	private String logName;
	private Request request;
	private ImageLoader imageLoader;
	private FutureTask<Bitmap> futureTask;
	private WeakReference<ImageView> imageViewReference;

	public LoadBitmapTask(ImageLoader imageLoader, Request request, ImageView imageView) {
		this.request = request;
		this.logName = getClass().getSimpleName();
		this.futureTask = new FutureTask<Bitmap>(this);
		this.imageLoader = imageLoader;
		this.imageViewReference = new WeakReference<ImageView>(imageView);
	}
	
	protected abstract Bitmap loadBitmap();
	
	@Override
	public Bitmap call() {
		if(futureTask.isCancelled()){
			if(imageLoader.getConfiguration().isDebugMode()){
				Log.e(imageLoader.getConfiguration().getLogTag(), new StringBuffer().append(logName).append("：").append("已取消").append(request.getName()).toString());
			}
			return null;
		}
		
		Bitmap bitmap = loadBitmap();
		if(request.getOptions().getCacheConfig().isCacheInMemory() && bitmap != null){
			imageLoader.getConfiguration().getBitmapCacher().put(request.getId(), bitmap);
		}

		ImageView imageView = getImageView();
		if (imageView != null) {
			imageLoader.getConfiguration().getHandler().post(new DisplayBitmapTask(imageLoader, imageView, bitmap, request.getOptions(), request.getName(), false));
		}else{
			if(imageLoader.getConfiguration().isDebugMode()){
				Log.e(imageLoader.getConfiguration().getLogTag(), new StringBuffer().append(logName).append("：").append("已取消绑定关系").append("：").append(request.getName()).toString());
			}
		}
		return bitmap;
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
	public FutureTask<Bitmap> getFutureTask() {
		return futureTask;
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
            final String bitmapData = bitmapLoadTask.getRequest().getId();
            if (bitmapData == null || !bitmapData.equals(request.getId())) {
                bitmapLoadTask.getFutureTask().cancel(true);
                if(imageLoader.getConfiguration().isDebugMode()){
                	Log.w(imageLoader.getConfiguration().getLogTag(), new StringBuffer().append("取消潜在的加载任务").append("：").append(bitmapLoadTask.getRequest().getName()).toString());
                }
            } else {
                return false;
            }
        }
        return true;
    }
}