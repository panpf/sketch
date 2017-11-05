# 在磁盘上缓存图片原文件，避免重复下载

[DiskCache] 用来在磁盘上缓存图片，默认实现是 [LruDiskCache]，根据最少使用原则释放缓存

### 最大容量

默认最大容量是 100MB

由于最大容量一旦创建就不能修改，因此想要修改的话就只能重新创建 [LruDiskCache]

```java
// 最大容量为 50MB
int newDiskCacheMaxSize = (int) (Runtime.getRuntime().maxMemory() / 10);
Configuration configuration = Sketch.with(context).getConfiguration();
configuration.setDiskCache(new LruDiskCache(context, 1, newDiskCacheMaxSize));
```

### 编辑缓存

如果你要通过 edit(String) 方法编辑磁盘缓存，那么你需要加同步锁，如下：

```java
DiskCache diskCache = Sketch.with(context).getConfiguration().getDiskCache();
String diskCacheKey = ...;

// 上锁
ReentrantLock lock = diskCache.getEditorLock(diskCacheKey);
lock.lock();

try {
    // 编辑
    DiskCache.Entry diskCacheEntry = diskCache.edit(diskCacheKey);
    ...
    diskCacheEntry.commit();
} finally {
    // 解锁
    lock.unlock();
}
```

### 缓存目录

缓存目录默认是 sdcard/Android/data/APP_PACKAGE_NAME/cache/sketch

另外为了兼容多进程，当在非主进程使用 [Sketch] 时缓存目录名称后会加上进程名，例如 "sketch:push"

#### 开关 DiskCache

```java
DiskCache diskCache = Sketch.with(context).getConfiguration().getDiskCache();

// 禁用 DiskCache
diskCache.setDisabled(true);

// 恢复 DiskCache
diskCache.setDisabled(false);
```

### 其它方法
* boolean exist(String)：判断缓存是否存在
* DiskCache.Entry get(String)：获取缓存
* DiskCache.Editor edit(String)：编辑缓存
* File getCacheDir()：获取缓存目录
* long getMaxSize()：获取最大容量
* long getSize()：获取当前缓存大小
* void clear()：清除缓存
* ReentrantLock getEditorLock(String)：获取编辑同步锁

[Sketch]: ../../sketch/src/main/java/me/panpf/sketch/Sketch.java
[DiskCache]: ../../sketch/src/main/java/me/panpf/sketch/cache/DiskCache.java
[LruDiskCache]: ../../sketch/src/main/java/me/panpf/sketch/cache/LruDiskCache.java
[DiskLruCache]: ../../sketch/src/main/java/me/panpf/sketch/util/DiskLruCache.java
