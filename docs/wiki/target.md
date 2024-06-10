# Target

Translations: [简体中文](target_zh.md)

The main job of [Target] is to display [Image]. It is also responsible for providing [SizeResolver], [ScaleDecider], [ResizeOnDrawHelper], [LifecycleResolver] and other properties when building [ImageRequest]. These properties will be used as default values.

## Compose

Displaying [Image] to the Compose component does not require you to actively set the target, [AsyncImage] and [AsyncImagePainter]
will be set, you only need to set other parameters, as follows:

```kotlin
AsyncIage(
    rqeuest = ImageRequest("https://example.com/image.jpg") {
        placeholder(Res.drawable.placeholder)
        crossfade()
    },
    contentDescription = "photo",
)
```

## View

When displaying [Image] to View, you need to actively set [Target]

### ImageView

[Sketch] provides [ImageViewTarget] to display images to [ImageView], as follows:

```kotlin
ImageRequest(context, "https://www.example.com/image.jpg") {
    placeholder(R.drawable.placeholder)
    crossfade()
    target(imageView)
}.enqueue(request)
```

### RemoteViews

[Sketch] also provides [RemoteViewsTarget] to display images to [RemoteViews], as follows:

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
> 1. As shown above [RemoteViewsTarget] only converts the Drawable to Bitmap and calls the setImageViewBitmap method of [RemoteViews] to set the Bitmap
> 2. So you still need to refresh the notification or AppWidget in the onUpdated function to display the Bitmap on the screen.

[Sketch]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/Sketch.kt

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