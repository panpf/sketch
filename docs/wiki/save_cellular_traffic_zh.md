# 节省蜂窝流量

翻译：[English](save_cellular_traffic.md)

> [!IMPORTANT]
> 必须导入 `sketch-extensions-view` 或 `sketch-extensions-compose` 模块

节省蜂窝流量功能可以在检测到当前是蜂窝流量时将 [ImageRequest] 的 depth 参数设置为 [Depth]
.LOCAL，这样就不会再从网络下载图片

### 配置

首先注册 [SaveCellularTrafficRequestInterceptor] 请求拦截器，如下：

```kotlin
/* 为所有 ImageRequest 注册 */
class MyApplication : Application(), SingletonSketch.Factory {

    override fun createSketch(): Sketch {
        return Sketch.Builder(this).apply {
            components {
                addRequestInterceptor(SaveCellularTrafficRequestInterceptor())
            }
        }.build()
    }
}

/* 为单个 ImageRequest 注册 */
imageView.displayImage("https://www.sample.com/image.jpg") {
    components {
        addRequestInterceptor(SaveCellularTrafficRequestInterceptor())
    }
}
```

然后针对单个请求开启节省蜂窝流量功能，如下：

```kotlin
imageView.displayImage("https://www.sample.com/image.jpg") {
    saveCellularTraffic(true)
}
```

最后配置节省蜂窝流量功能专用的错误状态图片，如下：

```kotlin
imageView.displayImage("https://www.sample.com/image.jpg") {
    saveCellularTraffic(true)

    error(R.drawable.ic_error) {
        saveCellularTrafficError(R.drawable.ic_signal_cellular)
    }
}
```

可选. 开启点击 ImageView 忽略节省蜂窝流量并重新显示图片功能

> 此功能需要使用 [SketchImageView]

```kotlin
sketchImageView.setClickIgnoreSaveCellularTrafficEnabled(true)
```

[Sketch]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/Sketch.kt

[SketchImageView]: ../../sketch-extensions-view-core/src/main/kotlin/com/github/panpf/sketch/SketchImageView.kt

[SaveCellularTrafficRequestInterceptor]: ../../sketch-extensions-core/src/main/kotlin/com/github/panpf/sketch/request/SaveCellularTrafficRequestInterceptor.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

[Depth]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/Depth.kt