package me.xiaopan.android.spear.request;

/**
 * 进度回调
 */
public interface ProgressCallback {

    /**
     * 更新下载进度
     * @param totalLength 总长度
     * @param completedLength 已完成长度
     */
    public void onUpdateProgress(long totalLength, long completedLength);
}
