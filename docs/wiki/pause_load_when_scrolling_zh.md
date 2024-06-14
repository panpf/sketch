# 列表滚动中暂停加载图片

翻译：[English](pause_load_when_scrolling.md)

> [!IMPORTANT]
> 必须导入 `sketch-extensions-view-core` 或 `sketch-extensions-compose-core` 模块

列表滚动的过程中开启异步线程加载图片会降低 UI 流畅度，因此在性能较差的设备上列表滚动中暂停加载图片能显著提高
UI 流畅度

### 配置

首先在你的列表控件上添加滚动监听，如下：

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

然后注册 [PauseLoadWhenScrollingDecodeInterceptor] 请求拦截器，如下：

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

> [!TIP]
> [PauseLoadWhenScrollingDecodeInterceptor] 仅对 [ImageRequest] 有效

最后针对单个请求开启列表滚动中暂停加载功能，如下：

```kotlin
ImageRequest(context, "https://example.com/image.jpg") {
    pauseLoadWhenScrolling(true)
}
```

[Sketch]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/Sketch.common.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

[PauseLoadWhenScrollingDecodeInterceptor]: ../../sketch-extensions-core/src/commonMain/kotlin/com/github/panpf/sketch/request/PauseLoadWhenScrollingDecodeInterceptor.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.kt
