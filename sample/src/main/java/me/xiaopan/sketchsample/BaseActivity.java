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

package me.xiaopan.sketchsample;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BindContentView bindContentView = getClass().getAnnotation(BindContentView.class);
        if (bindContentView != null && bindContentView.value() > 0) {
            setContentView(bindContentView.value());
        }

        if (toolbar != null) {
            onPreSetSupportActionBar();
            setSupportActionBar(toolbar);
            onPostSetSupportActionBar();
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        setTransparentStatusBar();
        super.setContentView(layoutResID);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        setTransparentStatusBar();
        super.setContentView(view, params);
    }

    @Override
    public void setContentView(View view) {
        setTransparentStatusBar();
        super.setContentView(view);
    }

    protected void onPreSetSupportActionBar() {

    }

    protected void onPostSetSupportActionBar() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();

        if (!isDisableSetFitsSystemWindows()) {
            setFitsSystemWindows();
        }

        ButterKnife.bind(this, this);
    }

    /**
     * 让状态栏完全透明
     */
    private void setTransparentStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void setFitsSystemWindows() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ViewGroup contentViewGroup = (ViewGroup) findViewById(android.R.id.content);
            if (contentViewGroup != null && contentViewGroup.getChildCount() > 0) {
                contentViewGroup.getChildAt(0).setFitsSystemWindows(true);
            }
        }
    }

    protected boolean isDisableSetFitsSystemWindows() {
        return false;
    }
}
