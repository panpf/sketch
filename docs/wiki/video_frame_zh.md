# Video Frame

翻译：[English](video_frame.md)

> [!IMPORTANT]
> 1. 必须导入 `sketch-video` 或 `sketch-video-ffmpeg` 模块
> 2. 仅支持 Android 平台

Sketch 支持解码视频帧，由以下 Decoder 提供支持：

* [VideoFrameDecoder]：使用 Android 内置的 MediaMetadataRetriever 类解码视频帧
    * 需要先导入 `sketch-video` 模块
    * 建议 Android 8.1 及以上版本使用，因为 8.0 及以下版本不支持读取帧的缩略图，在解码 4k
      等较大的视频时将消耗大量的内存
* [FFmpegVideoFrameDecoder]：使用 [wseemann/FFmpegMediaMetadataRetriever-project][FFmpegMediaMetadataRetriever-project] 库的 [FFmpegMediaMetadataRetriever] 类解码视频帧
    * 需要先导入 `sketch-video-ffmpeg` 模块
    * 库体积大概 23MB

### 注册

根据情况选择合适的 Decoder，然后注册它，如下：

```kotlin
// 在自定义 Sketch 时为所有 ImageRequest 注册
Sketch.Builder(this).apply {
    components {
        supportVideoFrame()
        //or
        supportFFmpegVideoFrame()
    }
}.build()

// 加载图片时为单个 ImageRequest 注册
ImageRequest(context, "file:///sdcard/sample.mp4") {
    components {
      supportVideoFrame()
      //or
      supportFFmpegVideoFrame()
    }
}
```

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

    // 设置指定时间处无法提取帧时的处理策略
    videoFrameOption(MediaMetadataRetriever.OPTION_CLOSEST)
}
```

[FFmpegMediaMetadataRetriever-project]: https://github.com/wseemann/FFmpegMediaMetadataRetriever

[FFmpegMediaMetadataRetriever]: https://github.com/wseemann/FFmpegMediaMetadataRetriever/blob/master/core/src/main/kotlin/wseemann/media/FFmpegMediaMetadataRetriever.java

[VideoFrameDecoder]: ../../sketch-video/src/main/kotlin/com/github/panpf/sketch/decode/VideoFrameDecoder.kt

[FFmpegVideoFrameDecoder]: ../../sketch-video-ffmpeg/src/main/kotlin/com/github/panpf/sketch/decode/FFmpegVideoFrameDecoder.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

[ImageOptions]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageOptions.kt