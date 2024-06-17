# Target

翻译：[English](target.md)

[Target] 主要的工作是用来显示 [Image]，同时还负责在构建 [ImageRequest]
时提供 [SizeResolver]、[ScaleDecider]、[ResizeOnDrawHelper]、[LifecycleResolver] 等属性，这些属性会被作为默认值使用

## Compose

将 [Image] 显示到 Compose 组件时不需要你主动设置 target，[AsyncImage] 和 [AsyncImagePainter]
会设置，你只需设置其它参数即可，如下：

```kotlin
AsyncIage(
    rqeuest = ComposableImageRequest("https://example.com/image.jpg") {
        placeholder(Res.drawable.placeholder)
        crossfade()
    },
    contentDescription = "photo",
)
```

> [!TIP]
> `placeholder(Res.drawable.placeholder)` 需要导入 `sketch-compose-resources` 模块

## View

将 [Image] 显示到 View 时需要你主动设置 [Target]

### ImageView

[Sketch] 提供了 [ImageViewTarget] 用来将图片显示到 [ImageView]，如下：

```kotlin
ImageRequest(context, "https://www.example.com/image.jpg") {
    placeholder(R.drawable.placeholder)
    crossfade()
    target(imageView)
}.enqueue(request)
```

### RemoteViews

[Sketch] 还提供了 [RemoteViewsTarget] 用来将图片显示到 [RemoteViews]，如下：

```kotlin
val remoteViews =
    RemoteViews(context.packageName, R.layout.remote_views_notification)

val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID).apply {
    setSmallIcon(R.mipmap.ic_launcher)
    setContent(remoteViews)
}.build()

ImageRequest(context, "https://www.example.com/image.jpg") {
    resize(100.dp2px, 100.dp2px, scale = START_CROP)
    target(
        RemoteViewsTarget(
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

> [!TIP]
> 1. 如上所示 [RemoteViewsTarget] 仅将 Drawable 转换为 Bitmap 并调用 [RemoteViews] 的
     setImageViewBitmap 方法设置 Bitmap
> 2. 所以还需要你在 onUpdated 函数中刷新通知或 AppWidget 才能将 Bitmap 显示到屏幕上

[Sketch]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/Sketch.common.kt

[Image]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/Image.kt

[Target]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/target/Target.kt

[ViewTarget]: ../../sketch-view-core/src/main/kotlin/com/github/panpf/sketch/target/ViewTarget.kt

[ImageViewTarget]: ../../sketch-view-core/src/main/kotlin/com/github/panpf/sketch/target/ImageViewTarget.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

[ImageResult]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageResult.kt

[RemoteViews]: https://developer.android.google.cn/reference/android/widget/RemoteViews

[RemoteViewsTarget]: ../../sketch-view-core/src/main/kotlin/com/github/panpf/sketch/target/RemoteViewsTarget.kt

[SizeResolver]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/resize/SizeResolver.kt

[ScaleDecider]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/resize/ScaleDecider.kt

[ResizeOnDrawHelper]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/resize/ResizeOnDraw.kt

[LifecycleResolver]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/LifecycleResolver.kt

[AsyncImage]: ../../sketch-compose-core/src/commonMain/kotlin/com/github/panpf/sketch/AsyncImage.kt

[AsyncImagePainter]: ../../sketch-compose-core/src/commonMain/kotlin/com/github/panpf/sketch/AsyncImagePainter.kt