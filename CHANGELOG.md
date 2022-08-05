# v3.0.0-rc02

### sketch

* change: RequestInterceptor is now executed on the main thread and Target.start() is now executed
  after checking the memory cache
* change: BitmapPool remove setInBitmap(), setInBitmapForRegion(), free
* improve: MemoryCache no longer needs edit lock

### sketch-zoom

* fix: Fix the bug that SketchZoomImageView location() is not as expected
* fix: Fix the bug that the center point pans when SketchZoomImageView zooms with one finger
  up [#160](https://github.com/panpf/sketch/issues/160)
* improve: SketchZoomImageView translate image during scale
* improve: Improve SketchZoomImageView

### sketch-gif-koral

* improve: 'sketch-gif-koral' no longer depends on 'sketch-gif-movie'

# v3.0.0-rc01

### sketch

Cache:

* fix: Fix the bug that LruBitmapPool does not support RGBA_F16
* change: Now the download cache directory name is 'download', and the result cache directory name
  is 'result'
* change: LruMemoryCache.put() now intercepts larger bitmaps
* rename: downloadDiskCache rename to downloadCache, resultDiskCache rename to resultCache,
  DISK_CACHE rename to DOWNLOAD_CACHE, RESULT_DISK_CACHE rename to RESULT_CACHE
* improve: Improve disk cache

Request:

* fix: Fix the bug that the bitmap cannot be placed in the BitmapPool without using CountBitmap when
  the memory cache is disabled
* change: Now RequestInterceptor runs in worker thread, and Target.onStart() is executed before
  RequestInterceptor
* change: Remove Transformed and JsonSerializable
* change: Refactor DownloadData
* change: imageExifOrientation property merged into ImageInfo
* new: Restore Sketch.globalImageOptions, because now placeholder is used before request interceptor

Decoder:

* fix: Fix BaseAnimatedImageDrawableDecoder registration callback is crash bug
* fix: Fix the bug that Bitmap read from Result cache is always immutable

### sketch-extensions

* rename: SketchImageView's registerListener(), unregisterListener(), registerProgressListener(),
  unregisterProgressListener() rename to registerDisplayListener(), unregisterDisplayListener(),
  registerDisplayProgressListener(), unregisterDisplayProgress

# v3.0.0-beta06

### sketch

Decoder:

* fix: Fix the bug that XmlDrawableBitmapDecoder do not support bitmapConfig

Sketch:

* remove: Remove Sketch.globalImageOptions
* remove: Removed Sketch.longImageDecider and can now specify LongImageDecider when creating
  LongImageScaleDecider, LongImageClipPrecisionDecider, LongImageReadModeDecider

ImageRequest:

* change: ImageRequest.Builder.global() rename to default()
* change: The type of the error property of ImageOptions and ImageRequest is changed to
  ErrorStateImage

Other:

* fix: Fix ImageOptions bug that error is still null when stateImage is null and configBlock is not
  null
* improve: DepthException no longer prints stack information

### sketch-extensions

* fix: Fixed a bug where the click to ignore cellular data saving feature did not work
* fix: Fix the bug that ApkIconBitmapDecoder, AppIconBitmapDecoder do not support bitmapConfig

### sketch-zoom

* new: SketchZoomImageView added touchPointToDrawablePoint method

# v3.0.0-beta05

### sketch

Decoder:

* fix: Fix the bug that XmlDrawableBitmapDecoder does not support loading vector resources from
  other packages in versions below 7.0
* improve: Replace ResourcesCompat with AppCompatResources

Logger:

* rename: Logger's construction parameter _level is rename to level

* fix: Fix the bug of abnormal transparency of placeholder drawable

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

* fix: Fix the bug that the final image content is wrong when precision is LESS_PIXELS and the image
  size is smaller than the resize size

# v3.0.0-beta03

Target:

* rename: ViewTarget rename to ViewDisplayTarget
* new: Added RemoteViewsDisplayTarget to display image to RemoteViews

DiskCache:

* fix: Fix the bug that the contentType disk cache does not take effect

MemoryCache:

* log: Improved log for LruMemoryCache and LruDiskCache

ViewAbility:

* new: ViewAbility support onSaveInstanceState() and onRestoreInstanceState()

Other:

* fix: Fix the bug that the ResizeAnimatableDrawable stop() method does not take effect
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

* fix: Fix the bug that the cacheKey of ImageRequest always contains the parameter whose cacheKey is
  null
* improve: Improve the return result of ImageRequest's newKey() and newCacheKey() methods
* new: DisplayResult, DisplayData, LoadResult, LoadData increased imageExifOrientation and
  transformedList properties

Decode:

* fix: InSampledTransformed is not added when the decoded Bitmap is not shrunk
* improve: Use BufferedInputStream when decoding to improve decoding speed

HTTP:

* fix: Fix the bug of deduplication in the addExtraHeaders() method of HurlStack.Builder

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

* fix: Fix the bug of deduplication in the addExtraHeaders() method of OkHttpStack.Builder

### sketch-video

* change: videoFrameMicros(), videoFramePercentDuration(), videoFrameOption() extension methods are
  changed to extension properties

# v3.0.0-alpha04

### sketch

ImageRequest:

* fix: Fix the bug that GlobalLifecycle does not adapt to LifecycleEventObserver, causing the
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

* fix: Fix the bug of samplingSizeForRegion() returning wrong result in api 24 and 25
* fix: Fix the bug that the returned Bitmap has the wrong size when the difference between the
  resize and the original image's number of pixels does not exceed 10% and the precision is
  LESS_PIXELS
* fix: Fix bug that resizePrecision is 'EXACTLY' when resizeSize is not set
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

* fix: Fix the bug that if the Bitmap of the transformation result is not new, the current Bitmap is
  incorrectly reclaimed, causing a crash
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

* fix: 修复由于 ViewTargetRequestDelegate.start() 方法错误的移除了对 Lifecycle 的观察导致 GifDrawable 不会自动暂停的 bug
* change: 现在只要设置了 resizeSize 或 resizeSizeResolver，默认的 Precision 就是 EXACTLY
* change: RequestManagerUtils 合并到 SketchUtils
* change: 现在 DefaultLongImageDecider 被分为 smallRatioMultiple 和 bigRatioMultiple
* change: 将 SketchConfigurator 更改为 SketchFactory
* change: 现在
  DataSource、BitmapDecoder、DrawableDecoder、Fetcher、HttpStack、RequestInterceptor、StateImage、Transformation、DecodeInterceptor
  等关键组件将使用 ImageRequest 携带的 Sketch
* change: 现在创建 SketchBitmapDrawable 时需要传 Resources
* new: 现在只有 Lifecycle 到达 Started 状态才会开始执行 ImageRequest
* new: 为 ImageView 添加了一系列 displayImage 重载方法，例如 displayImage(Int)、displayImage(Uri)
  、displayImage(File)、displayAssetImage(String) 等

### sketch-zoom

* fix: 修复了 View 大小改变时 Tiles 没有重置的 bug
* fix: 修复了 SketchZoomImageView 在非 SketchDrawable 时缩放异常的 bug
* fix: 修复了 Zoomer 的 rotateTo() 方法崩溃的 bug
* fix: 修复了 SketchZoomImageView 在没有设置 Drawable 时始终拦截触摸事件导致 ViewPager 无法左右滑动的 bug
* change: ZoomAbility 的 zoomScale, baseZoomScale, supportZoomScale, fullZoomScale, fillZoomScale,
  originZoomScale, minZoomScale, maxZoomScale 和 doubleClickZoomScales 属性重命名为 scale, baseScale,
  supportScale, fullScale, fillScale, originScale, minScale, maxScale 和 stepScales
* improve: 现在 Tiles 在当前缩放比例小于等于最小缩放比例时不会启用
* improve: 改进 Zoomer 的代码
* improve: ZoomAbility 现在改为监听 Lifecycle 的 ON_START 和 ON_STOP 事件来暂停和恢复 Tiles
* improve: ZoomAbility 将从优先 ImageRequest 获取生命周期

### sketch-gif-koral

* upgrade: 升级 android-gif-drawable 库的 1.2.15 版本

# v3.0.0-alpha02

### sketch

* fix: 修复 AnimatedImageDrawable 不支持通过 bounds 缩放的 bug
* fix: 修正了使用 VectorDrawable 作为状态图像时透明度不正常的 bug
* change: 现在 DisplayTarget 的所有实现当 error Drawable 为空时，不继续设置
* change: Scale.KEEP_ASPECT_RATIO 重命名为 SAME_ASPECT_RATIO
* improve: CrossfadeDrawable 现在恢复为根据 start 和 end Drawable 的最大尺寸作为 intrinsic 宽高
* improve: ColorResStateImage 合并到 ColorStateImage, DrawableResStateImage 合并到 DrawableStateImage,
* improve: IconStateImage 的 bg 属性现在支持 Drawable
* improve: 所有工具函数的访问控制现在为 internal
* improve: LongImageClipPrecisionDecider 现在默认使用 Sketch.longImageDecider 来判定长图
* new: ImageRequest 和 ImageOptions 增加 resizeApplyToDrawable 属性 IconDrawableStateImage 和
  IconDrawableStateImage 合并为 IconStateImage
* new: Resize 的 scale 属性现在支持 ScaleDecider 并提供 LongImageScaleDecider 实现

### sketch-extensions

* new: SketchImageView 增加 xml 属性

### sketch-zoom

* fix: 修复 findSampleSize 函数可能会崩溃的 bug
* change: SketchZoomImageView 的 readMode 现在默认关闭
* improve: DefaultReadModeDecider 改为 LongImageReadModeDecider，并默认使用 Sketch.longImageDecider 来判定长图

### sketch-compose

* improve: 改进 CrossfadePainter

# v3.0.0-alpha01

全新版本，新的开始

* change: maven groupId 改为 `io.github.panpf.sketch3`，因此 2.\* 版本不会提示升级
* change: 包名改为 `com.github.panpf.sketch` 因此与 2.\* 版本不会冲突
* change: 基于 kotlin 协程重写，API、功能实现全部改变，当一个新的库用就行
* improve: 不再要求必须使用 SketchImageView，任何 ImageView 及其子类都可以，甚至结合自定义 Target 可以支持任意 View
* improve: Zoom 模块的超大图采样功能重构并且支持多线程，因此速度更快更高效
* new: 新增支持 SVG
* new: 新增支持 Jetpack Compose
* new: 支持拦截请求和图片解码

> 参考 [coil] 2.0.0-alpha05 版本并结合 sketch 原有功能实现，[coil] 最低支持 API 21，而 sketch 最低支持 API 16


[coil]: https://github.com/coil-kt/coil
