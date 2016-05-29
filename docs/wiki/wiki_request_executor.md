RequestExecutor是用来执行请求的，默认的实现是DefaultRequestExecutor，DefaultRequestExecutor包含三个线程池
>* 网络任务线程池：主要用来执行比较耗时的下载任务，核心线程数``5``个，最大线程数``5``，队列长度为200；
>* 本地任务线程池：用来执行本地任务，例如assets、drawable、缓存图片等。核心线程数``1``个，最大线程数也是``1``个，这样一来本地任务可以一个一个加载，队列长度为200；
>* 任务调度线程池：用来分发请求，其核心作用在于判断请求该放到网络任务线程池执行还是该放到本地任务线程池执行。核心线程数``1``个，最大线程数也是``1``个，队列长度为200。

####相关配置
你可以通过DefaultRequestExecutor.Builder来创建DefaultRequestExecutor并通过其相关方法配置DefaultRequestExecutor，如下：
```java
Builder builder = new DefaultRequestExecutor.Builder();
// 网络任务线程池核心和最大线程数为10个
builder.netTaskExecutor(10, null);

Sketct.with(context).getConfiguration().setRequestExecutor(builder.build());
```

不建议自定义RequestExecutor，因为这部分事关整个框架的设计，稍有不慎可能就会出问题。