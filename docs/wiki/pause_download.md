# 移动数据或有流量限制的 WIFI 下暂停下载图片，节省流量

现如今移动流量还是比较贵的，图片又是流量大户，因此在移动数据或有流量限制的 WIFI 下暂停下载图片还是很有必要的

Sketch 支持一键开启全局移动数据或有流量限制的 WIFI 下暂停下载图片功能，暂停后如果磁盘中没有缓存就不下载了，并且可以为暂停下载的图片显示一个专用的提示图片并支持点击强制下载

``暂停下载功能只对 display 和 load 请求有效``

### 使用

你想要自己控制暂停下载，只需执行如下代码即可：

```java
// 全局暂停下载图片
Sketch.with(context).getConfiguration().setPauseDownloadEnabled(true);

// 全局恢复下载图片
Sketch.with(context).getConfiguration().setPauseDownloadEnabled(false);
```

### 移动数据下暂停下载

Sketch 内置了 [MobileNetworkPauseDownloadController] 来提移动数据或有流量限制的 WIFI 下自动暂停下载图片的功能并提供了开关，你只需开启即可，如下：

```java
// 开启移动数据下自动暂停下载图片功能
Sketch.with(getBaseContext()).getConfiguration().setMobileDataPauseDownloadEnabled(true);

// 关闭移动数据下自动暂停下载图片功能
Sketch.with(getBaseContext()).getConfiguration().setMobileDataPauseDownloadEnabled(false);
```

### 暂停下载时显示相应状态图片并点击强制下载

Sketch 还在 [DisplayOptions] 中 提供了 pauseDownloadImage 属性，可以在暂停下载时显示相应的状态图片，以提醒用户

SketchImageView 还提供了暂停下载时候点击强制下载并显示的功能，一句话开启即可

```java
sketchImageView.setClickDisplayOnPauseDownload(true);
```
此功能`只作用于当前图片`，并且`只忽略暂停下载功能一次`

[MobileNetworkPauseDownloadController]: ../../sketch/src/main/java/me/xiaopan/sketch/optionsfilter/MobileNetworkPauseDownloadController.java
[sketch_image_view]: sketch_image_view.md
[DisplayOptions]: ../../sketch/src/main/java/me/xiaopan/sketch/request/DisplayOptions.java
[SketchImageView]: ../../sketch/src/main/java/me/xiaopan/sketch/SketchImageView.java
