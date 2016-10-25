DiskCache用来在磁盘上缓存图片，默认实现是LruDiskCache，其核心是DiskLruCache

#### 相关方法
>* boolean exist(String)：判断缓存是否存在
>* DiskCache.Entry get(String)：获取缓存
>* DiskCache.Editor edit(String)：编辑缓存
>* File getCacheDir()：获取缓存目录
>* long getMaxSize()：获取最大容量
>* long getSize()：获取当前缓存大小
>* void clear()：清除缓存
>* void close()：关闭
>* ReentrantLock getEditorLock(String)：获取编辑同步锁

#### 配置最大容量
```java
Configuration configuration = Sketch.with(context).getConfiguration();
configuration.setDiskCache(new LruDiskCache(context, 1, 50 * 1024 * 1024));
```

#### 编辑缓存

如果你要通过edit(String)方法编辑磁盘缓存，那么你需要加同步锁，如下：
```java
DiskCache diskCache = Sketch.with(context).getConfiguration().getDiskCache();
String diskCacheKey = ...;

// 上锁
ReentrantLock lock = diskCache.getEditorLock(diskCacheKey);
lock.lock();

// 编辑
DiskCache.Entry diskCacheEntry = diskCache.edit(diskCacheKey);
...
diskCacheEntry.commit();

// 解锁
lock.unlock();
```
