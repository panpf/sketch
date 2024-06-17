# Change Log

Translations: [简体中文](CHANGELOG_zh.md)

# 4.0.0-alpha01

> [!CAUTION]
> 1. The 4.x version has undergone a lot of destructive reconstruction and simplification to be
     compatible with Compose Multiplatform, and is not compatible with the 3.x version.
> 2. The maven groupId is upgraded to `io.github.panpf.sketch4`, so versions 2.\* and 3.\* will not
     prompt for upgrade.

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