/*
 * Copyright (C) 2013 Peng fei Pan <sky@xiaopan.me>
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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import me.xiaoapn.android.spear.sample.R;
import me.xiaopan.android.spear.display.OriginalFadeInImageDisplayer;
import me.xiaopan.android.spear.display.ZoomInImageDisplayer;
import me.xiaopan.android.spear.display.ZoomOutImageDisplayer;
import me.xiaopan.android.spear.process.CircleImageProcessor;
import me.xiaopan.android.spear.process.ReflectionImageProcessor;
import me.xiaopan.android.spear.process.RoundedCornerImageProcessor;
import me.xiaopan.android.spear.request.DisplayOptions;
import me.xiaopan.android.spear.widget.SpearImageView;

public class NormalActivity extends ActionBarActivity {
    private String uri = "http://d.hiphotos.baidu.com/image/w%3D2048/sign=0d87feac087b02080cc938e156e1f3d3/bf096b63f6246b606fb2647de9f81a4c510fa27f.jpg";
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal);
        initImageSize();

        handler = new Handler();

        display(R.id.image_normal_11, createDisplayOptions(11), 100);
        display(R.id.image_normal_12, createDisplayOptions(12), 200);
        display(R.id.image_normal_13, createDisplayOptions(13), 300);

        display(R.id.image_normal_21, createDisplayOptions(21), 400);
        display(R.id.image_normal_22, createDisplayOptions(22), 500);

        display(R.id.image_normal_31, createDisplayOptions(31), 600);
    }

    private void initImageSize(){
        int width = getResources().getDisplayMetrics().widthPixels;
        setSize(findViewById(R.id.image_normal_11), (width-4)/3);
        setSize(findViewById(R.id.image_normal_12), (width-4)/3);
        setSize(findViewById(R.id.image_normal_13), (width-4)/3);

        setSize(findViewById(R.id.image_normal_21), (width-2)/2);
        setSize(findViewById(R.id.image_normal_22), (width-2)/2);

        setSize(findViewById(R.id.image_normal_31), width);
    }

    private void setSize(View view, int size){
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = size;
        layoutParams.height = size;
        view.setLayoutParams(layoutParams);
    }

    private void display(final int imageViewId, final DisplayOptions displayOptions, int delayed){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SpearImageView spearImageView = (SpearImageView) findViewById(imageViewId);
                spearImageView.setDisplayOptions(displayOptions);
                spearImageView.setImageByUri(uri);
            }
        }, delayed);
    }

    public DisplayOptions createDisplayOptions(int position){
        DisplayOptions displayOptions = new DisplayOptions(getBaseContext());
        displayOptions.disableMemoryCache();
        switch (position){
            case 11 :
                displayOptions.loadingDrawable(R.drawable.image_loading, true);
                displayOptions.loadFailDrawable(R.drawable.image_load_fail, true);
                displayOptions.displayer(new OriginalFadeInImageDisplayer());
                displayOptions.processor(new RoundedCornerImageProcessor());
                break;
            case 12 :
                displayOptions.loadingDrawable(R.drawable.image_loading, true);
                displayOptions.loadFailDrawable(R.drawable.image_load_fail, true);
                displayOptions.displayer(new OriginalFadeInImageDisplayer());
                displayOptions.processor(new RoundedCornerImageProcessor());
                break;
            case 13 :
                displayOptions.loadingDrawable(R.drawable.image_loading, true);
                displayOptions.loadFailDrawable(R.drawable.image_load_fail, true);
                displayOptions.displayer(new OriginalFadeInImageDisplayer());
                displayOptions.processor(new RoundedCornerImageProcessor());
                break;
            case 21 :
                displayOptions.loadingDrawable(R.drawable.image_loading, true);
                displayOptions.loadFailDrawable(R.drawable.image_load_fail, true);
                displayOptions.processor(new CircleImageProcessor());
                displayOptions.displayer(new ZoomInImageDisplayer());
                break;
            case 22 :
                displayOptions.loadingDrawable(R.drawable.image_loading, true);
                displayOptions.loadFailDrawable(R.drawable.image_load_fail, true);
                displayOptions.processor(new CircleImageProcessor());
                displayOptions.displayer(new ZoomInImageDisplayer());
                break;
            case 31 :
                displayOptions.loadFailDrawable(R.drawable.image_load_fail, true);
                displayOptions.displayer(new ZoomOutImageDisplayer());
                displayOptions.processor(new ReflectionImageProcessor());
                break;
        }
        return displayOptions;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_github, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_github :
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_github))));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
