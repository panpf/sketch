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

/**
 * DownloadHelper
 */
public interface DownloadHelper {
    /**
     * 设置名称，用于在log总区分请求
     * @param name 名称
     * @return DownloadHelper
     */
    DownloadHelper name(String name);

    /**
     * 设置监听器
     * @return DownloadHelper
     */
    DownloadHelper listener(DownloadListener downloadListener);

    /**
     * 关闭硬盘缓存
     * @return DownloadHelper
     */
    DownloadHelper disableDiskCache();

    /**
     * 设置进度监听器
     * @param progressListener 进度监听器
     * @return DownloadHelper
     */
    DownloadHelper progressListener(ProgressListener progressListener);

    /**
     * 设置下载参数
     * @param options 下载参数
     * @return DownloadHelper
     */
    DownloadHelper options(DownloadOptions options);

    /**
     * 设置下载参数，你只需要提前将DownloadOptions通过Sketch.putOptions()方法存起来，然后在这里指定其名称即可
     * @param optionsName 参数名称
     * @return DownloadHelper
     */
    DownloadHelper options(Enum<?> optionsName);

    /**
     * 设置请求Level
     * @param requestLevel 请求Level
     * @return DisplayHelper
     */
    DownloadHelper requestLevel(RequestLevel requestLevel);

    /**
     * 提交请求
     * @return Request 你可以通过Request来查看请求的状态或者取消这个请求
     */
    Request commit();
}
