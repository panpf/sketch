# Download progress indicator

Translations: [简体中文](download_progress_indicator_zh.md)

Sketch provides extended functions for view and Compose to display download progress, as follows:

![sample_progress_indicator.png](../res/sample_progress_indicator.png)

Three styles are provided to choose from, as follows:

![sample_progress_drawable.png](../res/sample_progress_drawable.png)

> They can also adjust color, size and behavior

### View

> [!IMPORTANT]
> * Required import `sketch-extensions-view` module
> * Required [SketchImageView]

```kotlin
val sketchImageView = SketchImageView(context)

sketchImageView.showMaskProgressIndicator()
// or
sketchImageView.showSectorProgressIndicator()
// or
sketchImageView.showRingProgressIndicator()
```

> View version functionality is implemented by [ProgressIndicatorAbility]

### Compose

> [!IMPORTANT]
> Required import `sketch-extensions-compose` module

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

> Compose version function is implemented by [ProgressIndicatorModifier]

### Custom indicator style

You can extends [AbsProgressDrawable] to implement your own progress indicator, as follows:

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
            // Draw your indicator
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

Then use your own indicator like this:

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

[ProgressIndicatorAbility]: ../../sketch-extensions-view-core/src/main/kotlin/com/github/panpf/sketch/ability/MimeTypeLogoAbility.kt

[AbsProgressDrawable]: ../../sketch-extensions-core/src/main/kotlin/com/github/panpf/sketch/drawable/AbsProgressDrawable.kt

[ProgressIndicatorModifier]: ../../sketch-extensions-compose/src/main/kotlin/com/github/panpf/sketch/compose/ability/ProgressIndicatorModifier.kt