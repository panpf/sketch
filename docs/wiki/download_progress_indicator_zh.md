# 下载进度指示器

翻译：[English](download_progress_indicator.md)

Sketch 为 view 和 Compose 提供了显示下载进度的扩展功能，如下：

![sample_progress_indicator.png](../res/sample_progress_indicator.png)

提供了三种样式可供选择，如下：

![sample_progress_drawable.png](../res/sample_progress_drawable.png)

> 它们还可以调整颜色、尺寸和行为

### View

> [!IMPORTANT]
> * 必须导入 `sketch-extensions-view` 模块
> * 必须使用 [SketchImageView]

```kotlin
val sketchImageView = SketchImageView(context)

sketchImageView.showMaskProgressIndicator()
// 或
sketchImageView.showSectorProgressIndicator()
// 或
sketchImageView.showRingProgressIndicator()
```

> View 版本功能由 [ProgressIndicatorAbility] 实现

### Compose

> [!IMPORTANT]
> 必须导入 `sketch-extensions-compose` 模块

```kotlin
val progressPainter = rememberDrawableProgressPainter(remember {
    SectorProgressDrawable()
    // or MaskProgressDrawable()
    // or RingProgressDrawable()
})
val state = rememberAsyncImageState()
AsyncImage(
    imageUri = "https://www.sample.com/image.jpg",
    modifier = Modifier
        .size(200.dp)
        .progressIndicator(state, progressPainter),
    state = state,
    contentDescription = "",
)
```

> Compose 版本功能由 [ProgressIndicatorModifier] 实现

### 自定义指示器样式

你可以继承 [AbsProgressDrawable] 实现你自己的进度指示器，如下：

```kotlin
class MyProgressDrawable(
    hiddenWhenIndeterminate: Boolean = false,
    hiddenWhenCompleted: Boolean = true,
    stepAnimationDuration: Int = AbsProgressDrawable.DEFAULT_STEP_ANIMATION_DURATION,
) : AbsProgressDrawable(
    hiddenWhenIndeterminate = hiddenWhenIndeterminate,
    hiddenWhenCompleted = hiddenWhenCompleted,
    stepAnimationDuration = stepAnimationDuration
) {

    private val progressPaint = Paint().apply {
        if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
            alpha = this@RingProgressDrawable.alpha
        }
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            colorFilter = this@RingProgressDrawable.colorFilter
        }
    }

    override fun drawProgress(canvas: Canvas, drawProgress: Float) {
        val bounds = bounds.takeIf { !it.isEmpty } ?: return
        canvas.withSave {
            // 绘制你的指示器
        }
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    @Deprecated(
        "Deprecated in Java. This method is no longer used in graphics optimizations",
        ReplaceWith("")
    )
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    override fun getIntrinsicWidth(): Int = 200

    override fun getIntrinsicHeight(): Int = 200

    override fun mutate(): ProgressDrawable {
        return this
    }
}
```

然后使用你自己的指示器，如下：

View:

```kotlin
val sketchImageView = SketchImageView(context)
sketchImageView.showProgressIndicator(MyProgressDrawable())
```

Compose:

```kotlin
val progressPainter = rememberDrawableProgressPainter(remember {
    MyProgressDrawable()
})
val state = rememberAsyncImageState()
AsyncImage(
    imageUri = "https://www.sample.com/image.jpg",
    modifier = Modifier
        .size(200.dp)
        .progressIndicator(state, progressPainter),
    state = state,
    contentDescription = "",
)
```

[SketchImageView]: ../../sketch-extensions-view-core/src/main/kotlin/com/github/panpf/sketch/SketchImageView.kt

[ProgressIndicatorAbility]: ../../sketch-extensions-view-core/src/main/kotlin/com/github/panpf/sketch/viewability/MimeTypeLogoAbility.kt

[AbsProgressDrawable]: ../../sketch-extensions-core/src/main/kotlin/com/github/panpf/sketch/drawable/AbsProgressDrawable.kt

[ProgressIndicatorModifier]: ../../sketch-extensions-compose/src/main/kotlin/com/github/panpf/sketch/compose/ability/ProgressIndicatorModifier.kt