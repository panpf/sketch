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
 * 请求
 */
public interface Request{
    /**
     * 获取图片Uri
     * @return 图片Uri
     */
    String getUri();

    /**
     * 获取名称，常用来在log中区分请求
     * @return 请求名称
     */
    String getName();

    /**
     * 获取请求的状态
     * @return 请求的状态
     */
    RequestStatus getRequestStatus();

    /**
     * 获取结果图片来源
     * @return 结果图片来源
     */
    ImageFrom getImageFrom();

    /**
     * 获取失败原因
     * @return 失败原因
     */
    FailCause getFailCause();

    /**
     * 获取取消原因
     * @return 取消原因
     */
    CancelCause getCancelCause();

    /**
     * 是否已经结束
     * @return true：已经结束了；false：还在处理中
     */
    boolean isFinished();

    /**
     * 是否已经取消
     * @return true：请求已经取消了；false：请求尚未取消
     */
    boolean isCanceled();

    /**
     * 取消请求
     * @return true：取消成功；false：请求已经完成或已经取消
     */
    boolean cancel();
}
