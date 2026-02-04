# Change Log

Translations: [简体中文](CHANGELOG.zh.md)

> [!CAUTION]
> 1. The 4.x version has undergone a lot of destructive reconstruction and simplification to be
     compatible with Compose Multiplatform, and is not compatible with the 3.x version.
> 2. The maven groupId is upgraded to `io.github.panpf.sketch4`, so versions 2.\* and 3.\* will not
     prompt for upgrade.
> 3. Reference [《Migration Documentation》](docs/migrate.md) migrating from 3.x to 4.x

# 4.4.0-beta01

There are no changes compared to version 4.4.0-alpha02

# 4.4.0-alpha02

* broken: By merging RequestInterceptor and DecodeInterceptor, binary compatibility is broken and
  libraries that depend on this need to modify their code and
  recompile. [#274](https://github.com/panpf/sketch/issues/274)
* fix: Fixed a bug where AsyncImage was invalid when using the colorFilter property and enabling
  crossfade. [#266](https://github.com/panpf/sketch/issues/266
* fix: Fixed a bug where ImageDecoder on Android platform cannot decode gifs with only one
  frame. [#179](https://github.com/panpf/sketch/issues/179)
* fix: Now the newFileUri(String) function skips the uri starting with '
  file:'. [#270](https://github.com/panpf/sketch/issues/270)
* fix: Fixed a bug that prevented Windows Network URIs from
  loading. [#267](https://github.com/panpf/sketch/issues/267)
* fix: Fixed a bug where HttpUriFetcher and SkiaAnimatedDecoder did not close the input
  stream. [#272](https://github.com/panpf/sketch/issues/272)
* fix: Fixed a bug where non-Android platforms used Skia to decode GIFs when the frame duration was
  0 and not modified to the default value of 100. [#273](https://github.com/panpf/sketch/issues/273)
* new: Added the function of loading thumbnail first, please refer to the documentation for
  details [Thumbnail.md](docs/thumbnail.md). [#241](https://github.com/panpf/sketch/issues/241)

# 4.4.0-alpha01

* improve: DecodeConfig is now accessible from the common module
* new: Added the 'sketch-blurhash' module to support BlurHash. Please refer to the documentation for
  details. [BlurHash](docs/blurhash.md). [#261](https://github.com/panpf/sketch/issues/261)

# 4.3.1

* fix: Fixed a bug that returned null when ThumbnailMemoryCacheStateImage starts with version
  4.3.0. [#263](https://github.com/panpf/sketch/issues/263)

# 4.3.0

* fix: Fixed a bug that caused the App to crash or image loading failure when
  ThumbnailMemoryCacheStateImage to encounter memory cache without
  ImageInfo. [#259](https://github.com/panpf/sketch/issues/259)
* fix: Fixed a bug where MemoryCacheRequestInterceptor failed to load images when they encountered a
  cache without ImageInfo. [#260](https://github.com/panpf/sketch/issues/260)
* break: 'sketch-compose-koin', 'sketch-view-koin', 'sketch-koin' The file name of the module has
  been changed from Singleton to Koin, and binary compatibility is corrupted. The library that
  depends on this may need to be recompiled.
* new: MemoryCache added entries() method to get all cache entries
* improve: Improve the performance of ThumbnailMemoryCacheStateImage, now iterates through all
  memory caches by default and improves the judgment accuracy of thumbnail images.

# 4.2.0

> [!NOTE]
> There is no change compared to the 4.2.0-beta02 version

* fix: Fixed a bug that did not update download progress when using sketch-http-ktor3 and
  sketch-http-ktor2. [#253](https://github.com/panpf/sketch/issues/253)
* fix: Fixed the bug where AsyncImageState can still receive the cancel status of the old request
  after canceling the old request due to the new request.
* change: Deprecate AsyncImage with keepContentNoneStartOnDraw parameter
* change: Deprecate the fitScale property of TransitionTarget, adding ViewTarget.scaleType
  and ComposeTarget.contentsScale and ComposeTarget.alignment instead of fitScale
* change: AnimatedImage is now an interface, and its implementation class is SkiaAnimatedImage
* improve: Thin request logs and remove information that is not related to loading
* new: CrossfadePainter now supports contentScale and alignment
* new: ResizePainter now supports contentScale and alignment
* new: CrossfadeDrawable now supports scaleType
* new: ResizeDrawable now supports scaleType
* new: Added `sketch-compose-koin` and `sketch-view-koin` modules to provide Koin dependency
  injection support. [#256](https://github.com/panpf/sketch/issues/256)
* new: Added `Sketch(PlatformContext, (Sketch.Builder.() -> Unit)? = null)` function and
  `Sketch.Builder.logger(Logger)` function
* new: ImageRequest, ImageOptions and ImageResult.Success add downloadCacheKey,
  downloadCacheKeyMapper, resultCacheKey,
  resultCacheKeyMapper, memoryCacheKey, memoryCacheKeyMapper
  properties. [#255](https://github.com/panpf/sketch/issues/255)

# 4.2.0-beta02

* new: Added `sketch-compose-koin` and `sketch-view-koin` modules to provide Koin dependency
  injection support. [#256](https://github.com/panpf/sketch/issues/256)
* new: Added `Sketch(PlatformContext, (Sketch.Builder.() -> Unit)? = null)` function and
  `Sketch.Builder.logger(Logger)` function
* new: ImageRequest, ImageOptions and ImageResult.Success add downloadCacheKey,
  downloadCacheKeyMapper, resultCacheKey,
  resultCacheKeyMapper, memoryCacheKey, memoryCacheKeyMapper
  properties. [#255](https://github.com/panpf/sketch/issues/255)

# 4.2.0-beta01

* fix: Fixed a bug that did not update download progress when using sketch-http-ktor3 and
  sketch-http-ktor2. [#253](https://github.com/panpf/sketch/issues/253)
* fix: Fixed the bug where AsyncImageState can still receive the cancel status of the old request
  after canceling the old request due to the new request.
* change: Deprecate AsyncImage with keepContentNoneStartOnDraw parameter
* change: Deprecate the fitScale property of TransitionTarget, adding ViewTarget.scaleType
  and ComposeTarget.contentsScale and ComposeTarget.alignment instead of fitScale
* change: AnimatedImage is now an interface, and its implementation class is SkiaAnimatedImage
* improve: Thin request logs and remove information that is not related to loading
* new: CrossfadePainter now supports contentScale and alignment
* new: ResizePainter now supports contentScale and alignment
* new: CrossfadeDrawable now supports scaleType
* new: ResizeDrawable now supports scaleType

# 4.1.0

> [!CAUTION]
> The compose multiplatform version 1.8.0 must be jvm 11 to compile. Please modify the configuration
> of sourceCompatibility, targetCompatibility, jvmTarget, etc. to 11+

* fix: Fixed a bug where CrossfadePainter, ResizePainter, ImageBitmapPainter and DrawablePainter
  cannot fill the canvas due to loss of floating point precision
* new: AsyncImage Adds the keepContentNoneStartOnDraw param
* depend: Update to kotlin 2.1.10
* depend: Update to compose multiplatform 1.8.0
* depend: Update to android-gif-drawable 1.2.29
* depend: Update to ffmpegMediaMetadataRetriever 1.0.19
* depend: Update to jetbrains-lifecycle 2.8.4
* depend: Update to kotlinx-coroutines 1.10.2
* depend: Update to ktor2 2.3.13
* depend: Update to ktor3 3.0.3
* depend: Update to okio 3.11.0
* depend: Don't replace `kotlin-stdlib-jdk7` and `kotlin-stdlib-jdk8` with `kotlin-stdlib`

# 4.0.6

compose:

* improve: AsyncImage now no longer crashes when the window size is 0, but is a minimum of
    100. [#244](https://github.com/panpf/sketch/issues/244

# 4.0.5

fetch:

* fix: Fixed a bug in FileUriFetcher that failed to load the windows file
  path. [#239](https://github.com/panpf/sketch/issues/239)

# 4.0.4

compose:

* fix: Fixed a bug where AsyncImage and SubcomposeAsyncImage could not load images when component
  size is 0. [#236](https://github.com/panpf/sketch/issues/236)
* improve: Restore AsyncImage and SubcomposeAsyncImage function to replace window container size if
  width or height is 0 when setting size

# 4.0.3

* fix: Fixed a bug that SvgDecoder could not recognize when there was comment on the head of the svg
  file. [#232](https://github.com/panpf/sketch/issues/232)

# 4.0.2

compose:

* fix: The AsyncImage component is reloaded when the current window size
  changes. [#231](https://github.com/panpf/sketch/issues/231)

# 4.0.1

android:

* fix: Fixed a bug where Resources were not set when BitmapImage was converted to BitmapDrawable,
  causing the Intrinsic size of BitmapDrawable to be inconsistent with
  Bitmap. [#226](https://github.com/panpf/sketch/issues/226)

# 4.0.0 Stable

Sketch:

* change: SketchSingleton refactored into SingletonSketch
* change: Sketch's execute(DownloadRequest) and enqueue(DownloadRequest) methods are refactored into
  executeDownload(ImageRequest) and enqueueDownload(ImageRequest)
* new: Sketch.Builder and ComponentRegistry.Builder added addComponents() function
* new: Sketch.Builder adds networkParallelismLimited() and decodeParallelismLimited()
  Method to control the number of network and decoding
  concurrency. [#200](https://github.com/panpf/sketch/issues/200)
* new: Added ComponentLoader, which supports automatic detection and registration of Fetcher and
  Decoder components. All components in the built-in module are supported.

request:

* change: There is no longer a distinction between Display, Load and Download, now there is only one
  ImageRequest, ImageResult and ImageListener
* change: The requestKey attribute of ImageResult has been removed, and the requestCacheKey
  attribute has been renamed to cacheKey.
* change: Now Target, ImageResult, DecodeResult all use Image
* change: SketchDrawable's imageUri, requestKey, requestCacheKey, imageInfo, dataFrom,
  transformedList, extras and other attributes have been removed, now please get them from
  ImageResult
* change: depth and depthFrom properties merged into DepthHolder
* change: Android platform-specific APIs such as bitmapConfig, colorSpace, preferQualityOverSpeed,
  placeholder(Int), fallback(Int), error(Int), etc. are provided in the form of extension functions
* change: resizeApplyToDrawable renamed to resizeOnDraw
* change: Parameters renamed to Extras
* change: LongImageClipPrecisionDecider renamed to LongImagePrecisionDecider,
  LongImageStartCropScaleDecider renamed to LongImageScaleDecider
* remove: Removed listener() and progressListener() methods, now use addListener() and
  addProgressListener() methods
* new: Added 'sizeMultiplier: Float' attribute for setting the scaling ratio of image size
* new: Added 'allowNullImage: Boolean' attribute

fetch:

* change: The parameter passed in Fetcher.Factory.create() is changed to RequestContext
* new: ResourceUriFetcher supports 'android.resource:///drawable/ic_launcher' and 'android.resource:
  ///1031232' uris
* new: AssetUriFetcher supports 'file:///android_asset/' uri

source:

* change: Refactor DataSource

decode:

* change: BitmapDecoder and DrawableDecoder merged into Decoder
* change: Decoder's decode() method removes the suspend modifier and changes the return type from
  Result<DecodeResult> to DecodeResult
* change: BitmapDecodeInterceptor and DrawableDecodeInterceptor merged into DecodeInterceptor
* change: BitmapConfig refactored to BitmapColorType
* improve: Improve the accuracy of animation-related Decoder in determining the image type. No
  longer rely on the mimeType attribute of FetchResult because it may be inaccurate.
* new: DrawableDecoder supports colorSpace

transformation:

* fix: Fix the bug that CircleCrop, Rotate, RoundedCorners Transformation does not work at
  RGB_565. [#209](https://github.com/panpf/sketch/issues/209)
* fix: Fixed the bug that Transformations such as blur and rotate on the Android platform did not
  keep the ColorSpace unchanged. [#213](https://github.com/panpf/sketch/issues/213)
* change: Transformation's transform() method removes the suspend modifier

cache:

* remove: Remove BitmapPool and its related disallowReuseBitmap attribute, CountBitmap, and
  SketchCountBitmapDrawable classes
* change: Refactor DiskCache SnapShot and Editor, get() and edit() are changed to openSnapShot() and
  openEditor(), and openSnapShot() and openEditor() of the same key now conflict with each other,
  openEditor always returns null before openSnapshot is closed.
* change: Refactor MemoryCache.Value

state:

* change: uriEmpty property of ImageRequest and ImageOptions renamed to fallback
* change: The type of error attribute of ImageRequest and ImageOptions changed from ErrorStateImage
  to StateImage
* change: ErrorStateImage removes ErrorStateImage.Builder.uriEmptyError() method and refactors it
  into ConditionStateImage
* improve: Improve IconDrawable, support fixed-size background and restrict icons to have fixed
  sizes or specify iconSize

animated:

* fix: Fixed the bug that GifDrawable and MovieDrawable could not apply animatedTransformation
  correctly. [#214](https://github.com/panpf/sketch/issues/214)
* fix: Fixed the bug that the repeatCount setting of GifDrawableDecoder should be increased by 1 by
  mistake. [#215](https://github.com/panpf/sketch/issues/215)
* change: Split the sketch-animated module into sketch-animated-core and sketch-animated-gif,
  sketch-animated-webp, sketch-animated-heif, sketch-animated-koralgif module and rename it to
  sketch-animated-gif-koral

http:

* remove: Removed setHttpHeader() and addHttpHeader() methods of DisplayRequest.Builder and
  DisplayOptions.Builder
* change: The http function is split into separate modules. The sketch-http, sketch-http-hurl,
  sketch-http-ktor2, and sketch-http-ktor3 modules are added. The sketch-okhttp module is renamed
  sketch-http-okhttp.

compose:

* fix: Fix the bug that the filterQuality parameter of AsyncImage is
  invalid. [#211](https://github.com/panpf/sketch/issues/211)
* remove: AsyncImage composable function removes placeholder, error, uriEmpty, onLoading, onSuccess,
  onError parameters
* new: AsyncImageState can now set ImageOptions, for example: 'rememberAsyncImageState {
  ImageOptions() }'
* new: Added ErrorStateImage.Builder.saveCellularTrafficError(DrawableResource) extension function
* new: ImageRequest.Builder and ImageOptions.Builder add size(IntSize) extension function
* new: AsyncImageState adds onPainterState and onLoadState properties, which are used to receive
  PainterState and LoadState updates in the form of callbacks.
* new: Added ComposableImageRequest() and ComposableImageOptions() functions, which can be used
  directly on placeholder, error, and fallback methods using DrawableResource

view:

* fix: Fix the bug that the image cannot be loaded when the ImageView is attached to the window but
  size is null due to padding. [#208](https://github.com/panpf/sketch/issues/208)
* change: displayImage renamed to loadImage
* change: ImageView.disposeDisplay() renamed to ImageView.disposeLoad()
* new: ImageRequest.Builder and ImageOptions.Builder add sizeWithView() and sizeWithDisplay()
  extension functions

extensions:

* fix: Fix the bug of exception when AppIconUriFetcher.Factory parses
  versionCode. [#204](https://github.com/panpf/sketch/issues/204)
* change: Split the sketch-extensions-apkicon and sketch-extensions-appicon modules from the
  sketch-extensions-core module

other:

* upgrade: Android minimum API raised to API 21
* depend: Upgrade kotlin 2.0.21, kotlinx coroutines 1.9.0
* depend: Upgrade jetbrains compose 1.7.0, jetbrains lifecycle 2.8.3

# v4.0.0-rc01

compose:

* new: AsyncImageState adds onPainterState and onLoadState properties, which are used to receive
  PainterState and LoadState updates in the form of callbacks.

# v4.0.0-beta03

core:

* new: Added Image.asBitmap(), asBitmapOrNull(), asDrawableOrNull(), asPainterOrNull() extension
  functions
* improve: The defaultImage property of ConditionStateImage and ComposableConditionStateImage is now
  nullable

decode:

* improve: Improve to determine whether regional decoding is supported based on mimeType. Non-image
  types directly return false. On non-Android platforms, based on the skiko version, it is
  determined whether heic, heif, and avi types are supported.

fetch:

* improve: AssetUriFetcher and ResourceUriFetcher are now compatible with the sketch3 protocol

# 4.0.0-beta02

core:

* fix: Fixed a bug that caused REMOVE records to be lost due to disk cache being killed, and
  re-initialization did not verify whether the file existed, causing size()
  exceptions. [#219](https://github.com/panpf/sketch/issues/219)

animated:

* fix: Fixed the bug that the dependency of sketch-animated-heif accidentally included the local
  test module. [#220](https://github.com/panpf/sketch/issues/220)

# 4.0.0-beta01

core:

* remove: Remove Image.getPixels()
* remove: Remove the AnyThread, MainThread, IntRange, and IntDef annotations under the '
  com.github.panpf.sketch.annotation' package and use the alternatives under the '
  androidx.annotation' package
* change: Merge AndroidBitmapImage and SkiaBitmapImage into BitmapImage
* change: DrawableEqualizer renamed to EquitableDrawable
* change: Remove AndroidBitmap, SkiBitmap, SkiaImageInfo, SkiaImage, SkiaRect
* change: SkiaAnimatedImage renamed to AnimatedImage
* change: ImageRequest's registerListener and registerProgressListener methods renamed to
  addListener and addProgressListener
* new: Added ComponentLoader, which supports automatic detection and registration of Fetcher and
  Decoder components. All components in the built-in module are supported.

compose:

* fix: Fix the bug that the filterQuality parameter of AsyncImage is
  invalid. [#211](https://github.com/panpf/sketch/issues/211)
* remove: Remove ComposeBitmapImage
* remove: Remove PainterState.Empty
* change: PainterEqualizer renamed to EquitablePainter
* change: ComposeImagePainter renamed to ImageBitmapPainter, SkiaAnimatedImagePainter renamed to
  AnimatedImagePainter

view:

* fix: Fix the bug that the image cannot be loaded when the ImageView is attached to the window but
  size is null due to padding. [#208](https://github.com/panpf/sketch/issues/208)

decode:

* change: Decoder's decode() method removes the suspend modifier and changes the return type from
  Result<DecodeResult> to DecodeResult
* change: BitmapConfig refactored to BitmapColorType
* new: Non-Android platforms now also support ColorType
* new: Non-Android platforms now also support ColorSpace
* new: DrawableDecoder supports colorSpace

fetch:

* change: The parameter passed in Fetcher.Factory.create() is changed to RequestContext

cache:

* change: SkiaBitmapImage is now cached in the memory cache on non-Android platforms, not
  ComposeBitmapImage.
* change: The default memory cache size is now 256MB for desktop and web platforms and 128MB for ios
  platforms

http:

* remove: Removed the setHttpHeader() and addHttpHeader() methods of ImageRequest.Builder and
  ImageOptions.Builder
* change: Remove the sketch-http-core module, add the sketch-http-hurl module, rename the
  sketch-http-ktor module to sketch-http-ktor2 and no longer support wasmJs, add the
  sketch-http-ktor3 module

animated:

* fix: Fixed the bug that on non-Android platforms, animations set repeatCount and do not stay on
  the last frame after playing, but stay on the first
  frame. [#212](https://github.com/panpf/sketch/issues/212)
* fix: Fixed the bug that GifDrawable and MovieDrawable could not apply animatedTransformation
  correctly. [#214](https://github.com/panpf/sketch/issues/214)
* fix: Fixed the bug that the repeatCount setting of GifDrawableDecoder should be increased by 1 by
  mistake. [#215](https://github.com/panpf/sketch/issues/215)
* change: Split the sketch-animated module into sketch-animated-core and sketch-animated-gif,
  sketch-animated-webp, sketch-animated-heif, sketch-animated-koralgif module and rename it to
  sketch-animated-gif-koral
* improve: animatedTransformation now supports non-Android platforms

transformation:

* fix: Fix the bug that CircleCrop, Rotate, RoundedCorners Transformation does not work at
  RGB_565. [#209](https://github.com/panpf/sketch/issues/209)
* fix: Fixed the bug that Transformations such as blur and rotate on the Android platform did not
  keep the ColorSpace unchanged. [#213](https://github.com/panpf/sketch/issues/213)
* change: Transformation's transform() method removes the suspend modifier

state:

* change: The type of error attribute of ImageRequest and ImageOptions changed from ErrorStateImage
  to StateImage
* change: ErrorStateImage is refactored into ConditionStateImage, and ConditionStateImage can be
  used in placeholder and fallback
* improve: Improve IconDrawable, support fixed-size background and restrict icons to have fixed
  sizes or specify iconSize
* improve: Improve IconPainter, support fixed-size background and restrict icons to have fixed sizes
  or specify iconSize
* new: Added 'Drawable.asStateImage(Any)' and 'ColorDrawable.asStateImage()' extension functions

extensions:

* fix: Fixed the bug that the equals method of RingProgressDrawable, SectorProgressDrawable,
  RingProgressPainter, SectorProgressPainter did not execute as
  expected. [#210](https://github.com/panpf/sketch/issues/210)
* change: Split the sketch-extensions-apkicon and sketch-extensions-appicon modules from the
  sketch-extensions-core module

other:

* depend: Upgrade kotlin 2.0.21, kotlinx coroutines 1.9.0
* depend: Upgrade jetbrains compose 1.7.0, jetbrains lifecycle 2.8.3

# 4.0.0-alpha08

* fix: Fixed the bug that the ComposableImageRequest() and ComposableImageOptions() functions cannot
  listen and update the Compose State. [#207](https://github.com/panpf/sketch/issues/207)
* fix: Fix the bug that CircleCrop, Rotate, RoundedCorners Transformation does not work at
  RGB_565. [#209](https://github.com/panpf/sketch/issues/209)
* remove: Remove SkiaExifOrientationHelper
* remove: DataSource removes sketch and request attributes
* change: DataSource's getFile() and getFileOrNull() methods add Sketch parameters
* change: DataSource's openSourceOrNull() and getFileOrNull() methods are now available as extension
  functions
* change: RequestContext moved to 'com.github.panpf.sketch.request' package
* new: SkiaBitmapImage now supports memory caching
* new: DataSource adds key attribute

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

* change: ImageView.disposeDisplay() renamed to ImageView.disposeLoad()
* new: Added ImageRequest.Builder.composableError() and ImageOptions.Builder.composableError()
  extension functions
* new: Added ErrorStateImage.Builder.saveCellularTrafficError(DrawableResource) extension function

# 4.0.0-alpha01

Sketch:

* change: SketchSingleton refactored into SingletonSketch

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

* remove: Remove BitmapPool and its related disallowReuseBitmap attribute, CountBitmap, and
  SketchCountBitmapDrawable classes
* change: Refactor DiskCache SnapShot and Editor, get() and edit() are changed to openSnapShot() and
  openEditor(), and openSnapShot() and openEditor() of the same key now conflict with each other,
  openEditor always returns null before openSnapshot is closed.
* change: Refactor MemoryCache.Value

state:

* change: uriEmpty attribute of ImageRequest and ImageOptions renamed to fallback
* remove: Delete ErrorStateImage.Builder.uriEmptyError()

compose:

* remove: AsyncImage composable function removes placeholder, error, uriEmpty, onLoading, onSuccess,
  onError parameters
* upgrade：Compose Multiplatform upgraded to 1.6.10
* new: AsyncImageState can now set ImageOptions, for example: 'rememberAsyncImageState {
  ImageOptions() }'

view:

* change: displayImage renamed to loadImage

other:

* upgrade：Android minimum API raised to API 21
* upgrade：kotlin is upgraded to 2.0.0, mainly to support Compose Multiplatform 1.6.10