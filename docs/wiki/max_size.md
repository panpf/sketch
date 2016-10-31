maxSize用来计算inSampleSize，防止加载过大的图片到内存中

#### 背景
我们在移动端显示图片的时候都需要面对加载大尺寸图片的问题
目前主流的Android旗舰机内存一般在2G左右，而APP最多可用内存为200M左右
现在手机都是800万像素的，随便拍一张照片都是3264x1840的，那么加载到内存中就需要3264x1840x4/1024/1024=22M，照这么看加载9张3264x1840的图片内存就耗完了，显然我们不能这样搞

#### 何时使用
ImageDecoder会在读取图片的时候根据maxSize计算出合适的inSampleSize，然后再读取图片

#### 关于inSampleSize
BitmapFactory提供了一个Options来配置读取图片的相关选项，其中一个参数就是inSampleSize，其作用是设置缩放倍数。

例如：
    原图尺寸3264x1840
    inSampleSize=2
那么最终读到内存的图片的尺寸就是1632x920，宽高都缩小了2倍，其所占内存从23M缩小到1632x920x4/1024/1024=5.7M，缩小了4倍。

#### 缺省值
>* 在使用load()方法加载图片的时候maxSize的缺省值是当前设备屏幕的宽高
>* 在使用SketchImageView的时候如果layout_width和layout_height是固定的那么就会用layout_width和layout_height来作为maxSize，否则就使用当前屏幕的宽高来作为maxSize

因此在大多数情况下你不需要主动设置maxSize，Sketch会自动帮你搞定

#### 自定义maxSize计算规则
如果你对现有的maxSize计算规则不满意，那么你可以继承ImageSizeCalculator类重写calculateImageMaxSize()方法实现你自己的计算规则
然后调用Sketch.with(context).getConfiguration().setImageSizeCalculator(ImageSizeCalculator)方法来使用你自定义的ImageSizeCalculator
