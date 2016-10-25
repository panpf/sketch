ImageShaper用来在绘制时修改图片的形状，

>* 是通过BitmapShader实现的，所以不需要创建新的图片
>* loadingImage、errorImage、pauseDownloadImage以及加载的图片都会被修改

在DisplayOptions中设置即可使用：

```java
DisplayOptions options = new DisplayOptions();
...
// 以20度圆角矩形的形状显示图片
options.setImageShaper(new RoundRectImageShaper(20));

SketchImageView sketchImageView = ...;
sketchImageView.setOptions(options);
sketchImageView.displayImage(R.drawable.sample);
```

目前内置了两种ImageShaper：
>* RoundRectImageShaper：圆角矩形，还支持描边
>* CircleImageShaper：圆形，还支持描边

如果需要在绘制时同时改变图片的尺寸就要用到ShapeSize了，[点击查看ShapeSize介绍](shape_size.md)
