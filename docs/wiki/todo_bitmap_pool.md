# 复用 Bitmap 降低 GC 频率，减少卡顿

从 Android 3.0 开始 Bitmap 包含的像素数据就从 native memory 挪到了 JVM 堆中，同时 BitmapFactory.Options 也增加了 inBitmap 属性用来复用 Bitmap

### 复用 Bitmap 有什么好处？

由于 Bitmap 通常占用内存比较大，因此频繁的创建新的 Bitmap 就会频繁的触发 GC，频繁的 GC 就会导致程序卡顿，最终影响用户体验

通过 inBitmap 可以利用已经存在内存中的 Bitmap 去存储新图片的像素数据，这样就不用创建新 Bitmap 了，没有创建新的 Bitmap 就不会轻易触发 GC，最终也就减少了卡顿

### inBitmap 使用条件

#### 在 BitmapFactory 中使用
\>= 3.0 && <= 4.3
* 只能是 jpeg 或 png图片
* Options.inSampleSize 只能是1`（默认是0，即使不需要缩小也必须改成1）`
* inBitmap 的宽高必须和新图片的宽高一样`（宽==宽&&高==高）`
* inBitmap 的 config 必须同新的 Options.inPreferredConfig 一样
* inBitmap.isMutable() == true

\>= 4.4
* inBitmap 的字节数大于等于新图片除以 inSampleSize 后所占的字节数即可
* inBitmap 的 config 同新的 Options.inPreferredConfig  最好一样，如果不一样将以 inBitmap 的 config 为准
* inBitmap.isMutable() == true

#### 在 BitmapRegionDecoder 中使用

\>= 4.1
* inBitmap 的字节数大于等于新图片除以 inSampleSize 后所占的字节数即可
* inBitmap 的 config 同新的 Options.inPreferredConfig 最好一样，如果不一样将以 inBitmap 的 config 为准
* inBitmap.isMutable() == true

`如果 inBitmap 不为空的时候解码失败或 inBitmap 不满足使用条件都将抛出 IllegalArgumentException 异常`

### BitmapPool

[BitmapPool] 用来存储已经不再使用的 Bitmap，Sketch 在解码之前会根据 width、height、Bitmap.Config 从 BitmapPool 中寻找可复用的 Bitmap 并设置给 Options.inBitmap

[BitmapPool] 的默认实现是 [LruBitmapPool]（`来自 Glide`）

#### 最大容量

默认最大容量是 3 个屏幕像素数：

```java
final DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
final int screenSize = displayMetrics.widthPixels * displayMetrics.heightPixels * 4;

final int bitmapPoolMaxSize = screenSize * 3;
```

由于最大容量一旦创建就不能修改，因此想要修改的话就只能重新创建 [BitmapPool]

```java
// 最大容量为 APP 最大可用内存的十分之一
int newBitmapPoolMaxSize = (int) (Runtime.getRuntime().maxMemory() / 10);
Sketch.with(context).getConfiguration().setBitmapPool(new LruBitmapPool(context, newBitmapPoolMaxSize));
```

#### 使用 BitmapPool

Sketch 默认开启了 BitmapPool，但如果你有编辑 Bitmap 的需求也可以通过 [BitmapPool] 寻找可复用的 Bitmap，如下：

```java
BitmapPool bitmapPool = Sketch.with(context).getConfiguration().getBitmapPool();

// 根据宽、高、Bitmap.Config 寻找可复用的 Bitmap
Bitmap bitmap = bitmapPool.get(100, 100, Bitmap.Config.ARGB_8888);
```

Bitmap 使用完毕后通过 [BitmapPoolUtils].freeBitmapToPool(Bitmap, BitmapPool) 方法回收 Bitmap

```java
BitmapPoolUtils.freeBitmapToPool(bitmap, bitmapPool);
```

[BitmapPoolUtils] 会先尝试将 Bitmap 放进 [BitmapPool] 中，如果 [BitmapPool] 已经满了或 Bitmap 不可复用的话就会执行 recycle() 回收掉 Bitmap

#### 开关 BitmapPool

```java
BitmapPool bitmapPool = Sketch.with(context).getConfiguration().getBitmapPool();

// 禁用 BitmapPool
bitmapPool.setDisabled(true);

// 恢复 BitmapPool
bitmapPool.setDisabled(false);
```

### 释放缓存

bitmap 的释放是全自动的，使用者无需关心，总结一下会在如下时机自动释放：

* 达到最大容量时自动释放较旧的 bitmap
* 设备可用内存较低触发了 Application 的 onLowMemory() 方法
* 系统整理内存触发了 Application 的 onTrimMemory(int) 方法

[BitmapPool]: ../../sketch/src/main/java/com/github/panpf/sketch/cache/BitmapPool.java
[LruBitmapPool]: ../../sketch/src/main/java/com/github/panpf/sketch/cache/LruBitmapPool.java
[BitmapPoolUtils]: ../../sketch/src/main/java/com/github/panpf/sketch/cache/BitmapPoolUtils.java
