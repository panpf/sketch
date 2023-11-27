# Target

[Target] 用来接收 [ImageRequest] 的结果 [ImageResult]，并将结果应用到目标上

从前面的 [入门][getting_started] 文档可以知道 [ImageRequest] 分为 [DisplayRequest]、[LoadRequest]、[DownloadRequest]
三种，他们又都有不同的 [ImageResult] 实现，因此 [Target] 也有对应的三种实现：

* [DisplayTarget]：接收 Drawable 类型的结果，[DisplayRequest] 专用
* [LoadTarget]：接收 Bitmap 类型的结果，[LoadRequest] 专用
* [DownloadTarget]：接收 [DownloadData] 类型的结果，[DownloadRequest] 专用

下面演示创建自定义 [DisplayTarget]:

```kotlin
DisplayRequest(context, "https://www.example.com/image.jpg") {
    target(
        onStart = { placeholder: Drawable? ->
            // Handle the placeholder drawable. 
        },
        onSuccess = { result: Drawable ->
            // Handle the successful result Drawable. 
        },
        onError = { error: Drawable? ->
            // Handle the error drawable. 
        }
    )
}.enqueue(request)
```

> LoadTarget 和 DownloadTarget 同 DisplayTarget 使用方式大同小异

[DisplayTarget] 通常用来将 Drawable 应用到 View，因此 Sketch 提供了 [ViewDisplayTarget] 和 [ImageViewDisplayTarget]
来简化使用

[DisplayRequest] 还提供了 target(ImageView) 方法来简化绑定到 ImageView，如下：

```kotlin
DisplayRequest(context, "https://www.example.com/image.jpg") {
    target(imageView)
}.enqueue()
```

### RemoteViews

Sketch 提供了 [RemoteViewsDisplayTarget] 用来将图片显示到 [RemoteViews]，如下：

```kotlin
val remoteViews =
    RemoteViews(context.packageName, R.layout.remote_views_notification)

val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID).apply {
    setSmallIcon(R.mipmap.ic_launcher)
    setContent(remoteViews)
}.build()

DisplayRequest(context, "https://www.example.com/image.jpg") {
    resize(100.dp2px, 100.dp2px, scale = START_CROP)
    target(
        RemoteViewsDisplayTarget(
            remoteViews = remoteViews,
            imageViewId = R.id.remoteViewsNotificationImage,
            ignoreNullDrawable = true,
            onUpdated = {
                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(1101, notification)
            }
        )
    )
}.enqueue()
```

1. 如上所示 [RemoteViewsDisplayTarget] 仅将 Drawable 转换为 Bitmap 并调用 [RemoteViews] 的 setImageViewBitmap
   方法设置 Bitmap
2. 所以还需要你在 onUpdated 函数中刷新通知或 AppWidget 才能将 Bitmap 显示到屏幕上

[getting_started]: getting_started.md

[Target]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/target/Target.kt

[DisplayTarget]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/target/DisplayTarget.kt

[ViewDisplayTarget]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/target/ViewDisplayTarget.kt

[ImageViewDisplayTarget]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/target/ImageViewDisplayTarget.kt

[LoadTarget]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/target/LoadTarget.kt

[DownloadTarget]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/target/DownloadTarget.kt

[ImageRequest]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

[ImageResult]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/ImageResult.kt

[DisplayRequest]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/DisplayRequest.kt

[LoadRequest]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/LoadRequest.kt

[DownloadRequest]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/DownloadRequest.kt

[DownloadData]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/DownloadData.kt

[RemoteViews]: https://developer.android.google.cn/reference/android/widget/RemoteViews

[RemoteViewsDisplayTarget]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/target/RemoteViewsDisplayTarget.kt