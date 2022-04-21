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
