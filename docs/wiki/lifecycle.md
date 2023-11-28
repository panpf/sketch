# Lifecycle

Translations: [简体中文](lifecycle_zh.md)

Sketch relies on Lifecycle to monitor the life cycle of the page for the following functions:

1. [ViewTargetRequestDelegate] automatically stops the request when onDestroy
2. [GenericViewDisplayTarget] automatically controls animation playback during onStart and onStop
3. [SketchZoomImageView] automatically releases or restores fragments during onStop and onStart

### Default Value

If lifecycle is not set when creating the request, Sketch will obtain lifecycle in the following
order:

view:

1. Obtained through view.findViewTreeLifecycleOwner() API
2. Obtained through view.context (if context implements the LifecycleOwner interface, such as
   Activity)
3. Obtained through ImageRequest.Builder.context (if the context implements the LifecycleOwner
   interface, such as Activity)
4. Use GlobalLifecycle

compose:

1. Obtained through LocalLifecycleOwner.current.lifecycle API
2. Obtained through ImageRequest.Builder.context (if the context implements the LifecycleOwner
   interface, such as Activity)
3. Use GlobalLifecycle

### Configure

If the above default value cannot obtain the Lifecycle or the default obtained Lifecycle does not
meet your needs, [ImageRequest].Builder also provides the lifecycle() method for setting the
lifecycle, as follows:

```kotlin
val lifecycle = LifecycleRegistry(this)
imageView.displayImage("https://www.example.com/image.gif") {
    lifecycle(lifecycle)
}
```

[ImageRequest]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

[ViewTargetRequestDelegate]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/internal/RequestDelegate.kt

[GenericViewDisplayTarget]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/target/GenericViewDisplayTarget.kt

[SketchZoomImageView]: ../../sketch-zoom/src/main/kotlin/com/github/panpf/sketch/zoom/SketchZoomImageView.kt