TransitionImageDisplayer通过TransitionDrawable可以以过渡渐变的方式显示新图片（新图片渐渐显示，旧图片渐渐消失），这应该是最自然、最舒服的图片显示方式了。

TransitionImageDisplayer默认使用ImageView上当前显示的图片（设置了loadingImage就用loadingImage，没有的话就创建一个透明的ColorDrawable）作为旧图片和新图片以过渡渐变的方式显示

#### 尺寸不一致的两张图片如何才能不变形

TransitionDrawable的使用条件是比较苛刻的，当两张图片的尺寸不一致时，在计算TransitionDrawable的宽或高时就会以两张图片里最大的宽或高作为TransitionDrawable的宽或高，然后在绘制时较小的图片就会被拉伸导致变形

源码如下：
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
`TransitionDrawable extends LayerDrawable`

为了解决这个问题，Sketch引入了[ShapeSize](shape_size.md)功能，可以在绘制的时候以指定的尺寸显示图片，当ShapeSize和图片比例不一致，则只显示图片中间的部分，类似CENTER_CROP效果。

因此在使用TransitionImageDisplayer的时候再指定ShapeSize让两张图片的尺寸保持一致，这样即可完美过渡显示，如下：
```java
SketchImageView sketchImageView = ...;
DisplayOptions options = sketchImageView.getOptions();

options.setLoadingImage(R.drawable.loading);
options.setImageDisplayer(new TransitionImageDisplayer());

// 以300x300的尺寸显示loadingImage和最终的图片
options.setShapeSize(300, 300);

sketchImageView.displayImage(R.drawable.sample);
```

>* ShapeSize会同时修改loadingImage、errorImage、pauseDownloadImage以及新图片的尺寸
>* 没有配置loadingImage时无需配置ShapeSize
>* 配置了loadingImage却没有配置ShapeSize时Sketch就会尝试用ImageView的固定宽高（layout_width和layout_height是固定的值）作为ShapeSize，如果宽高不固定就只能抛运行时异常了

[详细了解ShapeSize](shape_size.md)
