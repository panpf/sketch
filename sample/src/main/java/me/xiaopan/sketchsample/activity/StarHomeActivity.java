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
import android.graphics.Bitmap;
import android.os.Bundle;

import me.xiaopan.androidinjector.InjectContentView;
import me.xiaopan.androidinjector.InjectExtra;
import me.xiaopan.androidinjector.InjectParentMember;
import me.xiaopan.sketchsample.MyBaseActivity;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.fragment.StarHomeFragment;

/**
 * 明星个人主页
 */
@InjectParentMember
@InjectContentView(R.layout.activity_only_fragment)
public class StarHomeActivity extends MyBaseActivity implements WindowBackgroundManager.OnSetListener {
    @InjectExtra(StarHomeFragment.PARAM_REQUIRED_STRING_STAR_TITLE)
    private String starTitle;

    private WindowBackgroundManager windowBackgroundManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        windowBackgroundManager = new WindowBackgroundManager(this);

        StarHomeFragment starHomeFragment = new StarHomeFragment();
        starHomeFragment.setArguments(getIntent().getExtras());

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_onlyFragment_content, starHomeFragment)
                .commit();
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

    public static void launch(Activity activity, String starName) {
        Intent intent = new Intent(activity, StarHomeActivity.class);
        intent.putExtra(StarHomeFragment.PARAM_REQUIRED_STRING_STAR_TITLE, starName);
        intent.putExtra(StarHomeFragment.PARAM_REQUIRED_STRING_STAR_URL, "http://image.baidu.com/channel/star/" + starName);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.window_push_enter, R.anim.window_push_exit);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        windowBackgroundManager.destroy();
    }

    @Override
    public void onSetWindowBackground(String uri, Bitmap bitmap) {
        windowBackgroundManager.setBackground(uri, bitmap);
    }

    @Override
    public String getCurrentBackgroundUri() {
        return windowBackgroundManager.getCurrentBackgroundUri();
    }
}
