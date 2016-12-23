inSampleSize用来减小读到内存中的图片的尺寸

默认的实现是ImageSizeCalculator.calculateInSampleSize(int, int, int, int)方法
```java
/**
 * 计算InSampleSize
 *
 * @param outWidth     原始宽
 * @param outHeight    原始高
 * @param targetWidth  目标宽
 * @param targetHeight 目标高
 * @param supportLargeImage 是否支持大图，大图时会有特殊处理
 * @return 合适的InSampleSize
 */
public int calculateInSampleSize(int outWidth, int outHeight, int targetWidth, int targetHeight, boolean supportLargeImage) {
    targetWidth *= targetSizeScale;
    targetHeight *= targetSizeScale;

    int inSampleSize = 1;

    // 如果目标宽高都小于等于0，就别计算了
    if (targetWidth <= 0 && targetHeight <= 0) {
        return inSampleSize;
    }

    // 如果目标宽高都大于等于原始尺寸，也别计算了
    if (targetWidth >= outWidth && targetHeight >= outHeight) {
        return inSampleSize;
    }

    if (targetWidth <= 0) {
        // 目标宽小于等于0时，只要高度满足要求即可
        while (SketchUtils.ceil(outHeight, inSampleSize) > targetHeight) {
            inSampleSize *= 2;
        }
    } else if (targetHeight <= 0) {
        // 目标高小于等于0时，只要宽度满足要求即可
        while (SketchUtils.ceil(outWidth, inSampleSize) > targetWidth) {
            inSampleSize *= 2;
        }
    } else {
        // 首先限制像素数不能超过目标宽高的像素数
        final long maxPixels = targetWidth * targetHeight;
        while ((SketchUtils.ceil(outWidth, inSampleSize)) * (SketchUtils.ceil(outHeight, inSampleSize)) > maxPixels) {
            inSampleSize *= 2;
        }

        // 然后限制宽高不能大于OpenGL所允许的最大尺寸
        int maxSize = getOpenGLMaxTextureSize();
        while (SketchUtils.ceil(outWidth, inSampleSize) > maxSize || SketchUtils.ceil(outHeight, inSampleSize) > maxSize) {
            inSampleSize *= 2;
        }

        // 最后如果是为大图功能加载预览图的话，当缩小2倍的话为了节省内存考虑还不如缩小4倍（缩小1倍时不会启用大图功能，因此无需处理）
        if (supportLargeImage && inSampleSize == 2) {
            inSampleSize = 4;
        }
    }

    return inSampleSize;
}
```

其实现规则是：
>* 先根据targetSizeScale缩放目标宽高，默认是1.1f，目的是为了让比目标宽高稍稍大一点的图片能直接显示
>* 如果目标尺寸的宽高都小于等于0或都大于原图的宽高，就不计算了直接返回1
>* 然后限制像素数不能超过目标宽高的像素数
>* 接下来限制宽高均不能超过OpenGL所允许的最大尺寸
>* 最后如果是为大图功能加载预览图的话，当缩小2倍的时为了节省内存考虑还不如缩小4倍（缩小1倍时不会启用大图功能，因此无需处理）

#### 自定义inSampleSize计算规则
如果你想自定义inSampleSize的话，你只需继承ImageSizeCalculator，并重写calculateInSampleSize(int, int, int, int)方法，
然后调用Sketch.with(context).getConfiguration().setImageSizeCalculator(ImageSizeCalculator)方法应用即可
