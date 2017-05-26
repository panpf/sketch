HttpStack用来发起网络请求，然后返回响应，默认的实现是HurlStack

#### 相关配置：
>* setMaxRetryCount(int maxRetryCount)：设置连接超时后重试次数，默认0（不重试）
>* setConnectTimeout(int connectTimeout)：设置连接超时时间，默认7秒
>* setReadTimeout(int readTimeout)：设置读取超时时间，默认7秒
>* setUserAgent(String userAgent)：设置User-Agent，为空的话不设置
>* setExtraHeaders(Map<String, String> extraHeaders)：设置一些通用的请求头属性
>* addExtraHeaders(Map<String, String> extraHeaders)：添加一些通用的请求头属性


#### 自定义:

你可以实现HttpStack接口实现自定义HttpStack

然后通过Configuration设置即可，如下
```java
Sketch.with(context).getConfiguration().setHttpStack(new MyHttpStack());
```
