# SketchImageView

翻译：[English](sketch_image_view.md)

> [!IMPORTANT]
> 必须导入 `sketch-extensions-view` 或 `sketch-extensions-view-core` 模块

### XML 属性

[SketchImageView] 提供了丰富的 xml 属性可以在布局中配置请求属性，如下：

```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto" 
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <com.github.panpf.sketch.SketchImageView 
        android:id="@+id/imageView"
        android:layout_width="300dp" 
        android:layout_height="300dp"
        android:contentDescription="@string/app_name"
        app:sketch_placeholder="@drawable/im_placeholder"
        app:sketch_error="@drawable/im_error"
        app:sketch_uriEmpty="@drawable/im_uri_empty"
        app:sketch_crossfade="true"
        app:sketch_transformation="rotate"
        app:sketch_transformation_rotate_degrees="55" />
</FrameLayout>
```

更多支持的属性请参考 [attrs][attrs] 文件

### RequestState

[SketchImageView] 提供了 flow 的方式来监听请求的状态和结果，如下：

```kotlin
val sketchImageView = SketchImageView(context)

// 收集状态
scope.launch {
    sketchImageView.requestState.loadState.collect {
        when (it) {
            is LoadState.Started -> {
                val request: ImageRequest = it.request
            }
            is LoadState.Success -> {
                val request: ImageRequest = it.request
                val result: ImageResult.Success = it.result
            }
            is LoadState.Error -> {
                val request: ImageRequest = it.request
                val result: ImageResult.Error = it.result
            }
            is LoadState.Canceled -> {
                val request: ImageRequest = it.request
            }
            else -> {
                // null
            }
        }
    }
}

// 收集结果
scope.launch {
    sketchImageView.requestState.resultState.collect {
        when (it) {
            is ImageResult.Success -> {
            }
            is ImageResult.Error -> {
            }
            else -> {
                // null
            }
        }
    }
}

// 收集进度
scope.launch {
    sketchImageView.requestState.progressState.collect {
        if (it != null) {
            val totalLength: Long = it.totalLength
            val completedLength: Long = it.completedLength
        } else {
            // null
        }
    }
}
```

### 其它功能

得益于实现了 [ViewAbilityContainer] 接口，[SketchImageView] 还支持以下功能：

* [显示下载进度][show_download_progress]
* [显示图片类型角标][show_image_type]

[SketchImageView]: ../../sketch-extensions-view-core/src/main/kotlin/com/github/panpf/sketch/SketchImageView.kt

[ViewAbilityContainer]: ../../sketch-extensions-view-ability/src/main/kotlin/com/github/panpf/sketch/ability/ViewAbilityContainer.kt

[attrs]: ../../sketch-extensions-view-core/src/main/res/values/attrs.xml

[show_download_progress]: progress_indicator

[show_image_type]: mime_type_logo.md