Sketch支持暂停下载图片，你只需执行如下代码设置即可：
```java
// 暂停下载图片
Sketch.with(context).getConfiguration().setPauseDownload(true);

// 恢复下载图片
Sketch.with(context).getConfiguration().setPauseDownload(false);
```

结合此功能Sketch还提供了切换到移动网络的时候自动暂停下载图片的功能，如下：
```java
// 开启移动网络下自动暂停下载图片功能
Sketch.with(getBaseContext()).getConfiguration().setMobileNetworkPauseDownload(true);

// 关闭移动网络下自动暂停下载图片功能
Sketch.with(getBaseContext()).getConfiguration().setMobileNetworkPauseDownload(false);
```

Sketch还提供了pauseDownloadImage，通过DisplayOptions可以设置，当暂停下载图片的时候会自动显示pauseDownloadImage

另外SketchImageView还提供了当暂停下载的时候点击直接下载并显示的功能，你只需调用其setClickDisplayOnPauseDownload(boolean)方法开启即可