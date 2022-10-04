# Video Frame

Sketch 支持解码视频帧，由以下 Decoder 提供支持：

* [VideoFrameBitmapDecoder]：使用 Android 内置的 MediaMetadataRetriever 类解码视频帧
    * 需要先导入 `sketch-video` 模块
    * 建议 Android 8.1 及以上版本使用，因为 8.0 及以下版本不支持读取帧的缩略图，在解码 4k 等较大的视频时将消耗大量的内存
* [FFmpegVideoFrameBitmapDecoder]：使用 [wseemann]
  /[FFmpegMediaMetadataRetriever-project] 库的 [FFmpegMediaMetadataRetriever] 类解码视频文件的帧
    * 需要先导入 `sketch-video-ffmpeg` 模块
    * 库体积大概 23MB

### 注册

根据情况选择合适的 Decoder，然后在初始化 Sketch 时通过 components() 方法注册，这样所有的 [ImageRequest] 都可以使用，如下：

```kotlin
class MyApplication : Application(), SketchFactory {

    override fun createSketch(): Sketch {
        return Sketch.Builder(this).apply {
          components {
            addBitmapDecoder(FFmpegVideoFrameBitmapDecoder.Factory())
          }
        }.build()
    }
}
```

或者在显示图片时只给当前 [ImageRequest] 注册，这样就只有当前 [ImageRequest] 可以使用，如下：

```kotlin
imageView.displayImage("file:///sdcard/sample.mp4") {
    components {
        addBitmapDecoder(FFmpegVideoFrameBitmapDecoder.Factory())
    }
}
```

### 配置

[DisplayRequest] 和 [LoadRequest] 支持一些视频帧相关的配置，如下：

```kotlin
imageView.displayImage("file:///sdcard/sample.mp4") {
    // 提取 1000000 微秒处的帧
    videoFrameMicros(1000000)

    // 或 提取 10000 毫秒处的帧
    videoFrameMillis(10000)

    // 或 获取提取中间的帧
    videoFramePercentDuration(0.5f)

    // 设置指定时间处无法提取帧时的处理策略
    videoFrameOption(MediaMetadataRetriever.OPTION_CLOSEST)
}
```

[wseemann]: https://github.com/wseemann

[FFmpegMediaMetadataRetriever-project]: https://github.com/wseemann/FFmpegMediaMetadataRetriever

[FFmpegMediaMetadataRetriever]: https://github.com/wseemann/FFmpegMediaMetadataRetriever/blob/master/core/src/main/java/wseemann/media/FFmpegMediaMetadataRetriever.java

[VideoFrameBitmapDecoder]: ../../sketch-video/src/main/java/com/github/panpf/sketch/decode/VideoFrameBitmapDecoder.kt

[FFmpegVideoFrameBitmapDecoder]: ../../sketch-video-ffmpeg/src/main/java/com/github/panpf/sketch/decode/FFmpegVideoFrameBitmapDecoder.kt

[DisplayRequest]: ../../sketch/src/main/java/com/github/panpf/sketch/request/DisplayRequest.kt

[LoadRequest]: ../../sketch/src/main/java/com/github/panpf/sketch/request/LoadRequest.kt

[ImageRequest]: ../../sketch/src/main/java/com/github/panpf/sketch/request/ImageRequest.kt
