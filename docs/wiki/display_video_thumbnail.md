## 显示视频缩略图

Sketch本身并没有显示视频缩略图的能力，但是你可以通过自定义一个 Preprocessor 轻松实现显示视频缩略图的功能，在Demo里就有一个现成的 [VideoThumbnailPreprocessor.java](../../sample/src/main/java/me/xiaopan/sketchsample/util/VideoThumbnailPreprocessor.java) 可供使用

1.在你的 app 的 build.gradle 中加入 FFmpegMediaMetadataRetriever 的依赖，如下：
```groovy
compile 'com.github.wseemann:FFmpegMediaMetadataRetriever:1.0.14'
```

2.拷贝 [VideoThumbnailPreprocessor.java](../../sample/src/main/java/me/xiaopan/sketchsample/util/VideoThumbnailPreprocessor.java) 到你的 app 中

3.将 VideoThumbnailPreprocessor 加入到 ImagePreprocessor 中，如下：
```java
Configuration configuration = Sketch.with(context).getConfiguration();

configuration.getImagePreprocessor().addPreprocessor(new VideoThumbnailPreprocessor());
```

4.显示视频缩略图
```java
SketchImageView sketchImageView = ...;

String videoFilePath = "/sdcard/video.mp4";

// 将普通的视频文件路径包装一下，这样 VideoThumbnailPreprocessor 才能够识别出这是一个需要提取缩略图的视频文件
String videoFileUri = VideoThumbnailPreprocessor.createUri(videoFilePath);

sketchImageView.displayImage(videoFileUri);
```
