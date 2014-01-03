package me.xiaoapn.easy.imageloader.execute;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;

import me.xiaoapn.easy.imageloader.ImageLoader;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

public abstract class RequestExecuteRunnable implements Runnable{
	private AtomicBoolean cancelled;
	private Request request;
	private ImageLoader imageLoader;
	private WeakReference<ImageView> imageViewReference;

	public RequestExecuteRunnable(ImageLoader imageLoader, Request request, ImageView imageView) {
		this.cancelled = new AtomicBoolean(true);
		this.request = request;
		this.imageLoader = imageLoader;
		this.imageViewReference = new WeakReference<ImageView>(imageView);
	}
	
	@Override
	public void run() {
		if(!isCancelled()){
			if(request.getResultBitmap() != null && request.getOptions().getCacheConfig().isCacheInMemory()){
				imageLoader.getConfiguration().getBitmapCacher().put(request.getId(), request.getResultBitmap());
			}

			final ImageView imageView = getAttachedImageView();
			if (imageView != null) {
				imageLoader.getConfiguration().getHandler().post(new DisplayBitmapTask(imageView, request.getResultBitmap(), request.getOptions(), false));
			}
			
			if(imageLoader.getConfiguration().isDebugMode()){
				Log.e(imageLoader.getConfiguration().getLogTag()+":RequestExecuteRunnable", ":完成："+request.getName());
			}
		}
	}
	
	public boolean isCancelled() {
		return cancelled.get();
	}

	public void cancel(ImageView imageView){
	}

    /**
     * Returns the ImageView associated with this task as long as the ImageView's task still
     * points to this task as well. Returns null otherwise.
     */
    private ImageView getAttachedImageView() {
        final ImageView imageView = imageViewReference.get();
        if (this == getBitmapWorkerTask(imageView)) {
            return imageView;
        }else{
        	return null;
        }
    }
    
    /**
     * @param imageView Any imageView
     * @return Retrieve the currently active work task (if any) associated with this imageView.
     * null if there is no such task.
     */
    public static RequestExecuteRunnable getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    /**
     * Cancels any pending work attached to the provided ImageView.
     * @param imageView
     */
    public static void cancelWork(ImageView imageView) {
        final RequestExecuteRunnable bitmapWorkerTask = getBitmapWorkerTask(imageView);
        if (bitmapWorkerTask != null) {
//            bitmapWorkerTask.cancel(true);
        }
    }
}