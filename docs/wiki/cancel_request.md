Sketch会自动取消请求，因此你不必刻意关注怎么去取消一个请求，或者该在什么时候去取消一个请求

#### 什么时候会自动取消请求？
>* SketchImageView重用的时候会自动取消之前的请求
>* SketchImageView在onDetachedFromWindow的时候也会自动取消请求

#### 如何主动取消请求？
>* 方法1：在执行commit()或SketchImageView.display***Image()方法之后你会得到一个Request，
你可以通过Request的isCanceled()方法查看请求是否结束会或通过Request的cancel()方法取消请求
>* 方法2：你可以通过Sketch.cancel(ImageViewInterface)方法来取消请求

``取消请求的时候如果正在读取数据，就会立马停止读取，已经读取的数据就算浪费了``
