# 移动网络下暂停下载图片，节省流量

Sketch 支持全局暂停下载新图片，暂停后如果磁盘中没有缓存就不下载了

``暂停下载功能只对 display 和 load 请求有效``

你只需执行如下代码设置即可：

```java
// 全局暂停下载图片
Sketch.with(context).getConfiguration().setGlobalPauseDownload(true);

// 全局恢复下载图片
Sketch.with(context).getConfiguration().setGlobalPauseDownload(false);
```

#### 移动网络下暂停下载

结合此功能 Sketch 还提供了切换到移动网络的时候自动暂停下载图片的功能，如下：

```java
// 开启移动网络下自动暂停下载图片功能
Sketch.with(getBaseContext()).getConfiguration().setGlobalMobileNetworkPauseDownload(true);

// 关闭移动网络下自动暂停下载图片功能
Sketch.with(getBaseContext()).getConfiguration().setGlobalMobileNetworkPauseDownload(false);
```

Sketch 还提供了 pauseDownloadImage，通过 DisplayOptions 和 DisplayHelper 可以设置，当暂停下载的时候会自动显示 pauseDownloadImage

另外 SketchImageView 还提供了当暂停下载的时候点击强制下载并显示的功能，你只需调用其 setClickRetryOnPauseDownloadEnabled(true) 开启即可
