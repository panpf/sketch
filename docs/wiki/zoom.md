Sketch新增了ImageZoomer可以让SketchImageView支持手势缩放图片，那么现在你可以果断的抛弃其它的缩放工具了

#### 如何开启

```java
SketchImageView sketchImageView = ...;
sketchImageView.setZoomEnabled(true);
```

#### 对比PhotoView

ImageZoomer是直接在PhotoView基础上做的，并且做了以下改进：
>* ImageZoomer的双击缩放只有两级，并且是根据图片的尺寸、ImageView的宽高以及ScaleType动态计算的，而PhotoView则是固定的三级双击缩放比例，体验不好
>* 手动持续缩放时如果超过了最小比例或最大比例时PhotoView直接就拉不动了，而ImageZoomer依然可以缩放，并且超过后会有种拉橡皮筋的感觉，松手后自动回滚到最小或最大缩放比例，体验更好
>* ImageZoomer优化了scrollEdge的判断，修复了在不能整除的缩放比例下，无法识别边缘的BUG
>* ImageZoomer增加了滑动条，可以方便的看到当前滑动的位置
>* ImageZoomer增加了定位功能，可以指定图片上的一个点，然后以动画的方式移动到这个点

#### 缩放

```java
// 放大3倍（不使用动画）
imageZoomer.zoom(3f);

// 放大3倍并使用动画
imageZoomer.zoom(3f, true);

// 以100x200为中心点放大3倍并使用动画
imageZoomer.zoom(3f, 100f, 200f, true);
```

设置的缩放比例不能小于最小缩放比例也不能大于最大缩放比例

通过zoom方法设置的缩放比例只是临时性的并不会一直保持，其它任何缩放行为和更新行为都会覆盖此缩放比例

#### 旋转

```java
// 旋转到180°
imageZoomer.rotateTo(180);

// 顺时针再旋转90°
imageZoomer.rotateBy(90);
```

`目前只支持90°、180°、270°旋转`

旋转角度是会一直存在的

#### 定位

```java
// 定位到100x200（不用考虑旋转角度）
imageZoomer.location(100f, 200f);

// 定位到100x200并使用动画（不用考虑旋转角度）
imageZoomer.location(100f, 200f, true);
```

通过location方法设置的位置只是临时性的并不会一直保持，其它任何位移行为和更新行为都会覆盖此位置

#### 缩放比例

ImageZoomer会根据图片的尺寸、ImageView的宽高以及ScaleType动态计算合理的最小、最大缩放比例

另外ImageZoomer的双击缩放比例则只有两级，即在最小缩放比例和最大缩放比例之间切换，在保证了合理的最小、最大缩放比例的前提下，这样能简化用户的操作，提升用户体验

先介绍几个概念：
>* fillZoomScale：能够让他图片的宽或高充满view的缩放比例
>* fullZoomScale：能够看到图片全貌的缩放比例
>* originZoomScale：如果开启了分块显示超大图功能就是能够让原图一比一显示的缩放比例，否则的话这个固定是1.0f

最小缩放比例：
>* ScaleType是CENTER或ScaleType是ENTER_INSIDE并且图尺寸片比view小：`1.0f`
>* ScaleType是CENTER_CROP：`fillZoomScale`
>* ScaleType是FIT_START/FIT_CENTE/FIT_END或ScaleType是ENTER_INSIDE并且图尺寸片比view大：`fullZoomScale`
>* ScaleType是FIT_XY：`fullZoomScale`

最大缩放比例：
>* ScaleType是CENTER或ScaleType是ENTER_INSIDE并且图尺寸片比view小：`Math.max(originZoomScale, fillZoomScale)`
>* ScaleType是CENTER_CROP：`Math.max(originZoomScale, fillZoomScale * 1.5f)`
>* ScaleType是FIT_START/FIT_CENTE/FIT_END或ScaleType是ENTER_INSIDE并且图尺寸片比view大：`Math.max(originZoomScale, fillZoomScale)`
>* ScaleType是FIT_XY：`fullZoomScale`

#### 阅读模式

对于宽高相差特别大的图片（比如长微博），如果上来就显示全貌，那么肯定是什么也看不清除，用户必须双击一下放大才能看清楚，那么针对这样的图片ImageZoomer特别支持了阅读模式来提高用户体验

所以在显示满足阅读模式的图片时会默认就显示最大缩放比例直接将图片放大

```java
// 开启阅读模式
imageZoomer.setReadMode(true);
```

`只有宽是高的3倍，或高是宽的2倍的图片才能使用阅读模式`

如果你想修改这个计算规则你可以继承ImageSizeCalculator重写其canUseReadModeByHeight(int, int)和canUseReadModeByWidth(int, int)方法，然后通过Sketch.with(context).getConfiguration().setImageSizeCalculator(ImageSizeCalculator)方法应用

#### 滑动条

在滑动查看特别大的图片的时候通常由于看不道滑动进度而感到烦躁，于是ImageZoomer特别支持了滑动条

滑动时显示，无操作800毫秒后自动隐藏

#### 配置

```java
// 禁用手势缩放
imageZoomer.setZoomable(false);

// 设置双击缩放动画时长
imageZoomer.setZoomDuration(1000);

// 设置双击缩放动画插值器
imageZoomer.setZoomInterpolator(new AccelerateDecelerateInterpolator());

// 修改缩放类型为FIX_START
imageZoomer.setScaleType(ScaleType.FIX_START);

// 开启阅读模式
imageZoomer.setReadMode(true);
```

#### 获取信息

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

#### 监听

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

当没有注册OnViewTapListener或OnViewLongPressListener时，ImageZoomer会尝试回调ImageView的OnClickListener或OnLongClickListener

#### ArrayIndexOutOfBoundsException与IllegalArgumentException：pointerIndex out of range

由于是在PhotoView基础上做的不可避免的也有这个异常，这里推荐的处理方法是在Activity里拦截，如下：

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
