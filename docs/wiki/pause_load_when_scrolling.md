# 列表滑动中暂停加载图片

列表滑动中暂停加载功能可以在列表滑动中将 [ImageRequest] 的 depth 参数设置为 [Depth]
.MEMORY，这样就只会从内存中去找图片，不会再加载新图片，这在性能较差的设备上能显著提高列表滑动流畅度

### 配置

`需要导入 sketch-extensions 模块`

第 1 步. 在你的列表控件 RecyclerView 或 ListView 上添加滑动监听，如下：

```kotlin
recyclerView.addOnScrollListener(PauseLoadWhenScrollingMixedScrollListener())

// 或

listView.setOnScrollListener(PauseLoadWhenScrollingMixedScrollListener())
```

第 2 步. 注册

在初始化 [Sketch] 时添加 [PauseLoadWhenScrollingDrawableInterceptor] 请求拦截器，这样所有的 ImageRequest 都可以使用，如下：

```kotlin
class MyApplication : Application(), SketchFactory {

    override fun createSketch(): Sketch {
        return Sketch.Builder(this).apply {
            components {
                addDrawableDecodeInterceptor(PauseLoadWhenScrollingDrawableInterceptor())
            }
        }.build()
    }
}
```

或者在显示图片时只给当前 ImageRequest 注册，这样就只有当前 ImageRequest 可以使用，如下：

```kotlin
imageView.displayImage("https://www.sample.com/image.jpg") {
    components {
        addDrawableDecodeInterceptor(PauseLoadWhenScrollingDrawableInterceptor())
    }
}
```

> 注意：[PauseLoadWhenScrollingDrawableInterceptor] 仅对 [DisplayRequest] 有效

第 3 步. 针对单个请求开启列表滑动中暂停加载功能，如下：

```kotlin
imageView.displayImage("https://www.sample.com/image.jpg") {
    pauseLoadWhenScrolling(true)
}
```

[Sketch]: ../../sketch/src/main/java/com/github/panpf/sketch/Sketch.kt

[DisplayRequest]: ../../sketch/src/main/java/com/github/panpf/sketch/request/DisplayRequest.kt

[PauseLoadWhenScrollingDrawableInterceptor]: ../../sketch-extensions/src/main/java/com/github/panpf/sketch/request/PauseLoadWhenScrollingDrawableInterceptor.kt

[ImageRequest]: ../../sketch/src/main/java/com/github/panpf/sketch/request/ImageRequest.kt

[Depth]: ../../sketch/src/main/java/com/github/panpf/sketch/request/Depth.kt