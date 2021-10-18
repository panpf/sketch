# 使用 MaxSize 读取合适尺寸的缩略图，节省内存

### 背景

我们在 APP 中显示图片的时候都需要面对加载大尺寸图片的问题，假如手机内存为2GB，APP 最多可用内存为 200MB ，手机摄像头像素为800万，那么随便拍一张照片都是 3264x1840 的，完整加载到内存中就需要 3264x1840x4/1024/1024=22MB，照这么看加载 9 张 3264x1840 的图片内存就耗完了

如果 APP 中一个页面只有一个 ImageView 还好，但如果是九宫格要显示 9 个呢，一下子加载 9 张完整尺寸的图片 APP 一下就崩掉了，因此必须要根据 ImageView 的尺寸加载合适尺寸的缩略图

### 关于 inSampleSize

inSampleSize 是用来读取图片的完整缩略图的，正好可以用来解决读取大尺寸图片问题

BitmapFactory 提供了一个 Options 来配置读取图片的相关选项，其中一个参数就是 inSampleSize，其作用是设置缩放倍数

例如原图尺寸是 3264x1840，inSampleSize 是 2，那么最终读到内存的图片的尺寸就是 1632x920，宽高都缩小了 2 倍，其所占内存从 22MB 缩小到 1632x920x4/1024/1024=5.7MB，缩小了 4 倍

### MaxSize

在 Sketch 中 [MaxSize] 用来计算 inSampleSize，好加载适合 ImageView 尺寸的缩略图

#### 缺省值
* 在使用 load() 方法加载图片的时候 [MaxSize] 的缺省值是设备屏幕的宽高
* 在使用 SketchImageView 的时候如果 layout_width 和 layout_height 是固定的那么就会用 layout_width 和 layout_height 来作为 [MaxSize]，否则就使用设备屏幕的宽高来作为 [MaxSize]

为什么缺省值是设备屏幕的宽高？
* 因为 ImagweView 的尺寸通常不会超过设备屏幕的宽高

#### 自定义 MaxSize 计算规则

1. 继承 [ImageSizeCalculator] 类重写 calculateImageMaxSize() 方法实现你自己的计算规则
2. 调用 Sketch.with(context).getConfiguration().setSizeCalculator(ImageSizeCalculator) 方法使用你自定义的 [ImageSizeCalculator]

### inSampleSize 计算规则

默认的实现是 [ImageSizeCalculator].calculateInSampleSize(int, int, int, int) 方法，规则如下（`目标宽高是 MaxSize 的尺寸`）：

1. 先根据 targetSizeScale 缩放目标宽高，默认是1.1f，目的是为了让比目标宽高稍稍大一点的图片能直接显示
2. 如果目标宽高都小于等于0或都大于原图的宽高，就不计算了直接返回1
3. 然后根据缩略图的像素数不能超过目标宽高的像素数原则计算 inSampleSize
4. 接下来根据宽高均不能超过 OpenGL 所允许的最大尺寸（不同设备不同版本均不同），原则进一步计算 inSampleSize
5. 最后如果是为超大图功能加载预览图的话，当缩小 2 倍的时为了节省内存考虑还不如缩小 4 倍（缩小1倍时不会启用超大图功能）

#### 自定义 inSampleSize 计算规则

1. 继承 [ImageSizeCalculator] 类重写 calculateInSampleSize(int, int, int, int) 方法实现你自己的计算规则
2. 调用 Sketch.with(context).getConfiguration().setSizeCalculator(ImageSizeCalculator) 方法使用你自定义的 [ImageSizeCalculator]


[MaxSize]: ../../sketch/src/main/java/com/github/panpf/sketch/request/MaxSize.java
[ImageSizeCalculator]: ../../sketch/src/main/java/com/github/panpf/sketch/decode/ImageSizeCalculator.java
