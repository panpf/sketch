package me.xiaopan.android.imageloader.task.load;

import me.xiaopan.android.imageloader.decode.ByteArrayInputStreamCreator;
import me.xiaopan.android.imageloader.decode.FileInputStreamCreator;
import me.xiaopan.android.imageloader.task.download.DownloadRequest.DownloadListener;

import java.io.File;
import java.util.concurrent.Executor;

public class LoadDownloadListener implements DownloadListener {

    private Executor executor;
    private LoadRequest loadRequest;

    public LoadDownloadListener(Executor executor, LoadRequest loadRequest) {
        this.executor = executor;
        this.loadRequest = loadRequest;
    }

    @Override
	public void onStart() {

	}

	@Override
	public void onUpdateProgress(long totalLength, long completedLength) {

	}

    @Override
    public void onComplete(File cacheFile) {
        executor.execute(new BitmapLoadTask(loadRequest, new BitmapLoadCallable(loadRequest, new FileInputStreamCreator(cacheFile))));
    }

    @Override
    public void onComplete(byte[] data) {
        executor.execute(new BitmapLoadTask(loadRequest, new BitmapLoadCallable(loadRequest, new ByteArrayInputStreamCreator(data))));
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
