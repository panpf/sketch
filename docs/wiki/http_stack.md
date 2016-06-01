ImageDownloader是用来下载图片的，默认的实现是HttpUrlConnectionImageDownloader

####相关配置：
设置连接超时时间为30秒，默认为20秒
```java
Sketch.with(context).getConfiguration().getImageDownloader().setConnectTimeout(30 * 1000);
```

设置最大重试次数，默认为1，意思是只重试一次
```java
Sketch.with(context).getConfiguration().getImageDownloader().setMaxRetryCount(2);
```

设置进度回调次数，默认为10，意思是整个下载过程中进度回调10次，例如第一次是10%，第二次是20%，以此类推
```java
Sketch.with(context).getConfiguration().getImageDownloader().setProgressCallbackNumber(20);
```

####自定义
自定义ImageDownloader有以下几点需要注意：

1. 实现根据URI加锁机制，防止重复下载，并且在download()方法中要先上锁，在结束的时候解锁，例如：
    ```java
    @Override
    public Object download(DownloadRequest request) {
        // 根据下载地址加锁，防止重复下载
        ReentrantLock urlLock = getUrlLock(request.getUri());
        urlLock.lock();

        // 下载处理
        ...

        // 释放锁
        urlLock.unlock();
        return result;
    }
    ```
2. 在取到锁之后立马过滤已取消的请求，并且检查缓存文件是否已存在，如果已存在就返回缓存文件，例如：
    ```java
    // 如果已经取消了就直接结束
    if (request.isCanceled()) {
        return null;
    }

    // 如果缓存文件已经存在了就直接返回缓存文件
    File cacheFile = request.getCacheFile();
    if (cacheFile != null && cacheFile.exists()) {
        return cacheFile;
    }
    ```
3. 在每个关键步骤及时检查请求是否已取消
4. 根据isCacheInDisk()方法决定将数据保存到缓存文件还是内存中，并在最终返回结果的时候返回缓存文件或是字节数组
5. 如果要保存到缓存文件，就要调用request.getSketch().getConfiguration().getDiskCache().generateCacheFile(String)方法来获取缓存文件
6. 获取到缓存文件之后要调用request.getSketch().getConfiguration().getDiskCache().applyForSpace(long)申请空间，当返回true的时候说明申请成功，才能继续。这一步是为了当已缓存文件超出最大容量限制或本地空间不够用的时候删除旧的缓存文件来腾出空间
7. 空间申请完毕之后就要创建缓存文件了，创建的时候要在原文件名称的后面加".temp"创建一个临时缓存文件，数据写完后再重命名去掉".temp"。这样做是为了防止在下载中因文件存在而被误认为本地缓存可以用而读取了一个不完整的图片
8. 实现重试机制，并根据maxRetryCount决定重试次数
9. 在读取数据的时候要回调progressCallback，同时还要严格遵守progressCallbackNumber所限制的回调次数

详情请参考[HurlStack.java](https://github.com/xiaopansky/Sketch/blob/master/sketch/src/main/java/me/xiaopan/sketch/http/HurlStack.java)

自定义完成之后调用Sketch.with(context).getConfiguration().setHttpStack(HttpStack)设置即可