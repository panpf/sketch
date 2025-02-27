# 列表滚动中暂停加载图片

翻译：[English](pause_load_when_scrolling.md)

列表滚动的过程中大量加载图片会降低 UI 流畅度，因此在性能较差的设备上列表滚动中暂停加载图片能显著提高
UI 流畅度

首先安装组件

`${LAST_VERSION}`: [![Download][version_icon]][version_link] (不包含 'v')

```kotlin
implementation("io.github.panpf.sketch4:sketch-extensions-compose:${LAST_VERSION}")
// or
implementation("io.github.panpf.sketch4:sketch-extensions-view:${LAST_VERSION}")
```

然后在列表控件上添加滚动监听，如下：

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

再注册 [PauseLoadWhenScrollingDecodeInterceptor] 请求拦截器，如下：

```kotlin
// 在自定义 Sketch 时为所有 ImageRequest 注册
Sketch.Builder(context).apply {
    components {
        addDecodeInterceptor(PauseLoadWhenScrollingDecodeInterceptor())
    }
}.build()

// 加载图片时为单个 ImageRequest 注册
ImageRequest(context, "https://example.com/image.jpg") {
    components {
        addDecodeInterceptor(PauseLoadWhenScrollingDecodeInterceptor())
    }
}
```

最后针对单个请求开启列表滚动中暂停加载功能，如下：

```kotlin
ImageRequest(context, "https://example.com/image.jpg") {
    pauseLoadWhenScrolling(true)
}
```

[version_icon]: https://img.shields.io/maven-central/v/io.github.panpf.sketch4/sketch-singleton

[version_link]: https://repo1.maven.org/maven2/io/github/panpf/sketch4/

[Sketch]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/Sketch.common.kt

[ImageRequest]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.common.kt

[PauseLoadWhenScrollingDecodeInterceptor]: ../sketch-extensions-core/src/commonMain/kotlin/com/github/panpf/sketch/request/PauseLoadWhenScrollingDecodeInterceptor.kt