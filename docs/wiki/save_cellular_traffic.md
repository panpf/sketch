# Save cellular data

Translations: [简体中文](save_cellular_traffic_zh.md)

> [!IMPORTANT]
> Required import `sketch-extensions-core` module

The cellular traffic saving function can set the depth parameter of [ImageRequest] to [Depth].LOCAL
when detecting that current cellular traffic is present, so that images will no longer be downloaded
from the network.

### Configure

First register the [SaveCellularTrafficRequestInterceptor] request interceptor, as follows:

```kotlin
// Register for all ImageRequests when customizing Sketch
Sketch.Builder(this).apply {
    components {
        addRequestInterceptor(SaveCellularTrafficRequestInterceptor())
    }
}.build()

// Register for a single ImageRequest when loading an image
ImageRequest(context, "https://example.com/image.jpg") {
    components {
        addRequestInterceptor(SaveCellularTrafficRequestInterceptor())
    }
}
```

Then enable the cellular data saving function for a single request, as follows:

```kotlin
ImageRequest(context, "https://example.com/image.jpg") {
    saveCellularTraffic(true)
}
```

Finally, configure the error status picture dedicated to the cellular traffic saving function, as
follows:

```kotlin
ImageRequest(context, "https://example.com/image.jpg") {
    saveCellularTraffic(true)

    error(Res.drawable.ic_error) {
        saveCellularTrafficError(Res.drawable.ic_signal_cellular)
    }
}
```

### Click to force load

> [!IMPORTANT]
> 1. Only supports Android View
> 2. This feature requires the use of [SketchImageView]

Enable clicking ImageView to ignore cellular data and redisplay the image

```kotlin
sketchImageView.setClickIgnoreSaveCellularTrafficEnabled(true)
```

[Sketch]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/Sketch.kt

[SketchImageView]: ../../sketch-extensions-view-core/src/main/kotlin/com/github/panpf/sketch/SketchImageView.kt

[SaveCellularTrafficRequestInterceptor]: ../../sketch-extensions-core/src/commonMain/kotlin/com/github/panpf/sketch/request/SaveCellularTrafficRequestInterceptor.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

[Depth]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/Depth.kt