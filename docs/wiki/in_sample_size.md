#### 简介

inSampleSize是用来减小读到内存中的图片的尺寸，非常重要，默认的实现是ImageSizeCalculator.calculateInSampleSize(int, int, int, int)方法
```java
/**
 * 计算InSampleSize
 *
 * @param outWidth     原始宽
 * @param outHeight    原始高
 * @param targetWidth  目标宽
 * @param targetHeight 目标高
 * @return 合适的InSampleSize
 */
public int calculateInSampleSize(int outWidth, int outHeight, int targetWidth, int targetHeight) {
    // 如果目标尺寸都大于等于原始尺寸，也别计算了没意义
    if (targetWidth >= outWidth && targetHeight >= outHeight) {
        return 1;
    }

    // 如果目标尺寸都小于等于0，那就别计算了没意义
    if (targetWidth <= 0 && targetHeight <= 0) {
        return 1;
    }

    int inSampleSize = 1;
    if (targetWidth <= 0 && targetHeight != 0) {
        // 目标宽小于等于0时，只要高度满足要求即可
        while (outHeight / inSampleSize > targetHeight) {
            inSampleSize *= 2;
        }
    } else if (targetHeight <= 0) {
        // 目标高小于等于0时，只要宽度满足要求即可
        while (outWidth / inSampleSize > targetWidth) {
            inSampleSize *= 2;
        }
    } else {
        // 目标宽高都大于0时，首先有任意一边在缩放后小于目标尺寸即可
        while (outWidth / inSampleSize > targetWidth && outHeight / inSampleSize > targetHeight){
            inSampleSize *= 2;
        }

        // 然后根据比较像素总数的原则过滤掉那些比较极端的一边特别小，一边特别大的图片
        // 比如目标尺寸是400x400，图片的尺寸是6000*600，缩放后是3000*300
        // 这样看来的确是满足了第一个条件了，但是图片的尺寸依然很大
        // 因此这一步我们根据像素总数来过滤，规则是总像素数不得大于目标尺寸像素数的两倍
        final long totalReqPixelsCap = targetWidth * targetHeight * 2;
        while ((outWidth / inSampleSize) * (outHeight / inSampleSize) > totalReqPixelsCap) {
            inSampleSize *= 2;
        }

        // 最后宽高不能大于OpenGL所允许的最大尺寸
        if (openGLMaxTextureSize == -1) {
            openGLMaxTextureSize = OpenGLUtils.getMaxTextureSize();
        }
        while (outWidth / inSampleSize > openGLMaxTextureSize || outHeight / inSampleSize > openGLMaxTextureSize) {
            inSampleSize *= 2;
        }
    }

    return inSampleSize;
}
```

其实现规则是：
>* 如果目标尺寸的宽高都小于0，就不计算了直接返回1
>* 如果目标尺寸的宽高都大于原图的宽高，就不计算了直接返回1
>* 首先开始每次乘以2计算inSampleSize，直到应用inSampleSize后原图的宽或高小于目标尺寸的宽或高即可
>* 接下来开始按照总像素数进行就算inSampleSize，直到应用inSampleSize后像素数小于目标尺寸总像素数的2倍即可
>* 最后限制宽高均不能超过OpenGL所允许的最大尺寸

#### 自定义inSampleSize计算规则
如果你想自定义inSampleSize的话，你只需继承ImageSizeCalculator，并重写calculateInSampleSize(int, int, int, int)方法，
然后调用Sketch.with(context).getConfiguration().setImageSizeCalculator(ImageSizeCalculator)方法应用即可