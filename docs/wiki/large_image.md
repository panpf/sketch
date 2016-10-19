Sketch新增了LargeImageViewer可以让SketchImage支持分块显示超级大图，现在你可以抛弃SubsamplingScaleImageView了

#### 如何开启

```
SketchImageView sketchImageView = ...;
sketchImageView.setSupportLargeImage(true);
```

LargeImageViewer需要依赖ImageZoomer，因此LargeImageViewer会自动开启ImageZoomer

在关闭LargeImageViewer的时候如果检测到是ImageZoomer是被LargeImageViewer开启的也会一并关闭ImageZoomer

#### 支持的图片类型和系统版本
>* jpeg、png：API 10（2.3.3）及其以上
>* webp：API 14（4.0）及其以上

#### 使用条件

只要满足上述对图片类型和系统版本的要求并且读到内存的图片比原始图片小就可以使用超大图功能

#### 旋转

LargeImageViewer支持跟随ImageZoomer旋转，但只支持90°、180°、270°旋转

#### 配置

```java

// 显示碎片范围
largeImageViewer.setShowTileRect(true);
```

#### 获取信息

```java

// 获取当前碎片数量
int tiles = largeImageViewer.getTiles();

// 获取当前所有碎片占用的字节数
long tilesByteCount = largeImageViewer.getTilesAllocationByteCount();
```

#### 监听

```java

// 设置碎片变化监听器
largeImageViewer.setOnTileChangedListener(LargeImageViewer.OnTileChangedListener)
```
