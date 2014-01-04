package me.xiaoapn.easy.imageloader.execute;

import me.xiaoapn.easy.imageloader.ImageLoader;
import me.xiaoapn.easy.imageloader.util.GeneralUtils;
import android.util.Log;
import android.widget.ImageView;

/**
 * 文件请求执行任务
 */
public class FileLoadTask extends LoadBitmapTask{
	private ImageLoader imageLoader;	//图片加载器
	private FileRequest fileRequest;	//文件请求
	
	/**
	 * 创建文件请求执行任务
	 * @param fileRequest 文件请求
	 */
	public FileLoadTask(ImageLoader imageLoader, FileRequest fileRequest, ImageView imageView){
		super(imageLoader, fileRequest, imageView);
		this.imageLoader = imageLoader;
		this.fileRequest = fileRequest;
	}
	
	@Override
	public String call() {
		if(imageLoader.getConfiguration().isDebugMode()){
			Log.i(imageLoader.getConfiguration().getLogTag(), new StringBuffer(getLogName()).append("：").append("从本地加载开始").append("：").append(fileRequest.getName()).toString());
		}
		if(GeneralUtils.isAvailableOfFile(fileRequest.getImageFile(), 0, imageLoader, fileRequest.getName())){
			fileRequest.setResultBitmap(imageLoader.getConfiguration().getBitmapLoader().onFromFileLoad(fileRequest.getImageFile(), fileRequest.getImageView(), imageLoader));
		}else{
			fileRequest.setResultBitmap(null);
		}
		return super.call();
	}
}
