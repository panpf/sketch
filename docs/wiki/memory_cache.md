# 在内存中缓存 Bitmap 提升显示速度

[MemoryCache] 用来在内存中缓存图片，默认的实现是 [LruMemoryCache]，根据最少使用原则释放旧的图片

#### 最大容量

默认最大容量是 3 个屏幕像素数：

```java
final DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
final int screenSize = displayMetrics.widthPixels * displayMetrics.heightPixels * 4;

final int memoryCacheMaxSize = screenSize * 3;
```

由于最大容量一旦创建就不能修改，因此想要修改的话就只能重新创建 [MemoryCache]

```java
// 最大容量为 APP 最大可用内存的十分之一
int newMemoryCacheMaxSize = (int) (Runtime.getRuntime().maxMemory() / 10);
Sketch.with(context).getConfiguration().setMemoryCache(new LruMemoryCache(context, newMemoryCacheMaxSize));
```

#### 开关 MemoryCache

```java
MemoryCache memoryCache = Sketch.with(context).getConfiguration().getMemoryCache();

// 禁用 MemoryCache
memoryCache.setDisabled(true);

// 恢复 MemoryCache
memoryCache.setDisabled(false);
```

### 释放缓存

内存缓存的释放是全自动的，使用者无需关心，总结一下会在如下时机自动释放：

* 达到最大容量时自动释放较旧的缓存
* 在 SketchImageView 中 Drawable 被替换
* SketchImageView 执行 onDetachedFromWindow
* 请求取消
* 设备可用内存较低触发了 Application 的 onLowMemory() 方法
* 系统整理内存触发了 Application 的 onTrimMemory(int) 方法

### 缓存日志

内存缓存模块在运行期间会有一些日志输出，但默认是关闭的，开启或关闭的方法如下：

```java
// 开启 CACHE 类型日志
SLog.setLoggable(SLog.DEBUG);
```

### 其它方法

* getMaxSize()：获取最大容量
* getSize()：获取已用容量
* clear()：清空内存缓存
* remove(String)：删除缓存中指定 key 的图片
* get(String)：获取缓存中指定的 key 的图片
* put(String, SketchRefBitmap)：将图片放到缓存中


[MemoryCache]: ../../sketch/src/main/java/com/github/panpf/sketch/cache/MemoryCache.java
[LruMemoryCache]: ../../sketch/src/main/java/com/github/panpf/sketch/cache/LruMemoryCache.java
