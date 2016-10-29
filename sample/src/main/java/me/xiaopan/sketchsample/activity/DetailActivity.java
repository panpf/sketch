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

import me.xiaopan.androidinjector.InjectContentView;
import me.xiaopan.androidinjector.InjectParentMember;
import me.xiaopan.androidinjector.InjectView;
import me.xiaopan.sketchsample.ImageOptions;
import me.xiaopan.sketchsample.MyBaseActivity;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.fragment.DetailFragment;
import me.xiaopan.sketchsample.util.DeviceUtils;
import me.xiaopan.sketchsample.widget.MyImageView;

/**
 * 大图页面
 */
@InjectParentMember
@InjectContentView(R.layout.activity_only_fragment)
public class DetailActivity extends MyBaseActivity implements ApplyBackgroundCallback {

    @InjectView(R.id.image_onlyFragment_background) MyImageView backgroundImageView;
    @InjectView(R.id.layout_onlyFragment_content) View contentView;

    public static void launch(Activity activity, ArrayList<String> imageUrlList, String loadingImageOptionsInfo, int defaultPosition) {
        Intent intent = new Intent(activity, DetailActivity.class);
        intent.putStringArrayListExtra(DetailFragment.PARAM_REQUIRED_STRING_ARRAY_LIST_URLS, imageUrlList);
        intent.putExtra(DetailFragment.PARAM_REQUIRED_STRING_LOADING_IMAGE_OPTIONS_INFO, loadingImageOptionsInfo);
        intent.putExtra(DetailFragment.PARAM_OPTIONAL_INT_DEFAULT_POSITION, defaultPosition);
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

        backgroundImageView.setOptionsByName(ImageOptions.WINDOW_BACKGROUND);
        backgroundImageView.setAutoApplyGlobalAttr(false);

        toolbar.setVisibility(View.GONE);

        DetailFragment detailFragment = new DetailFragment();
        detailFragment.setArguments(getIntent().getExtras());

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_onlyFragment_content, detailFragment)
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
