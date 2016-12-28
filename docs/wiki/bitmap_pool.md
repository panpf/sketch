从Android 3.0 开始Bitmap包含的像素数据就从native memory挪到了JVM堆中，同时BitmapFactory.Options也增加了inBitmap属性用来复用Bitmap

### 复用Bitmap有什么好处？

由于Bitmap通常占用内存比较大，因此频繁的创建新的Bitmap就会频繁的触发GC，频繁的GC就会导致程序卡顿，最终影响用户体验

通过inBitmap可以利用已经存在内存中的Bitmap去存储新图片的像素数据，这样就不用创建新Bitmap了，没有创建新的Bitmap就不会轻易触发GC，最终也就减少了卡顿

### inBitmap使用条件

#### 在BitmapFactory中使用
\>= 3.0 && <= 4.3
>* 只能是jpg或png图片
>* Options.inSampleSize只能是1`（默认是0，即使不需要缩小也必须改成1）`
>* inBitmap的宽高必须和新图片的宽高一样`（宽==宽&&高==高）`
>* inBitmap的config必须同新的Options.inPreferredConfig一样
>* inBitmap.isMutable() == true

\>= 4.4
>* inBitmap的字节数大于等于新图片除以inSampleSize后所占的字节数即可
>* inBitmap的config同新的Options.inPreferredConfig最好一样，如果不一样将以inBitmap的config为准
>* inBitmap.isMutable() == true

#### 在BitmapRegionDecoder中使用

\>= 4.1
>* inBitmap的字节数大于等于新图片除以inSampleSize后所占的字节数即可
>* inBitmap的config同新的Options.inPreferredConfig最好一样，如果不一样将以inBitmap的config为准
>* inBitmap.isMutable() == true

`如果inBitmap不为空的时候解码失败或inBitmap不满足使用条件都将抛出IllegalArgumentException异常`

### BitmapPool

BitmapPool用来存储已经不再使用的Bitmap，Sketch在解码之前会根据width、height、Bitmap.Config从BitmapPool中寻找可复用的Bitmap并设置给Options.inBitmap

BitmapPool的默认实现是LruBitmapPool（`来自Glide`），默认最大容量是3个屏幕大小

### 使用BitmapPool

你可以通过Configuration拿到BitmapPool，如下：
```java
Context context = ...;
BitmapPool bitmapPool = Sketch.with(context).getConfiguration().getBitmapPool();
```

然后根据宽、高、Bitmap.Config寻找可复用的Bitmap
```java
Bitmap bitmap = bitmapPool.get(100, 100, Bitmap.Config.ARGB_8888);
```

Bitmap使用完毕后通过BitmapPoolUtils.freeBitmapToPool(Bitmap, BitmapPool)方法处理Bitmap
```java
BitmapPoolUtils.freeBitmapToPool(bitmap, bitmapPool);
```
BitmapPoolUtils会先尝试将Bitmap放进BitmapPool中，如果BitmapPool已经满了或Bitmap不可复用的话就会执行recycle()回收掉Bitmap
