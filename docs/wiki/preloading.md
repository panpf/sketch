# 预加载图片到内存

要想将图片预加载到内存中只需要不设置 target 即可，如下：

```kotlin
val request = DisplayImage(context, "https://www.sample.com/image.jpg") {
    // more ...
}
sketch.enqueue(request)
```

为了确保在后面使用时准确的命中缓存，需要预加载时的配置和使用时一模一样，特别是 resizeSize