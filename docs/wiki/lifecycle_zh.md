# Lifecycle

翻译：[English](lifecycle.md)

Sketch 依赖 Lifecycle 监控页面的生命周期，用于以下功能：

1. [ViewTargetRequestDelegate] 在 onDestroy 时自动停止请求
2. [GenericViewDisplayTarget] 在 在 onStart 和 onStop 时自动控制动图播放

### 默认值

如果在创建请求时没有设置 lifecycle Sketch 会按如下顺序获取 lifecycle:

view:

1. 通过 view.findViewTreeLifecycleOwner() API 获取
2. 通过 view.context 获取（如果 context 实现了 LifecycleOwner 接口，例如 Activity）
3. 通过 ImageRequest.Builder.context 获取（如果 context 实现了 LifecycleOwner 接口，例如 Activity）
4. 使用 GlobalLifecycle

compose:

1. 通过 LocalLifecycleOwner.current.lifecycle API 获取
2. 通过 ImageRequest.Builder.context 获取（如果 context 实现了 LifecycleOwner 接口，例如 Activity）
3. 使用 GlobalLifecycle

### 配置

如果上述默认值无法获取到 Lifecycle 或默认获取的 Lifecycle 不满足你的需求，[ImageRequest].Builder 还提供了
lifecycle() 方法用于设置 lifecycle，如下：

```kotlin
val lifecycle = LifecycleRegistry(this)
imageView.displayImage("https://www.example.com/image.gif") {
    lifecycle(lifecycle)
}
```

[ImageRequest]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

[ViewTargetRequestDelegate]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/internal/RequestDelegate.kt

[GenericViewDisplayTarget]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/target/GenericViewDisplayTarget.kt