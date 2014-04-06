/*
 * Copyright (C) 2013 Peng fei Pan <sky@xiaopan.me>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.xiaopan.android.imageloader.task.display;

import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

/**
 * 显示监听器，值的注意的是DisplayListener中所有的方法都会在主线中执行，所以实现着不必考虑异步线程中刷新UI的问题
 */
public interface DisplayListener {
    /**
     * 开始
     */
    public void onStart();

    /**
     * 更新加载进度
     * @param totalLength 总长度
     * @param completedLength 已完成长度
     */
    public void onUpdateProgress(long totalLength, long completedLength);

    /**
     * 完成
     * @param uri 地址
     * @param imageView 视图
     * @param drawable 图片
     */
    public void onComplete(String uri, ImageView imageView, BitmapDrawable drawable);

    /**
     * 加载失败
     */
    public void onFailure();

    /**
     * 取消
     */
    public void onCancel();
}
