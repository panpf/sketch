有的时候碰到被旋转的图片，看着很费劲，图片查看器支持了旋转还好说，要是不支持可就蛋疼了。

现在Sketch支持自动识别图片方向并且纠正，看起来就跟图片原本就是正常的一样，并且分块显示超大图功能也能自动纠正

`仅支持jpeg类型的图片`，因为目前只有jpeg类型的图片才会有exif信息

### 关闭自动纠正功能：

此功能是默认开启的，如果你需要个针对某个请求关闭此功能，设置如下：

#### Options：

```java
LoadOptions options = ...;
options.setCorrectImageOrientationDisabled(true);
```

or

```java
DisplayOptions options = ...;
options.setCorrectImageOrientationDisabled(true);
```

#### Helper：

```java
Sketch.with(context).load(...).disableCorrectImageOrientation().commit();
```

or

```java
Sketch.with(context).display(...).disableCorrectImageOrientation().commit();
```

### 获取图片方向

1.第一种方法：监听显示完成
```java
SketchImageView sketchImageView = ...;
sketchImageView.setDisplayListener(new DisplayListener(){
    @Override
    public void onCompleted(Drawable drawable, ImageFrom imageFrom, ImageAttrs imageAttrs){
        Log.d("Sketch", "image exif orientation is " + imageAttrs.getExifOrientation());
    }

    ...
});
```

2.第二种方法：直接通过Drawable获取
```java
SketchImageView sketchImageView = ...;
Drawable drawable = sketchImageView.getDrawable();
if (drawable != null && drawable instanceof SketchDrawable){
    SketchDrawable sketchDrawable = (SketchDrawable) drawable;
    Log.d("Sketch", "image exif orientation is " + sketchDrawable.getExifOrientation());
}
```

这里返回的方向是未经修饰的exif信息里记载的方向，你可以通过ImageOrientationCorrector类提供的工具方法解析这个方向，如下：
```
int exifOrientation = ...;

// 纠正方向时顺时针方向需要旋转的度数
int degrees = ImageOrientationCorrector.getExifOrientationDegrees(exifOrientation);

// 纠正方向时X轴需要是的缩放倍数，通常是1（不需要翻转）或-1（横向翻转）
int xScale = ImageOrientationCorrector.getExifOrientationTranslation(exifOrientation);
```
