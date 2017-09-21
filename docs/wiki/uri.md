# URI 类型及使用指南

Sketch 一共内置支持了 9 种类型的 uri，你还可以通过 [UriModel] 扩展 uri，详情请参考 [UriModel 详解及扩展 URI][uri_model]，下面就一一详解内置的几种 uri

### http or https uri

网络图片 uri，这种图片来自网络 使用之前需要先下载，对应方法如下：

```java
String netImageUri = "http://t.cn/RShdS1f" or "https://t.cn/RNdtMv2";

// Use SketchImageVie
SketchImageView sketchImageView = ...;
sketchImageView.displayImage(netImageUri);

// Use Sketch.display()
Sketch.with(context).display(netImageUri, sketchImageView).commit();

// Use Sketch.load()
Sketch.with(context).load(netImageUri, new LoadListener(){...}).commit();

// Use Sketch.download()
Sketch.with(context).download(netImageUri, new DownloadListener(){...}).commit();
```

### /sdcard/sample.png or file:///sdcard/sample.png uri

本地图片，位于本地磁盘上，对应方法如下：

```java
String imageFileUri = "/sdcard/sample.png" or "file:///sdcard/sample.png";

// Use SketchImageVie
SketchImageView sketchImageView = ...;
sketchImageView.displayImage(imageFileUri);

// Use Sketch.display()
Sketch.with(context).display(imageFileUri, sketchImageView).commit();

// Use Sketch.load()
Sketch.with(context).load(imageFileUri, new LoadListener(){...}).commit();

// Use Sketch.download()
Sketch.with(context).download(imageFileUri, new DownloadListener(){...}).commit();
```

### asset uri

Asset 资源，位于 APK 的 asset 文件夹内，对应方法如下：

```java
String assetResName = "sample.png";

// Use SketchImageVie
SketchImageView sketchImageView = ...;
sketchImageView.displayAssetImage(assetResName);

// Use Sketch.display()
Sketch.with(context).displayFromAsset(assetResName, sketchImageView).commit();

// Use Sketch.load()
Sketch.with(context).loadFromAsset(assetResName, new LoadListener(){...}).commit();
```

### drawable uri

drawable 资源，位于 APK 的 res/drawable 文件夹内，对应方法如下：

```java
int drawableResId = R.drawable.sample;

// Use SketchImageVie
SketchImageView sketchImageView = ...;
sketchImageView.displayResourceImage(drawableResId);

// Use Sketch.display()
Sketch.with(context).displayFromResource(drawableResId, sketchImageView).commit();

// Use Sketch.load()
Sketch.with(context).loadFromResource(drawableResId, new LoadListener(){...}).commit();
```

### content uri

content uri 通常是 ContentProvider 返回的，需要用 ContentResolver 读取，对应方法如下：

```java
String contentUri = "content://com.android.gallery/last";

// Use SketchImageVie
SketchImageView sketchImageView = ...;
sketchImageView.displayContentImage(contentUri);

// Use Sketch.display()
Sketch.with(context).displayFromContent(contentUri, sketchImageView).commit();

// Use Sketch.load()
Sketch.with(context).loadFromContent(contentUri, new LoadListener(){...}).commit();
```

### android.resource uri

android.resource uri 通过明确指定资源的包名、类型、名称来读取资源文件，android.resource uri 可以直接读取其它 APP 的资源，只要你知道资源名称或 ID，对应方法如下：

```java
// Make uri
String androidResUri = AndroidResUriModel.makeUriByName("com.android.settings", "drawable", "ic_launcher");
// 也可以通过资源的包名和 ID 读取
// String androidResUri = AndroidResUriModel.makeUriById("com.android.settings", 0x7f020000);

// Use SketchImageVie
SketchImageView sketchImageView = ...;
sketchImageView.displayImage(androidResUri);

// Use Sketch.display()
Sketch.with(context).display(androidResUri, sketchImageView).commit();

// Use Sketch.load()
Sketch.with(context).load(androidResUri, new LoadListener(){...}).commit();
```

### base64 uri

base64 uri 是将图片的数据转码成 base64 字符串来传递和显示，通常只适用于较小的图片，对应方法如下：

```java
String base64ImageUri = "data:image/jpeg;base64,/9j/4QaO...U7T/in//Z";

// Use SketchImageVie
SketchImageView sketchImageView = ...;
sketchImageView.displayImage(base64ImageUri);

// Use Sketch.display()
Sketch.with(context).display(base64ImageUri, sketchImageView).commit();

// Use Sketch.load()
Sketch.with(context).load(base64ImageUri, new LoadListener(){...}).commit();
```

### apk.icon uri

apk.icon uri 用于显示 apk 文件的图标，此功能特别适用于 APK 管理工具或垃圾清理工具，对应方法如下：

```java
// Make uri
String apkIconUri = ApkIconUriModel.makeUri("/sdcard/sample.apk");

// Use SketchImageVie
SketchImageView sketchImageView = ...;
sketchImageView.displayImage(apkIconUri);

// Use Sketch.display()
Sketch.with(context).display(apkIconUri, sketchImageView).commit();

// Use Sketch.load()
Sketch.with(context).load(apkIconUri, new LoadListener(){...}).commit();
```

### app.icon uri

app.icon uri 用于显示已安装 app 的图标，对应方法如下：

```java
// Make uri
String appIconUri = AppIconUriModel.makeUri("me.xiaopan.sketchsample", 241);

// Use SketchImageVie
SketchImageView sketchImageView = ...;
sketchImageView.displayImage(appIconUri);

// Use Sketch.display()
Sketch.with(context).display(appIconUri, sketchImageView).commit();

// Use Sketch.load()
Sketch.with(context).load(appIconUri, new LoadListener(){...}).commit();
```


[UriModel]: ../../sketch/src/main/java/me/xiaopan/sketch/uri/UriModel.java
[uri_model]: uri_model.md
