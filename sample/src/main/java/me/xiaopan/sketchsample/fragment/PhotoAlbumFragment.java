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
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.xiaopan.androidinjector.InjectContentView;
import me.xiaopan.androidinjector.InjectView;
import me.xiaopan.assemblyadapter.AssemblyRecyclerAdapter;
import me.xiaopan.sketch.util.SketchUtils;
import me.xiaopan.sketchsample.AssetImage;
import me.xiaopan.sketchsample.MyFragment;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.activity.ApplyBackgroundCallback;
import me.xiaopan.sketchsample.activity.ImageDetailActivity;
import me.xiaopan.sketchsample.adapter.itemfactory.PhotoAlbumItemFactory;
import me.xiaopan.sketchsample.util.ImageOrientationCorrectTestFileGenerator;
import me.xiaopan.sketchsample.util.ScrollingPauseLoadManager;

/**
 * 本地相册页面
 */
@InjectContentView(R.layout.fragment_photo_album)
public class PhotoAlbumFragment extends MyFragment implements PhotoAlbumItemFactory.OnImageClickListener, SwipeRefreshLayout.OnRefreshListener {
    @InjectView(R.id.refreshLayout_photoAlbum)
    private SwipeRefreshLayout refreshLayout;
    @InjectView(R.id.recyclerView_photoAlbum_content)
    private RecyclerView recyclerView;

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
        recyclerView.setOnScrollListener(new ScrollingPauseLoadManager(view.getContext()));

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
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
        ImageDetailActivity.launch(getActivity(), (ArrayList<String>) adapter.getDataList(), optionsKey, position);
    }

    @Override
    public void onRefresh() {
        if (getActivity() != null) {
            new ReadImagesTask(getActivity().getBaseContext()).execute();
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

    private class ReadImagesTask extends AsyncTask<Void, Integer, List<String>> {
        private Context context;

        private ReadImagesTask(Context context) {
            this.context = context;
        }

        @Override
        protected List<String> doInBackground(Void[] params) {
            Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String[] columns = new String[]{
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.DATE_MODIFIED
            };
            String sortOrder = MediaStore.Images.Media.DATE_MODIFIED + " DESC";

            ContentResolver mContentResolver = context.getContentResolver();
            Cursor mCursor = mContentResolver.query(mImageUri, columns, null, null, sortOrder);
            if (mCursor == null) {
                return null;
            }

            ImageOrientationCorrectTestFileGenerator generator = ImageOrientationCorrectTestFileGenerator.getInstance(context);
            String[] testFilePaths = generator.getFilePaths();

            List<String> imagePathList = new ArrayList<String>(mCursor.getCount() + AssetImage.ALL.length + testFilePaths.length);
            Collections.addAll(imagePathList, AssetImage.ALL);
            Collections.addAll(imagePathList, testFilePaths);
            while (mCursor.moveToNext()) {
                imagePathList.add(String.format("file://%s", mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA))));
            }
            mCursor.close();
            return imagePathList;
        }

        @Override
        protected void onPostExecute(List<String> imageUriList) {
            if (getActivity() == null) {
                return;
            }

            AssemblyRecyclerAdapter adapter = new AssemblyRecyclerAdapter(imageUriList);
            adapter.addItemFactory(new PhotoAlbumItemFactory(PhotoAlbumFragment.this));
            recyclerView.setAdapter(adapter);
            recyclerView.scheduleLayoutAnimation();
            PhotoAlbumFragment.this.adapter = adapter;
            refreshLayout.setRefreshing(false);
            if (imageUriList != null && imageUriList.size() > 0) {
                changeBackground(imageUriList.get(0));
            }
        }
    }
}
