package me.xiaopan.android.spear.request;

import java.io.File;

import me.xiaopan.android.spear.util.FailureCause;

/**
 * 下载监听器
 */
public interface DownloadListener {
    /**
     * 已开始
     */
    public void onStarted();

    /**
     * 已完成，当选择本地缓存的时候才会回调这个方法
     * @param cacheFile 本地缓存文件
     */
    public void onCompleted(File cacheFile);

    /**
     * 已完成，当没有选择本地缓存的时候将回调这个方法
     * @param data 数据
     */
    public void onCompleted(byte[] data);

    /**
     * 已失败
     * @param failureCause 失败原因
     */
    public void onFailed(FailureCause failureCause);

    /**
     * 已取消
     */
    public void onCanceled();
}