# Lifecycle

Sketch 依赖 Lifecycle 监控页面的生命周期，用于以下功能：

1. 在 onDestroy 时停止请求（ViewTargetRequestDelegate.onDestroy()）
2. 在 onStart 和 onStop 时控制动图播放（GenericViewTarget.onStart()/onStop()）
3. 在 onStart 和 onStop 时控制 SketchZoomView 释放碎片（ZoomAbility.lifecycleObserver）

### 配置

ImageRequest.Builder 提供了 lifecycle() 方法用于设置 lifecycle，如下：

```kotlin
imageView.displayImage("https://www.example.com/image.gif") {
    val lifecycle: Lifecycle = ...
    lifecycle(lifecycle)
}
```

### 默认值

如果在创建请求时没有设置 lifecycle Sketch 会按如下顺序获取 lifecycle:

1. 从 Context 取 Lifecycle
    1. 取 context
        1. 如果 target 是 ViewTarget 就用 view.context
        2. 否则用 ImageRequest.Builder.context
    2. 取 Lifecycle
        1. 如果 context 实现了 LifecycleOwner 接口就从 LifecycleOwner 取
        2. 如果 context 是 ContextWrapper 就取出 baseContext 再去递归条件 1
2. 使用 GlobalLifecycle

### ViewPager+Fragment

从 Context 取 Lifecycle 一般取到的是 Activity 的 Lifecycle，大多数情况下已经够用了，但是当在 ViewPager+Fragment 的结构中时，需要
Fragment 的 View Lifecycle，这时候需要你手动指定，如下：

```kotlin
class ImageZoomFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        imageView.displayImage("https://www.example.com/image.gif") {
            lifecycle(viewLifecycleOwner.lifecycle)
        }
    }
}
```