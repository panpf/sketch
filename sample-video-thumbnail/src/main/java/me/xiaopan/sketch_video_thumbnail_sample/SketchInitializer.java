package me.xiaopan.sketch_video_thumbnail_sample;

import android.content.Context;
import android.support.annotation.NonNull;

import me.xiaopan.sketch.Configuration;
import me.xiaopan.sketch.Initializer;

public class SketchInitializer implements Initializer {

    @Override
    public void onInitialize(@NonNull Context context, @NonNull Configuration configuration) {
        configuration.getUriModelRegistry().add(new VideoThumbnailUriModel());
    }
}
