package me.xiaopan.android.imageloader.task.download;

import java.io.File;

import me.xiaopan.android.imageloader.Configuration;
import me.xiaopan.android.imageloader.task.TaskRequest;

/**
 * 下载请求
 */
public class DownloadRequest extends TaskRequest{
	private String url;
	private File saveFile;
	private Configuration configuration;	//配置
	private DownloadListener downloadListener;
	private int maxRetryCount;	//最大重试次数
	
	public DownloadRequest(String url, File saveFile, DownloadListener downloadListener) {
		this.url = url;
		this.saveFile = saveFile;
		this.downloadListener = downloadListener;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public File getSaveFile() {
		return saveFile;
	}
	
	public void setSaveFile(File saveFile) {
		this.saveFile = saveFile;
	}
	
	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public DownloadListener getDownloadListener() {
		return downloadListener;
	}
	
	public void setDownloadListener(DownloadListener downloadListener) {
		this.downloadListener = downloadListener;
	}
	
	public int getMaxRetryCount() {
		return maxRetryCount;
	}

	public void setMaxRetryCount(int maxRetryCount) {
		this.maxRetryCount = maxRetryCount;
	}

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