# 播放动图

翻译：[English](animated_image.md)

Sketch 支持播放 GIF、WEBP、HEIF 动图，每一种动图都有相应的 [DrawableDecoder] 提供支持，如下：

| Type          | Decoder                       | APi Limit    | Additional Module |
|:--------------|:------------------------------|:-------------|:------------------|
| GIF           | [GifAnimatedDrawableDecoder]  | Android 9+   | sketch-gif        |
| GIF           | [GifMovieDrawableDecoder]     | Android 4.4+ | sketch-gif        |
| GIF           | [GifDrawableDrawableDecoder]  | Android 4.1+ | sketch-gif-koral  |
| WEBP Animated | [WebPAnimatedDrawableDecoder] | Android 9+   | sketch-gif        |
| HEIF Animated | [HeifAnimatedDrawableDecoder] | Android 11+  | sketch-gif        |

> 注意：
> 1. GIF 提供了三种 [DrawableDecoder] 可以根据 app 支持的最低版本选择合适的
> 2. `sketch-gif` 模块使用 Android 自带的 [ImageDecoder] 和 [Movie] 类实现播放
     GIF、WEBP、HEIF，不会额外增加包体积
> 3. `sketch-gif-koral` 模块使用 [koral--]/[android-gif-drawable] 库的 [GifDrawable] 类实现播放
     gif，库体积大概 250 KB

## 注册动图解码器

Sketch 默认并没有注册任何动图的 [DrawableDecoder]，需要你主动将 [DrawableDecoder] 注册到 Sketch
才能播放动图，如下：

```kotlin
/* 为所有 ImageRequest 注册 */
class MyApplication : Application(), SketchFactory {

    override fun createSketch(): Sketch {
        return Sketch.Builder(this).apply {
            components {
                addDrawableDecoder(
                    when {
                        VERSION.SDK_INT >= VERSION_CODES.P -> GifAnimatedDrawableDecoder.Factory()
                        VERSION.SDK_INT >= VERSION_CODES.KITKAT -> GifMovieDrawableDecoder.Factory()
                        else -> GifDrawableDrawableDecoder.Factory()
                    }
                )
                if (VERSION.SDK_INT >= VERSION_CODES.P) {
                    addDrawableDecoder(WebpAnimatedDrawableDecoder.Factory())
                }
                if (VERSION.SDK_INT >= VERSION_CODES.R) {
                    addDrawableDecoder(HeifAnimatedDrawableDecoder.Factory())
                }
            }
        }.build()
    }
}

/* 为单个 ImageRequest 注册 */
imageView.displayImage("https://www.example.com/image.gif") {
    components {
        addDrawableDecoder(
            when {
                VERSION.SDK_INT >= VERSION_CODES.P -> GifAnimatedDrawableDecoder.Factory()
                VERSION.SDK_INT >= VERSION_CODES.KITKAT -> GifMovieDrawableDecoder.Factory()
                else -> GifDrawableDrawableDecoder.Factory()
            }
        )
        if (VERSION.SDK_INT >= VERSION_CODES.P) {
            addDrawableDecoder(WebpAnimatedDrawableDecoder.Factory())
        }
        if (VERSION.SDK_INT >= VERSION_CODES.R) {
            addDrawableDecoder(HeifAnimatedDrawableDecoder.Factory())
        }
    }
}
```

## 配置

[ImageRequest] 和 [ImageOptions] 都提供了相关方法用于动图相关配置，如下：

```kotlin
imageView.displayImage("https://www.example.com/image.gif") {
    // 禁用动图，会只解码动图的第一帧
    disallowAnimatedImage()

    // 配置动图播放 1 次就停止，默认无限循环播放
    repeatCount(1)

    // 监听动图开始和停止播放
    onAnimationStart {
        // ...
    }
    onAnimationEnd {
        // ...
    }

    // 对动图的每一帧在绘制时进行修改 
    animatedTransformation { canvas ->
        // ...
    }
}
```

## 控制播放

动图相关的 [DrawableDecoder] 统一返回 [SketchAnimatableDrawable]，[SketchAnimatableDrawable] 实现了
Animatable2Compat 接口

你可以通过 Animatable2Compat 接口的 start() 和 stop() 方法手动控制开始播放和停止播放

#### 初始状态

[GenericViewDisplayTarget] 在将 [SketchAnimatableDrawable] 显示到 ImageView 上之后会检查
ImageRequest.lifecycle 的状态，如果 lifecycle 的状态大于 start 就开始播放

#### 自动控制

[GenericViewDisplayTarget] 会监听 ImageRequest.lifecycle 的 start 和 stop 状态自动控制播放


[koral--]: https://github.com/koral--

[android-gif-drawable]: https://github.com/koral--/android-gif-drawable

[GifDrawable]: https://github.com/koral--/android-gif-drawable/blob/dev/android-gif-drawable/src/main/kotlin/pl/droidsonroids/gif/GifDrawable.java

[DrawableDecoder]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/decode/DrawableDecoder.kt

[GifAnimatedDrawableDecoder]: ../../sketch-gif/src/main/kotlin/com/github/panpf/sketch/decode/GifAnimatedDrawableDecoder.kt

[HeifAnimatedDrawableDecoder]: ../../sketch-gif/src/main/kotlin/com/github/panpf/sketch/decode/HeifAnimatedDrawableDecoder.kt

[WebpAnimatedDrawableDecoder]: ../../sketch-gif/src/main/kotlin/com/github/panpf/sketch/decode/WebpAnimatedDrawableDecoder.kt

[GifDrawableDrawableDecoder]: ../../sketch-gif-koral/src/main/kotlin/com/github/panpf/sketch/decode/GifDrawableDrawableDecoder.kt

[GifMovieDrawableDecoder]: ../../sketch-gif/src/main/kotlin/com/github/panpf/sketch/decode/GifMovieDrawableDecoder.kt

[ImageRequest]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

[SketchFactory]: ../../sketch/src/main/kotlin/com/github/panpf/sketch/SketchFactory.kt

[SketchAnimatableDrawable]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/drawable/SketchAnimatableDrawable.kt

[Movie]: https://cs.android.com/android/platform/superproject/+/master:frameworks/base/graphics/java/android/graphics/Movie.java

[ImageDecoder]: https://cs.android.com/android/platform/superproject/+/master:frameworks/base/graphics/java/android/graphics/ImageDecoder.java

[ImageRequest]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

[ImageOptions]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/ImageOptions.kt

[GenericViewDisplayTarget]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/target/GenericViewDisplayTarget.kt