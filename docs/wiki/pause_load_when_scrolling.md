# 列表滑动中暂停加载图片

列表滑动中暂停加载功能可以在列表滑动中将 [ImageRequest] 的 depth 参数设置为 [Depth]
.MEMORY，这样就只会从内存中去找图片，不会再加载新图片，这在性能较差的设备上能显著提高列表滑动流畅度

### 配置

`需要导入 sketch-extensions 模块`

第 1 步. 在你的列表控件 RecyclerView 或 ListView 上添加滑动监听，如下：

```kotlin
recyclerView.addOnScrollListener(PauseLoadWhenScrollingMixedScrollListener())

// 或

listView.setOnScrollListener(PauseLoadWhenScrollingMixedScrollListener())
```

第 2 步. 在初始化 [Sketch] 时添加 [PauseLoadWhenScrollingDisplayInterceptor] 请求拦截器，如下：

```kotlin
class MyApplication : Application(), SketchFactory {

    override fun createSketch(): Sketch = Sketch.Builder(this).apply {
        components {
            addRequestInterceptor(PauseLoadWhenScrollingDisplayInterceptor())
        }
    }.build()
}
```

> 注意：[PauseLoadWhenScrollingDisplayInterceptor] 仅对 [DisplayRequest] 有效

第 3 步. 针对单个请求开启列表滑动中暂停加载功能，如下：

```kotlin
imageView.displayImage("https://www.sample.com/image.jpg") {
    pauseLoadWhenScrolling()
}
```

第 4 步. 可选. 配置使用 placeholder 图片作为列表滑动中暂停加载错误状态图片，如下：

```kotlin
imageView.displayImage("https://www.sample.com/image.jpg") {
    saveCellularTraffic()

    error(R.drawable.ic_error) {
        pauseLoadWhenScrollingError()   // 不指定图片表示使用 placeholder
    }
}
```

[Sketch]: ../../sketch/src/main/java/com/github/panpf/sketch/Sketch.kt

[DisplayRequest]: ../../sketch/src/main/java/com/github/panpf/sketch/request/DisplayRequest.kt

[PauseLoadWhenScrollingDisplayInterceptor]: ../../sketch-extensions/src/main/java/com/github/panpf/sketch/request/PauseLoadWhenScrollingDisplayInterceptor.kt

[ImageRequest]: ../../sketch/src/main/java/com/github/panpf/sketch/request/ImageRequest.kt

[Depth]: ../../sketch/src/main/java/com/github/panpf/sketch/request/Depth.kt