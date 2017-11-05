# 使用 ImageDisplayer 以动画的方式显示图片

通常显示 Bitmap 都是通过 setBitmapDrawable(Drawable) 方法，但是这个方法默认没有任何动画，会显的很突兀

现在已经不是 Android 2.x 的时代了，那时候只要你的 APP 能正确的显示图片就可以了，现在是体验为王的时代，因此我们要提升图片的显示体验

[ImageDisplayer] 就是最后用来显示图片的，通过 [ImageDisplayer] 可以以更自然、更炫酷的方式显示图片，提升用户体验

### 使用

可在 [DisplayOptions] 和 [DisplayHelper] 中使用，例如：

```java
// SketchImageView
SketchImageView sketchImageView = ...;
sketchImageView.getOptions().setDisplayer(new TransitionImageDisplayer());

// DisplayHelper
Sketch.with(context).display(uri, sketchImageView)
    .displayer(new TransitionImageDisplayer())
    .commit();
```

目前内置了以下几种 [ImageDisplayer]：

* [DefaultImageDisplayer]： 没有任何动画效果，默认的图片显示器
* [TransitionImageDisplayer]： 通过 TransitionDrawable 用当前图片（没有的话就创建一张透明的 Drawable代替）和新图片以过渡渐变的方式显示图片，请参考 [使用 TransitionImageDisplayer 以自然过渡渐变的方式显示图片](transition_image_displayer.md)
* [ZoomInImageDisplayer]：由小到大的显示图片，缩放比例是从 0.5f 到 1.0f
* [ZoomOutImageDisplayer]：由大到小的显示图片，缩放比例是从 1.5f 到 1.0f
* [ColorTransitionImageDisplayer]：用指定的颜色创建一个 Drawable 同新图片以过渡效果显示
* [FadeInImageDisplayer]：以渐入效果显示图片

### setAlwaysUse(boolean)

默认的显示从内存里取出的缓存图片时不使用 [ImageDisplayer]，但在两张图片来回交替显示在同一个 ImageView 的场景下，如果两张图片都已经存在于内存缓存中了，这时候就会因没有过渡动画而显得不自然

对于这样的场景你可以通过 [ImageDisplayer].setAlwaysUse(true) 方法设置只要涉及到显示图片就必须使用 [ImageDisplayer]

### 自定义

你还可以自定义 [ImageDisplayer]，用你喜欢的方式显示图片，但有几点需要注意：

1. 要先过滤一下 bitmap 为 null 或已经回收的情况
2. 调用 startAnimation() 执行动画之前要下调用 clearAnimation() 清理一下
3. 尽量使用 [ImageDisplayer].DEFAULT_ANIMATION_DURATION 作为动画持续时间

[ImageDisplayer]: ../../sketch/src/main/java/me/panpf/sketch/display/ImageDisplayer.java
[DefaultImageDisplayer]: ../../sketch/src/main/java/me/panpf/sketch/display/DefaultImageDisplayer.java
[TransitionImageDisplayer]: ../../sketch/src/main/java/me/panpf/sketch/display/TransitionImageDisplayer.java
[ZoomInImageDisplayer]: ../../sketch/src/main/java/me/panpf/sketch/display/ZoomInImageDisplayer.java
[ZoomOutImageDisplayer]: ../../sketch/src/main/java/me/panpf/sketch/display/ZoomOutImageDisplayer.java
[ColorTransitionImageDisplayer]: ../../sketch/src/main/java/me/panpf/sketch/display/ColorTransitionImageDisplayer.java
[FadeInImageDisplayer]: ../../sketch/src/main/java/me/panpf/sketch/display/FadeInImageDisplayer.java
[transition_image_displayer]: transition_image_displayer.md
[LoadOptions]: ../../sketch/src/main/java/me/panpf/sketch/request/LoadOptions.java
[DisplayOptions]: ../../sketch/src/main/java/me/panpf/sketch/request/DisplayOptions.java
[LoadHelper]: ../../sketch/src/main/java/me/panpf/sketch/request/LoadHelper.java
[DisplayHelper]: ../../sketch/src/main/java/me/panpf/sketch/request/DisplayHelper.java
