# SketchImageView

Translations: [简体中文](sketch_image_view_zh.md)

> [!IMPORTANT]
> Required import `sketch-extensions` module

### XML attributes

[SketchImageView] provides a wealth of xml attributes to configure request attributes in the layout,
as follows:

```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto" android:layout_width="match_parent"
    android:layout_height="match_parent">

    <com.github.panpf.sketch.sample.widget.MyImageView android:id="@+id/testFragmentImageView"
        android:layout_width="300dp" android:layout_height="300dp"
        android:contentDescription="@string/app_name"
        app:sketch_placeholder="@drawable/im_placeholder" app:sketch_error="@drawable/im_error"
        app:sketch_uriEmptyError="@drawable/im_uri_empty" app:sketch_crossfade="true"
        app:sketch_transformation="rotate" app:sketch_transformation_rotate_degrees="55" />
</FrameLayout>
```

For more supported attributes, please refer to the [attrs][attrs] file.

### Other Functions

Thanks to the implementation of the [ViewAbilityContainer] interface, [SketchImageView] also
supports the following functions:

* [Show download progress][show_download_progress]
* [Show image type badge][show_image_type]

[SketchImageView]: ../../sketch-extensions-core/src/main/kotlin/com/github/panpf/sketch/SketchImageView.kt

[ViewAbilityContainer]: ../../sketch-viewability/src/main/kotlin/com/github/panpf/sketch/viewability/ViewAbilityContainer.kt

[attrs]: ../../sketch-extensions-core/src/main/res/values/attrs.xml

[show_download_progress]: show_download_progress.md

[show_image_type]: show_image_type.md