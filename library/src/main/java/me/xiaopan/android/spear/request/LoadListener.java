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
public interface LoadListener {
    /**
     * 已开始
     */
    public void onStarted();

    /**
     * 已完成
     * @param bitmap 图片
     * @param from 来源
     */
    public void onCompleted(Bitmap bitmap, From from);

    /**
     * 已失败
     * @param failureCause 失败原因
     */
    public void onFailed(FailureCause failureCause);

    /**
     * 已取消
     */
    public void onCanceled();

    /**
     * 来源
     */
    public enum From{
        /**
         * 网络下载
         */
        NETWORK,

        /**
         * 本地
         */
        LOCAL,
    }
}
