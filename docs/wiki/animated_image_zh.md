# 动图

翻译：[English](animated_image.md)

Sketch 提供了 `sketch-animated-*` 系列模块以支持动图，所支持的平台以及差异如下：

| Module                    | DecoderProvider                           | Decoder                                                                                                                          | Android   | iOS | Desktop | Web |
|:--------------------------|:------------------------------------------|:---------------------------------------------------------------------------------------------------------------------------------|:----------|:----|:--------|:----|
| sketch-animated-gif       | [GifDecoderProvider]                      | android api 28+: [ImageDecoderGifDecoder]</br>android api 27-: [MovieGifDecoder]</br>non android: [SkiaGifDecoder]               | ✅         | ✅   | ✅       | ✅   |
| sketch-animated-gif-koral | [KoralGifDecoderProvider]                 | [KoralGifDecoder]                                                                                                                | ✅         | ❌   | ❌       | ❌   |
| sketch-animated-webp      | [AnimatedWebpDecoderProvider]             | android api 28+: [ImageDecoderAnimatedWebpDecoder]</br>android api 27-: Not supported</br>non android: [SkiaAnimatedWebpDecoder] | ✅(API 28) | ✅   | ✅       | ✅   |
| sketch-animated-heif      | [ImageDecoderAnimatedHeifDecoderProvider] | [ImageDecoderAnimatedHeifDecoder]                                                                                                | ✅(API 30) | ❌   | ❌       | ❌   |

> [!TIP]
> sketch-animated-webp 模块自带的 webp 动图解码器不支持 android api 27 及以下版本，如果有需要请参考
> sample 中的 [PenfeizhouAnimatedWebpDecoder] 并结合 https://github.com/penfeizhou/APNG4Android 库为
> android api 27 及以下版本提供支持

## 安装组件

加载动图前需要先从上述组件中选择一个并安装依赖，以 `sketch-animated-gif` 为例：

`${LAST_VERSION}`: [![Download][version_icon]][version_link] (不包含 'v')

```kotlin
implementation("io.github.panpf.sketch4:sketch-animated-gif:${LAST_VERSION}")
```

> [!IMPORTANT]
> 上述组件都支持自动注册，你只需要导入即可，无需额外配置，如果你需要手动注册，
> 请阅读文档：[《注册组件》](register_component_zh.md)

## 加载动图

直接指定 uri 加载图片即可，如下：

```kotlin
val imageUri = "https://www.sample.com/image.gif"

// compose
AsyncImage(
    uri = imageUri,
    contentDescription = "photo"
)

// view
imageView.loadImage(imageUri)
```

## 配置

`sketch-animated-core` 模块为 [ImageRequest] 和 [ImageOptions] 一些扩展方法用于动图相关的配置，如下：

```kotlin
ImageRequest(context, "https://www.example.com/image.gif") {
    // 禁用动图，只解码动图的第一帧
    disallowAnimatedImage()

    // 配置动图重复播放 1 次就停止，默认无限循环播放
    repeatCount(1)

    // 监听动图开始和停止播放
    onAnimationStart {
        // ...
    }
    onAnimationEnd {
        // ...
    }

    // 对动图的每一帧在绘制时在动图的前景绘制内容
    animatedTransformation { canvas: Any ->
        if (canvas is androidx.compose.ui.graphics.Canvas) {
            // ...
        } else if (canvas is android.graphics.Canvas) {
            // ...
        }
    }
}
```

## 控制播放

动图相关的 [Decoder] 统一返回 [AnimatableDrawable] 或 [AnimatablePainter]，你可以通过它们的 start() 和
stop() 方法控制播放，通过 isRunning() 方法判断播放状态

#### 初始状态

动图的初始状态由 [GenericViewTarget] 和 [GenericComposeTarget] 来控制，在将动图显示到 Target 上之后会检查
[ImageRequest].lifecycle 的状态，如果 lifecycle 的状态大于 start 就开始播放，否则就等到 lifecycle
的状态变为 start 再播放

#### 自动控制

[GenericViewTarget] 和 [GenericComposeTarget] 会监听 [ImageRequest].lifecycle 的 start 和 stop
状态自动控制播放

#### 缓存解码超时帧

Sketch 在非 Android 平台使用 skiko 的 Codec 解码动图，但 Codec 在越靠近动图末尾的帧时解码越慢

当解码耗时超过上一帧的持续时间时用户就会感觉到播放卡顿，因此为了提高播放流畅度，Sketch 支持了缓存解码超时帧功能

但此功能会大幅增加内存消耗，因此默认关闭，你可以通过 cacheDecodeTimeoutFrame() 函数开启，如下：

```kotlin
ImageRequest(context, "https://www.example.com/image.gif") {
    cacheDecodeTimeoutFrame(true)
}
```

[comment]: <> (classs)

[version_icon]: https://img.shields.io/maven-central/v/io.github.panpf.sketch4/sketch-singleton

[version_link]: https://repo1.maven.org/maven2/io/github/panpf/sketch4/

[AnimatableDrawable]: ../../sketch-core/src/androidMain/kotlin/com/github/panpf/sketch/drawable/AnimatableDrawable.kt

[AnimatablePainter]: ../../sketch-compose-core/src/commonMain/kotlin/com/github/panpf/sketch/painter/AnimatablePainter.kt

[Decoder]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/decode/Decoder.kt

[GenericComposeTarget]: ../../sketch-compose-core/src/commonMain/kotlin/com/github/panpf/sketch/target/GenericComposeTarget.kt

[GenericViewTarget]: ../../sketch-view-core/src/main/kotlin/com/github/panpf/sketch/target/GenericViewTarget.kt

[ImageDecoderGifDecoder]: ../../sketch-animated-gif/src/androidMain/kotlin/com/github/panpf/sketch/decode/ImageDecoderGifDecoder.kt

[KoralGifDecoder]: ../../sketch-animated-gif-koral/src/main/kotlin/com/github/panpf/sketch/decode/KoralGifDecoder.kt

[MovieGifDecoder]: ../../sketch-animated-gif/src/androidMain/kotlin/com/github/panpf/sketch/decode/MovieGifDecoder.kt

[SkiaGifDecoder]: ../../sketch-animated-gif/src/nonAndroidMain/kotlin/com/github/panpf/sketch/decode/SkiaGifDecoder.kt

[ImageDecoderAnimatedHeifDecoder]: ../../sketch-animated-heif/src/main/kotlin/com/github/panpf/sketch/decode/ImageDecoderAnimatedHeifDecoder.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.common.kt

[ImageOptions]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageOptions.common.kt

[Movie]: https://cs.android.com/android/platform/superproject/+/master:frameworks/base/graphics/java/android/graphics/Movie.java

[ImageDecoderAnimatedWebpDecoder]: ../../sketch-animated-webp/src/androidMain/kotlin/com/github/panpf/sketch/decode/ImageDecoderAnimatedWebpDecoder.kt

[SkiaAnimatedWebpDecoder]: ../../sketch-animated-webp/src/nonAndroidMain/kotlin/com/github/panpf/sketch/decode/SkiaAnimatedWebpDecoder.kt

[GifDecoderProvider]: ../../sketch-animated-gif/src/commonMain/kotlin/com/github/panpf/sketch/decode/internal/GifDecoderProvider.common.kt

[KoralGifDecoderProvider]: ../../sketch-animated-gif-koral/src/main/kotlin/com/github/panpf/sketch/decode/internal/KoralGifDecoderProvider.kt

[AnimatedWebpDecoderProvider]: ../../sketch-animated-webp/src/commonMain/kotlin/com/github/panpf/sketch/decode/internal/AnimatedWebpDecoderProvider.common.kt

[ImageDecoderAnimatedHeifDecoderProvider]: ../../sketch-animated-heif/src/main/kotlin/com/github/panpf/sketch/decode/internal/ImageDecoderAnimatedHeifDecoderProvider.kt

[PenfeizhouAnimatedWebpDecoder]: ../../sample/src/androidMain/kotlin/com/github/panpf/sketch/sample/util/PenfeizhouAnimatedWebpDecoder.kt

[comment]: <> (wiki)

[getting_started_platform_different]: getting_started_zh.md#平台差异