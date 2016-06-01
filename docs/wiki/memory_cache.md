MemoryCache用来在内存中缓存图片，默认的实现是LruMemoryCache，自动根据最少使用原则释放旧的图片

####相关配置
设置内存缓存最大容量
```java
// 最大容量为APP最大可用内存的十分之一
int newMemoryCacheMaxSize = (int) (Runtime.getRuntime().maxMemory()/10);
Sketch.with(context).getConfiguration().setMemoryCache(new LruMemoryCache(newMemoryCacheMaxSize));
```

####相关方法：
>* getMaxSize()：获取最大容量
>* getSize()：获取已用容量
>* clear()：清空内存缓存
>* remove(String)：删除缓存中指定key的图片
>* get(String)：获取缓存中指定的keu的图片
>* put(String, Drawable)：将图片放到缓存中，注意Drawable必须实现RecycleDrawableInterface接口

####自定义
有以下几点需要注意
>* put(String, Drawable)方法必须检查Drawable是否实现了RecycleDrawableInterface接口，没有的话就要抛出异常
>* 保证Drawable在放进内存缓存的时候要调用其setIsCached("put", true)方法，使引用计数加1，然后再移出内存缓存的时候调用其setIsCached("entryRemoved", false)方法，，使引用计数减1

最后调用Sketch.with(context).getConfiguration().setMemoryCache(MemoryCache)设置即可