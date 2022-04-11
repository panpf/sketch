# 播放动图

Sketch 支持播放 gif、webp、heif 动图，每一种动图都由相应的 [DrawableDecoder] 提供支持，如下：

|Type|Decoder|APi Limit|Additional Module|
|:---|:---|:---|:---|
|gif|[GifAnimatedDrawableDecoder]|Android 9+|_|
|gif|[GifMovieDrawableDecoder]|Android 4.4+|sketch-gif-movie|
|gif|[GifDrawableDrawableDecoder]|Android 4.1+|sketch-gif-koral|
|webp Animated|[WebPAnimatedDrawableDecoder]|Android 9+|_|
|heif Animated|[HeifAnimatedDrawableDecoder]|Android 11+|_|

> 注意：
> 1. [GifMovieDrawableDecoder] 和 [GifDrawableDrawableDecoder] 需要依赖额外的 module
> 2. gif 提供了三种个 [DrawableDecoder] 你可以根据你的 app 支持的最低版本选择合适的 [DrawableDecoder]
> 3. `sketch-gif-movie` 模块使用 Android 自带的 [Movie] 类实现播放 gif，不会额外增加包体积
> 4. `sketch-gif-koral` 模块使用 [koral--]/[android-gif-drawable] 库的 [GifDrawable] 类实现播放 gif，包体积会额外增加大概 250 KB

## 注册动图解码器

Sketch 默认并没有注册任何动图的 [DrawableDecoder]，需要你将需要的 [DrawableDecoder] 注册到 Sketch 才能播放动图

通过在 Application 类实现 [SketchConfigurator] 接口并使用 components 函数将 [DrawableDecoder] 注册到 Sketch，如下：

```kotlin
class MyApplication : MultiDexApplication(), SketchConfigurator {

    override fun createSketchConfig(): Builder.() -> Unit = {
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
}
```

## 配置

[ImageRequest] 和 [ImageOptions] 都提供了相关方法用于动图相关配置，如下：

```kotlin
imageView.displayImage("https://www.example.com/image.gif") {
    // 禁用动图。有些情况下列表中需要禁用动图播放，点击进入详情页的时候才可以播放
    disabledAnimatedImage()

    // 配置动图播放 1 次就停止，默认无限循环播放
    repeatCount(1)

    // 监听动图开始和停止播放
    onAnimationStart {
        // ...
    }
    onAnimationEnd {
        // ...
    }

    // 对动图的每一帧在绘制时进行转换 
    animatedTransformation { canvas ->
        // ...
    }
}
```

## 控制播放/停止

动图相关的 [DrawableDecoder] 统一返回 [SketchAnimatableDrawable]

##### 初始状态

Sketch 在将 [SketchAnimatableDrawable] 显示到 ImageView 上之后会根据 lifecycle 的状态决定是否开始播放

##### 手动控制

你可以通过 [SketchAnimatableDrawable] 的 start() 和 stop() 方法控制开始播放和停止播放

##### 自动控制

Sketch 会监听 lifecycle 的状态自动的播放或停止动图

[koral--]: https://github.com/koral--

[android-gif-drawable]: https://github.com/koral--/android-gif-drawable

[GifDrawable]: https://github.com/koral--/android-gif-drawable/blob/dev/android-gif-drawable/src/main/java/pl/droidsonroids/gif/GifDrawable.java

[DrawableDecoder]: ../../sketch/src/main/java/com/github/panpf/sketch/decode/DrawableDecoder.kt

[GifAnimatedDrawableDecoder]: ../../sketch/src/main/java/com/github/panpf/sketch/decode/GifAnimatedDrawableDecoder.kt

[HeifAnimatedDrawableDecoder]: ../../sketch/src/main/java/com/github/panpf/sketch/decode/HeifAnimatedDrawableDecoder.kt

[WebpAnimatedDrawableDecoder]: ../../sketch/src/main/java/com/github/panpf/sketch/decode/WebpAnimatedDrawableDecoder.kt

[GifDrawableDrawableDecoder]: ../../sketch-gif-koral/src/main/java/com/github/panpf/sketch/decode/GifDrawableDrawableDecoder.kt

[GifMovieDrawableDecoder]: ../../sketch-gif-movie/src/main/java/com/github/panpf/sketch/decode/GifMovieDrawableDecoder.kt

[ImageRequest]: ../../sketch/src/main/java/com/github/panpf/sketch/request/ImageRequest.kt

[SketchConfigurator]: ../../sketch/src/main/java/com/github/panpf/sketch/SketchConfigurator.kt

[SketchAnimatableDrawable]: ../../sketch/src/main/java/com/github/panpf/sketch/drawable/SketchAnimatableDrawable.kt

[Movie]: https://cs.android.com/android/platform/superproject/+/master:frameworks/base/graphics/java/android/graphics/Movie.java

[ImageRequest]: ../../sketch/src/main/java/com/github/panpf/sketch/request/ImageRequest.kt

[ImageOptions]: ../../sketch/src/main/java/com/github/panpf/sketch/request/ImageOptions.kt