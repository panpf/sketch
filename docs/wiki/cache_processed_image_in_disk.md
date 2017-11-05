# 使用 cacheProcessedImageInDisk 属性缓存需要复杂处理的图片，提升显示速度

一张图片要经过读取（或缩略图模式读取）和 ImageProcessor 处理才能显示在页面上，这两步通常也是整个显示过程中最耗时的，为了加快显示速度，减少用户等待时间我们可以将最终经过 inSampleSize 缩小的、缩略图模式读取的或 [ImageProcessor] 处理过的图片缓存在磁盘中，下次就可以读取后直接使用

### 如何开启：

```java
DisplayOptions displayOptions = new DisplayOptions();

displayOptions.setCacheProcessedImageInDisk(true);
```

### 使用条件

并不是你只要开启了就一定会将最终的图片缓存在磁盘缓存中，还需要满足以下任一条件：
* 有 maxSize 并且最终计算得出的 inSampleSize 大于等于 8
* 有 resize
* 有 ImageProcessor，并且确实生成了一张新的图片
* thumbnailMode 为 true 并且 resize 不为 null

### 存在的问题

由于 Android 天然存在的 BUG，导致读到内存里的图片，再保存到磁盘后图片会发生轻微的色彩变化（通常是发黄并丢失一些细节），因此在使用此功能时还是要慎重考虑此因素带来的影响，参考文章 [Android 中 decode JPG 时建议使用 inPreferQualityOverSpeed][reference_article]

`此功能读取图片时已强制设置 inPreferQualityOverSpeed 为 true`

[ImageProcessor]:../../sketch/src/main/java/me/panpf/sketch/process/ImageProcessor.java
[reference_article]: http://www.cnblogs.com/zhucai/p/inPreferQualityOverSpeed.html
