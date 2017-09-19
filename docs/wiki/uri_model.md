# 自定义 UriModel 让 Sketch 支持新的 Uri 类型

Sketch 通过 [UriModel] 来支持不同的 uri，也具备扩展功能

### 扩展 uri

1.继承 [UriModel] 类自定义，实现如下方法：

* UriModel match(Context, String)：匹配 uri，返回 true 将使用此 [UriModel] 来处理 uri
* DataSource getDataSource(Context, String, DownloadResult)：获取指定 uri 解码需要的数据源

其它方法根据需求重写，如下：

* String getUriContent(String uri) ：获取 uri 的内容部分，默认完整返回 uri，但有的 uri 协议头部分在读取数据时是无用的，例如 [ApkIconUriModel]、[DrawableUriModel]
* String getDiskCacheKey(String uri) ：获取 uri 的 磁盘缓存 key，默认完整返回 uri，但有的 uri 就只需要 uri
的一部分作为 磁盘缓存 key，例如 [FileVariantUriModel]
* boolean isFromNet() ：此类型 uri 的数据是否来自网络，目前只有 [HttpUriModel] 和 [HttpsUriModel] 需要
* boolean isConvertShortUriForKey() ：此类型的 uri 在生成请求 key 的时候是否需要转成短 uri，因为请求 key 不能太长，如果 uri 太长会导致请求 key 太长，例如 [Base64UriModel] 就需要转成短 uri

注意：

* 你的 [UriModel] 应该匹配只有你的 [UriModel] 才使用的 uri 协议，这个随意定义即可
* getDataSource(Context, String, DownloadResult) 方法不能返回 null，如果遇到错误应该抛出 GetDataSourceException 异常
* getDataSource(Context, String, DownloadResult) 方法的第三个参数 [DownloadResult] 只有 [UriModel].isFromNet() 方法返回 true 的 [UriModel] 才会用到，例如 [HttpUriModel]

2.在初始化 Sketch 时 通过 Sketch.with(context).getConfiguration().getUriModelRegistry().add(UriModel) 方法注册即可

更多细节请参考 [UriModel] 的实现类：

* [AssetUriModel]
* [HttpUriModel]
  * [HttpsUriModel]
* [DrawableUriModel]
* [FileUriModel]
  * [FileVariantUriModel]
* [AndroidResUriModel]
* [AbsDiskCacheUriModel]
  * [AbsStreamDiskCacheUriModel]
    * [XpkIconUriModel]
    * [Base64UriModel]
      * [Base64VariantUriModel]
  * [AbsBitmapDiskCacheUriModel]
    * [ApkIconUriModel]:
    * [VideoThumbnailUriModel]
    * [AppIconUriModel]:
* [ContentUriModel]:


### AbsDiskCacheUriModel

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
[VideoThumbnailUriModel]: ../../sample-video-thumbnail/src/main/java/me/xiaopan/sketch_video_thumbnail_sample/VideoThumbnailUriModel.java
[AppIconUriModel]: ../../sketch/src/main/java/me/xiaopan/sketch/uri/AppIconUriModel.java
[ContentUriModel]: ../../sketch/src/main/java/me/xiaopan/sketch/uri/ContentUriModel.java
[DiskCache]: ../../sketch/src/main/java/me/xiaopan/sketch/cache/DiskCache.java
[DownloadResult]: ../../sketch/src/main/java/me/xiaopan/sketch/request/DownloadResult.java
