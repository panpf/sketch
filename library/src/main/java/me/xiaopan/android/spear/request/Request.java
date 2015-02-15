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
import me.xiaopan.android.spear.util.ImageScheme;

/**
 * 请求
 */
public interface Request extends Runnable, StatusManager, RunManager{
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
     */
    public void dispatch();

    /**
     * 请求状态
     */
    public enum Status{
        /**
         * 等待分发
         */
        WAIT_DISPATCH,

        /**
         * 正在分发
         */
        DISPATCHING,

        /**
         * 等待下载
         */
        WAIT_DOWNLOAD,

        /**
         * 获取下载锁
         */
        GET_DOWNLOAD_LOCK,

        /**
         * 下载中
         */
        DOWNLOADING,

        /**
         * 等待加载
         */
        WAIT_LOAD,

        /**
         * 加载中
         */
        LOADING,

        /**
         * 等待显示
         */
        WAIT_DISPLAY,

        /**
         * 显示中
         */
        DISPLAYING,

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

    public enum RunStatus{
        /**
         * 分发
         */
        DISPATCH,

        /**
         * 加载
         */
        LOAD,

        /**
         * 下载
         */
        DOWNLOAD,
    }
}
