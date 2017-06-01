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

package me.xiaopan.sketchsample.fragment;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import me.xiaopan.assemblyadapter.AssemblyFragmentStatePagerAdapter;
import me.xiaopan.sketch.util.SketchUtils;
import me.xiaopan.sketch.viewfun.zoom.ImageZoomer;
import me.xiaopan.sketchsample.BaseFragment;
import me.xiaopan.sketchsample.BindContentView;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.adapter.itemfactory.ImageFragmentItemFactory;
import me.xiaopan.sketchsample.bean.Image;
import me.xiaopan.sketchsample.util.PageNumberSetter;
import me.xiaopan.sketchsample.util.ViewPagerPlayer;
import me.xiaopan.sketchsample.widget.DepthPageTransformer;
import me.xiaopan.sketchsample.widget.ZoomOutPageTransformer;

@BindContentView(R.layout.fragment_detail)
public class ImageDetailFragment extends BaseFragment implements ImageZoomer.OnViewTapListener {
    public static final String PARAM_REQUIRED_STRING_ARRAY_LIST_URLS = "PARAM_REQUIRED_STRING_ARRAY_LIST_URLS";
    public static final String PARAM_REQUIRED_STRING_LOADING_IMAGE_OPTIONS_KEY = "PARAM_REQUIRED_STRING_LOADING_IMAGE_OPTIONS_KEY";
    public static final String PARAM_OPTIONAL_INT_DEFAULT_POSITION = "PARAM_OPTIONAL_INT_DEFAULT_POSITION";

    @BindView(R.id.pager_detail_content)
    ViewPager viewPager;

    @BindView(R.id.text_detail_currentItem)
    TextView currentItemTextView;

    @BindView(R.id.text_detail_countItem)
    TextView countTextView;

    private List<Image> imageList;
    private String loadingImageOptionsKey;
    private int position;

    private Handler handler;
    private ViewPagerPlayer viewPagerPlayer;
    private boolean recoverPlay;
    private StartPlay startPlay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        startPlay = new StartPlay();

        Bundle arguments = getArguments();
        if (arguments != null) {
            imageList = arguments.getParcelableArrayList(PARAM_REQUIRED_STRING_ARRAY_LIST_URLS);
            loadingImageOptionsKey = arguments.getString(PARAM_REQUIRED_STRING_LOADING_IMAGE_OPTIONS_KEY);
            position = arguments.getInt(PARAM_OPTIONAL_INT_DEFAULT_POSITION);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPagerPlayer = new ViewPagerPlayer(viewPager);
        new PageNumberSetter(currentItemTextView, viewPager);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                // 4.0上使用
                viewPager.setPageTransformer(true, new DepthPageTransformer());
            } else {
                viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
            }
        } else {
            viewPager.setPageMargin(SketchUtils.dp2px(getActivity(), 8));
        }

        if (imageList != null) {
            AssemblyFragmentStatePagerAdapter pagerAdapter = new AssemblyFragmentStatePagerAdapter(getChildFragmentManager(), imageList);
            pagerAdapter.addItemFactory(new ImageFragmentItemFactory(getActivity(), loadingImageOptionsKey));
            viewPager.setAdapter(pagerAdapter);
            viewPager.setCurrentItem(position);
            currentItemTextView.setText(position + 1 + "");
            countTextView.setText(String.valueOf(imageList.size()));
        }

        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (recoverPlay && !viewPagerPlayer.isPlaying()) {
            handler.postDelayed(startPlay, 1000);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (viewPagerPlayer.isPlaying()) {
            viewPagerPlayer.stop();
            recoverPlay = true;
        }
        handler.removeCallbacks(startPlay);
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Override
    public void onViewTap(View view, float x, float y) {
        // 如果正在播放就关闭自动播放
        if (viewPagerPlayer.isPlaying()) {
            viewPagerPlayer.stop();
        } else {
            getActivity().finish();
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onEvent(ImageFragment.PlayImageEvent event) {
        viewPagerPlayer.start();
    }

    private class StartPlay implements Runnable {
        @Override
        public void run() {
            viewPagerPlayer.start();
            recoverPlay = false;
        }
    }
}
