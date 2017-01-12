ImageProcessor是用来在ImageDecoder解码完图片之后在对图片进行处理的

目前内置了以下几种ImageProcessor：
>* ResizeImageProcessor：默认的图片处理器，主要根据resize调整图片尺寸
>* CircleImageProcessor：可以将图片处理成圆形的，同时也会根据resize调整图片尺寸
>* ReflectionImageProcessor：可以将图片处理成倒影效果的，同时也会根据resize调整图片尺寸
>* RoundedCornerImageProcessor：可以将图片处理成圆角的，同时也会根据resize调整图片尺寸
>* GaussianBlurImageProcessor：可以对图片进行高斯模糊处理，同时也会根据resize调整图片尺寸

ImageProcessor的职责不仅要将图片处理成各种效果的还要负责根据resize调整图片的尺寸，具体原则是：
>* 首先保证最终返回的图片的宽高比同resize一致
>* 如果原图尺寸大于resize，那么就以resize为最终尺寸
>* 如果原图尺寸小于resize，那么就以原图尺寸为准缩小resize
>* 如果forceUseResize为true，那么必须以resize为最终尺寸
>* 如果resize为null，就不必改变尺寸
>* 根据ScaleType选择原图片上对应的区域绘制到新图片上

#### 自定义
自定义ImageProcessor有以下几点需要注意：

1. 你需要处理resize，你可以调用sketch.getConfiguration().getResizeCalculator().calculator(bitmap.getWidth(), bitmap.getHeight(), resize.getWidth(), resize.getHeight(), resize.getScaleType(), forceUseResize)来计算新图的大小、srcRect、destRect等，然后根据返回的ResizeCalculator.Result来创建新的图片
2. 通过ImageProcessor的process()方法传进去的Bitmap在你处理完之后你无需回收它，Sketch会去处理
3. 创建新的bitmap之前，先从BitmapPool中查找可复用bitmap，是在没有再创建新的bitmap
4. 在处理的过程中产生的过渡Bitmap在用完之后一定要调用BitmapPoolUtils.freeBitmapToPool(Bitmap, BitmapPool)回收掉

自定义完后调用LoadOptions、DisplayOptions.setImageProcessor(ImageProcessor)方法或LoadHelper、DisplayHelper的processor(ImageProcessor)方法设置即可
