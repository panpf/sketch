package me.xiaopan.android.imageloader.task.download;

import java.io.File;

import me.xiaopan.android.imageloader.task.TaskRequest;
import me.xiaopan.android.imageloader.task.load.LoadRequest;

/**
 * 下载请求
 */
public class DownloadRequest extends TaskRequest{
	private DownloadOptions downloadOptions;
	private DownloadListener downloadListener;
	
	public DownloadRequest(String uri) {
		super(uri);
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

	@Override
	public boolean isEnableDiskCache() {
		return downloadOptions != null?downloadOptions.isEnableDiskCache():false;
	}

	@Override
	public int getDiskCachePeriodOfValidity() {
		return downloadOptions != null?downloadOptions.getDiskCachePeriodOfValidity():0;
	}
	
	public static final DownloadRequest valueOf(LoadRequest loadRequest, DownloadListener downloadListener){
		DownloadRequest downloadRequest = new DownloadRequest(loadRequest.getUri());
		downloadRequest.setCacheFile(loadRequest.getCacheFile());
		downloadRequest.setConfiguration(loadRequest.getConfiguration());
		downloadRequest.setDownloadListener(downloadListener);
		downloadRequest.setDownloadOptions(DownloadOptions.valueOf(loadRequest.getLoadOptions()));
		downloadRequest.setName(loadRequest.getUri());
		return downloadRequest;
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
		public void onFailure();

        /**
         * 当下载取消
         */
        public void onCancel();
	}
}