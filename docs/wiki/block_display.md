# 分块显示超大图片

超大图片一直都是所有图片显示控件的噩梦，它们通常尺寸巨大，要想完整读取肯定会让 APP 因内存不足而崩掉

然后 Android 官方并没有提供现成可用的控件来解决这个问题，仅在 API 10 之后提供了 [BitmapRegionDecoder] 可以让我们读取完整图片的部分区域

纵观其它几款流行的图片加载器 [Fresco]、[Glide]、[Picasso] 都没有提供超大图片支持，而单独支持超大图片的 View 倒是有几款，例如 [Subsampling Scale Image View]、[WorldMap]、[LargeImage] ， 但都做的不够好或者没法跟现有的图片加载框架集成，做的不好还好说，不能跟现有图片加载框架集成用起来就很恶心了

下面用 [Glide] 代指现有的图片框架，用 [Subsampling Scale Image View] 代指单独的分块显示超大图控件来举例说明两者不能集成时的不便之处：

1. 图片详情页必须准备两个 ImageView，一个 [Glide] 是用的，一个是 [Subsampling Scale Image View]。先用 [Glide] 加载完图片，然后根据结果（如果返回了原始图片尺寸的话，没有的话你还要自己去解析并判断）判断这张图片需不需要用 [Subsampling Scale Image View]，如果需要的话再将 [Subsampling Scale Image View] 显示出来遮盖住 [Glide] 用的 ImageView，并初始化 [Subsampling Scale Image View]
2. [Subsampling Scale Image View] 要继续优化的话，还会涉及到内存缓存和 bitmap 复用池，如果 [Subsampling Scale Image View] 和 [Glide] 分别单独维护一套的话，APP 的可用内存就剩不了多少了，因此这两者必须能共用一套内存缓存和 bitmap 复用池
3. [Glide] 支持的 uri，[Subsampling Scale Image View] 未必支持

### 使用

Sketch 是目前唯一提供了超大图片支持的图片加载器，核心类是 [BlockDisplayer]，依赖于[手势缩放][zoom]功能，是[手势缩放][zoom]能的一部分

#### 支持的图片类型和系统版本
* jpeg、png：API 10（2.3.3）及其以上
* webp：API 14（4.0）及其以上

#### 使用条件

只要满足上述对图片类型和系统版本的要求并且读到内存的图片比原始图片小就可以使用超大图功能

#### 开启

只要开启[手势缩放][zoom]就自动开启了分块显示功能

#### 旋转

[BlockDisplayer] 支持跟随[手势缩放][zoom]功旋转，但只支持 90°、180°、270° 旋转

#### 在 ViewPager 中使用

由于 ViewPager 会至少缓存三个页面，所以至少会有三个 [BlockDisplayer] 同时工作，这样对内存的消耗是非常大的

因此 [BlockDisplayer] 特地提供了 setPause(boolean) 方法来减少在 ViewPager 中的内存消耗，如下：

```java
public class MyFragment extends Fragment {
    private SketchImageView sketchImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = ...;
        sketchImageView = ...;

        sketchImageView.setZoomEnabled(true);
        // 初始化超大图查看器的暂停状态，这一步很重要
        sketchImageView.getZoomer().getBlockDisplayer().setPause(!isVisibleToUser());

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
        // 不可见的时候暂停超大图查看器，节省内存，可见的时候恢复
        if (sketchImageView != null && sketchImageView.isZoomEnabled()) {
            sketchImageView.getZoomer().getBlockDisplayer().setPause(!isVisibleToUser);
        }
    }
}
```

#### 其它方法

```java
// 显示碎片范围
sketchImageView.getZoomer().getBlockDisplayer().setShowBlockRect(true);

// 获取当前碎片数量
int blockSize = sketchImageView.getZoomer().getBlockDisplayer().getBlockSize();

// 获取当前所有碎片占用的字节数
long allocationByteCount = sketchImageView.getZoomer().getBlockDisplayer().getAllocationByteCount();

// 设置碎片变化监听器
sketchImageView.getZoomer().getBlockDisplayer().setOnBlockChangedListener(BlockDisplayer.OnBlockChangedListener)
```

[BitmapRegionDecoder]: https://developer.android.google.cn/reference/android/graphics/BitmapRegionDecoder.html
[Fresco]: https://github.com/facebook/fresco
[Glide]: https://github.com/bumptech/glide
[Picasso]: https://github.com/square/picasso
[WorldMap]: https://github.com/johnnylambada/WorldMap
[Subsampling Scale Image View]: https://github.com/davemorrissey/subsampling-scale-image-view
[LargeImage]: https://github.com/LuckyJayce/LargeImage
[BlockDisplayer]: ../../sketch/src/main/java/me/xiaopan/sketch/zoom/BlockDisplayer.java
[zoom]: zoom.md
