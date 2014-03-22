package me.xiaopan.android.imageloader.task.download;

import me.xiaopan.android.imageloader.task.Task;

import java.io.File;

/**
 * 下载任务
 */
public class DownloadTask extends Task{
	private DownloadRequest downloadRequest;
	
	public DownloadTask(DownloadRequest downloadRequest) {
		super(downloadRequest, new DownloadCallable(downloadRequest));
		this.downloadRequest = downloadRequest;
	}
	
	@Override
	protected void done() {
		if(isCancelled()){
            if(downloadRequest.getDownloadListener() != null){
                downloadRequest.getDownloadListener().onCancel();
            }
		}else{
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
					downloadRequest.getDownloadListener().onFailure();
				}
			}
        }
	}
}
