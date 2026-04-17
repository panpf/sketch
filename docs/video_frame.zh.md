# 视频帧

翻译：[English](video_frame.md)

Sketch 提供了 `sketch-video-*` 系列模块以支持解码视频帧

| Module              | Decoder                                                                          | Android   | iOS | Desktop | Web |
|:--------------------|:---------------------------------------------------------------------------------|:----------|:----|:--------|:----|
| sketch-video        | [VideoFrameDecoder]<br>[PhotosAssetVideoFrameDecoder]<br>[FileVideoFrameDecoder] | ✅(API 27) | ✅   | ❌       | ❌   |
| sketch-video-ffmpeg | [FFmpegVideoFrameDecoder]                                                        | ✅         | ❌   | ❌       | ❌   |

* [VideoFrameDecoder]：
    * 使用 Android 内置的 MediaMetadataRetriever 类解码视频帧
    * 建议 Android 8.1 及以上版本使用，因为 8.0 及以下版本不支持读取帧的缩略图，在解码 4k
      等较大的视频时将消耗大量的内存
  * 仅支持 Android 平台
* [FFmpegVideoFrameDecoder]：
    * 使用 [wseemann/FFmpegMediaMetadataRetriever-project][FFmpegMediaMetadataRetriever-project]
      库的 [FFmpegMediaMetadataRetriever] 类解码视频帧
  * 仅支持 Android 平台
    * 库体积大概 23MB
* [PhotosAssetVideoFrameDecoder]：
  * 使用 AVAssetImageGenerator 类解码视频帧
  * 仅支持 file:///photos_asset/ 开头的 uri
  * 仅支持 ios 平台
* [FileVideoFrameDecoder]：
  * 使用 AVAssetImageGenerator 类解码视频帧
  * 仅支持本地 path uri
  * 仅支持 ios 平台

### 安装组件

`${LAST_VERSION}`: [![Download][version_icon]][version_link] (不包含 'v')

```kotlin
implementation("io.github.panpf.sketch4:sketch-video:${LAST_VERSION}")
// or
implementation("io.github.panpf.sketch4:sketch-video-ffmpeg:${LAST_VERSION}")
```

> [!IMPORTANT]
> 上述组件都支持自动注册，你只需要导入即可，无需额外配置，如果你需要手动注册，
> 请阅读文档：[《注册组件》](register_component.zh.md)

### 配置

[ImageRequest] 和 [ImageOptions] 支持一些视频帧相关的配置，如下：

```kotlin
ImageRequest(context, "file:///sdcard/sample.mp4") {
    // 提取 1000000 微秒处的帧
    videoFrameMicros(1000000)

    // 或提取 10000 毫秒处的帧
    videoFrameMillis(10000)

    // 或获取提取中间的帧
    videoFramePercentDuration(0.5f)

    // 设置指定时间处无法提取帧时的处理策略。仅限 Android 平台
    videoFrameOption(MediaMetadataRetriever.OPTION_CLOSEST)
}
```

[version_icon]: https://img.shields.io/maven-central/v/io.github.panpf.sketch4/sketch-singleton

[version_link]: https://repo1.maven.org/maven2/io/github/panpf/sketch4/

[FFmpegMediaMetadataRetriever-project]: https://github.com/wseemann/FFmpegMediaMetadataRetriever

[FFmpegMediaMetadataRetriever]: https://github.com/wseemann/FFmpegMediaMetadataRetriever/blob/master/core/src/main/kotlin/wseemann/media/FFmpegMediaMetadataRetriever.java

[VideoFrameDecoder]: ../sketch-video/src/androidMain/kotlin/com/github/panpf/sketch/decode/VideoFrameDecoder.kt

[FFmpegVideoFrameDecoder]: ../sketch-video-ffmpeg/src/main/kotlin/com/github/panpf/sketch/decode/FFmpegVideoFrameDecoder.kt

[ImageRequest]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.common.kt

[ImageOptions]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageOptions.common.kt

[PhotosAssetVideoFrameDecoder]: ../sketch-video/src/iosMain/kotlin/com/github/panpf/sketch/decode/PhotosAssetVideoFrameDecoder.kt

[FileVideoFrameDecoder]: ../sketch-video/src/iosMain/kotlin/com/github/panpf/sketch/decode/FileVideoFrameDecoder.kt