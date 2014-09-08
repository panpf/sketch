package me.xiaopan.android.spear.task;

import me.xiaopan.android.spear.request.ProgressCallback;

public class LoadJoinDownloadProgressCallback implements ProgressCallback{
    private ProgressCallback loadProgressCallback;

    public LoadJoinDownloadProgressCallback(ProgressCallback loadProgressCallback) {
        this.loadProgressCallback = loadProgressCallback;
    }

    @Override
    public void onUpdateProgress(long totalLength, long completedLength) {
        if(loadProgressCallback != null){
            loadProgressCallback.onUpdateProgress(totalLength, completedLength);
        }
    }
}
