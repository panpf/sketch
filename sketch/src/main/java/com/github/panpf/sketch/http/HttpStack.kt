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
package com.github.panpf.sketch.http

import java.io.IOException
import java.io.InputStream

/**
 * 负责发送 HTTP 请求，并返回响应
 */
interface HttpStack {

    companion object {
        const val DEFAULT_READ_TIMEOUT = 7 * 1000 // 默认读取超时时间
        const val DEFAULT_CONNECT_TIMEOUT = 7 * 1000 // 默认连接超时时间
        const val DEFAULT_MAX_RETRY_COUNT = 0 // 默认最大重试次数
    }

    /**
     * 获取最大重试次数，默认值是 [HttpStack.DEFAULT_MAX_RETRY_COUNT]
     */
    val maxRetryCount: Int

    /**
     * 设置最大重试次数
     *
     * @param maxRetryCount 最大重试次数
     */
    fun setMaxRetryCount(maxRetryCount: Int): HttpStack

    /**
     * 获取连接超时时间，单位毫秒，默认值是 [HttpStack.DEFAULT_CONNECT_TIMEOUT]
     */
    val connectTimeout: Int

    /**
     * 设置连接超时时间
     *
     * @param connectTimeout 连接超时时间，单位毫秒
     */
    fun setConnectTimeout(connectTimeout: Int): HttpStack

    /**
     * 获取读取超时时间，单位毫秒，默认值是 [HttpStack.DEFAULT_READ_TIMEOUT]
     */
    val readTimeout: Int

    /**
     * 设置读取超时时间
     *
     * @param readTimeout 读取超时时间，单位毫秒
     */
    fun setReadTimeout(readTimeout: Int): HttpStack

    /**
     * 获取自定义请求头中的 User-Agent 属性
     */
    val userAgent: String?

    /**
     * 设置请求头中的 User-Agent 属性
     *
     * @param userAgent 请求头中的 User-Agent 属性
     */
    fun setUserAgent(userAgent: String?): HttpStack

    /**
     * 获取扩展请求属性
     */
    val extraHeaders: Map<String?, String?>?

    /**
     * 设置扩展请求属性集
     *
     * @param extraHeaders 扩展请求属性集
     */
    fun setExtraHeaders(extraHeaders: Map<String?, String?>?): HttpStack

    /**
     * 获取可存在多个的请求属性
     */
    val addExtraHeaders: Map<String?, String?>?

    /**
     * 添加可存在多个的请求属性
     *
     * @param extraHeaders 扩展请求属性集
     */
    fun addExtraHeaders(extraHeaders: Map<String?, String?>?): HttpStack

    /**
     * 发送请求并获取响应
     *
     * @param uri http uri
     * @return [Response]
     */
    @Throws(IOException::class)
    fun getResponse(uri: String?): Response

    /**
     * 是否可以重试
     */
    fun canRetry(throwable: Throwable): Boolean

    /**
     * 统一响应接口
     */
    interface Response {
        /**
         * 获取响应状态码
         *
         * @throws IOException IO
         */
        @get:Throws(IOException::class)
        val code: Int

        /**
         * 获取响应消息
         *
         * @throws IOException IO
         */
        @get:Throws(IOException::class)
        val message: String?

        /**
         * 获取内容长度
         */
        val contentLength: Long

        /**
         * 获取内容类型
         */
        val contentType: String?

        /**
         * 内容是否是分块的？
         */
        val isContentChunked: Boolean

        /**
         * 获取内容编码
         */
        val contentEncoding: String?

        /**
         * 获取响应头
         */
        fun getHeaderField(name: String): String?

        /**
         * 获取响应头并转换成 int
         */
        fun getHeaderFieldInt(name: String, defaultValue: Int): Int

        /**
         * 获取响应头并转换成 long
         */
        fun getHeaderFieldLong(name: String, defaultValue: Long): Long

        /**
         * 获取所有的响应头
         */
        val headersString: String?

        /**
         * 获取内容输入流
         *
         * @throws IOException IO
         */
        @get:Throws(IOException::class)
        val content: InputStream

        /**
         * 释放连接
         */
        fun releaseConnection()
    }
}