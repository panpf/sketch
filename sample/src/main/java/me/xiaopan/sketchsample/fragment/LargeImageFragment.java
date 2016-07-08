package me.xiaopan.sketchsample.fragment;

import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import me.xiaopan.androidinjector.InjectContentView;
import me.xiaopan.androidinjector.InjectView;
import me.xiaopan.sketch.request.CancelCause;
import me.xiaopan.sketch.request.DisplayListener;
import me.xiaopan.sketch.request.FailedCause;
import me.xiaopan.sketch.request.ImageFrom;
import me.xiaopan.sketchsample.MyFragment;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.scale.ImageAmplifier;
import me.xiaopan.sketchsample.widget.LargeImageView;
import me.xiaopan.sketchsample.widget.MappingView;

@InjectContentView(R.layout.fragment_large_image)
public class LargeImageFragment extends MyFragment {

    @InjectView(R.id.largeImage)
    private LargeImageView largeImageView;
    @InjectView(R.id.mapping)
    private MappingView mappingView;
    @InjectView(R.id.text_largeImageFragment_scale)
    private TextView scaleTextView;

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

        String imageName = "large_image.jpg";
//        String imageName = "long_wei_bo.jpg";
//        String imageName = "card.png";

        largeImageView.displayAssetImage(imageName);

        imageAmplifier.setOnMatrixChangeListener(new ImageAmplifier.OnMatrixChangedListener() {
            @Override
            public void onMatrixChanged(RectF displayRect) {
                Drawable drawable = largeImageView.getDrawable();
                if (drawable == null) {
                    return;
                }
                Log.w("test", "displayRect: " + displayRect.toString());

                RectF visibleRect = imageAmplifier.getVisibleRect();
                float scale= imageAmplifier.getScale();
                mappingView.update(drawable.getIntrinsicWidth(), visibleRect);
                scaleTextView.setText(String.valueOf(scale));
                largeImageView.update(imageAmplifier.getDrawMatrix(), visibleRect, imageAmplifier.getDrawableWidth(), imageAmplifier.getDrawableHeight());
            }
        });
        mappingView.getOptions().setMaxSize(300, 300);
        mappingView.displayAssetImage(imageName);
    }
}
