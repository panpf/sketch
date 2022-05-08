# 节省蜂窝流量

节省蜂窝流量功能可以在检测到当前是蜂窝流量时将 [ImageRequest] 的 depth 参数设置为 [RequestDepth].LOCAL，这样就不会再从网络下载图片

### 配置

`需要导入 sketch-extensions 模块`

第 1 步. 在初始化 [Sketch] 时添加 [SaveCellularTrafficDisplayInterceptor] 请求拦截器，如下：

```kotlin
class MyApplication : Application(), SketchFactory {

    override fun createSketch(): Sketch = Sketch.Builder(this).apply {
        addRequestInterceptor(SaveCellularTrafficDisplayInterceptor())
    }.build()
}
```

> 注意：[SaveCellularTrafficDisplayInterceptor] 仅对 [DisplayRequest] 有效

第 2 步. 针对单个请求开启节省蜂窝流量功能，如下：

```kotlin
imageView.displayImage("https://www.sample.com/image.jpg") {
    saveCellularTraffic()
}
```

第 3 步. 可选. 配置节省蜂窝流量功能专用的错误图片，如下：

```kotlin
imageView.displayImage("https://www.sample.com/image.jpg") {
    saveCellularTraffic()

    error(R.drawable.ic_error) {
        saveCellularTrafficError(R.drawable.ic_signal_cellular)
    }
}
```

第 4 步. 可选. 开启点击当前图片忽略节省蜂窝流量功能并重新显示图片

此功能需要使用 [SketchImageView]

```kotlin
sketchImageView.setClickIgnoreSaveCellularTrafficEnabled(true)
```

[Sketch]: ../../sketch/src/main/java/com/github/panpf/sketch/Sketch.kt

[SketchImageView]: ../../sketch-extensions/src/main/java/com/github/panpf/sketch/SketchImageView.kt

[SaveCellularTrafficDisplayInterceptor]: ../../sketch-extensions/src/main/java/com/github/panpf/sketch/request/SaveCellularTrafficDisplayInterceptor.kt

[DisplayRequest]: ../../sketch/src/main/java/com/github/panpf/sketch/request/DisplayRequest.kt

[ImageRequest]: ../../sketch/src/main/java/com/github/panpf/sketch/request/ImageRequest.kt

[RequestDepth]: ../../sketch/src/main/java/com/github/panpf/sketch/request/RequestDepth.kt