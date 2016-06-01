>* ``新增``. RequestFuture增加了getName()方法用于获取请求名称
>* ``优化``. 优化了SpearImageView中onDetachedFromWindow()取消时的日志
>* ``新增``. SpearImageView的setImageUriBy***系列方法新增了返回值，返回对应的RequestFuture，方便查看请求的状态
>* ``修改``. SpearImageView的setImageByUri(Uri)方法改名为setImageByContent(Uri)