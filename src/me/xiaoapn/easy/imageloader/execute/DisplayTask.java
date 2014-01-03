package me.xiaoapn.easy.imageloader.execute;

import me.xiaoapn.easy.imageloader.ImageLoader;
import android.util.Log;

/**
 * 显示任务
 */
public class DisplayTask implements Runnable{
	private ImageLoader imageLoader;
	private Request request;
	
	public DisplayTask(ImageLoader imageLoader, Request request){
		this.imageLoader = imageLoader;
		this.request = request;
	}
	
	@Override
	public void run() {
		request.getOptions().getBitmapDisplayer().display(request.getImageView(), request.getResultBitmap(), request.getLoadedFrom());
		
		if(imageLoader.getConfiguration().isDebugMode()){
			Log.d(imageLoader.getConfiguration().getLogTag(), "从缓存中加载："+request.getName());
		}
	}
}