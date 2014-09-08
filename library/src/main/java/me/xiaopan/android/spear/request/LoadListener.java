package me.xiaopan.android.spear.request;

import android.graphics.Bitmap;

import me.xiaopan.android.spear.util.FailureCause;

/**
 * 加载监听器
 */
public interface LoadListener {
    /**
     * 已开始
     */
    public void onStarted();

    /**
     * 已完成
     * @param bitmap 图片
     */
    public void onCompleted(Bitmap bitmap);

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
