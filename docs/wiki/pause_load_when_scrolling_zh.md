# 列表滑动中暂停加载图片

列表滑动的过程中开启异步线程加载图片会降低 UI 流畅度，因此在性能较差的设备上列表滑动中暂停加载图片能显著提高 UI 流畅度



### 配置

`需要导入 sketch-extensions 模块`

第 1 步. 在你的列表控件上添加滑动监听，如下：

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

第 2 步. 注册

在初始化 [Sketch] 时添加 [PauseLoadWhenScrollingDrawableDecodeInterceptor] 请求拦截器，这样所有的 [ImageRequest] 都可以使用，如下：

```kotlin
class MyApplication : Application(), SketchFactory {

    override fun createSketch(): Sketch {
        return Sketch.Builder(this).apply {
            components {
                addDrawableDecodeInterceptor(PauseLoadWhenScrollingDrawableDecodeInterceptor())
            }
        }.build()
    }
}
```

或者在显示图片时只给当前 [ImageRequest] 注册，这样就只有当前 [ImageRequest] 可以使用，如下：

```kotlin
imageView.displayImage("https://www.sample.com/image.jpg") {
    components {
        addDrawableDecodeInterceptor(PauseLoadWhenScrollingDrawableDecodeInterceptor())
    }
}
```

> 注意：[PauseLoadWhenScrollingDrawableDecodeInterceptor] 仅对 [DisplayRequest] 有效

第 3 步. 针对单个请求开启列表滑动中暂停加载功能，如下：

```kotlin
imageView.displayImage("https://www.sample.com/image.jpg") {
    pauseLoadWhenScrolling(true)
}
```

[Sketch]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/Sketch.kt

[DisplayRequest]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/DisplayRequest.kt

[PauseLoadWhenScrollingDrawableDecodeInterceptor]: ../../sketch-extensions-core/src/main/kotlin/com/github/panpf/sketch/request/PauseLoadWhenScrollingDrawableDecodeInterceptor.kt

[ImageRequest]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/ImageRequest.kt
