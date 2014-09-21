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
     * @param from 来源
     */
    public void onCompleted(File cacheFile, From from);

    /**
     * 已完成，当没有选择本地缓存的时候将回调这个方法
     * @param data 数据
     * @param from 来源
     */
    public void onCompleted(byte[] data, From from);

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
         * 本地缓存
         */
        LOCAL_CACHE,
    }
}