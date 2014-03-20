package me.xiaopan.android.imageloader.task.download;

import java.io.File;

import me.xiaopan.android.imageloader.task.TaskRequest;

/**
 * 下载请求
 */
public class DownloadRequest extends TaskRequest{
	private File saveFile;
	private DownloadOptions downloadOptions;
	private DownloadListener downloadListener;
	
	public DownloadRequest(String uri) {
		setUri(uri);
	}

	public File getSaveFile() {
		return saveFile;
	}
	
	public void setSaveFile(File saveFile) {
		this.saveFile = saveFile;
	}

	public DownloadOptions getDownloadOptions() {
		return downloadOptions;
	}

	public void setDownloadOptions(DownloadOptions downloadOptions) {
		this.downloadOptions = downloadOptions;
	}

	public DownloadListener getDownloadListener() {
		return downloadListener;
	}
	
	public void setDownloadListener(DownloadListener downloadListener) {
		this.downloadListener = downloadListener;
	}
	
	/**
	 * 下载监听器
	 */
	public interface DownloadListener {
		/**
		 * 开始下载
		 */
		public void onStart();
		
		/**
		 * 更新下载进度
		 * @param totalLength
		 * @param completedLength
		 */
		public void onUpdateProgress(long totalLength, long completedLength);
		
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