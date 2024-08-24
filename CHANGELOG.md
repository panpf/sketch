# Change Log

Translations: [简体中文](CHANGELOG_zh.md)

> [!CAUTION]
> 1. The 4.x version has undergone a lot of destructive reconstruction and simplification to be
     compatible with Compose Multiplatform, and is not compatible with the 3.x version.
> 2. The maven groupId is upgraded to `io.github.panpf.sketch4`, so versions 2.\* and 3.\* will not
     prompt for upgrade.

# new

* fix: Fixed the bug that the ComposableImageRequest() and ComposableImageOptions() functions cannot
  listen and update the Compose State. [#207](https://github.com/panpf/sketch/issues/207)

# 4.0.0-alpha07

* improve: SkiaAnimatedImagePainter now does not share Bitmap for decoding and drawing to avoid
  cluttered images. [#206](https://github.com/panpf/sketch/issues/206)
* improve: SkiaAnimatedImagePainter now still does not start animation when drawing, it will
  automatically load the first frame
* new: SkiaAnimatedImagePainter now supports caching frames that take longer to decode than the
  frame duration to improve playback smoothness, but this uses more memory, closed by default,
  enabled through 'cacheDecodeTimeoutFrame()' function

# 4.0.0-alpha06

* fix: Fixed a bug where SkiaAnimatedImagePainter crashed when encountering disguised gif
  images. [#205](https://github.com/panpf/sketch/issues/205)
* improve: Improve the accuracy of animation-related Decoder in determining the image type. No
  longer rely on the mimeType attribute of FetchResult because it may be inaccurate.

# 4.0.0-alpha05

* fix: Fix the bug of exception when AppIconUriFetcher.Factory parses
  versionCode. [#204](https://github.com/panpf/sketch/issues/204)
* change: KotlinResourceUriFetcher supported uri changed from 'kotlin.resource://'
  to 'file:///kotlin_resource/'
* change: ComposeResourceUriFetcher supported uri changed from 'compose.resource://'
  to file:///compose_resource/
* change: The uri supported by ResourceUriFetcher has been changed from 'android.resource:
  //resource?resType=drawable&resName=ic_launcher' and 'android.resource://resource?resId=1031232'
  to 'android.resource:///drawable/ic_launcher' and ' android.resource:///1031232'
* change: AssetUriFetcher supported uri changed from 'asset://' to 'file:///android_asset/'
* change: ImageRequest.uri property type changed from String to Uri
* improve: All implementation classes of DataSource implement equals and hashCode methods
* improve: All implementation classes of Fetcher implement equals and hashCode methods

# 4.0.0-alpha04

* fix: Fixed the issue where HurlStack and OkHttpStack failed to control network concurrency due to
  switching threads again. [#199](https://github.com/panpf/sketch/issues/199)
* fix: Fixed the bug that decoding pictures will not start until all network tasks are
  completed. [#201](https://github.com/panpf/sketch/issues/201)
* change: The mergeComponents() method of ImageOptions and ImageRequest has been renamed to
  addComponents()
* change: The drawableResId parameter of ImageView.loadImage() and newResourceUri() functions was
  renamed to resId
* change: AndroidLogPipeline and PrintLogPipeline are changed to singleton mode
* improve: Check the type of PlatformContext to prevent passing Activity to Sketch or ImageRequest
* new: Add PlatformContext.sketch and View.sketch extension functions
* new: Sketch.Builder and ComponentRegistry.Builder added addComponents() function
* new: Sketch.Builder adds networkParallelismLimited() and decodeParallelismLimited() methods to
  control the number of network and decoding
  concurrency. [#200](https://github.com/panpf/sketch/issues/200)

# 4.0.0-alpha03

* change: ComposeBitmapValue renamed to ComposeBitmapImageValue
* change: Refactor Sketch.enqueueDownload() and executeDownload() and move from
  sketch-extensions-core module to sketch-core module
* change: LongImageClipPrecisionDecider renamed to LongImagePrecisionDecider,
  LongImageStartCropScaleDecider renamed to LongImageScaleDecider
* change: Revert to using screen size as final Size when building ImageRequest
* improve: Painter.asSketchImage() now returns PainterImage; ComposeBitmap.asSketchImage() now
  returns ComposeBitmapImage
* new: ImageRequest.Builder and ImageOptions.Builder add sizeWithView(), sizeWithDisplay(), size(
  IntSize) extension functions

# 4.0.0-alpha02

* change: ImageView.disposeLoad() renamed to ImageView.disposeLoad()
* new: Added ImageRequest.Builder.composableError() and ImageOptions.Builder.composableError()
  extension functions
* new: Added ErrorStateImage.Builder.saveCellularTrafficError(DrawableResource) extension function

# 4.0.0-alpha01

### sketch-core

request:

* change: There is no longer a distinction between Display, Load and Download, now there is only one
  ImageRequest, ImageResult and ImageListener
* change: The requestKey attribute of ImageResult has been removed, and the requestCacheKey
  attribute has been renamed to cacheKey.
* change: Now Target, ImageResult, DecodeResult all use Image
* change: SketchDrawable's imageUri, requestKey, requestCacheKey, imageInfo, dataFrom,
  transformedList, extras and other properties have been removed, now please get them from
  ImageResult
* change: depth and depthFrom properties merged into DepthHolder
* change: Android platform-specific APIs such as bitmapConfig, colorSpace, preferQualityOverSpeed,
  placeholder(Int), fallback(Int), error(Int), etc. are provided in the form of extension functions
* change: resizeApplyToDrawable renamed to resizeOnDraw
* change: Parameters renamed to Extras
* new: Added 'sizeMultiplier: Float' attribute to set the scaling ratio of the image size
* new: Added 'allowNullImage: Boolean' attribute

decode:

* change: BitmapDecoder and DrawableDecoder merged into Decoder
* change: BitmapDecodeInterceptor and DrawableDecodeInterceptor merged into DecodeInterceptor

cache:

* delete: Remove BitmapPool and its related disallowReuseBitmap attribute, CountBitmap, and
  SketchCountBitmapDrawable classes
* change: Refactor DiskCache SnapShot and Editor, get() and edit() are changed to openSnapShot() and
  openEditor(), and openSnapShot() and openEditor() of the same key now conflict with each other,
  openEditor always returns null before openSnapshot is closed.
* change: Refactor MemoryCache.Value

state:

* change: uriEmpty attribute of ImageRequest and ImageOptions renamed to fallback
* delete: Delete ErrorStateImage.Builder.uriEmptyError()

other:

* change: SketchSingleton refactored into SingletonSketch
* change: displayImage renamed to loadImage

### sketch-compose

* delete: AsyncImage composable function removes placeholder, error, uriEmpty, onLoading, onSuccess,
  onError parameters
* upgrade：Compose Multiplatform upgraded to 1.6.10
* new: AsyncImageState can now set ImageOptions, for example: 'rememberAsyncImageState {
  ImageOptions() }'

### other

* upgrade：Android minimum API raised to API 21
* upgrade：kotlin is upgraded to 2.0.0, mainly to support Compose Multiplatform 1.6.10