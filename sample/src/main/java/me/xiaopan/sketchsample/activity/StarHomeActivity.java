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
import android.view.View;
import android.view.ViewGroup;

import me.xiaopan.androidinjector.InjectContentView;
import me.xiaopan.androidinjector.InjectExtra;
import me.xiaopan.androidinjector.InjectParentMember;
import me.xiaopan.androidinjector.InjectView;
import me.xiaopan.sketchsample.ImageOptions;
import me.xiaopan.sketchsample.MyBaseActivity;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.fragment.StarHomeFragment;
import me.xiaopan.sketchsample.util.DeviceUtils;
import me.xiaopan.sketchsample.widget.MyImageView;

/**
 * 明星个人主页
 */
@InjectParentMember
@InjectContentView(R.layout.activity_only_fragment)
public class StarHomeActivity extends MyBaseActivity implements ApplyBackgroundCallback {

    @InjectView(R.id.image_onlyFragment_background) MyImageView backgroundImageView;
    @InjectView(R.id.layout_onlyFragment_content) View contentView;

    @InjectExtra(StarHomeFragment.PARAM_REQUIRED_STRING_STAR_TITLE)
    private String starTitle;

    public static void launch(Activity activity, String starName) {
        Intent intent = new Intent(activity, StarHomeActivity.class);
        intent.putExtra(StarHomeFragment.PARAM_REQUIRED_STRING_STAR_TITLE, starName);
        intent.putExtra(StarHomeFragment.PARAM_REQUIRED_STRING_STAR_URL, "http://image.baidu.com/channel/star/" + starName);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.window_push_enter, R.anim.window_push_exit);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        StarHomeFragment starHomeFragment = new StarHomeFragment();
        starHomeFragment.setArguments(getIntent().getExtras());

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_onlyFragment_content, starHomeFragment)
                .commit();
    }

    @Override
    protected boolean isDisableSetFitsSystemWindows() {
        return true;
    }

    @Override
    protected void onPreSetSupportActionBar() {
        toolbar.setTitle(starTitle);
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
