# 手势缩放

`需要导入 sketch-zoom 模块`

Sketch 的 [SketchZoomImageView] 提供了手势缩放以及超大图采样功能，手势缩放功能参考 [PhotoView] 实现

### 对比 PhotoView

* 双击缩放层级：从 [PhotoView] 的三级减少到两级，操作更简单
* 双击缩放比例：[PhotoView] 的双击缩放比例是固定的，而 [SketchZoomImageView] 是根据图片的尺寸、View 的宽高以及 ScaleType 动态计算的，体验更好
* 边界阻尼：手动缩放超过了最小或最大比例时 [PhotoView] 直接就拉不动了，而 [SketchZoomImageView] 会有种拉橡皮筋的感觉，体验更好
* 边界判定：[SketchZoomImageView] 优化了 scrollEdge 的判断，修复了在不能整除的缩放比例下，无法识别边缘的 BUG
* 滑动条：[SketchZoomImageView] 增加了滑动条，可以方便的看到当前滑动的位置
* 定位：[SketchZoomImageView] 增加了定位功能，可以指定图片上的一个点，然后以动画的方式移动到这个点
* 阅读模式：[SketchZoomImageView] 增加了阅读模式，对于长图的阅读体验更好

### 使用

直接用 [SketchZoomImageView] 替换 ImageView 即可

```kotlin
sketchZoomImageView.displayImage("https://www.sample.com/image.jpg")
```

> 注意：
> * 缩放功能支持任意来源的 Drawable
> * 超大图采样功能仅支持来自 Sketch 的 Drawable

### 缩放

```kotlin
// 放大 3 倍（不使用动画）
sketchZoomImageView.scale(3f)

// 放大 3 倍并使用动画
sketchZoomImageView.scale(3f, true)

// 以 100x200 为中心点放大 3 倍并使用动画
sketchZoomImageView.scale(3f, 100f, 200f, true)
```

> 注意：
> * 设置的缩放比例不能小于最小缩放比例也不能大于最大缩放比例
> * 通过 scale 方法设置的缩放比例只是临时性的并不会一直保持，其它任何缩放行为和更新行为都会覆盖此缩放比例

## 旋转

```kotlin
// 旋转到 180°
sketchZoomImageView.rotateTo(180)

// 顺时针再旋转 90°
sketchZoomImageView.rotateBy(90)
```

> 注意：
> * 只支持 90、180、270、360 等能整除 90 的旋转角度
> * 旋转角度是会一直存在的

### 定位

```kotlin
// 定位到 100x200（不用考虑旋转角度）
sketchZoomImageView.location(100f, 200f)

// 定位到 100x200 并使用动画（不用考虑旋转角度）
sketchZoomImageView.location(100f, 200f, true)
```

> 注意：通过 location 方法设置的位置只是临时性的并不会一直保持，其它任何位移行为和更新行为都会覆盖此位置

### 滑动条

滑动条可以让你在查看长图时清楚的知道当前的位置，[SketchZoomImageView] 默认开启滑动条功能，你也可以关闭它，如下：

```kotlin
sketchZoomImageView.scrollBarEnabled = false
```

> 滑动时显示，无操作 800 毫秒后自动隐藏

### 阅读模式

对于宽高相差特别大的长图，如果一开始显示全貌，那么什么也看不清楚，用户必须双击一下放大才能开始阅读

针对这样的图片 [SketchZoomImageView] 提供了阅读模式让其一开始就充满屏幕，这样用户就能直接开始阅读长图的内容了

开启阅读模式：

```kotlin
sketchZoomImageView.readModeEnabled = true
```

[SketchZoomImageView] 通过 [ReadModeDecider] 来判定是否需要使用阅读模式，默认实现是 [LongImageReadModeDecider]，仅对长图使用阅读模式

> 长图规则默认实现为 [DefaultLongImageDecider]，你还可以在创建 [LongImageReadModeDecider] 时使用自定义的长图判定规则

如果你想修改阅读模式判定规则可以实现 [ReadModeDecider] 接口，然后通过 [SketchZoomImageView] 的 `readModeDecider` 属性应用，如下：

```kotlin
class MyReadModeDecider : ReadModeDecider {

    override fun should(
        imageWidth: Int,
        imageHeight: Int,
        viewWidth: Int,
        viewHeight: Int
    ): Boolean {
        // 实现你的判定规则
    }
}

sketchZoomImageView.readModeDecider = MyReadModeDecider()
```

### 自定义缩放比例

通过 ScaleState 你可以修改最小、最大、双击、初始等缩放比例，如下自定义你的 ScaleState 并应用即可：

```kotlin
class MyScaleStateFactory : ScaleState.Factory {

    override fun create(
        viewSize: Size,
        imageSize: Size,
        drawableSize: Size,
        rotateDegrees: Int,
        scaleType: ScaleType,
        readModeDecider: ReadModeDecider?,
    ): ScaleState {
        val minScale = ...
        val maxScale = ...
        val fullScale = ...
        val fillScale = ...
        val originScale = ...
        val initial = ...
        val steps = ...
        return ScaleState(
            min = minScale,
            max = maxScale,
            full = fullScale,
            fill = fillScale,
            origin = originScale,
            initial = initial,
            doubleClickSteps = steps,
        )
    }
}

sketchZoomImageView.scaleStateFactory = MyScaleStateFactory()
```

### 缩放信息

```kotlin
// 获取当前缩放比例
sketchZoomImageView.scale

// 获取当前旋转角度（顺时针）
sketchZoomImageView.rotateDegrees

// 获取最小缩放比例
sketchZoomImageView.minScale

// 获取最大缩放比例
sketchZoomImageView.maxScale

// 获取当前预览图上用户能看到的区域（不受旋转影响）
sketchZoomImageView.getVisibleRect(Rect())

// more ...
```

### 手势缩放监听

```kotlin
// 监听缩放变化
sketchZoomImageView.addOnScaleChangeListener { scaleFactor: Float, focusX: Float, focusY: Float ->

}

// 监听单击
sketchZoomImageView.onViewTapListener = { view: View, x: Float, y: Float ->

}

// 监听长按
sketchZoomImageView.onViewLongPressListener = { view: View, x: Float, y: Float ->

}

// more ...
```

> 注意：当没有注册 OnViewTapListener 或 OnViewLongPressListener 时，[SketchZoomImageView] 会尝试回调 ImageView 的 OnClickListener 或 OnLongClickListener

### ArrayIndexOutOfBoundsException 与 IllegalArgumentException：pointerIndex out of range

由于是参考 [PhotoView] 实现的不可避免的也有这个异常，推荐在 Activity 里拦截，如下：

```kotlin
class ImageDetailActivity : AppCompatActivity() {

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        return try {
            super.dispatchTouchEvent(ev)
        } catch (e: RuntimeException) {
            e.printStackTrace()
            true
        }
    }
}
```

### 超大图采样

超大图通常尺寸巨大，想要完整读取到内存肯定会让 App 因内存不足而崩掉

[SketchZoomImageView] 通过 BitmapRegionDecoder 支持了超大图采样的功能，避免 App 崩溃

什么情况下会开启超大图采样功能？

1. 图片是 BitmapRegionDecoder支持的类型
2. Bitmap 尺寸比原始图片小
3. 图片是通过 Sketch 加载的

### Lifecycle

[SketchZoomImageView] 能够监听 Lifecycle 的状态，在 pause 状态时暂停超大图采样并释放所有碎片的 Bitmap，在 resume
状态时恢复超大图采样并重新加载碎片，这样能够在 Fragment 或 Activity 切换到后台或不显示时主动释放内存

[SketchZoomImageView] 会从 [DisplayRequest] 获取 Lifecycle，[DisplayRequest] 优先从 [SketchZoomImageView]
.context 上获取 Lifecycle，这样通常获取到的是 Activity 的 Lifecycle，一般情况下是够用的

如果 [SketchZoomImageView] 是在 ViewPager + Fragment 的组合中使用那么需要主动将 Fragment 的
viewLifecycleOwner.lifecycle 设置给
[DisplayRequest]，如下：

```kotlin
class MyFragment : Fragment() {

    override fun onViewCreated(view: View) {
        // ...
        sketchZoomImageView.displayImage("https://www.sample.com/image.jpg") {
            lifecycle(viewLifecycleOwner.lifecycle)
        }
    }
}
```

### 超大图采样监听

```kotlin
// 监听碎片变化
sketchZoomImageView.addOnTileChangedListener {

}
```

[Sketch]: ../../sketch/src/main/java/com/github/panpf/sketch/Sketch.kt

[BitmapRegionDecoder]: https://developer.android.google.cn/reference/android/graphics/BitmapRegionDecoder.html

[PhotoView]: https://github.com/chrisbanes/PhotoView

[SketchZoomImageView]: ../../sketch-zoom/src/main/java/com/github/panpf/sketch/zoom/SketchZoomImageView.kt

[ReadModeDecider]: ../../sketch-zoom/src/main/java/com/github/panpf/sketch/zoom/ReadModeDecider.kt

[LongImageReadModeDecider]: ../../sketch-zoom/src/main/java/com/github/panpf/sketch/zoom/ReadModeDecider.kt

[DisplayRequest]: ../../sketch/src/main/java/com/github/panpf/sketch/request/DisplayRequest.kt