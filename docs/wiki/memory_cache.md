MemoryCache用来在内存中缓存图片，默认的实现是LruMemoryCache，自动根据最少使用原则释放旧的图片

#### 相关方法：
>* getMaxSize()：获取最大容量
>* getSize()：获取已用容量
>* clear()：清空内存缓存
>* remove(String)：删除缓存中指定key的图片
>* get(String)：获取缓存中指定的keu的图片
>* put(String, Drawable)：将图片放到缓存中，注意Drawable必须实现RecycleDrawableInterface接口

#### 配置最大容量
```java
// 最大容量为APP最大可用内存的十分之一
int newMemoryCacheMaxSize = (int) (Runtime.getRuntime().maxMemory() / 10);
Sketch.with(context).getConfiguration().setMemoryCache(new LruMemoryCache(newMemoryCacheMaxSize));
```
