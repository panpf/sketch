package me.xiaoapn.easy.imageloader.download;

import java.io.File;

import me.xiaoapn.easy.imageloader.Configuration;
import me.xiaoapn.easy.imageloader.task.Request;

/**
 * 图片下载器
 */
public interface ImageDownloader {
	/**
	 * 执行
	 */
	public void execute(Request request, File cacheFile, Configuration configuration, DownloadListener onCompleteListener);
	
	/**
	 * 下载监听器
	 */
	public interface DownloadListener {
		/**
		 * 当下载完成
		 * @param cacheFile
		 */
		public void onComplete(File cacheFile);
		
		/**
		 * 当下载完成
		 * @param data
		 */
		public void onComplete(byte[] data);
		
		/**
		 * 当下载失败
		 */
		public void onFailed();
	}
}