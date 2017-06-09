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

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import me.xiaopan.assemblyadapter.AssemblyRecyclerAdapter;
import me.xiaopan.sketch.util.SketchUtils;
import me.xiaopan.sketchsample.AssetImage;
import me.xiaopan.sketchsample.BaseFragment;
import me.xiaopan.sketchsample.BindContentView;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.activity.ApplyBackgroundCallback;
import me.xiaopan.sketchsample.activity.ImageDetailActivity;
import me.xiaopan.sketchsample.adapter.itemfactory.MyPhotoItemFactory;
import me.xiaopan.sketchsample.bean.Image;
import me.xiaopan.sketchsample.util.ImageOrientationCorrectTestFileGenerator;
import me.xiaopan.sketchsample.util.ScrollingPauseLoadManager;
import me.xiaopan.sketchsample.widget.HintView;

/**
 * 本地相册页面
 */
@BindContentView(R.layout.fragment_recycler)
public class MyPhotosFragment extends BaseFragment implements MyPhotoItemFactory.OnImageClickListener, SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.refresh_recyclerFragment)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.recycler_recyclerFragment_content)
    RecyclerView recyclerView;

    @BindView(R.id.hint_recyclerFragment)
    HintView hintView;

    private AssemblyRecyclerAdapter adapter;

    private ApplyBackgroundCallback applyBackgroundCallback;
    private String backgroundImageUri;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof ApplyBackgroundCallback) {
            applyBackgroundCallback = (ApplyBackgroundCallback) activity;
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        refreshLayout.setOnRefreshListener(this);
        recyclerView.addOnScrollListener(new ScrollingPauseLoadManager(view.getContext()));

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        int padding = SketchUtils.dp2px(getActivity(), 2);
        recyclerView.setPadding(padding, padding, padding, padding);
        recyclerView.setClipToPadding(false);

        if (adapter != null) {
            recyclerView.setAdapter(adapter);
            recyclerView.scheduleLayoutAnimation();
        } else {
            refreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.setRefreshing(true);
                    onRefresh();
                }
            });
        }
    }

    @Override
    public void onClickImage(int position, String optionsKey) {
        // 含有这些信息时，说明这张图片不仅仅是缩小，而是会被改变，因此不能用作loading图了
        if (optionsKey.contains("Resize")
                || optionsKey.contains("ImageProcessor")
                || optionsKey.contains("thumbnailMode")) {
            optionsKey = null;
        }

        //noinspection unchecked
        List<String> urlList = adapter.getDataList();
        ArrayList<Image> imageArrayList = new ArrayList<>(urlList.size());
        for (String url : urlList) {
            imageArrayList.add(new Image(url, url));
        }

        ImageDetailActivity.launch(getActivity(), imageArrayList, optionsKey, position);
    }

    @Override
    public void onRefresh() {
        if (getActivity() != null) {
            new LoadPhotoListTask(getActivity().getBaseContext()).execute();
        }
    }

    @Override
    protected void onUserVisibleChanged(boolean isVisibleToUser) {
        if (applyBackgroundCallback != null && isVisibleToUser) {
            changeBackground(backgroundImageUri);
        }
    }

    private void changeBackground(String imageUri) {
        this.backgroundImageUri = imageUri;
        if (applyBackgroundCallback != null) {
            applyBackgroundCallback.onApplyBackground(backgroundImageUri);
        }
    }

    private class LoadPhotoListTask extends AsyncTask<Void, Integer, List<String>> {
        private Context context;

        private LoadPhotoListTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            hintView.hidden();
        }

        @Override
        protected List<String> doInBackground(Void[] params) {
            Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{
                            MediaStore.Images.Media.DATA,
                            MediaStore.Images.Media.DATE_TAKEN
                    },
                    null,
                    null,
                    MediaStore.Images.Media.DATE_TAKEN + " DESC");
            if (cursor == null) {
                return null;
            }

            ImageOrientationCorrectTestFileGenerator generator = ImageOrientationCorrectTestFileGenerator.getInstance(context);
            String[] testFilePaths = generator.getFilePaths();

            List<String> imagePathList = new ArrayList<>(cursor.getCount() + AssetImage.ALL.length + testFilePaths.length);
            Collections.addAll(imagePathList, AssetImage.ALL);
            Collections.addAll(imagePathList, testFilePaths);
            while (cursor.moveToNext()) {
                imagePathList.add(String.format("file://%s", cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))));
            }
            cursor.close();
            return imagePathList;
        }

        @Override
        protected void onPostExecute(List<String> imageUriList) {
            if (getActivity() == null) {
                return;
            }

            refreshLayout.setRefreshing(false);

            if (imageUriList == null || imageUriList.isEmpty()) {
                hintView.empty("No videos");
                recyclerView.setAdapter(null);
                return;
            }

            AssemblyRecyclerAdapter adapter = new AssemblyRecyclerAdapter(imageUriList);
            adapter.addItemFactory(new MyPhotoItemFactory(MyPhotosFragment.this));

            recyclerView.setAdapter(adapter);
            recyclerView.scheduleLayoutAnimation();

            MyPhotosFragment.this.adapter = adapter;

            changeBackground(imageUriList.get(0));
        }
    }
}
