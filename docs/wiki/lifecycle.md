# Lifecycle

Translations: [简体中文](lifecycle_zh.md)

[Sketch] relies on [PlatformLifecycle] to monitor the life cycle of the page for the following
functions:

1. When the animation is loaded, it will automatically play if the page has reached the Start state.
2. Start or stop playing animations when the page reaches Start or Stop state
3. Stop the request when the page is destroyed

Since the `androidx.lifecycle` library currently does not support the js platform, [Sketch] provides
a [PlatformLifecycle] interface. The js platform relies on `androidx.lifecycle` to implement
the [PlatformLifecycle] interface, but the js platform does not currently support the Lifecycle
function.

### Default Value

If [PlatformLifecycle] is not set when creating the request, [Sketch] will be obtained in the
following order:

* compose:
    1. Obtained through LocalLifecycleOwner.current.lifecycle API
    2. Use [GlobalPlatformLifecycle]
* view:
    1. Obtained through view.findViewTreeLifecycleOwner() API
    2. Obtained through view.context (if context implements the LifecycleOwner interface, such as
       Activity)
    3. Obtained through [ImageRequest].Builder.context (if context implements the LifecycleOwner
       interface, e.g.
       Activity)
    4. Use [GlobalPlatformLifecycle]

### Configuration

If the above [Sketch] cannot obtain [PlatformLifecycle] by default or the [PlatformLifecycle]
obtained by default does not meet your needs, [ImageRequest].Builder also provides the lifecycle()
method for setting lifecycle, as follows:

```kotlin
val platformLifecycle: PlatformLifecycle = ...
ImageRequest(context, "https://www.example.com/image.gif") {
    lifecycle(platformLifecycle)
}
// or
val lifecycle: Lifecycle = ...
ImageRequest(context, "https://www.example.com/image.gif") {
    lifecycle(lifecycle)
}
```

[Sketch]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/Sketch.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

[PlatformLifecycle]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/lifecycle/PlatformLifecycle.kt

[GlobalPlatformLifecycle]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/lifecycle/GlobalPlatformLifecycle.kt