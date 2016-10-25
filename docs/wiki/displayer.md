ImageDisplayer是最后用来显示图片的

目前内置了以下几种ImageDisplayer：
>* DefaultImageDisplayer： 默认的图片显示器，没有任何动画效果
>* ZoomInImageDisplayer：由小到大的显示图片，缩放比例是从0.5f到1.0f
>* ZoomOutImageDisplayer：由大到小的显示图片，缩放比例是从1.5f到1.0f
>* TransitionImageDisplayer： 过渡效果显示器，通过TransitionDrawable用当前图片（没有的话就创建一张透明的drawable代替）和新图片以过渡渐变的方式显示
>* ColorTransitionImageDisplayer：颜色过渡显示器，你可以指定一种颜色作为过渡效果的起始色
>* FadeInImageDisplayer：渐入显示器

#### TransitionImageDisplayer

由于TransitionDrawable在碰到两张尺寸不一致的图片时，会依照尺寸比较大的图片强行将另一张图片拉伸，这样最终显示出来的效果就会变形

为了解决这个问题，在使用TransitionImageDisplayer的时候就需要ShapeSize配合来调整Drawable的尺寸，
如果没有设置ShapeSize的话就会用ImageView的固定宽高（layout_width和layout_height是固定的值）作为ShapeSize，但宽高不固定就只能抛运行时异常了

#### 自定义
你还可以自定义ImageDisplayer，用你喜欢的方式显示图片，有以下几点需要注意：

1. 要先过滤一下bitmap为null以及已经回收的情况
2. 调用startAnimation()执行动画之前要下调用clearAnimation()清理一下
3. 尽量使用ImageDisplayer.DEFAULT_ANIMATION_DURATION作为动画持续时间
