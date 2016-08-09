package me.xiaopan.sketchsample.fragment;

import android.app.Activity;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import me.xiaopan.androidinjector.InjectContentView;
import me.xiaopan.androidinjector.InjectExtra;
import me.xiaopan.androidinjector.InjectView;
import me.xiaopan.sketch.feature.zoom.ImageZoomer;
import me.xiaopan.sketchsample.MyFragment;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.activity.WindowBackgroundManager;
import me.xiaopan.sketchsample.widget.MappingView;
import me.xiaopan.sketchsample.widget.MyImageView;

@InjectContentView(R.layout.fragment_large_image)
public class LargeImageFragment extends MyFragment {

    @InjectView(R.id.largeImage)
    private MyImageView imageView;

    @InjectView(R.id.mapping)
    private MappingView mappingView;

    @InjectView(R.id.text_largeImageFragment_scale)
    private TextView scaleTextView;

    @InjectExtra("imageUri")
    private String imageUri;

    private WindowBackgroundManager.WindowBackgroundLoader windowBackgroundLoader;

    public static LargeImageFragment build(String imageUri) {
        Bundle bundle = new Bundle();
        bundle.putString("imageUri", imageUri);
        LargeImageFragment fragment = new LargeImageFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity != null && activity instanceof WindowBackgroundManager.OnSetWindowBackgroundListener) {
            windowBackgroundLoader = new WindowBackgroundManager.WindowBackgroundLoader(activity.getBaseContext(), (WindowBackgroundManager.OnSetWindowBackgroundListener) activity);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageView.setEnableZoomFunction(true);
        imageView.setEnableSuperLargeImageFunction(true);

        imageView.displayImage(imageUri);

        final ImageZoomer imageZoomer = imageView.getImageZoomFunction().getImageZoomer();
        imageZoomer.addOnMatrixChangeListener(new ImageZoomer.OnMatrixChangedListener() {
            @Override
            public void onMatrixChanged(RectF displayRect) {
                mappingView.update(imageZoomer.getDrawableWidth(), imageZoomer.getVisibleRect());
                scaleTextView.setText(String.valueOf(imageZoomer.getScale()));
            }
        });
        mappingView.getOptions().setMaxSize(300, 300);
        mappingView.displayImage(imageUri);

        if (windowBackgroundLoader != null) {
            windowBackgroundLoader.load(imageUri);
        }
    }

    @Override
    public void onDetach() {
        if (windowBackgroundLoader != null) {
            windowBackgroundLoader.detach();
        }
        super.onDetach();
    }

    @Override
    protected void onUserVisibleChanged(boolean isVisibleToUser) {
        if (windowBackgroundLoader != null) {
            windowBackgroundLoader.setUserVisible(isVisibleToUser);
        }
    }
}
