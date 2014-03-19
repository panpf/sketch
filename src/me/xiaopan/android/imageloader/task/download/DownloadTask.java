package me.xiaopan.android.imageloader.task.download;

import java.io.File;
import java.util.concurrent.FutureTask;

/**
 * 下载任务
 */
public class DownloadTask extends FutureTask<Object>{
	private DownloadRequest downloadRequest;
	
	public DownloadTask(DownloadRequest downloadRequest) {
		super(new DownloadCallable(downloadRequest));
		this.downloadRequest = downloadRequest;
	}
	
	@Override
	protected void done() {
		if(!isCancelled()){
			Object result = null;
			try {
				result = get();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(downloadRequest.getDownloadListener() != null){
				if(result != null){
					if(result.getClass().isAssignableFrom(File.class)){
						downloadRequest.getDownloadListener().onComplete((File) result);
					}else{
						downloadRequest.getDownloadListener().onComplete((byte[]) result);
					}
				}else{
					downloadRequest.getDownloadListener().onFailed();
				}
			}
		}
	}
}
