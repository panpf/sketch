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

package me.xiaopan.android.imageloader.task.download;

import java.io.File;

/**
 * 下载监听器
 */
public interface DownloadListener {
    /**
     * 开始下载
     */
    public void onStart();

    /**
     * 更新下载进度
     * @param totalLength
     * @param completedLength
     */
    public void onUpdateProgress(long totalLength, long completedLength);

    /**
     * 下载完成
     * @param cacheFile
     */
    public void onComplete(File cacheFile);

    /**
     * 下载完成
     * @param data
     */
    public void onComplete(byte[] data);

    /**
     * 下载失败
     */
    public void onFailure();

    /**
     * 下载取消
     */
    public void onCancel();
}
