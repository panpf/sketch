package me.xiaopan.sketch;

import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

public class ClickRetryFunction implements ImageViewFunction, View.OnClickListener{
    private boolean clickRetryOnFailed;
    private boolean clickRetryOnPauseDownload;
    private View.OnClickListener wrapperClickListener;

    private boolean displayFailed;
    private boolean pauseDownload;

    private View view;
    private OnRetryListener retryListener;

    public ClickRetryFunction(View view, OnRetryListener retryListener) {
        this.view = view;
        this.retryListener = retryListener;
    }

    @Override
    public void onDisplay() {
        // 重新走了一遍显示流程，这些要重置
        displayFailed = false;
        pauseDownload = false;

        updateClickable();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {

    }

    @Override
    public void onDraw(Canvas canvas) {

    }

    @Override
    public boolean onDisplayStarted() {
        // 重新走了一遍显示流程，这些要重置
        displayFailed = false;
        pauseDownload = false;

        updateClickable();
        return false;
    }

    @Override
    public boolean onUpdateDownloadProgress(int totalLength, int completedLength) {
        return false;
    }

    @Override
    public boolean onDisplayCompleted(ImageFrom imageFrom, String mimeType) {
        return false;
    }

    @Override
    public boolean onDisplayFailed(FailedCause failedCause) {
        // 正常的失败才能重试，因此要过滤一下失败原因
        displayFailed = failedCause != FailedCause.URI_NULL_OR_EMPTY && failedCause != FailedCause.IMAGE_VIEW_NULL && failedCause != FailedCause.URI_NO_SUPPORT;
        updateClickable();
        return false;
    }

    @Override
    public boolean onCanceled(CancelCause cancelCause) {
        pauseDownload = cancelCause == CancelCause.PAUSE_DOWNLOAD;
        updateClickable();
        return false;
    }

    @Override
    public void onClick(View v) {
        if((clickRetryOnFailed && displayFailed) || (clickRetryOnPauseDownload && pauseDownload)){
            retryListener.onRetry();
        }

        if(wrapperClickListener != null){
            wrapperClickListener.onClick(v);
        }
    }

    /**
     * 设置当暂停下载的时候点击显示图片
     */
    public void setClickRetryOnPauseDownload(boolean clickDisplayOnPauseDownload) {
        this.clickRetryOnPauseDownload = clickDisplayOnPauseDownload;
        updateClickable();
    }

    /**
     * 设置当失败的时候点击重新显示图片
     */
    public void setClickRetryOnFailed(boolean clickRedisplayOnFailed) {
        this.clickRetryOnFailed = clickRedisplayOnFailed;
        updateClickable();
    }

    public void setWrapperClickListener(View.OnClickListener wrapperClickListener) {
        this.wrapperClickListener = wrapperClickListener;
        updateClickable();
    }

    private void updateClickable(){
        view.setClickable((clickRetryOnFailed && displayFailed)
        || (clickRetryOnPauseDownload && pauseDownload)
        || wrapperClickListener != null);
    }

    public interface OnRetryListener{
        void onRetry();
    }
}
