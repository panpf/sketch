# 使用 ImageProcessor 在解码后改变图片

[ImageProcessor] 用来将图片读取到内存之后对图片进行处理

### 使用

可在 [LoadOptions]/[DisplayOptions] 和 [LoadHelper]/[DisplayHelper] 中使用，例如：

```java
// SketchImageView
SketchImageView sketchImageView = ...;
sketchImageView.getOptions().setImageProcessor(new RoundRectImageProcessor(10));

// LoadHelper
Sketch.with(context).load(uri, listener)
    .processor(new RoundRectImageProcessor(10))
    .commit();

// DisplayHelper
Sketch.with(context).display(uri, sketchImageView)
    .processor(new RoundRectImageProcessor(10))
    .commit();
```

目前内置了以下几种 [ImageProcessor]：

* [ResizeImageProcessor]：默认的图片处理器，只会根据 resize 调整图片尺寸
* [CircleImageProcessor]：将图片处理成圆形的，同时也会根据 resize 调整图片尺寸
* [RoundRectImageProcessor]：将图片处理成圆角的，同时也会根据 resize 调整图片尺寸
* [ReflectionImageProcessor]：将图片处理成倒影效果的，同时也会根据 resize 调整图片尺寸
* [GaussianBlurImageProcessor]：对图片进行高斯模糊处理，同时也会根据 resize 调整图片尺寸

注意：
* [ImageProcessor] 的职责不仅要将图片处理成各种效果的还要根据 [Resize] 调整图片的尺寸，有关 [Resize] 具体规则请参考 [使用 Resize 精确修改图片的尺寸][resize]
* [ImageProcessor] 执行成功后，会返回一个新的 bitmap

#### 自定义

自定义 [ImageProcessor] 时有以下几点需要注意：

1. 需要处理 [Resize]，你可以调用 Sketch.getConfiguration().getResizeCalculator().calculator(bitmap.getWidth(), bitmap.getHeight(), resize.getWidth(), resize.getHeight(), resize.getScaleType(), resize != null && resize.getMode() == Resize.Mode.EXACTLY_SAME) 来计算新图片的大小、srcRect、destRect等，然后根据返回的 ResizeCalculator.Result 来创建新的图片，请参考 [RoundRectImageProcessor]
2. 通过 [ImageProcessor] 的 process() 方法传进去的 Bitmap 在处理完之后无需回收它，Sketch 会去回收
3. 创建新的 bitmap 之前，先从 BitmapPool 中查找可复用 bitmap，实在没有再创建新的 bitmap
4. 在处理的过程中产生的过渡 Bitmap 在用完之后一定要调用 BitmapPoolUtils.freeBitmapToPool(Bitmap, BitmapPool) 回收掉

自定义的 [ImageProcessor] 写好后通过 [LoadOptions]/[DisplayOptions] 的 setImageProcessor(ImageProcessor) 方法或 [LoadHelper]/[DisplayHelper] 的 processor(ImageProcessor) 方法使用即可

[ImageProcessor]: ../../sketch/src/main/java/me/xiaopan/sketch/process/ImageProcessor.java
[ImageDecoder]: ../../sketch/src/main/java/me/xiaopan/sketch/decode/ImageDecoder.java
[ResizeImageProcessor]: ../../sketch/src/main/java/me/xiaopan/sketch/process/ResizeImageProcessor.java
[CircleImageProcessor]: ../../sketch/src/main/java/me/xiaopan/sketch/process/CircleImageProcessor.java
[ReflectionImageProcessor]: ../../sketch/src/main/java/me/xiaopan/sketch/process/ReflectionImageProcessor.java
[RoundRectImageProcessor]: ../../sketch/src/main/java/me/xiaopan/sketch/process/RoundRectImageProcessor.java
[GaussianBlurImageProcessor]: ../../sketch/src/main/java/me/xiaopan/sketch/process/GaussianBlurImageProcessor.java
[resize]: resize.md
[Resize]: resize.md
[LoadOptions]: ../../sketch/src/main/java/me/xiaopan/sketch/request/LoadOptions.java
[DisplayOptions]: ../../sketch/src/main/java/me/xiaopan/sketch/request/DisplayOptions.java
[LoadHelper]: ../../sketch/src/main/java/me/xiaopan/sketch/request/LoadHelper.java
[DisplayHelper]: ../../sketch/src/main/java/me/xiaopan/sketch/request/DisplayHelper.java
