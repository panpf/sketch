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

package me.xiaopan.sketch.download;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import me.xiaopan.sketch.Identifier;

/**
 * 下载器
 */
public interface ImageDownloader extends Identifier {
    int DEFAULT_READ_TIMEOUT = 20 * 1000;   // 默认读取超时时间
    int DEFAULT_CONNECT_TIMEOUT = 15 * 1000;    // 默认连接超时时间
    int DEFAULT_MAX_RETRY_COUNT = 1;    // 默认最大重试次数

    /**
     * 获取最大重试次数
     */
    int getMaxRetryCount();

    /**
     * 设置最大重试次数（默认1）
     */
    @SuppressWarnings("unused")
    ImageDownloader setMaxRetryCount(int maxRetryCount);

    /**
     * 获取连接超时时间（默认15s）
     */
    @SuppressWarnings("unused")
    int getConnectTimeout();

    /**
     * 设置连接超时时间（默认15s）
     */
    @SuppressWarnings("unused")
    ImageDownloader setConnectTimeout(int connectTimeout);

    /**
     * 获取读取超时时间（默认20s）
     */
    @SuppressWarnings("unused")
    int getReadTimeout();

    /**
     * 设置读取超时时间（默认20s）
     */
    @SuppressWarnings("unused")
    ImageDownloader setReadTimeout(int readTimeout);

    /**
     * 获取User-Agent
     */
    @SuppressWarnings("unused")
    String getUserAgent();

    /**
     * 设置User-Agent
     */
    @SuppressWarnings("unused")
    ImageDownloader setUserAgent(String userAgent);

    /**
     * 获取扩展请求属性
     */
    @SuppressWarnings("unused")
    Map<String, String> getExtraHeaders();

    /**
     * 设置扩展请求属性
     */
    @SuppressWarnings("unused")
    ImageDownloader setExtraHeaders(Map<String, String> extraHeaders);

    /**
     * 获取可存在多个的请求属性
     */
    @SuppressWarnings("unused")
    Map<String, String> getAddExtraHeaders();

    /**
     * 添加可存在多个的请求属性
     */
    @SuppressWarnings("unused")
    ImageDownloader addExtraHeaders(Map<String, String> extraHeaders);

    /**
     * 获取响应
     */
    ImageHttpResponse getHttpResponse(String uri) throws IOException;

    /**
     * 是否可以重试
     */
    boolean canRetry(Throwable throwable);

    interface ImageHttpResponse {
        int getResponseCode() throws IOException;

        String getResponseMessage() throws IOException;

        long getContentLength();

        String getResponseHeadersString();

        InputStream getContent() throws IOException;

        void releaseConnection();
    }
}
