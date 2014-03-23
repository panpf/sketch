package me.xiaopan.android.imageloader.task.load;

import me.xiaopan.android.imageloader.task.download.DownloadListener;

import java.io.File;
import java.util.concurrent.Executor;

public class LoadJoinDownloadListener implements DownloadListener {

    private Executor executor;
    private LoadRequest loadRequest;

    public LoadJoinDownloadListener(Executor executor, LoadRequest loadRequest) {
        this.executor = executor;
        this.loadRequest = loadRequest;
    }

    @Override
	public void onStart() {

	}

	@Override
	public void onUpdateProgress(long totalLength, long completedLength) {
        if(loadRequest.getLoadListener() != null){
            loadRequest.getLoadListener().onUpdateProgress(totalLength, completedLength);
        }
	}

    @Override
    public void onComplete(File cacheFile) {
        executor.execute(new BitmapLoadTask(loadRequest, new BitmapLoadCallable(loadRequest, new CacheFileDecodeListener(cacheFile, loadRequest))));
    }

    @Override
    public void onComplete(byte[] data) {
        executor.execute(new BitmapLoadTask(loadRequest, new BitmapLoadCallable(loadRequest, new ByteArrayDecodeListener(data, loadRequest))));
    }

	@Override
	public void onFailure() {
        if(loadRequest.getLoadListener() != null){
            loadRequest.getLoadListener().onFailure();
        }
	}

    @Override
    public void onCancel() {
        if(loadRequest.getLoadListener() != null){
            loadRequest.getLoadListener().onCancel();
        }
    }
}
