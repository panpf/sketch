# Pause loading of images when list scrolling

Translations: [简体中文](pause_load_when_scrolling_zh.md)

> [!IMPORTANT]
> Required import `sketch-extensions` module

Enabling asynchronous thread loading of images during list scrolling will reduce UI fluency.
Therefore, pausing image loading during list scrolling can significantly improve performance on
devices with poor performance UI fluency

### Configure

First add a scroll listener to your list control, as follows:

```kotlin
// RecyclerView
recyclerView.addOnScrollListener(PauseLoadWhenScrollingMixedScrollListener())

// ListView
listView.setOnScrollListener(PauseLoadWhenScrollingMixedScrollListener())

// Compose LazyColumn
@Composable
fun ListContent() {
    val lazyListState = rememberLazyListState()
    LaunchedEffect(lazyGridState.isScrollInProgress) {
        PauseLoadWhenScrollingDrawableDecodeInterceptor.scrolling =
            lazyGridState.isScrollInProgress
    }

    LazyColumn(state = lazyListState) {
        // Draw your item
    }
}
```

Then register the [PauseLoadWhenScrollingDrawableDecodeInterceptor] request interceptor as follows:

```kotlin
/* Register for all ImageRequests */
class MyApplication : Application(), SketchFactory {

    override fun createSketch(): Sketch {
        return Sketch.Builder(this).apply {
            components {
                addDrawableDecodeInterceptor(PauseLoadWhenScrollingDrawableDecodeInterceptor())
            }
        }.build()
    }
}

/* Register for a single ImageRequest */
imageView.displayImage("https://www.sample.com/image.jpg") {
    components {
        addDrawableDecodeInterceptor(PauseLoadWhenScrollingDrawableDecodeInterceptor())
    }
}
```

> Note: [PauseLoadWhenScrollingDrawableDecodeInterceptor] is only valid for [DisplayRequest]

Finally, enable the pause loading function during list scrolling for a single request, as follows:

```kotlin
imageView.displayImage("https://www.sample.com/image.jpg") {
    pauseLoadWhenScrolling(true)
}
```

[Sketch]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/Sketch.kt

[DisplayRequest]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/DisplayRequest.kt

[PauseLoadWhenScrollingDrawableDecodeInterceptor]: ../../sketch-extensions-core/src/main/kotlin/com/github/panpf/sketch/request/PauseLoadWhenScrollingDrawableDecodeInterceptor.kt

[ImageRequest]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/ImageRequest.kt
