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

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.lang.reflect.Field;

import me.xiaoapn.android.spear.sample.R;
import me.xiaopan.android.spear.Spear;
import me.xiaopan.android.spear.request.DownloadListener;
import me.xiaopan.android.spear.request.DownloadOptions;
import me.xiaopan.android.spear.request.ProgressListener;
import me.xiaopan.android.spear.sample.widget.ProgressPieView;
import me.xiaopan.android.spear.util.FailureCause;

public class DownloadActivity extends ActionBarActivity {
    private EditText periodOfValidityEdit;
	private ImageView imageView;
	private ProgressPieView progressBar;

    private DownloadOptions downloadOptions;
    private String uri = "http://tupian.enterdesk.com/2013/xll/0112/taiqiumeinv/taiqiumeinv%20(3).jpg.680.510.jpg";

	@SuppressLint("InlinedApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_download);
        periodOfValidityEdit = (EditText) findViewById(R.id.edit_download_periodOfValidity);
        ToggleButton diskCacheToggleButton = (ToggleButton) findViewById(R.id.toggle_download_diskCache);
		imageView = (ImageView) findViewById(R.id.image_download);
		progressBar = (ProgressPieView) findViewById(R.id.progressBar_download);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_download);

        drawerLayout.setDrawerShadow(R.drawable.shape_drawer_shaow_down_left, GravityCompat.START);
        drawerLayout.setDrawerShadow(R.drawable.shape_drawer_shaow_down_right, GravityCompat.END);
        drawerLayout.openDrawer(Gravity.START);

        downloadOptions = new DownloadOptions();
        periodOfValidityEdit.setText("" + downloadOptions.getDiskCacheTimeout());

        diskCacheToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    Field field = DownloadOptions.class.getDeclaredField("enableDiskCache");
                    field.set(downloadOptions, isChecked);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                periodOfValidityEdit.setEnabled(isChecked);
            }
        });
        diskCacheToggleButton.setChecked(downloadOptions.isEnableDiskCache());

        periodOfValidityEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = periodOfValidityEdit.getEditableText().toString().trim();
                if(!"".equals(text)){
                    downloadOptions.diskCacheTimeout(Long.valueOf(text));
                }else{
                    downloadOptions.diskCacheTimeout(0);
                }
            }
        });

        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View view, float v) {

            }

            @Override
            public void onDrawerOpened(View view) {

            }

            @Override
            public void onDrawerClosed(View view) {
                Spear.with(getBaseContext()).download(uri, new DownloadListener() {
                    @Override
                    public void onStarted() {
                        progressBar.setProgress(0);
                        progressBar.setVisibility(View.VISIBLE);
                        imageView.setImageBitmap(null);
                    }

                    @Override
                    public void onCompleted(final byte[] data, From from) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageBitmap(BitmapFactory.decodeByteArray(data, 0, data.length));
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }

                    @Override
                    public void onCompleted(final File cacheFile, From from) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageURI(Uri.fromFile(cacheFile));
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }

                    @Override
                    public void onFailed(FailureCause failureCause) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getBaseContext(), "下载失败", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }

                    @Override
                    public void onCanceled() {

                    }
                }).options(downloadOptions).progressListener(new ProgressListener() {
                    @Override
                    public void onUpdateProgress(final int totalLength, final int completedLength) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setProgress((int) (((float) completedLength / totalLength) * 100));
                            }
                        });
                    }
                }).fire();
            }

            @Override
            public void onDrawerStateChanged(int i) {

            }
        });
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
