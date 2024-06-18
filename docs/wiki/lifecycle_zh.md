# Lifecycle

翻译：[English](lifecycle.md)

[Sketch] 依赖 [Lifecycle] 监控页面的生命周期，用于以下功能：

1. 动图加载完成时如果页面已经到达 Start 状态则自动播放
2. 在页面到达 Start 或 Stop 状态时启动或停止播放动图
3. 在页面销毁时停止请求

### 默认值

如果在创建请求时没有设置 [Lifecycle]，[Sketch] 会按如下顺序获取:

* compose:
    1. 通过 LocalLifecycleOwner.current.lifecycle API 获取
    2. 使用 [GlobalLifecycle]
* view:
    1. 通过 view.findViewTreeLifecycleOwner() API 获取
    2. 通过 view.context 获取（如果 context 实现了 LifecycleOwner 接口，例如 Activity）
    3. 通过 [ImageRequest].Builder.context 获取（如果 context 实现了 LifecycleOwner 接口，例如
       Activity）
    4. 使用 [GlobalLifecycle]

### 配置

如果上述 [Sketch] 默认无法获取到 [Lifecycle] 或默认获取的 [Lifecycle] 不满足你的需求，[ImageRequest]
.Builder 还提供了 lifecycle() 方法用于设置 lifecycle，如下：

```kotlin
val lifecycle: Lifecycle = ...
ImageRequest(context, "https://www.example.com/image.gif") {
    lifecycle(lifecycle)
}
// or
val lifecycle: Lifecycle = ...
ImageRequest(context, "https://www.example.com/image.gif") {
    lifecycle(lifecycle)
}
```

[Sketch]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/Sketch.common.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.common.kt

[Lifecycle]: https://developer.android.com/reference/kotlin/androidx/lifecycle/Lifecycle

[GlobalLifecycle]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/GlobalLifecycle.kt