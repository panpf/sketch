# SketchImageView

Translations: [简体中文](sketch_image_view_zh.md)

`需要导入 sketch-extensions 模块`

### XML 属性

[SketchImageView] 提供了丰富的 xml 属性可以在布局中配置请求属性，如下：

```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto" 
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <com.github.panpf.sketch.sample.widget.MyImageView 
        android:id="@+id/testFragmentImageView"
        android:layout_width="300dp" 
        android:layout_height="300dp"
        android:contentDescription="@string/app_name"
        app:sketch_placeholder="@drawable/im_placeholder"
        app:sketch_error="@drawable/im_error"
        app:sketch_uriEmptyError="@drawable/im_uri_empty"
        app:sketch_crossfade="true"
        app:sketch_transformation="rotate"
        app:sketch_transformation_rotate_degrees="55" />
</FrameLayout>
```

更多支持的属性请参考 [attrs][attrs] 文件

### 其它功能

得益于实现了 [ViewAbilityContainer] 接口，[SketchImageView] 还支持以下功能：

* [显示下载进度][show_download_progress]
* [显示图片类型角标][show_image_type]

[SketchImageView]: ../../sketch-extensions-core/src/main/kotlin/com/github/panpf/sketch/SketchImageView.kt

[ViewAbilityContainer]: ../../sketch-viewability/src/main/kotlin/com/github/panpf/sketch/viewability/ViewAbilityContainer.kt

[attrs]: ../../sketch-extensions-core/src/main/res/values/attrs.xml

[show_download_progress]: show_download_progress.md

[show_image_type]: show_image_type.md