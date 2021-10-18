# 通过 ShapeSize 在绘制时改变图片的尺寸

[ShapeSize] 用来配置 bitmap 以多大的尺寸显示，不会创建新的图片，原理就是用 BitmapShader 将图片填充到画笔里，然后画一个形状即可

### 使用

在 [DisplayOptions] 中设置即可，如下：

```java
SketchImageView sketchImageView = ...;
sketchImageView.getOptions().setShapeSize(300, 300);
sketchImageView.displayImage("http://t.cn/RShdS1f");
```

### 作用范围

loadingImage、errorImage、pauseDownloadImage 以及加载的图片都会被修改

### 调整尺寸时的裁剪规则

当 [ShapeSize] 的尺寸和原图片的尺寸不一致时就会对原图片进行裁剪，具体的裁剪规则是根据 [ShapeSize] 的 scaleType 属性决定的

默认从 ImageView 获取，你也可以强制指定，如下：

```java
SketchImageView sketchImageView = ...;
sketchImageView.getOptions().setShapeSize(new ShapeSize(300, 300, ScaleType.CENTER_CROP));
sketchImageView.displayImage("http://t.cn/RShdS1f");
```

### 使用 ImageView 的固定尺寸作为 ShapeSize

当 ImageView 已经设置了固定尺寸的话，我们就可以使用 ImageView 的固定尺寸作为 [ShapeSize]，如下：

```java
SketchImageView sketchImageView = ...;
sketchImageView.getOptions().setShapeSize(ShapeSize.byViewFixedSize());
sketchImageView.displayImage("http://t.cn/RShdS1f");
```

但是当你使用了此功能而 ImageView 却没有设置固定尺寸的话就会抛出异常，如下：

```java
IllegalStateException: ImageView's width and height are not fixed, can not be applied with the ShapeSize.byViewFixedSize() function
```

[ShapeSize]: ../../sketch/src/main/java/com/github/panpf/sketch/request/ShapeSize.java
[DisplayOptions]: ../../sketch/src/main/java/com/github/panpf/sketch/request/DisplayOptions.java
