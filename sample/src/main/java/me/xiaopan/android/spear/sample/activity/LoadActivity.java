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
import android.graphics.Bitmap;
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
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.lang.reflect.Field;

import me.xiaoapn.android.spear.sample.R;
import me.xiaopan.android.spear.Spear;
import me.xiaopan.android.spear.process.CircleImageProcessor;
import me.xiaopan.android.spear.process.ReflectionImageProcessor;
import me.xiaopan.android.spear.process.RoundedCornerImageProcessor;
import me.xiaopan.android.spear.request.DownloadOptions;
import me.xiaopan.android.spear.request.LoadListener;
import me.xiaopan.android.spear.request.LoadOptions;
import me.xiaopan.android.spear.util.FailureCause;

public class LoadActivity extends ActionBarActivity {
    private EditText periodOfValidityEdit;
    private ImageView imageView;
    private DrawerLayout drawerLayout;
    private EditText maxWidthEditText;
    private EditText maxHeightEditText;
    private boolean reload;
    private View imageTypeWidth;
    private View imageTypeHeight;

    private LoadOptions loadOptions;
    private String uri = "http://b.zol-img.com.cn/desk/bizhi/image/4/1366x768/1387347718813.jpg";

    @SuppressLint("InlinedApi")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        periodOfValidityEdit = (EditText) findViewById(R.id.edit_load_periodOfValidity);
        maxWidthEditText = (EditText) findViewById(R.id.edit_load_maxWidth);
        maxHeightEditText = (EditText) findViewById(R.id.edit_load_maxHeight);
        ToggleButton diskCacheToggleButton = (ToggleButton) findViewById(R.id.toggle_load_diskCache);
        imageView = (ImageView) findViewById(R.id.image_load);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_load);
        Spinner processorSpinner = (Spinner) findViewById(R.id.spinner_load_processor);
        Spinner scaleTypeSpinner = (Spinner) findViewById(R.id.spinner_load_scaleType);
        imageTypeWidth = findViewById(R.id.image_load_imageTypeWidth);
        imageTypeHeight = findViewById(R.id.image_load_imageTypeHeight);

        drawerLayout.setDrawerShadow(R.drawable.shape_drawer_shaow_down_left, GravityCompat.START);
        drawerLayout.setDrawerShadow(R.drawable.shape_drawer_shaow_down_right, GravityCompat.END);
        drawerLayout.openDrawer(Gravity.START);

        loadOptions = new LoadOptions(getBaseContext());
        periodOfValidityEdit.setText("" + loadOptions.getDiskCacheTimeout());

        diskCacheToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    Field field = DownloadOptions.class.getDeclaredField("enableDiskCache");
                    field.set(loadOptions, isChecked);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                periodOfValidityEdit.setEnabled(isChecked);
                reload = true;
            }
        });
        diskCacheToggleButton.setChecked(loadOptions.isEnableDiskCache());

        processorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        loadOptions.processor(null);
                        break;
                    case 1:
                        loadOptions.processor(new ReflectionImageProcessor());
                        break;
                    case 2:
                        loadOptions.processor(new RoundedCornerImageProcessor());
                        break;
                    case 3:
                        loadOptions.processor(new CircleImageProcessor());
                        break;
                }
                reload = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        scaleTypeSpinner.setSelection(3);
        scaleTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        loadOptions.scaleType(ImageView.ScaleType.MATRIX);
                        break;
                    case 1:
                        loadOptions.scaleType(ImageView.ScaleType.FIT_XY);
                        break;
                    case 2:
                        loadOptions.scaleType(ImageView.ScaleType.FIT_START);
                        break;
                    case 3:
                        loadOptions.scaleType(ImageView.ScaleType.FIT_CENTER);
                        break;
                    case 4:
                        loadOptions.scaleType(ImageView.ScaleType.FIT_END);
                        break;
                    case 5:
                        loadOptions.scaleType(ImageView.ScaleType.CENTER);
                        break;
                    case 6:
                        loadOptions.scaleType(ImageView.ScaleType.CENTER_CROP);
                        break;
                    case 7:
                        loadOptions.scaleType(ImageView.ScaleType.CENTER_INSIDE);
                        break;
                }
                reload = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        periodOfValidityEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                reload = true;
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        maxWidthEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                reload = true;
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        maxHeightEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                reload = true;
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        imageTypeWidth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uri = "http://b.zol-img.com.cn/desk/bizhi/image/4/1366x768/1387347718813.jpg";
                imageTypeWidth.setSelected(true);
                imageTypeHeight.setSelected(false);
                reload = true;
                drawerLayout.closeDrawers();
            }
        });
        imageTypeWidth.setSelected(true);

        imageTypeHeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uri = "http://a.hiphotos.baidu.com/image/w%3D2048/sign=21b75619df54564ee565e33987e69d82/738b4710b912c8fcab2d9cc7fe039245d78821d9.jpg";
                imageTypeWidth.setSelected(false);
                imageTypeHeight.setSelected(true);
                reload = true;
                drawerLayout.closeDrawers();
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
                if(reload){
                    String maxWidthString = maxWidthEditText.getEditableText().toString().trim();
                    String maxHeightString = maxHeightEditText.getEditableText().toString().trim();
                    if (!"".equals(maxWidthString) && !"".equals(maxHeightString)) {
                        loadOptions.maxsize(Integer.valueOf(maxWidthString), Integer.valueOf(maxHeightString));
                    } else {
                        loadOptions.maxsize(null);
                    }
                    loadOptions.resize(loadOptions.getMaxsize());

                    String text = periodOfValidityEdit.getEditableText().toString().trim();
                    if (!"".equals(text)) {
                        loadOptions.diskCacheTimeout(Long.valueOf(text));
                    } else {
                        loadOptions.diskCacheTimeout(0);
                    }

                    Spear.with(getBaseContext()).load(uri, new LoadListener() {
                        @Override
                        public void onStarted() {
                            imageView.setImageBitmap(null);
                        }

                        @Override
                        public void onCompleted(final Bitmap bitmap, From from) {
                            reload = false;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imageView.setImageBitmap(bitmap);
                                }
                            });
                        }

                        @Override
                        public void onFailed(FailureCause failureCause) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getBaseContext(), "加载失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onCanceled() {

                        }
                    }).options(loadOptions).fire();
                }
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