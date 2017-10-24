# 日志

Sketch 运行时的全部日志通过 [SLog] 来打印，[SLog] 默认使用 [android.util.Log] 来打印日志，

### 接管日志打印工作
如果你想在日志打印到控制台的同时将日志保存到本地文件，那么你可以通过 [SLog.Proxy] 来接管日志打印工作实现你的个性化需求

1.首先你需要实现 [SLog.Proxy] 接口，可参考 sample app 中的 [SampleLogProxy]
* [SLog.Proxy] 接口中定义的大部分方法跟 [android.util.Log] 是一样的
* 一个特殊的方法 onReplaced() 会在旧的 [SLog.Proxy] 被替换时调用

2.然后通过 Sketch.setLogProxy(SLogProxy) 方法应用即可

### 日志级别

在 [SLog] 类中定义了以下几个日志级别（从小到大）：

* LEVEL_VERBOSE：最详细的日志，参考 Android Logcat 中的日志分级
* LEVEL_DEBUG：开发阶段调试用的日志，参考 Android Logcat 中的日志分级
* LEVEL_INFO：常规信息日志，参考 Android Logcat 中的日志分级
* LEVEL_WARNING：警告日志，参考 Android Logcat 中的日志分级
* LEVEL_ERROR：错误日志，参考 Android Logcat 中的日志分级
* LEVEL_NONE：独有的，表示关闭所有级别的日志

注意：
* 默认的日志级别是 LEVEL_INFO
* 日志级别之间是单选互斥且区分大小关系的，假如当前日志级别是 LEVEL_INFO，那么 LEVEL_INFO 及其以上的都可以输出，以下的则不能

```java
// 切换到 DEBUG 日志级别
SLog.setLoggable(SLog.LEVEL_DEBUG);

// 判断当前是否可以输出 DEBUG 级别的日志
SLog.isLoggable(SLog.LEVEL_DEBUG) == true

// 获取当前日志级别
SLog.getLevel();
```

### 日志分类

在 [SLog] 类中定义了以下几个日志分类：

* TYPE_FLOW: 执行流程相关日志
* TYPE_CACHE: 内存缓存、bitmap pool、磁盘缓存相关日志
* TYPE_TIME: commit()和解码耗时相关日志
* TYPE_ZOOM: 手势缩放相关日志
* TYPE_ZOOM_BLOCK_DISPLAY: 分块显示超大图相关日志

注意：
* 日志分类默认全部关闭
* 日志分类之间是多选共存关系，你可以同时开启所有类型日志
* 所有 WARNING 和 ERROR 级别日志不受类型限制

```java
// 开启 CACHE 类型日志
SLog.setLoggable(SLog.TYPE_CACHE);

// 判断当前是否可以输出 CACHE 类型的日志
SLog.isLoggable(SLog.TYPE_CACHE) == true

// 关闭 CACHE 类型日志（此方法只能删除日志分类）
SLog.removeLoggable(SLog.TYPE_CACHE);
```

[SLog]: ../../sketch/src/main/java/me/xiaopan/sketch/SLog.java
[SLog.Proxy]: ../../sketch/src/main/java/me/xiaopan/sketch/SLog.java
[SampleLogProxy]: ../../sample/src/main/java/me/xiaopan/sketchsample/SampleLogProxy.kt
[android.util.Log]: https://developer.android.com/reference/android/util/Log.html
