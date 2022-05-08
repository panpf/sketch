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
* improve: Zoom 模块的分块显示超大图功能重构并且支持多线程，因此速度更快更高效
* new: 新增支持 SVG
* new: 新增支持 Jetpack Compose
* new: 支持拦截请求和图片解码

> 参考 [coil] 2.0.0-alpha05 版本并结合 sketch 原有功能实现，[coil] 最低支持 API 21，而 sketch 最低支持 API 16


[coil]: https://github.com/coil-kt/coil
