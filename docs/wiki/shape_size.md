ShapeSize用来在绘制图片时修改图片的尺寸

>* 不会创建新的图片，原理就是用BitmapShader将图片填充到画笔里，然后画一个形状即可
>* 当ShapeSize同原图的宽高比不一致时就会仅显示原图中间的部分，类似CENTER_CROP效果
>* loadingImage、errorImage、pauseDownloadImage以及加载的图片都会被修改

在DisplayOptions中设置即可使用：
```java
DisplayOptions options = new DisplayOptions();
...
// 以300x300的尺寸显示图片
options.setShapeSize(300, 300);

SketchImageView sketchImageView = ...;
sketchImageView.setOptions(options);
sketchImageView.displayImage(R.drawable.sample);
```

还可以用ImageView的固定宽高作为ShapeSize，如下：
```java
DisplayOptions options = new DisplayOptions();
options.setShapeSizeByFixedSize(true);
```
这时候ImageView的layout_width和layout_height必须是固定的值否则就会抛异常
