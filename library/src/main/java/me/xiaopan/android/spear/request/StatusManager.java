package me.xiaopan.android.spear.request;

/**
 * 状态管理器
 */
public interface StatusManager {
    /**
     * 转为等待分发状态
     */
    void toWaitDispatchStatus();

    /**
     * 转为正在分发状态
     */
    void toDispatchingStatus();

    /**
     * 转为等待下载状态
     */
    void toWaitDownloadStatus();

    /**
     * 转为获取下载锁状态
     */
    void toGetDownloadLockStatus();

    /**
     * 转为下载中状态
     */
    void toDownloadingStatus();

    /**
     * 转为等待加载状态
     */
    void toWaitLoadStatus();

    /**
     * 转为加载中状态
     */
    void toLoadingStatus();

    /**
     * 转为等待显示状态
     */
    void toWaitDisplayStatus();

    /**
     * 转为显示中状态
     */
    void toDisplayingStatus();

    /**
     * 转为已完成状态
     */
    void toCompletedStatus();

    /**
     * 转为已失败状态
     * @param failCause 失败原因
     */
    void toFailedStatus(FailCause failCause);

    /**
     * 转为已取消状态
     */
    void toCanceledStatus(CancelCause cancelCause);
}
