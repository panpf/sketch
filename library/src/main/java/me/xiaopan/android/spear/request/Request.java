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

import me.xiaopan.android.spear.Spear;
import me.xiaopan.android.spear.execute.RequestExecutor;
import me.xiaopan.android.spear.util.ImageScheme;

/**
 * 请求
 */
public interface Request {
    /**
     * 获取Spear
     * @return Spear
     */
    public Spear getSpear();

    /**
     * 设置Spear
     * @param spear Spar
     */
    public void setSpear(Spear spear);

    /**
     * 获取图片Uri
     * @return 图片Uri
     */
    public String getUri();

    /**
     * 设置图片Uri
     * @param uri 图片Uri
     */
    public void setUri(String uri);

    /**
     * 获取Uri协议类型
     */
    public ImageScheme getImageScheme();

    /**
     * 设置Uri协议类型
     * @param imageScheme Uri协议类型
     */
    public void setImageScheme(ImageScheme imageScheme);

    /**
     * 获取名称，常用来在log中区分请求
     * @return 请求名称
     */
    public String getName();

    /**
     * 设置名称
     * @param name 名称
     */
    public void setName(String name);

    /**
     * 获取请求的状态
     * @return 请求的状态
     */
    public Status getStatus();

    /**
     * 设置状态
     * @param status 状态
     */
    public void setStatus(Status status);

    /**
     * 是否已经结束
     * @return true：已经结束了；false：还在处理中
     */
    public boolean isFinished();

    /**
     * 是否已经取消
     * @return true：请求已经取消了；false：请求尚未取消
     */
    public boolean isCanceled();

    /**
     * 取消请求
     * @return true：取消成功；false：请求已经完成或已经取消
     */
    public boolean cancel();

    /**
     * 分发请求
     * @param requestExecutor 请求执行器
     */
    public void dispatch(RequestExecutor requestExecutor);

    /**
     * 更新进度
     * @param totalLength 总长度
     * @param completedLength 已完成长度
     */
    public void updateProgress(int totalLength, int completedLength);

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
