package me.xiaopan.android.spear.task;

import java.io.File;
import java.util.concurrent.Executor;

import me.xiaopan.android.spear.decode.ByteArrayDecodeListener;
import me.xiaopan.android.spear.decode.CacheFileDecodeListener;
import me.xiaopan.android.spear.request.DownloadListener;
import me.xiaopan.android.spear.request.LoadRequest;
import me.xiaopan.android.spear.util.FailureCause;

public class LoadJoinDownloadListener implements DownloadListener {

    private Executor executor;
    private LoadRequest loadRequest;

    public LoadJoinDownloadListener(Executor executor, LoadRequest loadRequest) {
        this.executor = executor;
        this.loadRequest = loadRequest;
    }

    @Override
	public void onStarted() {

	}

    @Override
    public void onCompleted(File cacheFile) {
        executor.execute(new LoadTask(loadRequest, new CacheFileDecodeListener(cacheFile, loadRequest)));
    }

    @Override
    public void onCompleted(byte[] data) {
        executor.execute(new LoadTask(loadRequest, new ByteArrayDecodeListener(data, loadRequest)));
    }

	@Override
	public void onFailed(FailureCause failureCause) {
        if(loadRequest.getLoadListener() != null){
            loadRequest.getLoadListener().onFailed(failureCause);
        }
	}

    @Override
    public void onCanceled() {
        if(loadRequest.getLoadListener() != null){
            loadRequest.getLoadListener().onCanceled();
        }
    }
}
