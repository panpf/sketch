package me.xiaoapn.easy.imageloader.execute.task;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import me.xiaoapn.easy.imageloader.ImageLoader;
import me.xiaoapn.easy.imageloader.decode.OnNewBitmapInputStreamListener;
import me.xiaoapn.easy.imageloader.util.GeneralUtils;
import me.xiaoapn.easy.imageloader.util.IoUtils;
import android.graphics.Bitmap;
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
	protected Bitmap loadBitmap() {
		Bitmap bitmap = null;
		if(GeneralUtils.isAvailableOfFile(fileRequest.getImageFile(), 0, imageLoader, fileRequest.getName())){
			bitmap = imageLoader.getConfiguration().getBitmapDecoder().decode(new OnNewBitmapInputStreamListener() {
				@Override
				public InputStream onNewBitmapInputStream() {
					try {
						return new BufferedInputStream(new FileInputStream(fileRequest.getImageFile()), IoUtils.BUFFER_SIZE);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
						return null;
					}
				}
			}, fileRequest.getTargetSize(), imageLoader, fileRequest.getName());
		}
		return bitmap;
	}
}
