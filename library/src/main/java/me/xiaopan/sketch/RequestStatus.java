package me.xiaopan.sketch;

/**
 * 请求的状态
 */
public enum RequestStatus {
    /**
     * 等待分发
     */
    WAIT_DISPATCH,

    /**
     * 正在分发
     */
    DISPATCHING,

    /**
     * 等待下载
     */
    WAIT_DOWNLOAD,

    /**
     * 正在获取下载锁
     */
    GET_DOWNLOAD_LOCK,

    /**
     * 正在下载
     */
    DOWNLOADING,

    /**
     * 等待加载
     */
    WAIT_LOAD,

    /**
     * 正在加载
     */
    LOADING,

    /**
     * 等待显示
     */
    WAIT_DISPLAY,

    /**
     * 正在显示
     */
    DISPLAYING,

    /**
     * 已完成
     */
    COMPLETED,

    /**
     * 已失败
     */
    FAILED,

    /**
     * 已取消
     */
    CANCELED,
}