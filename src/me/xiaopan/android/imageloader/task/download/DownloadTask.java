package me.xiaopan.android.imageloader.task.download;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * 下载任务
 */
public class DownloadTask<T> extends FutureTask<T>{
	public DownloadTask(Callable<T> callable) {
		super(callable);
	}

	/**
	 * 下载监听器
	 */
	public interface DownloadListener{
		/**
		 * 开始下载
		 */
		public void onStart();
		
		/**
		 * 下载成功
		 */
		public void onSuccess();
		
		/**
		 * 下载失败
		 */
		public void onFailure();
	}
}
