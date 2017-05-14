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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import me.xiaopan.androidinjector.InjectContentView;
import me.xiaopan.androidinjector.InjectView;
import me.xiaopan.assemblyadapter.AssemblyRecyclerAdapter;
import me.xiaopan.prl.PullRefreshLayout;
import me.xiaopan.sketch.util.SketchUtils;
import me.xiaopan.sketchsample.AssetImage;
import me.xiaopan.sketchsample.MyFragment;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.activity.ApplyBackgroundCallback;
import me.xiaopan.sketchsample.activity.DetailActivity;
import me.xiaopan.sketchsample.adapter.itemfactory.PhotoAlbumItemFactory;
import me.xiaopan.sketchsample.util.ScrollingPauseLoadManager;
import me.xiaopan.sketchsample.util.Settings;

/**
 * 本地相册页面
 */
@InjectContentView(R.layout.fragment_photo_album)
public class PhotoAlbumFragment extends MyFragment implements PhotoAlbumItemFactory.OnImageClickListener, PullRefreshLayout.OnRefreshListener {
    @InjectView(R.id.refreshLayout_photoAlbum)
    private PullRefreshLayout pullRefreshLayout;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pullRefreshLayout.setOnRefreshListener(this);
        recyclerView.setOnScrollListener(new ScrollingPauseLoadManager(view.getContext()));

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        int padding  = SketchUtils.dp2px(getActivity(), 2);
        recyclerView.setPadding(padding, padding, padding, padding);
        recyclerView.setClipToPadding(false);

        if (adapter != null) {
            recyclerView.setAdapter(adapter);
            recyclerView.scheduleLayoutAnimation();
        } else {
            pullRefreshLayout.startRefresh();
        }
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Override
    public void onImageClick(int position, String loadingImageOptionsId) {
        // 含有这些信息时，说明这张图片不仅仅是缩小，而是会被改变，因此不能用作loading图了
        if (loadingImageOptionsId.contains("Resize")
                || loadingImageOptionsId.contains("ImageProcessor")
                || loadingImageOptionsId.contains("thumbnailMode")) {
            loadingImageOptionsId = null;
        }
        DetailActivity.launch(getActivity(), (ArrayList<String>) adapter.getDataList(), loadingImageOptionsId, position);
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
            for(String assetImageName : AssetImage.ALL){
                imagePathList.add(assetImageName);
            }
            while (mCursor.moveToNext()) {
                //获取图片的路径
                imagePathList.add("file://" + mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA)));
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
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    pullRefreshLayout.stopRefresh();
                }
            }, 500);
            if (imageUriList != null && imageUriList.size() > 0) {
                changeBackground(imageUriList.get(0));
            }
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onGlobalAttrChanged(String key){
        if (Settings.PREFERENCE_PLAY_GIF_ON_LIST.equals(key)
                || Settings.PREFERENCE_GLOBAL_IN_PREFER_QUALITY_OVER_SPEED.equals(key)
                || Settings.PREFERENCE_GLOBAL_LOW_QUALITY_IMAGE.equals(key)
                || Settings.PREFERENCE_THUMBNAIL_MODE.equals(key)
                || Settings.PREFERENCE_CACHE_PROCESSED_IMAGE.equals(key)
                || Settings.PREFERENCE_SCROLLING_PAUSE_LOAD.equals(key)
                || Settings.PREFERENCE_DISABLE_CORRECT_IMAGE_ORIENTATION.equals(key)
                || Settings.PREFERENCE_MOBILE_NETWORK_PAUSE_DOWNLOAD.equals(key)) {
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }
}
