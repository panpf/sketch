package me.xiaopan.sketchsample.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import me.xiaopan.androidinjector.InjectContentView;
import me.xiaopan.androidinjector.InjectView;
import me.xiaopan.sketch.SketchImageView;
import me.xiaopan.sketchsample.MyFragment;
import me.xiaopan.sketchsample.R;

@InjectContentView(R.layout.fragment_image_orientation_test)
public class ImageOrientationTestFragment extends MyFragment {
    private static final String PARAM_REQUIRED_STRING_FILE_PATH = "PARAM_REQUIRED_STRING_FILE_PATH";

    @InjectView(R.id.image_imageOrientationTestFragment_before)
    SketchImageView beforeImageView;

    @InjectView(R.id.image_imageOrientationTestFragment_after)
    SketchImageView afterImageView;

    private String filePath;

    public static ImageOrientationTestFragment build(String filePath) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_REQUIRED_STRING_FILE_PATH, filePath);

        ImageOrientationTestFragment fragment = new ImageOrientationTestFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null) {
            filePath = arguments.getString(PARAM_REQUIRED_STRING_FILE_PATH);
        }

        if (TextUtils.isEmpty(filePath)) {
            throw new IllegalArgumentException("Not found filePath param");
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        beforeImageView.getOptions().setCorrectImageOrientationDisabled(true);
        beforeImageView.displayImage(filePath);

        afterImageView.displayImage(filePath);
    }
}
