# 节省蜂窝流量

节省蜂窝流量功能可以在检测到当前是蜂窝流量时将 [ImageRequest] 的 depth 参数设置为 [Depth].LOCAL，这样就不会再从网络下载图片

### 配置

`需要导入 sketch-extensions 模块`

第 1 步. 注册 

在初始化 [Sketch] 时添加 [SaveCellularTrafficDisplayInterceptor] 请求拦截器，这样所有的 [ImageRequest] 都可以使用，如下：

```kotlin
class MyApplication : Application(), SketchFactory {

    override fun createSketch(): Sketch {
        return Sketch.Builder(this).apply {
            components {
                addRequestInterceptor(SaveCellularTrafficDisplayInterceptor())
            }
        }.build()
    }
}
```

或者在显示图片时只给当前 [ImageRequest] 注册，这样就只有当前 [ImageRequest] 可以使用，如下：

```kotlin
imageView.displayImage("https://www.sample.com/image.jpg") {
    components {
        addRequestInterceptor(SaveCellularTrafficDisplayInterceptor())
    }
}
```

> 注意：[SaveCellularTrafficDisplayInterceptor] 仅对 [DisplayRequest] 有效

第 2 步. 针对单个请求开启节省蜂窝流量功能，如下：

```kotlin
imageView.displayImage("https://www.sample.com/image.jpg") {
    saveCellularTraffic(true)
}
```

第 3 步. 可选. 配置节省蜂窝流量功能专用的错误状态图片，如下：

```kotlin
imageView.displayImage("https://www.sample.com/image.jpg") {
    saveCellularTraffic(true)

    error(R.drawable.ic_error) {
        saveCellularTrafficError(R.drawable.ic_signal_cellular)
    }
}
```

第 4 步. 可选. 开启点击 ImageView 忽略节省蜂窝流量并重新显示图片功能

此功能需要使用 [SketchImageView]

```kotlin
sketchImageView.setClickIgnoreSaveCellularTrafficEnabled(true)
```

[Sketch]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/Sketch.kt

[SketchImageView]: ../../sketch-extensions-core/src/main/kotlin/com/github/panpf/sketch/SketchImageView.kt

[SaveCellularTrafficDisplayInterceptor]: ../../sketch-extensions-core/src/main/kotlin/com/github/panpf/sketch/request/SaveCellularTrafficDisplayInterceptor.kt

[DisplayRequest]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/DisplayRequest.kt

[ImageRequest]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

[Depth]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/Depth.kt