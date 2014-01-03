package me.xiaoapn.easy.imageloader.execute;

import me.xiaoapn.easy.imageloader.ImageLoader;

public abstract class RequestExecuteRunnable implements Runnable{
	private Request request;
	private ImageLoader imageLoader;

	public RequestExecuteRunnable(ImageLoader imageLoader, Request request) {
		this.request = request;
		this.imageLoader = imageLoader;
	}
	
	@Override
	public void run() {
		if(request.getResultBitmap() != null && request.getOptions().isCacheInMemory()){
			imageLoader.getConfiguration().getBitmapCacher().put(request.getId(), request.getResultBitmap());
		}
		
		//将当前下载对象从正在下载集合中删除
		imageLoader.getLoadingIdSet().remove(request.getId());	
		
		//发送处理Runnable到主线程
		imageLoader.getConfiguration().getHandler().post(new CompleteHandleRunnable(imageLoader, request));
	}
}