# 节省蜂窝流量

翻译：[English](save_cellular_traffic.md)

> [!IMPORTANT]
> 仅安卓平台可用

节省蜂窝流量功能可以在检测到当前是蜂窝流量时将 [ImageRequest] 的 depth 参数设置为 [Depth]
.LOCAL，这样就不会再从网络下载图片

### 安装依赖

`${LAST_VERSION}`: [![Download][version_icon]][version_link] (不包含 'v')

```kotlin
implementation("io.github.panpf.sketch4:sketch-extensions-core:${LAST_VERSION}")
```

### 配置

首先注册 [SaveCellularTrafficRequestInterceptor] 请求拦截器，如下：

```kotlin
// 在自定义 Sketch 时为所有 ImageRequest 注册
Sketch.Builder(context).apply {
    components {
        addRequestInterceptor(SaveCellularTrafficRequestInterceptor())
    }
}.build()

// 加载图片时为单个 ImageRequest 注册
ImageRequest(context, "https://example.com/image.jpg") {
    components {
        addRequestInterceptor(SaveCellularTrafficRequestInterceptor())
    }
}
```

然后针对单个请求开启节省蜂窝流量功能，如下：

```kotlin
ImageRequest(context, "https://example.com/image.jpg") {
    saveCellularTraffic(true)
}
```

最后配置节省蜂窝流量功能专用的错误状态图片，如下：

```kotlin
// View
ImageRequest(context, "https://example.com/image.jpg") {
    saveCellularTraffic(true)

    error(
        ConditionStateImage(defaultImage = R.drawable.ic_error) {
            saveCellularTrafficError(R.drawable.ic_signal_cellular)
        }
    )
}

// Compose
ComposableImageRequest(context, "https://example.com/image.jpg") {
    saveCellularTraffic(true)

    error(
        ComposableConditionStateImage(defaultImage = Res.drawable.ic_error) {
            saveCellularTrafficError(Res.drawable.ic_signal_cellular)
        }
    )
}
```

> [!TIP]
> `saveCellularTrafficError(Res.drawable.ic_signal_cellular)` 需要导入
`sketch-extensions-compose-resources` 模块

### 点击强制加载

> [!IMPORTANT]
> 1. 仅支持 Android View
> 2. 此功能需要使用 [SketchImageView]

开启点击 ImageView 忽略节省蜂窝流量并重新加载图片功能：

```kotlin
sketchImageView.setClickIgnoreSaveCellularTrafficEnabled(true)
```

[version_icon]: https://img.shields.io/maven-central/v/io.github.panpf.sketch4/sketch-singleton

[version_link]: https://repo1.maven.org/maven2/io/github/panpf/sketch4/

[Sketch]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/Sketch.common.kt

[SketchImageView]: ../../sketch-extensions-view/src/main/kotlin/com/github/panpf/sketch/SketchImageView.kt

[SaveCellularTrafficRequestInterceptor]: ../../sketch-extensions-core/src/commonMain/kotlin/com/github/panpf/sketch/request/SaveCellularTrafficRequestInterceptor.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.common.kt

[Depth]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/Depth.kt