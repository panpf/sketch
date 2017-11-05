package me.panpf.sketch.sample.videothumbnail

import android.content.Context

import me.panpf.sketch.Configuration
import me.panpf.sketch.Initializer

class SketchInitializer : Initializer {

    override fun onInitialize(context: Context, configuration: Configuration) {
        configuration.uriModelManager.add(VideoThumbnailUriModel())
    }
}
