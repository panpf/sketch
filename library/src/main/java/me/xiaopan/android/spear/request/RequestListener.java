package me.xiaopan.android.spear.request;

import android.graphics.Bitmap;

import me.xiaopan.android.spear.util.FailureCause;

/**
 * 加载监听器
 */
public interface RequestListener {
    /**
     * 开始加载
     */
    public void onStart();

    /**
     * 更新加载进度
     * @param totalLength 总长度
     * @param completedLength 已完成长度
     */
    public void onUpdateProgress(long totalLength, long completedLength);

    /**
     * 加载完成
     * @param bitmap 图片
     */
    public void onSuccess(Bitmap bitmap);

    /**
     * 加载失败
     * @param failureCause 失败原因
     */
    public void onFailure(FailureCause failureCause);

    /**
     * 加载取消
     */
    public void onCancel();
}
