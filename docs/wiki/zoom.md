# 手势缩放、旋转图片

图片的手势缩放几乎是每个需要展示图片的 APP 的必备功能，然而其它几款流行的图片加载器 [Fresco]、[Glide]、[Picasso] 都没有提供手势缩放支持，单独提供手势缩放的 View 倒是有几款，例如 [gesture-imageview]、[PhotoView]

Sketch 是目前唯一提供手势缩放支持的图片加载器，代码基于 [PhotoView] ，核心类是 [ImageZoomer]

### 对比 PhotoView

[ImageZoomer] 对比 [PhotoView] 做了以下改进：

* 双击缩放 从 [PhotoView] 的三级减少到两级，操作更简单
* [PhotoView] 的双击缩放比例是固定的，而 [ImageZoomer] 是根据图片的尺寸、ImageView 的宽高以及 ScaleType 动态计算的，体验更好
* 手动持续缩放时如果超过了最小比例或最大比例时 [PhotoView] 直接就拉不动了，而 [ImageZoomer] 依然可以缩放，超过后会有种拉橡皮筋的感觉，松手后自动回滚到最小或最大缩放比例，体验更好
* [ImageZoomer] 优化了 scrollEdge 的判断，修复了在不能整除的缩放比例下，无法识别边缘的 BUG
* [ImageZoomer] 增加了滑动条，可以方便的看到当前滑动的位置
* [ImageZoomer] 增加了定位功能，可以指定图片上的一个点，然后以动画的方式移动到这个点
* [ImageZoomer] 增加了阅读模式，对于微博长图的阅读体验更好

### 开启手势缩放

```java
SketchImageView sketchImageView = ...;
sketchImageView.setZoomEnabled(true);
```

### 缩放

```java
// 放大 3 倍（不使用动画）
imageZoomer.zoom(3f);

// 放大 3 倍并使用动画
imageZoomer.zoom(3f, true);

// 以 100x200 为中心点放大 3 倍并使用动画
imageZoomer.zoom(3f, 100f, 200f, true);
```

注意：
* 设置的缩放比例不能小于最小缩放比例也不能大于最大缩放比例
* 通过 zoom 方法设置的缩放比例只是临时性的并不会一直保持，其它任何缩放行为和更新行为都会覆盖此缩放比例

### 旋转

```java
// 旋转到 180°
imageZoomer.rotateTo(180);

// 顺时针再旋转 90°
imageZoomer.rotateBy(90);
```

`目前只支持90°、180°、270°旋转`

旋转角度是会一直存在的

### 定位

```java
// 定位到 100x200（不用考虑旋转角度）
imageZoomer.location(100f, 200f);

// 定位到 100x200 并使用动画（不用考虑旋转角度）
imageZoomer.location(100f, 200f, true);
```

通过 location 方法设置的位置只是临时性的并不会一直保持，其它任何位移行为和更新行为都会覆盖此位置

### 分块显示超大图

[ImageZoomer] 支持分块显示超大图，详情请参考 [分块显示超大图片][block_display.md]

### 阅读模式

对于宽高相差特别大的图片（比如长微博），如果上来就显示全貌，那么肯定是什么也看不清除，用户必须双击一下放大才能看清楚，那么针对这样的图片 [ImageZoomer] 特别支持了阅读模式来提高用户体验

在显示满足阅读模式的图片时会默认就显示最大缩放比例直接将图片放大

```java
SketchImageView sketchImageView = ...;
sketchImageView.setZoomEnabled(true);

// 开启阅读模式
sketchImageView.getImageZoomer().setReadMode(true);
```

`只有宽是高的 3 倍，或高是宽的 2 倍的图片才能使用阅读模式`

如果你想修改这个计算规则你可以继承 [ImageSizeCalculator] 重写其 canUseReadModeByHeight(int, int)和 canUseReadModeByWidth(int, int) 方法，然后通过 Sketch.with(context).getConfiguration().setSizeCalculator(ImageSizeCalculator) 方法应用

### 滑动条

在滑动查看特别大的图片的时候通常由于看不道滑动进度而感到烦躁，于是 [ImageZoomer] 特别支持了滑动条

滑动时显示，无操作 800 毫秒后自动隐藏

### 缩放比例的计算规则

[ImageZoomer] 根据图片的尺寸、 ImageView 的宽高以及 ScaleType 动态计算最合适的最小、最大缩放比例

另外 [ImageZoomer] 的双击缩放比例则只有两级，即在最小缩放比例和最大缩放比例之间切换，在保证了最合适的最小、最大缩放比例的前提下，这样能简化用户的操作

先介绍几个概念：
* fillZoomScale：能够让图片的宽或高充满 ImageView 的缩放比例
* fullZoomScale：能够看到图片全貌的缩放比例
* originZoomScale：如果开启了分块显示超大图功能就是能够让原图一比一显示的缩放比例，否则的话这个固定是 1.0f

如何计算最小缩放比例？
* ScaleType 是 CENTER 或 ScaleType 是 ENTER_INSIDE 并且图尺寸片比 ImageView 小：`1.0f`
* ScaleType 是 CENTER_CROP：`fillZoomScale`
* ScaleType 是 FIT_START/FIT_CENTE/FIT_END 或 ScaleType 是 ENTER_INSIDE 并且图尺寸片比 ImageView 大：`fullZoomScale`
* ScaleType 是 FIT_XY：`fullZoomScale`

如何计算最大缩放比例？
* ScaleType 是 CENTER 或 ScaleType 是 ENTER_INSIDE 并且图尺寸片比 ImageView 小：`Math.max(originZoomScale, fillZoomScale)`
* ScaleType 是 CENTER_CROP：`Math.max(originZoomScale, fillZoomScale * 1.5f)`
* ScaleType 是 FIT_START/FIT_CENTE/FIT_END 或 ScaleType 是 ENTER_INSIDE 并且图尺寸片比 ImageView 大：`Math.max(originZoomScale, fillZoomScale)`
* ScaleType 是 FIT_XY：`fullZoomScale`

### 自定义缩放比例

[ImageZoomer] 提供了 [ZoomScales] 接口可以自定义最大、最小以及双击缩放比例，可参考 [AdaptiveTwoLevelScales] 实现自定义，然后通过 ImageZoomer.setZoomScales(ZoomScales) 方法设置即可

### 配置

```java
// 禁用手势缩放
imageZoomer.setZoomable(false);

// 设置双击缩放动画时长
imageZoomer.setZoomDuration(1000);

// 设置双击缩放动画插值器
imageZoomer.setZoomInterpolator(new AccelerateDecelerateInterpolator());

// 修改缩放类型为 FIX_START
imageZoomer.setScaleType(ScaleType.FIX_START);

// 开启阅读模式
imageZoomer.setReadMode(true);
```

### 获取信息

```java
// 获取当前缩放比例
float zoomScale = imageZoomer.getZoomScale();

// 获取当前旋转角度（顺时针）
float rotateDegrees = imageZoomer.getRotateDegrees();

// 获取缩放类型
ScaleType scaleType = imageZoomer.getScaleType();

// 获取最小缩放比例
float minZoomScale = imageZoomer.getMinZoomScale();

// 获取最大缩放比例
float maxZoomScale = imageZoomer.getMaxZoomScale();

// 获取当前预览图上的用户能看到的区域（不受旋转影响）
Rect visibleRect = new Rect();
imageZoomer.getVisibleRect(visibleRect);

// 获取当前绘制Matrix（包含位移、旋转、缩放等等所有的信息）
Matrix drawMatrix = new Matrix();
imageZoomer.getDrawMatrix(drawMatrix);

// 获取当前绘制的区域
Rect drawRect = new Rect();
imageZoomer.getDrawRect(drawRect);

// 获取预览图片尺寸
Point drawableSize = imageZoomer.getDrawableSize();
```

### 监听

```java
// 设置Fling手势监听器
imageZoomer.setOnDragFlingListener(OnDragFlingListener)

// 设置缩放变化监听器
imageZoomer.setOnScaleChangeListener(OnScaleChangeListener)

// 添加一个Matrix变化监听器
imageZoomer.addOnMatrixChangeListener(OnMatrixChangeListener)

// 删除一个Matrix变化监听器
imageZoomer.removeOnMatrixChangeListener(OnMatrixChangeListener)

// 单击ImageView监听器
imageZoomer.setOnViewTapListener(OnViewTapListener);

// 长按ImageView监听器
imageZoomer.setOnViewLongPressListener(OnViewLongPressListener)

// 旋转变化监听器
imageZoomer.setOnRotateChangeListener(OnRotateChangeListener)
```

当没有注册 OnViewTapListener 或 OnViewLongPressListener 时，[ImageZoomer] 会尝试回调 ImageView 的 OnClickListener 或 OnLongClickListener

### ArrayIndexOutOfBoundsException 与 IllegalArgumentException：pointerIndex out of range

由于是在 [PhotoView] 基础上做的不可避免的也有这个异常，这里推荐的处理方法是在 Activity 里拦截，如下：

```java
@Override
public boolean dispatchTouchEvent(MotionEvent ev) {
    boolean result = true;
    try {
        result = super.dispatchTouchEvent(ev);
    } catch (RuntimeException e) {
        e.printStackTrace();
    }

    return result;
}
```


[Fresco]: https://github.com/facebook/fresco
[Glide]: https://github.com/bumptech/glide
[Picasso]: https://github.com/square/picasso
[gesture-imageview]: https://github.com/jasonpolites/gesture-imageview
[PhotoView]: https://github.com/chrisbanes/PhotoView
[ImageZoomer]: ../../sketch/src/main/java/me/panpf/sketch/zoom/ImageZoomer.java
[ImageSizeCalculator]: ../../sketch/src/main/java/me/panpf/sketch/decode/ImageSizeCalculator.java
[block_display.md]: block_display.md
[ZoomScales]: ../../sketch/src/main/java/me/panpf/sketch/zoom/ZoomScales.java
[AdaptiveTwoLevelScales]: ../../sketch/src/main/java/me/panpf/sketch/zoom/AdaptiveTwoLevelScales.java
