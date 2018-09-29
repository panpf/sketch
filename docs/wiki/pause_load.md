# 列表滑动时暂停加载图片，提升列表滑动流畅度

在性能较差的设备上在列表滑动的同时加载图片会影响列表的滑动流畅度，因此 Sketch 特地提供了暂停加载图片功能来帮助实现列表滑动时暂停加载图片功能，暂停后 Sketch 将只会从内存中去找图片

``暂停加载功能只对 display 请求有效``

### 使用
如果你想自己控制暂停加载，只需执行如下代码即可：

```java
// 全局暂停从本地加载载图片
Sketch.with(context).getConfiguration().setPauseLoadEnabled(true);

// 全局恢复从本地加载载图片
Sketch.with(context).getConfiguration().setPauseLoadEnabled(false);
```

### 列表滑动时停止加载

由于监控列表的滑动状态需要实现不同列表 View 的接口，因此 Sketch 没有内置滑动监听功能，需要你添加  sample app 中的  [ScrollingPauseLoadManager] 到你的 app 中，支持 RecyclerView 和 AbsListView

然后在使用列表 View 的时候设置滑动监听即可

```java
RecyclerView recyclerView = ...;
recyclerView.setOnScrollListener(new ScrollingPauseLoadManager(context));

ListView listView = ...;
listView.setOnScrollListener(new ScrollingPauseLoadManager(context));
```

注意：
* 推荐只在性比较差的设备上开启此功能，你可以通过 Android 版本号来判断，比如针对 4.4 以下的设备开启此功能

[ScrollingPauseLoadManager]: ../../sample/src/main/java/me/panpf/sketch/sample/util/ScrollingPauseLoadManager.java
