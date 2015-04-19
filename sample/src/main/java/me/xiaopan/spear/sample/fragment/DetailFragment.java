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

package me.xiaopan.spear.sample.fragment;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import me.xiaopan.android.inject.InjectContentView;
import me.xiaopan.android.inject.InjectExtra;
import me.xiaopan.android.inject.InjectView;
import me.xiaopan.spear.Spear;
import me.xiaopan.spear.sample.MyFragment;
import me.xiaopan.spear.sample.R;
import me.xiaopan.spear.sample.adapter.ImageFragmentAdapter;
import me.xiaopan.spear.sample.util.AnimationBatchExecutor;
import me.xiaopan.spear.sample.util.AnimationUtils;
import me.xiaopan.spear.sample.util.ApplyWallpaperAsyncTask;
import me.xiaopan.spear.sample.util.PageNumberSetter;
import me.xiaopan.spear.sample.util.SaveImageAsyncTask;
import me.xiaopan.spear.sample.util.SingleTapDetector;
import me.xiaopan.spear.sample.util.ViewPagerPlayer;
import me.xiaopan.spear.sample.widget.DepthPageTransformer;

/**
 * 图片详情页面
 */
@InjectContentView(R.layout.fragment_detail)
public class DetailFragment extends MyFragment implements SingleTapDetector.OnSingleTapListener, View.OnClickListener{
    public static final String PARAM_REQUIRED_STRING_ARRAY_LIST_URLS = "PARAM_REQUIRED_STRING_ARRAY_LIST_URLS";
    public static final String PARAM_OPTIONAL_INT_DEFAULT_POSITION = "PARAM_OPTIONAL_INT_DEFAULT_POSITION";

    @InjectView(R.id.pager_detail_content) private ViewPager viewPager;
    @InjectView(R.id.button_detail_share) private View shareButton;
    @InjectView(R.id.button_detail_play) private View playButton;
    @InjectView(R.id.button_detail_applyWallpaper) private View applyWallpaperButton;
    @InjectView(R.id.button_detail_save) private View saveButton;
    @InjectView(R.id.text_detail_currentItem) private TextView currentItemTextView;
    @InjectView(R.id.text_detail_countItem) private TextView countTextView;
    @InjectView(R.id.layout_detail_toolbar) private View toolbarLayout;
    @InjectView(R.id.layout_detail_number) private View numberLayout;

    @InjectExtra(PARAM_REQUIRED_STRING_ARRAY_LIST_URLS) private List<String> uris;
    @InjectExtra(PARAM_OPTIONAL_INT_DEFAULT_POSITION) private int position;
    private boolean show = false;

    private Handler handler;
    private AnimationBatchExecutor animationBatchExecutor;
    private ViewPagerPlayer viewPagerPlayer;
    private boolean recoverPlay;
    private StartPlay startPlay;
    private SingleTapDetector singleTapDetector;
    private boolean disableSingleTap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        startPlay = new StartPlay();
        singleTapDetector = new SingleTapDetector(getActivity(), this);

        if(getActivity() instanceof SetDispatchTouchEventListener){
            ((SetDispatchTouchEventListener) getActivity()).setDispatchTouchEventListener(new DispatchTouchEventListener() {
                @Override
                public void dispatchTouchEvent(MotionEvent ev) {
                    singleTapDetector.onTouchEvent(ev);
                }
            });
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        numberLayout.setVisibility(View.GONE);
        shareButton.setVisibility(View.INVISIBLE);
        applyWallpaperButton.setVisibility(View.INVISIBLE);
        playButton.setVisibility(View.INVISIBLE);
        saveButton.setVisibility(View.INVISIBLE);
        toolbarLayout.setBackgroundDrawable(null);

        animationBatchExecutor = new AnimationBatchExecutor(getActivity(), R.anim.action_show, R.anim.action_hidden, 70, shareButton, applyWallpaperButton, playButton, saveButton);
        viewPagerPlayer = new ViewPagerPlayer(viewPager);
        new PageNumberSetter(currentItemTextView, viewPager);
        viewPager.setPageTransformer(false, new DepthPageTransformer());

        shareButton.setOnClickListener(this);
        applyWallpaperButton.setOnClickListener(this);
        playButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);

        if (uris != null) {
            viewPager.setAdapter(new ImageFragmentAdapter(getChildFragmentManager(), uris));
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

    /**
     * 切换工具栏和页码的显示状态
     */
    private void toggleToolbarVisibleState(){
        show =! show;
        animationBatchExecutor.start(show);
        if(show){
            toolbarLayout.setBackgroundResource(R.drawable.shape_linear_gradient_transparent_to_black);
            AnimationUtils.visibleViewByAlpha(numberLayout);
        }else{
            toolbarLayout.setBackgroundDrawable(null);
            AnimationUtils.goneViewByAlpha(numberLayout);
        }
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if(disableSingleTap){
            disableSingleTap = false;
            return true;
        }

        // 如果正在播放就关闭自动播放
        if(viewPagerPlayer.isPlaying()){
            viewPagerPlayer.stop();
        }

        toggleToolbarVisibleState();
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(getActivity() instanceof SetDispatchTouchEventListener){
            ((SetDispatchTouchEventListener) getActivity()).setDispatchTouchEventListener(null);
        }
    }

    private File getImageFile(String imageUrl, String type){
        String currentUrl = uris.get(viewPager.getCurrentItem());
        if(currentUrl == null || "".equals(currentUrl.trim())){
            Toast.makeText(getActivity(), type+"，当前图片的URL是空的，没法拿到图片", Toast.LENGTH_LONG).show();
            return null;
        }else if(currentUrl.startsWith("http://") || currentUrl.startsWith("https://")){
            File  file = Spear.with(getActivity()).getConfiguration().getDiskCache().getCacheFile(currentUrl);
            if(file == null || !file.exists()){
                Toast.makeText(getActivity(), "图片还没有下载好哦，再等一会儿吧！", Toast.LENGTH_LONG).show();
                return null;
            }else{
                return file;
            }
        }else if(currentUrl.startsWith("/")){
            return new File(currentUrl);
        }else{
            Toast.makeText(getActivity(), "我去，怎么会有这样的URL "+imageUrl, Toast.LENGTH_LONG).show();
            return null;
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.button_detail_share :
                disableSingleTap = true;
                File imageFile = getImageFile(uris.get(viewPager.getCurrentItem()), "分享");
                if(imageFile != null){
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(imageFile));
                    intent.setType("image/" + parseFileType(imageFile.getName()));
                    List<ResolveInfo> infoList = getActivity().getPackageManager().queryIntentActivities(intent, 0);
                    if (infoList != null && !infoList.isEmpty()) {
                        startActivity(intent);
                    } else {
                        Toast.makeText(getActivity(), "您的设备上没有能够分享的APP", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case R.id.button_detail_applyWallpaper :
                disableSingleTap = true;
                File imageFile2 = getImageFile(uris.get(viewPager.getCurrentItem()), "设置壁纸");
                if(imageFile2 != null){
                    new ApplyWallpaperAsyncTask(getActivity(), imageFile2){
                        @Override
                        protected void onPostExecute(Boolean aBoolean) {
                            if(getActivity() != null){
                                Toast.makeText(getActivity(), aBoolean?"设置壁纸成功":"设置壁纸失败", Toast.LENGTH_LONG).show();
                            }
                        }
                    }.execute(0);
                }
                break;
            case R.id.button_detail_play :
                disableSingleTap = true;
                viewPagerPlayer.start();
                toggleToolbarVisibleState();
                break;
            case R.id.button_detail_save :
                disableSingleTap = true;
                String currentUrl = uris.get(viewPager.getCurrentItem());
                if(currentUrl == null || "".equals(currentUrl.trim())){
                    Toast.makeText(getActivity(), "保存图片失败，因为当前图片的URL是空的，没法拿到图片", Toast.LENGTH_LONG).show();
                }else if(currentUrl.startsWith("http://") || currentUrl.startsWith("https://")){
                    File imageFile3 = Spear.with(getActivity()).getConfiguration().getDiskCache().getCacheFile(currentUrl);
                    if(imageFile3 == null || !imageFile3.exists()){
                        Toast.makeText(getActivity(), "图片还没有下载好哦，再等一会儿吧！", Toast.LENGTH_LONG).show();
                    }else{
                        new SaveImageAsyncTask(getActivity(), imageFile3).execute("");
                    }
                }else if(currentUrl.startsWith("/")){
                    Toast.makeText(getActivity(), "当前图片本就是本地的无需保存", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getActivity(), "我去，怎么会有这样的URL "+currentUrl, Toast.LENGTH_LONG).show();
                }
                break;
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
        void dispatchTouchEvent(MotionEvent ev);
    }

    public interface SetDispatchTouchEventListener{
        void setDispatchTouchEventListener(DispatchTouchEventListener dispatchTouchEventListener);
    }

    public static String parseFileType(String fileName){
        int lastIndexOf = fileName.lastIndexOf(".");
        if(lastIndexOf < 0){
            return null;
        }
        String fileType = fileName.substring(lastIndexOf+1);
        if("".equals(fileType.trim())){
            return null;
        }
        return fileType;
    }
}
