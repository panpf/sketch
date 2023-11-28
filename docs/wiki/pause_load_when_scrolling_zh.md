# 列表滚动中暂停加载图片

翻译：[English](pause_load_when_scrolling.md)

> [!IMPORTANT]
> 必须导入 `sketch-extensions` 模块

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
    if (lazyListState.isScrollInProgress) {
        DisposableEffect(Unit) {
            PauseLoadWhenScrollingDrawableDecodeInterceptor.scrolling = true
            onDispose {
                PauseLoadWhenScrollingDrawableDecodeInterceptor.scrolling = false
            }
        }
    }

    LazyColumn(state = lazyListState) {
        // 绘制你的 item
    }
}
```

然后注册 [PauseLoadWhenScrollingDrawableDecodeInterceptor] 请求拦截器，如下：

```kotlin
/* 为所有 ImageRequest 注册 */
class MyApplication : Application(), SketchFactory {

    override fun createSketch(): Sketch {
        return Sketch.Builder(this).apply {
            components {
                addDrawableDecodeInterceptor(PauseLoadWhenScrollingDrawableDecodeInterceptor())
            }
        }.build()
    }
}

/* 为单个 ImageRequest 注册 */
imageView.displayImage("https://www.sample.com/image.jpg") {
    components {
        addDrawableDecodeInterceptor(PauseLoadWhenScrollingDrawableDecodeInterceptor())
    }
}
```

> 注意：[PauseLoadWhenScrollingDrawableDecodeInterceptor] 仅对 [DisplayRequest] 有效

最后针对单个请求开启列表滚动中暂停加载功能，如下：

```kotlin
imageView.displayImage("https://www.sample.com/image.jpg") {
    pauseLoadWhenScrolling(true)
}
```

[Sketch]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/Sketch.kt

[DisplayRequest]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/DisplayRequest.kt

[PauseLoadWhenScrollingDrawableDecodeInterceptor]: ../../sketch-extensions-core/src/main/kotlin/com/github/panpf/sketch/request/PauseLoadWhenScrollingDrawableDecodeInterceptor.kt

[ImageRequest]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/ImageRequest.kt
