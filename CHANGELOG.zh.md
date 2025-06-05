# 更新日志

翻译：[English](CHANGELOG.md)

> [!CAUTION]
> 1. 4.x 版本为兼容 Compose Multiplatform 而进行了大量破坏性重构和简化，不兼容 3.x 版本
> 2. maven groupId 升级为 `io.github.panpf.sketch4`，因此 2.\*、3.\* 版本不会提示升级
> 3. 参考 [《迁移文档》](docs/migrate.zh.md) 从 3.x 版本迁移 4.x 版本

# new

* change: 弃用带有keepContentNoneStartOnDraw 参数的 AsyncImage
* change: 弃用 TransitionTarget 的 fitScale 属性，新增 TransitionViewTarget.scaleType 和
  TransitionComposeTarget.contentsScale 代替 fitScale
* new: CrossfadePainter 现在支持 contentScale 和 alignment
* new: ResizePainter 现在支持 contentScale 和 alignment

# 4.1.0

> [!CAUTION]
> compose multiplatform 1.8.0 版本必须 jvm 11 才能编译，请修改
> sourceCompatibility、targetCompatibility、jvmTarget 等配置为 11+

* fix: 修复 CrossfadePainter, ResizePainter, ImageBitmapPainter 和 DrawablePainter
  因丢失浮点精度导致内容无法充满画布的 bug
* new: AsyncImage 增加 keepContentNoneStartOnDraw 参数
* depend: 更新 kotlin 2.1.10
* depend: 更新 compose multiplatform 1.8.0
* depend: 更新 android-gif-drawable 1.2.29
* depend: 更新 ffmpegMediaMetadataRetriever 1.0.19
* depend: 更新 jetbrains-lifecycle 2.8.4
* depend: 更新 kotlinx-coroutines 1.10.2
* depend: 更新 ktor2 2.3.13
* depend: 更新 ktor3 3.0.3
* depend: 更新 okio 3.11.0
* depend: 不再替换 `kotlin-stdlib-jdk7` 和 `kotlin-stdlib-jdk8` 为 `kotlin-stdlib`

# 4.0.6

compose:

* improve: 现在 AsyncImage 在窗口大小为 0 时不再崩溃，而是最小为
  100。 [#244](https://github.com/panpf/sketch/issues/244)

# 4.0.5

fetch:

* fix: 修复 FileUriFetcher 无法加载 windows 文件路径的
  bug。 [#239](https://github.com/panpf/sketch/issues/239)

# 4.0.4

compose:

* fix: 修复 AsyncImage 和 SubcomposeAsyncImage 在组件大小为 0 时无法加载图片的
  bug。 [#236](https://github.com/panpf/sketch/issues/236)
* improve: 恢复 AsyncImage 和 SubcomposeAsyncImage 在设置 size 时如果宽或高为 0 就替换为窗口容器大小的功能

# 4.0.3

* fix: 修复 svg 文件头部有注释时 SvgDecoder 无法识别的
  bug。 [#232](https://github.com/panpf/sketch/issues/232)

# 4.0.2

compose:

* fix: 当前窗口大小改变时 AsyncImage
  组件会重新加载。 [#231](https://github.com/panpf/sketch/issues/231)

# 4.0.1

android:

* fix: 修复 BitmapImage 转为 BitmapDrawable 时没有设置 Resources 导致 BitmapDrawable 的 Intrinsic
  尺寸和 Bitmap 不一致的 bug。 [#226](https://github.com/panpf/sketch/issues/226)

# 4.0.0 Stable

Sketch:

* change: SketchSingleton 重构为 SingletonSketch
* change: Sketch 的 execute(DownloadRequest) 和 enqueue(DownloadRequest) 方法重构为 executeDownload(
  ImageRequest) 和 enqueueDownload(ImageRequest)
* new: Sketch.Builder 和 ComponentRegistry.Builder 新增 addComponents() 函数
* new: Sketch.Builder 增加 networkParallelismLimited() 和 decodeParallelismLimited()
  方法控制网络和解码并发数量 [#200](https://github.com/panpf/sketch/issues/200)
* new: 新增 ComponentLoader，支持自动探测并注册 Fetcher 和 Decoder 组件，所有自带模块里的组件都已支持

request:

* change: 不再区分 Display、Load、Download，现在只有一个 ImageRequest、ImageResult、ImageListener
* change: ImageResult 的 requestKey 属性被移除、requestCacheKey 属性重命名为 cacheKey
* change: 现在 Target、ImageResult、DecodeResult 都使用 Image
* change: SketchDrawable 的
  imageUri、requestKey、requestCacheKey、imageInfo、dataFrom、transformedList、extras 等属性被移除，现在请从
  ImageResult 中获取它们
* change: depth 和 depthFrom 属性合并成 DepthHolder
* change: bitmapConfig、colorSpace、preferQualityOverSpeed、placeholder(Int)、fallback(Int)、error(Int) 等
  Android 平台特有 API 以扩展函数的形式提供
* change: resizeApplyToDrawable 重命名为 resizeOnDraw
* change: Parameters 重命名为 Extras
* change: LongImageClipPrecisionDecider 重命名为 LongImagePrecisionDecider,
  LongImageStartCropScaleDecider 重命名为 LongImageScaleDecider
* remove: 移除 listener() 和 progressListener() 方法，现在请使用 addListener() 和
  addProgressListener() 方法
* new: 新增 'sizeMultiplier: Float' 属性，用于设置图片大小的缩放比例
* new: 新增 'allowNullImage: Boolean' 属性

fetch:

* change: Fetcher.Factory.create() 的传参改为 RequestContext
* new: ResourceUriFetcher 支持 'android.resource:///drawable/ic_launcher' 和 'android.resource:
  ///1031232' uri
* new: AssetUriFetcher 支持 'file:///android_asset/' uri

source:

* change: 重构 DataSource

decode:

* change: Decoder 的 decode() 方法移除 suspend 修饰符并且返回类型从 Result<DecodeResult> 改为
  DecodeResult
* change: BitmapDecoder 和 DrawableDecoder 合并为 Decoder
* change: BitmapDecodeInterceptor 和 DrawableDecodeInterceptor 合并为 DecodeInterceptor
* change: BitmapConfig 重构为 BitmapColorType
* improve: 改进动图相关 Decoder 在判断图片类型时的准确性，不再依赖 FetchResult 的 mimeType
  属性，因为它可能不准确
* new: DrawableDecoder 支持 colorSpace

transformation:

* fix: 修复 CircleCrop、Rotate、RoundedCorners Transformation 在 RGB_565 时不工作的
  bug。 [#209](https://github.com/panpf/sketch/issues/209)
* fix: 修复 Android 平台上 blur、rotate 等 Transformation 没有保持 ColorSpace 不变的
  bug。 [#213](https://github.com/panpf/sketch/issues/213)
* change: Transformation 的 transform() 方法移除 suspend 修饰符

cache:

* remove: 移除 BitmapPool 以及和它相关的 disallowReuseBitmap
  属性、CountBitmap、SketchCountBitmapDrawable 类
* change: 重构 DiskCache SnapShot 和 Editor，get() 和 edit() 改为 openSnapShot() 和 openEditor()
  ，并且同一个 key 的 openSnapShot() 和 openEditor() 现在是互相冲突的, openSnapshot 未关闭前
  openEditor 始终返回 null
* change: 重构 MemoryCache.Value

state:

* change: ImageRequest 和 ImageOptions 的 uriEmpty 属性重命名为 fallback
* change: ImageRequest 和 ImageOptions 的 error 属性的类型从 ErrorStateImage 改为 StateImage
* change: ErrorStateImage 移除 ErrorStateImage.Builder.uriEmptyError() 方法并重构为
  ConditionStateImage
* improve: 改进 IconDrawable，支持有固定大小的 background 并且限制 icon 必须有固定尺寸或指定 iconSize

animated:

* fix: 修复 GifDrawable 和 MovieDrawable 无法正确应用 animatedTransformation 的
  bug。 [#214](https://github.com/panpf/sketch/issues/214)
* fix: 修复 GifDrawableDecoder 的 repeatCount 设置错误应该加 1 的
  bug。 [#215](https://github.com/panpf/sketch/issues/215)
* change: 拆分 sketch-animated 模块为 sketch-animated-core 和
  sketch-animated-gif、sketch-animated-webp、sketch-animated-heif, sketch-animated-koralgif 模块重命名为
  sketch-animated-gif-koral

http:

* remove: 移除 DisplayRequest.Builder 和 DisplayOptions.Builder 的 setHttpHeader() 和
  addHttpHeader() 方法
* change: http 功能拆分成单独的模块提供，增加
  sketch-http、sketch-http-hurl、sketch-http-ktor2、sketch-http-ktor3 模块，sketch-okhttp 模块重命名为
  sketch-http-okhttp

compose:

* fix: 修复 AsyncImage 的 filterQuality 参数无效的
  bug。 [#211](https://github.com/panpf/sketch/issues/211)
* remove: AsyncImage 可组合函数移除 placeholder、error、uriEmpty、onLoading、onSuccess、onError 参数
* new: AsyncImageState 现在可以设置 ImageOptions 了，例如：'rememberAsyncImageState {
  ImageOptions() }'
* new: 新增 ErrorStateImage.Builder.saveCellularTrafficError(DrawableResource) 扩展函数
* new: ImageRequest.Builder 和 ImageOptions.Builder 新增 size(IntSize) 扩展函数
* new: AsyncImageState 新增 onPainterState 和 onLoadState 属性，用于以回调的方式接收 PainterState 和
  LoadState 更新
* new: 新增 ComposableImageRequest() 和 ComposableImageOptions() 函数，可以直接在
  placeholder、error、fallback 方法上使用使用 DrawableResource

view:

* fix: 修复当 ImageView 已附到窗口但是因 padding 导致 size 为 null 时无法加载图片的
  bug。 [#208](https://github.com/panpf/sketch/issues/208)
* change: displayImage 重命名为 loadImage
* change: ImageView.disposeDisplay() 重命名为 ImageView.disposeLoad()
* new: ImageRequest.Builder 和 ImageOptions.Builder 新增 sizeWithView()、sizeWithDisplay() 扩展函数

extensions:

* fix: 修复 AppIconUriFetcher.Factory 解析 versionCode 时异常的
  bug。[#204](https://github.com/panpf/sketch/issues/204)
* change: 从 sketch-extensions-core 模块中拆分出 sketch-extensions-apkicon 和
  sketch-extensions-appicon 模块

other:

* upgrade：Android 最低 API 升到了 API 21
* depend: 升级 kotlin 2.0.21, kotlinx coroutines 1.9.0
* depend: 升级 jetbrains compose 1.7.0, jetbrains lifecycle 2.8.3

# v4.0.0-rc01

compose:

* new: AsyncImageState 新增 onPainterState 和 onLoadState 属性，用于以回调的方式接收 PainterState 和
  LoadState 更新

# v4.0.0-beta03

core:

* new: 新增 Image.asBitmap(), asBitmapOrNull(), asDrawableOrNull(), asPainterOrNull() 扩展函数
* improve: ConditionStateImage 和 ComposableConditionStateImage 的 defaultImage 属性现在可空

decode:

* improve: 改进根据 mimeType 判断是否支持区域解码，非 image 类型直接返回 false，非 Android 平台上根据
  skiko 版本判断是否支持 heic、heif、avif 类型

fetch:

* improve: AssetUriFetcher 和 ResourceUriFetcher 现在兼容 sketch3 的协议

# 4.0.0-beta02

core:

* fix: 修复磁盘缓存因被杀导致 REMOVE 记录丢失，重新初始化未校验文件是否存在导致 size() 异常的
  bug [#219](https://github.com/panpf/sketch/issues/219)

animated:

* fix: 修复 sketch-animated-heif 的依赖中意外的包含了本地测试 module 的
  bug [#220](https://github.com/panpf/sketch/issues/220)

# 4.0.0-beta01

core:

* remove: 删除 Image.getPixels()
* remove: 移除 'com.github.panpf.sketch.annotation' 包下的 AnyThread、MainThread、IntRange、IntDef
  注解，使用 'androidx.annotation' 包下的替代
* change: 合并 AndroidBitmapImage 和 SkiaBitmapImage 为 BitmapImage
* change: DrawableEqualizer 重命名为 EquitableDrawable
* change: 移除 AndroidBitmap、SkiBitmap、SkiaImageInfo、SkiaImage、SkiaRect
* change: SkiaAnimatedImage 重命名为 AnimatedImage
* change: ImageRequest 的 registerListener 和 registerProgressListener 方法重命名为 addListener 和
  addProgressListener
* new: 新增 ComponentLoader，支持自动探测并注册 Fetcher 和 Decoder 组件，所有自带模块里的组件都已支持

compose:

* fix: 修复 AsyncImage 的 filterQuality 参数无效的
  bug。 [#211](https://github.com/panpf/sketch/issues/211)
* remove: 移除 ComposeBitmapImage
* change: 移除 PainterState.Empty
* change: PainterEqualizer 重命名为 EquitablePainter
* change: ComposeImagePainter 重命名为 ImageBitmapPainter、SkiaAnimatedImagePainter 重命名为
  AnimatedImagePainter

view:

* fix: 修复当 ImageView 已附到窗口但是因 padding 导致 size 为 null 时无法加载图片的
  bug。 [#208](https://github.com/panpf/sketch/issues/208)

decode:

* change: Decoder 的 decode() 方法移除 suspend 修饰符并且返回类型从 Result<DecodeResult> 改为
  DecodeResult
* change: BitmapConfig 重构为 BitmapColorType
* new: 非安卓平台现在也支持 ColorType 了
* new: 非安卓平台现在也支持 ColorSpace 了
* new: DrawableDecoder 支持 colorSpace

fetch:

* change: Fetcher.Factory.create() 的传参改为 RequestContext

cache:

* change: 现在非安卓平台上内存缓存中缓存的是 SkiaBitmapImage，不再是 ComposeBitmapImage
* change: 桌面和 web 平台的默认内存缓存大小现在是 256MB，ios 平台是 128MB

http:

* remove: 移除 ImageRequest.Builder 和 ImageOptions.Builder 的 setHttpHeader() 和 addHttpHeader() 方法
* change: 移除 sketch-http-core 模块，增加 sketch-http-hurl 模块，sketch-http-ktor 模块重命名为
  sketch-http-ktor2 并且不再支持 wasmJs，增加 sketch-http-ktor3 模块

animated:

* fix: 修复非安卓平台上动图设置 repeatCount 并播放结束后没有停留在最后一帧，而停留在第一帧的
  bug。 [#212](https://github.com/panpf/sketch/issues/212)
* fix: 修复 GifDrawable 和 MovieDrawable 无法正确应用 animatedTransformation 的
  bug。 [#214](https://github.com/panpf/sketch/issues/214)
* fix: 修复 GifDrawableDecoder 的 repeatCount 设置错误应该加 1 的
  bug。 [#215](https://github.com/panpf/sketch/issues/215)
* change: 拆分 sketch-animated 模块为 sketch-animated-core 和
  sketch-animated-gif、sketch-animated-webp、sketch-animated-heif, sketch-animated-koralgif 模块重命名为
  sketch-animated-gif-koral
* improve: animatedTransformation 现在支持非 Android 平台

transformation:

* fix: 修复 CircleCrop、Rotate、RoundedCorners Transformation 在 RGB_565 时不工作的
  bug。 [#209](https://github.com/panpf/sketch/issues/209)
* fix: 修复 Android 平台上 blur、rotate 等 Transformation 没有保持 ColorSpace 不变的
  bug。 [#213](https://github.com/panpf/sketch/issues/213)
* change: Transformation 的 transform() 方法移除 suspend 修饰符

state:

* change: ImageRequest 和 ImageOptions 的 error 属性的类型从 ErrorStateImage 改为 StateImage
* change: ErrorStateImage 重构为 ConditionStateImage，并且 ConditionStateImage 可以用在 placeholder 和
  fallback
* improve: 改进 IconDrawable，支持有固定大小的 background 并且限制 icon 必须有固定尺寸或指定 iconSize
* improve: 改进 IconPainter，支持有固定大小的 background 并且限制 icon 必须有固定尺寸或指定 iconSize
* new: 新增 'Drawable.asStateImage(Any)' 和 'ColorDrawable.asStateImage()' 扩展函数

extensions:

* fix: 修复 RingProgressDrawable, SectorProgressDrawable, RingProgressPainter, SectorProgressPainter
  的 equals 方法未按预期执行的 bug。 [#210](https://github.com/panpf/sketch/issues/210)
* change: 从 sketch-extensions-core 模块中拆分出 sketch-extensions-apkicon 和
  sketch-extensions-appicon 模块

other:

* depend: 升级 kotlin 2.0.21, kotlinx coroutines 1.9.0
* depend: 升级 jetbrains compose 1.7.0, jetbrains lifecycle 2.8.3

# 4.0.0-alpha08

* fix: 修复 ComposableImageRequest() 和 ComposableImageOptions() 函数内部无法监听并更新 Compose
  State 的 bug。 [#207](https://github.com/panpf/sketch/issues/207)
* remove: 移除 SkiaExifOrientationHelper
* remove: DataSource 移除 sketch 和 request 属性
* change: DataSource 的 getFile() 和 getFileOrNull() 方法增加 Sketch 参数
* change: DataSource 的 openSourceOrNull() 和 getFileOrNull() 方法现在以扩展函数的形式提供
* change: RequestContext 移到 'com.github.panpf.sketch.request' 包下
* new: SkiaBitmapImage 现在支持内存缓存
* new: DataSource 新增 key 属性

# 4.0.0-alpha07

* improve: SkiaAnimatedImagePainter 现在解码和绘制不共享 Bitmap
  了，以避免画面错乱。 [#206](https://github.com/panpf/sketch/issues/206)
* improve: SkiaAnimatedImagePainter 现在走到绘制时依然没有启动动画，则会自动加载第一帧
* new: SkiaAnimatedImagePainter
  现在支持缓存解码耗时超过帧持续时间的帧以提高播放流畅度，但这会使用更多的内存，默认关闭，通过 '
  cacheDecodeTimeoutFrame()' 函数开启

# 4.0.0-alpha06

* fix: 修复 SkiaAnimatedImagePainter 在遇到伪装的 gif 图片时崩溃的
  bug。[#205](https://github.com/panpf/sketch/issues/205)
* improve: 改进动图相关 Decoder 在判断图片类型时的准确性，不再依赖 FetchResult 的 mimeType
  属性，因为它可能不准确

# 4.0.0-alpha05

* fix: 修复 AppIconUriFetcher.Factory 解析 versionCode 时异常的
  bug。[#204](https://github.com/panpf/sketch/issues/204)
* change: KotlinResourceUriFetcher 支持的 uri 从 'kotlin.resource://' 改为 'file:
  ///kotlin_resource/'
* change: ComposeResourceUriFetcher 支持的 uri 从 'compose.resource://'
  改为 'file:///compose_resource/'
* change: ResourceUriFetcher 支持的 uri 从 'android.resource:
  //resource?resType=drawable&resName=ic_launcher' 和 'android.resource://resource?resId=1031232'
  改为 'android.resource:///drawable/ic_launcher' 和 'android.resource:///1031232'
* change: ImageRequest.uri 属性的类型从 String 改为 Uri
* change: AssetUriFetcher 支持的 uri 从 'asset://' 改为 'file:///android_asset/'
* improve: Fetcher 的所有实现类都实现了 equals 和 hashCode 方法

# 4.0.0-alpha04

* fix: 修复 HurlStack 和 OkHttpStack
  因再次切换线程导致网络并发控制失败的问题 [#199](https://github.com/panpf/sketch/issues/199)
* fix: 修复所有网络任务完成后才会开始解码图片的
  bug. [#201](https://github.com/panpf/sketch/issues/201)
* change: ImageOptions 和 ImageRequest 的 mergeComponents() 方法重命名为 addComponents()
* change: ImageView.loadImage() 和 newResourceUri() 函数的 drawableResId 参数重命名为 resId
* change: AndroidLogPipeline 和 PrintLogPipeline 改为单例模式
* improve: 检查 PlatformContext 的类型以防止将 Activity 传递给 Sketch 或 ImageRequest
* new: 添加 PlatformContext.sketch 和 View.sketch 扩展函数
* new: Sketch.Builder 和 ComponentRegistry.Builder 新增 addComponents() 函数
* new: Sketch.Builder 增加 networkParallelismLimited() 和 decodeParallelismLimited()
  方法控制网络和解码并发数量 [#200](https://github.com/panpf/sketch/issues/200)

# 4.0.0-alpha03

* change: ComposeBitmapValue 重命名为 ComposeBitmapImageValue
* change: 重构 Sketch.enqueueDownload() 和 executeDownload() 并从 sketch-extensions-core 模块移到
  sketch-core 模块
* change: LongImageClipPrecisionDecider 重命名为 LongImagePrecisionDecider,
  LongImageStartCropScaleDecider 重命名为 LongImageScaleDecider
* change: 恢复在构建 ImageRequest 时使用屏幕大小作为最终的 Size
* improve: Painter.asSketchImage() 现在返回 PainterImage; ComposeBitmap.asSketchImage() 现在返回
  ComposeBitmapImage
* new: ImageRequest.Builder 和 ImageOptions.Builder 新增 sizeWithView()、sizeWithDisplay()、size(
  IntSize) 扩展函数

# 4.0.0-alpha02

* change: ImageView.disposeDisplay() 重命名为 ImageView.disposeLoad()
* new: 新增 ImageRequest.Builder.composableError() 和 ImageOptions.Builder.composableError() 扩展函数
* new: 新增 ErrorStateImage.Builder.saveCellularTrafficError(DrawableResource) 扩展函数

# 4.0.0-alpha01

Sketch:

* change: SketchSingleton 重构为 SingletonSketch

request:

* change: 不再区分 Display、Load、Download，现在只有一个 ImageRequest、ImageResult、ImageListener
* change: ImageResult 的 requestKey 属性被移除、requestCacheKey 属性重命名为 cacheKey
* change: 现在 Target、ImageResult、DecodeResult 都使用 Image
* change: SketchDrawable 的
  imageUri、requestKey、requestCacheKey、imageInfo、dataFrom、transformedList、extras 等属性被移除，现在请从
  ImageResult 中获取它们
* change: depth 和 depthFrom 属性合并成 DepthHolder
* change: bitmapConfig、colorSpace、preferQualityOverSpeed、placeholder(Int)、fallback(Int)、error(Int) 等
  Android 平台特有 API 以扩展函数的形式提供
* change: resizeApplyToDrawable 重命名为 resizeOnDraw
* change: Parameters 重命名为 Extras
* new: 新增 'sizeMultiplier: Float' 属性，用于设置图片大小的缩放比例
* new: 新增 'allowNullImage: Boolean' 属性

decode:

* change: BitmapDecoder 和 DrawableDecoder 合并为 Decoder
* change: BitmapDecodeInterceptor 和 DrawableDecodeInterceptor 合并为 DecodeInterceptor

cache:

* remove: 移除 BitmapPool 以及和它相关的 disallowReuseBitmap
  属性、CountBitmap、SketchCountBitmapDrawable 类
* change: 重构 DiskCache SnapShot 和 Editor，get() 和 edit() 改为 openSnapShot() 和 openEditor()
  ，并且同一个 key 的 openSnapShot() 和 openEditor() 现在是互相冲突的, openSnapshot 未关闭前
  openEditor 始终返回 null
* change: 重构 MemoryCache.Value

state:

* change: ImageRequest 和 ImageOptions 的 uriEmpty 属性重命名为 fallback
* remove: 移除 ErrorStateImage.Builder.uriEmptyError()

compose:

* remove: AsyncImage 可组合函数移除 placeholder、error、uriEmpty、onLoading、onSuccess、onError 参数
* upgrade：Compose Multiplatform 升级到 1.6.10
* new: AsyncImageState 现在可以设置 ImageOptions 了，例如：'rememberAsyncImageState {
  ImageOptions() }'

view:

* change: displayImage 重命名为 loadImage

other:

* upgrade：Android 最低 API 升到了 API 21
* upgrade：kotlin 升级到 2.0.0，主要是为了支持 Compose Multiplatform 1.6.10
