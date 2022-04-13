# SVG

Sketch 支持解码 SVG 静态图片，由 [SvgBitmapDecoder] 提供支持

### 注册

使用 [SvgBitmapDecoder] 需要先导入 `sketch-svg` 模块，然后在初始化 Sketch 时通过 components() 方法注册即可：

```kotlin
class MyApplication : Application(), SketchConfigurator {

    override fun createSketchConfig(): Builder.() -> Unit = {
        components {
            addBitmapDecoder(SvgBitmapDecoder.Factory())
        }
    }
}
```

### 配置

[DisplayRequest] 和 [LoadRequest] 支持一些 svg 相关的配置，如下：

```kotlin
DisplayRequest(context, "https://www.example.com/image.svg") {
    svgBackgroundColor(Color.WHITE)
}
```

[SvgBitmapDecoder]: ../../sketch-svg/src/main/java/com/github/panpf/sketch/decode/SvgBitmapDecoder.kt

[DisplayRequest]: ../../sketch/src/main/java/com/github/panpf/sketch/request/DisplayRequest.kt

[LoadRequest]: ../../sketch/src/main/java/com/github/panpf/sketch/request/LoadRequest.kt