# SketchImageView

Translations: [简体中文](sketch_image_view_zh.md)

> [!IMPORTANT]
> Required import `sketch-extensions-view` module

### XML attributes

[SketchImageView] provides a wealth of xml attributes to configure request attributes in the layout,
as follows:

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

For more supported attributes, please refer to the [attrs][attrs] file.

### RequestState

[SketchImageView] provides a flow method to monitor the status and results of requests, as follows:

```kotlin
val sketchImageView = SketchImageView(context)

// collect state
scope.launch {
    sketchImageView.requestState.loadState.collect {
        when (it) {
            is LoadState.Started -> {
                val request: DisplayRequest = it.request
            }
            is LoadState.Success -> {
                val request: DisplayRequest = it.request
                val result: DisplayResult.Success = it.result
            }
            is LoadState.Error -> {
                val request: DisplayRequest = it.request
                val result: DisplayResult.Error = it.result
            }
            is LoadState.Canceled -> {
                val request: DisplayRequest = it.request
            }
            else -> {
                // null
            }
        }
    }
}

// collect result
scope.launch {
    sketchImageView.requestState.resultState.collect {
        when (it) {
            is DisplayResult.Success -> {
            }
            is DisplayResult.Error -> {
            }
            else -> {
                // null
            }
        }
    }
}

// collect progress
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

### Other Functions

Thanks to the implementation of the [ViewAbilityContainer] interface, [SketchImageView] also
supports the following functions:

* [Show download progress][show_download_progress]
* [Show image type badge][show_image_type]

[SketchImageView]: ../../sketch-extensions-view-core/src/main/kotlin/com/github/panpf/sketch/SketchImageView.kt

[ViewAbilityContainer]: ../../sketch-extensions-view-ability/src/main/kotlin/com/github/panpf/sketch/ability/ViewAbilityContainer.kt

[attrs]: ../../sketch-extensions-view-core/src/main/res/values/attrs.xml

[show_download_progress]: download_progress_indicator.md

[show_image_type]: mime_type_logo.md