# Thumbnail

Sketch supports loading lower resolution thumbnails and original images at the same time
through [ThumbnailInterceptor]. If the thumbnail is loaded successfully first, the thumbnail will be
displayed, and then it will be switched to the original image after the original image is loaded
successfully, thereby improving the user experience, as follows:

```kotlin
ImageRequest(context, "https://www.example.com/image.jpg") {
    thumbnail("https://www.example.com/image_thumbnail.jpg")
}
```

If you want to customize the thumbnail request, you can also create a separate thumbnail
ImageRequest and pass it in:

```kotlin
ImageRequest(context, "https://www.example.com/image.jpg") {
    thumbnail(ImageRequest(context, "https://www.example.com/image_thumbnail.jpg") {
        // Configure thumbnail request here
    })
}
```

> [!IMPORTANT]
> * The thumbnail request does not need to set a target. It will automatically share the same target
    with the main request. Even if it is set, it will be replaced.
> * Thumbnail request does not trigger placeholder, fallback, error
> * Thumbnail requests will automatically block cacheKey from Target as well as Listener and
    ProgressListener
> * When using the uri method, a thumbnail request will be created based on the main request, but
    the cacheKey, Listener and ProgressListener from the main request will be blocked.


[ThumbnailInterceptor]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/internal/ThumbnailInterceptor.kt