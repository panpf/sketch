# Video Frame

Translations: [简体中文](video_frame_zh.md)

> [!IMPORTANT]
> 1. Required import `sketch-extensions-view` or `sketch-extensions-compose` module
> 2. Only supports Android platform

Sketch supports decoding video frames, powered by the following Decoder:

* [VideoFrameDecoder]：Decode video frames using Android's built-in MediaMetadataRetriever class
    * You need to import the `sketch-video` module first
    * It is recommended to use Android 8.1 and above, because versions 8.0 and below do not support reading frame thumbnails, which will consume a lot of memory when decoding larger videos such as 4k.
* [FFmpegVideoFrameDecoder]：Decode video frames using the [FFmpegMediaMetadataRetriever] class of the [wseemann/FFmpegMediaMetadataRetriever-project][FFmpegMediaMetadataRetriever-project] library
    * You need to import the `sketch-video-ffmpeg` module first
    * Library size is approximately 23 MB

### Registered

Select the appropriate Decoder according to the situation, and then register it as follows:

```kotlin
// Register for all ImageRequests when customizing Sketch
Sketch.Builder(context).apply {
    components {
        addDecoder(VideoFrameDecoder.Factory())
        //or
        addDecoder(FFmpegVideoFrameDecoder.Factory())
    }
}.build()

// Register for a single ImageRequest when loading an image
ImageRequest(context, "file:///sdcard/sample.mp4") {
    components {
      addDecoder(VideoFrameDecoder.Factory())
      //or
      addDecoder(FFmpegVideoFrameDecoder.Factory())
    }
}
```

### Configuration

[ImageRequest] and [ImageOptions] support some video frame-related configurations, as follows:

```kotlin
ImageRequest(context, "file:///sdcard/sample.mp4") {
    // Extract the frame at 1000000 microseconds
    videoFrameMicros(1000000)

    // or extract the frame at 10000 ms
    videoFrameMillis(10000)

    // or get the frames in between
    videoFramePercentDuration(0.5f)

    // Set the processing strategy when frames cannot be extracted at the specified time
    videoFrameOption(MediaMetadataRetriever.OPTION_CLOSEST)
}
```

[FFmpegMediaMetadataRetriever-project]: https://github.com/wseemann/FFmpegMediaMetadataRetriever

[FFmpegMediaMetadataRetriever]: https://github.com/wseemann/FFmpegMediaMetadataRetriever/blob/master/core/src/main/kotlin/wseemann/media/FFmpegMediaMetadataRetriever.java

[VideoFrameDecoder]: ../../sketch-video/src/main/kotlin/com/github/panpf/sketch/decode/VideoFrameDecoder.kt

[FFmpegVideoFrameDecoder]: ../../sketch-video-ffmpeg/src/main/kotlin/com/github/panpf/sketch/decode/FFmpegVideoFrameDecoder.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.common.kt

[ImageOptions]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageOptions.common.kt