# 其它知识点

### inSampleSize 计算规则

inSampleSize用来减小读到内存中的图片的尺寸

默认的实现是 [ImageSizeCalculator].calculateInSampleSize(int, int, int, int) 方法，规则如下：
1. 先根据targetSizeScale缩放目标宽高，默认是1.1f，目的是为了让比目标宽高稍稍大一点的图片能直接显示
2. 如果目标尺寸的宽高都小于等于0或都大于原图的宽高，就不计算了直接返回1
3. 然后限制像素数不能超过目标宽高的像素数
4. 接下来限制宽高均不能超过OpenGL所允许的最大尺寸
5. 最后如果是为大图功能加载预览图的话，当缩小2倍的时为了节省内存考虑还不如缩小4倍（缩小1倍时不会启用大图功能，因此无需处理）

##### 自定义 inSampleSize 计算规则
1.继承 ImageSizeCalculator，并重写 calculateInSampleSize(int, int, int, int) 方法，
2.调用 Sketch.with(context).getConfiguration().setSizeCalculator(ImageSizeCalculator) 方法应用即可

[ImageSizeCalculator]: ,,/../sketch/src/main/java/me/xiaopan/sketch/decode/ImageSizeCalculator.java
