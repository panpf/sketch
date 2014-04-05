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

import java.io.File;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import me.xiaoapn.android.imageloader.R;
import me.xiaopan.android.imageloader.ImageLoader;
import me.xiaopan.android.imageloader.task.download.DownloadListener;
import me.xiaopan.android.imageloader.task.download.DownloadOptions;
import android.app.Activity;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ToggleButton;

public class DownloadActivity extends Activity {
    private EditText periodOfValidityEdit;
	private ImageView imageView;
	private ProgressBar progressBar;
    private ToggleButton diskCacheToggleButton;
    private DrawerLayout drawerLayout;

    private DownloadOptions downloadOptions;
    private String uri = "http://tupian.enterdesk.com/2013/xll/0112/taiqiumeinv/taiqiumeinv%20(3).jpg.680.510.jpg";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_download);
        periodOfValidityEdit = (EditText) findViewById(R.id.edit_download_periodOfValidity);
        diskCacheToggleButton = (ToggleButton) findViewById(R.id.toggle_download_diskCache);
		imageView = (ImageView) findViewById(R.id.image_download);
		progressBar = (ProgressBar) findViewById(R.id.progressBar_download);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_download);

        drawerLayout.setDrawerShadow(R.drawable.shape_drawer_shaow_down_left, GravityCompat.START);
        drawerLayout.setDrawerShadow(R.drawable.shape_drawer_shaow_down_right, GravityCompat.END);
        drawerLayout.openDrawer(Gravity.START);

        downloadOptions = new DownloadOptions();
        periodOfValidityEdit.setText("" + downloadOptions.getDiskCachePeriodOfValidity());

        diskCacheToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                downloadOptions.setEnableDiskCache(isChecked);
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
                if(text != null && !"".equals(text)){
                    downloadOptions.setDiskCachePeriodOfValidity(Long.valueOf(text));
                }else{
                    downloadOptions.setDiskCachePeriodOfValidity(0);
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
                ImageLoader.getInstance(getBaseContext()).download(uri, downloadOptions, new DownloadListener() {
                    @Override
                    public void onStart() {
                        progressBar.setVisibility(View.VISIBLE);
                        imageView.setImageBitmap(null);
                    }

                    @Override
                    public void onUpdateProgress(final long totalLength, final long completedLength) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setProgress((int) (((float) completedLength / totalLength) * 100));
                                progressBar.setVisibility(View.VISIBLE);
                            }
                        });
                    }

                    @Override
                    public void onComplete(final byte[] data) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageBitmap(BitmapFactory.decodeByteArray(data, 0, data.length));
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }

                    @Override
                    public void onComplete(final File cacheFile) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageURI(Uri.fromFile(cacheFile));
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }

                    @Override
                    public void onFailure() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getBaseContext(), "下载失败", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }

            @Override
            public void onDrawerStateChanged(int i) {

            }
        });
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
