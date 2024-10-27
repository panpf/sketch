# Video Frame

Translations: [简体中文](video_frame_zh.md)

Sketch provides the `sketch-video-*` series of modules to support decoding video frames

| Module              | DecoderProvider                   | Decoder                   | Android   | iOS | Desktop | Web |
|:--------------------|:----------------------------------|:--------------------------|:----------|:----|:--------|:----|
| sketch-video        | [VideoFrameDecoderProvider]       | [VideoFrameDecoder]       | ✅(API 27) | ❌   | ❌       | ❌   |
| sketch-video-ffmpeg | [FFmpegVideoFrameDecoderProvider] | [FFmpegVideoFrameDecoder] | ✅         | ❌   | ❌       | ❌   |

* [VideoFrameDecoder]:
    * Decode video frames using Android's built-in MediaMetadataRetriever class
    * It is recommended to use Android 8.1 and above, because versions 8.0 and below do not support
      reading frame thumbnails, which will consume a lot of memory when decoding larger videos such
      as 4k.
* [FFmpegVideoFrameDecoder]:
    * Decode video frames using the [FFmpegMediaMetadataRetriever] class of
      the [wseemann/FFmpegMediaMetadataRetriever-project][FFmpegMediaMetadataRetriever-project]
      library
    * Library size is approximately 23 MB

## Install component

`${LAST_VERSION}`: [![Download][version_icon]][version_link] (Not included 'v')

```kotlin
implementation("io.github.panpf.sketch4:sketch-video:${LAST_VERSION}")
// or
implementation("io.github.panpf.sketch4:sketch-video-ffmpeg:${LAST_VERSION}")
```

> [!IMPORTANT]
> The above components all support automatic registration. You only need to import them without
> additional configuration. If you need to register manually, please read the
> documentation: [《Register component》](register_component.md)

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

[version_icon]: https://img.shields.io/maven-central/v/io.github.panpf.sketch4/sketch-singleton

[version_link]: https://repo1.maven.org/maven2/io/github/panpf/sketch4/

[FFmpegMediaMetadataRetriever-project]: https://github.com/wseemann/FFmpegMediaMetadataRetriever

[FFmpegMediaMetadataRetriever]: https://github.com/wseemann/FFmpegMediaMetadataRetriever/blob/master/core/src/main/kotlin/wseemann/media/FFmpegMediaMetadataRetriever.java

[VideoFrameDecoder]: ../../sketch-video/src/main/kotlin/com/github/panpf/sketch/decode/VideoFrameDecoder.kt

[FFmpegVideoFrameDecoder]: ../../sketch-video-ffmpeg/src/main/kotlin/com/github/panpf/sketch/decode/FFmpegVideoFrameDecoder.kt

[VideoFrameDecoderProvider]: ../../sketch-video/src/main/kotlin/com/github/panpf/sketch/decode/internal/VideoFrameDecoderProvider.kt

[FFmpegVideoFrameDecoderProvider]: ../../sketch-video-ffmpeg/src/main/kotlin/com/github/panpf/sketch/decode/internal/FFmpegVideoFrameDecoderProvider.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.common.kt

[ImageOptions]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageOptions.common.kt