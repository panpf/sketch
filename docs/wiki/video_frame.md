# Video Frame

Translations: [简体中文](video_frame_zh.md)

Sketch supports decoding video frames, powered by the following Decoder:

* [VideoFrameDecoder]: Use Android's built-in MediaMetadataRetriever class to decode video
  frames
    * You need to import the `sketch-video` module first
    * It is recommended to use Android 8.1 and above, because versions 8.0 and below do not support
      reading frame thumbnails, which will consume a lot of memory when decoding larger videos such
      as 4k.
* [FFmpegVideoFrameDecoder]：Use [wseemann]
  /[FFmpegMediaMetadataRetriever-project] Library's [FFmpegMediaMetadataRetriever] class decodes
  frames of video files
    * You need to import the `sketch-video-ffmpeg` module first
    * Library size is approximately 23MB

### Registered

Select the appropriate Decoder according to the situation, and then register it as follows:

```kotlin
/* Register for all ImageRequests */
class MyApplication : Application(), SingletonSketch.Factory {

    override fun createSketch(): Sketch {
        return Sketch.Builder(this).apply {
            components {
                addDecoder(FFmpegVideoFrameDecoder.Factory())
            }
        }.build()
    }
}

/* Register for a single ImageRequest */
imageView.displayImage("file:///sdcard/sample.mp4") {
    components {
        addDecoder(FFmpegVideoFrameDecoder.Factory())
    }
}
```

### Configure

[ImageRequest] and [ImageOptions] support some video frame-related configurations, as follows:

```kotlin
imageView.displayImage("file:///sdcard/sample.mp4") {
    // Extract the frame at 1000000 microseconds
    videoFrameMicros(1000000)

    // or extract the frame at 10000 ms
    videoFrameMillis(10000)

    // or get the frame in the middle of the extraction
    videoFramePercentDuration(0.5f)

    // Set the processing strategy when frames cannot be extracted at the specified time
    videoFrameOption(MediaMetadataRetriever.OPTION_CLOSEST)
}
```

[wseemann]: https://github.com/wseemann

[FFmpegMediaMetadataRetriever-project]: https://github.com/wseemann/FFmpegMediaMetadataRetriever

[FFmpegMediaMetadataRetriever]: https://github.com/wseemann/FFmpegMediaMetadataRetriever/blob/master/core/src/main/kotlin/wseemann/media/FFmpegMediaMetadataRetriever.java

[VideoFrameDecoder]: ../../sketch-video/src/main/kotlin/com/github/panpf/sketch/decode/VideoFrameDecoder.kt

[FFmpegVideoFrameDecoder]: ../../sketch-video-ffmpeg/src/main/kotlin/com/github/panpf/sketch/decode/FFmpegVideoFrameDecoder.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

[ImageOptions]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageOptions.kt