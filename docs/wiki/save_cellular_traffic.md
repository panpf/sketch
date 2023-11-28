# Save cellular data

Translations: [简体中文](save_cellular_traffic_zh.md)

> [!IMPORTANT]
> Required import `sketch-extensions` module

The cellular traffic saving function can set the depth parameter of [ImageRequest] to [Depth].LOCAL
when detecting that current cellular traffic is present, so that images will no longer be downloaded
from the network.

### Configure

First register the [SaveCellularTrafficDisplayInterceptor] request interceptor, as follows:

```kotlin
/* Register for all ImageRequests */
class MyApplication : Application(), SketchFactory {

    override fun createSketch(): Sketch {
        return Sketch.Builder(this).apply {
            components {
                addRequestInterceptor(SaveCellularTrafficDisplayInterceptor())
            }
        }.build()
    }
}

/* Register for a single ImageRequest */
imageView.displayImage("https://www.sample.com/image.jpg") {
    components {
        addRequestInterceptor(SaveCellularTrafficDisplayInterceptor())
    }
}
```

> Note: [SaveCellularTrafficDisplayInterceptor] is only valid for [DisplayRequest]

Then enable the cellular data saving function for a single request, as follows:

```kotlin
imageView.displayImage("https://www.sample.com/image.jpg") {
    saveCellularTraffic(true)
}
```

Finally, configure the error status picture dedicated to the cellular traffic saving function, as
follows:

```kotlin
imageView.displayImage("https://www.sample.com/image.jpg") {
    saveCellularTraffic(true)

    error(R.drawable.ic_error) {
        saveCellularTrafficError(R.drawable.ic_signal_cellular)
    }
}
```

Optional. Enable clicking ImageView to ignore cellular data and redisplay the image

> This feature requires the use of [SketchImageView]

```kotlin
sketchImageView.setClickIgnoreSaveCellularTrafficEnabled(true)
```

[Sketch]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/Sketch.kt

[SketchImageView]: ../../sketch-extensions-core/src/main/kotlin/com/github/panpf/sketch/SketchImageView.kt

[SaveCellularTrafficDisplayInterceptor]: ../../sketch-extensions-core/src/main/kotlin/com/github/panpf/sketch/request/SaveCellularTrafficDisplayInterceptor.kt

[DisplayRequest]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/DisplayRequest.kt

[ImageRequest]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

[Depth]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/Depth.kt