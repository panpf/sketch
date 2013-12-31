package me.xiaoapn.easy.imageloader;


/**
 * 本地加载任务
 */
public class LocalLoadTask implements Runnable {
	private ImageLoader imageLoader;	//图片加载器
	private LoadRequest loadRequest;	//加载请求
	
	/**
	 * 创建从本地加载图片的任务
	 * @param loadRequest 加载请求
	 */
	public LocalLoadTask(ImageLoader imageLoader, LoadRequest loadRequest){
		this.imageLoader = imageLoader;
		this.loadRequest = loadRequest;
	}
	
	@Override
	public void run() {
		imageLoader.getConfiguration().log("从本地加载图片："+loadRequest.getName());
		if(GeneralUtils.isAvailableOfFile(loadRequest.getCacheFile(), 0, imageLoader, loadRequest.getName())){
			loadRequest.setResultBitmap(GeneralUtils.getBitmapLoader(loadRequest.getOptions()).onDecodeFile(loadRequest.getCacheFile(), loadRequest.getImageView(), imageLoader));
		}else{
			loadRequest.setResultBitmap(null);
		}
		if(loadRequest.getResultBitmap() != null && loadRequest.getOptions() != null && loadRequest.getOptions().isCacheInMemory()){
			imageLoader.getConfiguration().getBitmapCacher().put(loadRequest.getId(), loadRequest.getResultBitmap());
		}
		imageLoader.getConfiguration().getHandler().post(new LoadResultHandleRunnable(imageLoader, loadRequest));
	}
}
