解码：
>* ``优化`` 解码部分捕获所有异常和错误，避免崩溃

下载：
>* ``优化`` API8以及以下改为使用HttpClient

请求：
>* ``优化`` 增加对不符合使用TransitionImageDisplayer情况的过滤，处理方式是直接抛出异常

缓存:
>* ``BUG`` 修复直接缓存了带有FixedSize导致显示错误的BUG

其它：
>* ``优化`` 持有Context的时候获取ApplicationContext避免误持有Activity
>* ``BUG`` 修复计算InSampleSize时遇到一边未知一边固定的情况会计算出超大的inSampleSize
>* ``BUG`` 修复设置FixedSize时按照src设置bounds导致图片尺寸不一致的BUG
