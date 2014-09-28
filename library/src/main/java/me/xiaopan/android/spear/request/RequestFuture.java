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

/**
 * 你可以通过RequestFuture来查看请求的状态或者取消这个请求
 */
public class RequestFuture {
    private Request request;

    public RequestFuture(Request request) {
        this.request = request;
    }

    /**
     * 获取请求名称，常用来在log中区分请求
     * @return 请求名称
     */
    public String getName(){
        return request.getName();
    }

    /**
     * 获取请求URI
     * @return 请求URI
     */
    public String getUri(){
        return request.getUri();
    }

    /**
     * 是否已经取消
     * @return 是否已经取消
     */
    public boolean isCanceled() {
        return request.isCanceled();
    }

    /**
     * 是否已经结束
     * @return 是否已经结束
     */
    public boolean isFinished() {
        return request.isFinished();
    }

    /**
     * 获取请求的状态
     * @return 请求的状态
     */
    public Request.Status getStatus() {
        return request.getStatus();
    }

    /**
     * 取消请求
     * @return true：取消成功；false：请求已经完成或已经取消
     */
    public boolean cancel() {
        return request.cancel();
    }
}