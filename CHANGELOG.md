# v3.2.1-beta02

### sketch

* fix: Again try fixing child thread exceptions may not catch bugs that cause your app to
  crash [#176](https://github.com/panpf/sketch/issues/176)
* fix: When the merged() method of ImageOptions has its own componentRegistry as null and other’s
  componentRegistry is not null, the returned componentRegistry is still null
* fix: Fixed an issue where AnimatableDrawable could not be played when used as a placeholder when
  resizeApplyToDrawable was turned on
* change: Remove HttpStack.getResponse() method suspend modifier
* change: BitmapDecoder, DrawableDecoder, Fetcher, BitmapDecodeInterceptor,
  DrawableDecodeInterceptor, RequestInterceptor return results wrapped in Result
* improve: IOException is no longer thrown when deleting disk cache files fails

# v3.2.1-beta01

### sketch

* fix: Fixing child thread exceptions may not catch bugs that cause your app to
  crash [#176](https://github.com/panpf/sketch/issues/176)

# v3.2.0 stable

### sketch

* change: The DataSource's newInputStream() and file() methods are now split into
  BasedStreamDataSource and BasedFileDataSource
* new: Added supportAnimatedGif(), supportAnimatedWebp(), supportAnimatedHeif() for configuring gif
  in a more understandable way

### sketch-compose

* fix: Fixed a bug where the AsyncImage function was not compatible with compose version 1.3.1
* improve: Deprecate imageUri plus configBlock version of AsyncImage, add only the imageUri version
  instead
* upgrade: Upgrade compose 1.3.1

### sketch-extensions

* change: The deprecated AppIconBitmapDecoder does not need to be configured anymore
* new: Added supportAppIcon() for configuring app icons in a more understandable way
* new: Added supportApkIcon() for configuring apk icons in a more understandable way

### sketch-gif-koral

* new: Added supportKoralGif() for configuring gif in a more understandable way

### sketch-gif-movie

* new: Added supportMovieGif() for configuring gif in a more understandable way

### sketch-svg

* new: Added supportSvg() for configuring svg in a more understandable way

### sketch-video

* new: Added supportVideoFrame() for configuring video frame in a more understandable way

### sketch-video-ffmpeg

* new: Added supportFFmpegVideoFrame() for configuring video frame in a more understandable way

### other

* improve: Improved implementation of the equals method, which now always returns false when
  compared to subclasses
* upgrade: Upgrade compile sdk to 33
* upgrade: Upgrade androidx activity 1.6.1, annotation 1.5.0, appcompat 1.6.0, compose animation and
  ui 1.3.3, core 1.9.0, exifinterface 1.3.5, fragment 1.5.5

# v3.2.0-beta02

* upgrade: Upgrade androidx activity 1.6.1, annotation 1.5.0, appcompat 1.6.0, compose animation and
  ui 1.3.3, core 1.9.0, exifinterface 1.3.5, fragment 1.5.5

# v3.2.0-beta01

### sketch

* change: The DataSource's newInputStream() and file() methods are now split into
  BasedStreamDataSource and BasedFileDataSource
* new: Added supportAnimatedGif(), supportAnimatedWebp(), supportAnimatedHeif() for configuring gif
  in a more understandable way

### sketch-compose

* fix: Fixed a bug where the AsyncImage function was not compatible with compose version 1.3.1
* improve: Deprecate imageUri plus configBlock version of AsyncImage, add only the imageUri version
  instead
* upgrade: Upgrade compose 1.3.1

### sketch-extensions

* change: The deprecated AppIconBitmapDecoder does not need to be configured anymore
* new: Added supportAppIcon() for configuring app icons in a more understandable way
* new: Added supportApkIcon() for configuring apk icons in a more understandable way

### sketch-gif-koral

* new: Added supportKoralGif() for configuring gif in a more understandable way

### sketch-gif-movie

* new: Added supportMovieGif() for configuring gif in a more understandable way

### sketch-svg

* new: Added supportSvg() for configuring svg in a more understandable way

### sketch-video

* new: Added supportVideoFrame() for configuring video frame in a more understandable way

### sketch-video-ffmpeg

* new: Added supportFFmpegVideoFrame() for configuring video frame in a more understandable way

### other

* improve: Improved implementation of the equals method, which now always returns false when
  compared to subclasses
* upgrade: Upgrade compile sdk to 33

# v3.1.0 stable

### sketch

BitmapPool:

* fix: Fixed the bug that the put method of LruBitmapPool failed to put the second bitmap when
  encountering a bitmap with the same size and config but not the same instance
* improve: Improve LruBitmapPool

Decode:

* fix: Fixed BitmapPool will still be accessed when disallowReuseBitmap is true
* improve: Improve freeBitmap performance and reduce main thread lag
* new: BitmapDecodeInterceptor, DrawableDecodeInterceptor support sorting

Drawable:

* improve: Improve ScaledAnimatedImageDrawable

HTTP:

* change: Add the suspend keyword to the getResponse() method of HttpStack #167
* new: HurlStack and OkHttpStack now support enabling protocols such as TLS 1.1 and 1.2

MemoryCache:

* change: MemoryCache now stores MemoryCache.Value
* change: MemoryCache adds keys() method

DiskCache:

* fix: Fixed bug where process names could not be correctly obtained in APi 17 and later
* fix: Fixed a bug where the GET method of LruDiskCache throws NullPointException
* improve: LruDiskCache will now automatically delete cache records of lost files

Request:

* fix: Fixed a bug where equals to ImageRequest and their newRequest() result in false
* fix: Fixed a bug in RequestInterceptor where modifying SizeResolver does not take effect
* fix: Fixed the bug that the newRequest method of ImageRequest would nest one more layer of
  listener and progressListener every time it called
* improve: Improve the request key and cacheKey
* new: RequestInterceptor support sorting

Resize:

* remove: Remove fixedPrecision(), longImageClipPrecision(), fixedScale(), longImageScale()
  functions
* change: To refactor the Resize
* change: Simplification resize() overloading method of ImageOptions and ImageRequest
* change: resize precision default changed to always LESS_PIXELS
* change: ResizeDrawable now uses Size and Scale directly
* improve: LongImageScaleDecider construction parameters now have default values

StateImage:

* change: Now getDrawable for the placeholder and error StateImage is null and no longer calls
  setImageDrawable for the ImageView
* new: Added ThumbnailMemoryCacheStateImage #166
* new: Now the final state Drawable implements the SketchStateDrawable interface

Other:

* remove: Remove BitmapInfo
* improve: Reduce w-level logs to avoid performance impact due to logs
* improve: Improve Equals and hashCode
* improve: Improve the code
* improve: Improve the log

### sketch-compose

* fix: Fixed bug where AsyncImage and SubcomposeAsyncImage would cause constant recomposition
* fix: Fixed AsyncImage and AsyncImagePainter always change resize precision to EXACTLY bug
* fix: Fixed bug that AsyncImagePainter did not inherit CrossfadeTransition.Factory.fadeStart
  property
* fix: Fixed bug where AsyncImagePainter would ignore nested Drawables when updating display count
* improve: AsyncImage now uses dedicated DisplayTarget, SizeResolver, ScaleDecider
* build: Upgraded appcompanist to 0.25.1

### sketch-extensions

* change: Replace PauseLoadWhenScrollingDisplayInterceptor with
  PauseLoadWhenScrollingDrawableDecodeInterceptor, And remove pauseLoadWhenScrollingError()

### sketch-gif-movie

* improve: Improve MovieDrawable

### sketch-gif-koral

* improve: Improve GifDrawableDrawableDecoder
* improve: To unify various Gif Drawable logic MovieDrawable no longer uses BitmapPool

### sketch-zoom

* improve: Improve SketchZoomImageView scroll bar style
* fix: Fixed the bug that SketchZoomImageView will still access MemoryCache when the read or write
  of memoryCachePolicy is not ENABLED
* fix: Fixed a bug where SketchZoomImageView would also trigger subsampling when encountering a
  state image
* fix: Fixed a bug where SketchZoomImageView might sometimes sample tiles not loading
* fix: Fixed the bug that when SketchZoomImageView is nested in ViewPager, it cannot return the
  swipe event to ViewPager when it reaches the edge and then swipes
* change: Refactor Scales. Scales rename to ScaleState，SketchImageView's scalesFactory property
  rename to scaleStateFactory
* improve: Improve zoom

### other

* build: compileSdk upgraded to 32
* build: Upgrade android build plugin 7.3.1 and kotlin 1.7.20

# v3.1.0-rc02

### sketch

* fix: Fixed bug where process names could not be correctly obtained in APi 17 and later
* improve: To unify various Gif Drawable logic MovieDrawable no longer uses BitmapPool
* new: RequestInterceptor, BitmapDecodeInterceptor, DrawableDecodeInterceptor support sorting
* new: HurlStack and OkHttpStack now support enabling protocols such as TLS 1.1 and 1.2

### sketch-zoom

* fix: Fixed a bug that caused SketchZoomImageView to run out of memory when swiping quickly
* improve: Improve SketchZoomImageView scroll bar style

# v3.1.0-rc01

### sketch

* fix: Fixed bug where requestKey did not decode
* fix: Fixed a bug where the GET method of LruDiskCache throws NullPointException
* change: To refactor the Resize
* change: ResizeDrawable now uses Size and Scale directly

# v3.1.0-beta04

### sketch

Request:

* fix: Fixed a bug where equals to ImageRequest and their newRequest() result in false
* fix: Fixed a bug in RequestInterceptor where modifying SizeResolver does not take effect
* fix: Fixed the bug that the newRequest method of ImageRequest would nest one more layer of
  listener and progressListener every time it called
* improve: Improve the request key and cacheKey

Resize:

* change: Simplification resize() overloading method of ImageOptions and ImageRequest
* change: resize precision default changed to always LESS_PIXELS

DiskCache:

* improve: LruDiskCache will now automatically delete cache records of lost files

StateImage:

* change: Now getDrawable for the placeholder and error StateImage is null and no longer calls
  setImageDrawable for the ImageView

Other:

* improve: LongImageScaleDecider construction parameters now have default values
* improve: Reduce w-level logs to avoid performance impact due to logs
* improve: Improve ScaledAnimatedImageDrawable
* improve: Improve Equals and hashCode
* improve: Improve the code
* improve: Improve the log

### sketch-extensions

* change: Replace PauseLoadWhenScrollingDisplayInterceptor with
  PauseLoadWhenScrollingDrawableDecodeInterceptor, And remove pauseLoadWhenScrollingError()

### sketch-compose

* fix: Fixed bug where AsyncImage and SubcomposeAsyncImage would cause constant recomposition
* fix: Fixed AsyncImage and AsyncImagePainter always change resize precision to EXACTLY bug
* fix: Fixed bug that AsyncImagePainter did not inherit CrossfadeTransition.Factory.fadeStart
  property
* fix: Fixed bug where AsyncImagePainter would ignore nested Drawables when updating display count
* improve: AsyncImage now uses dedicated DisplayTarget, SizeResolver, ScaleDecider

### sketch-gif-movie

* improve: Improve MovieDrawable

### sketch-gif-koral

* improve: Improve GifDrawableDrawableDecoder

# v3.1.0-beta03

### sketch

* fix: Fixed BitmapPool will still be accessed when disallowReuseBitmap is true
* remove: Remove fixedPrecision(), longImageClipPrecision(), fixedScale(), longImageScale()
  functions
* remove: Remove BitmapInfo
* improve: Improve freeBitmap performance and reduce main thread lag
* new: Now the final state Drawable implements the SketchStateDrawable interface

### sketch-zoom

* fix: Fixed the bug that SketchZoomImageView will still access MemoryCache when the read or write
  of memoryCachePolicy is not ENABLED
* fix: Fixed a bug where SketchZoomImageView would also trigger subsampling when encountering a
  state image

# v3.1.0-beta02

### sketch

* improve: Improve LruBitmapPool
* fix: Fixed the bug that the put method of LruBitmapPool failed to put the second bitmap when
  encountering a bitmap with the same size and config but not the same instance

### sketch-zoom

* bug: Fixed a bug where SketchZoomImageView might sometimes sample tiles not loading
* bug: Fixed the bug that when SketchZoomImageView is nested in ViewPager, it cannot return the
  swipe event to ViewPager when it reaches the edge and then swipes
* change: Refactor Scales. Scales rename to ScaleState，SketchImageView's scalesFactory property
  rename to scaleStateFactory
* improve: Improve zoom

# v3.1.0-beta01

### sketch

* change: MemoryCache now stores MemoryCache.Value
* change: MemoryCache adds keys() method
* change: Add the suspend keyword to the getResponse() method of HttpStack #167
* new: Added InexactlyMemoryCacheStateImage #166

### sketch-zoom

* improve: Improve zoom

# v3.0.0 stable

New version, new beginning

* change: maven groupId changed to `io.github.panpf.sketch3`, so version 2.\* will not prompt to
  upgrade
* change: Changed the package name to `com.github.panpf.sketch` so it won't conflict with the 2.\*
  version
* change: Based on kotlin coroutine rewrite, API and function implementation are all changed, just
  use a new library
* improve: It is no longer required to use SketchImageView, any ImageView and its subclasses can,
  combined with custom Target can support any View
* improve: The Zoom function is split into independent modules that can be independently relied on,
  and the large image sampling function is refactored and multi-threaded decoding is faster.
* improve: The gif module now directly depends on the [android-gif-drawable] library and no longer
  needs to be modified twice, and can be upgraded by itself
* new: Added support for SVG
* new: Added support for Jetpack Compose
* new: Support for request and decode interceptors
* new: Refer to [coil] v2.2.0 version and combine it with the original functions of sketch. Compared
  with [coil], there are the following differences:
    * Sketch supports minimum API 16, while [coil] only supports API 21 minimum
    * Sketch supports bitmap multiplexing, but [coil] does not
    * Sketch supports finer adjustment of image size
    * Sketch clearly distinguishes between display, load, and download requests
    * Sketch provides a picture zoom display component and supports large picture sampling

# v3.0.0-rc10

### sketch

* fix: Fixed the bug that the transformedList of the DrawableDecodeResult returned by
  DefaultDrawableDecoder was always null
* change: RequestInterceptor, BitmapDecodeInterceptor and DrawableDecodeInterceptor add key
  attribute
* improve: Improve HttpUriFetcher
* new: Added 'extras: Map<String, String>' property to DisplayResult and LoadResult to facilitate
  passing more information during decoding or transform stage
* new: ImageRequest and ImageOptions supports ComponentRegistry, you can configure components that
  only work on the current ImageRequest

# v3.0.0-rc09

### sketch

* fix: Fixed the bug that BitmapResultCacheDecodeInterceptor may cause multiple writes to the cache
  when the same image is loaded multiple times
* improve: BlurTransformation now actively recycles intermediate bitmaps

# v3.0.0-rc08

### sketch

* fix: Fixed the bug that BlurTransformation put input Bitmap to BitmapPool causing exception
* improve: Logger now outputs thread name
* improve: The log level for DepthException errors is now WARNING
* improve: LruBitmapPool intercepts repeated put the same bitmap
* improve: The history of the Bitmap can now be obtained from the log
* improve: Added ImageInvalidException to specifically represent invalid image file exceptions

# v3.0.0-rc07

### sketch

* rename: ImageView.result rename to ImageView.displayResult
* rename: ImageView.dispose() rename to ImageView.disposeDisplay()
* new: Size adds isSameAspectRatio extension function
* fix: No longer use getRunningAppProcesses() method to avoid failure of privacy audit

### sketch-zoom

* improve: Improve reset log of SketchZoomImageView

# v3.0.0-rc06

### sketch

* change: The DataSource.file() method removes the suspend tag
* improve: Remove url escape characters in ImageRequest key and cacheKey

### sketch-zoom

* fix: Fixed the bug that the tile and preview image of SketchZoomImageView are misplaced

# v3.0.0-rc05

### sketch

* fix: Fixed the bug that BitmapResultCacheDecodeInterceptor still writes Bitmap to the result cache
  when BitmapDecodeResult.transformedList is empty
* improve: Improve toString of FixedScaleDecider and FixedPrecisionDecider

# v3.0.0-rc04

### sketch

* rename: transition rename to transitionFactory
* rename: Resize's precision and scale rename to precisionDecider and scaleDecider
* improve: Manually close ImageDecoder
* improve: Improved Parameters
* improve: CircleCropTransformation now defaults to get scale from ImageRequest.resizeScaleDecider
* new: CrossfadeTransition now supports setting fadeStart

### sketch-video/sketch-video-ffmpeg

* rename: videoFramePercentDuration rename to videoFramePercent
* improve: Improve exception information for VideoFrameBitmapDecoder and
  FFmpegVideoFrameBitmapDecoder

### sketch-svg

* new: Support setting custom CSS rules for SVG

### sketch-compose

* improve: Improve AsyncImage based on coil v2.1.0

# v3.0.0-rc03

### sketch

* fix: Fixed the bug that the callback of the sub-drawables of CrossfadeDrawable, IconDrawable,
  ScaledAnimatedImageDrawable would be lost, causing the sub-animation Drawable to fail to play
* change: CrossfadeDrawable default duration changed from 100ms to 200ms
* new: Added 'exist(): Boolean' method to BitmapPool and DiskCache

### sketch-extensions

* improve: Improved animation of RingProgressDrawable

### other:

upgrade: Upgrade the latest version of kotlin and androidx library

# v3.0.0-rc02

### sketch

* change: RequestInterceptor is now executed on the main thread and Target.start() is now executed
  after checking the memory cache
* change: BitmapPool remove setInBitmap(), setInBitmapForRegion(), free
* improve: MemoryCache no longer needs edit lock

### sketch-zoom

* fix: Fixed the bug that SketchZoomImageView location() is not as expected
* fix: Fixed the bug that the center point pans when SketchZoomImageView zooms with one finger
  up [#160](https://github.com/panpf/sketch/issues/160)
* improve: SketchZoomImageView translate image during scale
* improve: Improve SketchZoomImageView

### sketch-gif-koral

* improve: 'sketch-gif-koral' no longer depends on 'sketch-gif-movie'

# v3.0.0-rc01

### sketch

Cache:

* fix: Fixed the bug that LruBitmapPool does not support RGBA_F16
* change: Now the download cache directory name is 'download', and the result cache directory name
  is 'result'
* change: LruMemoryCache.put() now intercepts larger bitmaps
* rename: downloadDiskCache rename to downloadCache, resultDiskCache rename to resultCache,
  DISK_CACHE rename to DOWNLOAD_CACHE, RESULT_DISK_CACHE rename to RESULT_CACHE
* improve: Improve disk cache

Request:

* fix: Fixed the bug that the bitmap cannot be placed in the BitmapPool without using CountBitmap
  when the memory cache is disabled
* change: Now RequestInterceptor runs in worker thread, and Target.onStart() is executed before
  RequestInterceptor
* change: Remove Transformed and JsonSerializable
* change: Refactor DownloadData
* change: imageExifOrientation property merged into ImageInfo
* new: Restore Sketch.globalImageOptions, because now placeholder is used before request interceptor

Decoder:

* fix: Fixed BaseAnimatedImageDrawableDecoder registration callback is crash bug
* fix: Fixed the bug that Bitmap read from Result cache is always immutable

### sketch-extensions

* rename: SketchImageView's registerListener(), unregisterListener(), registerProgressListener(),
  unregisterProgressListener() rename to registerDisplayListener(), unregisterDisplayListener(),
  registerDisplayProgressListener(), unregisterDisplayProgress

# v3.0.0-beta06

### sketch

Decoder:

* fix: Fixed the bug that XmlDrawableBitmapDecoder do not support bitmapConfig

Sketch:

* remove: Remove Sketch.globalImageOptions
* remove: Removed Sketch.longImageDecider and can now specify LongImageDecider when creating
  LongImageScaleDecider, LongImageClipPrecisionDecider, LongImageReadModeDecider

ImageRequest:

* change: ImageRequest.Builder.global() rename to default()
* change: The type of the error property of ImageOptions and ImageRequest is changed to
  ErrorStateImage

Other:

* fix: Fixed ImageOptions bug that error is still null when stateImage is null and configBlock is
  not null
* improve: DepthException no longer prints stack information

### sketch-extensions

* fix: Fixed a bug where the click to ignore cellular data saving feature did not work
* fix: Fixed the bug that ApkIconBitmapDecoder, AppIconBitmapDecoder do not support bitmapConfig

### sketch-zoom

* new: SketchZoomImageView added touchPointToDrawablePoint method

# v3.0.0-beta05

### sketch

Decoder:

* fix: Fixed the bug that XmlDrawableBitmapDecoder does not support loading vector resources from
  other packages in versions below 7.0
* improve: Replace ResourcesCompat with AppCompatResources

Logger:

* rename: Logger's construction parameter _level is rename to level

* fix: Fixed the bug of abnormal transparency of placeholder drawable

DataSource:

* remove: DataSource remove newFileDescriptor method

DiskCache:

* change: Sketch.diskCache split into downloadDiskCache and resultDiskCache
* change: Change the version attribute of LruDiskCache to appVersion and add the internalVersion
  attribute

ImageRequest:

* new: Add enqueue() and execute() methods for DisplayRequest, DownloadRequest, LoadRequest

Fetcher:

* new: Added newResourceUri(String, String) and newResourceUri(Int) function
* new: Added newResourceUri(String, String) and newResourceUri(Int) function
* new: Added newFileUri(File) function

SketchImageView:

* new: SketchImageView added registerListener(), unregisterListener(), registerProgressListener(),
  unregisterProgressListener() method

Transformation:

* new: Added BlurTransformation and MaskTransformation

Transition:

* new: CrossfadeTransition.Factory added alwaysUse property

StateImage:

* new: Added CurrentStateImage

### sketch-zoom

* fix: Fixed a bug where SketchZoomImageView would cause ViewPager to fail to slide
* change: Now move ZoomAbility's functional methods into SketchZoomImageView

# v3.0.0-beta04

Resize:

* fix: Fixed the bug that the final image content is wrong when precision is LESS_PIXELS and the
  image size is smaller than the resize size

# v3.0.0-beta03

Target:

* rename: ViewTarget rename to ViewDisplayTarget
* new: Added RemoteViewsDisplayTarget to display image to RemoteViews

DiskCache:

* fix: Fixed the bug that the contentType disk cache does not take effect

MemoryCache:

* log: Improved log for LruMemoryCache and LruDiskCache

ViewAbility:

* new: ViewAbility support onSaveInstanceState() and onRestoreInstanceState()

Other:

* fix: Fixed the bug that the ResizeAnimatableDrawable stop() method does not take effect
* remove: DepthException removes the depth property, UriInvalidException removes the uri property
* remove: SketchException no longer contains ImageRequest
* remove: SketchException no longer contains ImageRequest
* change: The message of UnknownException cannot be null
* improve: Improve the code

Docs:

* docs: Improve docs

Build:

* build: Improve build
* build: Add project icon for IntelliJ project list

# v3.0.0-beta02

* fix: Fixed the bug that the length() method of AssetDataSource and ResourceDataSource crashed in
  versions below KITKAT
* remove: Remove BlurTransformation
* change: In order to be compatible with the bug of webp in JELLY_BEAN_M3 and below versions, the
  imageType is no longer checked after decoding the Image boundary information
* change: DiskCache implements the Closeable interface and close it when Sketch shutdown
* new: ImageOptions and ImageRequest add resize(Resize?) method
* new: SketchDrawable adds requestCacheKey property

# v3.0.0-beta01

### sketch

ImageRequest:

* fix: Fixed the bug that the cacheKey of ImageRequest always contains the parameter whose cacheKey
  is null
* improve: Improve the return result of ImageRequest's newKey() and newCacheKey() methods
* new: DisplayResult, DisplayData, LoadResult, LoadData increased imageExifOrientation and
  transformedList properties

Decode:

* fix: InSampledTransformed is not added when the decoded Bitmap is not shrunk
* improve: Use BufferedInputStream when decoding to improve decoding speed

HTTP:

* fix: Fixed the bug of deduplication in the addExtraHeaders() method of HurlStack.Builder

StateImage:

* rename: newErrorStateImage() rename to ErrorStateImage()

AnimatedImage:

* change: repeatCount() , animationStartCallback(), animationEndCallback(), animatedTransformation()
  extension methods are changed to extension properties

### sketch-extensions

* rename: showMimeTypeLogoWithResId() rename to showMimeTypeLogoWithRes()
* rename: ArcProgressDrawable rename to SectorProgressDrawable
* improve: RingProgressDrawable and SectorProgressDrawable constructor parameters now have default
  values

### sketch-viewability

* improve: Improve ViewAbility

### sketch-okhttp

* fix: Fixed the bug of deduplication in the addExtraHeaders() method of OkHttpStack.Builder

### sketch-video

* change: videoFrameMicros(), videoFramePercentDuration(), videoFrameOption() extension methods are
  changed to extension properties

# v3.0.0-alpha04

### sketch

ImageRequest:

* fix: Fixed the bug that GlobalLifecycle does not adapt to LifecycleEventObserver, causing the
  request to fail when the target is not ViewTarget
* remove: Remove lowQualityBitmapConfig() and highQualityBitmapConfig()
* remove: Remove the downloadDiskCacheKey property of ImageRequest
* remove: Remove MIDDEN_QUALITY
* remove: DisplayRequest, DownloadRequest, LoadRequest, ImageOptions remove Builder top-level
  creation functions
* rename: errorImage rename to error
* rename: placeholderImage rename to placeholder
* rename: disabledAnimatedImage rename to disallowAnimatedImage
* rename: disabledReuseBitmap rename to disallowReuseBitmap
* rename: RequestDepth rename to Depth
* rename: LOW_QUALITY rename to LowQuality, HIGH_QUALITY rename to HighQuality
* rename: downloadDiskCachePolicy rename to downloadCachePolicy, bitmapResultDiskCachePolicy rename
* rename: The resizeSize(SizeResolver) method of ImageRequest and ImageOptions has been renamed to
  resizeSizeResolver(SizeResolver)
* rename: The merge() method of HttpHeaders and Parameters has been renamed to merged()
  to resultCachePolicy, bitmapMemoryCachePolicy rename to memoryCachePolicy
* change: depthFrom moved to 'sketch-extensions' module
* change: ImageRequest no longer holds Sketch
* change: ScreenSizeResolver rename to DisplaySizeResolver
* change: DisplayRequest(String?, ImageView, (DisplayRequest.Builder.() -> Unit)) method signature
  changed to DisplayRequest(ImageView, String?, (DisplayRequest.Builder.() -> Unit))
* change: Refactor ImageRequest and ImageOptions resize
* change: Now GenericViewTarget updates drawable on errors even if errorDrawable is null
* improve: All ImageResult implementations are now data classes
* improve: ImageRequest uses ImageOptions to store property
* new: HttpHeaders added newHttpHeaders(), Parameters added newParameters()
* new: ImageRequest adds newMemoryCacheKey(), newResultCacheDataKey(), newResultCacheMetaKey()
  extension methods
* new: Added isNotEmpty() extension method to HttpHeaders and ImageOptions

Sketch:

* remove: Remove static ComponentRegistry.new()
* rename: ComponentRegistry.new() rename to newRegistry()
* change: addRequestInterceptor(), addBitmapDecodeInterceptor(), addDrawableDecodeInterceptor() from
  Sketch moved to ComponentRegistry
* new: Sketch added shutdown() method

Decoder:

* fix: Fixed the bug of samplingSizeForRegion() returning wrong result in api 24 and 25
* fix: Fixed the bug that the returned Bitmap has the wrong size when the difference between the
  resize and the original image's number of pixels does not exceed 10% and the precision is
  LESS_PIXELS
* fix: Fixed bug that resizePrecision is 'EXACTLY' when resizeSize is not set
* fix: Fixed HARDWARE crash when setting inBitmap
* move: ImageFormat move to 'com.github.panpf.sketch.decode.internal' package
* rename: calculateSamplingSize() rename to samplingSize(), calculateSamplingSizeForRegion() rename
* removes: BitmapPool's get() method removes the operator
* change: ImageFormat.valueOfMimeType() change to mimeTypeToImageFormat()
* change: DecodeInterceptor is split into BitmapDecodeInterceptor and DrawableDecodeInterceptor
* change: RequestExtras and CountDrawablePendingManager merged into RequestContext
* change: No longer scales resize by 10% when calculating inSampleSize
* change: precision is LESS_PIXELS must not use BitmapRegionDecoder
* improve: ApkIconBitmapDecoder and AppIconBitmapDecoder now supported bitmapConfig panpf 23 minutes
  ago
* improve: Improve the decision rule of DrawableDecoder.Factory
* improve: Improved DefaultDrawableDecoder to samplingSizeForRegion()
* new: Added Bitmap.Config.isAndSupportHardware()

Transformation:

* fix: Fixed the bug that if the Bitmap of the transformation result is not new, the current Bitmap
  is incorrectly reclaimed, causing a crash
* improve: Improve Transformation

Cache:

* fix: Fixed a crash bug when adding an existing cache to LruMemoryCache
* improve: Improve logging for CountBitmap and LruBitmap
* improve: Clear enough space before put
* improve: Improve LruBitmapPool's setInBitmap() and setInBitmapForRegion()
* new: MemoryCache adds exist(String) method

Fetcher:

* rename: FetchResult.from rename to dataFrom

Transition:

* move: TransitionTarget's view property moved to Transition.Factory.create() method

other:

* improve: Added equals and hashCode implementations
* comment: Add code comments

### sketch-gif-koral

* fix: Fixed the bug that the transformedList of DrawableDecodeResult returned by
  GifDrawableDrawableDecoder was always null

### sketch-zoom

* fix: Fixed the bug that some pictures could not use the large block picture function
* fix: Fixed a crash bug when the preview image ratio was inconsistent with the original image ratio

# v3.0.0-alpha03

### sketch

* fix: Fixed a bug where GifDrawable would not automatically pause due to the
  ViewTargetRequestDelegate.start() method incorrectly removing the observation of Lifecycle
* change: Now as long as resizeSize or resizeSizeResolver is set, the default Precision is EXACTLY
* change: RequestManagerUtils merged into SketchUtils
* change: Now DefaultLongImageDecider is split into smallRatioMultiple and bigRatioMultiple
* change: Change SketchConfigurator to SketchFactory
* change: Now key components such as DataSource, BitmapDecoder, DrawableDecoder, Fetcher, HttpStack,
  RequestInterceptor, StateImage, Transformation, DecodeInterceptor will use Sketch carried by
  ImageRequest
* change: Now you need to pass Resources when creating SketchBitmapDrawable
* new: Now the ImageRequest will only be executed if the Lifecycle reaches the Started state
* new: Added a series of displayImage overloaded methods for ImageView, such as displayImage(Int),
  displayImage(Uri), displayImage(File), displayAssetImage(String), etc.

### sketch-zoom

* fix: Fixed a bug where Tiles didn't reset when View size changed
* fix: Fixed a bug that SketchZoomImageView zoomed abnormally when not SketchDrawable
* fix: Fixed a crash bug in Zoomer's rotateTo() method
* fix: Fixed the bug that SketchZoomImageView always intercepts touch events when no Drawable is
  set, causing ViewPager to fail to slide left and right
* change: ZoomAbility 's zoomScale, baseZoomScale, supportZoomScale, fullZoomScale, fillZoomScale,
  originZoomScale, minZoomScale, maxZoomScale and doubleClickZoomScales properties renamed to scale,
  baseScale, supportScale, fullScale, fillScale, originScale, minScale, maxScale and stepScales
* improve: Tiles are now not enabled when the current zoom is less than or equal to the minimum zoom
* improve: Improve Zoomer's code
* improve: ZoomAbility now listens to Lifecycle's ON_START and ON_STOP events instead to pause and
  resume Tiles
* improve: ZoomAbility will get the lifecycle from the priority ImageRequest

### sketch-gif-koral

* upgrade: Upgrade to version 1.2.15 of the android-gif-drawable library

# v3.0.0-alpha02

### sketch

* fix: Fixed bug that AnimatedImageDrawable doesn't support scaling via bounds
* fix: Fixed a bug with incorrect transparency when using VectorDrawable as state image
* change: All implementations of DisplayTarget now do not continue to set when the error Drawable is
  empty
* change: Scale.KEEP_ASPECT_RATIO renamed to SAME_ASPECT_RATIO
* improve: CrossfadeDrawable now reverts to using the maximum size of the start and end Drawable as
  intrinsic width and height
* improve: ColorResStateImage is merged into ColorStateImage, DrawableResStateImage is merged into
  DrawableStateImage
* improve: The bg property of IconStateImage now supports Drawable
* improve: Access control for all utility functions is now internal
* improve: LongImageClipPrecisionDecider now uses Sketch.longImageDecider by default to determine
  long images
* new: ImageRequest and ImageOptions added resizeApplyToDrawable property IconDrawableStateImage and
  IconDrawableStateImage merged into IconStateImage
* new: Resize's scale property now supports ScaleDecider and provides a LongImageScaleDecider
  implementation

### sketch-extensions

* new: SketchImageView adds xml attribute

### sketch-zoom

* fix: Fixed a bug where the findSampleSize function might crash
* change: The readMode of SketchZoomImageView is now off by default
* improve: Change DefaultReadModeDecider to LongImageReadModeDecider, and use
  Sketch.longImageDecider by default to determine long images

### sketch-compose

* improve: Improved CrossfadePainter

# v3.0.0-alpha01

New version, new beginning

* change: maven groupId changed to `io.github.panpf.sketch3`, so version 2.\* will not prompt to
  upgrade
* change: Changed the package name to `com.github.panpf.sketch` so it won't conflict with the 2.\*
  version
* change: Based on kotlin coroutine rewrite, API and function implementation are all changed, just
  use a new library
* improve: It is no longer required to use SketchImageView, any ImageView and its subclasses can,
  combined with custom Target can support any View
* improve: The Zoom function is split into independent modules that can be independently relied on,
  and the large image sampling function is refactored and multi-threaded decoding is faster.
* improve: The gif module now directly depends on the [android-gif-drawable] library and no longer
  needs to be modified twice, and can be upgraded by itself
* new: Added support for SVG
* new: Added support for Jetpack Compose
* new: Support for request and decode interceptors
* new: Refer to [coil] v2.2.0 version and combine it with the original functions of sketch. Compared
  with [coil], there are the following differences:
    * Sketch supports minimum API 16, while [coil] only supports API 21 minimum
    * Sketch supports bitmap multiplexing, but [coil] does not
    * Sketch supports finer adjustment of image size
    * Sketch clearly distinguishes between display, load, and download requests
    * Sketch provides a picture zoom display component and supports large picture sampling

[coil]: https://github.com/coil-kt/coil

[android-gif-drawable]: https://github.com/koral--/android-gif-drawable
