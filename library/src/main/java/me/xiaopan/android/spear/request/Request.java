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

import me.xiaopan.android.spear.task.Task;

/**
 * 请求
 */
public interface Request {
    /**
     * 获取请求名称，常用来在log中区分请求
     * @return 请求名称
     */
    public String getName();

    /**
     * 获取请求URI
     * @return 请求URI
     */
    public String getUri();

    /**
     * 设置任务
     * @param task 任务
     */
	public void setTask(Task task);

    /**
     * 是否已经结束
     * @return 是否已经结束
     */
    public boolean isFinished();

    /**
     * 是否已经取消
     */
    public boolean isCanceled();

    /**
     * 获取请求的状态
     * @return 请求的状态
     */
    public Status getStatus();

    /**
     * 设置请求的状态
     * @param status 请求的状态
     */
    public void setStatus(Status status);

    /**
     * 取消请求
     * @return true：取消成功；false：请求已经完成或已经取消
     */
    public boolean cancel();

    /**
     * 请求状态
     */
    public enum Status{
        /**
         * 等待中
         */
        WAITING,

        /**
         * 加载中
         */
        LOADING,

        /**
         * 已完成
         */
        COMPLETED,

        /**
         * 已失败
         */
        FAILED,

        /**
         * 已取消
         */
        CANCELED,
    }
}
