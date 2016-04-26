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
 * 下载请求
 */
public interface DownloadRequest {

    /**
     * 获取请求基本属性
     */
    RequestAttrs getAttrs();

    /**
     * 获取下载选项
     */
    DownloadOptions getOptions();

    /**
     * 获取下载结果
     */
    DownloadResult getDownloadResult();

    /**
     * 获取失败原因
     */
    FailedCause getFailedCause();

    /**
     * 获取取消原因
     */
    CancelCause getCancelCause();

    /**
     * 是否已经结束
     */
    boolean isFinished();

    /**
     * 是否已经取消
     */
    boolean isCanceled();

    /**
     * 取消请求
     *
     * @return true：取消成功；false：请求已经完成或已经取消
     */
    boolean cancel();

    /**
     * 设置状态
     */
    void setStatus(SketchRequest.Status status);

    /**
     * 更新进度
     */
    void updateProgress(int totalLength, int completedLength);

    /**
     * 提交
     */
    void submit();
}