package me.panpf.sketch.sample.videothumbnail

import android.content.Context

import me.xiaopan.sketch.Configuration
import me.xiaopan.sketch.Initializer

class SketchInitializer : Initializer {

    override fun onInitialize(context: Context, configuration: Configuration) {
        configuration.uriModelManager.add(VideoThumbnailUriModel())
    }
}
