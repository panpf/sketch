# 下载进度指示器

翻译：[English](progress_indicator.md)

Sketch 为 view 和 Compose 提供了显示下载进度的扩展功能，如下：

![sample_progress_indicator.png](../res/sample_progress_indicator.png)

提供了三种样式可供选择，如下：

![sample_progress_drawable.png](../res/sample_progress_drawable.png)

> 它们还可以调整颜色、尺寸和行为

## Compose

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
    uri = "https://example.com/image.jpg",
    modifier = Modifier
        .size(200.dp)
        .progressIndicator(state, progressPainter),
    state = state,
    contentDescription = "",
)
```

> Compose 版本功能由 [ProgressIndicatorModifier] 实现

### 自定义指示器样式

你可以继承 [AbsProgressPainter] 实现你自己的进度指示器，如下：

```kotlin
class MyProgressPainter(
    private val maskColor: Color = Color(PROGRESS_INDICATOR_MASK_COLOR),
    hiddenWhenIndeterminate: Boolean = PROGRESS_INDICATOR_HIDDEN_WHEN_INDETERMINATE,
    hiddenWhenCompleted: Boolean = PROGRESS_INDICATOR_HIDDEN_WHEN_COMPLETED,
    stepAnimationDuration: Int = PROGRESS_INDICATOR_STEP_ANIMATION_DURATION,
) : AbsProgressPainter(
    hiddenWhenIndeterminate = hiddenWhenIndeterminate,
    hiddenWhenCompleted = hiddenWhenCompleted,
    stepAnimationDuration = stepAnimationDuration
), SketchPainter {

    override val intrinsicSize: Size = Size(200.0, 200.0)

    override fun DrawScope.drawProgress(drawProgress: Float) {
        // 绘制你的指示器
    }
}
```

然后使用你自己的指示器，如下：

```kotlin
val progressPainter = remember { MyProgressPainter() }
val state = rememberAsyncImageState()
AsyncImage(
    uri = "https://example.com/image.jpg",
    modifier = Modifier
        .size(200.dp)
        .progressIndicator(state, progressPainter),
    state = state,
    contentDescription = "",
)
```

## View

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

### 自定义指示器样式

你可以继承 [AbsProgressDrawable] 实现你自己的进度指示器，如下：

```kotlin
class MyProgressDrawable(
    hiddenWhenIndeterminate: Boolean = PROGRESS_INDICATOR_HIDDEN_WHEN_INDETERMINATE,
    hiddenWhenCompleted: Boolean = PROGRESS_INDICATOR_HIDDEN_WHEN_COMPLETED,
    stepAnimationDuration: Int = PROGRESS_INDICATOR_DEFAULT_STEP_ANIMATION_DURATION,
) : AbsProgressDrawable(
    hiddenWhenIndeterminate = hiddenWhenIndeterminate,
    hiddenWhenCompleted = hiddenWhenCompleted,
    stepAnimationDuration = stepAnimationDuration
) {

    private val paint = Paint().apply {
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

```kotlin
val sketchImageView = SketchImageView(context)
sketchImageView.showProgressIndicator(MyProgressDrawable())
```

[SketchImageView]: ../../sketch-extensions-view/src/main/kotlin/com/github/panpf/sketch/SketchImageView.kt

[ProgressIndicatorAbility]: ../../sketch-extensions-view/src/main/kotlin/com/github/panpf/sketch/ability/MimeTypeLogoAbility.kt

[AbsProgressDrawable]: ../../sketch-extensions-core/src/androidMain/kotlin/com/github/panpf/sketch/drawable/internal/AbsProgressDrawable.kt

[ProgressIndicatorModifier]: ../../sketch-extensions-compose/src/commonMain/kotlin/com/github/panpf/sketch/ability/ProgressIndicatorModifier.kt

[AbsProgressPainter]: ../../sketch-extensions-compose/src/commonMain/kotlin/com/github/panpf/sketch/painter/internal/AbsProgressPainter.kt