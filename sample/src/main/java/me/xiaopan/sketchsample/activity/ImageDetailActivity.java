/*
 * Copyright 2013 Peng fei Pan
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.xiaopan.sketchsample.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import butterknife.BindView;
import me.xiaopan.sketchsample.BaseActivity;
import me.xiaopan.sketchsample.ImageOptions;
import me.xiaopan.sketchsample.BindContentView;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.bean.Image;
import me.xiaopan.sketchsample.fragment.ImageDetailFragment;
import me.xiaopan.sketchsample.util.DeviceUtils;
import me.xiaopan.sketchsample.widget.SampleImageView;

@BindContentView(R.layout.activity_only_fragment)
public class ImageDetailActivity extends BaseActivity implements ApplyBackgroundCallback {

    @BindView(R.id.image_onlyFragment_background) SampleImageView backgroundImageView;
    @BindView(R.id.layout_onlyFragment_content) View contentView;

    public static void launch(Activity activity, ArrayList<Image> images, String loadingImageOptionsInfo, int defaultPosition) {
        Intent intent = new Intent(activity, ImageDetailActivity.class);
        intent.putParcelableArrayListExtra(ImageDetailFragment.PARAM_REQUIRED_STRING_ARRAY_LIST_URLS, images);
        intent.putExtra(ImageDetailFragment.PARAM_REQUIRED_STRING_LOADING_IMAGE_OPTIONS_KEY, loadingImageOptionsInfo);
        intent.putExtra(ImageDetailFragment.PARAM_OPTIONAL_INT_DEFAULT_POSITION, defaultPosition);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.window_push_enter, R.anim.window_push_exit);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            contentView.setPadding(contentView.getPaddingLeft(),
                    contentView.getPaddingTop() + DeviceUtils.getStatusBarHeight(getResources()),
                    contentView.getPaddingRight(), contentView.getPaddingBottom());
        }

        ViewGroup.LayoutParams layoutParams = backgroundImageView.getLayoutParams();
        layoutParams.width = getResources().getDisplayMetrics().widthPixels;
        layoutParams.height = getResources().getDisplayMetrics().heightPixels;
        backgroundImageView.setLayoutParams(layoutParams);

        backgroundImageView.setOptions(ImageOptions.WINDOW_BACKGROUND);

        toolbar.setVisibility(View.GONE);

        ImageDetailFragment imageDetailFragment = new ImageDetailFragment();
        imageDetailFragment.setArguments(getIntent().getExtras());

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_onlyFragment_content, imageDetailFragment)
                .commit();
    }

    @Override
    protected boolean isDisableSetFitsSystemWindows() {
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean result = true;
        try {
            result = super.dispatchTouchEvent(ev);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.window_pop_enter, R.anim.window_pop_exit);
    }

    @Override
    public void onApplyBackground(String imageUri) {
        backgroundImageView.displayImage(imageUri);
    }
}
