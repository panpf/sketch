# Target

翻译：[English](target.md)

[Target] 用来将 [Image] 显示到 View、Compose 以及其它任意组件上

### View

显示到 View 时需要你主动设置 target，如下：

```kotlin
val imageView = ImageView(context)

ImageRequest(context, "https://www.example.com/image.jpg") {
    placeholder(R.drawable.placeholder)
    crossfade(true)
    target(imageView)
}.enqueue(request)

// 或
ImageRequest(context, "https://www.example.com/image.jpg") {
    placeholder(R.drawable.placeholder)
    crossfade(true)
    target(
        onStart = { requestContext, placeholder: Image? ->
            imageView.setImageDrawable(placeholder?.asDrawable())
        },
        onSuccess = { requestContext, result: Image ->
            imageView.setImageDrawable(result.asDrawable())
        },
        onError = { requestContext, error: Image? ->
            imageView.setImageDrawable(error?.asDrawable())
        }
    )
}.enqueue(request)
```

### Compose

显示到 Compose 时不需要你设置 target，AsyncImage 会设置，只需配置其它参数即可，如下：

```kotlin
AsyncIage(
    rqeuest = DisplayRequest(LocalContext.current, "https://example.com/image.jpg") {
        placeholder(R.drawable.placeholder)
        crossfade(true)
    },
    contentDescription = stringResource(R.string.description),
    contentScale = ContentScale.Crop,
    modifier = Modifier.clip(CircleShape)
)
```

### RemoteViews

Sketch 提供了 [RemoteViewsTarget] 用来将图片显示到 [RemoteViews]，如下：

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

1. 如上所示 [RemoteViewsTarget] 仅将 Drawable 转换为 Bitmap 并调用 [RemoteViews] 的
   setImageViewBitmap 方法设置 Bitmap
2. 所以还需要你在 onUpdated 函数中刷新通知或 AppWidget 才能将 Bitmap 显示到屏幕上

[Image]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/Image.kt

[Target]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/target/Target.kt

[ViewTarget]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/target/ViewTarget.kt

[ImageViewTarget]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/target/ImageViewTarget.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

[ImageResult]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageResult.kt

[RemoteViews]: https://developer.android.google.cn/reference/android/widget/RemoteViews

[RemoteViewsTarget]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/target/RemoteViewsTarget.kt