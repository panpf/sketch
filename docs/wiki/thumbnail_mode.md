# 使用 thumbnailMode 属性显示更清晰的缩略图

当你需要在一个比较小的 ImageView 上显示一张很大图片的缩略图的时候，你会得到一张缩小了很多倍很模糊的缩略图

例如 ImageView 宽高为 400x400，图片宽高为 30000x960，用 400x400 和 30000x960 计算得出的 inSampleSize 为 16，这样得到的缩略图尺寸为 1875x60，这张缩略图是极其模糊，无法辨别任何内容

为此 Sketch 特别支持了缩略图模式，专门用于在较小的 ImageView 上显示清晰的缩略图，开启缩略图模式后会根据 ImageView 的尺寸在原图上找到对应的映射区域，然后通过 BitmapRegionDecoder 读取映射区域的缩略图

继续上面的例子，30000x960 的原图中计算出 400x400 的映射区域是 14520,0-15480,960，宽高是 960x960，我们再用 400x400 和 960x960 计算得出的 inSampleSize 为 4，最后将 14520,0-15480,960 区域缩小 4 倍读取出来，这样就清晰的多了

### 支持的图片类型和系统版本

* jpeg、png：API 10（2.3.3）及其以上
* webp：API 14（4.0）及其以上

### 使用条件

1. 满足上述对图片类型和系统版本的要求
2. 配置 [Resize] 并且 [Resize] 的宽高比和原图的宽高比相差1.5倍

例如上述例子中 [Resize] 宽高比为 400/400=1.0f，原图宽高比为 30000/960=32.4，32.4f / 1.0f >= 1.5f 为 true 满足条件

### 使用

```java
SketchImageView sketchImageView = ...;

sketchImageView.getOptions()
    .setMaxSize(400, 400)
    .setResize(400, 400)
    .setThumbnailMode(true);

sketchImageView.displayImage("http://t.cn/RShdS1f");
```

### 映射区域计算规则

映射区域计算规则根据 [Resize] 的 scaleType 属性而定，跟 ImageView.ScaleType 效果一样


[Resize]: ../../sketch/src/main/java/me/xiaopan/sketch/request/Resize.java
