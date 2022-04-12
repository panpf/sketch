# Zoom

`需要导入 sketch-zoom 模块`

Sketch 的 SketchZoomImageView 提供了手势缩放以及分块显示超大图功能，手势缩放功能参考 PhotoView 实现

## 对比 PhotoView

* 双击缩放层级：PhotoView 的三级减少到两级，操作更简单
* 双击缩放比例：PhotoView 的双击缩放比例是固定的，而 SketchZoomImageView 是根据图片的尺寸、View 的宽高以及 ScaleType 动态计算的，体验更好
* 边界阻尼：手动缩放超过了最小或最大比例时 PhotoView 直接就拉不动了，而 SketchZoomImageView 会有种拉橡皮筋的感觉，体验更好
* 边界判定：SketchZoomImageView 优化了 scrollEdge 的判断，修复了在不能整除的缩放比例下，无法识别边缘的 BUG
* 滑动条：SketchZoomImageView 增加了滑动条，可以方便的看到当前滑动的位置
* 定位：SketchZoomImageView 增加了定位功能，可以指定图片上的一个点，然后以动画的方式移动到这个点
* 阅读模式：SketchZoomImageView 增加了阅读模式，对于长图的阅读体验更好

## 使用

直接用 [SketchZoomImageView] 替换 ImageView 即可

```kotlin
sketchZoomImageView.displayImage("https://www.sample.com/image.jpg")
```

### 缩放

```kotlin
// 放大 3 倍（不使用动画）
sketchZoomImageView.zoomAbility.zoom(3f);

// 放大 3 倍并使用动画
sketchZoomImageView.zoomAbility.zoom(3f, true);

// 以 100x200 为中心点放大 3 倍并使用动画
sketchZoomImageView.zoomAbility.zoom(3f, 100f, 200f, true);
```

> 注意：
> * 设置的缩放比例不能小于最小缩放比例也不能大于最大缩放比例
> * 通过 zoom 方法设置的缩放比例只是临时性的并不会一直保持，其它任何缩放行为和更新行为都会覆盖此缩放比例

### 旋转

```kotlin
// 旋转到 180°
sketchZoomImageView.zoomAbility.rotateTo(180)

// 顺时针再旋转 90°
sketchZoomImageView.zoomAbility.rotateBy(90)
```

> 注意：只支持90°、180°、270°旋转

旋转角度是会一直存在的

### 定位

```kotlin
// 定位到 100x200（不用考虑旋转角度）
sketchZoomImageView.zoomAbility.location(100f, 200f)

// 定位到 100x200 并使用动画（不用考虑旋转角度）
sketchZoomImageView.zoomAbility.location(100f, 200f, true)
```

> 注意：通过 location 方法设置的位置只是临时性的并不会一直保持，其它任何位移行为和更新行为都会覆盖此位置

### 滑动条

滑动条可以让你在查看长图时清楚的知道当前的位置，SketchZoomImageView 默认开启滑动条功能，你也可以关闭它，如下：

```kotlin
sketchZoomImageView.zoomAbility.scrollBarEnabled = false
```

滑动时显示，无操作 800 毫秒后自动隐藏

### 阅读模式

对于宽高相差特别大的图片（比如长微博图片），如果一开始显示全貌，那么什么也看不清楚，用户必须双击一下放大才能开始阅读

针对这样的图片 SketchZoomImageView 默认开启了阅读模式让其一开始就充满屏幕，这样用户就能直接开始阅读长微博图片的内容了

如何判定是否使用阅读模式？只有宽是高的 3 倍，或高是宽的 2 倍的图片才能使用阅读模式

如果你想修改这个判定你可以实现 ReadModeDecider 接口定义你自己的判定类，然后通过 ZoomAbility 的 readModeDecider 属性应用接口，如下：

```kotlin
class MyReadModeDecider : ReadModeDecider {

    override fun should(imageWidth: Int, imageHeight: Int): Boolean {
        // 实现你的判定规则
    }
}


sketchZoomImageView.zoomAbility.readModeDecider = MyReadModeDecider()
```

你也可以关闭阅读模式，如下：

```kotlin
sketchZoomImageView.zoomAbility.readModeEnabled = false
```

### 缩放信息

一些缩放相关信息你可以通过 ZoomAbility 获取，如下：

```kotlin
// 获取当前缩放比例
sketchZoomImageView.zoomAbility.zoomScale

// 获取当前旋转角度（顺时针）
sketchZoomImageView.zoomAbility.rotateDegrees

// 获取最小缩放比例
sketchZoomImageView.zoomAbility.minZoomScale

// 获取最大缩放比例
sketchZoomImageView.zoomAbility.maxZoomScale

// 获取当前预览图上用户能看到的区域（不受旋转影响）
sketchZoomImageView.zoomAbility.getVisibleRect(Rect())

// more ...
```

### 监听

```kotlin
// 设置缩放变化监听器
sketchZoomImageView.zoomAbility.addOnScaleChangeListener { scaleFactor: Float, focusX: Float, focusY: Float ->

}

// 单击监听器
sketchZoomImageView.zoomAbility.onViewTapListener = { view: View, x: Float, y: Float ->

}

// 长按监听器
sketchZoomImageView.zoomAbility.onViewLongPressListener = { view: View, x: Float, y: Float ->

}

// more ...
```

> 注意：当没有注册 OnViewTapListener 或 OnViewLongPressListener 时，SketchZoomImageView 会尝试回调 ImageView 的 OnClickListener 或 OnLongClickListener

### ArrayIndexOutOfBoundsException 与 IllegalArgumentException：pointerIndex out of range

由于是参考 [PhotoView] 实现的不可避免的也有这个异常，推荐在 Activity 里拦截，如下：

```kotlin
class ImageDetailActivity : AppCompatActivity() {

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        try {
            return super.dispatchTouchEvent(ev);
        } catch (e: RuntimeException) {
            e.printStackTrace();
            return true
        }
    }
}
```

### 分块显示超大图

超大图片通常尺寸巨大，想要完整读取到内存肯定会让 App 因内存不足而崩掉

SketchZoomImageView 通过 BitmapRegionDecoder 支持了分块显示超大图的功能，避免 App 崩溃

什么情况下会开启分块显示超大图功能？

1. 图片是 BitmapRegionDecoder支持的类型
2. Bitmap 尺寸比原始图片小
3. 图片是通过 Sketch 加载的

#### 在 ViewPager 中使用

由于 ViewPager 会至少缓存三个页面，所以至少会有三个 [BlockDisplayer] 同时工作，这样对内存的消耗是非常大的

因此 [BlockDisplayer] 特地提供了 setPause(boolean) 方法来减少在 ViewPager 中的内存消耗，如下：

```kotlin
public class MyFragment extends Fragment {
    private SketchImageView sketchImageView;

    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    ) {
        View view = ...;
        sketchImageView = ...;

        sketchImageView.setZoomEnabled(true);
        // 初始化分块显示器的暂停状态，这一步很重要
        sketchZoomImageView.zoomAbility.getBlockDisplayer().setPause(!isVisibleToUser());

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getUserVisibleHint()) {
            onUserVisibleChanged(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            onUserVisibleChanged(true);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isResumed()) {
            onUserVisibleChanged(isVisibleToUser);
        }
    }

    public boolean isVisibleToUser() {
        return isResumed() && getUserVisibleHint();
    }

    protected void onUserVisibleChanged(boolean isVisibleToUser) {
        // 不可见的时候暂停分块显示器，节省内存，可见的时候恢复
        if (sketchImageView != null && sketchImageView.isZoomEnabled()) {
            sketchZoomImageView.zoomAbility.getBlockDisplayer().setPause(!isVisibleToUser);
        }
    }
}
```

#### 其它方法

```kotlin
// 显示碎片范围
sketchZoomImageView.zoomAbility.getBlockDisplayer().setShowBlockRect(true);

// 获取当前碎片数量
int blockSize = sketchZoomImageView . zoomAbility . getBlockDisplayer ().getBlockSize();

// 获取当前所有碎片占用的字节数
long allocationByteCount = sketchZoomImageView . zoomAbility . getBlockDisplayer ().getAllocationByteCount();

// 设置碎片变化监听器
sketchZoomImageView.zoomAbility.getBlockDisplayer()
    .setOnBlockChangedListener(BlockDisplayer.OnBlockChangedListener)
```

[BitmapRegionDecoder]: https://developer.android.google.cn/reference/android/graphics/BitmapRegionDecoder.html

[Fresco]: https://github.com/facebook/fresco

[Glide]: https://github.com/bumptech/glide

[Picasso]: https://github.com/square/picasso

[WorldMap]: https://github.com/johnnylambada/WorldMap

[Subsampling Scale Image View]: https://github.com/davemorrissey/subsampling-scale-image-view

[LargeImage]: https://github.com/LuckyJayce/LargeImage

[BlockDisplayer]: ../../sketch-zoom/src/main/java/com/github/panpf/sketch/zoom/BlockDisplayer.java

[zoom]: zoom.md

[sketch_zoom_version_image]: https://api.bintray.com/packages/panpf/maven/sketch-zoom/images/download.svg

[sketch_zoom_version_link]: https://bintray.com/panpf/maven/sketch-zoom/_latestVersion#files

[Fresco]: https://github.com/facebook/fresco

[Glide]: https://github.com/bumptech/glide

[Picasso]: https://github.com/square/picasso

[gesture-imageview]: https://github.com/jasonpolites/gesture-imageview

[PhotoView]: https://github.com/chrisbanes/PhotoView

[ImageZoomer]: ../../sketch-zoom/src/main/java/com/github/panpf/sketch/zoom/ImageZoomer.java

[ImageSizeCalculator]: ../../sketch/src/main/java/com/github/panpf/sketch/decode/ImageSizeCalculator.java

[block_display.md]: block_display.md

[ZoomScales]: ../../sketch-zoom/src/main/java/com/github/panpf/sketch/zoom/ZoomScales.java

[AdaptiveTwoLevelScales]: ../../sketch-zoom/src/main/java/com/github/panpf/sketch/zoom/AdaptiveTwoLevelScales.java

[SketchZoomImageView]: ../../sketch-zoom/src/main/java/com/github/panpf/sketch/zoom/SketchZoomImageView.java
