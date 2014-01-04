package me.xiaoapn.easy.imageloader.execute;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import me.xiaoapn.easy.imageloader.ImageLoader;
import me.xiaoapn.easy.imageloader.util.GeneralUtils;
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
			BufferedInputStream inputStream = null;
			try {
				inputStream = new BufferedInputStream(new FileInputStream(fileRequest.getImageFile()));
				bitmap = imageLoader.getConfiguration().getBitmapDecoder().decode(inputStream, fileRequest.getTargetSize(), imageLoader, fileRequest.getName());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}finally{
				if(inputStream != null){
					try {
						inputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return bitmap;
	}
}
