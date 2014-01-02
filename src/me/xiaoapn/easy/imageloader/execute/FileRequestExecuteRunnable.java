package me.xiaoapn.easy.imageloader.execute;

import me.xiaoapn.easy.imageloader.ImageLoader;
import me.xiaoapn.easy.imageloader.util.GeneralUtils;
import android.util.Log;


/**
 * 文件请求执行任务
 */
public class FileRequestExecuteRunnable extends RequestExecuteRunnable{
	private ImageLoader imageLoader;	//图片加载器
	private FileRequest fileRequest;	//文件请求
	
	/**
	 * 创建文件请求执行任务
	 * @param fileRequest 文件请求
	 */
	public FileRequestExecuteRunnable(ImageLoader imageLoader, FileRequest fileRequest){
		super(imageLoader, fileRequest);
		this.imageLoader = imageLoader;
		this.fileRequest = fileRequest;
	}
	
	@Override
	public void run() {
		if(imageLoader.getConfiguration().isDebugMode()){
			Log.d(imageLoader.getConfiguration().getLogTag()+":FileRequestExecuteRunnable", "从本地加载开始："+fileRequest.getName());
		}
		if(GeneralUtils.isAvailableOfFile(fileRequest.getImageFile(), 0, imageLoader, fileRequest.getName())){
			fileRequest.setResultBitmap(GeneralUtils.getBitmapLoader(fileRequest.getOptions()).onFromFileLoad(fileRequest.getImageFile(), fileRequest.getImageView(), imageLoader));
		}else{
			fileRequest.setResultBitmap(null);
		}
		super.run();
		if(imageLoader.getConfiguration().isDebugMode()){
			Log.d(imageLoader.getConfiguration().getLogTag()+":FileRequestExecuteRunnable", "从本地加载成功："+fileRequest.getName());
		}
	}
}
