Sketch支持全局暂停下载新图片，暂停后如果磁盘中没有缓存就不下载了

``暂停下载功能只对display和load请求有效``

你只需执行如下代码设置即可：
```java
// 全局暂停下载图片
Sketch.with(context).getConfiguration().setGlobalPauseDownload(true);

// 全局恢复下载图片
Sketch.with(context).getConfiguration().setGlobalPauseDownload(false);
```

#### 移动网络下暂停下载
结合此功能Sketch还提供了切换到移动网络的时候自动暂停下载图片的功能，如下：
```java
// 开启移动网络下自动暂停下载图片功能
Sketch.with(getBaseContext()).getConfiguration().setMobileNetworkGlobalPauseDownload(true);

// 关闭移动网络下自动暂停下载图片功能
Sketch.with(getBaseContext()).getConfiguration().setMobileNetworkGlobalPauseDownload(false);
```

Sketch还提供了pauseDownloadImage，通过DisplayOptions和DisplayHelper可以设置，当暂停下载的时候会自动显示pauseDownloadImage

另外SketchImageView还提供了当暂停下载的时候点击强制下载并显示的功能，你只需调用其setClickRetryOnPauseDownloadEnabled(true)开启即可
