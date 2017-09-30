# 显示视频缩略图

[Sketch] 本身并没有提供显示视频缩略图的功能，但是你可以通过自定义 [UriModel] 轻松实现显示视频缩略图的功能，在源码中就有一个叫 [sample-video-thumbnail] 示例 module 展示了如何显示视频缩略图


### 为什么不内置显示视频缩略图的功能

首先读取视频缩略图的库通常都比较大，像 [FFmpegMediaMetadataRetriever] 每一种平台的 so 文件都有 6MB，如果集成到 [Sketch] 中将大大增加 AAR 包的体积

其次有视频播放或编辑需求的 APP 都会用特定或自己改造的视频库，如果单为了显示视频缩略图而引入 [FFmpegMediaMetadataRetriever] 会得不偿失


### 集成 [sample-video-thumbnail] 到你的 APP 中

1.在你的 app 的 build.gradle 中加入 [FFmpegMediaMetadataRetriever] 的依赖，如下：

```groovy
compile 'com.github.wseemann:FFmpegMediaMetadataRetriever:1.0.14'
```

2.拷贝 [VideoThumbnailUriModel.java] 到你的 app 中

3.在 Application 中将 [VideoThumbnailUriModel.java] 加入到 [UriModelRegistry] 中，如下：

```java
Configuration configuration = Sketch.with(context).getConfiguration();
configuration.getUriModelRegistry().add(new VideoThumbnailUriModel());
```

4.显示视频缩略图

```java
SketchImageView sketchImageView = ...;

String videoFilePath = "/sdcard/video.mp4";

// 将视频文件路径包装一下，这样 VideoThumbnailUriModel 才能够识别出这是一个需要提取缩略图的视频文件
String videoFileUri = VideoThumbnailUriModel.makeUri(videoFilePath);

sketchImageView.displayImage(videoFileUri);
```

5.如果你想要使用其它的视频处理库，只需将 [VideoThumbnailUriModel.java] 的 getContent(Context, String) 方法的实现替换掉即可

[Sketch]: ../../sketch/src/main/java/me/xiaopan/sketch/Sketch.java
[UriModel]: ../../sketch/src/main/java/me/xiaopan/sketch/uri/UriModel.java
[sample-video-thumbnail]: ../../sample-video-thumbnail/
[FFmpegMediaMetadataRetriever]: https://github.com/wseemann/FFmpegMediaMetadataRetriever
[VideoThumbnailUriModel.java]: ../../sample-video-thumbnail/src/main/java/me/xiaopan/ssvt/VideoThumbnailUriModel.java
[UriModelRegistry]: ../../sketch/src/main/java/me/xiaopan/sketch/uri/UriModelRegistry.java
