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

package me.xiaopan.sketch;

import android.os.Message;

/**
 * 下载请求
 */
public interface DownloadRequest extends Request, RequestRunManager {
    /**
     * 获取Sketch
     * @return Sketch
     */
    Sketch getSketch();

    /**
     * 设置请求名称，用于在log中区分请求
     * @param name 请求名称
     */
    void setName(String name);

    /**
     * 获取Uri协议类型
     */
    UriScheme getUriScheme();

    /**
     * 设置进度监听器
     * @param progressListener 进度监听器
     */
    void setProgressListener(ProgressListener progressListener);

    /**
     * 是否将图片缓存在本地
     * @return 是否将图片缓存在本地（默认是）
     */
    boolean isCacheInDisk();

    /**
     * 设置是否将图片缓存在本地
     * @param cacheInDisk 是否将图片缓存在本地（默认是）
     */
    void setCacheInDisk(boolean cacheInDisk);

    /**
     * 设置下载监听器
     * @param downloadListener 下载监听器
     */
    void setDownloadListener(DownloadListener downloadListener);

    /**
     * 设置请求Level
     * @param requestLevel 请求Level
     */
    void setRequestLevel(RequestLevel requestLevel);

    /**
     * 设置请求Level的来源
     * @param requestLevelFrom 请求Level的来源
     */
    void setRequestLevelFrom(RequestLevelFrom requestLevelFrom);

    /**
     * 设置状态
     * @param requestStatus 状态
     */
    void setRequestStatus(RequestStatus requestStatus);

    /**
     * 更新进度
     * @param totalLength 总长度
     * @param completedLength 已完成长度
     */
    void updateProgress(int totalLength, int completedLength);

    /**
     * 失败了
     * @param failCause 失败原因
     */
    void toFailedStatus(FailCause failCause);

    /**
     * 取消了
     * @param cancelCause 取消原因
     */
    void toCanceledStatus(CancelCause cancelCause);

    /**
     * 在主线程中执行
     * @param msg 消息
     */
    void invokeInMainThread(Message msg);
}