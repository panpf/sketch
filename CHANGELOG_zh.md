# 更新日志

翻译：[English](CHANGELOG.md)

# 4.0.0-alpha02

* change: ImageView.disposeLoad() 重命名为 ImageView.disposeLoad()
* new: 新增 ImageRequest.Builder.composableError() 和 ImageOptions.Builder.composableError() 扩展函数
* new: 新增 ErrorStateImage.Builder.saveCellularTrafficError(DrawableResource) 扩展函数

# 4.0.0-alpha01

> [!CAUTION]
> 1. 4.x 版本为兼容 Compose Multiplatform 而进行了大量破坏性重构和简化，不兼容 3.x 版本
> 2. maven groupId 升级为 `io.github.panpf.sketch4`，因此 2.\*、3.\* 版本不会提示升级

### sketch-core

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

* delete: 移除 BitmapPool 以及和它相关的 disallowReuseBitmap
  属性、CountBitmap、SketchCountBitmapDrawable 类
* change: 重构 DiskCache SnapShot 和 Editor，get() 和 edit() 改为 openSnapShot() 和 openEditor()
  ，并且同一个 key 的 openSnapShot() 和 openEditor() 现在是互相冲突的, openSnapshot 未关闭前
  openEditor 始终返回 null
* change: 重构 MemoryCache.Value

state:
* change: ImageRequest 和 ImageOptions 的 uriEmpty 属性重命名为 fallback
* delete: 移除 ErrorStateImage.Builder.uriEmptyError()

other:

* change: SketchSingleton 重构为 SingletonSketch
* change: displayImage 重命名为 loadImage

### sketch-compose

* delete: AsyncImage 可组合函数移除 placeholder、error、uriEmpty、onLoading、onSuccess、onError 参数
* upgrade：Compose Multiplatform 升级到 1.6.10
* new: AsyncImageState 现在可以设置 ImageOptions 了，例如：'rememberAsyncImageState {
  ImageOptions() }'

### other

* upgrade：Android 最低 API 升到了 API 21
* upgrade：kotlin 升级到 2.0.0，主要是为了支持 Compose Multiplatform 1.6.10
