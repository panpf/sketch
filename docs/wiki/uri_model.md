# UriModel 详解及扩展 URI

### UriModel

Sketch 通过 [UriModel] 来支持不同的 uri，[UriModel] 有两个核心方法：

* UriModel match(Context, String)：匹配 uri，返回 true 将使用此 [UriModel] 来处理 uri
* DataSource getDataSource(Context, String, DownloadResult)：获取指定 uri 解码需要的数据源

[UriModel] 现有如下子类，对应已支持的 uri 类型：

* [AssetUriModel]
* [HttpUriModel]
  * [HttpsUriModel]
* [DrawableUriModel]
* [FileUriModel]
  * [FileVariantUriModel]
* [AndroidResUriModel]
* [Base64UriModel]
  * [Base64VariantUriModel]
* [ApkIconUriModel]
* [AppIconUriModel]
* [ContentUriModel]

sample app 中还扩展了两个:

* [XpkIconUriModel]
* [VideoThumbnailUriModel]

#### makeUri()

大部分 [UriModel] 的子类都提供了 makeUri() 方法用于创建适用于特定 [UriModel] 的 uri，如下创建 apk icon uri：

```java
String apkIconUri = ApkIconUriModel.makeUri("/sdcard/sample.apk");
```

#### 获取 uri 的数据

除了将 uri 交由 Sketch 去显示图片之外，你也许会有其它的需求需要获取指定 uri 的数据，来做一些特殊的任务，比如保存图片到 SD 卡

首先你需要获取 uri 对应的 [UriModel]：

```java
String imageUri = ApkIconUriModel.makeUri("/sdcard/sample.apk");

UriModel uriModel = UriModel.match(context, imageUri);
if (uriModel == null) {
    throw new IllegalArgumentException("Unknown scheme uri. " + imageUri);
}
```

然后通过 [UriModel] 拿到 [DataSource]：

```java
DataSource dataSource;
try {
    dataSource = uriModel.getDataSource(context, imageUri, null);
} catch (GetDataSourceException e) {
    throw new IllegalArgumentException("Can not be generated DataSource.  " + imageUri, e);
}
```

最后通过 [DataSource] 获取 InputStream 读取并写出数据：

```
InputStream inputStream = dataSource.getInputStream();
try {
    // read and write data
} finally {
    inputStream.close();
}
```

对于需要下载的 uri 来说，如果没有磁盘缓存就无法获取 [DataSource]，你可以先通过 download() 方法下载，然后再获取 [DataSource]，如下：

```java
UriModel uriModel = ...;

if (uriModel.isFromNet()){
    Sketch.with(context).download(imageUri, new DownloadListener() {
        @Override
        public void onCompleted(@NonNull DownloadResult result) {
            DataSource dataSource;
            try {
                dataSource = uriModel.getDataSource(context, imageUri, result);
            } catch (GetDataSourceException e) {
                throw new IllegalArgumentException("Can not be generated DataSource.  " + imageUri, e);
            }
        }
    }).commit();
}
```

### 扩展 URI

1.继承 [UriModel] 类定义你的 [UriModel] 并实现其核心方法

其它方法根据需求重写：

* String getUriContent(String uri) ：获取 uri 的内容部分，默认完整返回 uri，但有的 uri 协议头部分在读取数据时是无用的，例如 [ApkIconUriModel]、[DrawableUriModel]
* String getDiskCacheKey(String uri) ：获取 uri 的 磁盘缓存 key，默认完整返回 uri，但有的 uri 就只需要 uri
的一部分作为 磁盘缓存 key，例如 [FileVariantUriModel]
* boolean isFromNet() ：此类型 uri 的数据是否来自网络，目前只有 [HttpUriModel] 和 [HttpsUriModel] 需要
* boolean isConvertShortUriForKey() ：此类型的 uri 在生成请求 key 的时候是否需要转成短 uri，因为请求 key 不能太长，如果 uri 太长会导致请求 key 太长，例如 [Base64UriModel] 就需要转成短 uri

注意：

* 你的 [UriModel] 应该匹配只有你的 [UriModel] 才使用的 uri 协议，这个由于定义，但要遵循基本的 uri 结构，详情请参考 [统一资源标志符 - 维基百科]
* getDataSource(Context, String, DownloadResult) 方法不能返回 null，如果遇到错误应该抛出 GetDataSourceException 异常
* getDataSource(Context, String, DownloadResult) 方法的第三个参数 [DownloadResult] 只有 [UriModel].isFromNet() 方法返回 true 的 [UriModel] 才会用到，例如 [HttpUriModel]

你的 [UriModel] 需要提供一个名叫 makeUri() 的静态方法，用于创建适用于你的 [UriModel] 的 uri，可参考 [AssetUriModel]

2.在初始化 Sketch 时注册即可，如下：

```java
Configuration configuration = ...;

configuration.getUriModelManager().add(new MyUriModel());
```

#### AbsDiskCacheUriModel

如果你自定义的 [UriModel] 需要用到 [DiskCache] ，那么你可以继承 [AbsDiskCacheUriModel] 来自定义，能帮你省去并规范磁盘缓存部分的处理

[AbsDiskCacheUriModel] 又分为以下两种：

* [AbsStreamDiskCacheUriModel] ：适用于通过 InputStream 读取图片的，你只需返回图片的 InputStream 即可，例如 [Base64UriModel]
* [AbsBitmapDiskCacheUriModel] ：适用于先拿到图片的 bitmap 再把 bitmap 保存到 [DiskCache] 的，你只需返回图片的 bitmap 即可，例如 [ApkIconUriModel]

[UriModel]: ../../sketch/src/main/java/me/xiaopan/sketch/uri/UriModel.java
[AssetUriModel]: ../../sketch/src/main/java/me/xiaopan/sketch/uri/AssetUriModel.java
[HttpUriModel]: ../../sketch/src/main/java/me/xiaopan/sketch/uri/HttpUriModel.java
[HttpsUriModel]: ../../sketch/src/main/java/me/xiaopan/sketch/uri/HttpsUriModel.java
[DrawableUriModel]: ../../sketch/src/main/java/me/xiaopan/sketch/uri/DrawableUriModel.java
[FileUriModel]: ../../sketch/src/main/java/me/xiaopan/sketch/uri/FileUriModel.java
[FileVariantUriModel]: ../../sketch/src/main/java/me/xiaopan/sketch/uri/FileVariantUriModel.java
[AndroidResUriModel]: ../../sketch/src/main/java/me/xiaopan/sketch/uri/AndroidResUriModel.java
[AbsDiskCacheUriModel]: ../../sketch/src/main/java/me/xiaopan/sketch/uri/AbsDiskCacheUriModel.java
[AbsStreamDiskCacheUriModel]: ../../sketch/src/main/java/me/xiaopan/sketch/uri/AbsStreamDiskCacheUriModel.java
[XpkIconUriModel]: ../../sample/src/main/java/me/xiaopan/sketchsample/util/XpkIconUriModel.java
[Base64UriModel]: ../../sketch/src/main/java/me/xiaopan/sketch/uri/Base64UriModel.java
[Base64VariantUriModel]: ../../sketch/src/main/java/me/xiaopan/sketch/uri/Base64VariantUriModel.java
[AbsBitmapDiskCacheUriModel]: ../../sketch/src/main/java/me/xiaopan/sketch/uri/AbsBitmapDiskCacheUriModel.java
[ApkIconUriModel]: ../../sketch/src/main/java/me/xiaopan/sketch/uri/ApkIconUriModel.java
[VideoThumbnailUriModel]: ../../sample-video-thumbnail/src/main/java/me/xiaopan/ssvt/VideoThumbnailUriModel.java
[AppIconUriModel]: ../../sketch/src/main/java/me/xiaopan/sketch/uri/AppIconUriModel.java
[ContentUriModel]: ../../sketch/src/main/java/me/xiaopan/sketch/uri/ContentUriModel.java
[DiskCache]: ../../sketch/src/main/java/me/xiaopan/sketch/cache/DiskCache.java
[DownloadResult]: ../../sketch/src/main/java/me/xiaopan/sketch/request/DownloadResult.java
[DataSource]: ../../sketch/src/main/java/me/xiaopan/sketch/datasource/DataSource.java

[统一资源标志符 - 维基百科]: https://zh.wikipedia.org/wiki/%E7%BB%9F%E4%B8%80%E8%B5%84%E6%BA%90%E6%A0%87%E5%BF%97%E7%AC%A6
