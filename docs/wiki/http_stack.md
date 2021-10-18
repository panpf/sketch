# 发送 HTTP 请求

[HttpStack] 用来发起网络请求，然后返回响应，默认的实现是 [HurlStack]

#### 相关配置：
* setMaxRetryCount(int maxRetryCount)：设置连接超时后重试次数，默认0（不重试）
* setConnectTimeout(int connectTimeout)：设置连接超时时间，默认 7 秒
* setReadTimeout(int readTimeout)：设置读取超时时间，默认 7 秒
* setUserAgent(String userAgent)：设置 User-Agent，为空的话不设置
* setExtraHeaders(Map<String, String> extraHeaders)：设置一些通用的请求头属性，不可重复
* addExtraHeaders(Map<String, String> extraHeaders)：添加一些通用的请求头属性，可重复


#### 自定义：

首先实现 [HttpStack] 接口定义自己的 [HttpStack]

然后通过 [Configuration] 使用即可，如下：

```java
Sketch.with(context).getConfiguration().setHttpStack(new MyHttpStack());
```

[HttpStack]: ../../sketch/src/main/java/com/github/panpf/sketch/http/HttpStack.java
[HurlStack]: ../../sketch/src/main/java/com/github/panpf/sketch/http/HurlStack.java
[Configuration]: ../../sketch/src/main/java/com/github/panpf/sketch/Configuration.java