修复BUG：
>* 修复TransitionImageDisplayer在碰到ImageView当前没有图片时崩溃的BUG

新增：
>* `StateImage` 新增OldStateImage，可以使用ImageView当前正在显示的图片作为loadingImage
>* `ImageDisplayer` ImageDisplayer新增setAlwaysUse(boolean)方法，可设置只要涉及到显示图片就得使用ImageDisplayer（显示从内存里取出的缓存图片时也不例外）