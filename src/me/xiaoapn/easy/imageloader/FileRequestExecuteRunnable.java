package me.xiaoapn.easy.imageloader;

import android.util.Log;


/**
 * 文件请求执行任务
 */
public class FileRequestExecuteRunnable implements Runnable {
	private ImageLoader imageLoader;	//图片加载器
	private FileRequest fileRequest;	//文件请求
	
	/**
	 * 创建文件请求执行任务
	 * @param fileRequest 文件请求
	 */
	public FileRequestExecuteRunnable(ImageLoader imageLoader, FileRequest fileRequest){
		this.imageLoader = imageLoader;
		this.fileRequest = fileRequest;
	}
	
	@Override
	public void run() {
		if(imageLoader.getConfiguration().isDebugMode()){
			Log.d(imageLoader.getConfiguration().getLogTag()+":FileRequestExecuteRunnable", "从本地加载开始："+fileRequest.getName());
		}
		if(GeneralUtils.isAvailableOfFile(fileRequest.getImageFile(), 0, imageLoader, fileRequest.getName())){
			fileRequest.setResultBitmap(GeneralUtils.getBitmapLoader(fileRequest.getOptions()).onDecodeFile(fileRequest.getImageFile(), fileRequest.getImageView(), imageLoader));
		}else{
			fileRequest.setResultBitmap(null);
		}
		if(fileRequest.getResultBitmap() != null && fileRequest.getOptions() != null && fileRequest.getOptions().isCacheInMemory()){
			imageLoader.getConfiguration().getBitmapCacher().put(fileRequest.getId(), fileRequest.getResultBitmap());
		}
		imageLoader.getConfiguration().getHandler().post(new CompleteHandleRunnable(imageLoader, fileRequest));
		if(imageLoader.getConfiguration().isDebugMode()){
			Log.d(imageLoader.getConfiguration().getLogTag()+":FileRequestExecuteRunnable", "从本地加载成功："+fileRequest.getName());
		}
	}
}
