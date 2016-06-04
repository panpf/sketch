### maxSize
maxSize用来防止加载过大的图片到内存中，已达到节省内存的目的

####背景
我们在移动端显示图片的时候都需要面对加载大尺寸图片的问题
目前主流的Android旗舰机内存一般在2G左右，而APP最多可用内存为200M左右
现在手机都是800万像素的，随便拍一张照片都是3264x1840的，那么加载到内存中就需要3264x1840x4/1024/1024=22M，照这么看加载9张3264x1840的图片内存就耗完了，显然我们不能这样搞

####何时使用
ImageDecoder会在读取图片的时候根据maxSize计算出合适的inSampleSize，然后再读取图片

####关于inSampleSize
BitmapFactory提供了一个Options来配置读取图片的相关选项，其中一个参数就是inSampleSize，其作用是设置缩放倍数。

例如：
    原图尺寸3264x1840
    inSampleSize=2
那么最终读到内存的图片的尺寸就是1632x920，宽高都缩小了2倍，其所占内存从23M缩小到1632x920x4/1024/1024=5.7M，缩小了4倍。

####缺省值
>* 在使用load()方法加载图片的时候maxSize的缺省值是当前设备屏幕的0.75倍
>* 在使用SketchImageView的时候如果layout_width和layout_height是固定的那么就会用layout_width和layout_height来作为maxSize，否则就使用当前屏幕宽高的0.75倍来作为maxSize

因此在大多数情况下你不需要主动设置maxSize，Sketch会自动帮你搞定

####自定义maxSize计算规则
如果你对现有的maxSize计算规则不满意，那么你可以继承ImageSizeCalculator类重写calculateImageMaxSize()方法实现你自己的计算规则
然后调用Sketch.with(context).getConfiguration().setImageSizeCalculator(ImageSizeCalculator)方法来使用你自定义的ImageSizeCalculator


### resize
resize用来修剪图片以及调整宽高比例，使用maxSize可以加载合适尺寸的图片到内存中，那么有的时候我们可能还需要固定尺寸的图片或固定宽高比例的图片，resize就是 用来解决这个问题的。

规则如下：
>* 如果原图的尺寸大于resize则按resize裁剪，如果小于resize则会按照resize的比例修剪原图，让原图的宽高比同resize一样。
>* 如果forceUseResize为true，则即使原图尺寸小于resize，则也会按照resize的尺寸创建一张新的图片。

####使用
```java
DisplayOptions options = ...;
options.setResize(300, 300);
options.seForceUseResize(true);
```

```java
Sketch.with(context).load(R.drawable.ic_launcher, new LoadListener(){
...
})
.resize(300, 300)
.commit();
```

使用DisplayOptions的时候还可以使用resizeByFixedSize(true)方法自动使用SketchImageView的layout_width和layout_height作为resize


### inSampleSize
inSampleSize是用来减小读到内存中的图片的尺寸，非常重要，默认的实现是ImageSizeCalculator.calculateInSampleSize(int, int, int, int)方法
```java
@Override
public int calculateInSampleSize(int outWidth, int outHeight, int targetWidth, int targetHeight) {
    // 如果目标尺寸都小于等于0，那就别计算了没意义
    if(targetWidth <= 0 && targetHeight <= 0){
        return 1;
    }

    // 如果目标尺寸都大于等于原始尺寸，也别计算了没意义
    if(targetWidth >= outWidth && targetHeight >= outHeight){
        return 1;
    }

    // 首先根据缩放后只要有任何一边小于等于目标即可的规则计算一遍inSampleSize
    int inSampleSize = 1;
    do{
        inSampleSize *= 2;
    }while ((outWidth/inSampleSize) > targetWidth && (outHeight/inSampleSize) > targetHeight);

    // 然后根据比较像素总数的原则过滤掉那些比较极端的一边特别小，一边特别大的图片
    // 比如目标尺寸是400x400，图片的尺寸是6000*600，缩放后是3000*300
    // 这样看来的确是满足了第一个条件了，但是图片的尺寸依然很大
    // 因此这一步我们根据像素总数来过滤，规则是总像素数不得大于目标尺寸像素数的两倍
    long totalPixels = (outWidth/inSampleSize) * (outHeight/inSampleSize);
    final long totalReqPixelsCap = targetWidth * targetHeight * 2;
    while (totalPixels > totalReqPixelsCap) {
        inSampleSize *= 2;
        totalPixels /= 2;
    }

    return inSampleSize;
}
```

其实现规则是：
>* 如果目标尺寸的宽高都小于0，就不计算了直接返回1
>* 如果目标尺寸的宽高都大于原图的宽高，就不计算了直接返回1
>* 然后开始每次乘以2计算inSampleSize，直到应用inSampleSize后原图的宽或高小于目标尺寸的宽或高即可
>* 接下来开始按照总像素数进行就算inSampleSize，直到应用inSampleSize后原图的总像素数小于目标尺寸总像素数的2倍即可

如果你想自定义inSampleSize的话，你只需继承ImageSizeCalculator，并重写calculateInSampleSize(int, int, int, int)方法，
然后调用Sketch.with(context).getConfiguration().setImageSizeCalculator(ImageSizeCalculator)方法应用即可