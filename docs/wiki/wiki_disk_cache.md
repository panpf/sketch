DiskCache用来在本地磁盘上缓存图片，默认实现是LruDiskCache，其核心是DiskLruCache

#### 相关方法
>* boolean exist(String)：判断缓存是否存在
>* DiskCache.Entry get(String)：获取缓存
>* DiskCache.Editor edit(String)：
>* File getCacheDir()：获取缓存目录
>* long getMaxSize()：获取最大容量
>* long getSize()：获取当前缓存大小
>* void clear()：清除缓存
>* void close()：关闭

#### 配置磁盘缓存最大容量
```java
Configuration configuration = Sketch.with(context).getConfiguration();
configuration.setDiskCache(new LruDiskCache(context, 1, 50 * 1024 * 1024));
```