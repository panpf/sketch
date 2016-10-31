ImageDisplayer是最后用来显示图片的，通过ImageDisplayer可以以更炫酷的方式显示图片

目前内置了以下几种ImageDisplayer：
>* DefaultImageDisplayer： 没有任何动画效果，默认的图片显示器
>* TransitionImageDisplayer： 通过TransitionDrawable用当前图片（没有的话就创建一张透明的drawable代替）和新图片以过渡渐变的方式显示 [点击查看详细介绍以及注意事项](transition_displayer.md)
>* ZoomInImageDisplayer：由小到大的显示图片，缩放比例是从0.5f到1.0f
>* ZoomOutImageDisplayer：由大到小的显示图片，缩放比例是从1.5f到1.0f
>* ColorTransitionImageDisplayer：用指定的颜色创建一个Drawable同新图片以过渡效果显示
>* FadeInImageDisplayer：已渐入效果显示图片

ImageDisplayer还可以通过setAlwaysUse(boolean)方法设置只要涉及到显示图片就得使用ImageDisplayer（显示从内存里取出的缓存图片时也不例外）

#### 自定义
你还可以自定义ImageDisplayer，用你喜欢的方式显示图片，有以下几点需要注意：

1. 要先过滤一下bitmap为null以及已经回收的情况
2. 调用startAnimation()执行动画之前要下调用clearAnimation()清理一下
3. 尽量使用ImageDisplayer.DEFAULT_ANIMATION_DURATION作为动画持续时间
