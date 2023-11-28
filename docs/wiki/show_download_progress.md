# Show download progress

Translations: [简体中文](show_download_progress_zh.md)

## Implemented through Listener

You can get the status and progress through the listener and progressListener provided by
DisplayRequest and then display them, as follows:

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

> Note: All methods will be executed on the main thread

## Implemented through SketchImageView

> [!IMPORTANT]
> Required import `sketch-extensions` module

[SketchImageView] provided by the sketch-extensions module supports multiple styles to display
download progress, as follows:

```kotlin
// A light black translucent mask layer is displayed on the top layer of SketchImageView. The mask layer disappears from top to bottom as the progress progresses.
sketchImageView.showMaskProgressIndicator()

// Display a fan-shaped progress on the top layer of SketchImageView条
sketchImageView.showSectorProgressIndicator()

// Display a circular progress bar at the top of SketchImageView
sketchImageView.showRingProgressIndicator()
```

> Notice:
> 1. You can choose any one of the above three
> 2. Download progress function is implemented by [ProgressIndicatorAbility]


[SketchImageView]: ../../sketch-extensions-core/src/main/kotlin/com/github/panpf/sketch/SketchImageView.kt

[ProgressIndicatorAbility]: ../../sketch-extensions-core/src/main/kotlin/com/github/panpf/sketch/viewability/MimeTypeLogoAbility.kt