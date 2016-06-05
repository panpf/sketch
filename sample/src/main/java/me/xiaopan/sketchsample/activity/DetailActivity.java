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
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import me.xiaopan.androidinjector.InjectContentView;
import me.xiaopan.androidinjector.InjectParentMember;
import me.xiaopan.sketchsample.MyBaseActivity;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.fragment.DetailFragment;

/**
 * 大图页面
 */
@InjectParentMember
@InjectContentView(R.layout.activity_only_fragment)
public class DetailActivity extends MyBaseActivity implements DetailFragment.SetDispatchTouchEventListener, WindowBackgroundManager.OnSetWindowBackgroundListener {
    private DetailFragment.DispatchTouchEventListener dispatchTouchEventListener;

    private WindowBackgroundManager windowBackgroundManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        windowBackgroundManager = new WindowBackgroundManager(this);

        toolbar.setVisibility(View.GONE);

        DetailFragment detailFragment = new DetailFragment();
        detailFragment.setArguments(getIntent().getExtras());

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_onlyFragment_content, detailFragment)
                .commit();
	}

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean result = true;
        try{
            result = super.dispatchTouchEvent(ev);
        }catch(RuntimeException e){
            e.printStackTrace();
        }

        if(dispatchTouchEventListener != null){
            dispatchTouchEventListener.dispatchTouchEvent(ev);
        }

        return result;
    }

    @Override
    public void setDispatchTouchEventListener(DetailFragment.DispatchTouchEventListener dispatchTouchEventListener) {
        this.dispatchTouchEventListener = dispatchTouchEventListener;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.window_pop_enter, R.anim.window_pop_exit);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        windowBackgroundManager.destroy();
    }

    public static void launch(Activity activity, ArrayList<String> imageUrlList, int defaultPosition){
        Intent intent = new Intent(activity, DetailActivity.class);
        intent.putStringArrayListExtra(DetailFragment.PARAM_REQUIRED_STRING_ARRAY_LIST_URLS, imageUrlList);
        intent.putExtra(DetailFragment.PARAM_OPTIONAL_INT_DEFAULT_POSITION, defaultPosition);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.window_push_enter, R.anim.window_push_exit);
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
