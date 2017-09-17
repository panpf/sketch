# 取消请求

### display 显示请求

display 显示请求 [Sketch] 会在合适的时候自动取消，如下：
* [SketchImageView] 重用的时候会自动取消旧的未完成的请求
* [SketchImageView] 在 onDetachedFromWindow 的时候自动取消未完成的请求

因此你不必刻意关注怎么去取消一个显示请求

但有时候你还是想知道如何主动取消显示请求？
1. 在执行 [DisplayHelper].commit() 或 [SketchImageView].display***Image() 方法之后你会得到一个 [DisplayRequest]，通过其 cancel() 方法可以取消显示请求
2. 你还可以通过 [Sketch].cancel(SketchView) 方法来取消显示请求

``取消请求的时候如果正在下载图片，就会立马停止下载，已经下载的数据就算浪费了``

### load 加载请求

在执行 [LoadHelper].commit() 方法之后你会得到一个 [LoadRequest]，通过其 cancel() 方法可以取消显示请求

### download 下载请求

在执行 [DownloadHelper].commit() 方法之后你会得到一个 [DownloadRequest]，通过其 cancel() 方法可以取消显示请求

[Sketch]: ../../sketch/src/main/java/me/xiaopan/sketch/Sketch.java
[SketchImageView]: ../../sketch/src/main/java/me/xiaopan/sketch/SketchImageView.java
[DisplayHelper]:../../sketch/src/main/java/me/xiaopan/sketch/request/DisplayHelper.java
[DisplayRequest]:../../sketch/src/main/java/me/xiaopan/sketch/request/DisplayRequest.java
[LoadHelper]:../../sketch/src/main/java/me/xiaopan/sketch/request/LoadHelper.java
[LoadRequest]:../../sketch/src/main/java/me/xiaopan/sketch/request/LoadRequest.java
[DownloadHelper]:../../sketch/src/main/java/me/xiaopan/sketch/request/DownloadHelper.java
[DownloadRequest]:../../sketch/src/main/java/me/xiaopan/sketch/request/DownloadRequest.java
