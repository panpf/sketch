/*
 * Copyright (C) 2019 panpf <panpfpanpf@outlook.com>
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

package com.github.panpf.sketch.request;

/**
 * 下载进度监听器
 */
public interface DownloadProgressListener {

    /**
     * 更新下载进度
     *
     * @param totalLength     总长度
     * @param completedLength 已完成长度
     */
    // TODO: 2019-05-02 这里用的是 int 改成 long，涉及到的所有代码都要改
    void onUpdateDownloadProgress(int totalLength, int completedLength);
}
