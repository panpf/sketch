# 显示下载进度

## 通过 Listener 实现

通过 DisplayRequest 提供的 listener 和 progressListener 得到状态和进度然后显示即可，如下：

```kotlin
imageView.displayImage("https://www.sample.com/image.jpg") {
    listener(
        onStart = { request: DisplayRequest ->
            // ...
        },
        onSuccess = { request: DisplayRequest, result: DisplayResult.Success ->
            // ...
        },
        onError = { request: DisplayRequest, result: DisplayResult.Error ->
            // ...
        },
        onCancel = { request: DisplayRequest ->
            // ...
        },
    )
    progressListener { request: DisplayRequest, totalLength: Long, completedLength: Long ->
        // ...
    }
}
```

> 注意：
> 1. 所有方法都将在主线程回调

## 通过 SketchImageView 实现

`需要导入 sketch-extensions 模块`

sketch-extensions 模块提供的 [SketchImageView] 支持多种样式显示下载进度，如下：

```kotlin
// 在 SketchImageView 最上层显示一层浅黑色半透明蒙层，蒙层随着进度的进行从上到下消失
sketchImageView.showMaskProgressIndicator()

// 在 SketchImageView 最上层显示一个扇形的进度条
sketchImageView.showSectorProgressIndicator()

// 在 SketchImageView 最上层显示一个环形的进度条
sketchImageView.showRingProgressIndicator()
```

> 注意：
> 1. 以上三种任选其一即可
> 2. 下载进度功能由 [ProgressIndicatorAbility] 实现


[SketchImageView]: ../../sketch-extensions/src/main/java/com/github/panpf/sketch/SketchImageView.kt

[ProgressIndicatorAbility]: ../../sketch-extensions/src/main/java/com/github/panpf/sketch/viewability/MimeTypeLogoAbility.kt