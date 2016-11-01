一张图片要经过读取（或缩略图模式读取）和ImageProcessor处理才能显示在页面上，这两步通常也是整个显示过程中最耗时的，为了加快显示速度，减少用户等待时间我们可以将最终经过inSampleSize缩小的、缩略图模式读取的或ImageProcessor处理过的图片缓存在磁盘中，下次就可以直接读取

#### 如何开启：

```java
DisplayOptions displayOptions = new DisplayOptions();

displayOptions.setCacheProcessedImageInDisk(true);
```

#### 使用条件

并不是你只要开启了就一定会将最终的图片缓存在磁盘缓存中，还需要满足以下任一条件：
>* 有maxSize并且最终计算得出的inSampleSize大于等于8
>* 有resize
>* 有ImageProcessor，并且确实生成了一张新的图片
>* thumbnailMode为true并且resize不为null

#### 存在的问题

由于Android天然存在的BUG，导致读到内存里的图片，再保存到磁盘后图片会发生轻微的色彩变化（通常是发黄），因此在使用此功能时还是要慎重考虑此因素带来的影响

参考文章：http://www.cnblogs.com/zhucai/p/inPreferQualityOverSpeed.html

`此功能读取图片时已强制设置inPreferQualityOverSpeed为true`
