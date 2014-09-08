package me.xiaopan.android.spear.request;


import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import me.xiaopan.android.spear.util.FailureCause;

/**
 * 显示监听器，值的注意的是DisplayListener中所有的方法都会在主线中执行，所以实现着不必考虑异步线程中刷新UI的问题
 */
public interface DisplayListener {
    /**
     * 已开始
     */
    public void onStarted();

    /**
     * 已完成
     * @param uri 地址
     * @param imageView 视图
     * @param drawable 图片
     */
    public void onCompleted(String uri, ImageView imageView, BitmapDrawable drawable);

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
