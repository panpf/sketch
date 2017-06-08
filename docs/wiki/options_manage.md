## 管理多个 Options
当你需要为每一种类型的图片都定义一个Options的时候就会有很多的Options，那么该怎么方便的去使用和管理这些Options呢？

完整示例如下：
```java

public class ImageOptions {
    /**
     * 通用矩形
     */
    public static final int RECT = 101;

    /**
     * 带描边的圆形
     */
    public static final int CIRCULAR_STROKE = 102;

    /**
     * 圆角矩形
     */
    public static final int ROUND_RECT = 104;

    private static final SparseArray<OptionsHolder> OPTIONS_HOLDER_SPARSE_ARRAY = new SparseArray<OptionsHolder>();

    static {
        OPTIONS_HOLDER_SPARSE_ARRAY.append(ImageOptions.RECT, new OptionsHolder() {
            @Override
            protected DownloadOptions onCreateOptions(Context context) {
                return new DisplayOptions()
                        .setLoadingImage(R.drawable.image_loading)
                        .setErrorImage(R.drawable.image_error)
                        .setPauseDownloadImage(R.drawable.image_pause_download)
                        .setImageDisplayer(new TransitionImageDisplayer())
                        .setShapeSizeByFixedSize(true);
            }
        });

        OPTIONS_HOLDER_SPARSE_ARRAY.append(ImageOptions.CIRCULAR_STROKE, new OptionsHolder() {
            @Override
            protected DownloadOptions onCreateOptions(Context context) {
                return new DisplayOptions()
                        .setLoadingImage(R.drawable.image_loading)
                        .setErrorImage(R.drawable.image_error)
                        .setPauseDownloadImage(R.drawable.image_pause_download)
                        .setImageDisplayer(new TransitionImageDisplayer())
                        .setImageShaper(new CircleImageShaper().setStroke(Color.WHITE, SketchUtils.dp2px(context, 1)))
                        .setShapeSizeByFixedSize(true);
            }
        });

        OPTIONS_HOLDER_SPARSE_ARRAY.append(ImageOptions.ROUND_RECT, new OptionsHolder() {
            @Override
            protected DownloadOptions onCreateOptions(Context context) {
                return new DisplayOptions()
                        .setLoadingImage(R.drawable.image_loading)
                        .setErrorImage(R.drawable.image_error)
                        .setPauseDownloadImage(R.drawable.image_pause_download)
                        .setImageShaper(new RoundRectImageShaper(SketchUtils.dp2px(context, 6)))
                        .setImageDisplayer(new TransitionImageDisplayer())
                        .setShapeSizeByFixedSize(true);
            }
        });
    }

    private ImageOptions() {
    }

    @SuppressWarnings("unused")
    public static DisplayOptions getDisplayOptions(Context context, @Type int optionsId) {
        return (DisplayOptions) OPTIONS_HOLDER_SPARSE_ARRAY.get(optionsId).getOptions(context);
    }

    @SuppressWarnings("unused")
    public static LoadOptions getLoadOptions(Context context, @Type int optionsId) {
        return (LoadOptions) OPTIONS_HOLDER_SPARSE_ARRAY.get(optionsId).getOptions(context);
    }

    @SuppressWarnings("unused")
    public static DownloadOptions getDownloadOptions(Context context, @Type int optionsId) {
        return OPTIONS_HOLDER_SPARSE_ARRAY.get(optionsId).getOptions(context);
    }

    private static abstract class OptionsHolder {
        private DownloadOptions options;

        public DownloadOptions getOptions(Context context) {
            if (options == null) {
                synchronized (this) {
                    if (options == null) {
                        options = onCreateOptions(context);
                    }
                }
            }
            return options;
        }

        protected abstract DownloadOptions onCreateOptions(Context context);
    }

    @IntDef({
            RECT,
            CIRCULAR_STROKE,
            ROUND_RECT,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type{

    }
}

```

然后在使用的时候就直接通过id从ImageOptions中取，如下：
```java
SketchImageView imageView = ...;
imageView.setOptions(ImageOptions.getDisplayOptions(context, ImageOptions.ROUND_RECT));
```
参考[ImageOptions.java](../../sample/src/main/java/me/xiaopan/sketchsample/ImageOptions.java)

你还可以继承SketchImageView，然后重载setOptions()方法，如下：
```java
public class MyImageView extends SketchImageView {
    ...

    public void setOptions(@ImageOptions.Type int optionsId){
        setOptions(ImageOptions.getDisplayOptions(getContext(), optionsId));
    }
}
```
参考[SampleImageView.java](../../sample/src/main/java/me/xiaopan/sketchsample/widget/SampleImageView.java)

使用的时候指定id即可：
```java
MyImageView imageView = ...;
imageView.setOptions(ImageOptions.ROUND_RECT);
```
