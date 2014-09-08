package me.xiaopan.android.spear.task;

import me.xiaopan.android.spear.request.DisplayRequest;
import me.xiaopan.android.spear.request.ProgressCallback;

public class DisplayJoinLoadProgressCallback implements ProgressCallback{
    private DisplayRequest request;
    private ProgressCallback displayProgressCallback;

    public DisplayJoinLoadProgressCallback(DisplayRequest request, ProgressCallback displayProgressCallback) {
        this.request = request;
        this.displayProgressCallback = displayProgressCallback;
    }

    @Override
    public void onUpdateProgress(final long totalLength, final long completedLength) {
        if(displayProgressCallback != null){
            request.getSpear().getHandler().post(new UpdateProgressRunnable(request, totalLength, completedLength, displayProgressCallback));
        }
    }

    private static class UpdateProgressRunnable implements Runnable{
        private DisplayRequest request;
        private long totalLength;
        private long completedLength;
        private ProgressCallback displayProgressCallback;

        private UpdateProgressRunnable(DisplayRequest request, long totalLength, long completedLength, ProgressCallback displayProgressCallback) {
            this.request = request;
            this.totalLength = totalLength;
            this.completedLength = completedLength;
            this.displayProgressCallback = displayProgressCallback;
        }

        @Override
        public void run() {
            if(request.isCanceled() || request.isFinished()){
                return;
            }
            displayProgressCallback.onUpdateProgress(totalLength, completedLength);
        }
    }
}
