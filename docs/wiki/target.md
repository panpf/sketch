# Target

Translations: [简体中文](target_zh.md)

[Target] is used to receive the result [ImageResult] of [ImageRequest] and apply the result to the
target

From the previous [Getting Started][getting_started] document, we can know that [ImageRequest] is
divided into [DisplayRequest], [LoadRequest], [DownloadRequest] Three, they all have
different [ImageResult] implementations, so [Target] also has three corresponding implementations:

* [DisplayTarget]: Receives results of Drawable type, dedicated to [DisplayRequest]
* [LoadTarget]: Receives Bitmap type results, dedicated to [LoadRequest]
* [DownloadTarget]: Receives results of type [DownloadData], dedicated to [DownloadRequest]

The following demonstrates creating a custom [DisplayTarget]:

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

> LoadTarget and DownloadTarget are used in much the same way as DisplayTarget.

[DisplayTarget] is usually used to apply Drawable to View, so Sketch provides [ViewDisplayTarget]
and [ImageViewDisplayTarget] to simplify use

[DisplayRequest] also provides the target(ImageView) method to simplify binding to ImageView, as
follows:

```kotlin
DisplayRequest(context, "https://www.example.com/image.jpg") {
    target(imageView)
}.enqueue()
```

### RemoteViews

Sketch provides [RemoteViewsDisplayTarget] to display images to [RemoteViews], as follows:

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

1. As shown above [RemoteViewsDisplayTarget] only converts the Drawable to Bitmap and calls the
   setImageViewBitmap method of [RemoteViews] to set the Bitmap
2. So you still need to refresh the notification or AppWidget in the onUpdated function to display
   the Bitmap on the screen

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