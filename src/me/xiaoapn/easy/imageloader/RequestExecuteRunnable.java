package me.xiaoapn.easy.imageloader;

public abstract class RequestExecuteRunnable implements Runnable{
	private Request request;
	private ImageLoader imageLoader;

	public RequestExecuteRunnable(ImageLoader imageLoader, Request request) {
		this.request = request;
		this.imageLoader = imageLoader;
	}
	
	@Override
	public void run() {
		//尝试缓存到内存中
		if(request.getResultBitmap() != null && request.getOptions() != null && request.getOptions().isCacheInMemory()){
			imageLoader.getConfiguration().getBitmapCacher().put(request.getId(), request.getResultBitmap());
		}
		
		//将当前下载对象从正在下载集合中删除
		imageLoader.getLoadingIdSet().remove(request.getId());	
		
		//发送处理Runnable到主线程
		imageLoader.getConfiguration().getHandler().post(new CompleteHandleRunnable(imageLoader, request));
	}
}