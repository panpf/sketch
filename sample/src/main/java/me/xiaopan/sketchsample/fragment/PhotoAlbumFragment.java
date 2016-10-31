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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import me.xiaopan.androidinjector.InjectContentView;
import me.xiaopan.androidinjector.InjectView;
import me.xiaopan.prl.PullRefreshLayout;
import me.xiaopan.sketch.request.UriScheme;
import me.xiaopan.sketchsample.MyFragment;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.activity.ApplyBackgroundCallback;
import me.xiaopan.sketchsample.activity.DetailActivity;
import me.xiaopan.sketchsample.adapter.PhotoAlbumImageAdapter;
import me.xiaopan.sketchsample.util.ScrollingPauseLoadManager;

/**
 * 本地相册页面
 */
@InjectContentView(R.layout.fragment_photo_album)
public class PhotoAlbumFragment extends MyFragment implements PhotoAlbumImageAdapter.OnImageClickListener, PullRefreshLayout.OnRefreshListener {
    @InjectView(R.id.refreshLayout_photoAlbum)
    private PullRefreshLayout pullRefreshLayout;
    @InjectView(R.id.recyclerView_photoAlbum_content)
    private RecyclerView recyclerView;

    private PhotoAlbumImageAdapter imageAdapter;

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

        pullRefreshLayout.setOnRefreshListener(this);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setOnScrollListener(new ScrollingPauseLoadManager(view.getContext()));

        if (imageAdapter != null) {
            recyclerView.setAdapter(imageAdapter);
            recyclerView.scheduleLayoutAnimation();
        } else {
            pullRefreshLayout.startRefresh();
        }
    }

    @Override
    public void onImageClick(int position, String loadingImageOptionsId) {
        // 含有这些信息时，说明这张图片不仅仅是缩小，而是会被改变，因此不能用作loading图了
        if (loadingImageOptionsId.contains("Resize")
                || loadingImageOptionsId.contains("ImageProcessor")
                || loadingImageOptionsId.contains("thumbnailMode")) {
            loadingImageOptionsId = null;
        }
        DetailActivity.launch(getActivity(), (ArrayList<String>) imageAdapter.getImageUrlList(), loadingImageOptionsId, position);
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

            List<String> imagePathList = new ArrayList<String>(mCursor.getCount() + 2);
            imagePathList.add(UriScheme.ASSET.createUri("liuyifei.bmp"));
            imagePathList.add(UriScheme.ASSET.createUri("zhuomian.webp"));
            imagePathList.add(UriScheme.ASSET.createUri("world_map.jpg"));
            imagePathList.add(UriScheme.ASSET.createUri("test_card.png"));
            imagePathList.add(UriScheme.ASSET.createUri("qing_ming_shang_he_tu.jpg"));
            imagePathList.add(UriScheme.ASSET.createUri("chang_wei_bo.jpg"));
            while (mCursor.moveToNext()) {
                //获取图片的路径
                imagePathList.add("file://" + mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA)));
            }
            mCursor.close();
            return imagePathList;
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            if (getActivity() == null) {
                return;
            }

            recyclerView.setAdapter(imageAdapter = new PhotoAlbumImageAdapter(getActivity(), strings, PhotoAlbumFragment.this, recyclerView));
            recyclerView.scheduleLayoutAnimation();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    pullRefreshLayout.stopRefresh();
                }
            }, 1000);
            if (strings != null && strings.size() > 0) {
                changeBackground(strings.get(0));
            }
        }
    }
}
