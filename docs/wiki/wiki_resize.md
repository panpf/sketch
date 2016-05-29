resize用来修剪图片以及调整宽高比例，以最大限度的节省内存

使用maxSize可以加载合适尺寸的图片到内存中，那么有的时候我们可能还需要固定尺寸的图片或固定宽高比例的图片，resize就是 用来解决这个问题的。

ImageProcessor在处理图片的时候，如果原图的尺寸大于resize则按resize裁剪，如果小于resize则会按照resize的比例修剪原图，让原图的宽高比同resize一样。

如果forceUseResize为true，则即使原图尺寸小于resize，则也会按照resize的尺寸创建一张新的图片。

####使用
```java
DisplayOptions options = ...;
options.setResize(300, 300);
options.seForceUseResize(true);
```

```java
Sketch.with(context).load(R.drawable.ic_launcher, new LoadListener(){
...
})
.resize(300, 300)
.commit();
```

使用DisplayOptions的时候还可以使用resizeByFixedSize(true)方法，使用SketchImageView的layout size作为resize