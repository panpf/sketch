### Resize 用来干嘛？

Resize 用来在解码时调整图片的尺寸和宽高比例

有的时候我们需要固定尺寸或固定宽高比的图片，例如将图片用作页面头部的背景，设计图上有标好的尺寸，但是图片的尺寸我们不能控制，这时候就可以用 Resize 用来解决问题

### 使用

假如设计图上标明的图片尺寸是 720x385，那么我们如实配置即可

```java
SketchImageView sketchImageView = ...;
sketchImageView.getOptions().setResize(720, 385);
sketchImageView.displayImage("http://t.cn/RShdS1f");
```

注意，Resize 有两种模式：
* ASPECT_RATIO_SAME: 新图片的尺寸不会比 resize 大，但宽高比一定会一样
* EXACTLY_SAME: 即使原图尺寸比 resize 小，也会得到一个跟 resize 尺寸一样的 bitmap

Resize 默认采用 ASPECT_RATIO_SAME 模式，这样的好处是会比较节省内存，因此通过上面的配置你会得到一张宽高比一定是 720/385 的图片，但尺寸可能会比 720x385 小的图片

如果你必须要求返回图片的尺寸跟 Resize 一模一样，那么你可以使用 EXACTLY_SAME 模式

```java
SketchImageView sketchImageView = ...;
sketchImageView.getOptions().setResize(new Resize(720, 385, Resize.EXACTLY_SAME));
sketchImageView.displayImage("http://t.cn/RShdS1f");
```

### 调整尺寸时的裁剪规则

当 Resize 的尺寸和原图片的尺寸不一致时就会对原图片进行裁剪，具体的裁剪规则是根据 Resize 的 scaleType 属性决定的。

默认从 ImageView 获取，你也可以强制指定，如下：

```java
SketchImageView sketchImageView = ...;
sketchImageView.getOptions().setResize(new Resize(720, 385, ScaleType.CENTER_CROP));
sketchImageView.displayImage("http://t.cn/RShdS1f");
```

### 自动使用 ImageView 的固定尺寸作为 Resize

当 ImageView 已经设置了固定尺寸的话，我们就不必再写一遍 resize 的尺寸，而是自动使用 ImageView 的尺寸，如下：

```java
SketchImageView sketchImageView = ...;
sketchImageView.getOptions().setResize(Resize.byViewFixedSize());
sketchImageView.displayImage("http://t.cn/RShdS1f");
```

Resize.byViewFixedSize() 方法也可以设置 Resize.Mode，如下：

```java
SketchImageView sketchImageView = ...;
sketchImageView.getOptions().setResize(Resize.byViewFixedSize(Resize.EXACTLY_SAME));
sketchImageView.displayImage("http://t.cn/RShdS1f");
```

但是当你使用了此功能而 ImageView 却没有设置固定尺寸的话就会抛出异常，如下：

```java
IllegalStateException: ImageView's width and height are not fixed, can not be applied with the Resize.byViewFixedSize() function
```
