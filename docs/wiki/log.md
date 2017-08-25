Sketch 运行时的全部日志通过 [SLog] 来打印，[SLog] 默认使用 [android.util.Log] 来打印日志，

### 接管日志打印工作
如果你想在日志打印到控制台的同时将日志保存到本地文件，那么你可以通过 [SLog.Proxy] 来接管日志打印工作实现你的个性化需求

1.首先你需要实现 [SLog.Proxy] 接口，可参考 sample app 中的 [SampleLogProxy]
* [SLog.Proxy] 接口中定义的大部分方法跟 [android.util.Log] 是一样的
* 一个特殊的方法 onReplaced() 会在旧的 [SLog.Proxy] 被替换时调用

2.然后通过 Sketch.setLogProxy(SLogProxy) 方法应用即可

### 日志分类

Sketch 的日志大致分为以下几类：
* REQUEST: 请求流程的日志
* CACHE: 内存缓存、bitmap pool、磁盘缓存的日志
* TIME: commit() 方法执行时间和解码耗时日志
* ZOOM: 手势缩放日志
* BASE: 其它日志

这些日志类型都定义在 [SLogType] 中

### 日志开关

所有的日志类型默认都是关闭的，但都能单独控制打开或关闭，例如：
```
// 打开请求流程日志
SLogType.REQUEST.setEnabled(true);

// 关闭缓存日志
SLogType.CACHE.setEnabled(false);
```


[SLog]: ../../sketch/src/main/java/me/xiaopan/sketch/SLog.java
[SLogType]: ../../sketch/src/main/java/me/xiaopan/sketch/SLogType.java
[SLog.Proxy]: ../../sketch/src/main/java/me/xiaopan/sketch/SLog.java
[SampleLogProxy]: ../../sample/src/main/java/me/xiaopan/sketchsample/SampleLogProxy.java
[android.util.Log]: https://developer.android.com/reference/android/util/Log.html
