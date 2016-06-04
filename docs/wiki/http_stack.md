#### 简介

HttpStack用来发起网络请求，并且返回响应

默认的实现是API9及以上使用HurlStack以下使用HttpClientStack

#### 相关配置：
>* setMaxRetryCount(int maxRetryCount)：设置连接超时后重试次数，默认1
>* setConnectTimeout(int connectTimeout)：设置连接超时时间，默认10秒
>* setReadTimeout(int readTimeout)：设置读取超时时间，默认20秒
>* setUserAgent(String userAgent)：设置User-Agent，为空的话不设置
>* setExtraHeaders(Map<String, String> extraHeaders)：设置一些通用的请求属性
>* addExtraHeaders(Map<String, String> extraHeaders)：添加一些通用的请求属性