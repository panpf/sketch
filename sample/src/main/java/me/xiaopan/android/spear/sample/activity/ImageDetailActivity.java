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

package me.xiaopan.android.spear.sample.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import me.xiaoapn.android.spear.sample.R;
import me.xiaopan.android.inject.InjectContentView;
import me.xiaopan.android.inject.InjectParentMember;
import me.xiaopan.android.spear.sample.MyActionBarActivity;
import me.xiaopan.android.spear.sample.fragment.ImageDetailFragment;

/**
 * 大图页面
 */
@InjectParentMember
@InjectContentView(R.layout.activity_only_fragment)
public class ImageDetailActivity extends MyActionBarActivity implements ImageDetailFragment.SetDispatchTouchEventListener {
    private ImageDetailFragment.DispatchTouchEventListener dispatchTouchEventListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        ImageDetailFragment imageDetailFragment = new ImageDetailFragment();
        imageDetailFragment.setArguments(getIntent().getExtras());

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_onlyFragment_content, imageDetailFragment)
                .commit();
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        toolbar.setVisibility(View.GONE);
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
    public void setDispatchTouchEventListener(ImageDetailFragment.DispatchTouchEventListener dispatchTouchEventListener) {
        this.dispatchTouchEventListener = dispatchTouchEventListener;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.window_pop_enter, R.anim.window_pop_exit);
    }

    public static void launch(Activity activity, ArrayList<String> imageUrlList, int defaultPosition){
        Intent intent = new Intent(activity, ImageDetailActivity.class);
        intent.putStringArrayListExtra(ImageDetailFragment.PARAM_REQUIRED_STRING_ARRAY_LIST_URLS, imageUrlList);
        intent.putExtra(ImageDetailFragment.PARAM_OPTIONAL_INT_DEFAULT_POSITION, defaultPosition);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.window_push_enter, R.anim.window_push_exit);
    }
}
