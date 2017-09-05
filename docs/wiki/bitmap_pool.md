# 复用 Bitmap 降低 GC 频率，减少卡顿

从 Android 3.0 开始 Bitmap 包含的像素数据就从 native memory 挪到了 JVM 堆中，同时 BitmapFactory.Options 也增加了 inBitmap 属性用来复用 Bitmap

### 复用 Bitmap 有什么好处？

由于 Bitmap 通常占用内存比较大，因此频繁的创建新的 Bitmap 就会频繁的触发 GC，频繁的 GC 就会导致程序卡顿，最终影响用户体验

通过 inBitmap 可以利用已经存在内存中的 Bitmap 去存储新图片的像素数据，这样就不用创建新 Bitmap 了，没有创建新的 Bitmap 就不会轻易触发 GC，最终也就减少了卡顿

### inBitmap 使用条件

#### 在 BitmapFactory 中使用
\>= 3.0 && <= 4.3
>* 只能是 jpeg 或 png图片
>* Options.inSampleSize 只能是1`（默认是0，即使不需要缩小也必须改成1）`
>* inBitmap 的宽高必须和新图片的宽高一样`（宽==宽&&高==高）`
>* inBitmap 的 config 必须同新的 Options.inPreferredConfig 一样
>* inBitmap.isMutable() == true

\>= 4.4
>* inBitmap 的字节数大于等于新图片除以 inSampleSize 后所占的字节数即可
>* inBitmap 的 config 同新的 Options.inPreferredConfig  最好一样，如果不一样将以 inBitmap 的 config 为准
>* inBitmap.isMutable() == true

#### 在 BitmapRegionDecoder 中使用

\>= 4.1
>* inBitmap 的字节数大于等于新图片除以 inSampleSize 后所占的字节数即可
>* inBitmap 的 config 同新的 Options.inPreferredConfig 最好一样，如果不一样将以 inBitmap 的 config 为准
>* inBitmap.isMutable() == true

`如果 inBitmap 不为空的时候解码失败或 inBitmap 不满足使用条件都将抛出 IllegalArgumentException 异常`

### BitmapPool

BitmapPool 用来存储已经不再使用的 Bitmap，Sketch 在解码之前会根据 width、height、Bitmap.Config 从 BitmapPool 中寻找可复用的 Bitmap 并设置给 Options.inBitmap

BitmapPool 的默认实现是 [LruBitmapPool]（`来自 Glide`），默认最大容量是 3 个屏幕大小

#### 使用 BitmapPool

你可以通过 Configuration 拿到 BitmapPool，如下：

```java
Context context = ...;
BitmapPool bitmapPool = Sketch.with(context).getConfiguration().getBitmapPool();
```

然后根据宽、高、Bitmap.Config 寻找可复用的 Bitmap

```java
Bitmap bitmap = bitmapPool.get(100, 100, Bitmap.Config.ARGB_8888);
```

Bitmap 使用完毕后通过 BitmapPoolUtils.freeBitmapToPool(Bitmap, BitmapPool) 方法回收 Bitmap

```java
BitmapPoolUtils.freeBitmapToPool(bitmap, bitmapPool);
```

BitmapPoolUtils 会先尝试将 Bitmap 放进 BitmapPool 中，如果 BitmapPool 已经满了或 Bitmap 不可复用的话就会执行 recycle() 回收掉 Bitmap

[LruBitmapPool]: ../../sketch/src/main/java/me/xiaopan/sketch/cache/LruBitmapPool.java
