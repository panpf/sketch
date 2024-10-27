# Pause loading of images when list scrolling

Translations: [简体中文](pause_load_when_scrolling_zh.md)

Loading a large number of images during list scrolling will reduce UI fluency. Therefore, pausing
the loading of images during list scrolling on devices with poor performance can significantly
improve UI fluency.

First install component

`${LAST_VERSION}`: [![Download][version_icon]][version_link] (Not included 'v')

```kotlin
implementation("io.github.panpf.sketch4:sketch-extensions-compose:${LAST_VERSION}")
// or
implementation("io.github.panpf.sketch4:sketch-extensions-view:${LAST_VERSION}")
```

Then add a scroll listener to the list control, as follows:

```kotlin
// RecyclerView
recyclerView.addOnScrollListener(PauseLoadWhenScrollingMixedScrollListener())

// ListView
listView.setOnScrollListener(PauseLoadWhenScrollingMixedScrollListener())

// Compose LazyColumn
@Composable
fun ListContent() {
    val lazyListState = rememberLazyListState()
    bindPauseLoadWhenScrolling(lazyListState)

    LazyColumn(state = lazyListState) {
        // ...
    }
}
```

Then register the [PauseLoadWhenScrollingDecodeInterceptor] request interceptor, as follows:

```kotlin
// Register for all ImageRequests when customizing Sketch
Sketch.Builder(context).apply {
    components {
        addDecodeInterceptor(PauseLoadWhenScrollingDecodeInterceptor())
    }
}.build()

// Register for a single ImageRequest when loading an image
ImageRequest(context, "https://example.com/image.jpg") {
    components {
        addDecodeInterceptor(PauseLoadWhenScrollingDecodeInterceptor())
    }
}
```

Finally, enable the pause loading function during list scrolling for a single request, as follows:

```kotlin
ImageRequest(context, "https://example.com/image.jpg") {
    pauseLoadWhenScrolling(true)
}
```

[version_icon]: https://img.shields.io/maven-central/v/io.github.panpf.sketch4/sketch-singleton

[version_link]: https://repo1.maven.org/maven2/io/github/panpf/sketch4/

[Sketch]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/Sketch.common.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.common.kt

[PauseLoadWhenScrollingDecodeInterceptor]: ../../sketch-extensions-core/src/commonMain/kotlin/com/github/panpf/sketch/request/PauseLoadWhenScrollingDecodeInterceptor.kt