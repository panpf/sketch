package me.xiaopan.sketchsample.fragment;

import android.os.Bundle;
import android.view.View;

import me.xiaopan.androidinjector.InjectContentView;
import me.xiaopan.androidinjector.InjectView;
import me.xiaopan.sketch.SketchImageView;
import me.xiaopan.sketch.request.CancelCause;
import me.xiaopan.sketch.request.DisplayListener;
import me.xiaopan.sketch.request.FailedCause;
import me.xiaopan.sketch.request.ImageFrom;
import me.xiaopan.sketchsample.MyFragment;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.scale.ImageAmplifier;

@InjectContentView(R.layout.fragment_large_image)
public class LargeImageFragment extends MyFragment{

    @InjectView(R.id.largeImage) private SketchImageView largeImageView;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ImageAmplifier imageAmplifier = new ImageAmplifier(largeImageView);

        largeImageView.setDisplayListener(new DisplayListener() {
            @Override
            public void onCompleted(ImageFrom imageFrom, String mimeType) {
                imageAmplifier.update();
            }

            @Override
            public void onStarted() {

            }

            @Override
            public void onFailed(FailedCause failedCause) {
                imageAmplifier.update();
            }

            @Override
            public void onCanceled(CancelCause cancelCause) {

            }
        });
//        largeImageView.displayAssetImage("large_image.jpg");
        largeImageView.displayAssetImage("card.png");
    }
}
