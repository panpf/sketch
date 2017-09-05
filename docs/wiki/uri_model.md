# 自定义 UriModel 让 Sketch 支持新的 Uri 类型

Sketch 通过 [UriModel] 来支持不同的 uri，理所当然也具备扩展的功能

### 扩展 uri

1.继承 [UriModel] 类定义你的扩展 UriModel，实现如下方法：
* UriModel match(Context, String)：匹配 uri，返回 true 将使用 此 UriMode 来处理 uri
* DataSource getDataSource(Context, String, DownloadResult)：获取指定 uri 的解码需要的数据源

注意：
* 你的 UriModel 应该匹配只有你的 UriModel 才使用的 uri 协议，这个随意定义即可
* getDataSource(Context, String, DownloadResult) 方法不能返回 null，如果遇到错误应该抛出 GetDataSourceEsception()异常

2.在初始化 Sketch 时 通过 Sketch.with(context).getConfiguration().getUriModelRegistry().add(UriModel) 方法注册即可

更多细节请参考 [UriModel] 的实现类

[UriModel]: ../../sketch/src/main/java/me/xiaopan/sketch/uri/UriModel.java
