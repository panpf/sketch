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

package me.xiaopan.android.spear.sample.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import me.xiaoapn.android.spear.sample.R;
import me.xiaopan.android.inject.InjectContentView;
import me.xiaopan.android.inject.InjectExtra;
import me.xiaopan.android.inject.InjectView;
import me.xiaopan.android.inject.app.InjectFragment;
import me.xiaopan.android.spear.Spear;
import me.xiaopan.android.spear.request.DisplayListener;
import me.xiaopan.android.spear.request.LoadListener;
import me.xiaopan.android.spear.sample.DisplayOptionsType;
import me.xiaopan.android.spear.sample.util.AnimationBatchExecutor;
import me.xiaopan.android.spear.sample.util.ViewPagerPlayer;
import me.xiaopan.android.spear.util.FailureCause;
import me.xiaopan.android.spear.widget.SpearImageView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * 图片详情页面
 */
@InjectContentView(R.layout.fragment_imge_detail)
public class ImageDetailFragment extends InjectFragment{
    public static final String PARAM_REQUIRED_STRING_ARRAY_LIST_URLS = "PARAM_REQUIRED_STRING_ARRAY_LIST_URLS";
    public static final String PARAM_OPTIONAL_INT_DEFAULT_POSITION = "PARAM_OPTIONAL_INT_DEFAULT_POSITION";

    @InjectView(R.id.pager_imageDetail_content) private ViewPager viewPager;
    @InjectView(R.id.button_imageDetail_share) private View shareButton;
    @InjectView(R.id.button_imageDetail_play) private View playButton;
    @InjectView(R.id.button_imageDetail_applyWallpaper) private View applyWallpaperButton;
    @InjectView(R.id.button_imageDetail_save) private View saveButton;
    @InjectView(R.id.text_imageDetail_currentItem) private TextView currentItemTextView;
    @InjectView(R.id.text_imageDetail_countItem) private TextView countTextView;
    
    @InjectExtra(PARAM_REQUIRED_STRING_ARRAY_LIST_URLS) private List<String> uris;
    @InjectExtra(PARAM_OPTIONAL_INT_DEFAULT_POSITION) private int position;

    private Handler handler;
    private AnimationBatchExecutor animationBatchExecutor;
    private ViewPagerPlayer viewPagerPlayer;
    private boolean recoverPlay;
    private StartPlay startPlay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        startPlay = new StartPlay();
        if(getActivity() instanceof SetDispatchTouchEventListener){
            ((SetDispatchTouchEventListener) getActivity()).setDispatchTouchEventListener(new DispatchTouchEventListener() {
                @Override
                public void dispatchTouchEvent(MotionEvent ev) {
                    // 不管发生任何时间都关闭自动播放
                    if(viewPagerPlayer.isPlaying()){
                        viewPagerPlayer.stop();
                        animationBatchExecutor.start(true);
                    }
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(getActivity() instanceof SetDispatchTouchEventListener){
            ((SetDispatchTouchEventListener) getActivity()).setDispatchTouchEventListener(null);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 初始化动画批量执行器
        animationBatchExecutor = new AnimationBatchExecutor(getActivity(), R.anim.action_show, R.anim.action_hidden, 50, shareButton, applyWallpaperButton, playButton, saveButton);

        viewPagerPlayer = new ViewPagerPlayer(viewPager);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                currentItemTextView.setText((i + 1) + "");
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                final String fileName = Uri.parse(photos[contentViewPager.getCurrentItem()]).getAuthority();
//
//                new SaveTempImageAsyncTask(getBaseContext()){
//                    @Override
//                    protected void onPostExecute(File file) {
//                        if(file == null){
//                            Toast.makeText(getBaseContext(), "保存图片失败，无法分享", Toast.LENGTH_LONG).show();
//                        }
//
//                        Intent intent = new Intent(Intent.ACTION_SEND);
//                        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
//                        intent.setType("image/" + SaveTempImageAsyncTask.parseFileType(fileName));
//                        List<ResolveInfo> infoList = getPackageManager().queryIntentActivities(intent, 0);
//                        if (infoList != null && !infoList.isEmpty()) {
//                            startActivity(intent);
//                        } else {
//                            Toast.makeText(getBaseContext(), R.string.toast_notFoundShareApp, Toast.LENGTH_LONG).show();
//                        }
//                    }
//                }.execute(fileName);
            }
        });

        applyWallpaperButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                final String fileName = Uri.parse(photos[contentViewPager.getCurrentItem()]).getAuthority();
//                new ApplyWallpaperAsyncTask(getBaseContext(), fileName){
//                    @Override
//                    protected void onPostExecute(Boolean aBoolean) {
//                        Toast.makeText(getBaseContext(), aBoolean?"设置壁纸成功":"设置壁纸失败", Toast.LENGTH_LONG).show();
//                    }
//                }.execute(0);
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPagerPlayer.start();
                animationBatchExecutor.start(false);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new SaveImageAsyncTask(getBaseContext()).execute(Uri.parse(photos[contentViewPager.getCurrentItem()]).getAuthority());
            }
        });

        if (uris != null) {
            viewPager.setAdapter(new ImageFragmentAdapter(getChildFragmentManager(), uris));
//            viewPager.setAdapter(new ImageAdapter(getActivity(), uris));
            viewPager.setCurrentItem(position);
            currentItemTextView.setText(position + 1 + "");
            countTextView.setText(uris.size() + "");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(recoverPlay && !viewPagerPlayer.isPlaying()){
            handler.postDelayed(startPlay, 1000);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(viewPagerPlayer.isPlaying()){
            viewPagerPlayer.stop();
            recoverPlay = true;
        }
        handler.removeCallbacks(startPlay);
    }

    private void setWallpaper(String imageUri) {
        Spear.with(getActivity()).load(imageUri, new LoadListener() {
            @Override
            public void onStarted() {

            }

            @Override
            public void onCompleted(final Bitmap bitmap, From from) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            getActivity().setWallpaper(bitmap);
                            Toast.makeText(getActivity(), "Apply Success", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Apply Failured", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onFailed(FailureCause failureCause) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Apply Failured", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCanceled() {

            }
        }).fire();
    }

    private static class ImageFragmentAdapter extends FragmentStatePagerAdapter {
        private List<String> uris;

        public ImageFragmentAdapter(FragmentManager fm, List<String> uris) {
            super(fm);
            this.uris = uris;
        }

        @Override
        public int getCount() {
            return uris.size();
        }

        @Override
        public Fragment getItem(int arg0) {
            ImageFragment imageFragment = new ImageFragment();
            Bundle bundle = new Bundle();
            bundle.putString(ImageFragment.PARAM_REQUIRED_IMAGE_URI, uris.get(arg0));
            imageFragment.setArguments(bundle);
            return imageFragment;
        }
    }

    private static class ImageAdapter extends PagerAdapter{
        private Context context;
        private List<String> uris;

        public ImageAdapter(Context context, List<String> uris) {
            this.context = context;
            this.uris = uris;
        }

        @Override
        public int getCount() {
            return uris.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            SpearImageView spearImageView = new SpearImageView(context);
            spearImageView.setImageByUri(uris.get(position));
            container.addView(spearImageView);
            return spearImageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    @InjectContentView(R.layout.fragment_image)
    public static class ImageFragment extends InjectFragment {
        public static final String PARAM_REQUIRED_IMAGE_URI = "PARAM_REQUIRED_IMAGE_URI";

        @InjectView(R.id.image_imageFragment_image) private SpearImageView imageView;
        @InjectView(R.id.progress_imageFragment_progress) private ProgressBar progressBar;

        @InjectExtra(PARAM_REQUIRED_IMAGE_URI) private String imageUri;

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            imageView.setDisplayOptions(DisplayOptionsType.IMAGE_DETAIL_ITEM);
            imageView.setDisplayListener(new DisplayListener() {
                @Override
                public void onStarted() {
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onCompleted(String uri, ImageView imageView, BitmapDrawable drawable, From from) {
                    progressBar.setVisibility(View.GONE);
                    new PhotoViewAttacher(imageView);
                }

                @Override
                public void onFailed(FailureCause failureCause) {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onCanceled() {

                }
            });
            imageView.setImageByUri(imageUri);
        }

        @Override
        public void onDetach() {
            if(imageView.getRequestFuture() != null && !imageView.getRequestFuture().isFinished()){
                imageView.getRequestFuture().cancel();
            }

            super.onDetach();
        }
    }

    private class StartPlay implements Runnable{

        @Override
        public void run() {
            viewPagerPlayer.start();
            recoverPlay = false;
        }
    }

    public interface DispatchTouchEventListener{
        public void dispatchTouchEvent(MotionEvent ev);
    }

    public interface SetDispatchTouchEventListener{
        public void setDispatchTouchEventListener(DispatchTouchEventListener dispatchTouchEventListener);
    }
}
