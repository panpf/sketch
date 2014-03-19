package me.xiaopan.android.imageloader.task.download;

import java.io.File;

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
