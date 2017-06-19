package me.xiaopan.sketchsample.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import me.xiaopan.sketchsample.BuildConfig;
import me.xiaopan.sketchsample.BaseFragment;
import me.xiaopan.sketchsample.BindContentView;
import me.xiaopan.sketchsample.R;

/**
 * 关于Fragment
 */
@BindContentView(R.layout.fragment_about)
public class AboutFragment extends BaseFragment {
    @BindView(R.id.text_about_versions)
    TextView versionTextView;
    @BindView(R.id.text_about_types)
    TextView typesTextView;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        versionTextView.setText(getString(R.string.text_version, BuildConfig.VERSION_NAME));
        typesTextView.setText(getString(R.string.text_types, BuildConfig.BUILD_TYPE, BuildConfig.FLAVOR));
    }
}
