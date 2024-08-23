# 动图

翻译：[English](animated_image.md)

> [!IMPORTANT]
> 必须导入 `sketch-animated` 或 `sketch-animated-koralgif` 模块

[Sketch] 支持播放 GIF、WEBP、HEIF 动图，每一种动图都有相应的 [Decoder] 提供支持，他们所支持的平台以及差异如下：

| Format        | Decoder                   | Android   | iOS | Desktop | Web | resize | Dependent modules        |
|:--------------|:--------------------------|:----------|:----|:--------|:----|--------|:-------------------------|
| GIF           | [GifAnimatedDecoder]      | ✅(API 28) | ❌   | ❌       | ❌   | ✅      | sketch-animated          |
| GIF           | [GifMovieDecoder]         | ✅         | ❌   | ❌       | ❌   | ❌      | sketch-animated          |
| GIF           | [GifDrawableDecoder]      | ✅         | ❌   | ❌       | ❌   | ✅      | sketch-animated-koralgif |
| GIF           | [GifSkiaAnimatedDecoder]  | ❌         | ✅   | ✅       | ✅   | ❌      | sketch-animated          |
| WEBP Animated | [WebpAnimatedDecoder]     | ✅(API 28) | ❌   | ❌       | ❌   | ✅      | sketch-animated          |
| WEBP Animated | [WebpSkiaAnimatedDecoder] | ❌         | ✅   | ✅       | ✅   | ❌      | sketch-animated          |
| HEIF Animated | [HeifAnimatedDecoder]     | ✅(API 30) | ❌   | ❌       | ❌   | ✅      | sketch-animated          |

> [!TIP]
> 在 Android 上为 GIF 提供了三种 [Decoder] 可供选择，你可以根据 app
> 支持的最低版本选择合适的 [Decoder]

## 注册动图解码器

[Sketch] 默认并没有注册任何动图的 [Decoder]，需要你主动将 [Decoder] 注册到 [Sketch] 才能播放动图，如下：

```kotlin
// 在自定义 Sketch 时为所有 ImageRequest 注册
Sketch.Builder(context).apply {
    components {
        addDecoder(
            when {
                VERSION.SDK_INT >= VERSION_CODES.P -> GifAnimatedDecoder.Factory()
                VERSION.SDK_INT >= VERSION_CODES.KITKAT -> GifMovieDecoder.Factory()
                else -> GifDrawableDecoder.Factory()
            }
        )
        if (VERSION.SDK_INT >= VERSION_CODES.P) {
            addDecoder(WebpAnimatedDecoder.Factory())
        }
        if (VERSION.SDK_INT >= VERSION_CODES.R) {
            addDecoder(HeifAnimatedDecoder.Factory())
        }
    }
}.build()

// 加载图片时为单个 ImageRequest 注册
ImageRequest(context, "https://www.example.com/image.gif") {
    components {
        addDecoder(
            when {
                VERSION.SDK_INT >= VERSION_CODES.P -> GifAnimatedDecoder.Factory()
                VERSION.SDK_INT >= VERSION_CODES.KITKAT -> GifMovieDecoder.Factory()
                else -> GifDrawableDecoder.Factory()
            }
        )
        if (VERSION.SDK_INT >= VERSION_CODES.P) {
            addDecoder(WebpAnimatedDecoder.Factory())
        }
        if (VERSION.SDK_INT >= VERSION_CODES.R) {
            addDecoder(HeifAnimatedDecoder.Factory())
        }
    }
}
```

## 配置

[ImageRequest] 和 [ImageOptions] 都提供了相关方法用于动图相关配置，如下：

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

    // [Only Android] 对动图的每一帧在绘制时进行修改 
    animatedTransformation { canvas: Canvas ->
        // ...
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


[AnimatableDrawable]: ../../sketch-core/src/androidMain/kotlin/com/github/panpf/sketch/drawable/AnimatableDrawable.kt

[AnimatablePainter]: ../../sketch-compose-core/src/commonMain/kotlin/com/github/panpf/sketch/painter/AnimatablePainter.kt

[Decoder]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/decode/Decoder.kt

[GenericComposeTarget]: ../../sketch-compose-core/src/commonMain/kotlin/com/github/panpf/sketch/target/GenericComposeTarget.kt

[GenericViewTarget]: ../../sketch-view-core/src/main/kotlin/com/github/panpf/sketch/target/GenericViewTarget.kt

[GifAnimatedDecoder]: ../../sketch-animated/src/androidMain/kotlin/com/github/panpf/sketch/decode/GifAnimatedDecoder.kt

[GifDrawableDecoder]: ../../sketch-animated-koralgif/src/main/kotlin/com/github/panpf/sketch/decode/GifDrawableDecoder.kt

[GifMovieDecoder]: ../../sketch-animated/src/androidMain/kotlin/com/github/panpf/sketch/decode/GifMovieDecoder.kt

[GifSkiaAnimatedDecoder]: ../../sketch-animated/src/nonAndroidMain/kotlin/com/github/panpf/sketch/decode/GifSkiaAnimatedDecoder.kt

[HeifAnimatedDecoder]: ../../sketch-animated/src/androidMain/kotlin/com/github/panpf/sketch/decode/HeifAnimatedDecoder.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.common.kt

[ImageOptions]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageOptions.common.kt

[Movie]: https://cs.android.com/android/platform/superproject/+/master:frameworks/base/graphics/java/android/graphics/Movie.java

[Sketch]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/Sketch.common.kt

[WebpAnimatedDecoder]: ../../sketch-animated/src/androidMain/kotlin/com/github/panpf/sketch/decode/WebpAnimatedDecoder.kt

[WebpSkiaAnimatedDecoder]: ../../sketch-animated/src/nonAndroidMain/kotlin/com/github/panpf/sketch/decode/WebpSkiaAnimatedDecoder.kt


[comment]: <> (wiki)

[getting_started_platform_different]: getting_started_zh.md#平台差异