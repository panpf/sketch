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
