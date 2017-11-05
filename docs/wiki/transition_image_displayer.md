# 使用 TransitionImageDisplayer 以自然过渡渐变的方式显示图片

[TransitionImageDisplayer] 通过 [TransitionDrawable] 可以过渡渐变的方式显示图片（新图片渐渐显示，旧图片渐渐消失），这应该是最自然、最舒服的图片显示方式了

[TransitionImageDisplayer] 默认使用 [ImageView] 正在显示的图片（设置了 loadingImage 就用 loadingImage，没有的话就创建一个透明的 ColorDrawable）作为旧图片和新图片以过渡渐变的方式显示

### 尺寸不一致的两张图片如何才能不变形

[TransitionDrawable] 的使用条件是比较苛刻的，当两张图片的尺寸不一致时，在计算 宽或高时就会以两张图片里最大的宽或高作为 [TransitionDrawable] 的宽或高，然后在绘制时较小的图片就会被拉伸导致变形

源码如下：

`TransitionDrawable extends LayerDrawable`

```java
public class LayerDrawable extends Drawable implements Drawable.Callback {
	....

	@Override
    public int getIntrinsicWidth() {
        int width = -1;
        int padL = 0;
        int padR = 0;

        final boolean nest = mLayerState.mPaddingMode == PADDING_MODE_NEST;
        final ChildDrawable[] array = mLayerState.mChildren;
        final int N = mLayerState.mNum;
        for (int i = 0; i < N; i++) {
            final ChildDrawable r = array[i];
            final int w = r.mDrawable.getIntrinsicWidth() + r.mInsetL + r.mInsetR + padL + padR;
            if (w > width) {
                width = w;
            }

            if (nest) {
                padL += mPaddingL[i];
                padR += mPaddingR[i];
            }
        }

        return width;
    }

	@Override
    public int getIntrinsicHeight() {
        int height = -1;
        int padT = 0;
        int padB = 0;

        final boolean nest = mLayerState.mPaddingMode == PADDING_MODE_NEST;
        final ChildDrawable[] array = mLayerState.mChildren;
        final int N = mLayerState.mNum;
        for (int i = 0; i < N; i++) {
            final ChildDrawable r = array[i];
            int h = r.mDrawable.getIntrinsicHeight() + r.mInsetT + r.mInsetB + padT + padB;
            if (h > height) {
                height = h;
            }

            if (nest) {
                padT += mPaddingT[i];
                padB += mPaddingB[i];
            }
        }

        return height;
    }
}
```

为了解决这个问题，Sketch 提供了 [ShapeSize] 功能（参考 [通过 ShapeSize 在绘制时改变图片的尺寸][shape_size]），可以在绘制的时候以指定的尺寸显示图片，当 [ShapeSize] 的尺寸和图片的尺寸比例不一致时，则根据 [ShapeSize] 的 scalyeType 属性决定显示图片的部分区域

因此在使用 [TransitionImageDisplayer] 的时候配置 [ShapeSize] 可以让两张图片的尺寸保持一致，这样即可完美过渡显示，如下：

```java
SketchImageView sketchImageView = ...;
DisplayOptions options = sketchImageView.getOptions();

options.setLoadingImage(R.drawable.loading);
options.setDisplayer(new TransitionImageDisplayer());

// 以 300x300 的尺寸显示 loadingImage 和最终的图片
options.setShapeSize(300, 300);

sketchImageView.displayImage(R.drawable.sample);
```

* [ShapeSize] 会同时修改 loadingImage、errorImage、pauseDownloadImage 以及新图片的尺寸
* 没有配置 loadingImage 时无需配置 [ShapeSize]
* 如果配置了 loadingImage 却没有配置 [ShapeSize]， Sketch 就会尝试用 [ImageView] 的固定尺寸作为 [ShapeSize]，如果无法使用固定尺寸就只能抛异常了

[TransitionImageDisplayer]: ../../sketch/src/main/java/me/panpf/sketch/display/TransitionImageDisplayer.java
[TransitionDrawable]: https://developer.android.google.cn/reference/android/graphics/drawable/TransitionDrawable.html
[ImageView]: https://developer.android.google.cn/reference/android/widget/ImageView.html
[shape_size]: shape_size.md
[ShapeSize]: ../../sketch/src/main/java/me/panpf/sketch/request/ShapeSize.java
