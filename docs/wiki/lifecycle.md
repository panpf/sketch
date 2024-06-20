# Lifecycle

Translations: [简体中文](lifecycle_zh.md)

[Sketch] relies on [Lifecycle] to monitor the life cycle of the page for the following
functions:

1. When the animation is loaded, it will automatically play if the page has reached the Start state.
2. Start or stop playing animations when the page reaches Start or Stop state
3. Stop the request when the page is destroyed
function.

### Default Value

If [Lifecycle] is not set when creating the request, [Sketch] will be obtained in the
following order:

* compose:
    1. Obtained through LocalLifecycleOwner.current.lifecycle API
    2. Use [GlobalLifecycle]
* view:
    1. Obtained through view.findViewTreeLifecycleOwner() API
    2. Obtained through view.context (if context implements the LifecycleOwner interface, such as
       Activity)
    3. Obtained through [ImageRequest].Builder.context (if context implements the LifecycleOwner
       interface, e.g.
       Activity)
    4. Use [GlobalLifecycle]

### Configuration

If the above [Sketch] cannot obtain [Lifecycle] by default or the [Lifecycle]
obtained by default does not meet your needs, [ImageRequest].Builder also provides the lifecycle()
method for setting lifecycle, as follows:

```kotlin
val lifecycle: Lifecycle = ...
ImageRequest(context, "https://www.example.com/image.gif") {
    lifecycle(lifecycle)
}
```

[Sketch]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/Sketch.common.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.common.kt

[Lifecycle]: https://developer.android.com/reference/kotlin/androidx/lifecycle/Lifecycle

[GlobalLifecycle]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/GlobalLifecycle.kt