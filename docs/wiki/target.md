# Target

Translations: [简体中文](target_zh.md)

[Target] is used to display [Image] to View, Compose and any other components

### View

When displayed in View, you need to actively set the target, as follows:

```kotlin
val imageView = ImageView(context)

ImageRequest(context, "https://www.example.com/image.jpg") {
    placeholder(R.drawable.placeholder)
    crossfade(true)
    target(imageView)
}.enqueue(request)

// or
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

When displaying to Compose, you do not need to set the target. AsyncImage will set it. You only need
to configure other parameters, as follows:

```kotlin
AsyncIage(
    rqeuest = ImageRequest("https://example.com/image.jpg") {
        placeholder(R.drawable.placeholder)
        crossfade(true)
    },
    contentDescription = "photo",
)
```

### RemoteViews

Sketch provides [RemoteViewsTarget] to display images to [RemoteViews], as follows:

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

1. As shown above [RemoteViewsTarget] only converts the Drawable to Bitmap and calls the
   setImageViewBitmap method of [RemoteViews] to set the Bitmap
2. So you still need to refresh the notification or AppWidget in the onUpdated function to display
   the Bitmap on the screen.

[Image]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/Image.kt

[Target]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/target/Target.kt

[ViewTarget]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/target/ViewTarget.kt

[ImageViewTarget]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/target/ImageViewTarget.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

[ImageResult]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageResult.kt

[RemoteViews]: https://developer.android.google.cn/reference/android/widget/RemoteViews

[RemoteViewsTarget]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/target/RemoteViewsTarget.kt