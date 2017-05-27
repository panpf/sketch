package me.xiaopan.sketchsample.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import me.xiaopan.androidinjector.InjectContentView;
import me.xiaopan.androidinjector.InjectView;
import me.xiaopan.sketch.BuildConfig;
import me.xiaopan.sketchsample.MyFragment;
import me.xiaopan.sketchsample.R;

/**
 * 关于Fragment
 */
@InjectContentView(R.layout.fragment_about)
public class AboutFragment extends MyFragment {
    @InjectView(R.id.text_about_versions)
    TextView versionTextView;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        versionTextView.setText(getString(R.string.text_version, BuildConfig.VERSION_NAME));
    }
}
