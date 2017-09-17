# 通过 ImageShaper 在绘制时改变图片的形状

[ImageShaper] 用来在绘制时修改图片的形状，特点如下：

* 通过 BitmapShader 实现，不会创建新的 Bitmap，节省内存
* 会应用于 loadingImage、errorImage 以及 pauseDownloadImage

### 使用

可在 [DisplayOptions] 和 [DisplayHelper] 中使用，例如：

```java
// SketchImageView
SketchImageView sketchImageView = ...;
sketchImageView.getOptions().setImageShaper(new RoundRectImageShaper(10));

// DisplayHelper
Sketch.with(context).display(uri, sketchImageView)
    .shaper(new RoundRectImageShaper(10))
    .commit();
```

目前内置了两种 [ImageShaper]：

* RoundRectImageShaper：圆角矩形，还支持描边
* CircleImageShaper：圆形，还支持描边

如果需要在绘制时同时改变图片的尺寸就要用到 [ShapeSize] 了

[ImageShaper]: ../../sketch/src/main/java/me/xiaopan/sketch/shaper/ImageShaper.java
[ImageProcessor]: image_processor.md
[LoadOptions]: ../../sketch/src/main/java/me/xiaopan/sketch/request/LoadOptions.java
[DisplayOptions]: ../../sketch/src/main/java/me/xiaopan/sketch/request/DisplayOptions.java
[LoadHelper]: ../../sketch/src/main/java/me/xiaopan/sketch/request/LoadHelper.java
[DisplayHelper]: ../../sketch/src/main/java/me/xiaopan/sketch/request/DisplayHelper.java
[ShapeSize]: shape_size.md
