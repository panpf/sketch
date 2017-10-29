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

package me.xiaopan.sketch.http;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import me.xiaopan.sketch.Identifier;

/**
 * 负责发送 HTTP 请求，并返回响应
 */
public interface HttpStack extends Identifier {
    int DEFAULT_READ_TIMEOUT = 7 * 1000;   // 默认读取超时时间
    int DEFAULT_CONNECT_TIMEOUT = 7 * 1000;    // 默认连接超时时间
    int DEFAULT_MAX_RETRY_COUNT = 0;    // 默认最大重试次数

    /**
     * 获取最大重试次数
     */
    int getMaxRetryCount();

    /**
     * 设置最大重试次数（默认HttpStack.DEFAULT_MAX_RETRY_COUNT）
     */
    @SuppressWarnings("unused")
    @NonNull
    HttpStack setMaxRetryCount(int maxRetryCount);

    /**
     * 获取连接超时时间（默认HttpStack.DEFAULT_CONNECT_TIMEOUT）
     */
    @SuppressWarnings("unused")
    int getConnectTimeout();

    /**
     * 设置连接超时时间（默认HttpStack.DEFAULT_CONNECT_TIMEOUT）
     */
    @SuppressWarnings("unused")
    @NonNull
    HttpStack setConnectTimeout(int connectTimeout);

    /**
     * 获取读取超时时间（默认HttpStack.DEFAULT_READ_TIMEOUT）
     */
    @SuppressWarnings("unused")
    int getReadTimeout();

    /**
     * 设置读取超时时间（默认HttpStack.DEFAULT_READ_TIMEOUT）
     */
    @SuppressWarnings("unused")
    @NonNull
    HttpStack setReadTimeout(int readTimeout);

    /**
     * 获取User-Agent
     */
    @SuppressWarnings("unused")
    @Nullable
    String getUserAgent();

    /**
     * 设置User-Agent
     */
    @SuppressWarnings("unused")
    @NonNull
    HttpStack setUserAgent(String userAgent);

    /**
     * 获取扩展请求属性
     */
    @SuppressWarnings("unused")
    @Nullable
    Map<String, String> getExtraHeaders();

    /**
     * 设置扩展请求属性
     */
    @SuppressWarnings("unused")
    @NonNull
    HttpStack setExtraHeaders(Map<String, String> extraHeaders);

    /**
     * 获取可存在多个的请求属性
     */
    @SuppressWarnings("unused")
    @Nullable
    Map<String, String> getAddExtraHeaders();

    /**
     * 添加可存在多个的请求属性
     */
    @SuppressWarnings("unused")
    @NonNull
    HttpStack addExtraHeaders(Map<String, String> extraHeaders);

    /**
     * 获取响应
     */
    @NonNull
    ImageHttpResponse getHttpResponse(String uri) throws IOException;

    /**
     * 是否可以重试
     */
    boolean canRetry(Throwable throwable);

    interface ImageHttpResponse {
        /**
         * 获取响应状态码
         *
         * @throws IOException IO
         */
        int getResponseCode() throws IOException;

        /**
         * 获取响应消息
         *
         * @throws IOException IO
         */
        @SuppressWarnings("unused")
        @Nullable
        String getResponseMessage() throws IOException;

        /**
         * 获取内容长度
         */
        long getContentLength();

        /**
         * 内容是否是分块的？
         */
        boolean isContentChunked();

        /**
         * 获取响应头
         */
        @Nullable
        String getResponseHeader(@NonNull String name);

        /**
         * 获取所有的响应头
         */
        @Nullable
        String getResponseHeadersString();

        /**
         * 获取内容输入流
         *
         * @throws IOException IO
         */
        @NonNull
        InputStream getContent() throws IOException;

        /**
         * 释放连接
         */
        void releaseConnection();
    }
}
