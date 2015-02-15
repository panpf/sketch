package me.xiaopan.android.spear.request;

/**
 * 状态管理器
 */
public interface StatusManager {
    /**
     * 转为等待分发状态
     */
    public void toWaitDispatchStatus();

    /**
     * 转为正在分发状态
     */
    public void toDispatchingStatus();

    /**
     * 转为等待下载状态
     */
    public void toWaitDownloadStatus();

    /**
     * 转为获取下载锁状态
     */
    public void toGetDownloadLockStatus();

    /**
     * 转为下载中状态
     */
    public void toDownloadingStatus();

    /**
     * 转为等待加载状态
     */
    public void toWaitLoadStatus();

    /**
     * 转为加载中状态
     */
    public void toLoadingStatus();

    /**
     * 转为等待显示状态
     */
    public void toWaitDisplayStatus();

    /**
     * 转为显示中状态
     */
    public void toDisplayingStatus();

    /**
     * 转为已完成状态
     */
    public void toCompletedStatus();

    /**
     * 转为已失败状态
     */
    public void toFailedStatus();

    /**
     * 转为已取消状态
     */
    public void toCanceledStatus();
}
