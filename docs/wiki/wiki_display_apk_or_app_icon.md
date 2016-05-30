Sketch支持直接读取APK文件的图标，如下：
```java
SketchImageView sketchImageView = ...;
sketchImageView.displayImage("/sdcard/google_play.apk");
```
![apps](https://github.com/xiaopansky/Sketch/raw/master/docs/res/apps.png)


使用load()方法加载apk图标也可以
```java
Sketch.with(context).load("/sdcard/google_play.apk", new LoadListener(){
	...
}).commit();
```