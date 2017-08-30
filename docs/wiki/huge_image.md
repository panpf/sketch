Sketch 通过 HugeImageViewer 可以让 SketchImageView 支持分块显示超级大图，现在你可以替换掉 SubsamplingScaleImageView 了

### 如何开启

```java
SketchImageView sketchImageView = ...;
sketchImageView.setHugeImageEnabled(true);
```
注意：
* HugeImageViewer 需要依赖 ImageZoomer，因此 HugeImageViewer 会自动开启 ImageZoomer
* 在关闭 HugeImageViewer 的时候如果检测到 ImageZoomer 是被 HugeImageViewer 开启的也会一并关闭 ImageZoomer

### 支持的图片类型和系统版本
>* jpeg、png：API 10（2.3.3）及其以上
>* webp：API 14（4.0）及其以上

### 使用条件

只要满足上述对图片类型和系统版本的要求并且读到内存的图片比原始图片小就可以使用超大图功能

### 旋转

HugeImageViewer 支持跟随 ImageZoomer 旋转，但只支持 90°、180°、270° 旋转

### 在 ViewPager 中使用
由于 ViewPager 会至少缓存三个页面，所以至少会有三个 HugeImageViewer 同时工作，这样对内存的消耗是非常大的

因此 HugeImageViewer 特地提供了 setPause(boolean) 方法来减少在 ViewPager 中的内存消耗，如下：

```java
public class MyFragment extends Fragment {
    private SketchImageView sketchImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = ...;
        sketchImageView = ...;

        // 初始化超大图查看器的暂停状态，这一步很重要
        sketchImageView.getHugeImageViewer().setPause(!isVisibleToUser());

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
        if (sketchImageView != null && sketchImageView.isHugeImageEnabled()) {
            sketchImageView.getHugeImageViewer().setPause(!isVisibleToUser);
        }
    }
}
```

### 其它方法

```java
// 显示碎片范围
hugeImageViewer.setShowTileRect(true);

// 获取当前碎片数量
int tiles = hugeImageViewer.getTiles();

// 获取当前所有碎片占用的字节数
long tilesByteCount = hugeImageViewer.getTilesAllocationByteCount();

// 设置碎片变化监听器
hugeImageViewer.setOnTileChangedListener(HugeImageViewer.OnTileChangedListener)
```
