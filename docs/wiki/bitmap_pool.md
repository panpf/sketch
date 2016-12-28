从Android 3.0 开始Bitmap包含的像素数据就从native memory挪到了JVM堆中，同时BitmapFactory.Options也就增加了一个属性inBitmap用来重复利用bitmap

### 重复利用bitmap有什么好处？

由于bitmap通常占用内存比较大，因此频繁的创建新的bitmap就会频繁的触发GC，频繁的GC就会导致程序卡顿，最终影响用户体验

通过inBitmap可以利用已经存在内存中的bitmap去存储新图片的像素数据，这样就不用创建新bitmap了，没有创建新的bitmap就不会轻易触发GC，最终也就减少了卡顿

### inBitmap使用条件

#### 在BitmapFactory中使用
\>= 3.0 && <= 4.3
>* 只能是jpg或png图片
>* Options.inSampleSize只能是1`（默认是0，即使不需要缩小也必须改成1）`
>* inBitmap的宽高必须和新图片的宽高一样`（宽==宽&&高==高）`
>* inBitmap的config必须同新的Options.inPreferredConfig一样
>* inBitmap.isMutable() == true

\>= 4.4
>* inBitmap的字节数大于等于新图片除以inSampleSize后所占的字节数
>* inBitmap的config同新的Options.inPreferredConfig最好一样，如果不一样将以inBitmap的config为准
>* inBitmap.isMutable() == true

#### 在BitmapRegionDecoder中使用

\>= 4.1
>* inBitmap的字节数大于等于新图片除以inSampleSize后所占的字节数
>* inBitmap的config同新的Options.inPreferredConfig最好一样，如果不一样将以inBitmap的config为准
>* inBitmap.isMutable() == true

`如果inBitmap不为空的时候解码失败或inBitmap不满足使用条件都将抛出IllegalArgumentException异常`

### Bitmap Pool
你可以通过Sketch.with(context).getConfiguration().getBitmapPool()得到BitmapPool
