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

package me.xiaopan.android.imageloader.sample.activity;

import android.content.Intent;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuItem;
import me.xiaoapn.android.imageloader.R;
import me.xiaopan.android.imageloader.ImageLoader;
import me.xiaopan.android.imageloader.display.ZoomInBitmapDisplayer;
import me.xiaopan.android.imageloader.display.ZoomOutBitmapDisplayer;
import me.xiaopan.android.imageloader.process.CircleBitmapProcessor;
import me.xiaopan.android.imageloader.process.ReflectionBitmapProcessor;
import me.xiaopan.android.imageloader.process.RoundedCornerBitmapProcessor;
import me.xiaopan.android.imageloader.task.display.DisplayListener;
import me.xiaopan.android.imageloader.task.display.DisplayOptions;
import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

/**
 * Created by Xiaopan on 2014/3/23 0023.
 */
public class NormalActivity extends Activity{
    private String uri = "http://d.hiphotos.baidu.com/image/w%3D2048/sign=0d87feac087b02080cc938e156e1f3d3/bf096b63f6246b606fb2647de9f81a4c510fa27f.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal);
        initImageSize();


        display(R.id.image_normal_11, R.id.progress_normal_11, createDisplayOptions(11));
        display(R.id.image_normal_12, R.id.progress_normal_12, createDisplayOptions(12));
        display(R.id.image_normal_13, R.id.progress_normal_13, createDisplayOptions(13));

        display(R.id.image_normal_21, R.id.progress_normal_21, createDisplayOptions(21));
        display(R.id.image_normal_22, R.id.progress_normal_22, createDisplayOptions(22));

        display(R.id.image_normal_31, R.id.progress_normal_31, createDisplayOptions(31));
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

    private void display(int imageViewId, int processBarId, DisplayOptions displayOptions){
        final ProgressBar progressBar = (ProgressBar) findViewById(processBarId);
        ImageLoader.getInstance(getBaseContext()).display(uri, (ImageView) findViewById(imageViewId), displayOptions, new DisplayListener() {
            @Override
            public void onStart() {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure() {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onComplete(String imageUri, ImageView imageView, BitmapDrawable drawable) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onUpdateProgress(long totalLength, long completedLength) {
                progressBar.setProgress((int) (((float) completedLength / totalLength) * 100));
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancel() {
            }
        });
    }

    public DisplayOptions createDisplayOptions(int position){
        DisplayOptions displayOptions = new DisplayOptions(getBaseContext());
        displayOptions.setEnableMemoryCache(false);
        switch (position){
            case 11 :
                displayOptions.setDisplayingDrawableResId(R.drawable.image_displaying);
                displayOptions.setFailureDrawableResId(R.drawable.image_failure);
                displayOptions.setBitmapProcessor(new RoundedCornerBitmapProcessor());
                break;
            case 12 :
                displayOptions.setDisplayingDrawableResId(R.drawable.image_displaying);
                displayOptions.setFailureDrawableResId(R.drawable.image_failure);
                displayOptions.setBitmapProcessor(new RoundedCornerBitmapProcessor());
                break;
            case 13 :
                displayOptions.setDisplayingDrawableResId(R.drawable.image_displaying);
                displayOptions.setFailureDrawableResId(R.drawable.image_failure);
                displayOptions.setBitmapProcessor(new RoundedCornerBitmapProcessor());
                break;
            case 21 :
                displayOptions.setDisplayingDrawableResId(R.drawable.image_displaying);
                displayOptions.setFailureDrawableResId(R.drawable.image_failure);
                displayOptions.setBitmapProcessor(new CircleBitmapProcessor());
                displayOptions.setBitmapDisplayer(new ZoomInBitmapDisplayer());
                break;
            case 22 :
                displayOptions.setDisplayingDrawableResId(R.drawable.image_displaying);
                displayOptions.setFailureDrawableResId(R.drawable.image_failure);
                displayOptions.setBitmapProcessor(new CircleBitmapProcessor());
                displayOptions.setBitmapDisplayer(new ZoomOutBitmapDisplayer());
                break;
            case 31 :
                displayOptions.setFailureDrawableResId(R.drawable.image_failure);
                displayOptions.setBitmapProcessor(new ReflectionBitmapProcessor());
                break;
        }
        return displayOptions;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_all, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_all_github :
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_github))));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
